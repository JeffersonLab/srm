package org.jlab.srm.business.session;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import org.jlab.smoothness.business.exception.UserFriendlyException;
import org.jlab.smoothness.business.service.SettingsService;

/**
 * @author ryans
 */
@Stateless
public class EmailFacade extends AbstractFacade<Object> {
  @Resource(name = "mail/jlab")
  private Session mailSession;

  public EmailFacade() {
    super(Object.class);
  }

  @PermitAll
  public void sendHTMLEmail(Address[] toAddresses, String subject, String html)
      throws MessagingException {
    MimeMessage message = new MimeMessage(mailSession);

    message.setFrom(new InternetAddress("srm@jlab.org"));

    message.setRecipients(Message.RecipientType.TO, toAddresses);
    message.setSubject(subject);

    message.setContent(html, "text/html; charset=UTF-8");

    message.saveChanges();

    Transport tr = mailSession.getTransport();
    tr.connect();
    tr.sendMessage(message, message.getAllRecipients());
    tr.close();
  }

  private void sendPlainTextEmail(Address from, Address[] toAddresses, String subject, String body)
      throws MessagingException {
    MimeMessage message = new MimeMessage(mailSession);

    message.setFrom(from);

    message.setRecipients(Message.RecipientType.TO, toAddresses);
    message.setSubject(subject);

    message.setText(body, "UTF-8");

    message.saveChanges();

    Transport tr = mailSession.getTransport();
    tr.connect();
    tr.sendMessage(message, message.getAllRecipients());
    tr.close();
  }

  @Override
  protected EntityManager getEntityManager() {
    return null;
  }

  @PermitAll
  public void sendMaskRequestEmail(String subject, String body) throws UserFriendlyException {
    String username = checkAuthenticated();

    try {
      Address fromAddress = new InternetAddress(username + "@jlab.org");

      if (subject == null || subject.isEmpty()) {
        throw new UserFriendlyException("subject must not be empty");
      }

      if (body == null || body.isEmpty()) {
        throw new UserFriendlyException("message must not be empty");
      }

      subject = "HCO Mask Request: " + subject;
      List<String> addressList = SettingsService.cachedSettings.csv("EMAIL_MASK_REQUEST_LIST");
      List<Address> toAddresses = new ArrayList<>();

      for (String address : addressList) {
        toAddresses.add(new InternetAddress(address));
      }

      if (toAddresses == null || toAddresses.isEmpty()) {
        throw new UserFriendlyException("No recipients configured.  Please contact your HCO admin");
      }

      sendPlainTextEmail(fromAddress, toAddresses.toArray(new Address[] {}), subject, body);
    } catch (AddressException e) {
      throw new UserFriendlyException("Invalid address", e);
    } catch (MessagingException e) {
      throw new UserFriendlyException("Unable to send email", e);
    }
  }
}
