package com.alexus.parslog.controller;

import com.alexus.parslog.data.LogLineClass;
import com.alexus.parslog.data.LogLineList;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Controller implements Initializable {
    @FXML
    public TextArea sourceText;
    @FXML
    public TreeView destTree;
    @FXML
    public TableView destTable;
    @FXML
    public TextField delimText;
    @FXML
    public TextField filterText;
    @FXML
    public Label errorLabel;
    @FXML
    public CheckBox showUnmatched;
    @FXML
    public ComboBox<Integer> widthList;

    private LogLineList parsedLog;
    Stage primaryStage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        widthList.getItems().clear();
        /*MenuItem mi = new MenuItem("Synhronize with grids");
        mi.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                System.out.println("click");
            }
        });

        sourceText.getContextMenu().getItems().add(mi);*/
    }

    private final Image redIcon = new Image(getClass().getResourceAsStream("/image/red_circle.png"));

    private void fillTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("root");
        rootItem.setExpanded(true);

        for (LogLineClass llc : parsedLog.getParsedLog()) {
            TreeItem<String> lineItem = null;
            String line = llc.getFullLine();

            if (filterText.getLength() > 0) {
                try {
                    if (line.matches(filterText.getText())) {
                        lineItem = new TreeItem<>(line, new ImageView(redIcon));
                    } else if (!showUnmatched.isSelected()) {
                        continue;
                    }
                } catch (PatternSyntaxException e) {
                    errorLabel.setText(e.getMessage());
                }
            }

            if (lineItem == null) {
                lineItem = new TreeItem<>(line);
            }

            rootItem.getChildren().add(lineItem);

            if (llc.getFieldList().size() > 1) {
                for (String block : llc.getFieldList()) {
                    TreeItem<String> blockItem = new TreeItem<>(block);
                    lineItem.getChildren().add(blockItem);
                }
            }
        }

        destTree.setRoot(rootItem);
    }

    private void fillTableView() {
        destTable.getColumns().clear();
        TableColumn col = new TableColumn("№");
        col.setMinWidth(50);
        col.setCellValueFactory(new PropertyValueFactory<LogLineClass, Integer>("number"));
        destTable.getColumns().add(col);
        for (int i = 1; i <= parsedLog.getColumnCount(); i++) {
            col = new TableColumn("F" + i);
            col.setMinWidth(200);
            col.setCellValueFactory(new PropertyValueFactory<LogLineClass, String>("F" + i));
            destTable.getColumns().add(col);
        }
        destTable.setItems(FXCollections.observableArrayList(parsedLog.getParsedLog()));
    }

    public void onParseButtonClicked() {
        parsedLog = new LogLineList();
        String text = sourceText.getText();
        Scanner lineScan = new Scanner(text);
        Pattern delimPattern = null;
        errorLabel.setText("");

        try {
            delimPattern = Pattern.compile(delimText.getText());
        } catch (PatternSyntaxException e) {
            errorLabel.setText(e.getMessage());
            return;
        }

        while (lineScan.hasNext()) {
            String line = lineScan.nextLine();
            parsedLog.addLine(line);
        }
        parsedLog.parsByDelimiter(delimPattern);
        fillTreeView();
        fillTableView();
    }

    public void onWidthParseButtonClicked() {
        parsedLog = new LogLineList();
        String text = sourceText.getText();
        Scanner lineScan = new Scanner(text);

        while (lineScan.hasNext()) {
            String line = lineScan.nextLine();
            parsedLog.addLine(line);
        }
        parsedLog.parsByFixedWidth(widthList.getItems());
        fillTreeView();
        fillTableView();
    }

    public void onAddPositionButtonClicked() {
        int pos = sourceText.getCaretPosition();
        widthList.getItems().add(pos);
        widthList.setPromptText(Arrays.toString(widthList.getItems().toArray()));
    }

    public void onClearButtonClicked() {
        widthList.getItems().clear();
    }

    public void onDragOver(DragEvent event) {
        if (event.getGestureSource() != sourceText &&
                event.getDragboard().hasString()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    public void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) {
            sourceText.setText(db.getString());
            success = true;
        }
        event.setDropCompleted(success);
        event.consume();
    }

    public void onSynchronizeGrid() {
        Integer line = destTable.getSelectionModel().getSelectedIndex();
        Integer pos = parsedLog.lineByPos(line + 1);
        if (line == null || pos == null) return;
        destTree.getSelectionModel().select(line.intValue());
        destTree.scrollTo(line.intValue());
        sourceText.positionCaret(pos.intValue());
        sourceText.selectForward();
    }

    public void onSynchronizeTree() {

    }

    public void onNextWord() {
        Pattern pt = Pattern.compile("\\s\\S");
        Matcher mt = pt.matcher(sourceText.getText());
        if (mt.find(sourceText.getCaretPosition()) && mt.start() > 0) {
            sourceText.selectRange(mt.start(), mt.end() - 1);
        }
    }

    public void onExportToXLSX() throws IOException {
        if (parsedLog.getParsedLog().size() == 0) return;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save as XLSX");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLSX documents", "*.xlsx"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showSaveDialog(primaryStage);

        if (file != null) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet spreadsheet = workbook.createSheet("Log");
            XSSFRow row;
            int rowid = 0;
            for (LogLineClass llc : parsedLog.getParsedLog()) {
                row = spreadsheet.createRow(rowid++);
                int cellid = 0;
                for (String s : llc.getFieldList()) {
                    XSSFCell cell = row.createCell(cellid++);
                    cell.setCellValue(s);
                }
            }
            FileOutputStream out = new FileOutputStream(file);
            workbook.write(out);
            out.close();
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
