package kz.greetgo.email;

import static kz.greetgo.email.EmailUtil.dummyCheck;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class EmailSaver implements EmailSender {
  
  private final String sendDir;
  private final String filePrefixName;
  
  private final Random rnd = new Random();
  
  public EmailSaver(String filePrefixName, String sendDir) {
    this.filePrefixName = filePrefixName;
    this.sendDir = sendDir;
  }
  
  @Override
  public void send(Email email) {
    String filename = createFileName();
    
    File file = new File(sendDir + "/" + filename + ".creating");
    dummyCheck(file.getParentFile().mkdirs());
    
    EmailSerializer emailSerializer = new EmailSerializer();
    try {
      emailSerializer.serialize(file, email);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    
    dummyCheck(file.renameTo(new File(sendDir + "/" + filename)));
  }
  
  private String createFileName() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSS");
    return filePrefixName + '-' + format.format(new Date()) + '.' + rnd.nextInt() + ".email.xml";
  }
}
