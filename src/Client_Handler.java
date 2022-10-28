import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Client_Handler implements Runnable {

    private String client_name;
    private DataInputStream in;
    private DataOutputStream out;
    Socket client;
    private ArrayList<Client_Handler> clientlist = new ArrayList<>();
    private HashMap<String, String> registered_users = new HashMap<>();

    //Hier wird für jeden Client ein Handler erstellt welcher in einem Thread ausgeführt werden.
    public Client_Handler(Socket client, String client_name, ArrayList<Client_Handler> clientlist, HashMap<String, String> registered_users) throws IOException {
        this.client = client;
        this.client_name = client_name;
        this.in = new DataInputStream(client.getInputStream());
        this.out = new DataOutputStream((client.getOutputStream()));
        this.clientlist = clientlist;
        this.registered_users = registered_users;
    }


    //Die Run methode ist hier entscheidend für den Thread. Hier wird der Code ausgeführt für die einzelnen Clients
    @Override
    public void run() {

        try {
            boolean run = true;
            //Es wird auf anfragen gewartet.
            while (run) {
                //Login oder Register erfordert
                if (client_name.equals("")) {
                    //Falls der Login oder das Registrieren 3 mal Fehlschlägt geht es in die IF-Bedingung
                    if (!loginOrRegister()) {
                        out.writeUTF("[Server] 405 : Verbindung wird getrennt. Login/Register zu oft Fehlgeschlagen");
                        run = false;
                        clientlist.remove(this);
                        System.out.println("[Server] der Cient " + client.getLocalAddress() + " wurde getrennt. Zu viele Fehlgeschlagene Logins/Register versuche");
                        client.close();
                    }
                } else {

                    String request = in.readUTF();

                    //hier werden die Anfragen bearbeitet.
                    String[] splittedInput = request.split(":");
                    String command = "FEHLEINGABE";
                    if (splittedInput.length != 0) {
                        command = splittedInput[0];
                    }

                    switch (command) {
                        case "SAY":
                            if (splittedInput.length != 3) {
                                out.writeUTF("[Server] 404 : Der Befehl enthält zu wenig Argumente");
                            } else {
                                String msg = splittedInput[2];
                                String empfaenger = splittedInput[1];
                                Client_Handler emp = null;
                                for (Client_Handler temp : clientlist) {
                                    if (temp.client_name.equals(empfaenger)) {
                                        emp = temp;
                                    }
                                }

                                if (emp == null) {
                                    out.writeUTF("[Server] 402 :Der Empfänger existiert nicht oder ist offline");
                                } else {
                                    System.out.println("[Server] Nachricht von " + this.client_name + " an " + emp.client_name);
                                    emp.out.writeUTF("[" + this.client_name + "] : " + msg);
                                    out.writeUTF("[Server] 200 :Die Nachricht wurde versendet");
                                }
                            }
                            break;
                        case "LOGOUT":
                            out.writeUTF("[Server] 200 : Logout erfolgreich");
                            run = false;
                            clientlist.remove(this);
                            System.out.println("[Server] der Cient " + client.getLocalAddress() + " hat sich abgemeldet");
                            client.close();
                            break;
                        case "DELETE":
                            String username;
                            String pw;
                            if (splittedInput.length == 3) {
                                username = splittedInput[1];
                                pw = splittedInput[2];
                                if (registered_users.get(username) == null) {
                                    out.writeUTF("[Server] 402: Der Benutzername existiert nicht.");
                                } else if (registered_users.get(username).equals(pw)) {
                                    //Man könnte schauen ob der Benutzername mit dem man angemeldet ist der gleiche ist wie auch der zu löschende Benutzer
                                    registered_users.remove(username);
                                    out.writeUTF("[Server] 200 : Löschen erfolgreich");
                                    System.out.println("[Server] der User " + username + " wurde gelöscht");
                                } else {
                                    out.writeUTF("[Server] 401 : Falsches Passwort");
                                }
                            } else {
                                out.writeUTF("[Server] 404 : Argument missing");
                            }
                            break;
                        default:
                            out.writeUTF("[Server] 400 : Commando nicht bekannt.");
                    }
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
            System.out.println("[Server] Verbindung zum Cient " + client.getLocalAddress() + " wurde unterbrochen");
            clientlist.remove(this);
            try {
                client.close();
                out.close();
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //Finally wird für die Aufräumarbeiten benutzt
        } finally {
            // Hier werden einfach die Streams geschlossen und wieder die möglichen Exceptions abgefangen.
            System.out.println("[Server] Verbindung zum Cient " + client.getLocalAddress() + " wurde unterbrochen");
            clientlist.remove(this);
            try {
                client.close();
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Login oder Register wird outgesourced in eine eigene Methode. Damit die Run-Methode nicht zu lang wird.
    private boolean loginOrRegister() throws IOException {
        out.writeUTF("[Server] Bitte loggen sie sich ein oder registrieren Sie sich");
        out.writeUTF("[Server] Nutzen Sie Bitte \"LOGIN:Benutzername:Pw\" oder \"REGISTER:Benutzername:Pw\"");
        int attempts = 0;


        while (attempts < 3) {
            String request = in.readUTF();
            String[] splittedInput = request.split(":");
            String command = "FEHLEINGABE";

            if (splittedInput.length != 0) {
                command = splittedInput[0];
            }


            switch (command) {
                case "LOGIN":
                    String username;
                    String pw;
                    if (splittedInput.length == 3) {
                        username = splittedInput[1];
                        pw = splittedInput[2];
                        if (registered_users.get(username) == null) {
                            out.writeUTF("[Server] 402: Der Benutzername existiert nicht.");
                        } else if (registered_users.get(username).equals(pw)) {
                            out.writeUTF("[Server] 200 : Anmelden erfolgreich");
                            this.client_name = username;
                            return true;
                        } else {
                            out.writeUTF("[Server] 401 : Falsches Passwort");
                        }
                    } else {
                        out.writeUTF("[Server] 404 : Argument missing");
                    }
                    out.writeUTF("[Server] Loginversuch " + attempts + 1 + " gescheitert");
                    System.out.println("[Server] Anmeldeversuch Nr:" + attempts + 1 + " von " + client.getLocalAddress() + " gescheitert");
                    break;
                case "REGISTER":
                    if (splittedInput.length == 3) {
                        username = splittedInput[1];
                        pw = splittedInput[2];
                        if (registered_users.get(username) != null) {
                            out.writeUTF("[Server] 403 : Der Benutzername ist bereits vergeben");
                        } else {
                            out.writeUTF("[Server] 200: Benutzer Erfolgreich registriert");
                            registered_users.put(username, pw);
                            this.client_name = username;
                            return true;
                        }
                    } else {
                        out.writeUTF("[Server] 404 : Argument missing");
                    }
                    out.writeUTF("[Server] Registerversuch  " + attempts + 1 + " gescheitert");
                    System.out.println("[Server] Registerversuch Nr:" + attempts + 1 + " von " + client.getLocalAddress() + " gescheitert");
                    break;
                default:
                    out.writeUTF("[Server] 400 : Commando nicht bekannt. Versuch nr " + attempts);
                    break;
            }
            attempts++;
        }
        return false;
    }
}

