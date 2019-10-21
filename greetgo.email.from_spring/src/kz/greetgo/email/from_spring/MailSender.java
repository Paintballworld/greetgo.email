package kz.greetgo.email.from_spring;

public interface MailSender {

  void send(SimpleMailMessage simpleMessage) throws MailException;

  void send(SimpleMailMessage[] simpleMessages) throws MailException;

}