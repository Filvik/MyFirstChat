import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * ����� ��� ������ � ������������ ������� � �������.
 */
class Client implements Runnable {
    private final Socket socket;
    private final long id;
    private PrintStream out;
    private InputStream is;
    private final Map<Long, Client> clients;

    public Client(Socket socket, Map<Long, Client> clients) {

        // id �������
        id = System.currentTimeMillis();

        this.clients = clients;
        this.socket = socket;
        // �������� ������ ����� � ������
        try {
            is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            out = new PrintStream(os);

            System.out.printf("������ %s �����������.%n", id);

            for (Client client : clients.values()) {
                if (client.getId() != id) {
                    client.getOut().println("������ � id" + id + " �����������");
                }
            }
        } catch (IOException e) {
            System.out.println("������ ��� �������� ������\n");
            e.printStackTrace();
        }
    }

    /**
     * ��������� ������ ����������� ������ �� ������� � ��������� ������.
     */
    public void run() {
        try {
            // ������� ������� �������� ����� � ������
            Scanner in = new Scanner(is);
            // ������ �� ���� � ����� � ����
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
                    client.getOut().println("������ � id" + id + " ����������");
                }
            }
            System.out.printf("������ %s ����������%n", id);
            socket.close();
            in.close();
            out.close();
        } catch (IOException e) {
            System.out.printf("������ ����������� ������� %s \n���������� ��������.%n", id);
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