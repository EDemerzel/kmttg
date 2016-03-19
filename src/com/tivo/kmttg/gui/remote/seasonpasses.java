package com.tivo.kmttg.gui.remote;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Stack;

import com.tivo.kmttg.JSON.JSONException;
import com.tivo.kmttg.JSON.JSONObject;
import com.tivo.kmttg.gui.table.TableUtil;
import com.tivo.kmttg.gui.table.spTable;
import com.tivo.kmttg.main.config;
import com.tivo.kmttg.main.jobData;
import com.tivo.kmttg.main.jobMonitor;
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

public class seasonpasses implements Initializable {
   @FXML public VBox panel = null;
   public spTable tab = null;
   @FXML public ChoiceBox<String> tivo = null;
   @FXML public Button copy = null;
   @FXML public Button conflicts = null;
   @FXML public Button modify = null;
   @FXML public Button upcoming = null;   
   @FXML public Button reorder = null;
   
   @FXML private Button refresh;
   @FXML private Button save;
   @FXML private Button load;
   @FXML private Button export;
   @FXML private Button delete;
   
   @Override
   public void initialize(URL location, ResourceBundle resources) {
   
      // Season Passes Tab items      
//      HBox row1 = new HBox();
//      row1.setSpacing(5);
//      row1.setAlignment(Pos.CENTER_LEFT);
//      row1.setPadding(new Insets(5,0,0,5));
//
//      Label title = new Label("Season Passes");
//
//      Label tivo_label = new Label();
//
//      tivo = new ChoiceBox<String>();
      tivo.valueProperty().addListener(new ChangeListener<String>() {
         @Override public void changed(ObservableValue<? extends String> ov, String oldVal, String newVal) {
            if (newVal != null && config.gui.remote_gui != null) {
               TableUtil.clear(tab.TABLE);
               String tivoName = newVal;
               config.gui.remote_gui.updateButtonStates(tivoName, "Season Passes");
               if (tab.tivo_data.containsKey(tivoName))
                  tab.AddRows(tivoName, tab.tivo_data.get(tivoName));
               tab.updateLoadedStatus();
            }
         }
      });
      tivo.setTooltip(tooltip.getToolTip("tivo_sp"));

//      Button refresh = new Button("Refresh");
      refresh.setTooltip(tooltip.getToolTip("refresh_sp"));
//      Button save = new Button("Save...");
      save.setTooltip(tooltip.getToolTip("save_sp"));
//      Button load = new Button("Load...");
      load.setTooltip(tooltip.getToolTip("load_sp"));
//      Button export = new Button("Export...");
      export.setTooltip(tooltip.getToolTip("export_sp"));
//      copy = new Button("Copy");
      copy.setTooltip(tooltip.getToolTip("copy_sp"));
//      Button delete = new Button("Delete");
      delete.setTooltip(tooltip.getToolTip("delete_sp"));
//      modify = new Button("Modify");
      modify.setTooltip(tooltip.getToolTip("modify_sp"));
//      reorder = new Button("Re-order");
      reorder.setTooltip(tooltip.getToolTip("reorder_sp"));
//      upcoming = new Button("Upcoming");
      upcoming.setTooltip(tooltip.getToolTip("upcoming_sp"));
//      conflicts = new Button("Conflicts");
      conflicts.setTooltip(tooltip.getToolTip("conflicts_sp"));
//      row1.getChildren().add(title);
//      row1.getChildren().add(tivo_label);
//      row1.getChildren().add(tivo);
//      row1.getChildren().add(refresh);
//      row1.getChildren().add(save);
//      row1.getChildren().add(load);
//      row1.getChildren().add(export);
//      row1.getChildren().add(delete);
//      row1.getChildren().add(copy);
//      row1.getChildren().add(modify);
//      row1.getChildren().add(reorder);
//      row1.getChildren().add(upcoming);
//      row1.getChildren().add(conflicts);

      tab = new spTable();
      VBox.setVgrow(tab.TABLE, Priority.ALWAYS); // stretch vertically

//      panel = new VBox();
//      panel.setSpacing(1);
//      panel.getChildren().addAll(row1, tab.TABLE);      
      panel.getChildren().add(tab.TABLE);
   }
   @FXML private void refreshCB(ActionEvent e) {
            // Refresh SP list
            TableUtil.clear(tab.TABLE);
            tab.setLoaded(false);
            SPListCB(tivo.getValue());
   }

   @FXML private void saveCB(ActionEvent e) {
	   final Window frame = tivo.getScene().getWindow();
            // Save SP data to a file
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0) {
               if (tab.isTableLoaded()) {
                  log.error("Cannot save loaded Season Passes");
                  return;
               }  else {
                  config.gui.remote_gui.Browser.getExtensionFilters().clear();
                  config.gui.remote_gui.Browser.getExtensionFilters().addAll(new ExtensionFilter("SP Files", "*.sp"));
                  config.gui.remote_gui.Browser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ALL FILES", "*"));
                  config.gui.remote_gui.Browser.setTitle("Save to file");
                  config.gui.remote_gui.Browser.setInitialDirectory(new File(config.programDir));
                  config.gui.remote_gui.Browser.setInitialFileName(tivoName + ".sp");
                  final File selectedFile = config.gui.remote_gui.Browser.showSaveDialog(frame);
                  if (selectedFile != null) {
                     tab.SPListSave(tivoName, selectedFile.getAbsolutePath());
                  }
               }
            }
   }

   @FXML private void loadCB(ActionEvent e) {
	   final Window frame = tivo.getScene().getWindow();
            // Load SP data from a file
            config.gui.remote_gui.Browser.getExtensionFilters().clear();
            config.gui.remote_gui.Browser.getExtensionFilters().addAll(new ExtensionFilter("SP Files", "*.sp"));
            config.gui.remote_gui.Browser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ALL FILES", "*"));
            config.gui.remote_gui.Browser.setTitle("Load from file");
            config.gui.remote_gui.Browser.setInitialDirectory(new File(config.programDir));
            config.gui.remote_gui.Browser.setInitialFileName(null);
            final File selectedFile = config.gui.remote_gui.Browser.showOpenDialog(frame);
            if (selectedFile != null) {
               tab.SPListLoad(selectedFile.getAbsolutePath());
            }
   }

   @FXML private void exportCB(ActionEvent e) {
	   final Window frame = tivo.getScene().getWindow();
            // Export SP data to a file in csv format
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0) {
               if (tab.isTableLoaded()) {
                  log.error("Cannot export loaded Season Passes");
                  return;
               }  else {
                  config.gui.remote_gui.Browser.getExtensionFilters().clear();
                  config.gui.remote_gui.Browser.getExtensionFilters().addAll(new ExtensionFilter("CSV Files", "*.csv"));
                  config.gui.remote_gui.Browser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ALL FILES", "*"));
                  config.gui.remote_gui.Browser.setTitle("Export to csv file");
                  config.gui.remote_gui.Browser.setInitialDirectory(new File(config.programDir));
                  config.gui.remote_gui.Browser.setInitialFileName(tivoName + "" + ".csv");
                  final File selectedFile = config.gui.remote_gui.Browser.showSaveDialog(frame);
                  if (selectedFile != null) {
                     tab.SPListExport(tivoName, selectedFile.getAbsolutePath());
                  }
               }
            }
   }

   @FXML private void copyCB(ActionEvent e) {
            // Copy selected SPs to a TiVo
            // Build list of eligible TiVos
            String thisTivo = tivo.getValue();
            Stack<String> all = config.getTivoNames();
            for (int i=0; i<all.size(); ++i) {
               String tivo = all.get(i);
               if (! config.rpcEnabled(tivo) && ! config.mindEnabled(tivo)) {
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
               tab.SPListCopy(tivoName);
            }
   }

   @FXML private void deleteCB(ActionEvent e) {
            tab.SPListDelete();
   }

   @FXML private void modifyCB(ActionEvent e) {
            // Modify selected SP
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0) {
               if (tab.isTableLoaded()) {
                  log.error("Cannot modify loaded Season Passes");
                  return;
               }  else {
                  tab.SPListModify(tivoName);
               }
            }
   }

   @FXML private void reorderCB(ActionEvent e) {
            // Re-prioritize SPs on TiVo to match current table row order
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0) {
               if (tab.isTableLoaded()) {
                  log.error("Cannot re-order loaded Season Passes");
                  return;
               }  else {
                  tab.SPReorderCB(tivoName);
               }
            }
   }

   @FXML private void upcomingCB(ActionEvent e) {
            int selected[] = TableUtil.GetSelectedRows(tab.TABLE);
            if (selected.length > 0) {
               int row = selected[0];
               JSONObject json = tab.GetRowData(row);
               if (json.has("__upcoming")) {
                  // Get upcoming SP episodes and display in ToDo table
                  config.gui.remote_gui.todo_tab.tab.clear();
                  config.gui.remote_gui.todo_tab.label.setText("");
                  String tivoName = tivo.getValue();
                  try {
                     if (tivoName != null && tivoName.length() > 0) {
                        jobData job = new jobData();
                        job.source          = tivoName;
                        job.tivoName        = tivoName;
                        job.type            = "remote";
                        job.name            = "Remote";
                        job.remote_upcoming = true;
                        job.rnpl            = json.getJSONArray("__upcoming");
                        job.todo            = config.gui.remote_gui.todo_tab.tab;
                        jobMonitor.submitNewJob(job);
                     }
                  } catch (JSONException e1) {
                     log.error("upcoming error - " + e1.getMessage());
                  }
               } else {
                  log.warn("No upcoming episodes scheduled for selected Season Pass");
               }
            }
   }

   @FXML private void conflictsCB(ActionEvent e) {
            int selected[] = TableUtil.GetSelectedRows(tab.TABLE);
            if (selected.length > 0) {
               int row = selected[0];
               JSONObject json = tab.GetRowData(row);
               if (json.has("__conflicts")) {
                  // Get conflict SP episodes and display in Won't Record table
                  config.gui.remote_gui.cancel_tab.tab.clear();
                  String tivoName = tivo.getValue();
                  try {
                     if (tivoName != null && tivoName.length() > 0) {
                        jobData job = new jobData();
                        job.source           = tivoName;
                        job.tivoName         = tivoName;
                        job.type             = "remote";
                        job.name             = "Remote";
                        job.remote_conflicts = true;
                        job.rnpl             = json.getJSONArray("__conflicts");
                        job.cancelled        = config.gui.remote_gui.cancel_tab.tab;
                        jobMonitor.submitNewJob(job);
                     }
                  } catch (JSONException e1) {
                     log.error("conflicts error - " + e1.getMessage());
                  }
               } else {
                  log.warn("No conflicting episodes for selected Season Pass");
               }
            }
   }
   
   // Submit remote SP request to Job Monitor
   public void SPListCB(String tivoName) {
      jobData job = new jobData();
      job.source      = tivoName;
      job.tivoName    = tivoName;
      job.type        = "remote";
      job.name        = "Remote";
      job.remote_sp   = true;
      job.sp          = tab;
      jobMonitor.submitNewJob(job);
   }

}
