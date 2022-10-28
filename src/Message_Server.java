import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Message_Server {

    //Hashmap besteht aus Benutzername als Key und der Value ist der Hashwert des PW. Damit können sich Benutzer Registrieren. Bzw Anmelden.
    private static HashMap<String, String> registered_users = new HashMap<>();
    //Der Server Port
    private static final int PORT = 50000;
    private static final int MAXTHREADS = 500;
    // Die Liste mit den Clients
    private static ArrayList<Client_Handler> clientlist = new ArrayList<>();
    // Ein Threadpool mit max 500 Threads / Verbindungen gleichzeitig.
    private static ExecutorService threadpool = Executors.newFixedThreadPool(MAXTHREADS);


    public static void main(String[] args) throws IOException {
        // Der Server
        ServerSocket serverSocket = new ServerSocket(PORT);

        // Der Clientsocket.
        Socket client;

        //Hier wird auf die Verbindungen gewartet.
        System.out.println("[Server] is waiting for connections....");
        while (true) {
            client = serverSocket.accept();
            System.out.println("[Server] connection from " + client.getLocalAddress());

            //Der login wird im Handler gemacht.
            String name = "";

            Client_Handler client_Thread = new Client_Handler(client, name, clientlist, registered_users);
            clientlist.add(client_Thread);
            threadpool.execute(client_Thread);

            //Hier wird überprüft ob ein Client eine Verbindung unplanmäsig beendet hat. Also kein Logout benutzt hat
            //Könnte als Thread ausgelegt werden welcher in einem Zeitintervall ausgeführt wird.
            clientlist.removeIf(temp -> temp.client.isClosed());
        }

    }


    //Wurde alles in den Handler verlegt
//    private static String clientLoginProcess(Socket client) throws IOException {
//
//        DataInputStream in = new DataInputStream(client.getInputStream());
//        DataOutputStream out = new DataOutputStream((client.getOutputStream()));
//
//        //Am Ende ersetzen mit HashValues des Passwort
//        registered_users.put("testUser", "test");
//        String[] splittedInput;
//        out.writeUTF("[Server] Warte auf ihre Aktion");
//
//        int attempts = 0;
//
//        String clientAnswer = in.readUTF();
//        splittedInput = clientAnswer.split(":");
//        String command = "FEHLEINGABE";
//        if (splittedInput.length != 0) {
//            command = splittedInput[0];
//        }
//
//
//        //Es sollen noch mehr Server Logs gemacht werden
//        while (attempts < 3) {
//
//            switch (command) {
//                case "LOGIN":
//                    String username;
//                    String pw;
//                    if (splittedInput.length == 3) {
//                        username = splittedInput[1];
//                        pw = splittedInput[2];
//                        if (registered_users.get(username) == null) {
//                            out.writeUTF("[Server] Nutzername nicht vergeben");
//                        } else if (registered_users.get(username).equals(pw)) {
//                            out.writeUTF("[Server] Anmelden erfolgreich");
//                            return username;
//                        } else {
//                            out.writeUTF("[Server] Falsches Passwort");
//                        }
//                    } else {
//                        out.writeUTF("[Server] Es Fehlen Werte zu Login");
//                    }
//                    out.writeUTF("[Server] Loginversuch " + attempts + " gescheitert");
//                    break;
//                case "REGISTER":
//                    if (splittedInput.length == 3) {
//                        username = splittedInput[1];
//                        pw = splittedInput[2];
//                        if (registered_users.get(username) != null) {
//                            out.writeUTF("[Server] Nutzername bereits vergeben");
//                        } else {
//                            out.writeUTF("[Server] 200: Benutzer Erfolgreich registriert");
//                            registered_users.put(username, pw);
//                            return username;
//                        }
//                    } else {
//                        out.writeUTF("[Server] Es Fehlen Werte zum Registrieren \n" +
//                                "Schema \"REGISTER:Username:Pw\"");
//                    }
//                    out.writeUTF("[Server] Register " + attempts + " gescheitert");
//                    break;
//                default:
//                    out.writeUTF("[Server] BEFEHL nicht erkannt. Versuch nr " + attempts);
//                    break;
//            }
//            attempts++;
//        }
//        out.writeUTF("[Server] Verbindung wird zurückgesetzt");
//
//        System.out.println("[Server] Verbindung zu " + client.getLocalAddress() + " geschlossen");
//        client.close();
//        return "aborted";
//    }

//    public static void removeClient(Client_Handler cl) {
//        clientlist.remove(cl);
//    }

}
