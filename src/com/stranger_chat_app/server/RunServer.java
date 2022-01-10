package com.stranger_chat_app.server;

import com.stranger_chat_app.server.controller.Client;
import com.stranger_chat_app.server.controller.ClientManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RunServer {
    public static volatile ClientManager clientManager;

    public static boolean isShutDown = false;
    public static ServerSocket server;

    public RunServer() {
    }

    private void run() {
        try {
            int port = 5004;

            server = new ServerSocket(port);
            System.out.println("Server is running at port " + port + ".");

            // init client manager
            clientManager = new ClientManager();

            // create threadpool
            ThreadPoolExecutor executor = new ThreadPoolExecutor(
                    10, // corePoolSize
                    100, // maximumPoolSize
                    10, // thread timeout
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(8) // queueCapacity
            );

            // server main loop - listen to client's connection
            while (!isShutDown) {
                try {
                    System.out.println("Waiting for a client ...");

                    // socket object to receive incoming client requests
                    Socket socket = server.accept();

                    // create new client runnable object
                    Client client = new Client(socket);
                    clientManager.add(client);

                    // execute client runnable
                    executor.execute(client);

                } catch (IOException ex) {
                    isShutDown = true;
                }
            }

            System.out.println("Shuting down executor...");
            executor.shutdownNow();

        } catch (IOException ex) {
            Logger.getLogger(RunServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        new RunServer().run();
    }
}
