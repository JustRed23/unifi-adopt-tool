package dev.JustRed23.uat.frontend.utils;

import javafx.scene.Parent;
import javafx.stage.Stage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class DragUtils {

    private static final Map<Stage, Draggable> draggableMap = new ConcurrentHashMap<>();

    public static Draggable getDraggable(Parent root, Stage stage) {
        return draggableMap.computeIfAbsent(stage, s -> new Draggable(root, s));
    }

    public static final class Draggable {

        private final Parent root;
        private final Stage stage;
        public double xOffset = 0, yOffset = 0;
        private Function<Draggable, Boolean> requirements = drag -> true;

        public Draggable(Parent root, Stage stage) {
            this.root = root;
            this.stage = stage;
        }

        public Draggable setRequirements(Function<Draggable, Boolean> requirements) {
            this.requirements = requirements;
            return this;
        }

        public void enable() {
            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });

            root.setOnMouseDragged(event -> {
                if (requirements.apply(this)) {
                    stage.setX(event.getScreenX() - xOffset);
                    stage.setY(event.getScreenY() - yOffset);
                }
            });
        }

        public void disable() {
            root.setOnMousePressed(null);
            root.setOnMouseDragged(null);
        }

        public Parent getRoot() {
            return root;
        }

        public Stage getStage() {
            return stage;
        }
    }
}
