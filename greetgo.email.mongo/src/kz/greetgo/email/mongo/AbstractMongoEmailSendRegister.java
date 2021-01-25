package kz.greetgo.email.mongo;

import com.mongodb.client.MongoCollection;
import kz.greetgo.email.EmailSendRegister;
import kz.greetgo.email.EmailSender;
import kz.greetgo.email.RealEmailSender;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Date;

public abstract class AbstractMongoEmailSendRegister implements EmailSendRegister {
  protected abstract MongoCollection<Document> collection();

  protected abstract RealEmailSender realEmailSender();

  public EmailSender createEmailSaver() {
    return email -> {

      System.out.println("4Uk3yoRcJI :: save email " + email);

    };
  }

  @Override
  public void sendAllExistingEmails() {
    throw new RuntimeException("Not impl yet: AbstractMongoEmailSendRegister.sendAllExistingEmails");
  }

  @Override
  public void cleanOldSentEntries(int daysBefore) {
    throw new RuntimeException("Not impl yet: AbstractMongoEmailSendRegister.cleanOldSentEntries");
  }

  protected void useJustInsertedId(ObjectId justInsertedId) {}

  protected Date now() {
    return new Date();
  }
}
