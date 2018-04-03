package kz.greetgo.email;

import java.util.ArrayList;
import java.util.List;

public class Email {
  private String from;
  private String to;
  private String subject;
  private String body;
  private final List<Attachment> attachments = new ArrayList<Attachment>();
  
  private final List<String> copies = new ArrayList<String>();
  
  public String getFrom() {
    return from;
  }
  
  public void setFrom(String from) {
    this.from = from;
  }
  
  public String getTo() {
    return to;
  }
  
  public void setTo(String to) {
    this.to = to;
  }
  
  public String getSubject() {
    return subject;
  }
  
  public void setSubject(String subject) {
    this.subject = subject;
  }
  
  public String getBody() {
    return body;
  }
  
  public void setBody(String body) {
    this.body = body;
  }
  
  public List<Attachment> getAttachments() {
    return attachments;
  }
  
  public List<String> getCopies() {
    return copies;
  }
  
  @Override
  public String toString() {
    return "Email [from=" + from + ", to=" + to + ", subject=" + subject + ", body=" + body
        + ", attachments=" + attachments + ", copies=" + copies + "]";
  }
}
