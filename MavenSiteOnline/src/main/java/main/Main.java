package main;

import model.Site;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static boolean stopMonitoring = false;
    private static List<String> phoneNumbers = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Site> sites = new ArrayList<>();

        // Collect phone numbers
        System.out.println("Digite os números de telefone para envio de mensagens (formato: whatsapp:+[código do país][número], digite 'sair' para terminar):");
        while (true) {
            String phoneNumber = scanner.nextLine();
            if (phoneNumber.equalsIgnoreCase("sair")) {
                break;
            }
            phoneNumbers.add(phoneNumber);
        }

        // Collect site URLs
        System.out.println("Digite os links dos sites (digite 'sair' para terminar):");
        while (true) {
            String url = scanner.nextLine();
            if (url.equalsIgnoreCase("sair")) {
                break;
            }
            sites.add(new Site(url));
        }

        // Start monitoring
        SwingUtilities.invokeLater(() -> new SiteMonitorWindow(sites, phoneNumbers));

        Thread stopThread = new Thread(() -> {
            System.out.println("Digite 'sair' para parar o monitoramento.");
            while (true) {
                String userInput = scanner.nextLine();
                if (userInput.equalsIgnoreCase("sair")) {
                    stopMonitoring = true;
                    System.out.println("Monitoramento encerrado!");
                    break;
                }
            }
        });

        stopThread.start();
    }

    public static boolean shouldStopMonitoring() {
        return stopMonitoring;
    }
}
