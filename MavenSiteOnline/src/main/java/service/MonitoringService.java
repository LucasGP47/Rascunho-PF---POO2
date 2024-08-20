package service;

import model.Site;
import main.Main;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MonitoringService {
    private final List<Site> sites;
    private final JPanel sitesPanel;
    private final JLabel countdownLabel;
    private final SiteChecker siteChecker;
    private final NotificationService notificationService;
    private final LogService logService;
    private final ExecutorService executor;
    private int checkInterval;
    
    private final Map<String, Boolean> notificationSentMap = new HashMap<>();
    private final boolean useGui;

    public MonitoringService(List<Site> sites, List<String> phoneNumbers, JPanel sitesPanel, JLabel countdownLabel, boolean useGui) {
        this.sites = sites;
        this.sitesPanel = sitesPanel;
        this.countdownLabel = countdownLabel;
        this.useGui = useGui;
        this.siteChecker = new SiteChecker();
        this.notificationService = new NotificationService(phoneNumbers);
        this.logService = new LogService("messages_log.txt");
        setCheckInterval();
        executor = Executors.newSingleThreadExecutor();

        for (Site site : sites) {
            notificationSentMap.put(site.getUrl(), false);
        }
    }

    public void startMonitoring() {
        executor.submit(this::monitorSites);
    }

    private void setCheckInterval() {
        if (useGui) {
            String intervalStr = JOptionPane.showInputDialog(null, "Digite o intervalo de verificação (em segundos):", "Configuração de Intervalo", JOptionPane.QUESTION_MESSAGE);
            try {
                checkInterval = Integer.parseInt(intervalStr) * 1000;
            } catch (NumberFormatException e) {
                checkInterval = 10000;  
            }
        } else {
        	System.out.print("Digite o intervalo de verificação (em segundos): ");
        	Scanner scanner = new Scanner(System.in);
            checkInterval = scanner.nextInt() * 1000;
            scanner.close();
        }
    }

    private void monitorSites() {
        while (!Main.shouldStopMonitoring()) {
            if (sitesPanel != null) {
                sitesPanel.removeAll();
            }

            for (Site site : sites) {
                JPanel sitePanel = null;
                if (sitesPanel != null) {
                    sitePanel = createSitePanel(site);
                    sitesPanel.add(sitePanel);
                }

                boolean isOnline = siteChecker.isSiteOnline(site);
                boolean hasChanged = isOnline && siteChecker.hasSiteChanged(site);
                
                if (!isOnline && !notificationSentMap.get(site.getUrl())) {
                    notificationService.sendOfflineNotification(site.getUrl());
                    notificationSentMap.put(site.getUrl(), true);
                }

                if (isOnline && notificationSentMap.get(site.getUrl())) {
                    notificationSentMap.put(site.getUrl(), false);
                }

                if (hasChanged) {
                    String currentTime = logService.formatCurrentDateTime();
                    site.setLastChangeTime(currentTime);
                }

                if (sitesPanel != null && sitePanel != null) {
                    updateSitePanel(sitePanel, isOnline, hasChanged, site.getLastChangeTime());
                }

                if (!useGui) {
                    System.out.println("URL: " + site.getUrl() + " | " + (isOnline ? "Online" : "Offline") + " | Mudou?: " + (hasChanged ? "Sim" : "Não") + " | Última Mudança: " + site.getLastChangeTime());
                }
            }

            if (sitesPanel != null) {
                sitesPanel.revalidate();
                sitesPanel.repaint();
            }

            countdownToNextCheck();
        }

        executor.shutdownNow();
    }

    private void countdownToNextCheck() {
        for (int i = checkInterval / 1000; i >= 0; i--) {
            if (countdownLabel != null) {
                int finalI = i;
                SwingUtilities.invokeLater(() -> countdownLabel.setText("Próxima verificação em: " + finalI + "s"));
            } else if (!useGui) {
                System.out.println("Próxima verificação em: " + i + "s");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    private JPanel createSitePanel(Site site) {
        JPanel sitePanel = new JPanel(new GridLayout(1, 4));
        sitePanel.add(new JLabel(site.getUrl()));
        sitePanel.add(new JLabel("Verificando..."));
        sitePanel.add(new JLabel("Verificando..."));
        sitePanel.add(new JLabel("Última mudança: N/A"));
        return sitePanel;
    }

    private void updateSitePanel(JPanel sitePanel, boolean isOnline, boolean hasChanged, String lastChangeTime) {
        JLabel statusLabel = (JLabel) sitePanel.getComponent(1);
        JLabel changeLabel = (JLabel) sitePanel.getComponent(2);
        JLabel lastChangeLabel = (JLabel) sitePanel.getComponent(3);

        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(isOnline ? "Online" : "Offline");
            statusLabel.setForeground(isOnline ? Color.GREEN : Color.RED);
            changeLabel.setText("Mudou: " + (hasChanged ? "Sim" : "Não"));
            lastChangeLabel.setText("Última mudança: " + lastChangeTime);
        });
    }
}
