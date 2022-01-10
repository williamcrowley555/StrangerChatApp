package com.stranger_chat_app.server.controller;

import com.stranger_chat_app.shared.constant.DataType;

import java.util.ArrayList;
import java.util.Set;

public class ClientManager {
    ArrayList<Client> clients;

    public ClientManager() {
        clients = new ArrayList<>();
    }

    public boolean add(Client client) {
        if (!clients.contains(client)) {
            clients.add(client);
            return true;
        }
        return true;
    }

    public boolean remove(Client c) {
        if (clients.contains(c)) {
            clients.remove(c);
            return true;
        }
        return false;
    }

    public void removeRejectedClient (String nickname) {
        for (Client client : clients) {
            if (client.getRejectedClients().contains(nickname)) {
                client.getRejectedClients().remove(nickname);
            }
        }
    }

    public Client find(String nickname) {
        for (Client client : clients) {
            if (client.getNickname() != null && client.getNickname().equals(nickname)) {
                return client;
            }
        }
        return null;
    }

    public void broadcast(DataType dataType, String content) {
        clients.forEach((client) -> {
            client.sendData(dataType, content);
        });
    }

    public Client findWaitingClient() {
        for (Client client : clients) {
            if (client.isWaiting()) {
                return client;
            }
        }

        return null;
    }

    public Client findWaitingClient(Client currentClient, Set<String> excludedNicknames) {
        for (Client client : clients) {
            if (client.isWaiting()) {
                if (excludedNicknames.contains(client.getNickname()))
                    continue;
                else if (client.getRejectedClients().contains(currentClient.getNickname()))
                    continue;
                else
                    return client;
            }
        }

        return null;
    }

    public int getSize() {
        return clients.size();
    }
}
