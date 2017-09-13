package kz.greetgo.email.probes;

import kz.greetgo.email.Email;
import kz.greetgo.email.EmailSender;
import kz.greetgo.email.EmailSenderController;

import java.io.File;

public class SendControllerProbe {
  public static void main(String[] args) {
    EmailSender emailSender = new EmailSender() {
      @Override
      public void send(Email email) {
        try {

          System.out.println("sending letter " + email);
          Thread.sleep(500);
          System.out.println("sending....");
          Thread.sleep(500);
          System.out.println("sending....");
          Thread.sleep(500);
          System.out.println("sent");

        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    };

    EmailSenderController emailSenderController
      = new EmailSenderController(emailSender,
      new File("build/email/to_send"),
      new File("build/email/sent")
    );

    emailSenderController.sendAllExistingEmails();

    emailSenderController.cleanOldSentFiles(0);
  }
}
