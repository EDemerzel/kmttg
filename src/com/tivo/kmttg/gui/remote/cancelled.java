package com.tivo.kmttg.gui.remote;

import java.net.URL;
import java.util.ResourceBundle;

import com.tivo.kmttg.gui.table.TableUtil;
import com.tivo.kmttg.gui.table.cancelledTable;
import com.tivo.kmttg.main.config;
import com.tivo.kmttg.main.jobData;
import com.tivo.kmttg.main.jobMonitor;
import com.tivo.kmttg.rpc.rnpl;
import com.tivo.kmttg.util.log;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class cancelled implements Initializable {
   @FXML public VBox panel = null;
   public cancelledTable tab = null;
   @FXML public ChoiceBox<String> tivo = null;
   @FXML public Button refresh = null;
   @FXML public Button autoresolve = null;
   @FXML public CheckBox includeHistory = null;
   @FXML public Button record = null;
   @FXML public Button explain = null;
   
   @FXML private Button refresh_todo;

   @FXML private void refreshCB(ActionEvent e) {
            // Refresh will not record list
            tab.clear();
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0) {
               jobData job = new jobData();
               job.source        = tivoName;
               job.tivoName      = tivoName;
               job.type          = "remote";
               job.name          = "Remote";
               job.remote_cancel = true;
               job.cancelled     = tab;
               jobMonitor.submitNewJob(job);
            }
   }
   
   @FXML private void recordCB(ActionEvent e) {
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0)
               tab.recordSingle(tivoName);
    }
   
   @FXML private void explainCB(ActionEvent e) {
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0) {
               int selected[] = TableUtil.GetSelectedRows(tab.TABLE);
               if (selected.length > 0) {
                  tab.getConflictDetails(tivoName, selected[0]);
               }
            }
    }

   @FXML private void refresh_todoCB(ActionEvent e) {
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0) {
               Task<Void> task = new Task<Void>() {
                  @Override public Void call() {
                     log.warn("Refreshing ToDo list for Will Not Record matches...");
                     util.all_todo = util.getTodoLists();
                     log.warn("Refresh ToDo list for Will Not Record matches completed.");
                     return null;
                  }
               };
               new Thread(task).start();
            }
    }
   
   @FXML private void autoresolveCB(ActionEvent e) {
            autoresolve.setDisable(true);
            Task<Void> task = new Task<Void>() {
               @Override public Void call() {
                  rnpl.AutomaticConflictsHandler();
                  autoresolve.setDisable(false);
                  return null;
               }
            };
            new Thread(task).start();
    }
   @Override
   public void initialize(URL location, ResourceBundle resources) {
      
      // Cancelled table items            
//      HBox row1 = new HBox();
//      row1.setSpacing(5);
//      row1.setAlignment(Pos.CENTER_LEFT);
//      row1.setPadding(new Insets(5,0,0,5));
//      
//      Label title = new Label("Not Record list");
//      
//      Label tivo_label = new Label();
//      
//      tivo = new ChoiceBox<String>();
      tivo.valueProperty().addListener(new ChangeListener<String>() {
         @Override public void changed(ObservableValue<? extends String> ov, String oldVal, String newVal) {
            if (newVal != null && config.gui.remote_gui != null) {
               config.gui.remote_gui.updateButtonStates(newVal, "Won't Record");
            }
         }
      });
      tivo.setTooltip(tooltip.getToolTip("tivo_cancel"));

//      refresh = new Button("Refresh");
      refresh.setTooltip(tooltip.getToolTip("refresh_cancel_top"));

//      record = new Button("Record");
      record.setTooltip(tooltip.getToolTip("record_cancel"));

//      explain = new Button("Explain");
      explain.setTooltip(tooltip.getToolTip("explain_cancel"));

//      Button refresh_todo = new Button("Refresh ToDo");
      refresh_todo.setTooltip(tooltip.getToolTip("refresh_todo"));
      
//      autoresolve = new Button("Autoresolve");
      autoresolve.setTooltip(tooltip.getToolTip("autoresolve"));
      
//      includeHistory = new CheckBox("Include History");
//      includeHistory.setSelected(false);
      includeHistory.setTooltip(tooltip.getToolTip("includeHistory_cancel"));
      
//      row1.getChildren().add(title);
//      row1.getChildren().add(tivo_label);
//      row1.getChildren().add(tivo);
//      row1.getChildren().add(refresh);
//      row1.getChildren().add(record);
//      row1.getChildren().add(explain);
//      row1.getChildren().add(refresh_todo);
//      row1.getChildren().add(autoresolve);
//      row1.getChildren().add(includeHistory);
      
      tab = new cancelledTable();
      VBox.setVgrow(tab.TABLE, Priority.ALWAYS); // stretch vertically
      
//      panel = new VBox();
//      panel.setSpacing(1);
//      panel.setPadding(new Insets(0,0,0,5));      
//      panel.getChildren().addAll(row1, tab.TABLE);
      panel.getChildren().add(tab.TABLE);
      
   }
}
