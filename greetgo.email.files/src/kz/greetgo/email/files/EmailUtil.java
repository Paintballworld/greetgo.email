package kz.greetgo.email.files;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class EmailUtil {

  public static List<File> findFilesRecursively(File directory, String extension) {
    final List<File> result = new ArrayList<>();
    final LinkedList<File> dirsToScan = new LinkedList<>();
    dirsToScan.offerLast(directory);

    final Set<File> alreadyScannedDirs = new HashSet<>();

    OUTER:
    while (true) {

      File dir = dirsToScan.pollFirst();
      if (dir == null) return result;

      {
        if (alreadyScannedDirs.contains(dir)) continue;
        alreadyScannedDirs.add(dir);
      }

      {
        File[] files = dir.listFiles();
        if (files == null) continue;
        INNER:
        for (File file : files) {
          if (file.isDirectory()) {
            dirsToScan.offerLast(file);
            continue INNER;
          }

          if (file.isFile() && (extension == null || file.getName().endsWith(extension))) {
            result.add(file);
          }
        }
      }
    }//OUTER
  }
}
