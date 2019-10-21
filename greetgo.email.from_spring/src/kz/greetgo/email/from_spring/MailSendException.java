package kz.greetgo.email.from_spring;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MailSendException extends MailException {

  private Map failedMessages = new HashMap();

  public MailSendException(String msg) {
    super(msg);
  }

  public MailSendException(String msg, Throwable ex) {
    super(msg, ex);
  }

  public MailSendException(Map failedMessages) {
    super(null);
    //noinspection unchecked
    this.failedMessages.putAll(failedMessages);
  }

  public Map getFailedMessages() {
    return failedMessages;
  }

  public String getMessage() {
    StringBuilder msg = new StringBuilder();
    String superMsg = super.getMessage();
    msg.append(superMsg != null ? superMsg : "Could not send mails: ");
    for (Iterator subExs = this.failedMessages.values().iterator(); subExs.hasNext(); ) {
      Exception subEx = (Exception) subExs.next();
      msg.append(subEx.getMessage());
      if (subExs.hasNext()) {
        msg.append("; ");
      }
    }
    return msg.toString();
  }

  public void printStackTrace(PrintStream ps) {
    if (this.failedMessages.isEmpty()) {
      super.printStackTrace(ps);
    } else {
      ps.println(this);
      for (Iterator subExs = this.failedMessages.values().iterator(); subExs.hasNext(); ) {
        Exception subEx = (Exception) subExs.next();
        subEx.printStackTrace(ps);
        if (subExs.hasNext()) {
          ps.println();
        }
      }
    }
  }

  public void printStackTrace(PrintWriter pw) {
    if (!this.failedMessages.isEmpty()) {
      pw.println(this);
      for (Iterator subExs = this.failedMessages.values().iterator(); subExs.hasNext(); ) {
        Exception subEx = (Exception) subExs.next();
        subEx.printStackTrace(pw);
        if (subExs.hasNext()) {
          pw.println();
        }
      }
    } else {
      super.printStackTrace(pw);
    }
  }

}
