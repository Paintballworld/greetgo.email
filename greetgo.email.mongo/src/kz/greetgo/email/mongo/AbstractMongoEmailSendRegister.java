package kz.greetgo.email.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import kz.greetgo.email.Email;
import kz.greetgo.email.EmailSendRegister;
import kz.greetgo.email.EmailSender;
import kz.greetgo.email.EmailSerializer;
import kz.greetgo.email.EmailSerializerXml;
import kz.greetgo.email.RealEmailSender;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.exists;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static kz.greetgo.email.mongo.RecordFields.CONTENT;
import static kz.greetgo.email.mongo.RecordFields.INSERTED_AT;
import static kz.greetgo.email.mongo.RecordFields.OPERATION_ID;
import static kz.greetgo.email.mongo.RecordFields.SEND_ERR;
import static kz.greetgo.email.mongo.RecordFields.SEND_ERR_CLASS;
import static kz.greetgo.email.mongo.RecordFields.SEND_ERR_AT;
import static kz.greetgo.email.mongo.RecordFields.SEND_ERR_MESSAGE;
import static kz.greetgo.email.mongo.RecordFields.SEND_ERR_STACK_STRACE;
import static kz.greetgo.email.mongo.RecordFields.SEND_FINISHED_AT;
import static kz.greetgo.email.mongo.RecordFields.SEND_STARTED_AT;
import static kz.greetgo.email.mongo.RecordFields.SENT_OK;

public abstract class AbstractMongoEmailSendRegister implements EmailSendRegister {
  protected abstract MongoCollection<Document> collection();

  protected abstract RealEmailSender realEmailSender();

  public EmailSender createEmailSaver() {
    return this::saveEmail;
  }

  private void saveEmail(Email email) {
    if (email == null) return;

    Document document = new Document();
    document.put(CONTENT, emailSerializer().serialize(email));
    document.put(INSERTED_AT, now());

    collection().insertOne(document);

    useJustInsertedId(document.getObjectId("_id"));

  }

  protected EmailSerializer emailSerializer() {
    return new EmailSerializerXml();
  }

  private final Random rnd = new SecureRandom();

  private ObjectId rndId() {
    byte[] bytes = new byte[12];
    rnd.nextBytes(bytes);
    return new ObjectId(bytes);
  }

  @Override
  public void sendAllExistingEmails() {
    while (sendOne()) {
      // empty body
    }
  }

  private boolean sendOne() {
    ObjectId operationId = rndId();

    {
      UpdateResult updateResult = collection().updateOne(
        and(
          exists(SENT_OK, false),
          exists(OPERATION_ID, false)
        ),
        combine(
          set(OPERATION_ID, operationId),
          set(SEND_STARTED_AT, now())
        )
      );

      if (updateResult.getModifiedCount() == 0) return false;
    }

    Document document = collection().find(eq(OPERATION_ID, operationId))
                                    .projection(
                                      combine(
                                        eq("_id", 1),
                                        eq(CONTENT, 1)
                                      )
                                    )
                                    .first();

    if (document == null) return true;

    Email email = emailSerializer().deserialize(document.getString(CONTENT));

    try {
      realEmailSender().realSend(email);
    } catch (Throwable sendError) {

      StringWriter strWriter = new StringWriter();
      PrintWriter  printWriter = new PrintWriter(strWriter);
      sendError.printStackTrace(printWriter);
      printWriter.flush();

      collection().updateOne(eq("_id", document.getObjectId("_id")),
                             combine(
                               set(SEND_ERR, true),
                               set(SEND_ERR_AT, now()),
                               set(SEND_ERR_CLASS, sendError.getClass().getName()),
                               set(SEND_ERR_MESSAGE, sendError.getMessage()),
                               set(SEND_ERR_STACK_STRACE, strWriter.toString())
                             ));
      sendError(sendError);
      return true;
    }

    collection().updateOne(eq("_id", document.getObjectId("_id")),
                           combine(
                             set(SENT_OK, true),
                             set(SEND_FINISHED_AT, now())
                           ));

    return true;
  }

  @SuppressWarnings("unused")
  protected void sendError(Throwable sendError) {}

  @Override
  public void cleanOldSentEntries(int hoursBefore) {

    GregorianCalendar calendar = new GregorianCalendar();
    calendar.setTime(now());
    calendar.add(Calendar.HOUR, -hoursBefore);

    collection().deleteMany(and(
      eq(SENT_OK, true),
      lt(SEND_FINISHED_AT, calendar.getTime())
    ));

  }

  protected void useJustInsertedId(ObjectId justInsertedId) {}

  protected Date now() {
    return new Date();
  }
}
