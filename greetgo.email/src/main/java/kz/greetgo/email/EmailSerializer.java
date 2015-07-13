package kz.greetgo.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class EmailSerializer {
  public void serialize(PrintStream out, Email email) {
    out.println("<?xml version='1.0' encoding='UTF-8' ?>");
    out.println("<letter>");
    printTag(out, "to", email.getTo());
    for (String copy : email.getCopies()) {
      printTag(out, "copy", copy);
    }
    printTag(out, "from", email.getFrom());
    printTag(out, "subject", email.getSubject());
    printTag(out, "body", email.getBody());
    if (!email.getAttachments().isEmpty()) {
      printAttachemts(out, email.getAttachments());
    }
    out.println("</letter>");
  }
  
  private void printAttachemts(PrintStream out, List<Attachment> attachments) {
    out.println();
    for (Attachment attachment : attachments) {
      printAttachment(out, attachment);
    }
  }
  
  private void printAttachment(PrintStream out, Attachment attachment) {
    if (attachment == null) return;
    if (attachment.data == null) return;
    
    out.print("\t<attachment name=\"" + attachment.name + "\">");
    out.print(DatatypeConverter.printBase64Binary(attachment.data));
    out.println("</attachment>");
  }
  
  public void serialize(File file, Email email) throws Exception {
    PrintStream out = new PrintStream(file, "UTF-8");
    serialize(out, email);
    out.close();
  }
  
  public void serialize(OutputStream outStream, Email email) throws Exception {
    PrintStream out = new PrintStream(outStream, false, "UTF-8");
    serialize(out, email);
    out.flush();
  }
  
  private static void printTag(PrintStream out, String tagName, String content) {
    out.print("\t<");
    out.print(tagName);
    out.print("><![CDATA[");
    out.print(content);
    out.print("]]></");
    out.print(tagName);
    out.println('>');
  }
  
  private SAXParser parser;
  
  private SAXParser parser() throws Exception {
    if (parser == null) {
      parser = SAXParserFactory.newInstance().newSAXParser();
    }
    return parser;
  }
  
  public Email deserialize(InputStream bin) throws Exception {
    ParseHandler handler = new ParseHandler();
    parser().parse(bin, handler);
    return handler.target;
  }
  
  public Email deserialize(File file) throws Exception {
    FileInputStream fin = null;
    try {
      fin = new FileInputStream(file);
      return deserialize(fin);
    } finally {
      if (fin != null) {
        fin.close();
      }
    }
  }
  
  private static class ParseHandler extends DefaultHandler {
    final Email target = new Email();
    
    StringBuilder text = null;
    
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      if (text == null) text = new StringBuilder();
      text.append(ch, start, length);
    }
    
    Attachment attachment = null;
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
        throws SAXException {
      text = null;
      
      if ("attachment".equals(qName)) {
        attachment = new Attachment();
        attachment.name = attributes.getValue("name");
      }
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
      if ("to".equals(qName)) {
        target.setTo(text.toString());
        return;
      }
      if ("copy".equals(qName)) {
        target.getCopies().add(text.toString());
        return;
      }
      if ("from".equals(qName)) {
        target.setFrom(text.toString());
        return;
      }
      if ("body".equals(qName)) {
        target.setBody(text.toString());
        return;
      }
      if ("subject".equals(qName)) {
        target.setSubject(text.toString());
        return;
      }
      if ("attachment".equals(qName)) {
        if (attachment != null) {
          attachment.data = DatatypeConverter.parseBase64Binary(text.toString());
          target.getAttachments().add(attachment);
          attachment = null;
        }
      }
    }
  }
}
