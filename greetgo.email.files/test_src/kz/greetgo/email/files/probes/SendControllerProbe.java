package kz.greetgo.email.files.probes;

import kz.greetgo.email.Email;
import kz.greetgo.email.EmailSender;
import kz.greetgo.email.files.EmailSenderController;

import java.io.File;

public class SendControllerProbe {
  public static void main(String[] args) {
    EmailSender emailSender = email -> {
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
