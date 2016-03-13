package com.tivo.kmttg.gui.dialog;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Stack;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.tivo.kmttg.gui.MyTooltip;
import com.tivo.kmttg.gui.table.autoTable;
import com.tivo.kmttg.gui.table.autoTable.Tabentry;
import com.tivo.kmttg.main.autoConfig;
import com.tivo.kmttg.main.autoEntry;
import com.tivo.kmttg.main.config;
import com.tivo.kmttg.main.encodeConfig;
import com.tivo.kmttg.util.debug;
import com.tivo.kmttg.util.file;
import com.tivo.kmttg.util.log;
import com.tivo.kmttg.util.string;

/**
 * The controller class for configAuto.fxml, includes static method that loads
 * the fxml and resource bundle.
 */
public class configAuto implements Initializable {
	
   private Stack<TextField> errors = new Stack<TextField>();
   private String textbg_default = "";
   private double pos_x = -1;
   private double pos_y = -1;
   
   private static configAuto singleton = null;
   private static ResourceBundle bundle;
   private static Stage dialog = null;
   @FXML private VBox content;
   @FXML private Button add;
   @FXML private Button del;
   @FXML private Button update;
   @FXML private Label text;
   private autoTable table = null;
   @FXML private ScrollPane table_scroll;
   @FXML private ChoiceBox<String> type;
   @FXML private ChoiceBox<String> tivo;
   @FXML private ChoiceBox<String> encoding_name;
   @FXML private ChoiceBox<String> encoding_name2;
   @FXML private TextField encoding_name2_suffix;
   @FXML private CheckBox enabled;
   @FXML private CheckBox metadata;
   @FXML private CheckBox decrypt;
   @FXML private CheckBox qsfix;
   @FXML private CheckBox twpdelete;
   @FXML private CheckBox rpcdelete;
   @FXML private CheckBox comskip;
   @FXML private CheckBox comcut;
   @FXML private CheckBox captions;
   @FXML private CheckBox encode;
   @FXML private CheckBox push;
   @FXML private CheckBox custom;
   @FXML private CheckBox dry_run;
   @FXML private CheckBox noJobWait;
   @FXML private TextField title;
   @FXML private TextField check_interval;
   @FXML private TextField comskipIni;
   @FXML private TextField channelFilter;
   @FXML private TextField tivoFileNameFormat;
   @FXML private CheckBox dateFilter;
   @FXML private CheckBox suggestionsFilter;
   @FXML private CheckBox suggestionsFilter_single;
   @FXML private CheckBox useProgramId_unique;
   @FXML private CheckBox kuidFilter;
   @FXML private CheckBox programIdFilter;
   @FXML private ChoiceBox<String> dateOperator;
   @FXML private TextField dateHours;
   @FXML private Button OK;
   @FXML private Button CANCEL;
   /** needed to use the reference to the row in two places in the fxml */
   @FXML private GridPane row5;

   
   private String _noSecondEncodingTxt;
   private String _TivosAll;

   public static void display(Stage frame) {
      debug.print("frame=" + frame);
      // Controller/fxml instantiation has to be done from outside the
      // controller, so we do it in static code and save the controller as a
      // singleton.
      // Create dialog if not already created
      if (dialog == null) {
         create(frame);
         
         // all other basic initialization is done when "initialize" is
         // automatically called when the fxml file is loaded.
      }
      
      // Parse auto.ini file to define current configuration
      autoConfig.parseAuto(config.autoIni);
      
      // Clear out any error highlights
      singleton.clearTextFieldErrors();
      
      // Update component settings to current configuration
      singleton.update();
      
      // Refresh available options based on settings
      singleton.refreshOptions();
      
      // Display the dialog
      if (singleton.pos_x != -1)
         dialog.setX(singleton.pos_x);
      if (singleton.pos_y != -1)
         dialog.setY(singleton.pos_y);
      dialog.show();
   }
   
   private void textFieldError(TextField f, String message) {
      debug.print("f=" + f + " message=" + message);
      log.error(message);
      f.setStyle("-fx-background-color: " + config.gui.getWebColor(config.lightRed));
      errors.add(f);
   }
   
   private void clearTextFieldErrors() {
      debug.print("");
      if (errors.size() > 0) {
         for (int i=0; i<errors.size(); i++) {
            errors.get(i).setStyle(textbg_default);
         }
         errors.clear();
      }
   }
  
   private static void create(Stage frame) {
      debug.print("frame=" + frame);
      
      // Create all the components of the dialog
      Parent configAuto_fxml;
      try {
    	  FXMLLoader loader = new FXMLLoader(configAuto.class.getResource(
    					  "configAuto.fxml"));
    	  bundle = ResourceBundle.getBundle("com.tivo.kmttg.gui.dialog.configAuto");
    	  loader.setResources(bundle);
    	  configAuto_fxml = loader.<Parent>load();
      	  Scene scene = new Scene(configAuto_fxml);
      	  scene.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, config.gui.contextHelpMouseEventHandler);
      	  // save our official instance of configAuto
      	  singleton = loader.<configAuto>getController();
      	  
      	  // all the controller adjustments are done in the initialize method
      	  // automatically called by FXMLLoader
      	  
          // create dialog window
          dialog = new Stage();
          dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
             @Override
             public void handle(WindowEvent arg0) {
                singleton.pos_x = dialog.getX(); singleton.pos_y = dialog.getY();
             }
          });
          dialog.initOwner(frame);
          dialog.initModality(Modality.NONE);
          dialog.setTitle(bundle.getString("dialog_title"));
          
//          Scene scene = new Scene(new VBox());
//          
//          ((VBox) scene.getRoot()).getChildren().add(content);
      	  
            config.gui.setFontSize(scene, config.FontSize);
            
            dialog.setScene(scene);
            
        } catch (IOException e1) {
      	  // TODO Auto-generated catch block
      	  e1.printStackTrace();
      	  dialog = null;
      	  singleton = null;
        }
   }
   
   /**
    * Automatically called after the fxml file is loaded.  
    * Most initialization is done in the fxml file, though.
    */
   public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
	   _noSecondEncodingTxt = resources.getString("_noSecondEncodingTxt");
	   _TivosAll = resources.getString("_TivosAll");
	   
       table = new autoTable();
       table.TABLE.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tabentry>() {
          @Override
          public void changed(ObservableValue<? extends Tabentry> obs, Tabentry oldSelection, Tabentry newSelection) {
             if (newSelection != null) {
                TableRowSelected(newSelection.getType().entry);
             }
          }
       });
       

	   table_scroll.setContent(table.TABLE);

      // choicebox values can't be filled in fxml when you're using a bundle 
      type.getItems().addAll(resources.getString("type_title"), resources.getString("type_keywords"));

      for (String s : getTivoFilterNames()) {
         tivo.getItems().add(s);
      }
      if (tivo.getItems().size() > 0)
         tivo.setValue(tivo.getItems().get(0));
      
      // This intentionally disabled for now
      //encode.addActionListener(new ActionListener() {
      //   public void actionPerformed(ActionEvent e) {
      //      boolean selected = encode.isSelected();
      //      if (config.VRD == 0) {
      //         if (selected) {
      //            if (config.OS.equals("windows") && file.isFile(config.mencoder)) {
      //               qsfix.setEnabled(true);
      //               qsfix.setSelected(true);
      //            }
      //         } else {
      //            qsfix.setEnabled(false);
      //            qsfix.setSelected(false);
      //         }
      //      }
      //   }
      //});
      
      SetEncodings(encodeConfig.getValidEncodeNames());
      
      dry_run.setSelected((Boolean)(autoConfig.dryrun == 1));
      
      noJobWait.setSelected((Boolean)(autoConfig.noJobWait == 1));
      
      // choicebox values can't be filled in fxml when you're using a bundle 
      dateOperator.getItems().addAll(resources.getString("date_morethan"), resources.getString("date_lessthan"));
      
      if (config.twpDeleteEnabled()) {
    	  // keep it
      } else {
    	  // hide it
    	  twpdelete.setManaged(false);
    	  twpdelete.setVisible(false);
      }
      if (config.rpcDeleteEnabled()) {
    	  // keep it
      }
      else {
    	  // hide it
    	  rpcdelete.setManaged(false);
    	  rpcdelete.setVisible(false);
      }

      setToolTips();
   }
   
   /** Component tooltip setup */
   public void setToolTips() {
      enabled.setTooltip(getToolTip("enabled"));
      
      metadata.setTooltip(config.gui.getToolTip("metadata"));
      decrypt.setTooltip(config.gui.getToolTip("decrypt"));
      qsfix.setTooltip(config.gui.getToolTip("qsfix"));
      twpdelete.setTooltip(config.gui.getToolTip("twpdelete"));
      rpcdelete.setTooltip(config.gui.getToolTip("rpcdelete"));
      comskip.setTooltip(config.gui.getToolTip("comskip"));
      comcut.setTooltip(config.gui.getToolTip("comcut"));
      captions.setTooltip(config.gui.getToolTip("captions"));
      encode.setTooltip(config.gui.getToolTip("encode"));
      push.setTooltip(config.gui.getToolTip("push"));
      custom.setTooltip(config.gui.getToolTip("custom"));
      encoding_name.setTooltip(config.gui.getToolTip("encoding"));
      encoding_name2.setTooltip(config.gui.getToolTip("encoding2"));
      encoding_name2_suffix.setTooltip(config.gui.getToolTip("encoding2_suffix"));
      
      table.TABLE.setTooltip(getToolTip("table"));
      type.setTooltip(getToolTip("type"));
      tivo.setTooltip(getToolTip("tivo"));
      dry_run.setTooltip(getToolTip("dry_run"));
      noJobWait.setTooltip(getToolTip("noJobWait"));
      title.setTooltip(getToolTip("title"));
      comskipIni.setTooltip(getToolTip("comskipIni"));
      channelFilter.setTooltip(getToolTip("channelFilter"));
      tivoFileNameFormat.setTooltip(getToolTip("tivoFileNameFormat"));
      check_interval.setTooltip(getToolTip("check_interval"));
      add.setTooltip(getToolTip("add"));
      update.setTooltip(getToolTip("update"));
      del.setTooltip(getToolTip("del"));      
      dateFilter.setTooltip(getToolTip("dateFilter"));
      suggestionsFilter.setTooltip(getToolTip("suggestionsFilter"));
      suggestionsFilter_single.setTooltip(getToolTip("suggestionsFilter_single"));
      useProgramId_unique.setTooltip(getToolTip("useProgramId_unique"));
      kuidFilter.setTooltip(getToolTip("kuidFilter"));
      programIdFilter.setTooltip(getToolTip("programIdFilter"));
      dateOperator.setTooltip(getToolTip("dateOperator"));
      dateHours.setTooltip(getToolTip("dateHours"));
      OK.setTooltip(getToolTip("OK"));
      CANCEL.setTooltip(getToolTip("CANCEL"));      
   }
   
   public Tooltip getToolTip(String component) {
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
   
   private void setTivoFilterNames() {
      tivo.getItems().clear();
      String[] names = getTivoFilterNames();
      for (int i=0; i<names.length; ++i) {
         tivo.getItems().add(names[i]);
      }
      if (tivo.getItems().size() > 0)
         tivo.setValue(tivo.getItems().get(0));
   }
   
   /** Defines choices for tivo name filtering */
   private String[] getTivoFilterNames() {
      Stack<String> names = config.getNplTivoNames();
      names.add(0, _TivosAll);
      String[] tivoNames = new String[names.size()];
      for (int i=0; i<names.size(); ++i) {
         tivoNames[i] = names.get(i);
      }
      return tivoNames;
   }

   /** Checks given tivo name against current valid names and resets to all if not valid */
   private String validateTivoName(String tivoName) {
      if ( ! tivoName.equals(_TivosAll) ) {
         Stack<String> names = config.getNplTivoNames();
         for (int i=0; i<names.size(); ++i) {
            if (tivoName.equals(names.get(i)))
               return tivoName;
         }
         log.error("TiVo '" + tivoName + "' currently not configured in kmttg - resetting to all");
      }
      return _TivosAll;
   }
   
   /** This will decide which options are enabled based on current config settings.
    Options are disabled when associated config entry is not setup */
   @FXML
   public void refreshOptions() {
      if (config.VRD == 0 && ! file.isFile(config.ffmpeg)) {
         qsfix.setSelected(false);
         qsfix.setDisable(true);
      } else {
         qsfix.setDisable(false);
      }
      
      if (!config.twpDeleteEnabled()) {
         twpdelete.setSelected(false);
         twpdelete.setDisable(true);
      } else {
         twpdelete.setDisable(false);
      }
      
      if ( ! config.rpcDeleteEnabled() ) {
         rpcdelete.setSelected(false);
         rpcdelete.setDisable(true);
      } else {
         rpcdelete.setDisable(false);
      }

      if (! file.isFile(config.comskip)) {
         comskip.setSelected(false);
         comskip.setDisable(true);
      } else {
         comskip.setDisable(false);
      }

      if (config.VRD == 0 && ! file.isFile(config.ffmpeg)) {
         comcut.setSelected(false);
         comcut.setDisable(true);
      } else {
         comcut.setDisable(false);
      }

      if (! file.isFile(config.t2extract) && ! file.isFile(config.ccextractor)) {
         captions.setSelected(false);
         captions.setDisable(true);
      } else {
         captions.setDisable(false);
      }
      if (config.VRD == 0 && qsfix.isSelected()) {
         captions.setSelected(false);
         captions.setDisable(true);         
      }

      if (! file.isFile(config.ffmpeg) &&
          ! file.isFile(config.mencoder) &&
          ! file.isFile(config.handbrake) ) {
         encode.setSelected(false);
         encode.setDisable(true);
      } else {
         encode.setDisable(false);
      }

      if ( ! file.isFile(config.pyTivo_config) ) {
         push.setSelected(false);
         push.setDisable(true);
      } else {
         push.setDisable(false);
      }
      
      if ( ! com.tivo.kmttg.task.custom.customCommandExists() ) {
         custom.setSelected(false);
         custom.setDisable(true);
      } else {
         custom.setDisable(false);
      }
      
   }
   
   public void clearTable() {
      debug.print("");
      table.clear();
   }
   
   public void addTableRow(autoEntry entry) {
      debug.print("entry=" + entry);
      table.AddRow(entry);
   }
   
   public void removeTableRow(int row) {
      debug.print("row=" + row);
      table.RemoveRow(row);
   }
  
   public int[] getTableSelectedRows() {
      debug.print("");
      int[] rows = table.getSelectedRows();
      if (rows.length <= 0)
         log.error("No rows selected");
      return rows;
   }
     
   /** Return autoEntry instance of selected entry */
   public autoEntry GetRowData(int row) {
      return table.GetRowData(row);
   }
   
   /** Update dialog settings based on autoConfig current settings */
   public void update() {
      SetKeywords(autoConfig.KEYWORDS);
      SetEncodings(encodeConfig.getValidEncodeNames());
      setTivoFilterNames();
      check_interval.setText("" + autoConfig.CHECK_TIVOS_INTERVAL);      
      dry_run.setSelected((Boolean)(autoConfig.dryrun == 1));
      noJobWait.setSelected((Boolean)(autoConfig.noJobWait == 1));
      dateFilter.setSelected((Boolean)(autoConfig.dateFilter == 1));
      dateOperator.setValue(autoConfig.dateOperator);
      dateHours.setText("" + autoConfig.dateHours);
      suggestionsFilter.setSelected((Boolean)(autoConfig.suggestionsFilter == 1));
      kuidFilter.setSelected((Boolean)(autoConfig.kuidFilter == 1));
      programIdFilter.setSelected((Boolean)(autoConfig.programIdFilter == 1));
   }
   
   /** Set encoding_name ChoiceBox choices */
   public void SetEncodings(Stack<String> values) {
      debug.print("values=" + values);
      
      encoding_name.getItems().clear();
      encoding_name2.getItems().clear();
      
      // Second encoding optional
      encoding_name2.getItems().add(_noSecondEncodingTxt);
      
      for (int i=0; i<values.size(); ++i) {
         encoding_name.getItems().add(values.get(i));
         encoding_name2.getItems().add(values.get(i));
      }
      if (encoding_name.getItems().size() > 0)
         encoding_name.setValue(encoding_name.getItems().get(0));
      if (encoding_name2.getItems().size() > 0)
         encoding_name2.setValue(encoding_name2.getItems().get(0));      
   }
   
   /** Set table entries according to auto config setup */
   public void SetKeywords(Stack<autoEntry> entries) {
      debug.print("entries=" + entries);
      clearTable();
      if (entries.size() > 0) {
         for (int i=0; i<entries.size(); i++) {
            addTableRow(entries.get(i));
         }
      }
   }   
   
   /** Callback for ADD button.
   Add type & keywords as a table entry */
   @FXML
   private void addCB() {
      debug.print("");
      String ktype = type.getValue();
      String keywords = string.removeLeadingTrailingSpaces(title.getText());
      if (keywords.length() == 0) {
         log.error("No keywords specified");
         return;
      }
      
      // Make sure this is not a duplicate entry
      Boolean duplicate = false;
      if (table.TABLE.getItems().size() > 0) {
         for (int i=0; i<table.TABLE.getItems().size(); ++i) {
            autoEntry check = GetRowData(i);
            if (check.type.equals(ktype)) {
               if (check.type.equals("title")) {
                  if (keywords.equals(check.keyword)) duplicate = true;
               } else {
                  if (keywords.equals(autoConfig.keywordsToString(check.keywords))) duplicate = true;
               }
            }
         }
      }      
      if (duplicate) {
         log.error("Duplicate entry, not adding");
         return;
      }
      
      autoEntry entry = new autoEntry();
      // Set entry settings based on dialog settings
      guiToEntry(entry);
      
      // Add a new table row
      addTableRow(entry);
   }
   
   /** Callback for UPDATE button.
   Update selected table entry with dialog settings */
   @FXML
   private void updateCB() {
      debug.print("");
      int[] rows = getTableSelectedRows();
      if (rows.length == 0) {
         log.error("No table row selected");
         return;
      }
   
      int row = rows[0]; // Process top most row
      autoEntry entry = GetRowData(row);
      
      // Update entry settings
      guiToEntry(entry);
      
      // Update table settings
      Tabentry e = table.TABLE.getItems().get(row);
      e.type = new autoTableEntry(entry);      
      if (entry.type.equals("title"))
         e.keywords = entry.keyword;
      else
         e.keywords = autoConfig.keywordsToString(entry.keywords);
      
      table.resize();
      log.warn("Updated auto transfers entry # " + (row+1));
   }
   
   /** Callback for DEL button.
   Remove selected table entries */
   @FXML
   private void delCB() {
      debug.print("");
      int[] rows = getTableSelectedRows();
      for (int i=rows.length-1; i>-1; --i) {
         removeTableRow(rows[i]);
      }      
   }

   @FXML
   private void cancelCB() {
       pos_x = dialog.getX(); pos_y = dialog.getY();
       dialog.hide();
   }
   
   /** Callback for OK button.
   Save table settings to auto.ini and hide the dialog */
   @FXML
   private void okCB() {
      debug.print("");
      clearTextFieldErrors();
      // Error checking
      int interval = 60;
      String value = string.removeLeadingTrailingSpaces(check_interval.getText());
      try {
         interval = Integer.parseInt(value);
      } catch(NumberFormatException e) {
         textFieldError(check_interval, "check interval should be an integer: '" + value + "'");
         return;
      }
      
      float hours = 48;
      value = string.removeLeadingTrailingSpaces(dateHours.getText());
      try {
         hours = Float.parseFloat(value);
      } catch(NumberFormatException e) {
         textFieldError(check_interval, "Date Filter hours should be of type float: '" + value + "'");
         return;
      }
      
      // Write to file
      try {
         BufferedWriter ofp = new BufferedWriter(new FileWriter(config.autoIni));
         ofp.write("# kmttg auto.ini file\n");
         ofp.write("<check_tivos_interval>\n" + interval + "\n\n");
         ofp.write("<dryrun>\n");
         if (dry_run.isSelected())
            ofp.write("1\n\n");
         else
            ofp.write("0\n\n");
         ofp.write("<noJobWait>\n");
         if (noJobWait.isSelected())
            ofp.write("1\n\n");
         else
            ofp.write("0\n\n");
         ofp.write("<dateFilter>\n");
         if (dateFilter.isSelected())
            ofp.write("1\n\n");
         else
            ofp.write("0\n\n");
         ofp.write("<dateOperator>\n" + dateOperator.getValue() + "\n\n");
         ofp.write("<dateHours>\n" + hours + "\n\n");
         ofp.write("<suggestionsFilter>\n");
         if (suggestionsFilter.isSelected())
            ofp.write("1\n\n");
         else
            ofp.write("0\n\n");
         ofp.write("<kuidFilter>\n");
         if (kuidFilter.isSelected())
            ofp.write("1\n\n");
         else
            ofp.write("0\n\n");
         ofp.write("<programIdFilter>\n");
         if (programIdFilter.isSelected())
            ofp.write("1\n\n");
         else
            ofp.write("0\n\n");
         
         int rows = table.TABLE.getItems().size();
         if (rows > 0) {
            autoEntry entry;
            for (int i=0; i<rows; ++i) {
               entry = GetRowData(i);
               // Some options may have to be turned off for disabled features
               if ( ! config.twpDeleteEnabled() )
                  entry.twpdelete = 0;
               if ( ! config.rpcDeleteEnabled() )
                  entry.rpcdelete = 0;
               ofp.write("\n");
               if (entry.type.equals("title")) {
                  ofp.write("<title>\n");
                  ofp.write(entry.keyword + "\n");
               } else {
                  ofp.write("<keywords>\n");
                  ofp.write(autoConfig.keywordsToString(entry.keywords) + "\n");
               }
               ofp.write("<options>\n");
               ofp.write("enabled "             + entry.enabled             + "\n");
               ofp.write("tivo "                + entry.tivo                + "\n");
               ofp.write("metadata "            + entry.metadata            + "\n");               
               ofp.write("decrypt "             + entry.decrypt             + "\n");               
               ofp.write("qsfix "               + entry.qsfix               + "\n");               
               ofp.write("twpdelete "           + entry.twpdelete           + "\n");               
               ofp.write("rpcdelete "          + entry.rpcdelete          + "\n");               
               ofp.write("comskip "             + entry.comskip             + "\n");               
               ofp.write("comcut "              + entry.comcut              + "\n");               
               ofp.write("captions "            + entry.captions            + "\n");               
               ofp.write("encode "              + entry.encode              + "\n");
               ofp.write("push "                + entry.push                + "\n");
               ofp.write("custom "              + entry.custom              + "\n");
               ofp.write("suggestionsFilter "   + entry.suggestionsFilter   + "\n");
               ofp.write("useProgramId_unique " + entry.useProgramId_unique + "\n");
               if (entry.encode_name != null && entry.encode_name.length() > 0)
                  ofp.write("encode_name " + entry.encode_name + "\n");
               if (entry.encode_name2 != null && entry.encode_name2.length() > 0)
                   ofp.write("encode_name2 " + entry.encode_name2 + "\n");
               if (entry.encode_name2_suffix != null && entry.encode_name2_suffix.length() > 0)
                   ofp.write("encode_name2_suffix " + entry.encode_name2_suffix + "\n");
               if (entry.channelFilter != null && entry.channelFilter.length() > 0)
                  ofp.write("channelFilter " + entry.channelFilter + "\n");
               if (entry.tivoFileNameFormat != null && entry.tivoFileNameFormat.length() > 0)
                  ofp.write("tivoFileNameFormat " + entry.tivoFileNameFormat + "\n");
               if (file.isFile(entry.comskipIni))
                  ofp.write("comskipIni " + entry.comskipIni + "\n");
               else
                  ofp.write("comskipIni " + "none" + "\n");
            }
         }
         
         ofp.close();
      } catch (IOException ex) {
         log.error("Cannot write to auto config file: " + config.autoIni);
         log.error(ex.toString());
         return;
      } 
      
      log.warn("Auto config settings saved");
      
      // Close dialog
      pos_x = dialog.getX(); pos_y = dialog.getY();
      dialog.hide();
      
      // Update autoConfig settings      
      autoConfig.parseAuto(config.autoIni);
   }
   
   /** Callback when user clicks on a table row
   This will update component settings according to selected row data */
   private void TableRowSelected(autoEntry entry) {
      enabled.setSelected((Boolean)(entry.enabled == 1));
      metadata.setSelected((Boolean)(entry.metadata == 1));
      decrypt.setSelected((Boolean)(entry.decrypt == 1));
      qsfix.setSelected((Boolean)(entry.qsfix == 1));
      twpdelete.setSelected((Boolean)(entry.twpdelete == 1));
      rpcdelete.setSelected((Boolean)(entry.rpcdelete == 1));
      comskip.setSelected((Boolean)(entry.comskip == 1));
      comcut.setSelected((Boolean)(entry.comcut == 1));
      captions.setSelected((Boolean)(entry.captions == 1));
      encode.setSelected((Boolean)(entry.encode == 1));
      push.setSelected((Boolean)(entry.push == 1));
      custom.setSelected((Boolean)(entry.custom == 1));
      suggestionsFilter_single.setSelected((Boolean)(entry.suggestionsFilter == 1));
      useProgramId_unique.setSelected((Boolean)(entry.useProgramId_unique == 1));
      
      encoding_name.setValue(entry.encode_name);
      
      if (entry.encode_name2 != null) {
    	  encoding_name2.setValue(entry.encode_name2);
    	  encoding_name2_suffix.setText(entry.encode_name2_suffix);
      } else
    	  encoding_name2.setValue(_noSecondEncodingTxt);
      
      comskipIni.setText(entry.comskipIni);
      
      if (entry.channelFilter != null)
         channelFilter.setText(entry.channelFilter);
      else
         channelFilter.setText("");
      
      if (entry.tivoFileNameFormat != null)
         tivoFileNameFormat.setText(entry.tivoFileNameFormat);
      else
         tivoFileNameFormat.setText("");
      
      type.setValue(entry.type);
      
      entry.tivo = validateTivoName(entry.tivo);
      tivo.setValue(entry.tivo);
      
      if (entry.type.equals("title")) {
         title.setText(entry.keyword);
      } else {
         title.setText(autoConfig.keywordsToString(entry.keywords));
      }
   }
   
   private Boolean guiToEntry(autoEntry entry) {
      String ktype = type.getValue();
      String ktivo = tivo.getValue();
      String keywords = string.removeLeadingTrailingSpaces(title.getText());
      if (keywords.length() == 0) {
         log.error("No keywords specified");
         return false;
      }
      
      if (enabled.isSelected())
         entry.enabled = 1;
      else
         entry.enabled = 0;
      
      if (metadata.isSelected())
         entry.metadata = 1;
      else
         entry.metadata = 0;
      
      if (decrypt.isSelected())
         entry.decrypt = 1;
      else
         entry.decrypt = 0;
      
      if (qsfix.isSelected())
         entry.qsfix = 1;
      else
         entry.qsfix = 0;
      
      if (twpdelete.isSelected())
         entry.twpdelete = 1;
      else
         entry.twpdelete = 0;
      
      if (rpcdelete.isSelected())
         entry.rpcdelete = 1;
      else
         entry.rpcdelete = 0;
      
      if (comskip.isSelected())
         entry.comskip = 1;
      else
         entry.comskip = 0;
      
      if (comcut.isSelected())
         entry.comcut = 1;
      else
         entry.comcut = 0;
      
      if (captions.isSelected())
         entry.captions = 1;
      else
         entry.captions = 0;
      
      if (encode.isSelected())
         entry.encode = 1;
      else
         entry.encode = 0;
      
      if (push.isSelected())
         entry.push = 1;
      else
         entry.push = 0;
      
      if (custom.isSelected())
         entry.custom = 1;
      else
         entry.custom = 0;
      
      if (suggestionsFilter_single.isSelected())
         entry.suggestionsFilter = 1;
      else
         entry.suggestionsFilter = 0;
      
      if (useProgramId_unique.isSelected())
         entry.useProgramId_unique = 1;
      else
         entry.useProgramId_unique = 0;
      
      entry.encode_name = encoding_name.getValue();
      
      // Does user want to encode second time? save profile name
      if (encoding_name2.getValue().equals(_noSecondEncodingTxt))
    	  entry.encode_name2 = null;
      else {
    	  entry.encode_name2 = encoding_name2.getValue();
    	  entry.encode_name2_suffix = encoding_name2_suffix.getText();
      }

      String ini = (String)string.removeLeadingTrailingSpaces(comskipIni.getText());
      if (ini.length() > 0 && ! ini.equals("none")) {
         if ( ! file.isFile(ini) ) {
            log.error("Specified comskip.ini override file does not exist...");
         }
      }
      entry.comskipIni = ini;
      
      String cFilter = (String)string.removeLeadingTrailingSpaces(channelFilter.getText());
      if (cFilter.length() > 0)
         entry.channelFilter = cFilter;
      else
         entry.channelFilter = null;
      
      cFilter = (String)string.removeLeadingTrailingSpaces(tivoFileNameFormat.getText());
      if (cFilter.length() > 0)
         entry.tivoFileNameFormat = cFilter;
      else
         entry.tivoFileNameFormat = null;
      
      entry.type = ktype;
      
      entry.tivo = ktivo;
      
      if (ktype.equals("title")) {
         entry.keyword = keywords;
      } else {
         autoConfig.stringToKeywords(keywords, entry);
      }
      
      return true;

   }
}
