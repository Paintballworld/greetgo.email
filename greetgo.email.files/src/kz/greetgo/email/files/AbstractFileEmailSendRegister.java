package kz.greetgo.email.files;

import kz.greetgo.email.Email;
import kz.greetgo.email.EmailSendRegister;
import kz.greetgo.email.EmailSender;
import kz.greetgo.email.EmailSerializer;
import kz.greetgo.email.RealEmailSender;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractFileEmailSendRegister implements EmailSendRegister {

  private final AtomicBoolean sendingIsGoingOn = new AtomicBoolean(false);

  protected abstract File toSendDir();

  protected abstract File sentDir();

  protected abstract RealEmailSender emailSender();

  protected abstract EmailSerializer emailSerializer();

  public EmailSender createEmailSaver() {
    return email -> {

      String filename = createSendingFileName();

      File file = toSendDir().toPath().resolve(filename + creatingExtension()).toFile();
      file.getParentFile().mkdirs();

      emailSerializer().serialize(file, email);

      file.renameTo(toSendDir().toPath().resolve(filename).toFile());

    };
  }

  protected String creatingExtension() {
    return ".creating";
  }

  private final Random rnd = new Random();

  protected String createSendingFileName() {
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss.SSS");
    return sendingFilePrefixName() + '-' + format.format(new Date()) + '.' + rnd.nextInt() + sendReadyExtension();
  }

  protected String sendReadyExtension() {
    return ".email.xml";
  }

  protected String sendingFilePrefixName() {
    return "sending-";
  }

  @Override
  public void sendAllExistingEmails() {
    if (!sendingIsGoingOn.compareAndSet(false, true)) {
      return;
    }
    try {
      //noinspection StatementWithEmptyBody
      while (hasToSendOne()) {
      }
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
    EmailInfo info = getFirstFromDir(toSendDir());
    if (info == null) return false;

    if (!info.file.renameTo(info.sendingFile)) return true;

    try {
      emailSender().realSend(info.email);
    } catch (Exception exception) {
      if (resendOnException(exception, info.email)) {
        info.sendingFile.renameTo(info.file);
        throw exception;
      }

      {
        Path   parentPath = info.sendingFile.getParentFile().toPath();
        String name       = info.sendingFile.getName();

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

  private EmailInfo getFirstFromDir(File emailSendDir) {
    File[] files = emailSendDir
                     .listFiles(pathname -> pathname.isFile() && pathname.getName().endsWith(sendReadyExtension()));
    if (files == null) {
      return null;
    }
    if (files.length == 0) {
      return null;
    }

    EmailInfo ret = new EmailInfo();
    ret.file  = files[0];
    ret.email = emailSerializer().deserialize(ret.file);

    ret.sendingFile = new File(ret.file.getPath() + sendingExtension());

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    ret.sentFile = sentDir().toPath().resolve(format.format(new Date())).resolve(ret.file.getName()).toFile();

    return ret;
  }

  protected String sendingExtension() {
    return ".sending";
  }

  @Override
  public void cleanOldSentEntries(final int hoursBefore) {
    final Calendar cal = new GregorianCalendar();
    final Date     now = new Date();

    for (File file : FindFiles.recursively(sentDir(), sendReadyExtension())) {
      cal.setTimeInMillis(file.lastModified());
      cal.add(Calendar.HOUR, hoursBefore);
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
