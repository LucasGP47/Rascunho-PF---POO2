package assists;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GetPhoneNumber {
    public static List<String> collectPhoneNumbers() {
        List<String> phoneNumbers = new ArrayList<>();
        @SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

        System.out.println("Digite os números de telefone para envio de mensagens (formato: whatsapp:+[código do país][número], digite 'sair' para terminar):");
        while (true) {
            String phoneNumber = scanner.nextLine();
            if (phoneNumber.equalsIgnoreCase("sair")) {
                break;
            }
            phoneNumbers.add(phoneNumber);
        }
        return phoneNumbers;
    }
}
