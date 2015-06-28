package com.tivo.kmttg.gui.remote;

import com.tivo.kmttg.gui.MyButton;
import com.tivo.kmttg.gui.table.TableUtil;
import com.tivo.kmttg.gui.table.deletedTable;
import com.tivo.kmttg.main.config;
import com.tivo.kmttg.main.jobData;
import com.tivo.kmttg.main.jobMonitor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class deleted {
   public VBox panel = null;
   public deletedTable tab = null;
   public ComboBox<String> tivo = null;
   public MyButton refresh = null;
   public Label label = null;
   public MyButton recover = null;
   public MyButton permDelete = null;  

   public deleted(final Stage frame) {
      
      // Deleted table items      
      HBox row1 = new HBox();
      row1.setSpacing(5);
      row1.setAlignment(Pos.CENTER_LEFT);
      row1.setPadding(new Insets(5,0,0,5));
      
      Label title = new Label("Recently Deleted list");
      
      Label tivo_label = new Label();
      
      tivo = new ComboBox<String>();
      tivo.valueProperty().addListener(new ChangeListener<String>() {
         @Override public void changed(ObservableValue<? extends String> ov, String oldVal, String newVal) {
            if (newVal != null && config.gui.remote_gui != null) {               
               // TiVo selection changed for Deleted tab
               TableUtil.clear(tab.TABLE);
               label.setText("");
               String tivoName = newVal;
               config.gui.remote_gui.updateButtonStates(tivoName, "Deleted");
               if (tab.tivo_data.containsKey(tivoName))
                  tab.AddRows(tivoName, tab.tivo_data.get(tivoName));
            }
         }
      });
      tivo.setTooltip(tooltip.getToolTip("tivo_deleted"));

      refresh = new MyButton("Refresh");
      refresh.setPadding(util.smallButtonInsets());
      refresh.setTooltip(tooltip.getToolTip("refresh_deleted"));
      refresh.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            // Refresh deleted list
            TableUtil.clear(tab.TABLE);
            label.setText("");
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0) {
               jobData job = new jobData();
               job.source         = tivoName;
               job.tivoName       = tivoName;
               job.type           = "remote";
               job.name           = "Remote";
               job.remote_deleted = true;
               job.deleted        = tab;
               jobMonitor.submitNewJob(job);
            }
         }
      });

      recover = new MyButton("Recover");
      recover.setPadding(util.smallButtonInsets());
      recover.setTooltip(tooltip.getToolTip("recover_deleted"));
      recover.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0) {
               tab.recoverSingle(tivoName);
            }
         }
      });

      permDelete = new MyButton("Permanently Delete");
      permDelete.setPadding(util.smallButtonInsets());
      permDelete.setTooltip(tooltip.getToolTip("permDelete_deleted"));
      permDelete.setOnAction(new EventHandler<ActionEvent>() {
         public void handle(ActionEvent e) {
            String tivoName = tivo.getValue();
            if (tivoName != null && tivoName.length() > 0) {
               tab.permanentlyDelete(tivoName);
            }
         }
      });
      
      label = new Label();
      
      row1.getChildren().add(title);
      row1.getChildren().add(tivo_label);
      row1.getChildren().add(tivo);
      row1.getChildren().add(refresh);
      row1.getChildren().add(recover);
      row1.getChildren().add(permDelete);
      row1.getChildren().add(label);
      
      tab = new deletedTable();
      VBox.setVgrow(tab.TABLE, Priority.ALWAYS); // stretch vertically
      
      panel = new VBox();
      panel.setSpacing(1);
      panel.getChildren().addAll(row1, tab.TABLE);
      
   }
}
