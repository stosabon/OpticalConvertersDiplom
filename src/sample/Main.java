package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.NumberStringConverter;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main extends Application {

    private List<OpticalConverter> mOpticalConverters;
    private TextField mX1TextField;
    private TextField mX2TextField;
    private TextField mEnvironmentCoef;
    private TextField mReflectiveCoef;
    private TextField mRoughnessCoef;
    private TableView<TableModel> mDataTable;
    private ComboBox<String> mLangsComboBox;

    @Override
    public void start(Stage primaryStage) throws Exception{
        mOpticalConverters = new ArrayList<>();

        ObjectInputStream objectinputstream = null;
        try {
            FileInputStream streamIn = new FileInputStream("converter.txt");
            objectinputstream = new ObjectInputStream(streamIn);
            List<OpticalConverter> readCase = (List<OpticalConverter>) objectinputstream.readObject();
            mOpticalConverters.addAll(readCase);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(objectinputstream != null){
                objectinputstream .close();
            }
        }

        HBox hBox = new HBox(100);
        VBox vBox = new VBox();
        vBox.getChildren().add(createFlowPane());
        LineChart lineChart = createChart();
        vBox.getChildren().add(lineChart);
        vBox.getChildren().add(createButtonBoxUnderGraph(lineChart));

        hBox.getChildren().add(createLeftPanel());
        hBox.getChildren().add(vBox);
        hBox.getChildren().add(createTableView());

        primaryStage.setTitle("Моделирование характеристик оптических преобразователей");
        primaryStage.setScene(new Scene(hBox));
        primaryStage.setMaximized(true);

        primaryStage.show();
    }

    private HBox createFlowPane() {
        List<String> converterNames = mOpticalConverters.stream().map(OpticalConverter::getName).collect(Collectors.toList());
        ObservableList<String> langs = FXCollections.observableList(converterNames);
        mLangsComboBox = new ComboBox<>(langs);

        Button button = new Button("Добавить светодиод");
        button.setOnAction(event -> showChooseConverter(mLangsComboBox));

        HBox hBox = new HBox(50, mLangsComboBox, button);
        hBox.setPadding(new Insets(50, 0, 0, 100));
        return hBox;
    }

    private LineChart<Number, Number> createChart() {
        NumberAxis x = new NumberAxis();
        NumberAxis y = new NumberAxis();

        LineChart<Number, Number> numberLineChart = new LineChart<>(x, y);
        numberLineChart.setTitle("Функция преобразования");
        numberLineChart.setCreateSymbols(false);

        return numberLineChart;
    }

    private void showChooseConverter(ComboBox<String> opticalConverterComboBox) {
        Dialog<OpticalConverter> dialog = new Dialog<>();
        dialog.setTitle("Выберите параметры светодиода");
        dialog.setHeaderText(null);
        dialog.initStyle(StageStyle.UTILITY);

        ButtonType createConverterButton = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().clear();
        dialog.getDialogPane().getButtonTypes().addAll(createConverterButton, new ButtonType("Отменить", ButtonBar.ButtonData.CANCEL_CLOSE));

        Node disableCreateConverter = dialog.getDialogPane().lookupButton(createConverterButton);
        disableCreateConverter.setDisable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField opticalConverterName = new TextField();

        grid.add(new Label("Введите название светодиода"), 0, 0);
        grid.add(opticalConverterName, 1, 0);

        opticalConverterName.textProperty().addListener((observable, oldValue, newValue) -> {
            Node createConverter = dialog.getDialogPane().lookupButton(createConverterButton);
            if (newValue.isEmpty()) {
                createConverter.setDisable(true);
            } else {
                createConverter.setDisable(false);
            }
        });

        TextField opticalConverterEnergy = new TextField();
        grid.add(new Label("Введите ток светодиода (мА)"), 0, 1);
        grid.add(opticalConverterEnergy, 1, 1);

        opticalConverterEnergy.textProperty().addListener((observable, oldValue, newValue) -> {
            Node createConverter = dialog.getDialogPane().lookupButton(createConverterButton);
            if (newValue.isEmpty()) {
                createConverter.setDisable(true);
            } else {
                createConverter.setDisable(false);
            }
        });

        TextField opticalConverterDegree = new TextField();
        grid.add(new Label("Введите угол распространения света светодиода в градусах"), 0, 2);
        grid.add(opticalConverterDegree, 1, 2);

        opticalConverterDegree.textProperty().addListener((observable, oldValue, newValue) -> {
            Node createConverter = dialog.getDialogPane().lookupButton(createConverterButton);
            if (newValue.isEmpty()) {
                createConverter.setDisable(true);
            } else {
                createConverter.setDisable(false);
            }
        });

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createConverterButton) {
                return new OpticalConverter(opticalConverterName.getText(), Integer.valueOf(opticalConverterDegree.getText()), Integer.valueOf(opticalConverterEnergy.getText()));
            }
            return null;
        });

        Optional<OpticalConverter> result = dialog.showAndWait();

        result.ifPresent(opticalConverter -> {
            mOpticalConverters.add(opticalConverter);
            try {
                FileOutputStream fileOut = new FileOutputStream("converter.txt");
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
                objectOut.write("".getBytes());
                objectOut.writeObject(mOpticalConverters);
                objectOut.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            opticalConverterComboBox.getItems().add(opticalConverter.getName());
        });
    }

    private VBox createLeftPanel() {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(0,0,0,50));

        Label label = new Label("Выберите внешние параметры");
        label.setMinHeight(50);

        Label x1 = new Label("Укажите расстояние от светодиода до фотодиода");
        x1.setMinHeight(50);
        mX1TextField = new TextField();

        Label x2 = new Label("Укажите ширину принимающей поверхности фотодиода");
        x2.setMinHeight(50);
        mX2TextField = new TextField();


        Label environmentCoef = new Label("Укажите коэффициент распространения среды");
        environmentCoef.setMinHeight(50);
        mEnvironmentCoef = new TextField();

        Label reflectiveCoef = new Label("Укажите коэффициент отражения поверхности");
        reflectiveCoef.setMinHeight(50);
        mReflectiveCoef = new TextField();

        Label roughnessCoef = new Label("Укажите коэффициент шероховатости поверхности");
        roughnessCoef.setMinHeight(50);
        mRoughnessCoef = new TextField();

        vBox.getChildren().add(label);
        vBox.getChildren().add(x1);
        vBox.getChildren().add(mX1TextField);
        vBox.getChildren().add(x2);
        vBox.getChildren().add(mX2TextField);
        vBox.getChildren().add(environmentCoef);
        vBox.getChildren().add(mEnvironmentCoef);
        vBox.getChildren().add(reflectiveCoef);
        vBox.getChildren().add(mReflectiveCoef);
        vBox.getChildren().add(roughnessCoef);
        vBox.getChildren().add(mRoughnessCoef);

        return vBox;
    }

    private TableView<TableModel> createTableView() {
        mDataTable = new TableView<>();
        mDataTable.setPrefWidth(400);
        mDataTable.setMaxHeight(500);
        TableColumn<TableModel, String> converterNameColumn = new TableColumn<>("Название светодиода");
        converterNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<TableModel, Double> distanceColumn = new TableColumn<>("Диапазон (мм)");
        distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
        TableColumn<TableModel, Double> sensitivityColumn = new TableColumn<>("Чувствительность");
        sensitivityColumn.setCellValueFactory(new PropertyValueFactory<>("sensitivity"));
        mDataTable.getColumns().add(converterNameColumn);
        mDataTable.getColumns().add(distanceColumn);
        mDataTable.getColumns().add(sensitivityColumn);
        mDataTable.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

        return mDataTable;
    }

    private HBox createButtonBoxUnderGraph(LineChart lineChart) {
        HBox hBox = new HBox(50);
        hBox.setPadding(new Insets(50, 0, 0, 100));

        Button clearButton = new Button("Очистить график");
        clearButton.setOnAction(event -> {
            lineChart.getData().clear();
            mDataTable.getItems().clear();
        });

        Button addButton = new Button("Добавить график");
        addButton.setOnAction(event -> {
            String converterName = mLangsComboBox.getSelectionModel().getSelectedItem();
            OpticalConverter selectedConverter = null;
            for (OpticalConverter opticalConverter : mOpticalConverters) {
                if (opticalConverter.getName().equals(converterName)) {
                    selectedConverter = opticalConverter;
                }
            }

            ObservableList<XYChart.Data> datas = FXCollections.observableArrayList();

            double maxValue = 0;
            double xmin = -1;
            double xmax = -1;
            double ymin = -1;

            for(double distance = 0; distance < 200; distance += 1.0) {
                int numOfLigherts = 0;
                for (int lighters = 1; lighters < 10000; lighters++) {
                    double radians = Math.toRadians((double) selectedConverter.getDegereeRange() / 10000.0 * (double) lighters);
                    double tan = Math.tan(radians);
                    double katet = distance * tan;
                    int x1 = Integer.valueOf(mX1TextField.getText());
                    int x2 = Integer.valueOf(mX2TextField.getText());

                    double y = 2.0 * katet;


                    if (y > (double) x1 && y < (double) (x1 + x2)) {
                        numOfLigherts++;
                    }
                }
                if (numOfLigherts != 0) {
                    float reflectiveCoef = Float.valueOf(mReflectiveCoef.getText());
                    float environmentCoef = Float.valueOf(mEnvironmentCoef.getText());
                    if (datas.isEmpty()) {
                        xmin = distance;
                        ymin = selectedConverter.getEnergy() / 10000.0 * (double) numOfLigherts * reflectiveCoef * environmentCoef;
                    }

                    if (maxValue < selectedConverter.getEnergy() / 10000.0 * (double) numOfLigherts * reflectiveCoef * environmentCoef) {
                        maxValue = selectedConverter.getEnergy() / 10000.0 * (double) numOfLigherts * reflectiveCoef * environmentCoef;
                        xmax = distance;
                    }
                    datas.add(new XYChart.Data(distance,
                            (double) selectedConverter.getEnergy() / 10000.0 * (double) numOfLigherts
                            * reflectiveCoef * environmentCoef));
                }

            }
            mDataTable.getItems().add(mDataTable.getItems().size(),
                    new TableModel(
                            selectedConverter.getName(),
                            xmax - xmin,
                            Math.toDegrees(Math.tan(((maxValue - ymin) / selectedConverter.getEnergy()) / ((xmax - xmin) / 200)))));
            XYChart.Series series1 = new XYChart.Series();
            series1.setName(selectedConverter.getName());
            series1.setData(datas);
            lineChart.getData().add(series1);
            });

        hBox.getChildren().add(clearButton);
        hBox.getChildren().add(addButton);

        return hBox;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
