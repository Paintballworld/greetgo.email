package kz.greetgo.email;

import static kz.greetgo.email.EmailUtil.dummyCheck;
import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.testng.annotations.Test;

public class EmailSenderControllerTest {
  
  @Test
  public void send() throws Exception {
    
    String sendDir = "build/send";
    String sendedDir = "build/sended";
    
    EmailSaver saver = new EmailSaver("saver", sendDir);
    EmailSaver realSender = new EmailSaver("realSender", "build/sendedReal");
    
    EmailSenderController c = new EmailSenderController(realSender, new File(sendDir), new File(
        sendedDir));
    
    Email e = new Email();
    e.setBody("body");
    e.setSubject("subject");
    e.setFrom("from");
    e.setTo("to");
    
    List<Attachment> attachments = new ArrayList<Attachment>();
    byte[] data = new byte[10];
    attachments.add(new Attachment("attachment1", data));
    attachments.add(new Attachment("attachment2", data));
    e.getAttachments().addAll(attachments);
    
    saver.send(e);
    
    c.sendAllExistingEmails();
    
    assertThat(1);
  }
  
  @Test
  public void cleanOldSendedFiles() throws Exception {
    
    File sendedDir = new File("build/sended_" + (new Date().getTime()));
    dummyCheck(sendedDir.mkdirs());
    
    File fOld = new File(sendedDir, "old.xml");
    File fNew = new File(sendedDir, "new.xml");
    
    dummyCheck(fOld.createNewFile());
    dummyCheck(fNew.createNewFile());
    
    Calendar cal = new GregorianCalendar();
    cal.add(Calendar.DAY_OF_YEAR, -100);
    
    dummyCheck(fOld.setLastModified(cal.getTimeInMillis()));
    
    EmailSenderController c = new EmailSenderController(null, null, sendedDir);
    c.cleanOldSendedFiles(10);
    
    assertThat(fOld.exists()).isFalse();
    assertThat(fNew.exists()).isTrue();
    
    dummyCheck(fNew.delete());
    dummyCheck(sendedDir.delete());
  }
}
