package kz.greetgo.email.from_spring.javamail;

import kz.greetgo.email.from_spring.MailException;
import kz.greetgo.email.from_spring.MailSender;

import javax.mail.internet.MimeMessage;
import java.io.InputStream;

public interface JavaMailSender extends MailSender {

  MimeMessage createMimeMessage();

  MimeMessage createMimeMessage(InputStream contentStream) throws MailException;

  void send(MimeMessage mimeMessage) throws MailException;

  void send(MimeMessage[] mimeMessages) throws MailException;

  void send(MimeMessagePreparator mimeMessagePreparator) throws MailException;

  void send(MimeMessagePreparator[] mimeMessagePreparators) throws MailException;

}