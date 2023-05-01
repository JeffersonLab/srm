package org.jlab.srm.business.session;

import org.jlab.smoothness.business.service.UserAuthorizationService;
import org.jlab.smoothness.persistence.view.User;
import org.jlab.srm.business.session.AbstractFacade.OrderDirective;
import org.jlab.srm.persistence.entity.HcoSettings;
import org.jlab.srm.persistence.entity.ResponsibleGroup;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.*;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ryans
 */
@Singleton
@DeclareRoles({"srm-admin"})
@Startup
public class ScheduledEmailer {

    private static final Logger logger = Logger.getLogger(
            ScheduledEmailer.class.getName());
    @EJB
    ResponsibleGroupFacade groupFacade;
    @EJB
    EmailFacade emailFacade;
    @EJB
    HcoSettingsFacade settingsFacade;
    @Resource
    private TimerService timerService;
    private Timer timer;

    @PostConstruct
    private void init() {
        logger.log(Level.FINE, "Canceling Cached Timers");
        listAll();
        clearAll();

        timer = null;

        if (settingsFacade.findSettings().isAutoEmail()) {
            logger.log(Level.FINE, "Creating New Timer");
            enableTimer();
        }
    }

    @RolesAllowed("srm-admin")
    public boolean isEnabled() {
        return timer != null;
    }

    @RolesAllowed("srm-admin")
    public void setEnabled(Boolean enabled) {

        settingsFacade.setAutoEmail(enabled);

        if (enabled) {
            enableTimer();
        } else {
            disableTimer();
        }
        logger.log(Level.FINE, "Enabled: {0}; Listing Timers", enabled);
        listAll();
    }

    private void enableTimer() {
        if (!isEnabled()) {
            ScheduleExpression schedExp = new ScheduleExpression();
            schedExp.second("0");
            schedExp.minute("0");
            schedExp.hour("7");
            TimerConfig config = new TimerConfig();
            config.setPersistent(false);
            timer = timerService.createCalendarTimer(schedExp, config);
        }
    }

    private void disableTimer() {
        if (isEnabled()) {
            timer.cancel();
            timer = null;
            clearAll();
        }
    }

    private void listAll() {
        for (Timer t : timerService.getTimers()) {
            logger.log(Level.FINE, "Timer Expression: {0}, Remaining: {1}, Next timeout: {2}", new Object[]{t.getSchedule(), t.getTimeRemaining(), t.getNextTimeout()});
        }
    }

    private void clearAll() {
        /*Timers persist by default and may be hanging around after a redeploy*/
        for (Timer t : timerService.getTimers()) {
            t.cancel();
        }
    }

    @Timeout
    private void handleTimeout(Timer timer) {
        logger.log(Level.FINE, "Sending Auto Emails");

        try {
            sendActivityEmails();
            sendGroupActionNeededEmails();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unable to send email on schedule", e);
        }
    }

    @RolesAllowed("srm-admin")
    public void sendActivityEmails() throws IOException, MessagingException {
        String url = "http://localhost:8080/srm/activity-daily-email?email=Y";

        Document doc = Jsoup.connect(url).get();

        if (!doc.select("#doNotSend").text().trim().isEmpty()) {
            logger.log(Level.FINE, "Skipping activity email");
        } else {
            logger.log(Level.FINE, "Sending activity email");

            String html = doc.outerHtml();

            HcoSettings settings = settingsFacade.findSettings();

            List<Address> addresses = settings.getActivityEmailAddresses();

            if (!addresses.isEmpty()) {
                emailFacade.sendHTMLEmail(addresses.toArray(new Address[]{}), "HCO - Activity", html);
            }
        }
    }

    @RolesAllowed("srm-admin")
    public void sendGroupActionNeededEmails() throws IOException, MessagingException {
        List<ResponsibleGroup> groupList = groupFacade.findAll(new OrderDirective("name"));

        for (ResponsibleGroup g : groupList) {
            sendGroupMail(g);
        }
    }

    private void sendGroupMail(ResponsibleGroup group) throws IOException, MessagingException {
        String url = "http://localhost:8080/srm/group-daily-email?email=Y&groupId=" + group.getGroupId();

        Document doc = Jsoup.connect(url).get();

        if (!doc.select("#doNotSend").text().trim().isEmpty()) {
            logger.log(Level.FINE, "Skipping email for group: {0}", group.getName());
        } else {
            logger.log(Level.FINE, "Sending email for group: {0}", group.getName());

            String html = doc.outerHtml();

            List<InternetAddress> addresses = new ArrayList<>();

            UserAuthorizationService userService = UserAuthorizationService.getInstance();
            List<User> userList = userService.getUsersInRole(group.getLeaderWorkgroup());
            group.setLeaders(userList);

            if (group.getLeaders() != null && !group.getLeaders().isEmpty()) {
                for (User user : group.getLeaders()) {
                    addresses.add(new InternetAddress(user.getUsername() + "@jlab.org"));
                }
                emailFacade.sendHTMLEmail(addresses.toArray(new InternetAddress[]{}), "HCO - " + group.getName() + " Action Needed", html);
            }
        }
    }

    @RolesAllowed("srm-admin")
    public void sendGroupMail(BigInteger groupId) throws IOException, MessagingException {
        ResponsibleGroup group = groupFacade.find(groupId);

        UserAuthorizationService userService = UserAuthorizationService.getInstance();
        List<User> userList = userService.getUsersInRole(group.getLeaderWorkgroup());
        group.setLeaders(userList);

        if (group.getLeaders() == null || group.getLeaders().isEmpty()) {
            throw new MessagingException("No group leaders to email");
        }

        sendGroupMail(group);
    }
}
