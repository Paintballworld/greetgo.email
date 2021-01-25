package kz.greetgo.email.mongo.probes;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class ConnectToMongoProbe {
  public static void main(String[] args) {
    new ConnectToMongoProbe().execute();
  }

  private void execute() {

    try (MongoClient mongoClient = new MongoClient("localhost", 31098)) {

      MongoDatabase             db       = mongoClient.getDatabase("greetgo_email");
      MongoCollection<Document> emails   = db.getCollection("emails");
      Document                  document = new Document();

      document.append("wow", "Value of wow");
      emails.insertOne(document);

    }
    System.out.println("TUjlQCv734");
  }
}
