import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Message_Client {


    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 50000;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERVER_IP,SERVER_PORT);
        DataInputStream in = new DataInputStream(socket.getInputStream());
        DataOutputStream out = new DataOutputStream((socket.getOutputStream()));

        Scanner scn = new Scanner(System.in);

        String serverResponse = in.readUTF();
        System.out.println(serverResponse);
        String command;

        //Damit die NAchrichten und die Antworten unabhängig voneinander sind und sich nicht blockieren werden 2 Threads erstellt. Einer fürs Schreiben der Befehle und einer fürs Empfangen.

        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {
                    // Tastatureingabe des Befehls
                    String command = scn.nextLine();

                    try {
                        // An des Server schicken
                        out.writeUTF(command);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = in.readUTF();
                        System.out.println(msg);
                    } catch (IOException e) {
                        //e.printStackTrace();
                        continue;
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();
    }



}