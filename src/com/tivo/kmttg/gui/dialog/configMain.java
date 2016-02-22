package com.tivo.kmttg.gui.dialog;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Stack;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.tivo.kmttg.gui.MyTooltip;
import com.tivo.kmttg.httpserver.kmttgServer;
import com.tivo.kmttg.main.beacon;
import com.tivo.kmttg.main.config;
import com.tivo.kmttg.main.jobData;
import com.tivo.kmttg.main.jobMonitor;
import com.tivo.kmttg.main.mdns;
import com.tivo.kmttg.task.autotune;
import com.tivo.kmttg.task.custom;
import com.tivo.kmttg.util.debug;
import com.tivo.kmttg.util.file;
import com.tivo.kmttg.util.log;
import com.tivo.kmttg.util.string;

public class configMain implements Initializable {
   private static Stack<TextField> errors = new Stack<TextField>();
   private static String textbg_default = null;
   private static double pos_x = -1;
   private static double pos_y = -1;
   
   @FXML private Button add = null;
   @FXML private Button del = null;
   @FXML private Button share_add = null;
   @FXML private Button share_del = null;
   @FXML private Button OK = null;
   @FXML private Button CANCEL = null;
   @FXML private Button autotune_test = null;
   private static configMain singleton = null;
   private static ResourceBundle bundle;
   private static Stage dialog = null;
   @FXML private ChoiceBox<String> tivos = null;
   @FXML private ChoiceBox<String> shares = null;
   @FXML private CheckBox remove_tivo = null;
   @FXML private CheckBox remove_comcut = null;
   @FXML private CheckBox remove_comcut_mpeg = null;
   @FXML private CheckBox remove_mpeg = null;
   @FXML private CheckBox QSFixBackupMpegFile = null;
   @FXML private CheckBox download_check_length = null;
   @FXML private CheckBox check_space = null;
   @FXML private CheckBox beacon = null;
   @FXML private CheckBox npl_when_started = null;
   @FXML private CheckBox showHistoryInTable = null;
   @FXML private CheckBox UseOldBeacon = null;
   @FXML private CheckBox download_time_estimate = null;
   @FXML private CheckBox UseAdscan = null;
   @FXML private CheckBox VRD = null;
   @FXML private CheckBox VrdReview = null;
   @FXML private CheckBox comskip_review = null;
   @FXML private CheckBox VrdReview_noCuts = null;
   @FXML private CheckBox VrdQsFilter = null;
   @FXML private CheckBox VrdDecrypt = null;
   @FXML private CheckBox DsdDecrypt = null;
   @FXML private CheckBox tivolibreDecrypt = null;
   @FXML private CheckBox tivolibreCompat = null;
   @FXML private CheckBox httpserver_enable = null;
   @FXML private CheckBox httpserver_share_filter = null;
   @FXML private CheckBox VrdEncode = null;
   @FXML private CheckBox VrdAllowMultiple = null;
   @FXML private CheckBox VrdCombineCutEncode = null;
   @FXML private CheckBox VrdQsfixMpeg2ps = null;
   @FXML private CheckBox VrdOneAtATime = null;
   @FXML private CheckBox TSDownload = null;
   @FXML private CheckBox TivoWebPlusDelete = null;
   @FXML private CheckBox rpcDelete = null;
   @FXML private CheckBox rpcOld = null;
   @FXML private CheckBox HideProtectedFiles = null;
   @FXML private CheckBox OverwriteFiles = null;
   @FXML private CheckBox DeleteFailedDownloads = null;
   @FXML private CheckBox toolTips = null;
   @FXML private CheckBox slingBox = null;
   @FXML private CheckBox tableColAutoSize = null;
   @FXML private CheckBox jobMonitorFullPaths = null;
   @FXML private CheckBox autotune_enabled = null;
   @FXML private CheckBox combine_download_decrypt = null;
   @FXML private CheckBox single_download = null;
   @FXML private CheckBox rpcnpl = null;
   @FXML private CheckBox enableRpc = null;
   @FXML private CheckBox persistQueue = null;
   @FXML private TextField VRDexe = null;
   @FXML private TextField tivo_name = null;
   @FXML private TextField tivo_ip = null;
   @FXML private TextField share_name = null;
   @FXML private TextField share_dir = null;
   @FXML private TextField files_path = null;
   @FXML private TextField MAK = null;
   @FXML private TextField FontSize = null;
   @FXML private TextField file_naming = null;
   @FXML private TextField tivo_output_dir = null;
   @FXML private TextField mpeg_output_dir = null;
   @FXML private TextField qsfixDir = null;
   @FXML private TextField mpeg_cut_dir = null;
   @FXML private TextField encode_output_dir = null;
   @FXML private TextField tivodecode = null;
   /** Included so it can be hidden */
   @FXML private Label dsd_label = null;
   @FXML private TextField dsd = null;
   @FXML private TextField ffmpeg = null;
   @FXML private TextField mediainfo = null;
   @FXML private TextField mencoder = null;
   @FXML private TextField handbrake = null;
   @FXML private TextField comskip = null;
   @FXML private TextField comskip_ini = null;
   @FXML private TextField wan_http_port = null;
   @FXML private TextField wan_https_port = null;
   @FXML private TextField wan_rpc_port = null;
   @FXML private TextField limit_npl_fetches = null;
   @FXML private TextField active_job_limit = null;
   @FXML private TextField t2extract = null;
   //private static TextField t2extract_args = null;
   /** included so we can hide it */
   @FXML private Label ccextractor_label = null;
   @FXML private TextField ccextractor = null;
   @FXML private TextField AtomicParsley = null;
   @FXML private TextField disk_space = null;
   @FXML private TextField customCommand = null;
   @FXML private TextField toolTipsDelay = null;
   @FXML private TextField toolTipsTimeout = null;
   @FXML private TextField cpu_cores = null;
   @FXML private TextField download_tries = null;
   @FXML private TextField download_retry_delay = null;
   @FXML private TextField download_delay = null;
   @FXML private TextField metadata_entries = null;
   @FXML private TextField httpserver_port = null;
   @FXML private TextField httpserver_cache = null;
   @FXML private TextField autoLogSizeMB = null;
   @FXML private TextField pyTivo_host = null;
   @FXML private TextField web_query = null;
   @FXML private TextField web_browser = null;
   /** included so it can be hidden for certain cases */
   @FXML private Label web_browser_label = null;
   @FXML private TextField tivo_username = null;
   @FXML private TextField tivo_password = null;
   @FXML private TextField pyTivo_config = null;
   @FXML private TextField autotune_channel_interval = null;
   @FXML private TextField autotune_button_interval = null;
   @FXML private TextField autotune_chan1 = null;
   @FXML private TextField autotune_chan2 = null;
   @FXML private ChoiceBox<String> MinChanDigits = null;
   @FXML private ChoiceBox<String> pyTivo_tivo = null;
   @FXML private ChoiceBox<String> pyTivo_files = null;
   @FXML private ChoiceBox<String> metadata_files = null;
   @FXML private ChoiceBox<String> keywords = null;
   @FXML private ChoiceBox<String> customFiles = null;
   @FXML private ChoiceBox<String> autotune_tivoName = null;
   @FXML private ChoiceBox<String> lookAndFeel = null;
   private FileChooser FileBrowser = null;
   private DirectoryChooser DirBrowser = null;
   @FXML private TabPane tabbed_panel = null;
      
   public static void display(Stage frame) {
      debug.print("frame=" + frame);
      // Create dialog if not already created
      if (dialog == null) {
         create(frame);
         
         // all other basic initialization is done when "initialize" is
         // automatically called when the fxml file is loaded.
      }
      
      // Update component settings to current configuration
      singleton.read();
      
      // Clear out any error highlights
      singleton.clearTextFieldErrors();
      
      // Display the dialog
      if (pos_x != -1)
         dialog.setX(pos_x);
      if (pos_y != -1)
         dialog.setY(pos_y);
      dialog.show();
   }
   
   // Paint text field background to indicate an error setting
   private void textFieldError(TextField f, String message) {
      debug.print("f=" + f + " message=" + message);
      log.error(message);
      f.setStyle("-fx-background-color: " + config.gui.getWebColor(config.lightRed));
      errors.add(f);
      // Set tab background of this text field to error color as well
      Tab tab = getParentTab(f);
      if (f != null)
         tab.setStyle("-fx-background-color: " + config.gui.getWebColor(config.lightRed));         
   }
   
   private Tab getParentTab(Node node) {
      for (Tab tab : tabbed_panel.getTabs()) {
         for (Node n : tab.getContent().lookupAll("*")) {
            if (n.equals(node))
               return tab;
         }
      }
      return null;
   }
   
   // Clear all text field and tab background color error paint settings
   private void clearTextFieldErrors() {
      debug.print("");
      if (errors.size() > 0) {
         for (int i=0; i<errors.size(); i++) {
            errors.get(i).setStyle(textbg_default);
         }
         errors.clear();
      }
      // Clear tab background settings as well
      for (int i=0; i<tabbed_panel.getTabs().size(); ++i)
         tabbed_panel.getTabs().get(i).setStyle(textbg_default);
   }
   
   // Callback for OK button
   @FXML private void okCB() {
      debug.print("");
      clearTextFieldErrors();
      int errors = write();
      if (errors > 0) {
         Alert alert = new Alert(AlertType.CONFIRMATION);
         // Hack to default to CANCEL button
         DialogPane pane = alert.getDialogPane();
         for ( ButtonType t : alert.getButtonTypes() )
            ( (Button) pane.lookupButton(t) ).setDefaultButton( t == ButtonType.CANCEL );
         alert.setTitle(bundle.getString("OK_title"));
         config.gui.setFontSize(alert, config.FontSize);
         alert.setContentText("" + errors + bundle.getString("OK_content"));
         Optional<ButtonType> result = alert.showAndWait();
         if (result.get() == ButtonType.OK) {
            config.save();
            pos_x = dialog.getX(); pos_y = dialog.getY();
            dialog.hide();
         }
      } else {
         config.save();
         pos_x = dialog.getX(); pos_y = dialog.getY();
         dialog.hide();
      }
      config.gui.refreshOptions(true);
   }
   
   // Callback for tivo add button
   @FXML private void addCB() {
      debug.print("");
      // Add name=ip to tivos ChoiceBox
      String name = string.removeLeadingTrailingSpaces(tivo_name.getText());
      String ip = string.removeLeadingTrailingSpaces(tivo_ip.getText());
      if ( name.length() == 0) {
         log.error("Enter a name in the 'Tivo Name' field");
         return;
      }
      if ( ip.length() == 0) {
         log.error("Enter an ip address in the 'Tivo IP#' field");
         return;
      }
      addTivo(name, ip);      
   }
   
   public static void addTivo(String name, String ip) {  
	   singleton.addTiVo(name,  ip);
   }
   
   private void addTiVo(String name, String ip) {
      debug.print("name=" + name + " ip=" + ip);
      if (dialog == null || tivos == null) return;
      String value = name + "=" + ip;
      // Don't add duplicate value
      Boolean doit = true;
      int count = tivos.getItems().size();
      if (count > 0) {
         for (int i=0; i<count; i++) {
            String s = tivos.getItems().get(i);
            if (s.equals(value))
               doit = false;
         }
      }
      if (doit) {
         tivos.getItems().add(value);
         tivos.setValue(value);
      }
   }
   
   // Callback for share add button
   @FXML private void share_addCB() {
      debug.print("");
      // Add name=dir to shares ChoiceBox
      String name = string.removeLeadingTrailingSpaces(share_name.getText());
      String dir = string.removeLeadingTrailingSpaces(share_dir.getText());
      if ( name.length() == 0) {
         log.error("Enter a name in the 'Share Name' field");
         return;
      }
      if ( dir.length() == 0) {
         log.error("Enter a valid directory in 'Share Directory' field");
         return;
      }
      addShare(name, dir);      
   }
   
   public void addShare(String name, String dir) {  
      debug.print("name=" + name + " dir=" + dir);
      if (dialog == null || shares == null) return;
      String value = name + "=" + dir;
      // Don't add duplicate value
      Boolean doit = true;
      int count = shares.getItems().size();
      if (count > 0) {
         for (int i=0; i<count; i++) {
            String s = shares.getItems().get(i);
            if (s.equals(value))
               doit = false;
         }
      }
      if (doit) {
         shares.getItems().add(value);
         shares.setValue(value);
      }
   }
   
   private void updateWanSettings(String setting) {
      if (setting != null) {
         String tivoName = setting.replaceFirst("=.+$", "");
         // Update http & https setting according to selected TiVo
         String http = config.getWanSetting(tivoName, "http");
         if (http != null) {
            wan_http_port.setText(http);
         } else {
            wan_http_port.setText("");
         }
         String https = config.getWanSetting(tivoName, "https");
         if (https != null) {
            wan_https_port.setText(https);
         } else {
            wan_https_port.setText("");
         }
         String rpc = config.getWanSetting(tivoName, "rpc");
         if (rpc != null) {
            wan_rpc_port.setText(rpc);
         } else {
            wan_rpc_port.setText("");
         }
      }
   }
   
   private void updateLimitNplSettings(String setting) {
      if (setting != null) {
         String tivoName = setting.replaceFirst("=.+$", "");
         // Update limit_npl_fetches setting according to selected TiVo
         int limit = config.getLimitNplSetting(tivoName);
         limit_npl_fetches.setText("" + limit);
      }
   }
   
   private void updateEnableRpcSettings(String setting) {
      if (setting != null) {
         String tivoName = setting.replaceFirst("=.+$", "");
         // Update enableRpc setting according to selected TiVo
         if (config.rpcEnabled(tivoName))
            enableRpc.setSelected(true);
         else
            enableRpc.setSelected(false);
      }
   }
   
   // Callback for tivo del button
   @FXML private void delCB() {
      debug.print("");
      // Remove current selection in tivos ChoiceBox
      int selected = tivos.getSelectionModel().getSelectedIndex();
      if (selected > -1) {
         tivos.getItems().remove(selected);
      } else {
         log.error("No tivo entries left to remove");
      }
   }
   
   // Callback for share del button
   @FXML private void share_delCB() {
      debug.print("");
      // Remove current selection in shares ChoiceBox
      int selected = shares.getSelectionModel().getSelectedIndex();
      if (selected > -1) {
         shares.getItems().remove(selected);
      } else {
         log.error("No share entries left to remove");
      }
   }
   
   // Callback for autotune test button
   @FXML private void autotune_testCB() {
      debug.print("");
      if ( autotune_tivoName.getItems().size() > 0 ) {
         String cinterval = string.removeLeadingTrailingSpaces(
            autotune_channel_interval.getText()
         );
         String binterval = string.removeLeadingTrailingSpaces(
            autotune_button_interval.getText()
         );
         String chan1 = string.removeLeadingTrailingSpaces(
            autotune_chan1.getText()
         );
         String chan2 = string.removeLeadingTrailingSpaces(
            autotune_chan2.getText()
         );
         int channel_interval, button_interval;
         if (cinterval.length() == 0) {
            log.error("channel interval number not specified");
            return;
         } else {
            try {
               channel_interval = Integer.parseInt(
                  string.removeLeadingTrailingSpaces(cinterval)
               );
            } catch (Exception e) {
               log.error("channel interval should be an integer");
               return;
            }
         }
         if (binterval.length() == 0) {
            log.error("button interval number not specified");
            return;
         } else {
            try {
               button_interval = Integer.parseInt(
                  string.removeLeadingTrailingSpaces(binterval)
               );
            } catch (Exception e) {
               log.error("button interval should be an integer");
               return;
            }
         }
         if (chan1.length() == 0) {
            log.error("channel 1 not specified");
            return;
         }
         if (chan2.length() == 0) {
            log.error("channel 2 not specified");
            return;
         }
         String tivoName = autotune_tivoName.getValue();
         if (tivoName == null || tivoName.length() == 0) {
            log.error("No TiVo name selected");
            return;
         }
         jobData job = new jobData();
         job.source   = tivoName;
         job.tivoName = tivoName;
         job.type     = "autotune";
         job.name     = "telnet";
         job.autotune_channel_interval = channel_interval;
         job.autotune_button_interval = button_interval;
         job.autotune_chan1 = chan1;
         job.autotune_chan2 = chan2;
         jobMonitor.submitNewJob(job);
      }
   }
   
   // Callback for keywords ChoiceBox
   private void keywordsCB(String keyword) {
      debug.print("");
      if (keyword != null) {
         // Append selected entry to file_naming text field
         // (Replace current selection if any)
         int len = file_naming.getText().length();
         file_naming.positionCaret(len);
         file_naming.replaceSelection(keyword);
      }
      keywords.setValue(null);
   }
   
   // Callback for customFiles ChoiceBox
   private void customFilesCB(String keyword) {
      debug.print("");
      
      // Append selected entry to customCommand text field
      // (Replace current selection if any)
      int len = customCommand.getText().length();
      customCommand.positionCaret(len);
      customCommand.replaceSelection(keyword);
   }
   
   // Callback for autotune_tivoName ChoiceBox
   private void autotune_tivoNameCB(String name) {
      debug.print("");
      if (name != null && name.length() > 0) {
         if (autotune.isConfigured(name))
            autotune_enabled.setSelected(true);
         else
            autotune_enabled.setSelected(false);
         autotune_channel_interval.setText("" + config.autotune.get(name).get("channel_interval"));
         autotune_button_interval.setText("" + config.autotune.get(name).get("button_interval"));
         autotune_chan1.setText("" + config.autotune.get(name).get("chan1"));
         autotune_chan2.setText("" + config.autotune.get(name).get("chan2"));
      }
   }
   
   // Update widgets with config settings
   public void read() {
      debug.print("");
      String name;
      // Tivos
      Stack<String> tivoNames = config.getTivoNames();
      if (tivoNames.size()>0) {
         // Update tivo name lists
         tivos.getItems().clear();
         autotune_tivoName.getItems().clear();
         String ip;
         for (int i=0; i<tivoNames.size(); i++) {
            name = tivoNames.get(i);
            ip = config.TIVOS.get(name);
            tivos.getItems().add(name + "=" + ip);
            if (config.nplCapable(name))
               autotune_tivoName.getItems().add(name);
         }
         if (tivos.getItems().size() > 0)
            tivos.setValue(tivos.getItems().get(0));
         if (autotune_tivoName.getItems().size() > 0)
            autotune_tivoName.setValue(autotune_tivoName.getItems().get(0));
      }
      
      // Shares
      if (config.httpserver_shares.size()>0) {
         // Update share name lists
         shares.getItems().clear();
         for (String dir : config.httpserver_shares.keySet()) {
            shares.getItems().add(dir + "=" + config.httpserver_shares.get(dir));
         }
         if (shares.getItems().size() > 0)
            shares.setValue(shares.getItems().get(0));
      }
      
      // enableRpc
      enableRpc.setSelected(false);
      name = tivos.getValue();
      if (name != null) {
         String tivoName = name.replaceFirst("=.+$", "");
         if (config.rpcEnabled(tivoName))
            enableRpc.setSelected(true);
         else
            enableRpc.setSelected(false);
      }      
      
      // limit_npl_fetches
      limit_npl_fetches.setText("0");
      name = tivos.getValue();
      if (name != null) {
         String tivoName = name.replaceFirst("=.+$", "");
         int limit = config.getLimitNplSetting(tivoName);
         limit_npl_fetches.setText("" + limit);
      }
      
      // wan http & https ports
      wan_http_port.setText("");
      wan_https_port.setText("");
      name = tivos.getValue();
      if (name != null) {
         String tivoName = name.replaceFirst("=.+$", "");
         String http = config.getWanSetting(tivoName, "http");
         if (http != null)
            wan_http_port.setText(http);
         String https = config.getWanSetting(tivoName, "https");
         if (https != null)
            wan_https_port.setText(https);
         String rpc = config.getWanSetting(tivoName, "rpc");
         if (rpc != null)
            wan_rpc_port.setText(rpc);
      }
            
      // Beacon
      if (config.CheckBeacon == 1)
         beacon.setSelected(true);
      else
         beacon.setSelected(false);
      
      // UseOldBeacon
      if (config.UseOldBeacon == 1)
         UseOldBeacon.setSelected(true);
      else
         UseOldBeacon.setSelected(false);
      
      // npl_when_started
      if (config.npl_when_started == 1)
         npl_when_started.setSelected(true);
      else
         npl_when_started.setSelected(false);
      
      // showHistoryInTable
      if (config.showHistoryInTable == 1)
         showHistoryInTable.setSelected(true);
      else
         showHistoryInTable.setSelected(false);
      
      // download_time_estimate
      if (config.download_time_estimate == 1)
         download_time_estimate.setSelected(true);
      else
         download_time_estimate.setSelected(false);
      
      // Remove .TiVo
      if (config.RemoveTivoFile == 1)
         remove_tivo.setSelected(true);
      else
         remove_tivo.setSelected(false);
      
      // Remove comcut files
      if (config.RemoveComcutFiles == 1)
         remove_comcut.setSelected(true);
      else
         remove_comcut.setSelected(false);
      
      // Remove mpeg file after comcut
      if (config.RemoveComcutFiles_mpeg == 1)
         remove_comcut_mpeg.setSelected(true);
      else
         remove_comcut_mpeg.setSelected(false);
      
      // Remove .mpg file
      if (config.RemoveMpegFile == 1)
         remove_mpeg.setSelected(true);
      else
         remove_mpeg.setSelected(false);

      if (config.QSFixBackupMpegFile == 1)
         QSFixBackupMpegFile.setSelected(true);
      else
         QSFixBackupMpegFile.setSelected(false);

      if (config.download_check_length == 1)
         download_check_length.setSelected(true);
      else
         download_check_length.setSelected(false);
      
      // Check disk space
      if (config.CheckDiskSpace == 1)
         check_space.setSelected(true);
      else
         check_space.setSelected(false);
            
      // UseAdscan
      if (config.UseAdscan == 1)
         UseAdscan.setSelected(true);
      else
         UseAdscan.setSelected(false);
            
      // VrdReview
      if (config.VrdReview == 1)
         VrdReview.setSelected(true);
      else
         VrdReview.setSelected(false);
      
      // comskip_review
      if (config.comskip_review == 1)
         comskip_review.setSelected(true);
      else
         comskip_review.setSelected(false);
      
      // VrdReview_noCuts
      if (config.VrdReview_noCuts == 1)
         VrdReview_noCuts.setSelected(true);
      else
         VrdReview_noCuts.setSelected(false);
      
      // VrdQsFilter
      if (config.VrdQsFilter == 1)
         VrdQsFilter.setSelected(true);
      else
         VrdQsFilter.setSelected(false);
      
      // VrdDecrypt
      if (config.VrdDecrypt == 1)
         VrdDecrypt.setSelected(true);
      else
         VrdDecrypt.setSelected(false);
      
      // DsdDecrypt
      if (config.DsdDecrypt == 1)
         DsdDecrypt.setSelected(true);
      else
         DsdDecrypt.setSelected(false);
      
      // tivolibreDecrypt
      if (config.tivolibreDecrypt == 1) {
         tivolibreDecrypt.setSelected(true);
         DsdDecrypt.setSelected(false);
         config.DsdDecrypt = 0;
      }
      else
          tivolibreDecrypt.setSelected(false);      
      
      // tivolibreCompat
      if (config.tivolibreCompat == 1)
         tivolibreCompat.setSelected(true);      
      else
         tivolibreCompat.setSelected(false);
      
      // httpserver_enable
      if (config.httpserver_enable == 1)
         httpserver_enable.setSelected(true);
      else
         httpserver_enable.setSelected(false);
      
      // httpserver_share_filter
      if (config.httpserver_share_filter == 1)
         httpserver_share_filter.setSelected(true);
      else
         httpserver_share_filter.setSelected(false);
      
      // VRD flag
      if (config.VRD == 1)
         VRD.setSelected(true);
      else
         VRD.setSelected(false);
      
      // VRDexe
      VRDexe.setText(config.VRDexe);
      
      // VrdEncode
      if (config.VrdEncode == 1)
         VrdEncode.setSelected(true);
      else
         VrdEncode.setSelected(false);
      
      // VrdAllowMultiple
      if (config.VrdAllowMultiple == 1)
         VrdAllowMultiple.setSelected(true);
      else
         VrdAllowMultiple.setSelected(false);
      
      // VrdCombineCutEncode
      if (config.VrdCombineCutEncode == 1)
         VrdCombineCutEncode.setSelected(true);
      else
         VrdCombineCutEncode.setSelected(false);
      
      // VrdQsfixMpeg2ps
      if (config.VrdQsfixMpeg2ps == 1)
         VrdQsfixMpeg2ps.setSelected(true);
      else
         VrdQsfixMpeg2ps.setSelected(false);
      
      // VrdOneAtATime
      if (config.VrdOneAtATime == 1)
    	  VrdOneAtATime.setSelected(true);
      else
    	  VrdOneAtATime.setSelected(false);
      
      // TSDownload
      if (config.TSDownload == 1)
         TSDownload.setSelected(true);
      else
         TSDownload.setSelected(false);
      
      // TivoWebPlusDelete
      if (config.twpDeleteEnabled())
         TivoWebPlusDelete.setSelected(true);
      else
         TivoWebPlusDelete.setSelected(false);
      
      // rpcDelete
      if (config.rpcDelete == 1)
         rpcDelete.setSelected(true);
      else
         rpcDelete.setSelected(false);
      
      // rpcOld
      if (config.rpcOld == 1)
         rpcOld.setSelected(true);
      else
         rpcOld.setSelected(false);
      
      // HideProtectedFiles
      if (config.HideProtectedFiles == 1)
         HideProtectedFiles.setSelected(true);
      else
         HideProtectedFiles.setSelected(false);
      
      // OverwriteFiles
      if (config.OverwriteFiles == 1)
         OverwriteFiles.setSelected(true);
      else
         OverwriteFiles.setSelected(false);
      
      // DeleteFailedDownloads
      if (config.DeleteFailedDownloads == 1)
         DeleteFailedDownloads.setSelected(true);
      else
         DeleteFailedDownloads.setSelected(false);
      
      // combine_download_decrypt
      if (config.combine_download_decrypt == 1)
         combine_download_decrypt.setSelected(true);
      else
         combine_download_decrypt.setSelected(false);
      
      // single_download
      if (config.single_download == 1)
         single_download.setSelected(true);
      else
         single_download.setSelected(false);
      
      // rpcnpl
      if (config.rpcnpl == 1)
         rpcnpl.setSelected(true);
      else
         rpcnpl.setSelected(false);
      
      // persistQueue
      if (config.persistQueue)
    	  persistQueue.setSelected(true);
      else
    	  persistQueue.setSelected(false);
      
      // toolTips
      if (config.toolTips == 1)
         toolTips.setSelected(true);
      else
         toolTips.setSelected(false);
      
      // slingBox
      if (config.slingBox == 1)
         slingBox.setSelected(true);
      else
         slingBox.setSelected(false);
      
      // tableColAutoSize
      if (config.tableColAutoSize == 1)
         tableColAutoSize.setSelected(true);
      else
         tableColAutoSize.setSelected(false);
      
      // jobMonitorFullPaths
      if (config.jobMonitorFullPaths == 1)
         jobMonitorFullPaths.setSelected(true);
      else
         jobMonitorFullPaths.setSelected(false);
      
      // Files naming
      file_naming.setText(config.tivoFileNameFormat);
      
      // FILES Default path
      files_path.setText(config.TIVOS.get("FILES"));
      
      // Min requested space
      disk_space.setText("" + config.LowSpaceSize);
      
      // MAK
      MAK.setText(config.MAK);
      
      // FontSize
      FontSize.setText("" + config.FontSize);
      
      // .TiVo output dir
      tivo_output_dir.setText(config.outputDir);
      
      // .mpg output dir
      mpeg_output_dir.setText(config.mpegDir);
      
      // qsfixDir
      qsfixDir.setText(config.qsfixDir);
      
      // .mpg cut dir
      mpeg_cut_dir.setText(config.mpegCutDir);
      
      // encode output dir
      encode_output_dir.setText(config.encodeDir);
            
      // mencoder
      mencoder.setText(config.mencoder);

      // handbrake
      handbrake.setText(config.handbrake);
      
      // comskip
      comskip.setText(config.comskip);
      
      // comskip_ini
      comskip_ini.setText(config.comskipIni);
      
      // tivodecode
      tivodecode.setText(config.tivodecode);
      
      // dsd
      dsd.setText(config.dsd);
      
      // t2extract
      t2extract.setText(config.t2extract);
      
      // t2extract_args
      //t2extract_args.setText(config.t2extract_args);
      
      // ccextractor
      ccextractor.setText(config.ccextractor);
      
      // AtomicParsley
      AtomicParsley.setText(config.AtomicParsley);
      
      // ffmpeg
      ffmpeg.setText(config.ffmpeg);
      
      // mediainfo
      mediainfo.setText(config.mediainfo);
      
      // customCommand
      customCommand.setText(config.customCommand);
      
      // active job limit
      active_job_limit.setText("" + config.MaxJobs);
      
      // MinChanDigits
      MinChanDigits.setValue("" + config.MinChanDigits);
      
      // toolTipsDelay
      toolTipsDelay.setText("" + config.toolTipsDelay);
      
      // toolTipsTimeout
      toolTipsTimeout.setText("" + config.toolTipsTimeout);
      
      // cpu_cores
      cpu_cores.setText("" + config.cpu_cores);
      
      // download_tries
      download_tries.setText("" + config.download_tries);
      
      // download_retry_delay
      download_retry_delay.setText("" + config.download_retry_delay);
      
      // download_delay
      download_delay.setText("" + config.download_delay);
      
      // metadata_entries
      metadata_entries.setText("" + config.metadata_entries);
      
      // httpserver_port
      httpserver_port.setText("" + config.httpserver_port);
      
      // httpserver_cache
      httpserver_cache.setText(config.httpserver_cache);
      
      // autoLogSizeMB
      autoLogSizeMB.setText("" + config.autoLogSizeMB);
      
      // pyTivo_host
      pyTivo_host.setText("" + config.pyTivo_host);
      
      // web_query
      if (config.web_query.length() > 0)
         web_query.setText("" + config.web_query);
      else
         web_query.setText("http://www.imdb.com/find?s=all&q=");
      
      // web_browser
      if (config.web_browser.length() > 0)
         web_browser.setText("" + config.web_browser);
      else
         web_browser.setText("");
      
      // tivo_username
      if (config.getTivoUsername() != null)
         tivo_username.setText("" + config.getTivoUsername());
      else
         tivo_username.setText("");
      
      // tivo_password
      if (config.getTivoPassword() != null)
         tivo_password.setText("" + config.getTivoPassword());
      else
         tivo_password.setText("");
      
      // pyTivo_config
      pyTivo_config.setText("" + config.pyTivo_config);
      
      // pyTivo_tivo
      Stack<String> names = config.getNplTivoNames();
      if (names.size() > 0) {
         String setting = names.get(0);
         for (int i=0; i<names.size(); ++i) {
            if (names.get(i).equals(config.pyTivo_tivo)) {
               setting = config.pyTivo_tivo;
            }
         }
         pyTivo_tivo.setValue(setting);
      }
      
      // pyTivo_files
      pyTivo_files.setValue(config.pyTivo_files);
      
      // metadata_files
      metadata_files.setValue(config.metadata_files);
      
      // lookAndFeel
      if (lookAndFeel != null && config.lookAndFeel != null) {
         List<String> available = config.gui.getAvailableLooks();
         Boolean legal = false;
         for (String entry : available) {
            if (config.lookAndFeel.equals(entry))
               legal = true;
         }
         if (legal)
            lookAndFeel.setValue(config.lookAndFeel);
      }
      
      // autotune settings
      if (autotune_tivoName != null) {
         name = autotune_tivoName.getValue();
      } else {
         name = config.getNplTivoNames().get(0);
      }
      if (name != null && name.length() > 0) {
         if (autotune.isConfigured(name))
            autotune_enabled.setSelected(true);
         else
            autotune_enabled.setSelected(false);
         autotune_channel_interval.setText("" + config.autotune.get(name).get("channel_interval"));
         autotune_button_interval.setText("" + config.autotune.get(name).get("button_interval"));
         autotune_chan1.setText("" + config.autotune.get(name).get("chan1"));
         autotune_chan2.setText("" + config.autotune.get(name).get("chan2"));
      }
   }
   
   // Update config settings with widget values
   public int write() {
      debug.print("");
      int errors = 0;
      String value;
      String name;
      
      // enableRpc
      name = tivos.getValue();
      if (name != null) {
         String tivoName = name.replaceFirst("=.+$", "");
         if (enableRpc.isSelected())
            config.setRpcSetting("enableRpc_" + tivoName, "1");
         else
            config.setRpcSetting("enableRpc_" + tivoName, "0");
      }
      
      // Tivos
      int count = tivos.getItems().size();
      LinkedHashMap<String,String> h = new LinkedHashMap<String,String>();
      if (count > 0) {
         for (int i=0; i<count; i++) {
            String s = tivos.getItems().get(i);
            String[] l = s.split("=");
            if (l.length == 2) {
               h.put(l[0], l[1]);
            }
         }
      }
      config.setTivoNames(h);
      
      // Shares
      count = shares.getItems().size();
      if (count > 0) {
         config.httpserver_shares.clear();
         for (int i=0; i<count; i++) {
            String s = shares.getItems().get(i);
            String[] l = s.split("=");
            if (l.length == 2) {
               config.httpserver_shares.put(l[0], l[1]);
            }
         }
      }
      
      // limit_npl_fetches
      name = tivos.getValue();
      if (name != null) {
         String tivoName = name.replaceFirst("=.+$", "");
         value = string.removeLeadingTrailingSpaces(limit_npl_fetches.getText());
         if (value.length() > 0) {
            try {
               Integer.parseInt(value);
               config.setLimitNplSetting("limit_npl_" + tivoName, value);
            } catch(NumberFormatException e) {
               textFieldError(limit_npl_fetches, "limit npl fetches should be a number: '" + value + "'");
               errors++;
            }
         } else {
            config.setLimitNplSetting("limit_npl_" + tivoName, "0");
         }
      }
      
      // wan http & https ports
      name = tivos.getValue();
      if (name != null) {
         String tivoName = name.replaceFirst("=.+$", "");
         value = string.removeLeadingTrailingSpaces(wan_http_port.getText());
         if (value.length() > 0) {
            try {
               Integer.parseInt(value);
               config.setWanSetting(tivoName, "http", value);
            } catch(NumberFormatException e) {
               textFieldError(wan_http_port, "wan http port should be a number: '" + value + "'");
               errors++;
            }
         } else {
            config.setWanSetting(tivoName, "http", "");
         }
         
         value = string.removeLeadingTrailingSpaces(wan_https_port.getText());
         if (value.length() > 0) {
            try {
               Integer.parseInt(value);
               config.setWanSetting(tivoName, "https", value);
            } catch(NumberFormatException e) {
               textFieldError(wan_https_port, "wan https port should be a number: '" + value + "'");
               errors++;
            }
         } else {
            config.setWanSetting(tivoName, "https", "");
         }
         
         value = string.removeLeadingTrailingSpaces(wan_rpc_port.getText());
         if (value.length() > 0) {
            try {
               Integer.parseInt(value);
               config.setWanSetting(tivoName, "rpc", value);
            } catch(NumberFormatException e) {
               textFieldError(wan_rpc_port, "wan rpc port should be a number: '" + value + "'");
               errors++;
            }
         } else {
            config.setWanSetting(tivoName, "rpc", "");
         }
      }
      
      // UseOldBeacon
      if (UseOldBeacon.isSelected()) {
         config.UseOldBeacon = 1;
      } else {
         config.UseOldBeacon = 0;
      }
      
      // download_time_estimate
      if (download_time_estimate.isSelected()) {
         config.download_time_estimate = 1;
      } else {
         config.download_time_estimate = 0;
      }
      
      // npl_when_started
      if (npl_when_started.isSelected()) {
         config.npl_when_started = 1;
      } else {
         config.npl_when_started = 0;
      }
      
      // showHistoryInTable
      if (showHistoryInTable.isSelected()) {
         config.showHistoryInTable = 1;
      } else {
         config.showHistoryInTable = 0;
      }
      
      // Beacon
      if (beacon.isSelected()) {
         config.CheckBeacon = 1;
         if (config.UseOldBeacon == 0) {
            if (config.jmdns == null) config.jmdns = new mdns();            
         } else {
            if (config.tivo_beacon == null) config.tivo_beacon = new beacon();
         }         
      } else {
         config.CheckBeacon = 0;
         if (config.UseOldBeacon == 0) {
            if (config.jmdns != null) {
               config.jmdns.close();
               config.jmdns = null;
            }
         } else {
            config.tivo_beacon = null;
         }
      }
            
      // Remove .TiVo
      if (remove_tivo.isSelected())
         config.RemoveTivoFile = 1;
      else
         config.RemoveTivoFile = 0;
            
      // Remove comcut files
      if (remove_comcut.isSelected())
         config.RemoveComcutFiles = 1;
      else
         config.RemoveComcutFiles = 0;
      
      // Remove mpeg file after comcut
      if (remove_comcut_mpeg.isSelected())
         config.RemoveComcutFiles_mpeg = 1;
      else
         config.RemoveComcutFiles_mpeg = 0;
      
      // Remove .mpg file
      if (remove_mpeg.isSelected())
         config.RemoveMpegFile = 1;
      else
         config.RemoveMpegFile = 0;
      
      if (QSFixBackupMpegFile.isSelected())
         config.QSFixBackupMpegFile = 1;
      else
         config.QSFixBackupMpegFile = 0;
      
      if (download_check_length.isSelected())
         config.download_check_length = 1;
      else
         config.download_check_length = 0;
      
      // Check disk space
      if (check_space.isSelected())
         config.CheckDiskSpace = 1;
      else
         config.CheckDiskSpace = 0;
      
      // VRD flag
      if (VRD.isSelected())
         config.VRD = 1;
      else
         config.VRD = 0;
      
      // VRDexe
      value = string.removeLeadingTrailingSpaces(VRDexe.getText());
      if (value.length() > 0) {
    	   if (file.isFile(value)) {
    	      config.VRDexe = value;
    	   } else {
            textFieldError(VRDexe, "Configured path to VRD executable doesn't exist: '" + value + "'");
            errors++;
    	   }
      }
      
      // UseAdscan
      if (UseAdscan.isSelected() && config.VRD == 1)
         config.UseAdscan = 1;
      else
         config.UseAdscan = 0;
      
      // VrdReview
      if (VrdReview.isSelected() && config.VRD == 1)
         config.VrdReview = 1;
      else
         config.VrdReview = 0;
      
      // comskip_review
      if (comskip_review.isSelected() && file.isFile(config.comskip))
         config.comskip_review = 1;
      else
         config.comskip_review = 0;
      
      // VrdReview_noCuts
      if (VrdReview_noCuts.isSelected() && config.VRD == 1)
         config.VrdReview_noCuts = 1;
      else
         config.VrdReview_noCuts = 0;
      
      // VrdQsFilter
      if (VrdQsFilter.isSelected() && config.VRD == 1)
         config.VrdQsFilter = 1;
      else
         config.VrdQsFilter = 0;
      
      // VrdDecrypt
      if (VrdDecrypt.isSelected() && config.VRD == 1)
         config.VrdDecrypt = 1;
      else
         config.VrdDecrypt = 0;
      
      // DsdDecrypt
      if (DsdDecrypt.isSelected())
         config.DsdDecrypt = 1;
      else
         config.DsdDecrypt = 0;
      
      // tivolibreDecrypt
      if (tivolibreDecrypt.isSelected()) {
         config.tivolibreDecrypt = 1;
         config.DsdDecrypt = 0;
      }
      else
         config.tivolibreDecrypt = 0;
      
      // tivolibreCompat
      if (tivolibreCompat.isSelected())
         config.tivolibreCompat = 1;
      else
         config.tivolibreCompat = 0;
      
      // httpserver_enable
      if (httpserver_enable.isSelected()) {
         config.httpserver_enable = 1;
         if (config.httpserver == null)
            new kmttgServer();
      }
      else {
         config.httpserver_enable = 0;
         if (config.httpserver != null) {
            config.httpserver.stop();
            config.httpserver = null;
         }
      }
      
      // httpserver_share_filter
      if (httpserver_share_filter.isSelected()) {
         config.httpserver_share_filter = 1;
      }
      else {
         config.httpserver_share_filter = 0;
      }
      
      // VrdEncode
      if (VrdEncode.isSelected() && config.VRD == 1)
         config.VrdEncode = 1;
      else
         config.VrdEncode = 0;
      
      // VrdAllowMultiple
      if (VrdAllowMultiple.isSelected() && config.VRD == 1)
         config.VrdAllowMultiple = 1;
      else
         config.VrdAllowMultiple = 0;
      
      // VrdCombineCutEncode
      if (VrdCombineCutEncode.isSelected() && config.VRD == 1)
         config.VrdCombineCutEncode = 1;
      else
         config.VrdCombineCutEncode = 0;
      
      // VrdQsfixMpeg2ps
      if (VrdQsfixMpeg2ps.isSelected() && config.VRD == 1)
         config.VrdQsfixMpeg2ps = 1;
      else
         config.VrdQsfixMpeg2ps = 0;
      
      // VrdOneAtATime
      if (VrdOneAtATime.isSelected() && config.VRD == 1)
         config.VrdOneAtATime = 1;
      else
         config.VrdOneAtATime = 0;
      
      // TSDownload
      if (TSDownload.isSelected())
         config.TSDownload = 1;
      else
         config.TSDownload = 0;
      
      // TivoWebPlusDelete
      if (TivoWebPlusDelete.isSelected())
         config.twpDeleteEnabledSet(true);
      else
         config.twpDeleteEnabledSet(false);
      
      // rpcDelete
      if (rpcDelete.isSelected())
         config.rpcDelete = 1;
      else
         config.rpcDelete = 0;
      
      // rpcOld
      if (rpcOld.isSelected())
         config.rpcOld = 1;
      else
         config.rpcOld = 0;
      
      // HideProtectedFiles
      if (HideProtectedFiles.isSelected())
         config.HideProtectedFiles = 1;
      else
         config.HideProtectedFiles = 0;
      
      // OverwriteFiles
      if (OverwriteFiles.isSelected())
         config.OverwriteFiles = 1;
      else
         config.OverwriteFiles = 0;
      
      // DeleteFailedDownloads
      if (DeleteFailedDownloads.isSelected())
         config.DeleteFailedDownloads = 1;
      else
         config.DeleteFailedDownloads = 0;
      
      // combine_download_decrypt
      if (combine_download_decrypt.isSelected())
         config.combine_download_decrypt = 1;
      else
         config.combine_download_decrypt = 0;
      
      // single_download
      if (single_download.isSelected())
         config.single_download = 1;
      else
         config.single_download = 0;
      
      // rpcnpl
      if (rpcnpl.isSelected())
         config.rpcnpl = 1;
      else
         config.rpcnpl = 0;
      
      // persistQueue
      if (persistQueue.isSelected())
         config.persistQueue = true;
      else
         config.persistQueue = false;
      
      // toolTips
      if (toolTips.isSelected())
         config.toolTips = 1;
      else
         config.toolTips = 0;
      MyTooltip.enableToolTips(config.toolTips);
      
      // slingBox
      if (slingBox.isSelected())
         config.slingBox = 1;
      else
         config.slingBox = 0;
      
      // tableColAutoSize
      if (tableColAutoSize.isSelected())
         config.tableColAutoSize = 1;
      else
         config.tableColAutoSize = 0;
      
      // jobMonitorFullPaths
      if (jobMonitorFullPaths.isSelected())
         config.jobMonitorFullPaths = 1;
      else
         config.jobMonitorFullPaths = 0;
      
      // Files naming
      value = file_naming.getText();
      if (value.length() == 0) {
         // Reset to default if none given
         value = "[title] ([monthNum]_[mday]_[year])";
      }
      config.tivoFileNameFormat = value;
      
      // FILES Default path
      value = string.removeLeadingTrailingSpaces(files_path.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = config.programDir;
      } else {
         if (! file.isDir(value) ) {
            textFieldError(files_path, "FILES Default Path setting not a valid dir: '" + value + "'");
            errors++;
         }
      }
      config.TIVOS.put("FILES", value);
      
      // Min requested space
      value = string.removeLeadingTrailingSpaces(disk_space.getText());
      if (value.length() > 0) {
         try {
            config.LowSpaceSize = Integer.parseInt(value);
         } catch(NumberFormatException e) {
            textFieldError(disk_space, "Illegal setting for min required disk space (GB): '" + value + "'");
            log.error("Setting to 0 (no check)");
            config.LowSpaceSize = 0;
            disk_space.setText("" + config.LowSpaceSize);
            errors++;
         }
      } else {
         config.LowSpaceSize = 0;
      }
      
      // MAK
      value = string.removeLeadingTrailingSpaces(MAK.getText());
      if (value.length() > 0) {
         if (value.length() == 10) {
            try {
               Long.parseLong(value);
               config.MAK = value;
            } catch(NumberFormatException e) {
               textFieldError(MAK, "MAK should be a 10 digit number: '" + value + "'");
               errors++;
            }
         } else {
            textFieldError(MAK, "MAK should be a 10 digit number: '" + value + "'");
            errors++;
         }
      } else {
         textFieldError(MAK, "MAK not specified - should be a 10 digit number");
         errors++;
      }
      
      // FontSize
      value = string.removeLeadingTrailingSpaces(FontSize.getText());
      int size = 12;
      if (value.length() > 0) {
         try {
            size = Integer.parseInt(value);
         } catch(NumberFormatException e) {
            textFieldError(FontSize, "Illegal setting for FontSize: '" + value + "'");
            log.error("Setting to 12");
            size = 12;
            FontSize.setText("" + size);
            errors++;
         }
      }
      if (config.FontSize != size) {
         config.FontSize = size;
         config.gui.setFontSize(config.gui.getFrame().getScene(), size);
      }
      
      // .TiVo output dir
      value = string.removeLeadingTrailingSpaces(tivo_output_dir.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = config.programDir;         
      } else {
         if ( ! file.isDir(value) ) {
            textFieldError(tivo_output_dir, ".TiVo Output Dir setting not a valid dir: '" + value + "'");
            errors++;
         }
      }
      config.outputDir = value;
      
      // .mpg output dir
      value = string.removeLeadingTrailingSpaces(mpeg_output_dir.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = config.programDir;
      } else {
         if ( ! file.isDir(value) ) {
            textFieldError(mpeg_output_dir, ".mpg Output Dir setting not a valid dir: '" + value + "'");
            errors++;
         }
      }
      config.mpegDir = value;
      
      // qsfixDir
      value = string.removeLeadingTrailingSpaces(qsfixDir.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = config.mpegDir;
      } else {
         if ( ! file.isDir(value) ) {
            textFieldError(qsfixDir, "QS Fix Output Dir setting not a valid dir: '" + value + "'");
            errors++;
         }
      }
      config.qsfixDir = value;
      
      // .mpg cut dir
      value = string.removeLeadingTrailingSpaces(mpeg_cut_dir.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = config.programDir;
      } else {
         if ( ! file.isDir(value) ) {
            textFieldError(mpeg_cut_dir, ".mpg Cut Dir setting not a valid dir: '" + value + "'");
            errors++;
         }
      }
      config.mpegCutDir = value;
      
      // encode output dir
      value = string.removeLeadingTrailingSpaces(encode_output_dir.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = config.programDir;
      } else {
         if ( ! file.isDir(value) ) {
            textFieldError(encode_output_dir, "Encode Output Dir setting not a valid dir: '" + value  + "'");
            errors++;
         }
      }
      config.encodeDir = value;
      
      // mencoder
      value = string.removeLeadingTrailingSpaces(mencoder.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      } else {
         if ( ! file.isFile(value) ) {
            textFieldError(mencoder, "mencoder setting not a valid file: '" + value + "'");
            errors++;
         }
      }
      config.mencoder = value;
      
      // handbrake
      value = string.removeLeadingTrailingSpaces(handbrake.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      } else {
         if ( ! file.isFile(value) ) {
            textFieldError(handbrake, "handbrake setting not a valid file: '" + value + "'");
            errors++;
         }
      }
      config.handbrake = value;
      
      // comskip
      value = string.removeLeadingTrailingSpaces(comskip.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      } else {
         if ( ! file.isFile(value) ) {
            textFieldError(comskip, "comskip setting not a valid file: '" + value + "'");
            errors++;
         }
      }
      config.comskip = value;
      
      // comskip_ini
      value = string.removeLeadingTrailingSpaces(comskip_ini.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      } else {
         if ( ! file.isFile(value) ) {
            textFieldError(comskip_ini, "comskip.ini setting not a valid file: '" + value + "'");
            errors++;
         }
      }
      config.comskipIni = value;
      
      // tivodecode
      value = string.removeLeadingTrailingSpaces(tivodecode.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      } else {
         if ( ! file.isFile(value) ) {
            textFieldError(tivodecode, "tivodecode setting not a valid file: '" + value + "'");
            errors++;
         }
      }
      config.tivodecode = value;
      
      // dsd
      value = string.removeLeadingTrailingSpaces(dsd.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      } else {
         if ( ! file.isFile(value) ) {
            textFieldError(dsd, "dsd setting not a valid file: '" + value + "'");
            errors++;
         }
      }
      config.dsd = value;
      
      // t2extract
      value = string.removeLeadingTrailingSpaces(t2extract.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      } else {
         if ( ! file.isFile(value) ) {
            textFieldError(t2extract, "ccextractor setting not a valid file: '" + value  + "'");
            errors++;
         }
      }
      config.t2extract = value;
      
      // t2extract_args
      //value = string.removeLeadingTrailingSpaces(t2extract_args.getText());
      //if (value.length() == 0) {
      //   // Reset to default if none given
      //   value = "";
      //}
      //config.t2extract_args = value;
      
      // ccextractor
      value = string.removeLeadingTrailingSpaces(ccextractor.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      } else {
         if ( ! file.isFile(value) ) {
            textFieldError(ccextractor, "ccextractor setting not a valid file: '" + value  + "'");
            errors++;
         }
      }
      config.ccextractor = value;
            
      // AtomicParsley
      value = string.removeLeadingTrailingSpaces(AtomicParsley.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      } else {
         if ( ! file.isFile(value) ) {
            textFieldError(AtomicParsley, "AtomicParsley setting not a valid file: '" + value + "'");
            errors++;
         }
      }
      config.AtomicParsley = value;
      
      // ffmpeg
      value = string.removeLeadingTrailingSpaces(ffmpeg.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      } else {
         if ( ! file.isFile(value) ) {
            textFieldError(ffmpeg, "ffmpeg setting not a valid file: '" + value + "'");
            errors++;
         }
      }
      config.ffmpeg = value;
      
      // mediainfo
      value = string.removeLeadingTrailingSpaces(mediainfo.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      } else {
         if ( ! file.isFile(value) ) {
            textFieldError(mediainfo, "mediainfo setting not a valid file: '" + value + "'");
            errors++;
         }
      }
      config.mediainfo = value;
      
      // customCommand
      value = string.removeLeadingTrailingSpaces(customCommand.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      } else {
         if ( ! custom.customCommandExists(value) ) {
            textFieldError(customCommand, "custom command setting does not start with a valid file: '" + value + "'");
            errors++;
         }
      }
      config.customCommand = value;
      
      // active job limit
      value = string.removeLeadingTrailingSpaces(active_job_limit.getText());
      if (value.length() > 0) {
         try {
            config.MaxJobs = Integer.parseInt(value);
         } catch(NumberFormatException e) {
            textFieldError(active_job_limit, "Illegal setting for active job limit: '" + value + "'");
            log.error("Setting to 2");
            config.MaxJobs = 2;
            active_job_limit.setText("" + config.MaxJobs);
            errors++;
         }
      } else {
         config.MaxJobs = 2;
      }
      
      // MinChanDigits
      config.MinChanDigits = Integer.parseInt(MinChanDigits.getValue());
      
      // cpu_cores
      value = string.removeLeadingTrailingSpaces(cpu_cores.getText());
      if (value.length() > 0) {
         try {
            config.cpu_cores = Integer.parseInt(value);
         } catch(NumberFormatException e) {
            textFieldError(cpu_cores, "Illegal setting for cpu cores: '" + value + "'");
            log.error("Setting to 1");
            config.cpu_cores = 1;
            cpu_cores.setText("" + config.cpu_cores);
            errors++;
         }
      } else {
         config.cpu_cores = 1;
      }
      
      // download_tries
      value = string.removeLeadingTrailingSpaces(download_tries.getText());
      if (value.length() > 0) {
         try {
            config.download_tries = Integer.parseInt(value);
         } catch(NumberFormatException e) {
            textFieldError(download_tries, "Illegal setting for # download tries: '" + value + "'");
            log.error("Setting to 5");
            config.download_tries = 5;
            download_tries.setText("" + config.download_tries);
            errors++;
         }
      } else {
         config.download_tries = 5;
      }
      
      // download_retry_delay
      value = string.removeLeadingTrailingSpaces(download_retry_delay.getText());
      if (value.length() > 0) {
         try {
            config.download_retry_delay = Integer.parseInt(value);
         } catch(NumberFormatException e) {
            textFieldError(download_retry_delay, "Illegal setting for delay between download tries: '" + value + "'");
            log.error("Setting to 10");
            config.download_retry_delay = 10;
            download_retry_delay.setText("" + config.download_retry_delay);
            errors++;
         }
      } else {
         config.download_retry_delay = 10;
      }
      
      // metadata_entries
      value = string.removeLeadingTrailingSpaces(metadata_entries.getText());
      if (value.length() > 0) {
         config.metadata_entries = value;
      } else {
         config.metadata_entries = "";
      }
      
      // httpserver_port
      value = string.removeLeadingTrailingSpaces(httpserver_port.getText());
      if (value.length() > 0) {
         try {
            config.httpserver_port = Integer.parseInt(value);
         } catch(NumberFormatException e) {
            textFieldError(httpserver_port, "Illegal setting for web server port: '" + value + "'");
            log.error("Setting to 8181");
            config.httpserver_port = 8181;
            httpserver_port.setText("" + config.httpserver_port);
            errors++;
         }
      } else {
         config.httpserver_port = 8181;
      }
      
      // httpserver_cache
      value = string.removeLeadingTrailingSpaces(httpserver_cache.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = config.httpserver_home + File.separator + "cache";
      } else {
         if ( ! file.isDir(value) ) {
            textFieldError(httpserver_cache, "web server cache setting not a valid dir: '" + value  + "'");
            errors++;
         }
      }
      config.httpserver_cache = value;
            
      // download_delay
      value = string.removeLeadingTrailingSpaces(download_delay.getText());
      if (value.length() > 0) {
         try {
            config.download_delay = Integer.parseInt(value);
         } catch(NumberFormatException e) {
            textFieldError(download_delay, "Illegal setting for download delay: '" + value + "'");
            log.error("Setting to 10");
            config.download_delay = 10;
            download_delay.setText("" + config.download_delay);
            errors++;
         }
      } else {
         config.download_delay = 10;
      }
      
      // autoLogSizeMB
      value = string.removeLeadingTrailingSpaces(autoLogSizeMB.getText());
      if (value.length() > 0) {
         try {
            config.autoLogSizeMB = Integer.parseInt(value);
            if (config.autoLogSizeMB < 1) {
               textFieldError(autoLogSizeMB, "Illegal setting for auto log file size limit (MB): '" + config.autoLogSizeMB + "'");
               log.error("Should be integer > 0... Setting to 10");
               config.autoLogSizeMB = 10;
               autoLogSizeMB.setText("" + config.autoLogSizeMB);
               errors++;               
            }
         } catch(NumberFormatException e) {
            textFieldError(autoLogSizeMB, "Illegal setting for auto log file size limit (MB): '" + value + "'");
            log.error("Should be integer > 0... Setting to 10");
            config.autoLogSizeMB = 10;
            autoLogSizeMB.setText("" + config.autoLogSizeMB);
            errors++;
         }
      } else {
         config.autoLogSizeMB = 10;
      }
      
      // toolTipsDelay
      value = string.removeLeadingTrailingSpaces(toolTipsDelay.getText());
      if (value.length() > 0) {
         try {
            config.toolTipsDelay = Integer.parseInt(value);
            MyTooltip.setTooltipDelay(config.toolTipsDelay, config.toolTipsTimeout);
         } catch(NumberFormatException e) {
            textFieldError(toolTipsDelay, "Illegal setting for toolTips delay: '" + value + "'");
            log.error("Setting to 2");
            config.toolTipsDelay = 2;
            toolTipsDelay.setText("" + config.toolTipsDelay);
            errors++;
         }
      } else {
         config.toolTipsDelay = 2;
      }
      
      // toolTipsTimeout
      value = string.removeLeadingTrailingSpaces(toolTipsTimeout.getText());
      if (value.length() > 0) {
         try {
            config.toolTipsTimeout = Integer.parseInt(value);
            MyTooltip.setTooltipDelay(config.toolTipsDelay, config.toolTipsTimeout);
         } catch(NumberFormatException e) {
            textFieldError(toolTipsTimeout, "Illegal setting for toolTips timeout: '" + value + "'");
            log.error("Setting to 20");
            config.toolTipsTimeout = 20;
            toolTipsTimeout.setText("" + config.toolTipsTimeout);
            errors++;
         }
      } else {
         config.toolTipsTimeout = 20;
      }
      
      // pyTivo_host
      value = string.removeLeadingTrailingSpaces(pyTivo_host.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "localhost";
      }
      config.pyTivo_host = value;
      
      // web_query
      value = string.removeLeadingTrailingSpaces(web_query.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "http://www.imdb.com/find?s=all&q=";
      }
      config.web_query = value;
      
      // web_browser
      value = string.removeLeadingTrailingSpaces(web_browser.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      }
      config.web_browser = value;
      
      // tivo_username
      value = string.removeLeadingTrailingSpaces(tivo_username.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      }
      config.setTivoUsername(value);
      
      // tivo_password
      value = string.removeLeadingTrailingSpaces(tivo_password.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      }
      config.setTivoPassword(value);
      
      // pyTivo_config
      value = string.removeLeadingTrailingSpaces(pyTivo_config.getText());
      if (value.length() == 0) {
         // Reset to default if none given
         value = "";
      }
      config.pyTivo_config = value;
      
      // pyTivo_tivo
      config.pyTivo_tivo = pyTivo_tivo.getValue();
      
      // pyTivo_files
      config.pyTivo_files = pyTivo_files.getValue();
      
      // metadata_files
      config.metadata_files = metadata_files.getValue();
      
      // lookAndFeel
      config.lookAndFeel = lookAndFeel.getValue();
      
      // autotune settings
      if (autotune_tivoName != null && autotune_tivoName.getItems().size() > 0) {
         name = autotune_tivoName.getValue();
         if (name != null) {
            if (autotune_enabled.isSelected())
               autotune.enable(name);
            else
               autotune.disable(name);
            config.autotune.get(name).put("channel_interval", string.removeLeadingTrailingSpaces(autotune_channel_interval.getText()));
            config.autotune.get(name).put("button_interval", string.removeLeadingTrailingSpaces(autotune_button_interval.getText()));
            config.autotune.get(name).put("chan1", string.removeLeadingTrailingSpaces(autotune_chan1.getText()));
            config.autotune.get(name).put("chan2", string.removeLeadingTrailingSpaces(autotune_chan2.getText()));
         }
      }
      
      return errors;
   }

   private static void create(Stage frame) {
      debug.print("frame=" + frame);
      // Create all the components of the dialog
      Parent configMain_fxml;
      try {
    	  FXMLLoader loader = new FXMLLoader(configMain.class.getResource(
    					  "configMain.fxml"));
    	  bundle = ResourceBundle.getBundle("com.tivo.kmttg.gui.dialog.configMain");
    	  loader.setResources(bundle);
    	  configMain_fxml = loader.<Parent>load();
      	  Scene scene = new Scene(configMain_fxml);
      	  // save our official instance of configAuto
      	  singleton = loader.<configMain>getController();
      	  
      	  // all the controller adjustments are done in the initialize method
      	  // automatically called by FXMLLoader
      	  
      
      // create dialog window
      dialog = new Stage();
      dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
         @Override
         public void handle(WindowEvent arg0) {
            pos_x = dialog.getX(); pos_y = dialog.getY();
         }
      });
      dialog.initOwner(frame);
      dialog.initModality(Modality.NONE); // Non modal
      dialog.setTitle(bundle.getString("dialog_title"));
//      Scene scene = new Scene(new VBox());
      config.gui.setFontSize(scene, config.FontSize);
//      ((VBox) scene.getRoot()).getChildren().add(main_panel);
      dialog.setScene(scene);
      dialog.setResizable(false);
      
      } catch (IOException e1) {
    	  // TODO Auto-generated catch block
    	  e1.printStackTrace();
    	  dialog = null;
    	  singleton = null;
      }
  }

   @Override
	public void initialize(URL location, ResourceBundle resources) {
	      tivos.valueProperty().addListener(new ChangeListener<String>() {
	          @Override public void changed(ObservableValue<? extends String> ov, String oldVal, String newVal) {
	             if (newVal != null) {
	                updateWanSettings(newVal);
	                updateLimitNplSettings(newVal);
	                updateEnableRpcSettings(newVal);
	             }
	          }
	       });
	       FileBrowser = new FileChooser(); FileBrowser.setInitialDirectory(new File(config.programDir));
	       FileBrowser.setTitle(bundle.getString("FileBrowser_title"));
	       DirBrowser = new DirectoryChooser(); DirBrowser.setInitialDirectory(new File(config.programDir));
	       DirBrowser.setTitle(bundle.getString("DirBrowser_title"));

	       keywords.getItems().addAll(
	          "[title]", "[mainTitle]", "[episodeTitle]", "[channelNum]",
	          "[channel]", "[min]", "[hour]", "[wday]", "[mday]", "[month]",
	          "[monthNum]", "[year]", "[movieYear]", "[originalAirDate]", "[season]", "[episode]", 
	          "[EpisodeNumber]", "[SeriesEpNumber]", "[description]", "[tivoName]", "[startTime]", "[/]"
	       );
	       keywords.valueProperty().addListener(new ChangeListener<String>() {
	          @Override public void changed(ObservableValue<? extends String> ov, String oldVal, String newVal) {
	             if (newVal != null) {
	             	keywordsCB(newVal);
	             }
	          }
	       });

	       for (String name : config.getNplTivoNames())
	     	  autotune_tivoName.getItems().add(name);
	       autotune_tivoName.valueProperty().addListener(new ChangeListener<String>() {
	          @Override public void changed(ObservableValue<? extends String> ov, String oldVal, String newVal) {
	             if (newVal != null) {
	             	autotune_tivoNameCB(newVal);
	             }
	          }
	       });
	       
	       for (String name : config.getNplTivoNames())
	     	  pyTivo_tivo.getItems().add(name);
	       pyTivo_tivo.getSelectionModel().select(0);
	       
	       pyTivo_files.getItems().addAll(
	          "tivoFile", "mpegFile", "mpegFile_cut", "encodeFile", "last", "all"
	       );
	       pyTivo_files.getSelectionModel().select(0);
	       
	       metadata_files.getItems().addAll(
	          "tivoFile", "mpegFile", "mpegFile_cut", "encodeFile", "last", "all"
	       );
	       metadata_files.getSelectionModel().select(0);
	       
	       for (String name : config.gui.getAvailableLooks())
	     	  lookAndFeel.getItems().add(name);
	       lookAndFeel.getSelectionModel().select("default.css");
	       lookAndFeel.valueProperty().addListener(new ChangeListener<String>() {
	          @Override public void changed(ObservableValue<? extends String> ov, String oldVal, String newVal) {
	             if (newVal != null) {
	                config.gui.setLookAndFeel(newVal); 
	             }
	          }
	       });

	       customFiles.getItems().addAll(
	          "[tivoFile]", "[metaFile]", "[mpegFile]", "[mpegFile_cut]", "[srtFile]", "[encodeFile]"
	       );
	       customFiles.getSelectionModel().select(0);
	       customFiles.valueProperty().addListener(new ChangeListener<String>() {
	          @Override public void changed(ObservableValue<? extends String> ov, String oldVal, String newVal) {
	             if (newVal != null) {
	             	customFilesCB(newVal); 
	             }
	          }
	       });

	       CANCEL.setOnAction(new EventHandler<ActionEvent>() {
	          public void handle(ActionEvent e) {
	             pos_x = dialog.getX(); pos_y = dialog.getY();
	             dialog.hide();
	          }
	       });

	       // File browser mouse double-click listeners
	       files_path.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = DirBrowser.showDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  files_path.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });
	       
	       tivo_output_dir.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = DirBrowser.showDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  tivo_output_dir.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       mpeg_output_dir.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = DirBrowser.showDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  mpeg_output_dir.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       qsfixDir.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = DirBrowser.showDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  qsfixDir.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       mpeg_cut_dir.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = DirBrowser.showDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  mpeg_cut_dir.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       encode_output_dir.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = DirBrowser.showDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  encode_output_dir.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       tivodecode.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = FileBrowser.showOpenDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  tivodecode.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       dsd.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = FileBrowser.showOpenDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  dsd.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       ffmpeg.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = FileBrowser.showOpenDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  ffmpeg.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       mediainfo.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = FileBrowser.showOpenDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  mediainfo.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       customCommand.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = FileBrowser.showOpenDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  customCommand.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       mencoder.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = FileBrowser.showOpenDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  mencoder.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       handbrake.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = FileBrowser.showOpenDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  handbrake.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       comskip.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = FileBrowser.showOpenDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  comskip.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       comskip_ini.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = FileBrowser.showOpenDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  comskip_ini.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       t2extract.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = FileBrowser.showOpenDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  t2extract.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       ccextractor.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = FileBrowser.showOpenDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  ccextractor.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       AtomicParsley.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = FileBrowser.showOpenDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                 	  AtomicParsley.setText(selectedFile.getPath());
	                   }
	                }
	             }
	          }
	       });

	       pyTivo_config.setOnMouseClicked(new EventHandler<MouseEvent>() {
	          @Override
	          public void handle(MouseEvent mouseEvent) {
	             if( mouseEvent.getButton().equals(MouseButton.PRIMARY) ) {
	                if (mouseEvent.getClickCount() == 2) {
	                   File selectedFile = FileBrowser.showOpenDialog(config.gui.getFrame());
	                   if (selectedFile != null) {
	                      String selected = selectedFile.getPath();
	                      if (string.basename(selected).equals("pyTivo.conf")) {
	                     	 pyTivo_config.setText(selected);
	                      } else {
	                         log.error("Invalid file chosen - must be pyTivo.conf: " + selected);
	                      }
	                   }
	                }
	             }
	          }
	       });
	       
	       // dsd
	       if (config.OS.equals("windows")) {
//	          gy++;
//	          programs_panel.add(dsd_label, 0, gy);
//	          programs_panel.add(dsd, 1, gy);
	       } else {
	     	  dsd_label.setManaged(false);
	     	  dsd_label.setVisible(false);
	     	  dsd.setManaged(false);
	     	  dsd.setVisible(false);
	       }
	       
	       // ccextractor (intentionally disabled for now)
	       ccextractor_label.setManaged(false);
	       ccextractor_label.setVisible(false);
	       ccextractor.setManaged(false);
	       ccextractor.setVisible(false);
	       
	       if (config.OS.equals("windows")) {
//	          // DsdDecrypt
//	          program_options_panel.add(DsdDecrypt, 1, gy);
	       } else {
	     	  DsdDecrypt.setManaged(false);
	     	  DsdDecrypt.setVisible(false);
	       }
	       
	       // web_browser - not used for Mac or Windows
	       if ( config.OS.equals("other")) {
//	          gy++;
//	          visual_panel.add(web_browser_label, 0, gy);
//	          visual_panel.add(web_browser, 1, gy);
	       } else {
	     	  web_browser.setManaged(false);
	     	  web_browser.setVisible(false);
	     	  web_browser_label.setManaged(false);
	     	  web_browser_label.setVisible(false);
	       }
	       
	       if (config.OS.equals("windows"))
	     	  ;
//	          addTabPane("VideoRedo", vrd_panel);
	       else
	     	  tabbed_panel.getTabs().remove(6);
	   
	       
       // Set component tooltips
       setToolTips();
	}
   
   public void setToolTips() {
      debug.print("");
      VRDexe.setTooltip(getToolTip("VRDexe"));
      tivo_name.setTooltip(getToolTip("tivo_name"));
      tivo_ip.setTooltip(getToolTip("tivo_ip"));
      share_name.setTooltip(getToolTip("share_name"));
      share_dir.setTooltip(getToolTip("share_dir"));
      autotune_enabled.setTooltip(getToolTip("autotune_enabled"));
      autotune_channel_interval.setTooltip(getToolTip("autotune_channel_interval"));
      autotune_button_interval.setTooltip(getToolTip("autotune_button_interval"));
      autotune_chan1.setTooltip(getToolTip("autotune_chan1"));
      autotune_chan2.setTooltip(getToolTip("autotune_chan2"));
      autotune_tivoName.setTooltip(getToolTip("autotune_tivoName"));
      add.setTooltip(getToolTip("add")); 
      del.setTooltip(getToolTip("del")); 
      share_add.setTooltip(getToolTip("share_add")); 
      share_del.setTooltip(getToolTip("share_del")); 
      remove_tivo.setTooltip(getToolTip("remove_tivo"));
      remove_comcut.setTooltip(getToolTip("remove_comcut"));
      remove_comcut_mpeg.setTooltip(getToolTip("remove_comcut_mpeg"));
      remove_mpeg.setTooltip(getToolTip("remove_mpeg"));
      QSFixBackupMpegFile.setTooltip(getToolTip("QSFixBackupMpegFile"));
      download_check_length.setTooltip(getToolTip("download_check_length"));
      check_space.setTooltip(getToolTip("check_space"));
      beacon.setTooltip(getToolTip("beacon"));
      UseOldBeacon.setTooltip(getToolTip("UseOldBeacon"));
      npl_when_started.setTooltip(getToolTip("npl_when_started"));
      showHistoryInTable.setTooltip(getToolTip("showHistoryInTable"));
      download_time_estimate.setTooltip(getToolTip("download_time_estimate"));
      UseAdscan.setTooltip(getToolTip("UseAdscan"));
      VRD.setTooltip(getToolTip("VRD"));
      VrdReview.setTooltip(getToolTip("VrdReview"));
      comskip_review.setTooltip(getToolTip("comskip_review"));
      VrdReview_noCuts.setTooltip(getToolTip("VrdReview_noCuts"));
      VrdQsFilter.setTooltip(getToolTip("VrdQsFilter"));
      VrdDecrypt.setTooltip(getToolTip("VrdDecrypt"));
      DsdDecrypt.setTooltip(getToolTip("DsdDecrypt"));
      tivolibreDecrypt.setTooltip(getToolTip("tivolibreDecrypt"));
      tivolibreCompat.setTooltip(getToolTip("tivolibreCompat"));
      httpserver_enable.setTooltip(getToolTip("httpserver_enable"));
      httpserver_share_filter.setTooltip(getToolTip("httpserver_share_filter"));
      VrdEncode.setTooltip(getToolTip("VrdEncode"));
      VrdAllowMultiple.setTooltip(getToolTip("VrdAllowMultiple"));
      VrdCombineCutEncode.setTooltip(getToolTip("VrdCombineCutEncode"));
      VrdQsfixMpeg2ps.setTooltip(getToolTip("VrdQsfixMpeg2ps"));
      VrdOneAtATime.setTooltip(getToolTip("VrdOneAtATime"));
      TSDownload.setTooltip(getToolTip("TSDownload"));
      TivoWebPlusDelete.setTooltip(getToolTip("TivoWebPlusDelete"));
      rpcDelete.setTooltip(getToolTip("rpcDelete"));
      rpcOld.setTooltip(getToolTip("rpcOld"));
      HideProtectedFiles.setTooltip(getToolTip("HideProtectedFiles"));
      OverwriteFiles.setTooltip(getToolTip("OverwriteFiles"));
      DeleteFailedDownloads.setTooltip(getToolTip("DeleteFailedDownloads"));
      combine_download_decrypt.setTooltip(getToolTip("combine_download_decrypt"));
      single_download.setTooltip(getToolTip("single_download"));
      rpcnpl.setTooltip(getToolTip("rpcnpl"));
      enableRpc.setTooltip(getToolTip("enableRpc"));
      persistQueue.setTooltip(getToolTip("persistQueue"));
      files_path.setTooltip(getToolTip("files_path"));
      MAK.setTooltip(getToolTip("MAK"));
      FontSize.setTooltip(getToolTip("FontSize"));
      file_naming.setTooltip(getToolTip("file_naming"));
      tivo_output_dir.setTooltip(getToolTip("tivo_output_dir"));
      mpeg_output_dir.setTooltip(getToolTip("mpeg_output_dir"));
      qsfixDir.setTooltip(getToolTip("qsfixDir"));
      mpeg_cut_dir.setTooltip(getToolTip("mpeg_cut_dir"));
      encode_output_dir.setTooltip(getToolTip("encode_output_dir"));
      tivodecode.setTooltip(getToolTip("tivodecode"));
      dsd.setTooltip(getToolTip("dsd"));
      ffmpeg.setTooltip(getToolTip("ffmpeg"));
      mediainfo.setTooltip(getToolTip("mediainfo"));
      mencoder.setTooltip(getToolTip("mencoder"));
      handbrake.setTooltip(getToolTip("handbrake"));
      comskip.setTooltip(getToolTip("comskip"));
      comskip_ini.setTooltip(getToolTip("comskip_ini"));
      t2extract.setTooltip(getToolTip("t2extract"));
      //t2extract_args.setTooltip(getToolTip("t2extract_args"));
      ccextractor.setTooltip(getToolTip("ccextractor"));
      AtomicParsley.setTooltip(getToolTip("AtomicParsley"));
      wan_http_port.setTooltip(getToolTip("wan_http_port"));
      wan_https_port.setTooltip(getToolTip("wan_https_port"));
      wan_rpc_port.setTooltip(getToolTip("wan_rpc_port"));
      limit_npl_fetches.setTooltip(getToolTip("limit_npl_fetches"));
      active_job_limit.setTooltip(getToolTip("active_job_limit"));
      disk_space.setTooltip(getToolTip("disk_space"));
      customCommand.setTooltip(getToolTip("customCommand"));
      keywords.setTooltip(getToolTip("keywords"));
      customFiles.setTooltip(getToolTip("customFiles")); 
      OK.setTooltip(getToolTip("OK")); 
      CANCEL.setTooltip(getToolTip("CANCEL"));
      autotune_test.setTooltip(getToolTip("autotune_test"));
      toolTips.setTooltip(getToolTip("toolTips"));
      slingBox.setTooltip(getToolTip("slingBox"));
      tableColAutoSize.setTooltip(getToolTip("tableColAutoSize"));
      jobMonitorFullPaths.setTooltip(getToolTip("jobMonitorFullPaths"));
      toolTipsDelay.setTooltip(getToolTip("toolTipsDelay")); 
      toolTipsTimeout.setTooltip(getToolTip("toolTipsTimeout")); 
      cpu_cores.setTooltip(getToolTip("cpu_cores"));
      download_tries.setTooltip(getToolTip("download_tries"));
      download_retry_delay.setTooltip(getToolTip("download_retry_delay"));
      download_delay.setTooltip(getToolTip("download_delay"));
      metadata_entries.setTooltip(getToolTip("metadata_entries"));
      httpserver_port.setTooltip(getToolTip("httpserver_port"));
      httpserver_cache.setTooltip(getToolTip("httpserver_cache"));
      autoLogSizeMB.setTooltip(getToolTip("autoLogSizeMB"));
      web_query.setTooltip(getToolTip("web_query"));
      web_browser.setTooltip(getToolTip("web_browser"));
      tivo_username.setTooltip(getToolTip("tivo_username"));
      tivo_password.setTooltip(getToolTip("tivo_password"));
      pyTivo_host.setTooltip(getToolTip("pyTivo_host"));
      pyTivo_config.setTooltip(getToolTip("pyTivo_config"));
      pyTivo_tivo.setTooltip(getToolTip("pyTivo_tivo"));
      pyTivo_files.setTooltip(getToolTip("pyTivo_files"));
      metadata_files.setTooltip(getToolTip("metadata_files"));
      lookAndFeel.setTooltip(getToolTip("lookAndFeel"));
      MinChanDigits.setTooltip(getToolTip("MinChanDigits"));
   }
   
   public Tooltip getToolTip(String component) {
      debug.print("component=" + component);
      String text = "";
      
      try {
	      // we set up tooltip text for most component ids with "tooltip_" in front of them.
	      String tip = bundle.getString("tooltip_"+component);
	      if(tip != null && tip.length() > 0) {
	    	  text = tip;
	      }
      } catch (Exception e) {
    	  text = "";
      }
      
      return MyTooltip.make(text);
   }
      
}
