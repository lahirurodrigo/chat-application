package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lk.ijse.server.Server;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class LoginFormController implements Initializable {

    @FXML
    private JFXButton btnLogin;

    @FXML
    private TextField txtUserName;

    static String[] users = new String[]{"1","2","3"};

    Server server;

    public static Map<Socket, String> clientMap = new ConcurrentHashMap<>();

    public static String user;

    @FXML
    void btnLoginOnAction(ActionEvent event) throws IOException {

        String username = txtUserName.getText();

        for (String name : users){
            if (username.equals(name)){
                Socket socket = server.getSocket();
                user = name;

                System.out.println(name);

                //clientMap.put(socket,name);

                Parent rootNode = FXMLLoader.load(this.getClass().getResource("/view/client_form.fxml"));
                Scene scene = new Scene(rootNode);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.centerOnScreen();
                stage.show();



            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        server = new Server();
        new Thread(server).start();
    }
}
