package facade;

import model.Site;
import service.MonitoringService;
import service.SiteMonitorWindow;

import javax.swing.*;
import java.util.List;

public class SiteMonitoringFacade {
    private final List<Site> sites;
    private final List<String> phoneNumbers;
    private MonitoringService monitoringService;

    public SiteMonitoringFacade(List<Site> sites, List<String> phoneNumbers) {
        this.sites = sites;
        this.phoneNumbers = phoneNumbers;
    }

    public void startMonitoringWithGui() {
        SwingUtilities.invokeLater(() -> {
            new SiteMonitorWindow(sites, phoneNumbers);
        });
    }

    public void startMonitoringWithConsole() {
        monitoringService = new MonitoringService(sites, phoneNumbers, null, null, false); 
        monitoringService.startMonitoring();
    }
    
    public void startMonitoring(boolean useGui) {
        if (useGui) {
            startMonitoringWithGui();
        } else {
            startMonitoringWithConsole();
        }
    }
}
