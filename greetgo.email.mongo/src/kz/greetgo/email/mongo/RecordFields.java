package kz.greetgo.email.mongo;

public interface RecordFields {

  String INSERTED_AT      = "insertedAt";
  String SENT_OK          = "sentOk";
  String SEND_STARTED_AT  = "sendStartedAt";
  String SEND_FINISHED_AT = "sendFinishedAt";
  String OPERATION_ID     = "operationId";
  String CONTENT          = "content";

  String SEND_ERR              = "sendErr";
  String SEND_ERR_AT           = "sendErrAt";
  String SEND_ERR_CLASS        = "sendErrClass";
  String SEND_ERR_MESSAGE      = "sendErrMessage";
  String SEND_ERR_STACK_STRACE = "sendErrStackTrace";

}
