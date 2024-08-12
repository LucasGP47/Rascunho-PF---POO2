package main;

import model.Site;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static boolean stopMonitoring = false;

    public static void main(String[] args) {
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        List<Site> sites = new ArrayList<>();

        System.out.println("Digite os links dos sites (digite 'sair' para terminar):");
        while (true) {
            String url = scanner.nextLine();
            if (url.equalsIgnoreCase("sair")) {
                break;
            }
            sites.add(new Site(url));
        }

        SwingUtilities.invokeLater(() -> new SiteMonitorWindow(sites));

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
