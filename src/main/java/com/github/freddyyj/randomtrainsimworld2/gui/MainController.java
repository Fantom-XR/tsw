package com.github.freddyyj.randomtrainsimworld2.gui;

import com.github.freddyyj.randomtrainsimworld2.*;
import com.github.freddyyj.randomtrainsimworld2.Main;
import com.github.freddyyj.randomtrainsimworld2.config.SaveLoco;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Main controller of {@link com.github.freddyyj.randomtrainsimworld2.gui.Main}.
 * Call {@link MainController#setRandom(Random)} to set random object to your custom class.
 */
public class MainController {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private VBox boxRoute;
    @FXML
    private Pane boxLoco;
    @FXML
    private VBox boxWeather;
    @FXML
    private TextField textPickedRoute;
    @FXML
    private TextField textPickedLoco;
    @FXML
    private TextField textPickedWeather;
    @FXML
    private MenuItem itemSave;
    @FXML
    private MenuItem itemSaveAs;
    @FXML
    private MenuItem itemLoad;
    @FXML
    private MenuItem itemClose;
    @FXML
    private MenuItem itemAbout;
    private List<List<Node>> locos;
    private List<Node> routes;
    private List<Node> weathers;
    private VBox currentRoute; // Box of locos that selected
    private CheckBox currentBox; // Route Checkbox
    private Random random;

    /**
     * default constructor
     */
    public MainController() {
    }

    @FXML
    private void initialize() {
        locos = new Vector<List<Node>>();
        routes = boxRoute.getChildren();
        weathers = boxWeather.getChildren();
        currentRoute = getLocoBoxByID("checkCSX");
        currentRoute.setVisible(true);
        currentRoute.setDisable(false);
        List<Node> locoList;
        for (int i = 0; i < boxLoco.getChildren().size(); i++) {
            VBox locoBox = (VBox) boxLoco.getChildren().get(i);
            locoList = locoBox.getChildren();
            locos.add(locoList);
        }

        Vector<String> routes = new Vector<>();
        HashMap<String, List<String>> locos = new HashMap<>();
        for (int i = 0; i < this.routes.size(); i++) {
            routes.add(((CheckBox) this.routes.get(i)).getText());
            List<String> locoLists = new Vector<>();
            for (int j = 0; j < this.locos.get(i).size(); j++) {
                locoLists.add(((CheckBox) this.locos.get(i).get(j)).getText());
            }
            locos.put(((CheckBox) this.routes.get(i)).getText(), locoLists);
        }
        Vector<String> weather = new Vector<>();
        for (int i = 0; i < weathers.size(); i++) {
            weather.add(((CheckBox) weathers.get(i)).getText());
        }

        currentBox = (CheckBox) this.routes.get(0);

        anchorPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.setOnKeyPressed(this::onShortcut);
            }
        });

        random=Random.getInstance();
    }

    /**
     * Get all route list.
     * @return all route list
     */
    public ArrayList<String> getRouteList(){
        ArrayList<String> route=new ArrayList<>();
        for (int i=0;i<routes.size();i++){
            route.add(((CheckBox)routes.get(i)).getText());
        }
        return route;
    }

    /**
     * Get all locomotive list.
     * @return all locomotive list
     */
    public ArrayList<ArrayList<String>> getLocoList(){
        ArrayList<ArrayList<String>> loco=new ArrayList<>();
        for (int i=0;i<locos.size();i++){
            loco.add(new ArrayList<>());
            for (int j=0;j<locos.get(i).size();j++){
                loco.get(i).add(((CheckBox)locos.get(i).get(j)).getText());
            }
        }
        return loco;
    }

    /**
     * Get all weather list.
     * @return all weather list
     */
    public ArrayList<String> getWeather(){
        ArrayList<String> weather=new ArrayList<>();
        for (int i=0;i<weathers.size();i++){
            weather.add(((CheckBox)weathers.get(i)).getText());
        }
        return weather;
    }

    /**
     * Set random class.
     * <p>
     *     If you want to set custom random class, call {@link com.github.freddyyj.randomtrainsimworld2.gui.Main}.{@link com.github.freddyyj.randomtrainsimworld2.gui.Main#controller}.setRandom(Random).
     * </p>
     * @param random extended {@link Random} object
     */
    public void setRandom(Random random){
        this.random=random;
    }

    @FXML
    protected void onCheckRouteClick(MouseEvent e) {
        if (currentBox == null) {
            currentBox = (CheckBox) routes.get(0);
        }
        if (e.getSource() instanceof CheckBox) {
            currentBox = (CheckBox) e.getSource();
        }
        if (currentRoute == null) {
            currentRoute = getLocoBoxByID("checkCSX");
        }
        currentRoute.setVisible(false);
        currentRoute.setDisable(true);
        Node routeBox = (Node) e.getSource();
        String id = routeBox.getId();
        currentRoute = getLocoBoxByID(id);
        currentRoute.setVisible(true);
        currentRoute.setDisable(false);

        textPickedRoute.setText(((CheckBox) routeBox).getText());
    }

    @FXML
    protected void onRandomAll(ActionEvent e) {
        ArrayList<ArrayList<Locomotive>> locoList=new ArrayList<>();
        for (int i=0;i<Main.getInstance().getRoutes().size();i++){
            locoList.add(Main.getInstance().getLocomotive(Main.getInstance().getRoutes().get(i).getName()));
        }

        Locomotive loco = random.randomLocomotiveInAll(locoList);
        Route route = loco.getRoute();

        CheckBox locoBox = getLocoByName(loco.getName(), route.getName());
        Weather weather = random.randomWeather(Main.getInstance().getWeathers());

        textPickedRoute.setText(route.getName());
        textPickedLoco.setText(loco.getName());
        textPickedWeather.setText(weather.getName());

        currentRoute.setVisible(false);
        currentRoute.setDisable(true);
        String name=getRouteByName(route.getName()).getId();
        currentRoute = getLocoBoxByID(name);
        currentRoute.setVisible(true);
        currentRoute.setDisable(false);

        currentBox=getRouteByName(loco.getRoute().getName());
    }

    @FXML
    protected void onRandomRoute(ActionEvent e) {
        Route selected=random.randomRoute(Main.getInstance().getRoutes());
        CheckBox selectedRoute = getRouteByName(selected.getName());

        currentRoute.setVisible(false);
        currentRoute.setDisable(true);

        textPickedRoute.setText(selectedRoute.getText());
        currentRoute = getLocoBoxByID(selectedRoute.getId());
        currentRoute.setVisible(true);
        currentRoute.setDisable(false);

        currentBox=selectedRoute;
    }

    @FXML
    protected void onRandomLoco(ActionEvent e) {
        Locomotive selected=random.randomLocomotive(Main.getInstance().getLocomotive(currentBox.getText()));
        CheckBox loco = getLocoByName(selected.getName(),currentBox.getText());

        textPickedRoute.setText(currentBox.getText());
        textPickedLoco.setText(loco.getText());
    }

    @FXML
    protected void onRandomWeather(ActionEvent e) {
        Weather weather = random.randomWeather(Main.getInstance().getWeathers());
        textPickedWeather.setText(weather.getName());
    }
    @FXML
    protected void onRandom2020(ActionEvent e){
        ArrayList<ArrayList<Locomotive>> locoList=new ArrayList<>();
        for (int i=0;i<Main.getInstance().getRoutes().size();i++){
            if (!Main.getInstance().getRoutes().get(i).getName().endsWith("(TSW2)"))
                locoList.add(Main.getInstance().getLocomotive(Main.getInstance().getRoutes().get(i).getName()));
        }

        Locomotive loco = random.randomLocomotiveInAll(locoList);
        Route route = loco.getRoute();

        CheckBox locoBox = getLocoByName(loco.getName(), route.getName());
        Weather weather = random.randomWeather(Main.getInstance().getWeathers());

        textPickedRoute.setText(route.getName());
        textPickedLoco.setText(loco.getName());
        textPickedWeather.setText(weather.getName());

        currentRoute.setVisible(false);
        currentRoute.setDisable(true);
        String name=getRouteByName(route.getName()).getId();
        currentRoute = getLocoBoxByID(name);
        currentRoute.setVisible(true);
        currentRoute.setDisable(false);

        currentBox=getRouteByName(loco.getRoute().getName());
    }
    @FXML
    protected void onRandom2(ActionEvent e){
        ArrayList<ArrayList<Locomotive>> locoList=new ArrayList<>();
        for (int i=0;i<Main.getInstance().getRoutes().size();i++){
            if (Main.getInstance().getRoutes().get(i).getName().endsWith("(TSW2)"))
                locoList.add(Main.getInstance().getLocomotive(Main.getInstance().getRoutes().get(i).getName()));
        }

        Locomotive loco = random.randomLocomotiveInAll(locoList);
        Route route = loco.getRoute();

        CheckBox locoBox = getLocoByName(loco.getName(), route.getName());
        Weather weather = random.randomWeather(Main.getInstance().getWeathers());

        textPickedRoute.setText(route.getName());
        textPickedLoco.setText(loco.getName());
        textPickedWeather.setText(weather.getName());

        currentRoute.setVisible(false);
        currentRoute.setDisable(true);
        String name=getRouteByName(route.getName()).getId();
        currentRoute = getLocoBoxByID(name);
        currentRoute.setVisible(true);
        currentRoute.setDisable(false);

        currentBox=getRouteByName(loco.getRoute().getName());
    }

    @FXML
    protected void onCheckLocoSelect(ActionEvent e) {
        if (e.getSource() instanceof CheckBox) {
            CheckBox selectedLoco = (CheckBox) e.getSource();
            Main.getInstance().selectLocomotive(selectedLoco.isSelected(), Main.getInstance().getLocomotive(currentBox.getText(),selectedLoco.getText()), Main.getInstance().getRoute(currentBox.getText()));
        }
    }

    @FXML
    protected void onCheckRouteSelect(ActionEvent e) {
        if (e.getSource() instanceof CheckBox) {
            CheckBox selectedRoute = (CheckBox) e.getSource();
            Main.getInstance().selectRoute(selectedRoute.isSelected(), Main.getInstance().getRoute(selectedRoute.getText()));
        }

    }

    @FXML
    protected void onCheckWeatherSelect(ActionEvent e) {
        if (e.getSource() instanceof CheckBox) {
            CheckBox selectedWeather = (CheckBox) e.getSource();
            Main.getInstance().selectWeather(selectedWeather.isSelected(), Main.getInstance().getWeather(selectedWeather.getText()));
        }

    }

    @FXML
    protected void onSaveAs(ActionEvent e) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save File as");
        chooser.getExtensionFilters().add(new ExtensionFilter("JSON File", "*.json"));
        File currentFile = chooser.showSaveDialog(anchorPane.getScene().getWindow());
        if (currentFile != null) {
            Main.getInstance().saveAs(currentFile.getPath());
        }
    }

    @FXML
    protected void onLoad(ActionEvent e) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Load Save File");
        chooser.getExtensionFilters().add(new ExtensionFilter("JSON File", "*.json"));
        File file = chooser.showOpenDialog(anchorPane.getScene().getWindow());
        if (file != null) {
            Main.getInstance().reloadSaveFile(file.getPath());
            reload(Main.getInstance().getUnselectedLocos());
        }
    }

    @FXML
    protected void onSave(ActionEvent e) {
        if (Main.getInstance().getSaveFilePath() != null)
            Main.getInstance().saveAs(Main.getInstance().getSaveFilePath());
        else {
            onSaveAs(e);
        }
    }

    @FXML
    protected void onClose(ActionEvent e) {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void onHelp(ActionEvent e) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Random Train Sim World");
        alert.setHeaderText("Random picker for Train Sim World");
        alert.setContentText("Homepage: https://github.com/FreddyYJ/RandomTrainSimWorld");

        alert.showAndWait();
    }

    @FXML
    protected void onShortcut(KeyEvent e) {
        if (e.isShortcutDown()) {
            if (e.getCode() == KeyCode.S) {
                onSave(null);
            }
        }
    }

    /**
     * Reload selection of {@link CheckBox}s of routes, locomotives and weathers.
     * @param save a {@link SaveLoco} object
     */
    public void reload(SaveLoco save) {
        ArrayList<String> routes=save.getRoute();
        for (int i = 0; i < this.routes.size(); i++) {
            if (routes.contains(((CheckBox)this.routes.get(i)).getText())) {
                ((CheckBox) this.routes.get(i)).setSelected(false);
            } else {
                ((CheckBox) this.routes.get(i)).setSelected(true);
            }
        }

        ArrayList<ArrayList<String>> locos=new ArrayList<>();
        for (int i=0;i<this.routes.size();i++){
            locos.add(save.getLocomotive(((CheckBox)this.routes.get(i)).getText()));
        }

        for (int i = 0; i < locos.size(); i++) {
            for (int j = 0; j < this.locos.get(i).size(); j++) {
                if (locos.get(i).contains(((CheckBox)this.locos.get(i).get(j)).getText())) {
                    ((CheckBox) this.locos.get(i).get(j)).setSelected(false);
                } else {
                    ((CheckBox) this.locos.get(i).get(j)).setSelected(true);
                }

            }
        }

        ArrayList<String> weathers=save.getWeather();
        for (int i = 0; i < this.weathers.size(); i++) {
            if (weathers.contains(((CheckBox)this.weathers.get(i)).getText())) {
                ((CheckBox) this.weathers.get(i)).setSelected(false);
            } else {
                ((CheckBox) this.weathers.get(i)).setSelected(true);
            }
        }

    }

    /**
     * Get {@link VBox} that has locomotive {@link CheckBox}s.
     * @param routeId route {@link CheckBox} ID
     * @return {@link VBox} that has locomotive {@link CheckBox}s
     */
    protected VBox getLocoBoxByID(String routeId) {
        List<Node> loco = boxLoco.getChildren();
        String locoId;
        String[] id = routeId.split("check");
        locoId = id[1];
        if (locoId.endsWith("Tsw2")){
            locoId=locoId.split("Tsw2")[0];
            locoId = "box" + locoId + "LocosTsw2";
        }
        else
            locoId = "box" + locoId + "Locos";
        for (int i = 0; i < loco.size(); i++) {
            if (loco.get(i).getId().equals(locoId))
                return (VBox) loco.get(i);
        }
        return null;
    }

    /**
     * Get route {@link CheckBox} with {@link VBox}.
     * @param locoId {@link VBox} ID
     * @return route {@link CheckBox}
     */
    protected CheckBox getRouteByVBox(VBox locoId) {
        String id;
        id = (locoId.getId().split("box"))[1];
        id = (id.split("Locos"))[0];
        if(id.endsWith("Tsw2")){
            id=id.split("Tsw2")[0];
            id="check" + id+"Tsw2";
        }
        else
            id = "check" + id;
        for (int i = 0; i < routes.size(); i++) {
            if (routes.get(i).getId().equals(id))
                return (CheckBox) routes.get(i);
        }
        return null;
    }

    /**
     * Get route {@link CheckBox} with route name.
     * @param name route name
     * @return route {@link CheckBox}
     */
    protected CheckBox getRouteByName(String name){
        for (int i=0;i<routes.size();i++){
            if (((CheckBox)routes.get(i)).getText().equals(name)) return (CheckBox)routes.get(i);
        }
        return null;
    }

    /**
     * Get locomotive {@link CheckBox} with locomotive name and route name.
     * @param name locomotive name
     * @param route route name
     * @return locomotive {@link CheckBox}
     */
    protected CheckBox getLocoByName(String name, String route) {
        int routeIndex=0;
        for (int i=0;i<routes.size();i++){
            if (((CheckBox)routes.get(i)).getText().equals(route))
                routeIndex=i;
        }
        List<Node> locoList = locos.get(routeIndex);
        for (int i = 0; i < locoList.size(); i++) {
            if (((CheckBox) locoList.get(i)).getText().equals(name))
                return (CheckBox) locoList.get(i);
        }
        return null;
    }

    /**
     * Get weather {@link CheckBox} with weather name.
     * @param name weather name
     * @return weather {@link CheckBox}
     */
    protected CheckBox getWeatherByName(String name) {
        for (int i = 0; i < weathers.size(); i++) {
            if (((CheckBox) weathers.get(i)).getText().equals(name))
                return (CheckBox) weathers.get(i);
        }
        return null;
    }
}
