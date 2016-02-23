package kz.greetgo.email.from_spring.javamail;

import javax.mail.internet.MimeMessage;

public interface MimeMessagePreparator {

  void prepare(MimeMessage mimeMessage) throws Exception;

}