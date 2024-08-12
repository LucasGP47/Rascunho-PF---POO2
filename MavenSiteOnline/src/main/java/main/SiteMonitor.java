package main;

import model.Site;
import service.SiteChecker;

public class SiteMonitor implements Runnable {
    private final Site site;
    private final SiteChecker siteChecker;

    public SiteMonitor(Site site) {
        this.site = site;
        this.siteChecker = new SiteChecker();
    }

    @Override
    public void run() {
        while (!Main.shouldStopMonitoring()) {
            boolean isOnline = siteChecker.isSiteOnline(site);
            System.out.println("O site " + site.getUrl() + " está online: " + isOnline);

            if (isOnline) {
                boolean hasChanged = siteChecker.hasSiteChanged(site);
                if (hasChanged) {
                    System.out.println("O conteúdo do site " + site.getUrl() + " mudou.");
                } else {
                    System.out.println("O conteúdo do site " + site.getUrl() + " não mudou.");
                }
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }

        System.out.println("Monitoramento do site " + site.getUrl() + " parado.");
    }
}
