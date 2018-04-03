package kz.greetgo.email.from_spring;

public abstract class MailException extends RuntimeException {
  public MailException(String msg) {
    super(msg);
  }

  public MailException(String msg, Throwable ex) {
    super(msg, ex);
  }
}