package kz.greetgo.email.from_spring.javamail;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.beans.PropertyEditorSupport;

public class InternetAddressEditor extends PropertyEditorSupport {

  public void setAsText(String text) throws IllegalArgumentException {
    if (text != null && text.trim().length() > 0) {
      try {
        setValue(new InternetAddress(text));
      } catch (AddressException ex) {
        throw new IllegalArgumentException("Could not parse mail address: " + ex.getMessage());
      }
    } else {
      setValue(null);
    }
  }

  public String getAsText() {
    InternetAddress value = (InternetAddress) getValue();
    return (value != null ? value.toUnicodeString() : "");
  }

}