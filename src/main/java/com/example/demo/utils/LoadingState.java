package com.example.demo.utils;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

public class LoadingState {
    
    public static void setLoading(Control control, boolean loading) {
        if (control == null) return;
        
        Platform.runLater(() -> {
            control.setDisable(loading);
            if (control instanceof Button) {
                Button button = (Button) control;
                if (loading) {
                    button.setStyle(button.getStyle() + " -fx-opacity: 0.6;");
                } else {
                    button.setStyle(button.getStyle().replace(" -fx-opacity: 0.6;", ""));
                }
            }
        });
    }


}
