package main;

import facade.SiteMonitoringFacade;

public class Main {
	
	private static boolean stopMonitoring = false;
	
    public static void main(String[] args) {
        SiteMonitoringFacade facade = new SiteMonitoringFacade();
        facade.setupMonitoring();
    }
    
    public static boolean shouldStopMonitoring() { 
        return stopMonitoring;
    }
}
