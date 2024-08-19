package facade;

import assists.GetPhoneNumber;
import assists.GetSiteURL;
import model.Site;
import service.SiteMonitorWindow;

import javax.swing.*;
import java.util.List;

public class SiteMonitoringFacade {
    private List<String> phoneNumbers;
    private List<Site> sites;

    public void setupMonitoring() {
        collectPhoneNumbers();
        collectSites();
        startMonitoring();
    }

    private void collectPhoneNumbers() {
        phoneNumbers = GetPhoneNumber.collectPhoneNumbers();
    }

    private void collectSites() {
        sites = GetSiteURL.collectSites();
    }

    private void startMonitoring() {
        SwingUtilities.invokeLater(() -> new SiteMonitorWindow(sites, phoneNumbers));
    }
}
