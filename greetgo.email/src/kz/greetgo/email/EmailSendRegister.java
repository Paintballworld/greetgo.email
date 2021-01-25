package kz.greetgo.email;

public interface EmailSendRegister {

  /**
   * Send all existing email to real sender
   */
  void sendAllExistingEmails();

  /**
   * Removes old sent email entries
   *
   * @param hoursBefore how old entry in hours to remove
   */
  void cleanOldSentEntries(int hoursBefore);

}
