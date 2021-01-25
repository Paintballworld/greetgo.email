package kz.greetgo.email.files;

import kz.greetgo.email.Attachment;
import kz.greetgo.email.Email;
import kz.greetgo.email.EmailSender;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractRealEmailSendRegisterTest {

  @Test
  public void send() {

    String sendDir = "build/send";
    String sentDir = "build/sent";

    TestRegister c = new TestRegister(email -> {
      System.out.println("ThM2VLLJ55 :: real send email " + email);
    }, new File(sendDir), new File(sentDir));
    EmailSender saver = c.createEmailSaver();

    Email e = new Email();
    e.setBody("body");
    e.setSubject("subject");
    e.setFrom("from");
    e.setTo("to");

    List<Attachment> attachments = new ArrayList<>();
    byte[]           data        = new byte[10];
    attachments.add(new Attachment("attachment1", data));
    attachments.add(new Attachment("attachment2", data));
    e.getAttachments().addAll(attachments);

    saver.send(e);

    c.sendAllExistingEmails();

    assertThat(1);
  }

  @Test
  public void cleanOldSentFiles() throws Exception {

    File sentDir = new File("build/sent_" + (new Date().getTime()));
    sentDir.mkdirs();

    File fOld = new File(sentDir, "old.email.xml");
    File fNew = new File(sentDir, "new.email.xml");

    fOld.createNewFile();
    fNew.createNewFile();

    Calendar cal = new GregorianCalendar();
    cal.add(Calendar.DAY_OF_YEAR, -100);

    fOld.setLastModified(cal.getTimeInMillis());

    AbstractRealEmailSendRegister c = new TestRegister(null, null, sentDir);
    c.cleanOldSentFiles(10);

    assertThat(fOld.exists()).isFalse();
    assertThat(fNew.exists()).isTrue();

    fNew.delete();
    sentDir.delete();
  }
}
