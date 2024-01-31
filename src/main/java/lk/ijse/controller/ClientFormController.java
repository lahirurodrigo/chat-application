package lk.ijse.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientFormController implements Initializable {

    @FXML
    private JFXButton btnSend;

    @FXML
    private JFXButton btnEmoji;

    @FXML
    private JFXButton btnImageBrowser;

    @FXML
    private TextField txtMessage;

    @FXML
    private VBox vBoxArea;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane rootNode;


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

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_RIGHT);
        hBox.setPadding(new Insets(5,5,5,10));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #81ecec;-fx-fill: #f5f6fa;-fx-font-weight: normal; -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5,10,5,10));
        text.setFill(Color.color(0,0,0));
        hBox.getChildren().add(textFlow);

        vBoxArea.getChildren().add(hBox);

    }

    @FXML
    void btnEmojiOnAction(ActionEvent event) {

    }

    @FXML
    void btnImageBrowserOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp", "*.jpeg")
        );

        Stage stage = (Stage) rootNode.getScene().getWindow();

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            String sendImage = file.toURI().toString();
            System.out.println("Image URL: " + sendImage);
            sendImageToServer(sendImage);
        }
    }

    private void sendImageToServer(String sendImage) {
        Image image = new Image(sendImage);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(5,5,5,10));
        hBox.getChildren().add(imageView);
        hBox.setAlignment(Pos.CENTER_RIGHT);

        vBoxArea.getChildren().add(hBox);

        try {
            dataOutputStream.writeUTF(sendImage);
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void txtMessageOnAction(ActionEvent event) {
        btnSend.requestFocus();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> txtMessage.requestFocus());

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

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5,5,5,10));

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #dfe6e9; -fx-font-weight: normal; -fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5,10,5,10));
        text.setFill(Color.color(0,0,0));
        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {

                vBoxArea.getChildren().add(hBox);
                //vBoxArea.getChildren().add(hBoxName);
            }
        });
    }
}
