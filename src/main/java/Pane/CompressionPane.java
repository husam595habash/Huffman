package Pane;

import Structures.CodeTableEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.control.cell.PropertyValueFactory;

public class CompressionPane {
    private VBox root;
    private TextArea headerDetailsArea;
    private TableView<CodeTableEntry> tableView;
    private Button backButton;

    public CompressionPane() {
        // Header Area
        headerDetailsArea = new TextArea();
        headerDetailsArea.setEditable(false);
        headerDetailsArea.setWrapText(true);
        headerDetailsArea.setFont(Font.font("Consolas", 16));
        headerDetailsArea.setPrefHeight(200);

        TitledPane headerPane = new TitledPane("Header Details", headerDetailsArea);
        headerPane.setExpanded(true);

        // Table initialization
        initializeTableView();

        TitledPane tablePane = new TitledPane("Huffman Code Table", tableView);
        tablePane.setExpanded(true);

        // Back Button
        backButton = new Button("Back");
        backButton.getStyleClass().add("button");
        backButton.setPrefWidth(120);
        backButton.setFont(Font.font(14));
        HBox buttonBox = new HBox(backButton);
        buttonBox.setPadding(new Insets(20, 0, 10, 0));
        buttonBox.setAlignment(Pos.BOTTOM_LEFT);

        root = new VBox(30, headerPane, tablePane, buttonBox);
        root.setPadding(new Insets(30));
        root.setPrefSize(1000, 700);  // Set preferred size
    }

    private void initializeTableView() {
        tableView = new TableView<>();
        tableView.setPrefHeight(400);
        tableView.setPlaceholder(new Label("No codes to display"));
        tableView.setStyle("-fx-font-size: 16px;");

        TableColumn<CodeTableEntry, Character> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueColumn.setMinWidth(230);

        TableColumn<CodeTableEntry, Integer> freqColumn = new TableColumn<>("Frequency");
        freqColumn.setCellValueFactory(new PropertyValueFactory<>("freq"));
        freqColumn.setMinWidth(200);

        TableColumn<CodeTableEntry, String> codeColumn = new TableColumn<>("Code");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeColumn.setMinWidth(200);

        tableView.getColumns().addAll(valueColumn, freqColumn, codeColumn);
    }

    public VBox getRoot() {
        return root;
    }

    public Button getBackButton() {
        return backButton;
    }

    public void setHeaderDetails(String text) {
        headerDetailsArea.setText(text);
    }

    public void setCodeTable(CodeTableEntry[] codeTable) {
        ObservableList<CodeTableEntry> data = FXCollections.observableArrayList();
        for (CodeTableEntry entry : codeTable) {
            if (entry != null && entry.getCode() != null) {
                data.add(entry);
            }
        }
        tableView.setItems(data);
    }
}
