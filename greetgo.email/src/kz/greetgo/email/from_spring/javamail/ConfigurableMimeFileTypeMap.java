package kz.greetgo.email.from_spring.javamail;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ConfigurableMimeFileTypeMap extends FileTypeMap {

  private Resource mappingLocation = new ClassPathResource("mime.types", getClass());
  private String[] mappings;
  private FileTypeMap fileTypeMap;

  public void setMappingLocation(Resource mappingLocation) {
    this.mappingLocation = mappingLocation;
  }

  public void setMappings(String[] mappings) {
    this.mappings = mappings;
  }

  public void afterPropertiesSet() {
    getFileTypeMap();
  }

  protected final FileTypeMap getFileTypeMap() {
    if (this.fileTypeMap == null) {
      try {
        this.fileTypeMap = createFileTypeMap(this.mappingLocation, this.mappings);
      } catch (IOException ex) {
        throw new IllegalStateException(
          "Could not load specified MIME type mapping file: " + this.mappingLocation);
      }
    }
    return fileTypeMap;
  }

  protected FileTypeMap createFileTypeMap(Resource mappingLocation, String[] mappings) throws IOException {
    final MimetypesFileTypeMap fileTypeMap;
    if (mappingLocation == null) {
      fileTypeMap = new MimetypesFileTypeMap();
    } else {
      final InputStream inputStream = mappingLocation.getInputStream();
      if (inputStream == null) {
        throw new NullPointerException("mappingLocation.getInputStream() == null");
      } else {
        fileTypeMap = new MimetypesFileTypeMap(inputStream);
      }
    }

    if (mappings != null) for (String mapping : mappings) {
      fileTypeMap.addMimeTypes(mapping);
    }

    return fileTypeMap;
  }


  public String getContentType(File file) {
    return getFileTypeMap().getContentType(file);
  }

  public String getContentType(String fileName) {
    return getFileTypeMap().getContentType(fileName);
  }

}