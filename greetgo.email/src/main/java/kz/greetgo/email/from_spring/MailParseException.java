package kz.greetgo.email.from_spring;

public class MailParseException extends MailException {

  public MailParseException(String msg) {
    super(msg);
  }

  public MailParseException(String msg, Throwable ex) {
    super(msg, ex);
  }

  public MailParseException(Throwable ex) {
    super("Could not parse mail: " + ex.getMessage(), ex);
  }

}