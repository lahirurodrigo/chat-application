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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lk.ijse.emojiPicker.EmojiPicker;

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
        textFlow.setStyle("-fx-background-color: #3498db; -fx-font-weight: normal; -fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5,10,5,10));
        text.setFill(Color.WHITE);
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

        this.vBoxArea.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                scrollPane.setVvalue((Double) newValue);
            }
        });

        emoji();

    }

    private void emoji() {

        EmojiPicker emojiPicker = new EmojiPicker();

        VBox vBox = new VBox(emojiPicker);
        vBox.setPrefSize(150,300);
        vBox.setLayoutX(400);
        vBox.setLayoutY(175);
        vBox.setStyle("-fx-font-size: 30");

        rootNode.getChildren().add(vBox);
        emojiPicker.setVisible(false);

        btnEmoji.setOnAction(MouseEvent ->{
            if(emojiPicker.isVisible()){
                emojiPicker.setVisible(false);
            }else{
                emojiPicker.setVisible(true);
            }
        });

        emojiPicker.getEmojiLstView().setOnMouseClicked(mouseEvent -> {
            String selectedEmoji = emojiPicker.getEmojiLstView().getSelectionModel().getSelectedItem();

            if (selectedEmoji != null){
                txtMessage.setText(txtMessage.getText() + selectedEmoji);
            }

            emojiPicker.setVisible(false);
        });
    }

    private void setMessage(String message, VBox vBoxArea) {

        if (message.matches(".*\\.(png|jpe?g|gif)$")){

            System.out.println("hello image is here: "+message);
            HBox hBoxName = new HBox();
            hBoxName.setAlignment(Pos.CENTER_LEFT);
            Text textName = new Text(message.split("[,]")[0]);
            TextFlow textFlowName = new TextFlow(textName);
            hBoxName.getChildren().add(textFlowName);

            System.out.println("123456: "+message.split("[,]")[1]);
            Image image = new Image(message.split("[,]")[1]);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(200);
            imageView.setFitWidth(200);
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));
            hBox.getChildren().add(imageView);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    vBoxArea.getChildren().add(hBoxName);
                    vBoxArea.getChildren().add(hBox);
                }
            });

        }else{
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER_LEFT);
            hBox.setPadding(new Insets(5,5,5,10));

            Text text = new Text(message);
            TextFlow textFlow = new TextFlow(text);
            textFlow.setStyle("-fx-background-color: #2c3e50; -fx-font-weight: normal; -fx-background-radius: 20px");
            textFlow.setPadding(new Insets(5,10,5,10));
            text.setFill(Color.WHITE);
            hBox.getChildren().add(textFlow);

            Platform.runLater(new Runnable() {
                @Override
                public void run() {

                    vBoxArea.getChildren().add(hBox);
                }
            });

        }
    }
}
