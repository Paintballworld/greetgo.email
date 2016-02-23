package kz.greetgo.email.from_spring.javamail;

import javax.activation.FileTypeMap;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

class SmartMimeMessage extends MimeMessage {
  private final String defaultEncoding;
  private final FileTypeMap defaultFileTypeMap;

  public SmartMimeMessage(Session session, String defaultEncoding, FileTypeMap defaultFileTypeMap) {
    super(session);
    this.defaultEncoding = defaultEncoding;
    this.defaultFileTypeMap = defaultFileTypeMap;
  }

  public String getDefaultEncoding() {
    return defaultEncoding;
  }

  public FileTypeMap getDefaultFileTypeMap() {
    return defaultFileTypeMap;
  }
}
