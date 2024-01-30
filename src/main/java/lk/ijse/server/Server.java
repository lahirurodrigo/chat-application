package lk.ijse.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable{

    Socket socket ;

    static List<ClientHandler> clientList = new ArrayList<>();

    public Server(){

    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(6000);
            System.out.println("server started!!!");

            while(true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                clientList.add(clientHandler);
                new Thread(clientHandler).start();
                //clientHandler(socket);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void broadcastMessage(String message, Socket socket) {
        for (ClientHandler client : clientList) {
            if (client.socket!=socket){
                System.out.println("This is sender: "+socket);
                client.sendMessage(message,socket);
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
