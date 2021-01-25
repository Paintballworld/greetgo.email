package kz.greetgo.email;

import java.util.Arrays;
import java.util.Objects;

public class Attachment {
  public String name;
  public byte[] data;

  public Attachment() {}

  public Attachment(String name, byte[] data) {
    this.name = name;
    this.data = data.clone();
  }

  public static Attachment of(String name, byte[] data) {
    return new Attachment(name, data);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Attachment that = (Attachment) o;

    if (!Objects.equals(name, that.name)) return false;
    return Arrays.equals(data, that.data);
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + Arrays.hashCode(data);
    return result;
  }
}
