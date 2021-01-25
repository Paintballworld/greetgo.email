package kz.greetgo.email.files;

import kz.greetgo.email.EmailSerializer;
import kz.greetgo.email.EmailSerializerXml;
import kz.greetgo.email.RealEmailSender;

import java.io.File;

public class TestRegister extends AbstractRealEmailSendRegister {
  final File            toSendDir;
  final File            sentDir;
  final RealEmailSender realSender;

  public TestRegister(RealEmailSender realSender, File toSendDir, File sentDir) {
    this.toSendDir  = toSendDir;
    this.sentDir    = sentDir;
    this.realSender = realSender;
  }

  @Override
  protected File toSendDir() {
    return toSendDir;
  }

  @Override
  protected File sentDir() {
    return sentDir;
  }

  @Override
  protected RealEmailSender emailSender() {
    return realSender;
  }

  @Override
  protected EmailSerializer emailSerializer() {
    return new EmailSerializerXml();
  }
}
