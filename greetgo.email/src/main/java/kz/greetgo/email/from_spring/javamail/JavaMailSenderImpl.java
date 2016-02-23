package kz.greetgo.email.from_spring.javamail;

import kz.greetgo.email.from_spring.*;

import javax.activation.FileTypeMap;
import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class JavaMailSenderImpl implements JavaMailSender {

  public static final String DEFAULT_PROTOCOL = "smtp";

  public static final int DEFAULT_PORT = -1;

  private Session session = Session.getInstance(new Properties());

  private String protocol = DEFAULT_PROTOCOL;

  private String host;

  private int port = DEFAULT_PORT;

  private String username;

  private String password;

  private String defaultEncoding;

  private FileTypeMap defaultFileTypeMap;

  public JavaMailSenderImpl() {
    ConfigurableMimeFileTypeMap fileTypeMap = new ConfigurableMimeFileTypeMap();
    fileTypeMap.afterPropertiesSet();
    this.defaultFileTypeMap = fileTypeMap;
  }

  public void setJavaMailProperties(Properties javaMailProperties) {
    this.session = Session.getInstance(javaMailProperties);
  }

  public void setSession(Session session) {
    if (session == null) {
      throw new IllegalArgumentException("Cannot work with a null Session");
    }
    this.session = session;
  }

  public Session getSession() {
    return session;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }


  public String getProtocol() {
    return protocol;
  }


  public void setHost(String host) {
    this.host = host;
  }

  public String getHost() {
    return host;
  }


  public void setPort(int port) {
    this.port = port;
  }


  public int getPort() {
    return port;
  }


  public void setUsername(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }


  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return password;
  }


  public void setDefaultEncoding(String defaultEncoding) {
    this.defaultEncoding = defaultEncoding;
  }


  public String getDefaultEncoding() {
    return defaultEncoding;
  }


  public void setDefaultFileTypeMap(FileTypeMap defaultFileTypeMap) {
    this.defaultFileTypeMap = defaultFileTypeMap;
  }


  public FileTypeMap getDefaultFileTypeMap() {
    return defaultFileTypeMap;
  }


  public void send(SimpleMailMessage simpleMessage) throws MailException {
    send(new SimpleMailMessage[]{simpleMessage});
  }

  public void send(SimpleMailMessage[] simpleMessages) throws MailException {
    List mimeMessages = new ArrayList(simpleMessages.length);
    for (int i = 0; i < simpleMessages.length; i++) {
      SimpleMailMessage simpleMessage = simpleMessages[i];
      MimeMailMessage message = new MimeMailMessage(createMimeMessage());
      simpleMessage.copyTo(message);
      mimeMessages.add(message.getMimeMessage());
    }
    doSend((MimeMessage[]) mimeMessages.toArray(new MimeMessage[mimeMessages.size()]), simpleMessages);
  }

  public MimeMessage createMimeMessage() {
    return new SmartMimeMessage(getSession(), getDefaultEncoding(), getDefaultFileTypeMap());
  }

  public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
    try {
      return new MimeMessage(getSession(), contentStream);
    } catch (MessagingException ex) {
      throw new MailParseException("Could not parse raw MIME content", ex);
    }
  }

  public void send(MimeMessage mimeMessage) throws MailException {
    send(new MimeMessage[]{mimeMessage});
  }

  public void send(MimeMessage[] mimeMessages) throws MailException {
    doSend(mimeMessages, null);
  }

  public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
    send(new MimeMessagePreparator[]{mimeMessagePreparator});
  }

  public void send(MimeMessagePreparator[] mimeMessagePreparators) throws MailException {
    try {
      List mimeMessages = new ArrayList(mimeMessagePreparators.length);
      for (int i = 0; i < mimeMessagePreparators.length; i++) {
        MimeMessage mimeMessage = createMimeMessage();
        mimeMessagePreparators[i].prepare(mimeMessage);
        mimeMessages.add(mimeMessage);
      }
      send((MimeMessage[]) mimeMessages.toArray(new MimeMessage[mimeMessages.size()]));
    } catch (MailException ex) {
      throw ex;
    } catch (MessagingException ex) {
      throw new MailParseException(ex);
    } catch (IOException ex) {
      throw new MailPreparationException(ex);
    } catch (Exception ex) {
      throw new MailPreparationException(ex);
    }
  }


  protected void doSend(MimeMessage[] mimeMessages, Object[] originalMessages) throws MailException {
    Map failedMessages = new HashMap();
    try {
      Transport transport = getTransport(getSession());
      transport.connect(getHost(), getPort(), getUsername(), getPassword());
      try {
        for (int i = 0; i < mimeMessages.length; i++) {
          MimeMessage mimeMessage = mimeMessages[i];
          try {
            if (mimeMessage.getSentDate() == null) {
              mimeMessage.setSentDate(new Date());
            }
            mimeMessage.saveChanges();
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
          } catch (MessagingException ex) {
            Object original = (originalMessages != null ? originalMessages[i] : mimeMessage);
            failedMessages.put(original, ex);
          }
        }
      } finally {
        transport.close();
      }
    } catch (AuthenticationFailedException ex) {
      throw new MailAuthenticationException(ex);
    } catch (MessagingException ex) {
      throw new MailSendException("Mail server connection failed", ex);
    }
    if (!failedMessages.isEmpty()) {
      throw new MailSendException(failedMessages);
    }
  }


  protected Transport getTransport(Session session) throws NoSuchProviderException {
    return session.getTransport(getProtocol());
  }

}