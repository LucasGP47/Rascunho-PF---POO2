package facade;

import model.Site;
import service.ConsoleMonitor;
import service.SiteMonitorWindow;

import java.util.List;

public class MonitoringFacade {
    private final List<Site> sites;
    private final List<String> phoneNumbers;

    public MonitoringFacade(List<Site> sites, List<String> phoneNumbers) {
        this.sites = sites;
        this.phoneNumbers = phoneNumbers;
    }

    public void startGuiMonitoring() {
        javax.swing.SwingUtilities.invokeLater(() -> new SiteMonitorWindow(sites, phoneNumbers));
    }

    public void startConsoleMonitoring() {
        ConsoleMonitor consoleMonitor = new ConsoleMonitor(sites, phoneNumbers);
        consoleMonitor.start();
    }
}
