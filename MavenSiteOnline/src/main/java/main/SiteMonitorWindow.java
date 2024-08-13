package main;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import model.Site;
import service.SiteChecker;

import com.twilio.exception.ApiException;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("serial")
public class SiteMonitorWindow extends JFrame {
    private final List<Site> sites;
    private final List<String> phoneNumbers;
    private final SiteChecker siteChecker;
    private final JLabel countdownLabel;
    private final JPanel sitesPanel;
    private final ExecutorService executor;
    private int checkInterval;
    private final String messagesLogPath = "messages_log.txt";

    private final Map<String, Boolean> notificationSentMap = new HashMap<>();

    // Twilio credentials
    public static final String ACCOUNT_SID = "ACfa76215834903571cb7b2440258ca942";
    public static final String AUTH_TOKEN = "xxxxxxxxxxxxxxxxxxxxxxxx";

    public SiteMonitorWindow(List<Site> sites, List<String> phoneNumbers) {
        this.sites = sites;
        this.phoneNumbers = phoneNumbers;
        this.siteChecker = new SiteChecker();
        this.countdownLabel = new JLabel();
        this.sitesPanel = new JPanel();
        this.sitesPanel.setLayout(new GridLayout(sites.size(), 1, 10, 10));

        setTitle("Monitoramento de Sites");
        setSize(800, 600);
        setLayout(new BorderLayout());
        add(countdownLabel, BorderLayout.NORTH);
        add(new JScrollPane(sitesPanel), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        for (Site site : sites) {
            notificationSentMap.put(site.getUrl(), false);
        }

        setCheckInterval();
        executor = Executors.newSingleThreadExecutor();
        executor.submit(this::monitorSites);
    }

    private void setCheckInterval() {
        String intervalStr = JOptionPane.showInputDialog(this, "Digite o intervalo de verificação (em segundos):", "Configuração de Intervalo", JOptionPane.QUESTION_MESSAGE);
        try {
            checkInterval = Integer.parseInt(intervalStr) * 1000;
        } catch (NumberFormatException e) {
            checkInterval = 10000;  // Default to 10 seconds if input is invalid
        }
    }

    private void monitorSites() {
        while (!Main.shouldStopMonitoring()) {
            sitesPanel.removeAll();

            for (Site site : sites) {
                JPanel sitePanel = new JPanel(new GridLayout(1, 4));
                JLabel urlLabel = new JLabel(site.getUrl());
                JLabel statusLabel = new JLabel("Verificando...");
                JLabel changeLabel = new JLabel("Verificando...");
                JLabel lastChangeLabel = new JLabel("Última mudança: N/A");

                sitePanel.add(urlLabel);
                sitePanel.add(statusLabel);
                sitePanel.add(changeLabel);
                sitePanel.add(lastChangeLabel);
                sitesPanel.add(sitePanel);

                boolean isOnline = siteChecker.isSiteOnline(site);
                boolean hasChanged = isOnline && siteChecker.hasSiteChanged(site);
                String lastChangeTime = hasChanged ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) : "N/A";

                if (!isOnline && !notificationSentMap.get(site.getUrl())) {
                    try {
                        sendWhatsAppMessage(site.getUrl());
                    } catch (Exception e) {
                        logTwilioError(e.getMessage());
                    }
                    notificationSentMap.put(site.getUrl(), true);
                }

                if (isOnline && notificationSentMap.get(site.getUrl())) {
                    notificationSentMap.put(site.getUrl(), false);
                }

                logSiteStatus(site.getUrl(), isOnline, hasChanged, lastChangeTime);

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText(isOnline ? "Online" : "Offline");
                    statusLabel.setForeground(isOnline ? Color.GREEN : Color.RED);
                    changeLabel.setText("Mudou: " + (hasChanged ? "Sim" : "Não"));
                    lastChangeLabel.setText("Última mudança: " + lastChangeTime);
                });
            }

            sitesPanel.revalidate();
            sitesPanel.repaint();

            for (int i = checkInterval / 1000; i >= 0; i--) {
                int finalI = i;
                SwingUtilities.invokeLater(() -> countdownLabel.setText("Próxima verificação em: " + finalI + "s"));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }

        executor.shutdownNow();
    }

    private void sendWhatsAppMessage(String url) {
        String messageContent = "AVISO: A URL: " + url + " está fora do ar.";
        
        for (String phoneNumber : phoneNumbers) {
            try {
                Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("whatsapp:" + phoneNumber),  // Dynamic recipient
                        new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),    // Static sender (Twilio sandbox)
                        messageContent
                ).create();

                logTwilioMessage("Mensagem enviada: " + message.getSid());
            } catch (ApiException e) {
                logTwilioError("Falha ao enviar a mensagem: " + e.getMessage());
            }
        }
    }

    private void logSiteStatus(String url, boolean isOnline, boolean hasChanged, String lastChangeTime) {
        String statusLog = String.format("Site: %s | Status: %s | Mudou: %s | Última mudança: %s%n", 
                url, (isOnline ? "Online" : "Offline"), (hasChanged ? "Sim" : "Não"), lastChangeTime);
        writeToFile(messagesLogPath, statusLog, false);  // Overwrite log file
    }

    private void logTwilioMessage(String message) {
        writeToFile(messagesLogPath, message + System.lineSeparator(), false);
    }

    private void logTwilioError(String error) {
        writeToFile(messagesLogPath, "Erro Twilio: " + error + System.lineSeparator(), false);
    }

    private void writeToFile(String filePath, String content, boolean append) {
        try (FileWriter writer = new FileWriter(filePath, append)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
