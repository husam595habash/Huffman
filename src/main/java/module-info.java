module com.example.huffman {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.huffman to javafx.fxml;
    opens Structures to javafx.base; // ✅ This line fixes the PropertyValueFactory access error

    exports com.example.huffman;
    exports Structures; // (optional, if you're using the package externally)
}
