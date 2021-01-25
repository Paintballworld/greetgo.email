package kz.greetgo.email.mongo.test;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import kz.greetgo.email.Attachment;
import kz.greetgo.email.Email;
import kz.greetgo.util.RND;

public abstract class TestParent {

  protected MongoDatabase getTestDb() {
    MongoClient   mongoClient = new MongoClient("localhost", 31098);
    return mongoClient.getDatabase("test-db");
  }

  protected Email rndEmail() {
    Email email = new Email();
    email.setSubject(RND.str(10));
    email.setFrom(RND.str(10));
    email.getCopies().add(RND.str(10));
    email.getCopies().add(RND.str(10));
    email.getCopies().add(RND.str(10));
    email.setTo(RND.str(10));
    email.setBody(RND.str(100));
    email.getAttachments().add(Attachment.of(RND.str(10), RND.byteArray(100)));
    email.getAttachments().add(Attachment.of(RND.str(10), RND.byteArray(100)));
    email.getAttachments().add(Attachment.of(RND.str(10), RND.byteArray(100)));
    return email;
  }

}
