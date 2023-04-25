package org.jlab.hco.business.session;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class DailyScheduledMaskExpire {

    private static final Logger LOGGER = Logger.getLogger(DailyScheduledMaskExpire.class.getName());

    private Timer timer;
    @Resource
    private TimerService timerService;
    @EJB
    private ComponentFacade componentFacade;

    @PostConstruct
    private void init() {
        clearTimer();
        startTimer();
    }

    private void clearTimer() {
        LOGGER.log(Level.FINEST, "Clearing Daily Timer");
        for (Timer t : timerService.getTimers()) {
            LOGGER.log(Level.FINE, "Timer Expression: {0}, Remaining: {1}, Next timeout: {2}", new Object[]{t.getSchedule(), t.getTimeRemaining(), t.getNextTimeout()});
            t.cancel();
        }
        timer = null;
    }

    private void startTimer() {
        LOGGER.log(Level.INFO, "Starting Daily Mask Expiration Timer");
        ScheduleExpression schedExp = new ScheduleExpression();
        schedExp.second("0");
        schedExp.minute("0");
        schedExp.hour("0");
        TimerConfig config = new TimerConfig();
        config.setPersistent(false);
        timer = timerService.createCalendarTimer(schedExp, config);
    }

    @Timeout
    private void handleTimeout(Timer timer) {
        LOGGER.log(Level.FINEST, "handleTimeout: Checking for expired masks...");
        componentFacade.expireMasks(); // AuditContext is null so hcoadm username will be used
    }
}
