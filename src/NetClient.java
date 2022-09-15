import java.awt.Color;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class NetClient extends JFrame implements KeyListener {

    /** ����� ������� */
    private final static String serverIP = "127.0.0.1";
    /** ���� ������� */
    private final static int serverPort = 1234;

    Socket socket;
    private boolean firstInput = true;
    private boolean input = false;

    private final JTextArea textArea;
    private InputStreamReader in;
    private PrintWriter out;

    NetClient() {
        // ������� ����
        super("Simple Chat client");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // ��������� �� ���� ��������� ����
        textArea = new JTextArea();
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setEditable(false);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane);

        connect();
    }

    void connect() {
        try {
            socket = new Socket(serverIP, serverPort);
            in = new InputStreamReader(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream());
            textArea.addKeyListener(this);
        } catch (IOException e) {
            textArea.setForeground(Color.RED);
            textArea.append("Server " + serverIP + " port " + serverPort + " " + "" + "NOT AVAILABLE");
            e.printStackTrace();
        }

        new Thread(() -> {
            // � ��������� ������
            // ��������� ������� �� �������
            while (true) {
                try {
                    int a = in.read();
                    addCharToTextArea(a, false);
                    //��������� ��� ������ �� �������� � �� ������� �� ���� ������.
                    if (a == -1) {
                        socket.close();
                        in.close();
                        out.close();
                        System.exit(0);
                    }
                } catch (IOException e) {
                    textArea.setForeground(Color.RED);
                    textArea.append("\nCONNECTION ERROR");
                    e.printStackTrace();
                    return;
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        new NetClient().setVisible(true);
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
//		 ���������� ������������ ������ � ���� � �� �����
        out.print(arg0.getKeyChar());
        out.flush();

        System.out.print((int) (arg0.getKeyChar()));
        addCharToTextArea(arg0.getKeyChar(), true);
    }

    /**
     * �������� � ���.
     * @param c ���������� ������.
     * @param flag ���������� ������ ���� ������.
     *             true - ���� ������ ������ � ����������.
     */
    void addCharToTextArea(int c, boolean flag) {

        if (flag || firstInput) {
            if (c == 10) {
                input = true;
                firstInput = false;
            } else if (input) {
                textArea.append("��: ");
                textArea.setCaretPosition(textArea.getDocument().getLength());
                input = false;
            }
        }
        textArea.append((char) c + "");
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }
}
