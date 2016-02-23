
package kz.greetgo.email.from_spring.javamail;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.*;
import java.io.*;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;


public class MimeMessageHelper {

  public static final int MULTIPART_MODE_NO = 0;

  public static final int MULTIPART_MODE_MIXED = 1;

  public static final int MULTIPART_MODE_RELATED = 2;

  public static final int MULTIPART_MODE_MIXED_RELATED = 3;


  private static final String MULTIPART_SUBTYPE_MIXED = "mixed";

  private static final String MULTIPART_SUBTYPE_RELATED = "related";

  private static final String MULTIPART_SUBTYPE_ALTERNATIVE = "alternative";

  private static final String CONTENT_TYPE_ALTERNATIVE = "text/alternative";

  private static final String CONTENT_TYPE_HTML = "text/html";

  private static final String CONTENT_TYPE_CHARSET_SUFFIX = ";charset=";

  private static final String HEADER_CONTENT_ID = "Content-ID";


  private final MimeMessage mimeMessage;

  private MimeMultipart rootMimeMultipart;

  private MimeMultipart mimeMultipart;

  private final String encoding;

  private FileTypeMap fileTypeMap;

  private boolean validateAddresses = false;


  public MimeMessageHelper(MimeMessage mimeMessage) {
    this(mimeMessage, null);
  }

  public MimeMessageHelper(MimeMessage mimeMessage, String encoding) {
    this.mimeMessage = mimeMessage;
    this.encoding = (encoding != null ? encoding : getDefaultEncoding(mimeMessage));
    this.fileTypeMap = getDefaultFileTypeMap(mimeMessage);
  }

  public MimeMessageHelper(MimeMessage mimeMessage, boolean multipart) throws MessagingException {
    this(mimeMessage, multipart, null);
  }

  public MimeMessageHelper(MimeMessage mimeMessage, boolean multipart, String encoding)
    throws MessagingException {

    this(mimeMessage, (multipart ? MULTIPART_MODE_MIXED_RELATED : MULTIPART_MODE_NO), encoding);
  }

  public MimeMessageHelper(MimeMessage mimeMessage, int multipartMode) throws MessagingException {
    this(mimeMessage, multipartMode, null);
  }

  public MimeMessageHelper(MimeMessage mimeMessage, int multipartMode, String encoding)
    throws MessagingException {

    this.mimeMessage = mimeMessage;
    createMimeMultiparts(mimeMessage, multipartMode);
    this.encoding = (encoding != null ? encoding : getDefaultEncoding(mimeMessage));
    this.fileTypeMap = getDefaultFileTypeMap(mimeMessage);
  }


  public final MimeMessage getMimeMessage() {
    return mimeMessage;
  }


  protected void createMimeMultiparts(MimeMessage mimeMessage, int multipartMode) throws MessagingException {
    switch (multipartMode) {
      case MULTIPART_MODE_NO:
        setMimeMultiparts(null, null);
        break;
      case MULTIPART_MODE_MIXED:
        MimeMultipart mixedMultipart = new MimeMultipart(MULTIPART_SUBTYPE_MIXED);
        mimeMessage.setContent(mixedMultipart);
        setMimeMultiparts(mixedMultipart, mixedMultipart);
        break;
      case MULTIPART_MODE_RELATED:
        MimeMultipart relatedMultipart = new MimeMultipart(MULTIPART_SUBTYPE_RELATED);
        mimeMessage.setContent(relatedMultipart);
        setMimeMultiparts(relatedMultipart, relatedMultipart);
        break;
      case MULTIPART_MODE_MIXED_RELATED:
        MimeMultipart rootMixedMultipart = new MimeMultipart(MULTIPART_SUBTYPE_MIXED);
        mimeMessage.setContent(rootMixedMultipart);
        MimeMultipart nestedRelatedMultipart = new MimeMultipart(MULTIPART_SUBTYPE_RELATED);
        MimeBodyPart relatedBodyPart = new MimeBodyPart();
        relatedBodyPart.setContent(nestedRelatedMultipart);
        rootMixedMultipart.addBodyPart(relatedBodyPart);
        setMimeMultiparts(rootMixedMultipart, nestedRelatedMultipart);
        break;
      default:
        throw new IllegalArgumentException("Only multipart modes MIXED_RELATED, RELATED and NO supported");
    }
  }

  protected final void setMimeMultiparts(MimeMultipart root, MimeMultipart main) {
    this.rootMimeMultipart = root;
    this.mimeMultipart = main;
  }

  public final boolean isMultipart() {
    return (this.rootMimeMultipart != null);
  }

  private void checkMultipart() throws IllegalStateException {
    if (!isMultipart()) {
      throw new IllegalStateException("Not in multipart mode - " +
        "create an appropriate MimeMessageHelper via a constructor that takes a 'multipart' flag " +
        "if you need to set alternative texts or add inline elements or attachments.");
    }
  }

  public final MimeMultipart getRootMimeMultipart() throws IllegalStateException {
    checkMultipart();
    return this.rootMimeMultipart;
  }

  public final MimeMultipart getMimeMultipart() throws IllegalStateException {
    checkMultipart();
    return this.mimeMultipart;
  }


  protected String getDefaultEncoding(MimeMessage mimeMessage) {
    if (mimeMessage instanceof SmartMimeMessage) {
      return ((SmartMimeMessage) mimeMessage).getDefaultEncoding();
    }
    return null;
  }

  public String getEncoding() {
    return encoding;
  }

  protected FileTypeMap getDefaultFileTypeMap(MimeMessage mimeMessage) {
    if (mimeMessage instanceof SmartMimeMessage) {
      FileTypeMap fileTypeMap = ((SmartMimeMessage) mimeMessage).getDefaultFileTypeMap();
      if (fileTypeMap != null) {
        return fileTypeMap;
      }
    }
    ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();
    fileTypeMap.afterPropertiesSet();
    return fileTypeMap;
  }

  public void setFileTypeMap(FileTypeMap fileTypeMap) {
    this.fileTypeMap = (fileTypeMap != null ? fileTypeMap : getDefaultFileTypeMap(getMimeMessage()));
  }

  public FileTypeMap getFileTypeMap() {
    return fileTypeMap;
  }


  public void setValidateAddresses(boolean validateAddresses) {
    this.validateAddresses = validateAddresses;
  }

  public boolean isValidateAddresses() {
    return validateAddresses;
  }

  protected void validateAddress(InternetAddress address) throws AddressException {
    if (isValidateAddresses()) {
      address.validate();
    }
  }

  protected void validateAddresses(InternetAddress[] addresses) throws AddressException {
    for (int i = 0; i < addresses.length; i++) {
      validateAddress(addresses[i]);
    }
  }


  public void setFrom(InternetAddress from) throws MessagingException {
    notNull(from, "From address must not be null");
    validateAddress(from);
    this.mimeMessage.setFrom(from);
  }

  private void notNull(Object from, String message) {
    assertThat(from).as(message).isNotNull();
  }

  public void setFrom(String from) throws MessagingException {
    notNull(from, "From address must not be null");
    setFrom(new InternetAddress(from));
  }

  public void setFrom(String from, String personal) throws MessagingException, UnsupportedEncodingException {
    notNull(from, "From address must not be null");
    setFrom(getEncoding() != null ?
      new InternetAddress(from, personal, getEncoding()) : new InternetAddress(from, personal));
  }

  public void setReplyTo(InternetAddress replyTo) throws MessagingException {
    notNull(replyTo, "Reply-to address must not be null");
    validateAddress(replyTo);
    this.mimeMessage.setReplyTo(new InternetAddress[]{replyTo});
  }

  public void setReplyTo(String replyTo) throws MessagingException {
    notNull(replyTo, "Reply-to address must not be null");
    setReplyTo(new InternetAddress(replyTo));
  }

  public void setReplyTo(String replyTo, String personal) throws MessagingException, UnsupportedEncodingException {
    notNull(replyTo, "Reply-to address must not be null");
    InternetAddress replyToAddress = (getEncoding() != null) ?
      new InternetAddress(replyTo, personal, getEncoding()) : new InternetAddress(replyTo, personal);
    setReplyTo(replyToAddress);
  }


  public void setTo(InternetAddress to) throws MessagingException {
    notNull(to, "To address must not be null");
    validateAddress(to);
    this.mimeMessage.setRecipient(Message.RecipientType.TO, to);
  }

  public void setTo(InternetAddress[] to) throws MessagingException {
    notNull(to, "To address array must not be null");
    validateAddresses(to);
    this.mimeMessage.setRecipients(Message.RecipientType.TO, to);
  }

  public void setTo(String to) throws MessagingException {
    notNull(to, "To address must not be null");
    setTo(new InternetAddress(to));
  }

  public void setTo(String[] to) throws MessagingException {
    notNull(to, "To address array must not be null");
    InternetAddress[] addresses = new InternetAddress[to.length];
    for (int i = 0; i < to.length; i++) {
      addresses[i] = new InternetAddress(to[i]);
    }
    setTo(addresses);
  }

  public void addTo(InternetAddress to) throws MessagingException {
    notNull(to, "To address must not be null");
    validateAddress(to);
    this.mimeMessage.addRecipient(Message.RecipientType.TO, to);
  }

  public void addTo(String to) throws MessagingException {
    notNull(to, "To address must not be null");
    addTo(new InternetAddress(to));
  }

  public void addTo(String to, String personal) throws MessagingException, UnsupportedEncodingException {
    notNull(to, "To address must not be null");
    addTo(getEncoding() != null ?
      new InternetAddress(to, personal, getEncoding()) :
      new InternetAddress(to, personal));
  }


  public void setCc(InternetAddress cc) throws MessagingException {
    notNull(cc, "Cc address must not be null");
    validateAddress(cc);
    this.mimeMessage.setRecipient(Message.RecipientType.CC, cc);
  }

  public void setCc(InternetAddress[] cc) throws MessagingException {
    notNull(cc, "Cc address array must not be null");
    validateAddresses(cc);
    this.mimeMessage.setRecipients(Message.RecipientType.CC, cc);
  }

  public void setCc(String cc) throws MessagingException {
    notNull(cc, "Cc address must not be null");
    setCc(new InternetAddress(cc));
  }

  public void setCc(String[] cc) throws MessagingException {
    notNull(cc, "Cc address array must not be null");
    InternetAddress[] addresses = new InternetAddress[cc.length];
    for (int i = 0; i < cc.length; i++) {
      addresses[i] = new InternetAddress(cc[i]);
    }
    setCc(addresses);
  }

  public void addCc(InternetAddress cc) throws MessagingException {
    notNull(cc, "Cc address must not be null");
    validateAddress(cc);
    this.mimeMessage.addRecipient(Message.RecipientType.CC, cc);
  }

  public void addCc(String cc) throws MessagingException {
    notNull(cc, "Cc address must not be null");
    addCc(new InternetAddress(cc));
  }

  public void addCc(String cc, String personal) throws MessagingException, UnsupportedEncodingException {
    notNull(cc, "Cc address must not be null");
    addCc(getEncoding() != null ?
      new InternetAddress(cc, personal, getEncoding()) :
      new InternetAddress(cc, personal));
  }


  public void setBcc(InternetAddress bcc) throws MessagingException {
    notNull(bcc, "Bcc address must not be null");
    validateAddress(bcc);
    this.mimeMessage.setRecipient(Message.RecipientType.BCC, bcc);
  }

  public void setBcc(InternetAddress[] bcc) throws MessagingException {
    notNull(bcc, "Bcc address array must not be null");
    validateAddresses(bcc);
    this.mimeMessage.setRecipients(Message.RecipientType.BCC, bcc);
  }

  public void setBcc(String bcc) throws MessagingException {
    notNull(bcc, "Bcc address must not be null");
    setBcc(new InternetAddress(bcc));
  }

  public void setBcc(String[] bcc) throws MessagingException {
    notNull(bcc, "Bcc address array must not be null");
    InternetAddress[] addresses = new InternetAddress[bcc.length];
    for (int i = 0; i < bcc.length; i++) {
      addresses[i] = new InternetAddress(bcc[i]);
    }
    setBcc(addresses);
  }

  public void addBcc(InternetAddress bcc) throws MessagingException {
    notNull(bcc, "Bcc address must not be null");
    validateAddress(bcc);
    this.mimeMessage.addRecipient(Message.RecipientType.BCC, bcc);
  }

  public void addBcc(String bcc) throws MessagingException {
    notNull(bcc, "Bcc address must not be null");
    addBcc(new InternetAddress(bcc));
  }

  public void addBcc(String bcc, String personal) throws MessagingException, UnsupportedEncodingException {
    notNull(bcc, "Bcc address must not be null");
    addBcc(getEncoding() != null ?
      new InternetAddress(bcc, personal, getEncoding()) :
      new InternetAddress(bcc, personal));
  }


  public void setSentDate(Date sentDate) throws MessagingException {
    notNull(sentDate, "Sent date must not be null");
    this.mimeMessage.setSentDate(sentDate);
  }

  public void setSubject(String subject) throws MessagingException {
    notNull(subject, "Subject must not be null");
    if (getEncoding() != null) {
      this.mimeMessage.setSubject(subject, getEncoding());
    } else {
      this.mimeMessage.setSubject(subject);
    }
  }


  /**
   * Set the given text directly as content in non-multipart mode
   * or as default body part in multipart mode.
   * Always applies the default content type "text/plain".
   * <p><b>NOTE:</b> Invoke addInline <i>after</i> setText; else, mail
   * readers might not be able to resolve inline references correctly.
   *
   * @param text the text for the message
   * @throws MessagingException in case of errors
   * @see #addInline
   */
  public void setText(String text) throws MessagingException {
    setText(text, false);
  }

  /**
   * Set the given text directly as content in non-multipart mode
   * or as default body part in multipart mode.
   * The "html" flag determines the content type to apply.
   * <p><b>NOTE:</b> Invoke addInline <i>after</i> setText; else, mail
   * readers might not be able to resolve inline references correctly.
   *
   * @param text the text for the message
   * @param html whether to apply content type "text/html" for an
   *             HTML mail, using default content type ("text/plain") else
   * @throws MessagingException in case of errors
   * @see #addInline
   */
  public void setText(String text, boolean html) throws MessagingException {
    notNull(text, "Text must not be null");
    MimePart partToUse = null;
    if (isMultipart()) {
      partToUse = getMainPart();
    } else {
      partToUse = this.mimeMessage;
    }
    if (html) {
      setHtmlTextToMimePart(partToUse, text);
    } else {
      setPlainTextToMimePart(partToUse, text);
    }
  }

  /**
   * Set the given plain text and HTML text as alternatives, offering
   * both options to the email client. Requires multipart mode.
   * <p><b>NOTE:</b> Invoke addInline <i>after</i> setText; else, mail
   * readers might not be able to resolve inline references correctly.
   *
   * @param plainText the plain text for the message
   * @param htmlText  the HTML text for the message	 * @throws MessagingException in case of errors
   * @see #addInline
   */
  public void setText(String plainText, String htmlText) throws MessagingException {
    notNull(plainText, "Plain text must not be null");
    notNull(htmlText, "HTML text must not be null");

    MimeMultipart messageBody = new MimeMultipart(MULTIPART_SUBTYPE_ALTERNATIVE);
    getMainPart().setContent(messageBody, CONTENT_TYPE_ALTERNATIVE);

    // Create the plain text part of the message.
    MimeBodyPart plainTextPart = new MimeBodyPart();
    setPlainTextToMimePart(plainTextPart, plainText);
    messageBody.addBodyPart(plainTextPart);

    // Create the HTML text part of the message.
    MimeBodyPart htmlTextPart = new MimeBodyPart();
    setHtmlTextToMimePart(htmlTextPart, htmlText);
    messageBody.addBodyPart(htmlTextPart);
  }

  private MimeBodyPart getMainPart() throws MessagingException {
    MimeMultipart mimeMultipart = getMimeMultipart();
    MimeBodyPart bodyPart = null;
    for (int i = 0; i < mimeMultipart.getCount(); i++) {
      BodyPart bp = mimeMultipart.getBodyPart(i);
      if (bp.getFileName() == null) {
        bodyPart = (MimeBodyPart) bp;
      }
    }
    if (bodyPart == null) {
      MimeBodyPart mimeBodyPart = new MimeBodyPart();
      mimeMultipart.addBodyPart(mimeBodyPart);
      bodyPart = mimeBodyPart;
    }
    return bodyPart;
  }

  private void setPlainTextToMimePart(MimePart mimePart, String text) throws MessagingException {
    if (getEncoding() != null) {
      mimePart.setText(text, getEncoding());
    } else {
      mimePart.setText(text);
    }
  }

  private void setHtmlTextToMimePart(MimePart mimePart, String text) throws MessagingException {
    if (getEncoding() != null) {
      mimePart.setContent(text, CONTENT_TYPE_HTML + CONTENT_TYPE_CHARSET_SUFFIX + getEncoding());
    } else {
      mimePart.setContent(text, CONTENT_TYPE_HTML);
    }
  }


  public void addInline(String contentId, DataSource dataSource) throws MessagingException {
    notNull(contentId, "Content ID must not be null");
    notNull(dataSource, "DataSource must not be null");
    MimeBodyPart mimeBodyPart = new MimeBodyPart();
    mimeBodyPart.setDisposition(MimeBodyPart.INLINE);
    // We're using setHeader here to stay compatible with JavaMail 1.2,
    // rather than JavaMail 1.3's setContentID.
    mimeBodyPart.setHeader(HEADER_CONTENT_ID, "<" + contentId + ">");
    mimeBodyPart.setDataHandler(new DataHandler(dataSource));
    getMimeMultipart().addBodyPart(mimeBodyPart);
  }

  public void addInline(String contentId, File file) throws MessagingException {
    notNull(file, "File must not be null");
    FileDataSource dataSource = new FileDataSource(file);
    dataSource.setFileTypeMap(getFileTypeMap());
    addInline(contentId, dataSource);
  }

  public void addInline(String contentId, Resource resource) throws MessagingException {
    notNull(resource, "Resource must not be null");
    String contentType = getFileTypeMap().getContentType(resource.getFilename());
    addInline(contentId, resource, contentType);
  }

  public void addInline(String contentId, InputStreamSource inputStreamSource, String contentType)
    throws MessagingException {

    notNull(inputStreamSource, "InputStreamSource must not be null");
    if (inputStreamSource instanceof Resource && ((Resource) inputStreamSource).isOpen()) {
      throw new IllegalArgumentException(
        "Passed-in Resource contains an open stream: invalid argument. " +
          "JavaMail requires an InputStreamSource that creates a fresh stream for every call.");
    }
    DataSource dataSource = createDataSource(inputStreamSource, contentType, "inline");
    addInline(contentId, dataSource);
  }

  public void addAttachment(String attachmentFilename, DataSource dataSource) throws MessagingException {
    notNull(attachmentFilename, "Attachment filename must not be null");
    notNull(dataSource, "DataSource must not be null");
    MimeBodyPart mimeBodyPart = new MimeBodyPart();
    mimeBodyPart.setDisposition(MimeBodyPart.ATTACHMENT);
    mimeBodyPart.setFileName(attachmentFilename);
    mimeBodyPart.setDataHandler(new DataHandler(dataSource));
    getRootMimeMultipart().addBodyPart(mimeBodyPart);
  }

  public void addAttachment(String attachmentFilename, File file) throws MessagingException {
    notNull(file, "File must not be null");
    FileDataSource dataSource = new FileDataSource(file);
    dataSource.setFileTypeMap(getFileTypeMap());
    addAttachment(attachmentFilename, dataSource);
  }

  public void addAttachment(String attachmentFilename, InputStreamSource inputStreamSource)
    throws MessagingException {

    String contentType = getFileTypeMap().getContentType(attachmentFilename);
    addAttachment(attachmentFilename, inputStreamSource, contentType);
  }

  public void addAttachment(
    String attachmentFilename, InputStreamSource inputStreamSource, String contentType)
    throws MessagingException {

    notNull(inputStreamSource, "InputStreamSource must not be null");
    if (inputStreamSource instanceof Resource && ((Resource) inputStreamSource).isOpen()) {
      throw new IllegalArgumentException(
        "Passed-in Resource contains an open stream: invalid argument. " +
          "JavaMail requires an InputStreamSource that creates a fresh stream for every call.");
    }
    DataSource dataSource = createDataSource(inputStreamSource, contentType, attachmentFilename);
    addAttachment(attachmentFilename, dataSource);
  }

  protected DataSource createDataSource(
    final InputStreamSource inputStreamSource, final String contentType, final String name) {

    return new DataSource() {
      public InputStream getInputStream() throws IOException {
        return inputStreamSource.getInputStream();
      }

      public OutputStream getOutputStream() {
        throw new UnsupportedOperationException("Read-only javax.activation.DataSource");
      }

      public String getContentType() {
        return contentType;
      }

      public String getName() {
        return name;
      }
    };
  }

}