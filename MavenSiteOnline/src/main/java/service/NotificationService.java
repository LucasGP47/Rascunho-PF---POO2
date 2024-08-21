package service;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.account.Message;

import java.util.List;

public class NotificationService {
    public static final String ACCOUNT_SID = "ACfa76215834903571cb7b2440258ca942";
    public static final String AUTH_TOKEN = "52822791c40f652342e43d13df446e70";
    private final List<String> phoneNumbers;

    public NotificationService(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public void sendOfflineNotification(String url) {
        String messageContent = "AVISO: A URL: " + url + " está fora do ar.";
        for (String phoneNumber : phoneNumbers) {
            try {
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("whatsapp:" + phoneNumber),
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
                        messageContent
                ).create();
                System.out.println("Mensagem enviada: " + message.getSid());
            } catch (ApiException e) {
                System.err.println("Falha ao enviar a mensagem: " + e.getMessage());
            }
        }
    }
}
