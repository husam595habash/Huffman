package Pane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;

public class MainPane {
    private BorderPane root;
    private Button browseButton, compressButton, decompressButton;
    private Label titleLabel;
    private TextField filePathField;

    public MainPane() {
        show();
    }

    private void show() {
        root = new BorderPane();

        titleLabel = new Label("File Compression / Decompression");
        titleLabel.getStyleClass().add("label-white");

        filePathField = new TextField();
        filePathField.setPrefWidth(350);
        filePathField.setPromptText("No file selected");
        filePathField.setEditable(false);
        filePathField.getStyleClass().add("text-field");

        browseButton = new Button("Browse");
        browseButton.getStyleClass().add("action-button");


        HBox fileBox = new HBox(10, browseButton, filePathField);
        fileBox.setAlignment(Pos.CENTER);

        compressButton = new Button("Compress");
        compressButton.getStyleClass().add("action-button");
        compressButton.setDisable(true);

        decompressButton = new Button("Decompress");
        decompressButton.getStyleClass().add("action-button");
        decompressButton.setDisable(true);

        HBox buttonBox = new HBox(20, compressButton, decompressButton);
        buttonBox.setAlignment(Pos.CENTER);

        VBox layout = new VBox(30, titleLabel, fileBox, buttonBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        root.setCenter(layout);
        root.getStyleClass().add("root-pane");
    }

    public BorderPane getRoot() {
        return root;
    }

    public Button getBrowseButton() {
        return browseButton;
    }

    public Button getCompressButton() {
        return compressButton;
    }

    public Button getDecompressButton() {
        return decompressButton;
    }

    public TextField getFilePathField() {
        return filePathField;
    }
}
