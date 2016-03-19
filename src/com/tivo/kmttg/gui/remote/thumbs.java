package com.tivo.kmttg.gui.remote;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Stack;

import com.tivo.kmttg.gui.table.TableUtil;
import com.tivo.kmttg.gui.table.thumbsTable;
import com.tivo.kmttg.main.config;
import com.tivo.kmttg.util.log;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.FileChooser.ExtensionFilter;

public class thumbs implements Initializable {
   @FXML public VBox panel = null;
   public thumbsTable tab = null;
   @FXML public Button refresh = null;
   @FXML public Button copy = null;
   @FXML public Button update = null;
   @FXML public Label label = null;
   @FXML public ChoiceBox<String> tivo = null;
   
   @FXML private Button load;
   @FXML private Button save;
   
   @Override
   public void initialize(URL location, ResourceBundle resources) {
      
      // Thumbs tab items      
//      HBox row1 = new HBox();
//      row1.setSpacing(5);
//      row1.setAlignment(Pos.CENTER_LEFT);
//      row1.setPadding(new Insets(5,0,0,5));
//      
//      Label title = new Label("Thumb Ratings");
//      
//      Label tivo_label = new Label();
//      
//      tivo = new ChoiceBox<String>();
      tivo.valueProperty().addListener(new ChangeListener<String>() {
         @Override public void changed(ObservableValue<? extends String> ov, String oldVal, String newVal) {
            if (newVal != null && config.gui.remote_gui != null) {                
               // TiVo selection changed for Thumbs tab
               TableUtil.clear(tab.TABLE);
               label.setText("");
               String tivoName = newVal;
               config.gui.remote_gui.updateButtonStates(tivoName, "Thumbs");
               if (tab.tivo_data.containsKey(tivoName))
                  tab.AddRows(tivoName, tab.tivo_data.get(tivoName));
               tab.updateLoadedStatus();
            }
         }
      });
      tivo.setTooltip(tooltip.getToolTip("tivo_thumbs"));

//      Button save = new Button("Save...");
      save.setTooltip(tooltip.getToolTip("save_thumbs"));
      
//      Button load = new Button("Load...");
      load.setTooltip(tooltip.getToolTip("load_thumbs"));
//      copy = new Button("Copy");
      copy.setTooltip(tooltip.getToolTip("copy_thumbs"));
//      refresh = new Button("Refresh");
      refresh.setTooltip(tooltip.getToolTip("refresh_thumbs"));
//      update = new Button("Modify");
      update.setTooltip(tooltip.getToolTip("update_thumbs"));
//      label = new Label();
//            
//      row1.getChildren().add(title);
//      row1.getChildren().add(tivo_label);
//      row1.getChildren().add(tivo);
//      row1.getChildren().add(refresh);
//      row1.getChildren().add(save);
//      row1.getChildren().add(load);
//      row1.getChildren().add(copy);
//      row1.getChildren().add(update);
//      row1.getChildren().add(label);
      
      tab = new thumbsTable();
      VBox.setVgrow(tab.TABLE, Priority.ALWAYS); // stretch vertically
      
//      panel = new VBox();
//      panel.setSpacing(1);
//      panel.getChildren().addAll(row1, tab.TABLE);      
      panel.getChildren().add(tab.TABLE);
   }
   
   @FXML private void saveCB(ActionEvent e) {
	   final Window frame = tivo.getScene().getWindow();
            // Save thumbs list
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0) {
               if (tab.isTableLoaded()) {
                  log.error("Cannot save loaded Thumbs");
                  return;
               }  else {
                  config.gui.remote_gui.Browser.getExtensionFilters().clear();
                  config.gui.remote_gui.Browser.getExtensionFilters().addAll(new ExtensionFilter("Thumbs Files", "*.thumbs"));
                  config.gui.remote_gui.Browser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ALL FILES", "*"));
                  config.gui.remote_gui.Browser.setTitle("Save to file");
                  config.gui.remote_gui.Browser.setInitialDirectory(new File(config.programDir));
                  config.gui.remote_gui.Browser.setInitialFileName(tivoName + ".thumbs");
                  final File selectedFile = config.gui.remote_gui.Browser.showSaveDialog(frame);
                  if (selectedFile != null) {
                     tab.saveThumbs(tivoName, selectedFile.getAbsolutePath());
                  }
               }
            }
   }

   @FXML private void loadCB(ActionEvent e) {
	   final Window frame = tivo.getScene().getWindow();
            // Load thumbs list
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0) {
               config.gui.remote_gui.Browser.getExtensionFilters().clear();
               config.gui.remote_gui.Browser.getExtensionFilters().addAll(new ExtensionFilter("Thumbs Files", "*.thumbs"));
               config.gui.remote_gui.Browser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ALL FILES", "*"));
               config.gui.remote_gui.Browser.setTitle("Load thumbs file");
               config.gui.remote_gui.Browser.setInitialDirectory(new File(config.programDir));
               config.gui.remote_gui.Browser.setInitialFileName(tivoName + ".thumbs");
               final File selectedFile = config.gui.remote_gui.Browser.showOpenDialog(frame);
               if (selectedFile != null) {
                  label.setText("");
                  tab.loadThumbs(selectedFile.getAbsolutePath());
               }
            }
   }

   @FXML private void copyCB(ActionEvent e) {
	   final Window frame = tivo.getScene().getWindow();
            // Copy selected thumbs to a TiVo
            // Build list of eligible TiVos (series 4 and later and no Minis)
            String thisTivo = tivo.getValue();
            Stack<String> all = config.getTivoNames();
            for (int i=0; i<all.size(); ++i) {
               String tivo = all.get(i);
               if (! config.rpcEnabled(tivo)) {
                  all.remove(i);
                  continue;
               }
               if (! config.nplCapable(tivo)) {
                  all.remove(i);
                  continue;
               }
            }
            
            // Prompt user to choose a TiVo
            ChoiceDialog<String> dialog = new ChoiceDialog<String>(all.get(0), all);
            dialog.setTitle("Copy To");
            dialog.setHeaderText("Choose which TiVo to copy to");
            dialog.setContentText("TiVo:");
            String tivoName = null;
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent())
               tivoName = result.get();
            if (tivoName != null && tivoName.length() > 0) {
               if (tivoName.equals(thisTivo)) {
                  // Don't copy to self unless in loaded state
                  if (! tab.isTableLoaded()) {
                     log.error("Destination TiVo is same as source TiVo: " + tivoName);
                     return;
                  }
               }
               tab.copyThumbs(tivoName);
            }
   }

   @FXML private void refreshCB(ActionEvent e) {
	   final Window frame = tivo.getScene().getWindow();
            // Refresh thumbs list
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0) {
               label.setText("");
               tab.refreshThumbs(tivoName);
            }
   }

   @FXML private void updateCB(ActionEvent e) {
	   final Window frame = tivo.getScene().getWindow();
            // Update thumbs list
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0)
               label.setText("");
               tab.updateThumbs(tivoName);
   }
}
