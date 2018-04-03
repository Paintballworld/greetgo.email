package kz.greetgo.email.from_spring;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class SimpleMailMessage implements MailMessage, Serializable {

  private String from;

  private String replyTo;

  private String[] to;

  private String[] cc;

  private String[] bcc;

  private Date sentDate;

  private String subject;

  private String text;


  /**
   * Create a new SimpleMailMessage.
   */
  public SimpleMailMessage() {}

  /**
   * Copy constructor.
   */
  public SimpleMailMessage(SimpleMailMessage original) {
    this.from = original.getFrom();
    this.replyTo = original.getReplyTo();
    if (original.getTo() != null) {
      this.to = new String[original.getTo().length];
      System.arraycopy(original.getTo(), 0, this.to, 0, original.getTo().length);
    }
    if (original.getCc() != null) {
      this.cc = new String[original.getCc().length];
      System.arraycopy(original.getCc(), 0, this.cc, 0, original.getCc().length);
    }
    if (original.getBcc() != null) {
      this.bcc = new String[original.getBcc().length];
      System.arraycopy(original.getBcc(), 0, this.bcc, 0, original.getBcc().length);
    }
    this.sentDate = original.getSentDate();
    this.subject = original.getSubject();
    this.text = original.getText();
  }


  public void setFrom(String from) {
    this.from = from;
  }

  public String getFrom() {
    return this.from;
  }

  public void setReplyTo(String replyTo) {
    this.replyTo = replyTo;
  }

  public String getReplyTo() {
    return replyTo;
  }

  public void setTo(String to) {
    this.to = new String[]{to};
  }

  public void setTo(String[] to) {
    this.to = to;
  }

  public String[] getTo() {
    return this.to;
  }

  public void setCc(String cc) {
    this.cc = new String[]{cc};
  }

  public void setCc(String[] cc) {
    this.cc = cc;
  }

  public String[] getCc() {
    return cc;
  }

  public void setBcc(String bcc) {
    this.bcc = new String[]{bcc};
  }

  public void setBcc(String[] bcc) {
    this.bcc = bcc;
  }

  public String[] getBcc() {
    return bcc;
  }

  public void setSentDate(Date sentDate) {
    this.sentDate = sentDate;
  }

  public Date getSentDate() {
    return sentDate;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getSubject() {
    return this.subject;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getText() {
    return this.text;
  }


  /**
   * Copy the contents of this message to the given target message.
   *
   * @param target the MailMessage to copy to
   */
  public void copyTo(MailMessage target) {
    if (getFrom() != null) {
      target.setFrom(getFrom());
    }
    if (getReplyTo() != null) {
      target.setReplyTo(getReplyTo());
    }
    if (getTo() != null) {
      target.setTo(getTo());
    }
    if (getCc() != null) {
      target.setCc(getCc());
    }
    if (getBcc() != null) {
      target.setBcc(getBcc());
    }
    if (getSentDate() != null) {
      target.setSentDate(getSentDate());
    }
    if (getSubject() != null) {
      target.setSubject(getSubject());
    }
    if (getText() != null) {
      target.setText(getText());
    }
  }


  public String toString() {
    StringBuffer sb = new StringBuffer("SimpleMailMessage: ");
    sb.append("from=").append(this.from).append("; ");
    sb.append("replyTo=").append(this.replyTo).append("; ");
    sb.append("to=").append(arrayToCommaDelimitedString(this.to)).append("; ");
    sb.append("cc=").append(arrayToCommaDelimitedString(this.cc)).append("; ");
    sb.append("bcc=").append(arrayToCommaDelimitedString(this.bcc)).append("; ");
    sb.append("sentDate=").append(this.sentDate).append("; ");
    sb.append("subject=").append(this.subject).append("; ");
    sb.append("text=").append(this.text);
    return sb.toString();
  }

  private String arrayToCommaDelimitedString(String[] strs) {
    StringBuilder sb = new StringBuilder();
    for (String str : strs) {
      sb.append(str).append("; ");
    }
    if (sb.length() > 0) sb.setLength(sb.length() - 2);
    return sb.toString();
  }

  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof SimpleMailMessage)) {
      return false;
    }
    SimpleMailMessage otherMessage = (SimpleMailMessage) other;
    return (Objects.equals(this.from, otherMessage.from) &&
      Objects.equals(this.replyTo, otherMessage.replyTo) &&
      java.util.Arrays.equals(this.to, otherMessage.to) &&
      java.util.Arrays.equals(this.cc, otherMessage.cc) &&
      java.util.Arrays.equals(this.bcc, otherMessage.bcc) &&
      Objects.equals(this.sentDate, otherMessage.sentDate) &&
      Objects.equals(this.subject, otherMessage.subject) &&
      Objects.equals(this.text, otherMessage.text));
  }

  public int hashCode() {
    int hashCode = (this.from == null ? 0 : this.from.hashCode());
    hashCode = 29 * hashCode + (this.replyTo == null ? 0 : this.replyTo.hashCode());
    for (int i = 0; this.to != null && i < this.to.length; i++) {
      hashCode = 29 * hashCode + (this.to == null ? 0 : this.to[i].hashCode());
    }
    for (int i = 0; this.cc != null && i < this.cc.length; i++) {
      hashCode = 29 * hashCode + (this.cc == null ? 0 : this.cc[i].hashCode());
    }
    for (int i = 0; this.bcc != null && i < this.bcc.length; i++) {
      hashCode = 29 * hashCode + (this.bcc == null ? 0 : this.bcc[i].hashCode());
    }
    hashCode = 29 * hashCode + (this.sentDate == null ? 0 : this.sentDate.hashCode());
    hashCode = 29 * hashCode + (this.subject == null ? 0 : this.subject.hashCode());
    hashCode = 29 * hashCode + (this.text == null ? 0 : this.text.hashCode());
    return hashCode;
  }

}