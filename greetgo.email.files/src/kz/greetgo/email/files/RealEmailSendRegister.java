package kz.greetgo.email.files;

public interface RealEmailSendRegister {
  /**
   * Send all existing email to real sender
   */
  void sendAllExistingEmails();

  /**
   * Removes old files from directory with sent files
   *
   * @param daysBefore how old file in days to remove
   */
  void cleanOldSentFiles(int daysBefore);
}
