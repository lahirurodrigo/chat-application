package lk.ijse.server;

import lk.ijse.controller.LoginFormController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import static lk.ijse.controller.LoginFormController.clientMap;

public class ClientHandler implements Runnable {

    DataOutputStream dataOutputStream;

    DataInputStream dataInputStream;

    Socket socket ;

    public ClientHandler(Socket socket) throws IOException {

        this.socket = socket;
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        dataInputStream = new DataInputStream(socket.getInputStream());
        String username = LoginFormController.user;
        clientMap.put(socket,username);

    }


    @Override
    public void run() {
        try {
            while(true){
                String message = dataInputStream.readUTF();
                Server.broadcastMessage(message,socket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                socket.close();
                System.out.println("Client disconnected: " + socket);
                Server.clientList.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendMessage(String message, Socket socket) {
        try {
            String sender = clientMap.get(socket);
            System.out.println("Sender socket: "+socket);
            System.out.println("Sender:"+sender);
            message  = sender+","+message;
            dataOutputStream.writeUTF(message.trim());
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
