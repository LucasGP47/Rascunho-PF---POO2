package main;

import assists.GetSiteURL;
import facade.SiteMonitoringFacade;
import assists.GetPhoneNumber;
import model.Site;

import java.util.List;

public class Main {
    private static boolean stopMonitoring = false;

    public static void main(String[] args) {
        List<String> phoneNumbers = GetPhoneNumber.collectPhoneNumbers();
        List<Site> sites = GetSiteURL.collectSites();

        SiteMonitoringFacade facade = new SiteMonitoringFacade(sites, phoneNumbers);

        // Controle de uso do GUI ou Console via boolean (true para GUI, false para console)
        boolean useGui = false;  
        facade.startMonitoring(useGui);
    }

    public static boolean shouldStopMonitoring() {
        return stopMonitoring;
    }

    public static void stopMonitoring() {
        stopMonitoring = true;
    }
}
