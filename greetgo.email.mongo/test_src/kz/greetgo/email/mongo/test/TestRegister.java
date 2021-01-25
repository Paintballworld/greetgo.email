package kz.greetgo.email.mongo.test;

import com.mongodb.client.MongoCollection;
import kz.greetgo.email.Email;
import kz.greetgo.email.RealEmailSender;
import kz.greetgo.email.mongo.AbstractMongoEmailSendRegister;
import kz.greetgo.util.RND;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class TestRegister extends AbstractMongoEmailSendRegister {

  private final MongoCollection<Document> collection;
  public final  GregorianCalendar         time         = new GregorianCalendar();
  public final  List<Email>               realSentList = new ArrayList<>();

  public TestRegister(MongoCollection<Document> collection) {
    this.collection = collection;
    time.setTime(RND.dateYears(-2, 0));
  }

  @Override
  protected RealEmailSender realEmailSender() {
    return email -> {
      realSentList.add(email);
      time.add(Calendar.SECOND, 30);
    };
  }

  @Override
  protected Date now() {
    return time.getTime();
  }

  @Override
  protected MongoCollection<Document> collection() {
    return collection;
  }

  public final List<ObjectId> insertedIdList = new ArrayList<>();

  @Override
  protected void useJustInsertedId(ObjectId justInsertedId) {
    insertedIdList.add(justInsertedId);
  }

}
