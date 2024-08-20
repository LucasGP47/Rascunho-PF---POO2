package service;

import model.Site;
import main.Main;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsoleMonitor {
    private final List<Site> sites;
    private final SiteChecker siteChecker;
    private final NotificationService notificationService;
    private final LogService logService;
    private final ExecutorService executor;
    private int checkInterval;
    
    private final Map<String, Boolean> notificationSentMap = new HashMap<>();

    public ConsoleMonitor(List<Site> sites, List<String> phoneNumbers) {
        this.sites = sites;
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
        System.out.println("Digite o intervalo de verificação (em segundos):");
        try {
            checkInterval = Integer.parseInt(System.console().readLine()) * 1000;
        } catch (NumberFormatException e) {
            checkInterval = 10000;  // Padrão = 10 segundos
        }
    }

    private void monitorSites() {
        while (!Main.shouldStopMonitoring()) {
            for (Site site : sites) {
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
                    site.setLastChangeTime(currentTime);  // Atualiza o tempo de mudança no Site
                }

                printSiteStatus(site, isOnline, hasChanged, site.getLastChangeTime());
            }

            countdownToNextCheck();
        }

        executor.shutdownNow();
    }

    private void printSiteStatus(Site site, boolean isOnline, boolean hasChanged, String lastChangeTime) {
        System.out.printf("URL: %s | Status: %s | Mudou?: %s | Última Mudança: %s%n",
                site.getUrl(),
                isOnline ? "Online" : "Offline",
                hasChanged ? "Sim" : "Não",
                lastChangeTime != null ? lastChangeTime : "N/A"
        );
    }

    private void countdownToNextCheck() {
        for (int i = checkInterval / 1000; i >= 0; i--) {
            System.out.println("Próxima verificação em: " + i + "s");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}
