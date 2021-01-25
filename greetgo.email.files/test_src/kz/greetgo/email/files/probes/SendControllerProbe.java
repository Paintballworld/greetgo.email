package kz.greetgo.email.files.probes;

import kz.greetgo.email.EmailSender;
import kz.greetgo.email.RealEmailSender;
import kz.greetgo.email.files.TestRegister;

import java.io.File;

public class SendControllerProbe {
  public static void main(String[] args) {
    RealEmailSender emailSender = email -> {
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

    TestRegister abstractRealEmailSendRegister = new TestRegister(emailSender,
                                                                  new File("build/email/to_send"),
                                                                  new File("build/email/sent"));

    abstractRealEmailSendRegister.sendAllExistingEmails();

    abstractRealEmailSendRegister.cleanOldSentFiles(0);
  }
}
