package kz.greetgo.email.from_spring;

public class MailAuthenticationException extends MailException {

  public MailAuthenticationException(String msg) {
    super(msg);
  }

  public MailAuthenticationException(String msg, Throwable ex) {
    super(msg, ex);
  }

  public MailAuthenticationException(Throwable ex) {
    super("Authentication failed: " + ex.getMessage(), ex);
  }

}