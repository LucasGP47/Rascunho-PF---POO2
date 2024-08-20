import facade.SiteMonitoringFacade;
import model.Site;
import java.util.ArrayList;
import java.util.List;

public class testing {

    public static void main(String[] args) {
        List<Site> sites = new ArrayList<>();
        sites.add(new Site("http://localhost/index.html")); 

        List<String> phoneNumbers = new ArrayList<>();
        phoneNumbers.add("+5512991527253");

        SiteMonitoringFacade facade = new SiteMonitoringFacade(sites, phoneNumbers);

        System.out.println("Iniciando monitoramento com GUI...");
        facade.startMonitoring(true);

        try {
            Thread.sleep(10000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Iniciando monitoramento no console...");
        facade.startMonitoring(false);

        try {
            Thread.sleep(20000); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Teste concluído.");
    }
}