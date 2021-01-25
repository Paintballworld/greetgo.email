package kz.greetgo.email;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public interface EmailSerializer {
  String serialize(Email email);

  void serialize(PrintStream out, Email email);

  void serialize(OutputStream outStream, Email email);

  void serialize(File file, Email email);

  Email deserialize(InputStream bin);

  Email deserialize(String xmlString);

  Email deserialize(File file);
}
