package kz.greetgo.email.from_spring;

import java.util.Date;

public interface MailMessage {

  void setFrom(String from) throws MailParseException;

  void setReplyTo(String replyTo) throws MailParseException;

  void setTo(String to) throws MailParseException;

  void setTo(String[] to) throws MailParseException;

  void setCc(String cc) throws MailParseException;

  void setCc(String[] cc) throws MailParseException;

  void setBcc(String bcc) throws MailParseException;

  void setBcc(String[] bcc) throws MailParseException;

  void setSentDate(Date sentDate) throws MailParseException;

  void setSubject(String subject) throws MailParseException;

  void setText(String text) throws MailParseException;

}