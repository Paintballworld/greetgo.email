package kz.greetgo.email;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class RealSendEmailProbe2 {
  public static void main(String[] args) throws Exception {

    final String username = "jour_email_account@gmail.com";
    final String password = "password_for_jour_email_account";

    Properties props = new Properties();
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props,
      new javax.mail.Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(username, password);
        }
      }
    );


    Message message = new MimeMessage(session);
    message.setFrom(new InternetAddress("jour_email_account@gmail.com"));
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("кому отправляется письмо@host.com"));
    message.setRecipients(Message.RecipientType.CC, InternetAddress.parse("кому отправить копию@host.com"));
    message.setSubject("Тестовое письмо");
    message.setText("Это письмо для проверки там какой-то функции\n\n Удалите это письмо");

    Transport.send(message);

    System.out.println("Done");

  }
}
