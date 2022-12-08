package dev.JustRed23.uat.frontend;

import dev.JustRed23.abcm.Config;
import dev.JustRed23.abcm.exception.ConfigInitException;
import dev.JustRed23.uat.frontend.controllers.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import static dev.JustRed23.uat.frontend.utils.DragUtils.getDraggable;

public class Main extends Application {

    public static CountDownLatch latch = new CountDownLatch(1);
    public static BorderPane head;

    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/views/mainscreen.fxml"), "View not found"));

        latch.await();
        getDraggable(root, stage).setRequirements(draggable -> head.contains(draggable.xOffset, draggable.yOffset)).enable();

        stage.setTitle("Unifi Adopt Tool");

        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);

        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void exit() {
        System.exit(0);
    }

    public static void main(String[] args) throws ConfigInitException {
        Config.addParser(InetAddressParser.class);
        Config.addScannable(UAT.class);

        File cfgDir = new File(System.getProperty("user.home") + File.separator + ".uat");
        if (!cfgDir.exists())
            cfgDir.mkdir();

        Config.setConfigDir(cfgDir);
        Config.init();
        launch(args);
    }
}
