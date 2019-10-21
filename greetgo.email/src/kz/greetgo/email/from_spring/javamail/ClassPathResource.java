package kz.greetgo.email.from_spring.javamail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ClassPathResource implements Resource {

  private final String name;
  private final Class<?> aClass;

  public ClassPathResource(String name, Class<?> aClass) {
    this.name = name;
    this.aClass = aClass;
  }

  @Override
  public boolean exists() {
    return false;
  }

  @Override
  public boolean isReadable() {
    return true;
  }

  @Override
  public boolean isOpen() {
    throw new RuntimeException("Illegal operation");
  }

  @Override
  public URL getURL() throws IOException {
    return aClass.getResource(name);
  }

  @Override
  public URI getURI() throws IOException {
    try {
      return getURL().toURI();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public File getFile() throws IOException {
    throw new RuntimeException("Illegal operation");
  }

  @Override
  public long contentLength() throws IOException {
    throw new RuntimeException("Illegal operation");
  }

  @Override
  public long lastModified() throws IOException {
    throw new RuntimeException("Illegal operation");
  }

  @Override
  public Resource createRelative(String relativePath) throws IOException {
    return new ClassPathResource(relativePath, aClass);
  }

  @Override
  public String getFilename() {
    return name;
  }

  @Override
  public String getDescription() {
    return "no description";
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return aClass.getResourceAsStream(name);
  }
}
