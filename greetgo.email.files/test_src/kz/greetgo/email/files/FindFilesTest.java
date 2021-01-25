package kz.greetgo.email.files;

import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class FindFilesTest {

  @Test
  public void recursively() throws Exception {
    Random rnd = new Random();

    String dir = "build/" + rnd.nextLong();

    File file1 = create(new File(dir + "/asd/dsa/wow.asd.txt"));
    File file2 = create(new File(dir + "/asd/dsa/wow.txt"));
    File file3 = create(new File(dir + "/asd/dsa/asd.txt"));
    File file4 = create(new File(dir + "/dsa/asd/wow.wow.txt"));
    File fileLeft = create(new File(dir + "/asd/dsa/asd.txt.left"));

    //
    //
    List<File> files = FindFiles.recursively(new File(dir), ".txt");
    //
    //

    assertThat(files).hasSize(4);

    Set<String> fileSet = new HashSet<>();
    for (File file : files) {
      fileSet.add(file.getAbsolutePath().toUpperCase());
    }

    assertThat(fileSet).contains(file1.getAbsolutePath().toUpperCase());
    assertThat(fileSet).contains(file2.getAbsolutePath().toUpperCase());
    assertThat(fileSet).contains(file3.getAbsolutePath().toUpperCase());
    assertThat(fileSet).contains(file4.getAbsolutePath().toUpperCase());
    assertThat(fileSet).doesNotContain(fileLeft.getAbsolutePath().toUpperCase());


    assertThat(FindFiles.recursively(new File(dir), null)).hasSize(5);
  }

  private File create(File file) throws IOException {
    //noinspection ResultOfMethodCallIgnored
    file.getParentFile().mkdirs();
    //noinspection ResultOfMethodCallIgnored
    file.createNewFile();
    return file;
  }
}
