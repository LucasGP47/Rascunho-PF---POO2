package service;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogService {
    private final String logFilePath;

    public LogService(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public void logTwilioMessage(String message) {
        writeToFile("Twilio Log: " + message + System.lineSeparator());
    }

    public void logTwilioError(String error) {
        writeToFile("Erro Twilio: " + error + System.lineSeparator());
    }

    private void writeToFile(String content) {
        try (FileWriter writer = new FileWriter(logFilePath, false)) { 
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String formatCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
