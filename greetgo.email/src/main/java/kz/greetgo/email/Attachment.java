package kz.greetgo.email;

public class Attachment {
  public String name;
  public byte[] data;
  
  public Attachment() {}
  
  public Attachment(String name, byte[] data) {
    this.name = name;
    this.data = data.clone();
  }
}
