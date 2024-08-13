package main;

import model.Site;
import service.SiteMonitorWindow;
import assists.GetSiteURL;
import assists.GetPhoneNumber;

import javax.swing.*;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static boolean stopMonitoring = false;

    public static void main(String[] args) {

        List<String> phoneNumbers = GetPhoneNumber.collectPhoneNumbers();

        List<Site> sites = GetSiteURL.collectSites();

        SwingUtilities.invokeLater(() -> new SiteMonitorWindow(sites, phoneNumbers));

        stopMonitoringThread();
    }

    private static void stopMonitoringThread() {
        Thread stopThread = new Thread(() -> {
            System.out.println("Digite 'sair' para parar o monitoramento.");
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    String userInput = scanner.nextLine();
                    if (userInput.equalsIgnoreCase("sair")) {
                        stopMonitoring = true;
                        System.out.println("Monitoramento encerrado!");
                        break;
                    }
                }
            }
        });
        stopThread.start();
    }

    public static boolean shouldStopMonitoring() {
        return stopMonitoring;
    }
}
