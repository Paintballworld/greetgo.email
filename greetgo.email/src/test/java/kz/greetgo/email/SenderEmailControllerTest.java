package kz.greetgo.email;

import static org.fest.assertions.api.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.testng.annotations.Test;

public class SenderEmailControllerTest {
  
  @Test
  public void send() throws Exception {
    
    String sendDir = "build/send";
    String sendedDir = "build/sended";
    
    EmailSaver saver = new EmailSaver("saver", sendDir);
    EmailSaver realSender = new EmailSaver("realSender", "build/sendedReal");
    
    SenderEmailController c = new SenderEmailController(realSender, new File(sendDir), new File(
        sendedDir));
    
    Email e = new Email();
    e.setBody("body");
    e.setSubject("subject");
    e.setFrom("itukibayev@greet-go.com");
    e.setTo("itukibayev@greet-go.com");
    
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
    sendedDir.mkdirs();
    
    File fOld = new File(sendedDir, "old.xml");
    File fNew = new File(sendedDir, "new.xml");
    
    fOld.createNewFile();
    fNew.createNewFile();
    
    Calendar cal = new GregorianCalendar();
    cal.add(Calendar.DAY_OF_YEAR, -100);
    
    fOld.setLastModified(cal.getTimeInMillis());
    
    SenderEmailController c = new SenderEmailController(null, null, sendedDir);
    c.cleanOldSendedFiles(10);
    
    assertThat(fOld.exists()).isFalse();
    assertThat(fNew.exists()).isTrue();
    
    fNew.delete();
    sendedDir.delete();
  }
}
