package assists;

import model.Site;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GetSiteURL {
    public static List<Site> collectSites() {
        List<Site> sites = new ArrayList<>();
        @SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

        System.out.println("Digite os links dos sites (digite 'sair' para terminar):");
        while (true) {
            String url = scanner.nextLine();
            if (url.equalsIgnoreCase("sair")) {
                break;
            }
            sites.add(new Site(url));
        }
        return sites;
    }
}
