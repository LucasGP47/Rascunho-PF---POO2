package service;

import model.Site;
import javax.swing.*;
import java.awt.*;
import java.util.List;

@SuppressWarnings("serial")
public class SiteMonitorWindow extends JFrame {
    private final MonitoringService monitoringService;
    private final JLabel countdownLabel;
    private final JPanel sitesPanel;

    public SiteMonitorWindow(List<Site> sites, List<String> phoneNumbers) {
        setTitle("Monitoramento de Sites");
        setSize(800, 600);
        setLayout(new BorderLayout());
        countdownLabel = new JLabel();
        sitesPanel = new JPanel();
        sitesPanel.setLayout(new GridLayout(sites.size(), 1, 10, 10));
        add(countdownLabel, BorderLayout.NORTH);
        add(new JScrollPane(sitesPanel), BorderLayout.CENTER);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        monitoringService = new MonitoringService(sites, phoneNumbers, sitesPanel, countdownLabel, true);
        monitoringService.startMonitoring();
    }
}
