package kz.greetgo.email;

import com.sun.mail.smtp.SMTPTransport;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;
import java.util.Date;
import java.util.Properties;


public class RealSendEmailProbe1 {
  public static void main(String[] args) throws Exception {

    Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    Properties props = System.getProperties();
    props.setProperty("mail.smtps.host", "smtp.gmail.com");
    props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
    props.setProperty("mail.smtp.socketFactory.fallback", "false");
    props.setProperty("mail.smtp.port", "465");
    props.setProperty("mail.smtp.socketFactory.port", "465");
    props.setProperty("mail.smtps.auth", "true");

    props.put("mail.smtps.quitwait", "false");

    Session session = Session.getInstance(props, null);

    final MimeMessage msg = new MimeMessage(session);


    msg.setFrom(new InternetAddress("john.kolpakov.x@gmail.com"));
    msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("кому отправляется письмо@host.com", false));
    msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse("кому отправляется копия@host.com", false));
    msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse("кому отправляется скрытая копия@host.com", false));

    msg.setSubject("Тестовое письмо");

    msg.setText("Это тестовое письмо - удалите его немедленно", "UTF-8");
    msg.setSentDate(new Date());

    SMTPTransport t = (SMTPTransport) session.getTransport("smtps");

    t.connect("smtp.gmail.com", "your_email@gmail.com", "password_for_your_email");
    t.sendMessage(msg, msg.getAllRecipients());

    t.close();

    System.out.println("OK");
  }
}
