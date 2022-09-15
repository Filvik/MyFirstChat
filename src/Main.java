import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        try {
            // создаем серверный сокет на порту 1234
            ServerSocket server = new ServerSocket(1234);

            Map<Long, Client> clients = new HashMap<>();

            while (true) {
                System.out.println("Waiting...");
                // ждем клиента из сети
                Socket socket = server.accept();
                System.out.println("Client connected!");
                // создаем клиента на своей стороне
                Client client = new Client(socket, clients);
                clients.put(client.getId(), client);
                printCurrentClients(clients);
                // запускаем поток
                Thread thread = new Thread(client);
                thread.start();
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Ошибка создания сервера");
        }
    }

    /**
     * Выводит количество клиентов и их id.
     * @param clients Коллекция клиентов подключенных к серверу.
     */
    private static void printCurrentClients(Map<Long, Client> clients) {
        System.out.printf("Сейчас клиентов %s%n", clients.size());
        for (Client client : clients.values()) {
            System.out.printf("id клиента: %s%n", client.getId());
        }
    }
}
