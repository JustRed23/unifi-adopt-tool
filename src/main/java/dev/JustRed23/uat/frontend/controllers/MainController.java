package dev.JustRed23.uat.frontend.controllers;

import dev.JustRed23.ipscan.util.MACUtils;
import dev.JustRed23.uat.frontend.Main;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class MainController implements Initializable {

    @FXML
    private BorderPane head;

    @FXML
    private Button exit;

    @FXML
    private Text status;

    //MAC FIELDS
    @FXML private TextField MAC1;
    @FXML private TextField MAC2;
    @FXML private TextField MAC3;
    @FXML private TextField MAC4;
    @FXML private TextField MAC5;
    @FXML private TextField MAC6;
    //MAC FIELDS

    @FXML private Button adopt;

    public void initialize(URL location, ResourceBundle resources) {
        Main.head = head;
        Main.latch.countDown();

        setTextFormatter(MAC1);
        focusNext(MAC1, MAC2);

        setTextFormatter(MAC2);
        focusNext(MAC2, MAC3);

        setTextFormatter(MAC3);
        focusNext(MAC3, MAC4);

        setTextFormatter(MAC4);
        focusNext(MAC4, MAC5);

        setTextFormatter(MAC5);
        focusNext(MAC5, MAC6);

        setTextFormatter(MAC6);
    }

    private void setTextFormatter(@NotNull TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9a-fA-F]*")) {
                textField.setText(newValue.replaceAll("[^0-9a-fA-F]", ""));
            }
            if (textField.getText().length() > 2) {
                String s = textField.getText().substring(0, 2);
                textField.setText(s);
            }
            textField.setText(textField.getText().toUpperCase());
            adopt.setDisable(MAC1.getText().length() != 2
                    || MAC2.getText().length() != 2
                    || MAC3.getText().length() != 2
                    || MAC4.getText().length() != 2
                    || MAC5.getText().length() != 2
                    || MAC6.getText().length() != 2);
        });
    }

    private void focusNext(@NotNull TextField textField, @NotNull TextField nextTextField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (textField.getText().length() == 2) {
                textField.getParent().requestFocus();
                nextTextField.requestFocus();
            }
        });
    }

    private @NotNull String combineMac() {
        String mac = MAC1.getText() + ':' + MAC2.getText() + ':' + MAC3.getText() + ':' + MAC4.getText() + ':' + MAC5.getText() + ':' + MAC6.getText();
        return MACUtils.format(mac);
    }

    private void clearMAC() {
        MAC1.clear();
        MAC2.clear();
        MAC3.clear();
        MAC4.clear();
        MAC5.clear();
        MAC6.clear();

        adopt.setDisable(true);
    }

    private String adoptOrigStyle;
    private Paint adoptOrigFill;
    @FXML
    private void adoptCalled() {
        MAC1.setDisable(true);
        MAC2.setDisable(true);
        MAC3.setDisable(true);
        MAC4.setDisable(true);
        MAC5.setDisable(true);
        MAC6.setDisable(true);

        exit.setDisable(true);
        adopt.setDisable(true);
        adopt.setText("Adopting...");
        adoptOrigFill = adopt.getTextFill();
        adopt.setTextFill(Color.WHITE);
        adoptOrigStyle = adopt.getStyle();
        adopt.setStyle("-fx-background-color: #453C41");
        adopt();
    }

    private void adopt() {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(this::adoptSuccess);
        }, "backend").start();
    }

    private void adoptFailed(String error) {
        status.setText(error);
        after(false);
    }

    private void adoptSuccess() {
        status.setText("Device successfully adopted.");
        after(true);
    }

    private void after(boolean success) {
        MAC1.setDisable(false);
        MAC2.setDisable(false);
        MAC3.setDisable(false);
        MAC4.setDisable(false);
        MAC5.setDisable(false);
        MAC6.setDisable(false);

        status.setVisible(true);

        exit.setDisable(false);
        adopt.setDisable(false);
        adopt.setText("Adopt");
        adopt.setTextFill(adoptOrigFill);
        adopt.setStyle(adoptOrigStyle);

        if (success)
            clearMAC();
    }

    @FXML
    private void exitCalled() {
        Main.exit();
    }

    @FXML
    private void exitHover() {
        exit.setTextFill(Color.RED);
    }

    @FXML
    private void exitUnhover() {
        exit.setTextFill(Color.WHITE);
    }
}
