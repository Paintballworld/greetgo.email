package kz.greetgo.email;

import static kz.greetgo.email.EmailUtil.dummyCheck;

import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class EmailSenderController {
  
  private boolean inSendOperation = false;
  
  private final File sendDir;
  private final File sendedDir;
  private final EmailSender emailSender;
  
  private final EmailSerializer emailSerializer = new EmailSerializer();
  
  public EmailSenderController(EmailSender emailSender, File sendDir, File sendedDir) {
    this.emailSender = emailSender;
    this.sendDir = sendDir;
    this.sendedDir = sendedDir;
  }
  
  public void sendAllExistingEmails() {
    synchronized (this) {
      if (inSendOperation) return;
      inSendOperation = true;
    }
    try {
      while (hasToSendOne()) {}
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      synchronized (this) {
        inSendOperation = false;
      }
    }
  }
  
  private static class EmailInfo {
    File file, sendingFile, sendedFile;
    Email email;
  }
  
  private boolean hasToSendOne() throws Exception {
    EmailInfo info = getFirstFromDir(sendDir);
    if (info == null) return false;
    
    dummyCheck(info.file.renameTo(info.sendingFile));
    
    try {
      emailSender.send(info.email);
    } catch (Exception exception) {
      dummyCheck(info.sendingFile.renameTo(info.file));
      throw exception;
    }
    
    dummyCheck(info.sendedFile.getParentFile().mkdirs());
    dummyCheck(info.sendingFile.renameTo(info.sendedFile));
    
    return true;
  }
  
  private EmailInfo getFirstFromDir(File emailSendDir) throws Exception {
    File[] files = emailSendDir.listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        return pathname.isFile() && pathname.getName().endsWith(".xml");
      }
    });
    if (files == null) return null;
    if (files.length == 0) return null;
    
    EmailInfo ret = new EmailInfo();
    ret.file = files[0];
    ret.email = emailSerializer.deserialize(ret.file);
    
    ret.sendingFile = new File(ret.file.getAbsolutePath() + ".sending");
    
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    ret.sendedFile = new File(sendedDir + "/" + format.format(new Date()) + "/"
        + ret.file.getName());
    
    return ret;
  }
  
  public void cleanOldSendedFiles(final int daysBefore) {
    final Calendar cal = new GregorianCalendar();
    final Date now = new Date();
    File[] files = sendedDir.listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        if (!pathname.isFile()) return false;
        if (!pathname.getName().endsWith(".xml")) return false;
        
        cal.setTimeInMillis(pathname.lastModified());
        cal.add(Calendar.DAY_OF_YEAR, daysBefore);
        
        return cal.getTime().before(now);
      }
    });
    if (files == null) return;
    if (files.length == 0) return;
    
    for (File file : files) {
      dummyCheck(file.delete());
    }
  }
}
