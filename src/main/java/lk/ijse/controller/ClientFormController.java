package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientFormController implements Initializable {

    @FXML
    private JFXButton btnSend;

    @FXML
    private TextField txtMessage;

    @FXML
    private VBox vBoxArea;

    @FXML
    private ScrollPane scrollPane;

    Socket socket;

    DataInputStream dataInputStream;

    DataOutputStream dataOutputStream;

    String message = "";
    @FXML
    void btnSendOnAction(ActionEvent event) throws IOException {

        message = txtMessage.getText();
        dataOutputStream.writeUTF(txtMessage.getText().trim());
        dataOutputStream.flush();
        txtMessage.clear();
        Label label = new Label(message);
        vBoxArea.getChildren().add(label);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        new Thread(()->{
            try {
                Socket socket= new Socket("localhost", 6000);
                System.out.println(socket);
                String username = LoginFormController.user;
                System.out.println(username+"    "+socket);
                //LoginFormController.clientMap.put(socket,username);

                while(true){
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    dataInputStream = new DataInputStream(socket.getInputStream());
                        try {
                            String message = dataInputStream.readUTF();
                            setMessage(message,ClientFormController.this.vBoxArea);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    private void setMessage(String message, VBox vBoxArea) {

        HBox hBoxName = new HBox();
        hBoxName.setAlignment(Pos.CENTER_LEFT);

        Text textName = new Text(message);
        TextFlow textFlowName = new TextFlow(textName);
        hBoxName.getChildren().add(textFlowName);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                vBoxArea.getChildren().add(hBoxName);
                //vBoxArea.getChildren().add(hBoxName);
            }
        });
    }
}
