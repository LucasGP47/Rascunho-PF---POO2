package service;

import model.Site;
import java.util.List;

public class ConsoleMonitor {
    private final List<Site> sites;
    private final List<String> phoneNumbers;

    public ConsoleMonitor(List<Site> sites, List<String> phoneNumbers) {
        this.sites = sites;
        this.phoneNumbers = phoneNumbers;
    }

    public void start() {
        MonitoringService monitoringService = new MonitoringService(sites, phoneNumbers, null, null);
        monitoringService.startMonitoring();
    }
}