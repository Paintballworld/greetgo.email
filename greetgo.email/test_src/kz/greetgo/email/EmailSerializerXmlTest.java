package kz.greetgo.email;

import kz.greetgo.util.RND;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class EmailSerializerXmlTest {

  @Test
  public void serialize__deserialize() throws Exception {

    Email email = new Email();
    email.setBody(RND.str(30));
    email.setFrom(RND.str(10));
    email.setTo(RND.str(10));
    email.setSubject(RND.str(10));
    email.getCopies().add(RND.str(10));
    email.getCopies().add(RND.str(10));
    email.getCopies().add(RND.str(10));

    email.getAttachments().add(Attachment.of(RND.str(10), RND.byteArray(100)));
    email.getAttachments().add(Attachment.of(RND.str(10), RND.byteArray(100)));
    email.getAttachments().add(Attachment.of(RND.str(10), RND.byteArray(100)));

    EmailSerializerXml emailSerializer = new EmailSerializerXml();

    ByteArrayOutputStream outStream = new ByteArrayOutputStream();

    //
    //
    emailSerializer.serialize(outStream, email);
    //
    //

    String content = outStream.toString(UTF_8.name());

    System.out.println("N5jd1t1KNw\n" + content);

    ByteArrayInputStream inputStream = new ByteArrayInputStream(outStream.toByteArray());

    //
    //
    Email actual = emailSerializer.deserialize(inputStream);
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual.getSubject()).isEqualTo(email.getSubject());
    assertThat(actual.getBody()).isEqualTo(email.getBody());
    assertThat(actual.getFrom()).isEqualTo(email.getFrom());
    assertThat(actual.getTo()).isEqualTo(email.getTo());
    assertThat(actual.getCopies()).isEqualTo(email.getCopies());
    assertThat(actual.getAttachments()).isEqualTo(email.getAttachments());
  }

  @Test
  public void name() {

    try {
      Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

      Element letter = document.createElement("letter");
      document.appendChild(letter);

      Element from = document.createElement("from");
      letter.appendChild(from);

      from.setTextContent("hello world <a> wow </a> \" xxx");

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer        transformer        = transformerFactory.newTransformer();
      DOMSource          source             = new DOMSource(document);
      StringWriter       stringWriter       = new StringWriter();
      StreamResult       result             = new StreamResult(stringWriter);

      transformer.transform(source, result);

      String str = stringWriter.toString();
      System.out.println("XL95d0069k :: " + str);

    } catch (ParserConfigurationException | TransformerException e) {
      throw new RuntimeException(e);
    }
  }
}
