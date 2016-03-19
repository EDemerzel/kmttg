package com.tivo.kmttg.gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import org.w3c.dom.NodeList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.sun.javafx.css.StyleManager;
import com.tivo.kmttg.JSON.JSONArray;
import com.tivo.kmttg.JSON.JSONException;
import com.tivo.kmttg.JSON.JSONObject;
import com.tivo.kmttg.gui.dialog.Pushes;
import com.tivo.kmttg.gui.dialog.ShowDetails;
import com.tivo.kmttg.gui.dialog.SkipDialog;
import com.tivo.kmttg.gui.dialog.autoLogView;
import com.tivo.kmttg.gui.dialog.configAuto;
import com.tivo.kmttg.gui.dialog.configMain;
import com.tivo.kmttg.gui.remote.remotegui;
import com.tivo.kmttg.gui.remote.util;
import com.tivo.kmttg.gui.table.TableUtil;
import com.tivo.kmttg.gui.table.jobTable;
import com.tivo.kmttg.gui.table.nplTable;
import com.tivo.kmttg.gui.table.nplTable.Tabentry;
import com.tivo.kmttg.install.mainInstall;
import com.tivo.kmttg.install.update;
import com.tivo.kmttg.main.auto;
import com.tivo.kmttg.main.autoConfig;
import com.tivo.kmttg.main.config;
import com.tivo.kmttg.main.encodeConfig;
import com.tivo.kmttg.main.jobData;
import com.tivo.kmttg.main.jobMonitor;
import com.tivo.kmttg.main.kmttg;
import com.tivo.kmttg.rpc.AutoSkip;
import com.tivo.kmttg.rpc.SkipService;
import com.tivo.kmttg.util.debug;
import com.tivo.kmttg.util.file;
import com.tivo.kmttg.util.log;
import com.tivo.kmttg.util.string;
import javafx.scene.control.TitledPane;

public class gui implements Initializable {

   public ResourceBundle bundle;
   
   @FXML private Stage jFrame = null;
   private String title = config.kmttg;
   @FXML private SplitPane jContentPane = null;
   @FXML private SplitPane recordingsSplit = null;
   @FXML private SplitPane splitBottom = null;
   @FXML private TabPane tabbed_panel = null;
   @FXML private Tab schedules_tab;
   @FXML private Tab apps_tab;
   @FXML private TabPane tabbed_panel_schedules = null;
   @FXML private TabPane tabbed_panel_apps = null;
   @FXML private Menu fileMenu = null;
   @FXML private Menu jobMenu = null;
   @FXML private Menu autoMenu = null;
   // alternates with Background depending on OS
   @FXML private Menu serviceMenuService = null;
   // alternates with Service depending on OS
   @FXML private Menu serviceMenuBackground = null;
   @FXML private Menu helpMenu = null;
   // removed in linux
   @FXML private MenuItem helpToolsUpdateMenuItem = null;
   @FXML private CheckMenuItem loopInGuiMenuItem = null;
   @FXML private CheckMenuItem resumeDownloadsMenuItem = null;
   @FXML private CheckMenuItem toggleLaunchingJobsMenuItem = null;
   @FXML public  MenuItem addSelectedTitlesMenuItem = null;
   @FXML public  MenuItem addSelectedHistoryMenuItem = null;
   // removed if no tivo username
   @FXML private MenuItem pushesMenuItem = null;
   @FXML public MenuItem searchMenuItem = null;
   // removed if feature disabled or no rpc
   @FXML private MenuItem autoSkipMenuItem = null;
   // removed if feature disabled or no rpc
   @FXML public CheckMenuItem skipServiceMenuItem = null;
   @FXML public Boolean skipServiceMenuItem_cb = true;
   // always removed from menu, just there for the shortcut.
   @FXML public MenuItem thumbsMenuItem = null;
   
   @FXML private ChoiceBox<String> encoding = null;
   @FXML private Label encoding_label = null;
   @FXML private Label encoding_description_label = null;
   @FXML public Button start = null;
   @FXML public Button cancel = null;
   @FXML public CheckBox metadata = null;
   @FXML public CheckBox decrypt = null;
   @FXML public CheckBox qsfix = null;
   @FXML public CheckBox twpdelete = null;
   @FXML public CheckBox rpcdelete = null;
   @FXML public CheckBox comskip = null;
   @FXML public CheckBox comcut = null;
   @FXML public CheckBox captions = null;
   @FXML public CheckBox encode = null;
   @FXML public CheckBox push = null;
   @FXML public CheckBox custom = null;
   @FXML private WebView text = null;
   private textpane textp = null;
   private jobTable jobTab = null;
   @FXML private ProgressBar progressBar = null;
   @FXML public  ScrollPane jobPane = null;
   @FXML SplitPane helpSplit;
   @FXML TabPane helpTabPane;
   @FXML TitledPane progressPane;
   
   private Hashtable<String,tivoTab> tivoTabs = new Hashtable<String,tivoTab>();
   public static Hashtable<String,Image> Images;
   
   public remotegui remote_gui = null;
   public slingboxgui  slingbox_gui = null;

   @FXML private StackPane show_details_stack = null;
   @FXML private HBox showDetails = null;
   @FXML private ShowDetails showDetailsController = null;
   public ShowDetails show_details = null;
   @FXML private WebView plain_show_details_view = null;
   public textpane plain_show_details = null;
   
   public Stage getFrame() {
      debug.print("");
      return jFrame;
   }
   
   public tivoTab getTab(String tabName) {
      debug.print("tabName=" + tabName);
      return tivoTabs.get(tabName);
   }
   
public static class guiApp extends Application {
   /**
    * A wrapper for {@link Application#launch(String...)} that, when
    * called, ensures this class is the one that is launched.
    */
   public static void Launch() {
      debug.print("");
      launch();
   }
   
   @Override
   public void start(Stage stage) {
	   try {
      debug.print("stage=" + stage);
      
    	  FXMLLoader loader = new FXMLLoader(gui.class.getResource(
    			  "gui.fxml"));
    	  ResourceBundle bundle = ResourceBundle.getBundle("com.tivo.kmttg.gui.gui");
    	  loader.setResources(bundle);
    	  Parent gui_fxml = loader.<Parent>load();
    	  
    	  // all the adjustments are done in the initialize method
    	  // automatically called by FXMLLoader
    	  // config.gui is set to the controller instance during that initialize
    	  
    	  Scene scene = new Scene(gui_fxml);
    	  // new help tooltip handler
    	  scene.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, config.gui.contextHelpMouseEventHandler);	   
    	  config.gui.jFrame = stage;
//    	  Scene scene = new Scene(new VBox());

    	  // .. but the scene and stage are not available to the controller's initialize.
    	  config.gui.initializeScene(stage, scene);

	   } catch (Exception e1) {
		   // TODO Auto-generated catch block
    	  StringWriter sw = new StringWriter();
    	  PrintWriter writer = new PrintWriter(sw);
    	  e1.printStackTrace(writer);
    	  debug.print("DEBUG: failed to load gui UI:"+sw.toString());
		   config.gui = null;
		   config.gui.jFrame = null;
	   }
   }
}
      
   @Override
   public void initialize(URL location, ResourceBundle resources) {
	   debug.print("");
      bundle = resources;
      config.gui = this;
      show_details = showDetailsController;
      plain_show_details = new textpane(plain_show_details_view);
      
      // add apps and schedules using one controller.
      try {
    	  if(remote_gui == null) {
    		  remote_gui = new remotegui();
    	  }
    	  remotegui controller = remote_gui;
    	  FXMLLoader apps_loader = new FXMLLoader(gui.class.getResource(
    			  "remote/apps.fxml"));
    	  FXMLLoader schedules_loader = new FXMLLoader(gui.class.getResource(
    			  "remote/schedules.fxml"));
    	  apps_loader.setController(controller);
    	  schedules_loader.setController(controller);
    	  
    	  //TODO ResourceBundle for remotegui stuff, for the fxml files
//    	  ResourceBundle bundle = ResourceBundle.getBundle("com.tivo.kmttg.gui.remotegui");
//    	  apps_loader.setResources(bundle);
//    	  schedules_loader.setResources(bundle);    	  
    	  tabbed_panel_apps = apps_loader.<TabPane>load();
    	  tabbed_panel_schedules = schedules_loader.<TabPane>load();
    	  
    	  apps_tab.setContent(tabbed_panel_apps);
    	  schedules_tab.setContent(tabbed_panel_schedules);

    	  // initialize would be called twice because we're using the same controller in both, 
    	  // but that initialize needs all fields filled in.  
    	  // Instead we make remotegui not implement Initializable, 
    	  // and do the work after both fxml files have been loaded.
    	  controller.initializeManual(apps_loader.getLocation(), apps_loader.getResources());
    	  //TODO make sure that used the appropriate bundle for remotegui
    	  
      } catch (Exception e) {
    	  StringWriter sw = new StringWriter();
    	  PrintWriter writer = new PrintWriter(sw);
    	  e.printStackTrace(writer);
    	  debug.print("DEBUG: failed to load remote UI:"+sw.toString());
    	  log.error("failed to load remote UI:"+e);
      }
      
      // adjust the menubar as needed
      initMenuBar();
      
      // adjust the main canvas components
      getContentPane();
   }

   /**
    * Handler applied to every scene containing tooltips to view in the help area.
    */
   public EventHandler<MouseEvent> contextHelpMouseEventHandler = new EventHandler<MouseEvent>() {
	    @Override
	    public void handle(MouseEvent mouseEvent) {
	    	if (helpTabPane != null && helpTabPane.getTabs().size() > 0) {
	    		EventTarget target = mouseEvent.getTarget();
	    		if (target != null && target instanceof Control) {
	    			Tooltip tooltip = ((Control) target).getTooltip();
	    			if (tooltip != null) {
	    				helpTabPane.getTabs().get(0).setContent(tooltip.getGraphic());
	    			}
	    		}
	    	}
	    }
	};
   
   public void initializeScene(Stage stage, Scene scene) {
            
//      // Add additional rpc remote tab
//      addTabPane(bundle.getString("tab_remote"), tabbed_panel_apps, remote_gui.getPanel());
      
      // Init TableMap utility class
      TableMap.init();
      
      setFontSize(scene, config.FontSize);
      jobTab_packColumns(5);
      addGlobalKeyListener(scene);
      jFrame.setScene(scene);      
      jFrame.setTitle(title);
      jFrame.setOnCloseRequest(new EventHandler<WindowEvent>() {
         @Override
         public void handle(WindowEvent event) {
            saveSettings();
            System.exit(0);
         }
      });
      jFrame.setWidth(1000);
      jFrame.setHeight(800);

      // Pack table columns when content pane resized
      scene.widthProperty().addListener(new ChangeListener<Number>() {
         @Override
         public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
            TableUtil.autoSizeTableViewColumns(jobTab.JobMonitor, false);
         }
      });
      
      // Set master flag indicating that kmttg is running in GUI mode
      config.GUIMODE = true;
      
      // Restore last GUI run settings from file
      readSettings();
      
      // Enable/disable options according to configuration
      refreshOptions(true);
      
      // Create and enable/disable component tooltips
      MyTooltip.enableToolTips(config.toolTips);
      setToolTips();
      
      jFrame.show();
      
      // Create NowPlaying icons
      CreateImages();
      
//      // Init show_details dialog
//      show_details = ShowDetails.load(jFrame);
      
      // Start NPL jobs
      if (config.npl_when_started == 1) {
         Platform.runLater(new Runnable() {
            @Override public void run() {
               initialNPL(config.TIVOS);               
            }
         });
      }
      
      setLookAndFeel(config.lookAndFeel);
      
      // Download tools if necessary
      mainInstall.install();

      // Invoke a 1000ms period timer for job monitor
      kmttg.timer = new Timer();
      kmttg.timer.schedule(
         new TimerTask() {
             @Override
             public void run() {
                Platform.runLater(new Runnable() {
                   @Override public void run() {
                      jobMonitor.monitor(config.gui);
                   }
                });
             }
         }
         ,0,
         1000
      );
      
      // Upon startup, try and load saved queue
      /* Intentionally disabled - only do this for auto transfers mode now
      if (config.persistQueue)
         jobMonitor.loadAllJobs(10);   // delay load to give gui time to setup*/
      kmttg._startingUp = false;
   }
   
   // Adds a universal key listener so that menu shortcuts work as expected
   public void addGlobalKeyListener(Scene scene) {
      debug.print("scene=" + scene);
      
      // thumbsMenuItem is special - it's removed from the menu because it's not always valid, 
      // but that prevents the menuitem accelerator from automatically working.
      scene.getAccelerators().put(
    		  thumbsMenuItem.getAccelerator(),
    		  new Runnable() {
    		    @Override public void run() {
    		    	thumbsMenuItem.fire();
    		    }
    		  }
    		);
      
//      scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
//         public void handle(KeyEvent e) {
//            String tabName = getCurrentTabName();
//            if (tabName.equals(bundle.getString("tab_remote"))) {
//               String subTabName = config.gui.remote_gui.getCurrentTabName();
//               if (subTabName.equals(bundle.getString("tab_remote_remote"))) {
//                  // For Remote-Remote tab don't want to interfere with anything
//                  return;
//               }
//            }
//            
//            // Proceed with handling menu keyboard accelerators
//            if (e.getEventType() == KeyEvent.KEY_PRESSED && e.isControlDown()) {
//               if (e.getCode() == KeyCode.L) {
//                  clearMessagesMenuItem.fire();
//                  e.consume();
//               }
//               if (e.getCode() == KeyCode.E) {
//                  refreshEncodingsMenuItem.fire();
//                  e.consume();
//               }
//               if (e.getCode() == KeyCode.M) {
//                  saveMessagesMenuItem.fire();
//                  e.consume();
//               }
//               if (e.getCode() == KeyCode.O) {
//                  configureMenuItem.fire();
//                  e.consume();
//               }
//               /*if (e.getCode() == KeyCode.R) {
//                  resetServerMenuItem.fire();
//                  return true;
//               }*/
//               if (e.getCode() == KeyCode.S) {
//                  searchMenuItem.fire();
//                  e.consume();
//               }
//               if (e.getCode() == KeyCode.T) {
//                  thumbsMenuItem.fire();
//                  e.consume();
//               }
//            }
//         }
//      });           
   }

   public void setFontSize(Scene scene, int fontSize) {
      debug.print("scene=" + scene + " fontSize=" + fontSize);
      scene.getRoot().setStyle("-fx-font-size: " + fontSize + "pt;");
      //listFontFamilies();
   }

   public void setFontSize(Dialog<?> dialog, int fontSize) {
      debug.print("dialog=" + dialog + " fontSize=" + fontSize);
      dialog.getDialogPane().setStyle("-fx-font-size: " + fontSize + "pt;");
   }

   public void setFontSize(Alert alert, int fontSize) {
      debug.print("alert=" + alert + " fontSize=" + fontSize);
      alert.getDialogPane().setStyle("-fx-font-size: " + fontSize + "pt;");
   }
   
   public void listFontFamilies() {
      debug.print("");
      List<String> familiesList = Font.getFamilies();
      for (String family : familiesList) {
         System.out.println(family);
      }
   }
   
   public void setLookAndFeel(String name) {
      debug.print("name=" + name);
      if (name == null)
         name = "default.css";
      if (!name.endsWith(".css"))
         name += ".css";
      config.cssFile = name;
      File f = new File(config.cssDir + File.separator + config.cssFile);
      if ( ! f.exists() ) {
         log.warn("Unable to load css file: " + f.getAbsolutePath());
         config.cssFile = "default.css";
         f = new File(config.cssDir + File.separator + config.cssFile);         
         log.warn("Trying alternate default file: " + config.cssFile);
      }
      if (f.exists()) {
         // NOTE: This css will apply to any/all Stages
         Application.setUserAgentStylesheet(null);
         StyleManager.getInstance().addUserAgentStylesheet(f.toURI().toString());
      } else {
         log.error("Unable to load css file: " + f.getAbsolutePath());
      }
   }
   
   public List<String> getAvailableLooks() {
      debug.print("");
      // Parse available css files in css dir
      String dir = config.cssDir;
      
      File d = new File(dir);
      if ( ! d.isDirectory() ) {
         log.error("css dir not valid: " + dir);
         return null;
      }
      FilenameFilter filter = new FilenameFilter() {
         public boolean accept(File dir, String name) {
            debug.print("dir=" + dir + " name=" + name);
            File d = new File(dir.getPath() + File.separator + name);
            if (d.isDirectory()) {
               return false;
            }
            // .css files
            if ( name.toLowerCase().endsWith(".css") ) {
               if (name.toLowerCase().equals("kmttg.css"))
                  return false;
               return true;
            }
            return false;
         }
      };
     
      // Define list of filter entries
      List<String> css_list = new ArrayList<String>();
      File[] files = d.listFiles(filter);
      for (int i=0; i<files.length; i++) {
         css_list.add(files[i].getName());
      }
      
      // Sort encode list alphabetically
      Collections.sort(css_list);
      return css_list;
   }
   
   public void grabFocus() {
      debug.print("");
      if (jFrame != null)
         if(! jFrame.isFocused()) { jFrame.requestFocus(); }
   }
   
   private SplitPane getContentPane() {
      debug.print("");
                  
         // CANCEL JOBS button
         cancel.setTooltip(getToolTip("cancel"));

         // START JOBS button
         start.setTooltip(getToolTip("start"));
         
         // Tasks
         
         // Tasks row
         if (config.twpDeleteEnabled()) {
//            tasks_panel.getChildren().add(twpdelete);            
         } else {
        	 twpdelete.setVisible(false);
        	 twpdelete.setManaged(false);
         }
         if (config.rpcDeleteEnabled()) {
//            tasks_panel.getChildren().add(rpcdelete);            
         } else {
        	 rpcdelete.setVisible(false);
        	 rpcdelete.setManaged(false);
         }
         
         // Encoding row
         // Encoding label
 
         // Encoding names combo box
         SetEncodings(encodeConfig.getValidEncodeNames());

         // Encoding description label
         String description = "";
         if (encodeConfig.getValidEncodeNames().size() > 0) {
        	 description = MessageFormat.format(bundle.getString("encoding_description_label"), encodeConfig.getEncodeName(), encodeConfig.getDescription(encodeConfig.getEncodeName()), encodeConfig.getExtension(encodeConfig.getEncodeName()), encodeConfig.getCommandName(encodeConfig.getEncodeName()));
         }
         encoding_description_label.setText(description);
         
         // Job Monitor table
         jobTab = new jobTable();
         jobPane.setContent(jobTab.JobMonitor);
         
         // Progress Bar

         // Message area
         textp = new textpane(text);
                  
         // Tabbed panel
         tabbed_panel.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override public void changed(ObservableValue<? extends Tab> ov, Tab oldVal, Tab newVal) {
               if (getCurrentTabName() != null && getCurrentTabName().equals(bundle.getString("tab_remote"))) {
                  // Set focus on remote pane
                  remote_gui.getPanel().requestFocus();
               }
            }
         });

         // Add permanent tabs
         tivoTabs.put(bundle.getString("tab_files"), new tivoTab(bundle.getString("tab_files")));
         addTabPane(bundle.getString("tab_files"), tabbed_panel, tivoTabs.get(bundle.getString("tab_files")).getPanel());
         
         // Add Tivo tabs
         SetTivos(config.TIVOS);
         
      return jContentPane;
   }
   
   @FXML public void startCB() {
       String tivoName = getCurrentTabName();
       if (tivoName.equals(bundle.getString("tab_remote")))
          log.error("START JOBS invalid with Remote tab selected.");
       else
          tivoTabs.get(tivoName).startCB();
   }

   @FXML public void qsfixCB() {
       refreshOptions(false);
   }
   
   private void initMenuBar() {
      debug.print("");
      initFileMenu();
      initAutoTransfersMenu();
      initHelpMenu();
   }

   private Menu initFileMenu() {
      debug.print("");
         if (config.getTivoUsername() != null)
//            fileMenu.getItems().add(getPushesMenuItem());
        	 ; else fileMenu.getItems().remove(pushesMenuItem);
         if (config.rpcEnabled() && AutoSkip.skipEnabled()) {
//            fileMenu.getItems().add(getAutoSkipMenuItem());
//            fileMenu.getItems().add(getSkipServiceMenuItem());
         }
         else {
        	 fileMenu.getItems().remove(autoSkipMenuItem);
        	 fileMenu.getItems().remove(skipServiceMenuItem);
         }
//         //fileMenu.add(getThumbsMenuItem());
         // Create thumbs menu item but don't add to File menu
//         getThumbsMenuItem();
         fileMenu.getItems().remove(thumbsMenuItem);
      return fileMenu;
   }
   
   private Menu initAutoTransfersMenu() {
      debug.print("");
         if (config.OS.equals("windows"))
//            autoMenu.getItems().add(getServiceMenu());
         {
        	 autoMenu.getItems().remove(serviceMenuBackground);
         }else{
        	 autoMenu.getItems().remove(serviceMenuService);
         }
//            autoMenu.getItems().add(getBackgroundJobMenu());
      return autoMenu;
   }

   private Menu initHelpMenu() {
      debug.print("");
         if (config.OS.equals("windows") || config.OS.equals("mac"))
;//            helpMenu.getItems().add(getHelpToolsUpdateMenuItem());
         else helpMenu.getItems().remove(helpToolsUpdateMenuItem);
      return helpMenu;
   }

   @FXML public void helpAboutMenuItemCB() {
               help.showHelp();
   }

   @FXML public void helpUpdateMenuItemCB() {
               update.update_kmttg_background();
   }

   @FXML public void helpToolsUpdateMenuItemCB() {
               update.update_tools_background();
   }
   @FXML public void exitMenuItemCB() {
               saveSettings();
               System.exit(0);
   }

   @FXML public void autoConfigMenuItemCB() {
               configAuto.display(jFrame);
   }
   @FXML public void saveMessagesMenuItemCB() {
               String file = config.programDir + File.separator + "kmttg.log";
//               String eol = System.lineSeparator();
               String eol = "\n";
               if (config.OS.equals("windows"))
                  eol = "\r\n";
               try {
                  NodeList list = text.getEngine().getDocument().getElementById("content").getChildNodes();
                  StringBuilder sb = new StringBuilder();
                  for (int i=0; i<list.getLength(); ++i) {
                     org.w3c.dom.Node node = list.item(i);
                     if (node.getNodeName().equalsIgnoreCase("pre")) {
                        String[] lines = node.getTextContent().split("\n");
                        for (String line : lines)
                           sb.append(line + eol);
                     }
                  }
                  BufferedWriter ofp = new BufferedWriter(new FileWriter(file));
                  ofp.write(sb.toString());
                  ofp.close();
                  log.warn("Saved output messages to file: " + file);
               } catch (IOException ex) {
                  log.error("Problem writing to file: " + file);
               }
   }

   @FXML public void clearMessagesMenuItemCB() {
               textp.clear();
   }

   /*private MenuItem getResetServerMenuItem() {
      debug.print("");
      if (resetServerMenuItem == null) {
         resetServerMenuItem = new MenuItem();
         resetServerMenuItem.setText("Reset TiVo web server");
         resetServerMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
               String tivoName = getSelectedTivoName();
               if (tivoName != null) {
                  String urlString = "http://" + config.TIVOS.get(tivoName) + "/TiVoConnect?Command=ResetServer";
                  // Add wan port if configured
                  String wan_port = config.getWanSetting(tivoName, "http");
                  if (wan_port != null)
                     urlString = string.addPort(urlString, wan_port);
                  try {
                     URL url = new URL(urlString);
                     log.warn("Resetting " + tivoName + " TiVo: " + urlString);
                     url.openConnection();
                  }
                  catch(Exception ex) {
                     log.error(ex.toString());
                  }
               } else {
                  log.error("This command must be run with a TiVo tab selected.");
               }
            }
         });
      }
      return resetServerMenuItem;
   }*/

   @FXML public void pushesMenuItemCB() {
               log.print(config.pyTivo_mind);
               String tivoName = getSelectedTivoName();
               if (tivoName == null)
                  log.error("This command must be run with a TiVo tab selected.");
               else {
                  config.middlemind_host = "middlemind.tivo.com";
                  if (config.pyTivo_mind.startsWith("staging"))
                     config.middlemind_host = "stagingmiddlemind.tivo.com";
                  log.warn("Querying middlemind host: " + config.middlemind_host);
                  new Pushes(tivoName, getFrame());
               }
   }

   @FXML public void toggleLaunchingJobsMenuItemCB() {
                 if (toggleLaunchingJobsMenuItem.isSelected()) {
//               if (newVal) {
                  jobMonitor.NoNewJobs = true;
                  log.warn("Launching queued jobs disabled. Queued jobs will not be launched.");
               } else {
                  jobMonitor.NoNewJobs = false;
                  log.warn("Launching queued jobs enabled. Resuming normal job processing.");
               }
   }

   @FXML public void saveJobsMenuItemCB() {
               jobMonitor.saveQueuedJobs();
   }

   @FXML public void loadJobsMenuItemCB() {
               jobMonitor.loadQueuedJobs();
   }
   
   @FXML public void runInGuiMenuItemCB() {
               autoRunInGUICB();
   }
   
   @FXML public void loopInGuiMenuItemCB() {
               autoLoopInGUICB(
//            		   newVal);
            		   loopInGuiMenuItem.isSelected());
   }
   
   @FXML public void resumeDownloadsMenuItemCB() {
               config.resumeDownloads = 
            		   //newVal;
            		   resumeDownloadsMenuItem.isSelected();
   }
   
   @FXML public void addSelectedTitlesMenuItemCB() {
               TableMap tmap = TableMap.getCurrent();
               if (tmap == null || (tmap != null && ! tmap.isRemote())) {
                  // Non remote table
                  tivoTabs.get(getSelectedTivoName()).autoSelectedTitlesCB();
                  return;
               }
               // Processing for remote tables
               if (tmap != null) {
                  int[] selected = tmap.getSelected();
                  if (selected != null && selected.length > 0) {
                     for (int row : selected) {
                        JSONObject json = tmap.getJson(row);
                        if (json != null && json.has("title")) {
                           try {
                              auto.autoAddTitleEntryToFile(json.getString("title"));
                           } catch (JSONException e1) {
                              log.error("Add selected titles json exception - " + e1.getMessage());
                           }
                        }
                     }
                  } else {
                     log.error("No show selected in table");
                     return;                        
                  }
               }
   }

   @FXML public void addSelectedHistoryMenuItemCB() {
               String tivoName = getSelectedTivoName();
               if (tivoName != null) {
                  tivoTabs.get(tivoName).autoSelectedHistoryCB();
               } else {
                  log.error("This command must be run from a TiVo tab with selected tivo shows.");
               }
   }

   @FXML public void logFileMenuItemCB() {
               new autoLogView(jFrame);
   }

   @FXML public void configureMenuItemCB() {
               configMain.display(jFrame);
   }

   @FXML public void refreshEncodingsMenuItemCB() {
               refreshEncodingProfilesCB();
   }

   @FXML public void serviceStatusMenuItemCB() {
               String query = auto.serviceStatus();
               if (query != null) {
                  log.warn(query);
               }
   }

   @FXML public void serviceInstallMenuItemCB() {
               String query = auto.serviceStatus();
               if (query != null) {                  
                  if (query.matches("^.+STATUS.+$")) {
                     log.warn("kmttg service already installed");
                     return;
                  }
                  auto.serviceCreate();
               }
   }

   @FXML public void serviceStartMenuItemCB() {
               String query = auto.serviceStatus();
               if (query != null) {                  
                  if (query.matches("^.+RUNNING$")) {
                     log.warn("kmttg service already running");
                     return;
                  }
               }
               auto.serviceStart();
   }

   @FXML public void serviceStopMenuItemCB() {
               String query = auto.serviceStatus();
               if (query != null) {                  
                  if (query.matches("^.+STOPPED$")) {
                     log.warn("kmttg service already stopped");
                     return;
                  }
               }
               auto.serviceStop();
   }

   @FXML public void serviceRemoveMenuItemCB() {
               String query = auto.serviceStatus();
               if (query != null) {
                  if (query.matches("^.+not been created.+$")) {
                     log.warn("kmttg service not installed");
                     return;
                  }
                  auto.serviceDelete();
               }
   }

   @FXML public void backgroundJobStatusMenuItemCB() {
               auto.unixAutoIsRunning(true);
   }

   @FXML public void backgroundJobEnableMenuItemCB() {
               auto.unixAutoStart();
   }

   @FXML public void backgroundJobDisableMenuItemCB() {
               auto.unixAutoKill();
   }

   @FXML public void searchMenuItemCB() {
               TableUtil.SearchGUI();
   }

   @FXML public void autoSkipMenuItemCB() {
               new SkipDialog(config.gui.getFrame());
   }

   @FXML public void skipServiceMenuItemCB() {
	             if ( ! skipServiceMenuItem.isSelected() ) {
//               if ( ! newVal ) {
                  // Turn off service
                  if (config.skipService != null)
                     config.skipService.stop();
                  return;
               }
               if (! skipServiceMenuItem_cb)
                  return;
               
               // Don't do anything if no skip data available
               JSONArray skipData = AutoSkip.getEntries();
               if (skipData == null || skipData.length() == 0) {
                  log.warn("No skip table data available - ignoring skip service request");
                  skipServiceMenuItem.setSelected(false);
                  return;
               }
               
               // Build list of eligible TiVos
               Stack<String> all = config.getTivoNames();
               for (int i=0; i<all.size(); ++i) {
                  if (! config.rpcEnabled(all.get(i)))
                     all.remove(i);
               }
               
               // Prompt user to choose a TiVo
               ChoiceDialog<String> dialog = new ChoiceDialog<String>(all.get(0), all);
               dialog.setTitle(bundle.getString("dialog_title"));
               dialog.setContentText(bundle.getString("dialog_content"));
               String tivoName = null;
               Optional<String> result = dialog.showAndWait();
               if (result.isPresent())
                  tivoName = result.get();
               if (tivoName != null && tivoName.length() > 0) {               
                  // Start service for selected TiVo
                  config.skipService = new SkipService(tivoName);
                  config.skipService.start();
               } else {
                  skipServiceMenuItem.setSelected(false);
               }
   }

   @FXML public void thumbsMenuItemCB() {
               TableUtil.ThumbsGUI();
   }

   // This will decide which options are enabled based on current config settings
   // Options are disabled when associated config entry is not setup
   public void refreshOptions(Boolean refreshProfiles) {
      debug.print("refreshProfiles=" + refreshProfiles);
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
      
      // Refresh encoding profiles in case toggled between VRD & regular
      if (config.GUIMODE && refreshProfiles) refreshEncodingProfilesCB();
      
      //FIXME commented out - remotegui is more complicated now
      //FIXME (GUIMODE only adjusts these tabbed panes?? maybe this was just a speedup)
//      // Add remote tab if appropriate
//      if (config.GUIMODE) {
//        if (remote_gui == null) {
//         remote_gui = new remotegui(jFrame);
//        }
//        addTabPane(bundle.getString("tab_remote"), tabbed_panel_apps, remote_gui.getPanel());
//      } else {
//    	  tabbed_panel_apps.getTabs().removeIf((t) -> t.getText().equals(bundle.getString("tab_remote")));
//      }
      
      // Add slingbox tab if appropriate
      if (config.slingBox == 1) {
    	  //re-add slingbox_gui if they just turned it on.
    	  if(tabbed_panel_apps.getTabs().filtered((t) -> t.getText().equals(bundle.getString("tab_slingbox"))).size() == 0) {
    		  Tab tab = new Tab();
    		  tab.setContent(slingbox_gui.getPanel());
    		  tabbed_panel_apps.getTabs().add(tab);
    	  }
//         if (slingbox_gui == null)
//            slingbox_gui = slingboxgui.load(jFrame);
//         if(slingbox_gui != null)
//        	 addTabPane(bundle.getString("tab_slingbox"), tabbed_panel_apps, slingbox_gui.getPanel());
      } else {
    	  tabbed_panel_apps.getTabs().removeIf((t) -> t.getText().equals(bundle.getString("tab_slingbox")));
      }
   }
   
   // Callback for "Refresh Encoding Profiles" File menu entry
   // This will re-parse encoding files and reset Encoding Profile list in GUI
   private void refreshEncodingProfilesCB() {
      debug.print("");
      log.warn("Refreshing encoding profiles");
      encodeConfig.parseEncodingProfiles();
   }
   
   // Callback for "Run Once in GUI" Auto Transfers menu entry
   // This is equivalent to a batch mode run but is performed in GUI
   public void autoRunInGUICB() {
      debug.print("");
      config.GUI_AUTO = 0;
      if ( ! autoConfig.parseAuto(config.autoIni) ) {
         log.error("Auto Transfers config has errors or is not setup");
         return;
      }
      if ( auto.getTitleEntries().isEmpty() && auto.getKeywordsEntries().isEmpty() ) {
         log.error("No keywords defined in " + config.autoIni + "... aborting");
         return;
      }
      Stack<String> tivoNames = auto.getTiVos();
      if (tivoNames.size() > 0) {
         for (int i=0; i<tivoNames.size(); i++) {
            // Queue up a nowplaying list job for this tivo
            config.GUI_AUTO++;
            tivoTab t = getTab(tivoNames.get(i));
            if (t != null) {
               jobMonitor.getNPL(tivoNames.get(i));
            }
         }
      }
   }

   // Callback for "Loop in GUI" Auto Transfers menu entry
   // This is equivalent to auto mode run but is performed in GUI
   public void autoLoopInGUICB(Boolean enabled) {
      debug.print("enabled=" + enabled);
      // This triggers jobMonitor to clear launch hash
      config.GUI_AUTO = -1;
      
      // If button enabled then start Loop in GUI mode, else exit that mode
      if (enabled) {
         // If kmttg service or background job running prompt user to stop it
         Boolean auto_running = false;
         String question = "";
         if (config.OS.equals("windows")) {
            // Query to stop windows service if it's running
            String query = auto.serviceStatus();
            if (query != null && query.matches("^.+RUNNING$")) {
               auto_running = true;
               question = bundle.getString("question_stop_service");
            }
         } else {
            auto_running = auto.unixAutoIsRunning(false);            
            question = bundle.getString("question_stop_background");
         }
         if (auto_running) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle(bundle.getString("question_title"));
            setFontSize(alert, config.FontSize);
            alert.setContentText(question);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
               if (config.OS.equals("windows")) {
                  auto.serviceStop();
               } else {
                  auto.unixAutoKill();
               }               
            }
         }

         // Start Loop in GUI mode
         config.GUI_LOOP = 1;
         log.warn("\nAuto Transfers Loop in GUI enabled");
      } else {
         // Stop Loop in GUI mode
         log.warn("\nAuto Transfers Loop in GUI disabled");
         config.GUI_LOOP = 0;
         log.stopLogger();
      }
   }
   
   // Encoding cyclic change callback
   // Set the description according to selected item
   @FXML private void encodingCB() {
      ChoiceBox<String> combo = encoding;
      debug.print("combo=" + combo);
      String encodeName = combo.getValue();
      if (encodeName == null) {
    	  return;
      }
      config.encodeName = encodeName;
 	  String description = MessageFormat.format(bundle.getString("encoding_description_label"), encodeConfig.getEncodeName(), encodeConfig.getDescription(encodeConfig.getEncodeName()), encodeConfig.getExtension(encodeConfig.getEncodeName()), encodeConfig.getCommandName(encodeConfig.getEncodeName()));
      // Set encoding_description_label accordingly
      encoding_description_label.setText(description);
   }
 
   // Cancel button callback
   // Kill and remove selected jobs from job monitor
   @FXML private void cancelCB() {
      debug.print("");
      int[] rows = TableUtil.GetSelectedRows(jobTab.JobMonitor);

      if (rows.length > 0) {
         int row;
         for (int i=rows.length-1; i>=0; i--) {
            row = rows[i];
            jobData job = jobTab.GetSelectionData(row);
            if (job != null) jobMonitor.kill(job);
         }
      }
   }
   
   // Add a new tab pane
   private void addTabPane(String name, TabPane pane, Node content) {
      debug.print("name=" + name + " pane=" + pane + " content=" + content);
      for (Tab t : pane.getTabs()) {
         if (t.getText().equals(name))
         {
        	 // Prevent duplicates
        	 if(t.getContent() == null)
        		 t.setContent(content);
        	 return;
         }
      }
      // Tab didn't exist.  Create it.
         Tab tab = new Tab();
         tab.setContent(content);
         tab.setText(name);
         pane.getTabs().add(tab);
   }

   // Create tivo tabs as needed
   public void SetTivos(LinkedHashMap<String,String> values) {
      debug.print("values=" + values);
      if ( values.size() > 1 ) {
         String[] names = new String[values.size()-1];
         int i = 0;         
         for (String value : values.keySet()) {
            if (! value.equals(bundle.getString("tab_files")) && ! value.equals(bundle.getString("tab_remote"))) {
               if (config.nplCapable(value)) {
                  names[i] = value;
                  i++;
               }
            }
         }
         
         // Remove unwanted tabs
         tivoTabRemoveExtra(names);
         
         // Add tabs
         for (int j=i-1; j>=0; j--) {
            tivoTabAdd(names[j]);
         }
         
         // remote gui
         if (remote_gui != null)
            remote_gui.setTivoNames();

      } else {
         // Remove all tivo tabs
         String itemName = tabbed_panel.getTabs().get(0).getText();
         while(! itemName.equals(bundle.getString("tab_files")) && ! itemName.equals(bundle.getString("tab_remote"))) {
            tivoTabRemove(itemName);
         }
      }
   }

   // Start NPL jobs for 1st time
   public void initialNPL(LinkedHashMap<String,String> values) {
      debug.print("values=" + values);
      for (String value : values.keySet()) {
         if (! value.equals(bundle.getString("tab_files")) && ! value.equals(bundle.getString("tab_remote")) && config.nplCapable(value)) {
            jobMonitor.getNPL(value);
         }
      }
   }
   
   public String getCurrentTabName() {
      debug.print("");
      //FIXME this doesn't account for new tab layout
      return tabbed_panel.getSelectionModel().getSelectedItem().getText();
   }
   
   public String getSelectedTivoName() {
      debug.print("");
      String tabName = getCurrentTabName();
      if (! tabName.equals(bundle.getString("tab_files")) && ! tabName.equals(bundle.getString("tab_remote")) && ! tabName.equals(bundle.getString("tab_slingbox"))) {
         return tabName;
      }
      return null;
   }
   
   public String getCurrentRemoteTivoName() {
      debug.print("");
      if (getCurrentTabName().equals(bundle.getString("tab_remote")))
         return config.gui.remote_gui.getTivoName(config.gui.remote_gui.getCurrentTabName());
      return null;
   }
   
   public JSONObject getCurrentRemoteJson() {
      debug.print("");
      if (getCurrentTabName().equals(bundle.getString("tab_remote")))
         return config.gui.remote_gui.getSelectedJSON(config.gui.remote_gui.getCurrentTabName());         
      return null;
   }
   
   // Check name against existing tabbed panel names
   private Boolean tivoTabExists(String name) {
      debug.print("name=" + name);
      int numTabs = tabbed_panel.getTabs().size();
      String tabName;
      for (int i=0; i<numTabs; i++) {
         tabName = tabbed_panel.getTabs().get(i).getText();
         if (tabName != null && tabName.equals(name)) {
            return true;
         }
      }
      return false;
   }
   
   private void tivoTabAdd(String name) {
      debug.print("name=" + name);
      if ( ! tivoTabExists(name) ) {
         tivoTab tab = new tivoTab(name);
         Tab tabpane = new Tab();
         tabpane.setContent(tab.getPanel());
         tabpane.setText(name);
         tabbed_panel.getTabs().add(0, tabpane);
         tivoTabs.put(name,tab);
      }
   }
   
   private void tivoTabRemove(String name) {
      debug.print("name=" + name);
      if (tivoTabs.containsKey(name)) {
         tabbed_panel.getTabs().remove(tivoTabs.get(name));
         tivoTabs.remove(name);
      }
   }
   
   private void tivoTabRemoveExtra(String[] names) {
      debug.print("names=" + Arrays.toString(names));
      int numTabs = tabbed_panel.getTabs().size();
      if (numTabs > 0 && names.length > 0) {
         // Determine tabs we no longer want
         Stack<String> unwanted = new Stack<String>();
         String tabName;
         Boolean remove;
         for (int i=0; i<numTabs; i++) {
            tabName = tabbed_panel.getTabs().get(i).getText();
            if (tabName != null && ! tabName.equals(bundle.getString("tab_files")) && ! tabName.equals(bundle.getString("tab_remote"))) {
               remove = true;
               for (int j=0; j<names.length; j++) {
                  if (names[j] != null && names[j].equals(tabName)) {
                     remove = false;
                  }
               }
               if (remove) {
                  unwanted.add(tabName);
               }
            }
         }
         // Now remove the unwanted tabs
         if (unwanted.size() > 0) {
            for (int i=0; i<unwanted.size(); i++) {
               tivoTabRemove(unwanted.get(i));
            }
         }
      }
   }
   
   // Set current tab to this tivo (if valid)
   public void SetTivo(String tivoName) {
      debug.print("tivoName=" + tivoName);
      for (int i=0; i<tabbed_panel.getTabs().size(); ++i) {
         if (tabbed_panel.getTabs().get(i).getText().equals(tivoName)) {
            tabbed_panel.getSelectionModel().select(i);
         }
      }
   }
   
   // Add a tivo
   public void AddTivo(String name, String ip) {
      debug.print("name=" + name + " ip=" + ip);
      tivoTabAdd(name);
      configMain.addTivo(name, ip);
   }
   
   // Set encoding ChoiceBox choices
   public void SetEncodings(final Stack<String> values) {
      debug.print("values=" + values);

      if (encoding != null) {
         Platform.runLater(new Runnable() {
            @Override public void run() {
               // Get existing setting in ChoiceBox
               String current = null;
               if (encoding.getItems().size() > 0) {
                  current = encoding.getValue();
               }
               Boolean valid = false;
               String[] names = new String[values.size()];
               for (int i=0; i<values.size(); ++i) {
                  names[i] = values.get(i);
                  if (current != null && current.equals(names[i]))
                     valid = true;
               }
               encoding.getItems().clear();
               for (int i=0; i<names.length; i++) {
            	   encoding.getItems().add(names[i]);
               }
               if (! valid)
                  current = null;
               if (current != null)
                  encoding.setValue(current);
               else {
                  if (encoding.getItems().size() > 0)
                     encoding.setValue(encoding.getItems().get(0));
               }
            }
         });
      }
   }
   
   public void SetSelectedEncoding(final String name) {
      debug.print("name=" + name);
      Platform.runLater(new Runnable() {
          @Override public void run() {
		      if (encoding.getItems().size() > 0) {
		         encoding.setValue(name);
		      }
          }
      });
   }
   
   private void CreateImages() {
      debug.print("");
      Images = new Hashtable<String,Image>();
      String[] names = {
         "expires-soon-recording", "save-until-i-delete-recording",
         "in-progress-recording", "in-progress-transfer",
         "expired-recording", "suggestion-recording", "folder",
         "copy-protected", "running", "queued", "skipmode"
      };
      URL url;
      for (int i=0; i<names.length; i++) {
         try {
            // From jar file
            url = getClass().getResource("/" + names[i] + ".png");
            Images.put(names[i], new Image(url.toURI().toString()));
         } catch (Exception e) {
            // From eclipse
            Images.put(names[i], new Image(new File("images/" + names[i] + ".png").toURI().toString()));            
         }
      }
   }   
   
   // Save current GUI settings to a file
   public void saveSettings() {
      debug.print("");
      if (config.gui_settings != null) {
         if (slingbox_gui != null)
            slingbox_gui.updateConfig();
         try {
            double centerDivider0 = recordingsSplit.getDividerPositions()[0];
            double centerDivider = jContentPane.getDividerPositions()[0];
            double bottomDivider = splitBottom.getDividerPositions()[0];
            double helpDisplay = -1;
            if (helpSplit.getItems().contains(helpTabPane)) {
            	helpDisplay = helpSplit.getDividerPositions()[0];
            }
            progressPane.isExpanded();
            //FIXME doesn't account for new tab layout
            String tabName = tabbed_panel.getSelectionModel().getSelectedItem().getText();
            int width = (int)jFrame.getWidth(); if (width <0) width = 0;
            int height = (int)jFrame.getHeight(); if (height <0) height = 0;
            int x = (int)jFrame.getX(); if (x <0) x = 0;
            int y = (int)jFrame.getY(); if (y <0) y = 0;
            BufferedWriter ofp = new BufferedWriter(new FileWriter(config.gui_settings));            
            ofp.write("# kmttg gui preferences file\n");
            ofp.write("<GUI_LOOP>\n"            + config.GUI_LOOP            + "\n");
            ofp.write("<metadata>\n"            + metadata_setting()         + "\n");
            ofp.write("<decrypt>\n"             + decrypt_setting()          + "\n");
            ofp.write("<qsfix>\n"               + qsfix_setting()            + "\n");
            ofp.write("<twpdelete>\n"           + twpdelete_setting()        + "\n");
            ofp.write("<rpcdelete>\n"          + rpcdelete_setting()       + "\n");
            ofp.write("<comskip>\n"             + comskip_setting()          + "\n");
            ofp.write("<comcut>\n"              + comcut_setting()           + "\n");
            ofp.write("<captions>\n"            + captions_setting()         + "\n");
            ofp.write("<encode>\n"              + encode_setting()           + "\n");
            ofp.write("<push>\n"                + push_setting()             + "\n");
            ofp.write("<custom>\n"              + custom_setting()           + "\n");
            ofp.write("<encode_name>\n"         + config.encodeName          + "\n");
            ofp.write("<toolTips>\n"            + config.toolTips            + "\n");
            ofp.write("<toolTipsDelay>\n"       + config.toolTipsDelay       + "\n");
            ofp.write("<toolTipsTimeout>\n"     + config.toolTipsTimeout     + "\n");
            ofp.write("<slingBox>\n"            + config.slingBox            + "\n");
            ofp.write("<slingBox_perl>\n"       + config.slingBox_perl       + "\n");
            ofp.write("<slingBox_dir>\n"        + config.slingBox_dir        + "\n");
            ofp.write("<slingBox_ip>\n"         + config.slingBox_ip         + "\n");
            ofp.write("<slingBox_port>\n"       + config.slingBox_port       + "\n");
            ofp.write("<slingBox_pass>\n"       + config.slingBox_pass       + "\n");
            ofp.write("<slingBox_res>\n"        + config.slingBox_res        + "\n");
            ofp.write("<slingBox_vbw>\n"        + config.slingBox_vbw        + "\n");
            ofp.write("<slingBox_type>\n"       + config.slingBox_type       + "\n");
            ofp.write("<slingBox_container>\n"  + config.slingBox_container  + "\n");
            ofp.write("<jobMonitorFullPaths>\n" + config.jobMonitorFullPaths + "\n");
            ofp.write("<width>\n"               + width                      + "\n");
            ofp.write("<height>\n"              + height                     + "\n");
            ofp.write("<x>\n"                   + x                          + "\n");
            ofp.write("<y>\n"                   + y                          + "\n");
            ofp.write("<centerDivider0>\n"      + centerDivider0             + "\n");
            ofp.write("<centerDivider>\n"       + centerDivider              + "\n");
            ofp.write("<bottomDivider>\n"       + bottomDivider              + "\n");
            ofp.write("<progressExpanded>\n"    + (progressPane.isExpanded()?1:0) + "\n");
            ofp.write("<helpDisplay>\n"         + helpDisplay                + "\n");
            if (remote_gui != null) {
            	//FIXME doesn't account for new tab layout... or does it?
               int tabIndex_r = remote_gui.getPanel().getSelectionModel().getSelectedIndex();
               ofp.write("<tab_remote>\n"       + tabIndex_r                 + "\n");
            }
            ofp.write("<tab>\n"                 + tabName                    + "\n");
            
            ofp.write("<columnOrder>\n");
            String name, colName;
            // NPL & Files tables
            for (Enumeration<String> e=tivoTabs.keys(); e.hasMoreElements();) {
               name = e.nextElement();
               String order[] = tivoTabs.get(name).getColumnOrder();
               colName = order[0];
               if (colName.equals("")) colName = "ICON";
               ofp.write(name + "=" + colName);
               for (int j=1; j<order.length; ++j) {
                  colName = order[j];
                  if (colName.equals("")) colName = "ICON";
                  ofp.write("," + colName);
               }
               ofp.write("\n");
            }
            // Job table
            String order[] = jobTab.getColumnOrder();
            ofp.write("JOBS=" + order[0]);
            for (int j=1; j<order.length; ++j) {
               ofp.write("," + order[j]);
            }
            ofp.write("\n\n");
            
            ofp.write("<columnWidths>\n");
            for (Enumeration<String> e=tivoTabs.keys(); e.hasMoreElements();) {
               name = e.nextElement();
               ObservableList<TreeTableColumn<Tabentry, ?>> cols = tivoTabs.get(name).getTable().NowPlaying.getColumns();
               int[] widths = new int[cols.size()];
               int i=0;
               for (TreeTableColumn<Tabentry, ?> col : cols) {
                  widths[i++] = (int)col.getWidth();
               }
               ofp.write(name + "=" + widths[0]);
               for (int j=1; j<widths.length; ++j) {
                  ofp.write("," + widths[j]);
               }
               ofp.write("\n");
            }
            ofp.write("\n");
            
            ofp.write("<showFolders>\n");
            for (Enumeration<String> e=tivoTabs.keys(); e.hasMoreElements();) {
               name = e.nextElement();
               if ( ! name.equals(bundle.getString("tab_files")) && ! name.equals(bundle.getString("tab_remote")) ) {
                  if (tivoTabs.get(name).showFolders()) {
                     ofp.write(name + "=" + 1 + "\n");
                  } else {
                     ofp.write(name + "=" + 0 + "\n");
                  }
               }
            }
            if (remote_gui != null) {
               String[]names = {
                  "todo", "sp", "cancel", "premiere", "search", "guide", "stream", "deleted", "thumbs", "rc", "info" 
               };
               ofp.write("\n<rpc_tivo>\n");
               for (String tab : names)
                  ofp.write(tab + "=" + remote_gui.getTivoName(tab) + "\n");
               ofp.write("\n<rpc_includePast>\n");
               if (remote_gui.cancel_tab.includeHistory.isSelected())
                  ofp.write("1\n");
               else
                  ofp.write("0\n");
               
               // Search max hits
               int max = (Integer) remote_gui.search_tab.max.getValue();
               ofp.write("\n<rpc_search_max>\n");
               ofp.write("" + max + "\n");
               
               // Search streaming settings
               ofp.write("\n<rpc_search_type>\n");
               ofp.write("" + remote_gui.search_tab.search_type.getValue());
               
               int includeFree = 0;
               if (remote_gui.search_tab.includeFree.isSelected())
                  includeFree = 1;
               ofp.write("\n<rpc_search_includeFree>\n");
               ofp.write("" + includeFree + "\n");
               
               int includePaid = 0;
               if (remote_gui.search_tab.includePaid.isSelected())
                  includePaid = 1;
               ofp.write("\n<rpc_search_includePaid>\n");
               ofp.write("" + includePaid + "\n");
               
               //int includeVod = 0;
               //if (remote_gui.search_tab.includeVod.isSelected())
               //   includeVod = 1;
               //ofp.write("\n<rpc_search_includeVod>\n");
               //ofp.write("" + includeVod + "\n");
               
               //int unavailable = 0;
               //if (remote_gui.search_tab.unavailable.isSelected())
               //   unavailable = 1;
               //ofp.write("\n<rpc_search_unavailable>\n");
               //ofp.write("" + unavailable + "\n");
               
               // Record dialog
               JSONObject json = util.recordOpt.getValues();
               if (json != null) {
                  try {
                     ofp.write("\n<rpc_recordOpt>\n");
                     String[] n = {"keepBehavior", "startTimePadding", "endTimePadding", "anywhere"};
                     for (int j=0; j<n.length; ++j) {
                        ofp.write(n[j] + "=" + json.get(n[j]) + "\n");
                     }
                  } catch (JSONException e) {
                     log.error(e.getMessage());
                     log.error(Arrays.toString(e.getStackTrace()));
                  }
               }
               
               // SP dialog
               json = util.spOpt.getValues();
               if (json != null) {
                  try {
                     ofp.write("\n<rpc_spOpt>\n");
                     String[] n = {"showStatus", "maxRecordings", "keepBehavior", "startTimePadding", "endTimePadding"};
                     for (int j=0; j<n.length; ++j) {
                        ofp.write(n[j] + "=" + json.get(n[j]) + "\n");
                     }
                  } catch (JSONException e) {
                     log.error(e.getMessage());
                     log.error(Arrays.toString(e.getStackTrace()));
                  }
               }
            }
            
            ofp.write("\n");
            ofp.close();
         }         
         catch (IOException ex) {
            log.error("Problem writing to file: " + config.gui_settings);
         }         
      }
   }
   
   // Read initial settings from file
   public void readSettings() {
      debug.print("");
      if (! file.isFile(config.gui_settings)) {
         return;
      }
      try {
         int width = -1;
         int height = -1;
         int x = -1;
         int y = -1;
         int value;
         double centerDivider0 = -1, centerDivider = -1, bottomDivider = -1;
         double helpDisplay = 0.75;
         BufferedReader ifp = new BufferedReader(new FileReader(config.gui_settings));
         String line = null;
         String key = null;
         JSONObject rpc_recordOpt = new JSONObject();
         JSONObject rpc_spOpt = new JSONObject();
         while (( line = ifp.readLine()) != null) {
            // Get rid of leading and trailing white space
            line = line.replaceFirst("^\\s*(.*$)", "$1");
            line = line.replaceFirst("^(.*)\\s*$", "$1");
            if (line.length() == 0) continue; // skip empty lines
            if (line.matches("^#.+")) continue; // skip comment lines
            if (line.matches("^<.+>")) {
               key = line.replaceFirst("<", "");
               key = key.replaceFirst(">", "");
               continue;
            }
            if (key.equals("GUI_LOOP")) {
               if (line.matches("1"))
                  loopInGuiMenuItem.setSelected(true);
            }
            if (key.equals("metadata")) {
               if (line.matches("1"))
                  metadata.setSelected(true);
               else
                  metadata.setSelected(false);
            }
            if (key.equals("decrypt")) {
               if (line.matches("1"))
                  decrypt.setSelected(true);
               else
                  decrypt.setSelected(false);
            }
            if (key.equals("qsfix")) {
               if (line.matches("1"))
                  qsfix.setSelected(true);
               else
                  qsfix.setSelected(false);
            }            
            if (key.equals("twpdelete")) {
               if (line.matches("1"))
                  twpdelete.setSelected(true);
               else
                  twpdelete.setSelected(false);
            }            
            if (key.equals("rpcdelete")) {
               if (line.matches("1"))
                  rpcdelete.setSelected(true);
               else
                  rpcdelete.setSelected(false);
            }            
            if (key.equals("comskip")) {
               if (line.matches("1"))
                  comskip.setSelected(true);
               else
                  comskip.setSelected(false);
            }
            if (key.equals("comcut")) {
               if (line.matches("1"))
                  comcut.setSelected(true);
               else
                  comcut.setSelected(false);
            }
            if (key.equals("captions")) {
               if (line.matches("1"))
                  captions.setSelected(true);
               else
                  captions.setSelected(false);
            }
            if (key.equals("encode")) {
               if (line.matches("1"))
                  encode.setSelected(true);
               else
                  encode.setSelected(false);
            }
            if (key.equals("push")) {
               if (line.matches("1"))
                  push.setSelected(true);
               else
                  push.setSelected(false);
            }
            if (key.equals("custom")) {
               if (line.matches("1"))
                  custom.setSelected(true);
               else
                  custom.setSelected(false);
            }
            if (key.equals("toolTips")) {
               if (line.matches("1"))
                  config.toolTips = 1;
               else
                  config.toolTips = 0;
            }
            if (key.equals("slingBox")) {
               if (line.matches("1"))
                  config.slingBox = 1;
               else
                  config.slingBox = 0;
            }
            if (key.equals("slingBox_pass"))
               config.slingBox_pass = line;
            if (key.equals("slingBox_ip"))
               config.slingBox_ip = line;
            if (key.equals("slingBox_port"))
               config.slingBox_port = line;
            if (key.equals("slingBox_perl"))
               config.slingBox_perl = line;
            if (key.equals("slingBox_dir"))
               config.slingBox_dir = line;
            if (key.equals("slingBox_res"))
               config.slingBox_res = line;
            if (key.equals("slingBox_vbw"))
               config.slingBox_vbw = line;
            if (key.equals("slingBox_type"))
               config.slingBox_type = line;
            if (key.equals("slingBox_container"))
               config.slingBox_container = line;
            if (key.equals("jobMonitorFullPaths")) {
               if (line.matches("1"))
                  config.jobMonitorFullPaths = 1;
               else
                  config.jobMonitorFullPaths = 0;
            }
            if (key.equals("encode_name")) {
               config.encodeName_orig = line;
               if (encodeConfig.isValidEncodeName(line)) {
                  config.encodeName = line;
                  // runlater needed else doesn't get set right at kmttg startup
                  final String line_final = line;
                  Platform.runLater(new Runnable() {
                     @Override public void run() {
                        config.encodeName = line_final;
                        encoding.setValue(line_final);
                     }
                  });
               }
            }
            if (key.equals("toolTipsDelay")) {
               try {
                  config.toolTipsDelay = Integer.parseInt(line);
               } catch (NumberFormatException e) {
                  config.toolTipsDelay = 2;
               }
            }
            if (key.equals("toolTipsTimeout")) {
               try {
                  config.toolTipsTimeout = Integer.parseInt(line);
               } catch (NumberFormatException e) {
                  config.toolTipsTimeout = 20;
               }
            }
            if (key.equals("width")) {
               try {
                  width = Integer.parseInt(line);
               } catch (NumberFormatException e) {
                  width = -1;
               }
            }
            if (key.equals("height")) {
               try {
                  height = Integer.parseInt(line);
               } catch (NumberFormatException e) {
                  height = -1;
               }
            }
            if (key.equals("x")) {
               try {
                  x = Integer.parseInt(line);
               } catch (NumberFormatException e) {
                  x = -1;
               }
            }
            if (key.equals("y")) {
               try {
                  y = Integer.parseInt(line);
               } catch (NumberFormatException e) {
                  y = -1;
               }
            }
            if (key.equals("tab_remote")) {
            	//FIXME doesn't account for new layout
               try {
                  value = Integer.parseInt(line);
               } catch (NumberFormatException e) {
                  value = 0;
               }
               if (remote_gui != null)
                  remote_gui.getPanel().getSelectionModel().select(value);
            }
            if (key.equals("centerDivider0")) {
                try {
                   centerDivider0 = Double.parseDouble(line);
                } catch (NumberFormatException e) {
                   centerDivider0 = -1;
                }
             }
            if (key.equals("centerDivider")) {
               try {
                  centerDivider = Double.parseDouble(line);
               } catch (NumberFormatException e) {
                  centerDivider = -1;
               }
            }
            if (key.equals("bottomDivider")) {
               try {
                  bottomDivider = Double.parseDouble(line);
               } catch (NumberFormatException e) {
                  bottomDivider = -1;
               }
            }
            if (key.equals("helpDisplay")) {
                try {
                   helpDisplay = Double.parseDouble(line);
                } catch (NumberFormatException e) {
                   helpDisplay = -1;
                }
            }
            if (key.equals("progressExpanded")) {
                if (line.matches("1"))
                   progressPane.setExpanded(true);
                else
                   progressPane.setExpanded(false);
            }
            if (key.equals("tab")) {
               SetTivo(line);
            }
            if (key.equals("columnOrder")) {
               String[] l = line.split("=");
               String[] order = l[1].split(",");
               if (tivoTabs.containsKey(l[0])) {
                  tivoTabs.get(l[0]).setColumnOrder(order);
               }
               if (l[0].equals("JOBS")) {
                  jobTab.setColumnOrder(order);
               }
            }
            if (key.equals("columnWidths")) {
               String[] l = line.split("=");
               String name = l[0];
               String[] order = l[1].split(",");
               int[] widths = new int[order.length];
               for (int i=0; i<order.length; ++i) {
                  widths[i] = Integer.parseInt(order[i]);
               }
               if (tivoTabs.containsKey(l[0])) {
                  ObservableList<TreeTableColumn<Tabentry, ?>> cols = tivoTabs.get(name).getTable().NowPlaying.getColumns();
                  int j=0;
                  for (TreeTableColumn<Tabentry, ?> col : cols) {
                     col.setPrefWidth(widths[j++]);
                  }
               }
            }
            if (key.equals("showFolders")) {
               String[] l = line.split("=");
               if (l[1].equals("1")) {
                  if (tivoTabs.containsKey(l[0]))
                     tivoTabs.get(l[0]).showFoldersSet(true);
               }
            }
            if (key.equals("rpc_tivo") && remote_gui != null) {
               String[] l = line.split("=");
               if (l.length == 2 && tivoTabs.containsKey(l[1]))
                  remote_gui.setTivoName(l[0], l[1]);
            }
            /*if (key.equals("rpc_web_bookmarks") && remote_gui != null) {
               if (line.matches("^html::.+$") || line.matches("^flash::.+$"))
                  remote_gui.bookmark_web.addItem(line);
               else
                  remote_gui.bookmark_web.addItem("html::" + line);
            }*/
            
            if (key.equals("rpc_search_max") && remote_gui != null) {
               try {
                  int max = Integer.parseInt(line);
                  remote_gui.search_tab.max.getValueFactory().setValue(max);
               }
               catch (NumberFormatException ex) {
                  // Don't do anything here
               }
            }
            
            if (key.equals("rpc_search_type") && remote_gui != null) {
               String search_type = string.removeLeadingTrailingSpaces(line);
               remote_gui.search_tab.search_type.getSelectionModel().select(search_type);
            }
            
            if (key.equals("rpc_search_includeFree") && remote_gui != null) {
               try {
                  int includeFree = Integer.parseInt(line);
                  remote_gui.search_tab.includeFree.setSelected(includeFree == 1);
               }
               catch (NumberFormatException ex) {
                  // Don't do anything here
               }
            }
            
            if (key.equals("rpc_search_includePaid") && remote_gui != null) {
               try {
                  int includePaid = Integer.parseInt(line);
                  remote_gui.search_tab.includePaid.setSelected(includePaid == 1);
               }
               catch (NumberFormatException ex) {
                  // Don't do anything here
               }
            }
            
            //if (key.equals("rpc_search_includeVod") && remote_gui != null) {
            //   try {
            //      int includeVod = Integer.parseInt(line);
            //      remote_gui.search_tab.includeVod.setSelected(includeVod == 1);
            //   }
            //   catch (NumberFormatException ex) {
            //      // Don't do anything here
            //   }
            //}
            
            //if (key.equals("rpc_search_unavailable") && remote_gui != null) {
            //   try {
            //      int unavailable = Integer.parseInt(line);
            //      remote_gui.search_tab.unavailable.setSelected(unavailable == 1);
            //   }
            //   catch (NumberFormatException ex) {
            //      // Don't do anything here
            //   }
            //}
            
            if (key.equals("rpc_includePast") && remote_gui != null) {
               if (line.matches("1"))
                  remote_gui.cancel_tab.includeHistory.setSelected(true);
            }
            
            if (key.equals("rpc_recordOpt") && remote_gui != null) {
               String[] l = line.split("=");
               if (l.length == 2) {
                  rpc_recordOpt.put(l[0], l[1]);
               }
            }
            if (key.equals("rpc_spOpt") && remote_gui != null) {
               String[] l = line.split("=");
               if (l.length == 2) {
                  rpc_spOpt.put(l[0], l[1]);
               }
            }
         }
         ifp.close();
         
         if (remote_gui != null) {
            if (rpc_recordOpt.length() > 0) {
               util.recordOpt.setValues(rpc_recordOpt);
            }
            if (rpc_spOpt.length() > 0) {
               util.spOpt.setValues(rpc_spOpt);
            }
         }
         
         if (width > 0 && height > 0) {
            jFrame.setWidth(width);
            jFrame.setHeight(height);
         }
         
         if (x >= 0 && y >= 0) {
            Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
            if (x < bounds.getMinX()) x = (int)bounds.getMinX();
            if (x > bounds.getMaxX()) x = (int)bounds.getMinX();
            if (y < bounds.getMinY()) y = (int)bounds.getMinY();
            if (y > bounds.getMaxY()) y = (int)bounds.getMinY();
            jFrame.setX(x);
            jFrame.setY(y);
         }
         
         class backgroundRun implements Runnable {
            double centerDivider0, centerDivider, bottomDivider, helpDisplay;
            public backgroundRun(double centerDivider0,double centerDivider, double bottomDivider, double helpDisplay) {
               this.centerDivider0 = centerDivider0;
               this.centerDivider = centerDivider;
               this.bottomDivider = bottomDivider;
               this.helpDisplay = helpDisplay;
            }
            @Override public void run() {
                if (centerDivider0 > 0 && centerDivider0 < 1)
                    recordingsSplit.setDividerPosition(0, centerDivider0);
                if (centerDivider > 0 && centerDivider < 1)
                    jContentPane.setDividerPosition(0, centerDivider);
               
               if (bottomDivider > 0 && bottomDivider < 1)
                  splitBottom.setDividerPosition(0, bottomDivider);
               
               if (helpDisplay > 0 && helpDisplay < 1) {
            	   helpSplit.setDividerPosition(0, helpDisplay);
               } else if(helpDisplay < 0) {
        		   helpSplit.getItems().remove(helpTabPane);
               }
            }
         }
         Platform.runLater(new backgroundRun(centerDivider0, centerDivider, bottomDivider, helpDisplay));
      }         
      catch (Exception ex) {
         log.warn("Problem parsing config file: " + config.gui_settings);
      }
   }
   
   // Component tooltip setup
   public void setToolTips() {
      debug.print("");
      metadata.setTooltip(getToolTip("metadata"));
      decrypt.setTooltip(getToolTip("decrypt"));
      qsfix.setTooltip(getToolTip("qsfix"));
      twpdelete.setTooltip(getToolTip("twpdelete"));
      rpcdelete.setTooltip(getToolTip("rpcdelete"));
      comskip.setTooltip(getToolTip("comskip"));
      comcut.setTooltip(getToolTip("comcut"));
      captions.setTooltip(getToolTip("captions"));
      encode.setTooltip(getToolTip("encode"));
      push.setTooltip(getToolTip("push"));
      custom.setTooltip(getToolTip("custom"));
      encoding.setTooltip(getToolTip("encoding"));
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
   
   // Abstraction methods
   public void setTitle(final String s) {
      debug.print("s=" + s);
      Platform.runLater(new Runnable() {
         @Override public void run() {
            jFrame.setTitle(s);
            // also show status in the jobs pane title
            if(s == null || s.equals(config.kmttg)) {
            	progressPane.setText(bundle.getString("progressPane"));
            } else {
            	progressPane.setText(s);
            }
         }
      });
   }
   public void text_print(String s) {
      debug.print("s=" + s);
      textp.print(s);
   }
   public void text_warn(String s) {
      debug.print("s=" + s);
      textp.warn(s);
   }
   public void text_error(String s) {
      debug.print("s=" + s);
      textp.error(s);
   }
   public void text_print(Stack<String> s) {
      debug.print("s=" + s);
      textp.print(s);
   }
   public void text_warn(Stack<String> s) {
      debug.print("s=" + s);
      textp.warn(s);
   }
   public void text_error(Stack<String> s) {
      debug.print("s=" + s);
      textp.error(s);
   }
   public void jobTab_packColumns(int pad) {
      debug.print("pad=" + pad);
      if (jobTab != null && jobTab.JobMonitor != null)
         TableUtil.autoSizeTableViewColumns(jobTab.JobMonitor, true);
   }
   public jobData jobTab_GetRowData(int row) {
      debug.print("row=" + row);
      return jobTab.GetRowData(row);
   }
   public void jobTab_UpdateJobMonitorRowStatus(final jobData job, final String status) {
      debug.print("job=" + job + " status=" + status);
      Platform.runLater(new Runnable() {
         @Override public void run() {
            jobTab.UpdateJobMonitorRowStatus(job, status);
         }
      });
   }
   public void jobTab_UpdateJobMonitorRowOutput(final jobData job, final String status) {
      debug.print("job=" + job + " status=" + status);
      Platform.runLater(new Runnable() {
         @Override public void run() {
            jobTab.UpdateJobMonitorRowOutput(job, status);
         }
      });
   }
   public void jobTab_AddJobMonitorRow(final jobData job, final String source, final String output) {
      debug.print("job=" + job + " source=" + source + " output=" + output);
      Platform.runLater(new Runnable() {
         @Override public void run() {
            jobTab.AddJobMonitorRow(job, source, output);
         }
      });
   }
   public void jobTab_RemoveJobMonitorRow(final jobData job) {
      debug.print("job=" + job);
      Platform.runLater(new Runnable() {
         @Override public void run() {
            jobTab.RemoveJobMonitorRow(job);
         }
      });
   }
   public void progressBar_setValue(final int value) {
      debug.print("value=" + value);
      Platform.runLater(new Runnable() {
         @Override public void run() {
            progressBar.setProgress((double)value/100.0);
         }
      });
   }
   public void refresh() {
      debug.print("");
      jContentPane.requestLayout();
   }
   public void nplTab_SetNowPlaying(String tivoName, Stack<Hashtable<String,String>> entries) {
      debug.print("tivoName=" + tivoName + " entries=" + entries);
      if (tivoTabs.containsKey(tivoName)) {
         tivoTabs.get(tivoName).nplTab_SetNowPlaying(entries);
      }
   }
   public void nplTab_clear(String tivoName) {
      debug.print("tivoName=" + tivoName);
      if (tivoTabs.containsKey(tivoName)) {
         tivoTabs.get(tivoName).nplTab_clear();
      }
   }
   public void nplTab_UpdateStatus(String tivoName, String status) {
      debug.print("tivoName=" + tivoName + " status=" + status);
      if (tivoTabs.containsKey(tivoName)) {
         tivoTabs.get(tivoName).nplTab_UpdateStatus(status);
      }
   }
   
   // Returns state of checkbox options (as int for writing to auto.ini purposes)
   public int metadata_setting() {
      debug.print("");
      int selected = 0;
      if (metadata.isSelected()) selected = 1;
      return selected;
   }
   public int decrypt_setting() {
      debug.print("");
      int selected = 0;
      if (decrypt.isSelected()) selected = 1;
      return selected;
   }
   public int qsfix_setting() {
      debug.print("");
      int selected = 0;
      if (qsfix.isSelected()) selected = 1;
      return selected;
   }
   public int twpdelete_setting() {
      debug.print("");
      int selected = 0;
      if (twpdelete.isSelected()) selected = 1;
      return selected;
   }
   public int rpcdelete_setting() {
      debug.print("");
      int selected = 0;
      if (rpcdelete.isSelected()) selected = 1;
      return selected;
   }
   public int comskip_setting() {
      debug.print("");
      int selected = 0;
      if (comskip.isSelected()) selected = 1;
      return selected;
   }
   public int comcut_setting() {
      debug.print("");
      int selected = 0;
      if (comcut.isSelected()) selected = 1;
      return selected;
   }
   public int captions_setting() {
      debug.print("");
      int selected = 0;
      if (captions.isSelected()) selected = 1;
      return selected;
   }
   public int encode_setting() {
      debug.print("");
      int selected = 0;
      if (encode.isSelected()) selected = 1;
      return selected;
   }
   public int push_setting() {
      debug.print("");
      int selected = 0;
      if (push.isSelected()) selected = 1;
      return selected;
   }
   public int custom_setting() {
      debug.print("");
      int selected = 0;
      if (custom.isSelected()) selected = 1;
      return selected;
   }
   
   // Identify NPL table items associated with queued/running jobs
   public void updateNPLjobStatus(Hashtable<String,String> map) {
      debug.print("map=" + map);
      Stack<String> tivoNames = config.getNplTivoNames();
      if (tivoNames.size() > 0) {
         for (int i=0; i<tivoNames.size(); i++) {
            tivoTab t = getTab(tivoNames.get(i));
            if (t != null) {
               nplTable npl = t.getTable();
               npl.updateNPLjobStatus(map);
            }
         }
      }
   }
   
   public String getWebColor(Color color) {
      debug.print("color=" + color);
      String c = String.format( "#%02X%02X%02X",
            (int)( color.getRed() * 255 ),
            (int)( color.getGreen() * 255 ),
            (int)( color.getBlue() * 255 ) );
      return(c);
   }

   @FXML
   public void helpToggleDisplayMenuItemCB() {
	   if (!helpSplit.getItems().contains(helpTabPane)) {
		   helpSplit.getItems().add(helpTabPane);
		   helpSplit.setDividerPosition(0, 0.75);
	   } else {
		   helpSplit.getItems().remove(helpTabPane);
	   }
   }

   @FXML
   public void closeHelpCB(Event event) {
	   helpSplit.getItems().remove(helpTabPane);
	   // prevent the actual close of the tab
	   event.consume();
   }

   	/**
	 * Show details area can show the fancy {@link ShowDetails}, a plain web
	 * view details, or a particular tivo's free space. This method will
	 * actually add any arbitrary content if not present, show that content and
	 * hide the rest of it.
	 * 
	 * @param content
	 *            if null, uses the showDetails instance.
	 */
	public void showDetails(Node content) {
		if (content == null) {
			content = showDetails;
		}
		boolean foundChild = false;
		for (Node child : show_details_stack.getChildren()) {
			if (child == content) {
				foundChild = true;
			} else {
				child.setVisible(false);
			}
		}
		if(!foundChild) {
			show_details_stack.getChildren().add(content);
		}
		content.setVisible(true);
	}

}
