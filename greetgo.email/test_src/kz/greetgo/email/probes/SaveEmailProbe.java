package kz.greetgo.email.probes;

import kz.greetgo.email.Attachment;
import kz.greetgo.email.Email;
import kz.greetgo.email.EmailSaver;
import kz.greetgo.email.EmailSender;

import java.nio.charset.StandardCharsets;

public class SaveEmailProbe {
  public static void main(String[] args) {
    //noinspection UnnecessaryLocalVariable
    EmailSaver emailSaver = new EmailSaver("cool", "build/email/to_send");

    //noinspection UnnecessaryLocalVariable
    EmailSender emailSender = emailSaver;

    Email email = new Email();
    email.setFrom("asd");
    email.setTo("ekolpakov@greet-go.com");
    email.setBody("Hello world");
    email.setSubject("New letter");
    {
      Attachment a = new Attachment();
      a.name = "wow.txt";
      a.data = "Some text file with cool text".getBytes(StandardCharsets.UTF_8);
      email.getAttachments().add(a);
    }
    emailSender.send(email);

  }
}
