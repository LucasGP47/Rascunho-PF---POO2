package teste;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class Example {
  // Find your Account Sid and Token at twilio.com/console
  public static final String ACCOUNT_SID = "ACfa76215834903571cb7b2440258ca942";
  public static final String AUTH_TOKEN = "3329954859ff55e5b354e1827695089a";

  public static void main(String[] args) {
    Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    Message message = Message.creator(
      new com.twilio.type.PhoneNumber("whatsapp:+5512997897418"),
      new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
      "oi"

    ).create();

    System.out.println(message.getSid());
  }
}