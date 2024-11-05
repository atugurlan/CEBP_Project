package entities;

import java.util.ArrayList;

public class ClientManager {
    private static ArrayList<Client> clients = new ArrayList<Client>();

    public static void addClient(Client client) {
        clients.add(client);
    }

    public static Client findClientByID(int id) {
        for(Client client : clients) {
            if(client.getId() == id) {
                return client;
            }
        }

        return null;
    }

    public static void print() {
        for(Client client : clients) {
            System.out.println(client + "\n");
        }
    }
}
