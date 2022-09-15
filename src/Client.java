import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс для работы с подключением клиента к серверу.
 */
class Client implements Runnable {
    private final Socket socket;
    private final long id;
    private PrintStream out;
    private InputStream is;
    private final Map<Long, Client> clients;

    public Client(Socket socket, Map<Long, Client> clients) {

        // id клиента
        id = System.currentTimeMillis();

        this.clients = clients;
        this.socket = socket;
        // получаем потоки ввода и вывода
        try {
            is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            out = new PrintStream(os);

            System.out.printf("Клиент %s подключился.%n", id);

            for (Client client : clients.values()) {
                if (client.getId() != id) {
                    client.getOut().println("Клиент с id" + id + " подключился");
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании потока\n");
            e.printStackTrace();
        }
    }

    /**
     * Запускает работу клиентского сокета на сервере в отдельном потоке.
     */
    public void run() {
        try {
            // создаем удобные средства ввода и вывода
            Scanner in = new Scanner(is);
            // читаем из сети и пишем в сеть
            out.println("Welcome to chat!\n");
            try {
                String input = in.nextLine();
                while (!input.equals("bye")) {
                    for (Client client : clients.values()) {
                        if (client.getId() != id) {
                            client.getOut().print("id " + id + ": ");
                            client.getOut().println(input);
                        }
                    }
                    input = in.nextLine();
                }
            } catch (NoSuchElementException ignored) {
            }

            for (Client client : clients.values()) {
                if (client.getId() != id) {
                    client.getOut().println("Клиент с id" + id + " отключился");
                }
            }
            System.out.printf("Клиент %s отключился%n", id);
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            System.out.printf("Ошибка подключения клиента %s \nСоединение прервано.%n", id);
        }
        clients.remove(id);
    }

    public long getId() {
        return id;
    }

    public PrintStream getOut() {
        return out;
    }
}