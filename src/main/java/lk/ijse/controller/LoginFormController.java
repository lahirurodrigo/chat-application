package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lk.ijse.server.Server;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class LoginFormController implements Initializable {

    @FXML
    private JFXButton btnLogin;

    @FXML
    private JFXTextField txtUserName;

    String[] users = new String[]{"Pasindu","Dilan","Pathum"};

    private int count = 0;
    private List<String> names = new ArrayList<>();
    Server server;
    public static Map<Socket, String> clientMap = new ConcurrentHashMap<>();
    public static String user;

    @FXML
    void btnLoginOnAction(ActionEvent event) throws IOException {
        String userName = txtUserName.getText();
        count = 0;
        for (String name : users) {
            if (!name.equals(userName)){
                count++;
                if (count==users.length){
                    new Alert(Alert.AlertType.ERROR, "Invalid user name").show();
                }
            }else{
                if (! userName.isEmpty()) {

                    for (int i = 0; i < names.size(); i++) {
                        if (userName.equals(names.get(i))){
                            new Alert(Alert.AlertType.ERROR, "Username already logged!!!").show();
                            return;
                        }
                    }
                    names.add(userName);


                    Socket socket = server.getSocket();
                    user = userName;
                    Parent rootNode = FXMLLoader.load(this.getClass().getResource("/view/client_form.fxml"));
                    Scene scene = new Scene(rootNode);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.centerOnScreen();
                    stage.setTitle(userName);
                    stage.show();

                    txtUserName.clear();

                } else {
                    new Alert(Alert.AlertType.ERROR,"Please Enter username!!").showAndWait();
                }
            }

        }

    }

    @FXML
    void txtUserNameOnAction(ActionEvent event) throws IOException {
        btnLoginOnAction(event);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> txtUserName.requestFocus());

        server = new Server();
        new Thread(server).start();
    }
}
