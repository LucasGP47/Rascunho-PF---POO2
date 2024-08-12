package main;

import model.Site;
import service.SiteChecker;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import javax.swing.*;
import java.awt.*;
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
    private final SiteChecker siteChecker;
    private final JLabel countdownLabel;
    private final JPanel sitesPanel;
    private final ExecutorService executor;
    private static final int CHECK_INTERVAL = 10000;

    private final Map<String, Boolean> notificationSentMap = new HashMap<>();

    // Credenciais do Sandbox (NAO ALTERAR)
    public static final String ACCOUNT_SID = "ACfa76215834903571cb7b2440258ca942";
    public static final String AUTH_TOKEN = "3329954859ff55e5b354e1827695089a";

    public SiteMonitorWindow(List<Site> sites) {
        this.sites = sites;
        this.siteChecker = new SiteChecker();
        this.countdownLabel = new JLabel("Próxima verificação em: " + CHECK_INTERVAL / 1000 + "s");
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

        executor = Executors.newSingleThreadExecutor();
        executor.submit(this::monitorSites);
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
                    sendWhatsAppMessage(site.getUrl());
                    notificationSentMap.put(site.getUrl(), true); 
                }

                if (isOnline && notificationSentMap.get(site.getUrl())) {
                    notificationSentMap.put(site.getUrl(), false);
                }

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText(isOnline ? "Online" : "Offline");
                    statusLabel.setForeground(isOnline ? Color.GREEN : Color.RED);

                    changeLabel.setText("Mudou: " + (hasChanged ? "Sim" : "Não"));
                    lastChangeLabel.setText("Última mudança: " + lastChangeTime);
                });
            }

            sitesPanel.revalidate();
            sitesPanel.repaint();

            for (int i = CHECK_INTERVAL / 1000; i >= 0; i--) {
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
    	Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
          new com.twilio.type.PhoneNumber("whatsapp:+5512991527253"),
          new com.twilio.type.PhoneNumber("whatsapp:+14155238886"),
          "AVISO: A URL" + url + "esta fora do ar!"

        ).create();

        System.out.println(message.getSid());
      }
    }

