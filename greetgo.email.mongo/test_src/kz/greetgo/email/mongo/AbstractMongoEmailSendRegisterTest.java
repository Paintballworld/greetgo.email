package kz.greetgo.email.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import kz.greetgo.email.Email;
import kz.greetgo.email.EmailSender;
import kz.greetgo.email.RealEmailSender;
import kz.greetgo.email.mongo.test.TestParent;
import kz.greetgo.util.RND;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static kz.greetgo.email.mongo.RecordFields.*;
import static kz.greetgo.email.mongo.RecordFields.INSERTED_AT;
import static org.assertj.core.api.Assertions.assertThat;

public class AbstractMongoEmailSendRegisterTest extends TestParent {

  @Test
  public void send__sendAllExistingEmails__cleanOldSentEntries() {

    MongoDatabase             database       = getTestDb();
    MongoCollection<Document> collection     = database.getCollection("test_" + RND.strEng(20));
    GregorianCalendar         time           = new GregorianCalendar();
    List<Email>               realSentList   = new ArrayList<>();
    List<ObjectId>            insertedIdList = new ArrayList<>();

    time.setTime(RND.dateYears(-2, 0));

    AbstractMongoEmailSendRegister testRegister = new AbstractMongoEmailSendRegister() {
      @Override
      protected MongoCollection<Document> collection() {
        return collection;
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
      protected void useJustInsertedId(ObjectId justInsertedId) {
        insertedIdList.add(justInsertedId);
      }
    };

    EmailSender emailSaver = testRegister.createEmailSaver();

    Email email = rndEmail();

    Date insertTime = time.getTime();

    //
    //
    emailSaver.send(email);
    //
    //

    time.add(Calendar.MINUTE, 3);

    assertThat(realSentList).isEmpty();

    assertThat(insertedIdList).hasSize(1);

    ObjectId documentId = insertedIdList.get(0);

    {
      Document document = collection.find(eq("_id", documentId)).first();
      assertThat(document).isNotNull();

      Date     insertedAt     = document.getDate(INSERTED_AT);
      boolean  sent           = document.getBoolean(SENT, false);
      Date     sendStartedAt  = document.getDate(SEND_STARTED_AT);
      Date     sendFinishedAt = document.getDate(SEND_FINISHED_AT);
      ObjectId operationId    = document.getObjectId(OPERATION_ID);

      assertThat(insertedAt).isEqualTo(insertTime);
      assertThat(sent).isFalse();
      assertThat(sendStartedAt).isNull();
      assertThat(sendFinishedAt).isNull();
      assertThat(operationId).isNull();
    }

    Date startSendTime = time.getTime();

    //
    //
    testRegister.sendAllExistingEmails();
    //
    //

    Date endSendTime = time.getTime();

    time.add(Calendar.MINUTE, 5);

    assertThat(startSendTime).isNotEqualTo(endSendTime);

    assertThat(realSentList).hasSize(1);
    assertThat(realSentList.get(0).getTo()).isEqualTo(email.getTo());
    assertThat(realSentList.get(0).getSubject()).isEqualTo(email.getSubject());

    {
      Document document = collection.find(eq("_id", documentId)).first();
      assertThat(document).isNotNull();

      Date     insertedAt     = document.getDate(INSERTED_AT);
      boolean  sent           = document.getBoolean(SENT, false);
      Date     sendStartedAt  = document.getDate(SEND_STARTED_AT);
      Date     sendFinishedAt = document.getDate(SEND_FINISHED_AT);
      ObjectId operationId    = document.getObjectId(OPERATION_ID);

      assertThat(insertedAt).isEqualTo(insertTime);
      assertThat(sent).isTrue();
      assertThat(sendStartedAt).isEqualTo(startSendTime);
      assertThat(sendFinishedAt).isEqualTo(endSendTime);
      assertThat(operationId).isNotNull();
    }

    time.add(Calendar.MINUTE, 5);

    //
    //
    testRegister.cleanOldSentEntries(3);
    //
    //

    {
      Document document = collection.find(eq("_id", documentId)).first();
      assertThat(document).isNotNull();
    }

    time.add(Calendar.DAY_OF_YEAR, 5);

    //
    //
    testRegister.cleanOldSentEntries(3);
    //
    //

    {
      Document document = collection.find(eq("_id", documentId)).first();
      assertThat(document).isNull();
    }

  }

}
