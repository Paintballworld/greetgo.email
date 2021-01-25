package kz.greetgo.email;

public interface EmailSendRegister {

  /**
   * Send all existing email to real sender
   */
  void sendAllExistingEmails();

  /**
   * Removes old sent email entries
   *
   * @param daysBefore how old entry in days to remove
   */
  void cleanOldSentEntries(int daysBefore);

}
