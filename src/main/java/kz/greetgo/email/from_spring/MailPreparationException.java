package kz.greetgo.email.from_spring;

public class MailPreparationException extends MailException {

  public MailPreparationException(String msg) {
    super(msg);
  }

  public MailPreparationException(String msg, Throwable ex) {
    super(msg, ex);
  }

  public MailPreparationException(Throwable ex) {
    super("Could not prepare mail: " + ex.getMessage(), ex);
  }

}