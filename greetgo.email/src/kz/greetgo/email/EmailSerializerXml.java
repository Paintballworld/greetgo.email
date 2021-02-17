package kz.greetgo.email;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

public class EmailSerializerXml implements EmailSerializer {

  @Override
  public String serialize(Email email) {
    try {
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

      Element letter = document.createElement("letter");
      document.appendChild(letter);

      addTag(document, letter, "subject", email.getSubject());
      addTag(document, letter, "to", email.getTo());
      for (String copy : email.getCopies()) {
        addTag(document, letter, "copy", copy);
      }
      addTag(document, letter, "from", email.getFrom());
      addTag(document, letter, "body", email.getBody());

      for (Attachment attachment : email.getAttachments()) {
        addAttachment(document, letter, attachment);
      }

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer        transformer        = transformerFactory.newTransformer();
      DOMSource          source             = new DOMSource(document);
      StringWriter       stringWriter       = new StringWriter();
      StreamResult       result             = new StreamResult(stringWriter);

      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      transformer.transform(source, result);

      return stringWriter.toString();
    } catch (ParserConfigurationException | TransformerException e) {
      throw new RuntimeException(e);
    }
  }

  private void addAttachment(Document document, Element letter, Attachment attachment) {
    Element element = document.createElement("attachment");
    letter.appendChild(element);
    element.setAttribute("name", attachment.name);
    element.setTextContent(Base64.getEncoder().encodeToString(attachment.data));
  }

  private void addTag(Document document, Element letter, String tagName, String tagContent) {
    Element element = document.createElement(tagName);
    letter.appendChild(element);
    element.setTextContent(tagContent);
  }

  @Override
  public void serialize(PrintStream out, Email email) {
    out.println(serialize(email));
  }

  @Override
  public void serialize(File file, Email email) {
    try (PrintStream out = new PrintStream(file, "UTF-8")) {
      serialize(out, email);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void serialize(OutputStream outStream, Email email) {
    try (PrintStream out = new PrintStream(outStream, false, "UTF-8")) {
      serialize(out, email);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private SAXParser parser() throws ParserConfigurationException, SAXException {
    return SAXParserFactory.newInstance().newSAXParser();
  }

  @Override
  public Email deserialize(InputStream bin) {
    ParseHandler handler = new ParseHandler();
    try {
      parser().parse(bin, handler);
    } catch (SAXException | ParserConfigurationException | IOException e) {
      throw new RuntimeException(e);
    }
    return handler.target;
  }

  @Override
  public Email deserialize(String xmlString) {
    ParseHandler handler = new ParseHandler();
    try {
      parser().parse(new ByteArrayInputStream(xmlString.getBytes(UTF_8)), handler);
    } catch (SAXException | ParserConfigurationException | IOException e) {
      throw new RuntimeException(e);
    }
    return handler.target;
  }

  @Override
  public Email deserialize(File file) {
    try (FileInputStream fin = new FileInputStream(file)) {
      return deserialize(fin);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static class ParseHandler extends DefaultHandler {
    final Email target = new Email();

    StringBuilder text = null;

    private String text() {
      StringBuilder x = text;
      return x == null ? null : x.toString();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
      if (text == null) text = new StringBuilder();
      text.append(ch, start, length);
    }

    Attachment attachment = null;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
      text = null;

      if ("attachment".equals(qName)) {
        attachment      = new Attachment();
        attachment.name = attributes.getValue("name");
      }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
      if ("to".equals(qName)) {
        target.setTo(text());
        return;
      }
      if ("copy".equals(qName)) {
        target.getCopies().add(text());
        return;
      }
      if ("from".equals(qName)) {
        target.setFrom(text());
        return;
      }
      if ("body".equals(qName)) {
        target.setBody(text());
        return;
      }
      if ("subject".equals(qName)) {
        target.setSubject(text());
        return;
      }
      if ("attachment".equals(qName)) {
        if (attachment != null) {
          attachment.data = Base64.getDecoder().decode(text());
          target.getAttachments().add(attachment);
          attachment = null;
        }
      }
    }
  }
}
