package kz.greetgo.email;

import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class EmailSerializerTest {

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

    EmailSerializer emailSerializer = new EmailSerializer();

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
}
