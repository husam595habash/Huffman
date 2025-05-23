package com.example.huffman;

import Compression.Compression;
import Decompression.Decompression;
import Pane.CompressionPane;
import Pane.MainPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class HelloApplication extends Application {
    private File file;
    private MainPane mainPane = new MainPane();
    private CompressionPane compressionPane = new CompressionPane();

    @Override
    public void start(Stage stage) {
        // GUI STEP 1: Set up main page scene
        Scene scene = new Scene(mainPane.getRoot() , 600 , 400);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();

        // GUI STEP 2: Handle "Browse" button
        mainPane.getBrowseButton().setOnAction(e -> {
            file = showFileChooser(stage);
            if (file != null) {
                String extension = Compression.getFileExtension(file);
                mainPane.getFilePathField().setText(file.getAbsolutePath());

                // Enable decompress only if it's a .huf file, otherwise enable compress
                if (extension != null && extension.equalsIgnoreCase("huf")) {
                    mainPane.getDecompressButton().setDisable(false);
                    mainPane.getCompressButton().setDisable(true);
                } else {
                    mainPane.getCompressButton().setDisable(false);
                    mainPane.getDecompressButton().setDisable(true);
                }
            } else {
                // No file selected: disable both buttons
                mainPane.getFilePathField().setText("No File Selected");
                mainPane.getCompressButton().setDisable(true);
                mainPane.getDecompressButton().setDisable(true);
            }
        });


        // GUI STEP 3: Handle "Compress" button
        mainPane.getCompressButton().setOnAction(event -> {
            try {
                Compression.compression(file , compressionPane);
                scene.setRoot(compressionPane.getRoot());
                stage.setWidth(700);
                stage.setHeight(800);
            } catch (Exception e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Compression Failed");
                errorAlert.setContentText("An error occurred: " + e.getMessage());
                errorAlert.showAndWait();
            }
        });

        // GUI STEP 4: Handle "Back" button
        compressionPane.getBackButton().setOnAction(ev -> {
            scene.setRoot(mainPane.getRoot());
            stage.setWidth(600);
            stage.setHeight(400);
        });

        // GUI STEP 5: Handle "Decompress" button
        mainPane.getDecompressButton().setOnAction(e -> {
            boolean isSuccess = Decompression.decompression(file);
            if (isSuccess) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Decompression completed successfully.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Decompression failed.");
                alert.showAndWait();
            }

        });
    }





    private File showFileChooser(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        fileChooser.setInitialDirectory(new File("C:\\Users\\husam\\OneDrive\\Documents"));
        return fileChooser.showOpenDialog(stage);
    }
    public static void main(String[] args) {
        launch();
    }
}