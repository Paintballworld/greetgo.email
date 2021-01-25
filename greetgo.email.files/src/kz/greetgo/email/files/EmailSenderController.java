package kz.greetgo.email.files;

import kz.greetgo.email.Email;
import kz.greetgo.email.EmailSender;
import kz.greetgo.email.EmailSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.atomic.AtomicBoolean;

public class EmailSenderController {

  private final AtomicBoolean sendingIsGoingOn = new AtomicBoolean(false);

  private final File toSendDir;
  private final File        sentDir;
  private final EmailSender emailSender;

  private final EmailSerializer emailSerializer = new EmailSerializer();

  public EmailSenderController(EmailSender emailSender, File toSendDir, File sentDir) {
    this.emailSender = emailSender;
    this.toSendDir = toSendDir;
    this.sentDir = sentDir;
  }

  public void sendAllExistingEmails() {
    if (!sendingIsGoingOn.compareAndSet(false, true)) {
      return;
    }
    try {
      //noinspection StatementWithEmptyBody
      while (hasToSendOne()) {}
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      sendingIsGoingOn.set(false);
    }
  }

  private static class EmailInfo {
    File file, sendingFile, sentFile;
    Email email;
  }

  private boolean hasToSendOne() throws Exception {
    EmailInfo info = getFirstFromDir(toSendDir);
    if (info == null) return false;

    if (!info.file.renameTo(info.sendingFile)) return true;

    try {
      emailSender.send(info.email);
    } catch (Exception exception) {
      if (resendOnException(exception, info.email)) {
        info.sendingFile.renameTo(info.file);
        throw exception;
      }

      {
        Path parentPath = info.sendingFile.getParentFile().toPath();
        String name = info.sendingFile.getName();

        try (FileOutputStream fileOutput = new FileOutputStream(parentPath.resolve(name + ".exception.txt").toFile());
             PrintStream pr = new PrintStream(fileOutput, false, "UTF-8")) {
          pr.println("" + exception.getClass() + " :: " + exception.getMessage());
          pr.println();
          exception.printStackTrace(pr);
        }
      }
    }

    info.sentFile.getParentFile().mkdirs();
    info.sendingFile.renameTo(info.sentFile);

    return true;
  }

  protected boolean resendOnException(Exception exception, Email email) {
    return true;
  }

  private EmailInfo getFirstFromDir(File emailSendDir) throws Exception {
    File[] files = emailSendDir.listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(".xml"));
    if (files == null) {
      return null;
    }
    if (files.length == 0) {
      return null;
    }

    EmailInfo ret = new EmailInfo();
    ret.file = files[0];
    ret.email = emailSerializer.deserialize(ret.file);

    ret.sendingFile = new File(ret.file.getAbsolutePath() + ".sending");

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    ret.sentFile = new File(sentDir + "/" + format.format(new Date()) + "/" + ret.file.getName());

    return ret;
  }

  /**
   * Removes old files from directory with sent files
   *
   * @param daysBefore how old file in days to remove
   */
  public void cleanOldSentFiles(final int daysBefore) {
    final Calendar cal = new GregorianCalendar();
    final Date now = new Date();

    for (File file : EmailUtil.findFilesRecursively(sentDir, ".xml")) {
      cal.setTimeInMillis(file.lastModified());
      cal.add(Calendar.DAY_OF_YEAR, daysBefore);
      if (cal.getTime().before(now)) {
        deleteWithParents(file);
      }
    }
  }

  private static void deleteWithParents(File file) {
    if (file.delete()) {
      deleteWithParents(file.getParentFile());
    }
  }
}
