package kz.greetgo.email.mongo;

import kz.greetgo.email.Email;
import kz.greetgo.email.EmailSender;

public abstract class AbstractEmailMongoSaver implements EmailSender {
  @Override
  public void send(Email email) {
    throw new RuntimeException("Not impl yet: EmailMongoSaver.send");
  }
}
