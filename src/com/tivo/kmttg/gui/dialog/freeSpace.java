package com.tivo.kmttg.gui.dialog;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.Stack;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.tivo.kmttg.gui.table.TableUtil;
import com.tivo.kmttg.gui.table.bitrateTable;
import com.tivo.kmttg.main.config;
import com.tivo.kmttg.util.debug;
import com.tivo.kmttg.util.log;
import com.tivo.kmttg.util.string;

public class freeSpace implements Initializable {
   private String tivoName = null;
   private Stage frame = null;
   private Stage dialog = null;
   @FXML private TextField space = null;
   private bitrateTable tab = null;
   @FXML private Label totals1 = null;
   @FXML private Label totals2 = null;
   private float disk_space = 0;
   private Hashtable<String,Hashtable<String,Double>> chanData = new Hashtable<String,Hashtable<String,Double>>();
   private Hashtable<String,Object> totalsData = new Hashtable<String,Object>();
   @FXML private PieChart chart = null;
   @FXML private ScrollPane tabScroll = null;
   
   private ResourceBundle bundle;
   
   public static freeSpace display(String tivoName, Stage frame) {
	   freeSpace result;
	   
	   try {
		   FXMLLoader loader = new FXMLLoader(freeSpace.class.getResource(
				   "freeSpace.fxml"));
		   // this controller must be instantiated here to initialize key information.
		   result = new freeSpace(tivoName, frame);
		   loader.setController(result);
		   
		   ResourceBundle bundle = ResourceBundle.getBundle("com.tivo.kmttg.gui.dialog.freeSpace");
		   loader.setResources(bundle);
		   VBox content = loader.<VBox>load();

		   // create and display dialog window
		   Stage dialog = new Stage();
		   dialog.initOwner(frame);
		   dialog.setTitle(MessageFormat.format(bundle.getString("dialog_title"), tivoName));
		   dialog.setScene(new Scene(content));
		   config.gui.setFontSize(dialog.getScene(), config.FontSize);
		   result.dialog = dialog;
		   result.dialog.show();
	   } catch (IOException e1) {
		   final StringWriter sw = new StringWriter();
		   final PrintWriter pw = new PrintWriter(sw, true);
		   e1.printStackTrace(pw);
		   config.gui.text_error(sw.getBuffer().toString());
		   // TODO Auto-generated catch block
		   e1.printStackTrace();
		   result = null;
	   }
	   return result;
   }
   private freeSpace(String tivoName, Stage frame) {
      this.tivoName = tivoName;
      this.frame = frame;
   }
   
   @Override
   public void initialize(URL location, ResourceBundle resources) {
	   this.bundle = resources;
	   
      chart.setPrefWidth(frame.getWidth());
      chart.setTitle(MessageFormat.format(bundle.getString("chart"),tivoName));

      // Populate dataset
      if ( ! setData() ) {
         log.error("Failed to obtain data for TiVo: " + tivoName);
         return;
      }
      
      space.setText(MessageFormat.format(bundle.getString("space"),getDiskSpace()));
      space.setTooltip(config.gui.getToolTip("total_disk_space"));

      updateLabels();
      
      // bitrateTable
      tab = new bitrateTable();
      tab.AddRows(chanData);
      tabScroll.setContent(tab.TABLE);
     
      TableUtil.autoSizeTableViewColumns(tab.TABLE, true);
   }
   
   @FXML private void spaceCB(final KeyEvent event) {
       if (event.isControlDown())
           return;
        if( event.getCode() == KeyCode.ENTER ) {
           setData();
           updateLabels();
           config.diskSpace.put(tivoName, getDiskSpace());
           config.save();
        }
	   
   }
   
   private Boolean setData() {
      // Init data to 0
      Hashtable<String,Float> data = new Hashtable<String,Float>();      
      data.put("suggestions", (float)0.0);
      data.put("kuid",        (float)0.0);
      data.put("kusn",        (float)0.0);
      
      // Init totalsData to 0
      totalsData.put("bytes", (double)0.0);
      totalsData.put("duration", (double)0.0);
      
      Stack<Hashtable<String,String>> entries = config.gui.getTab(tivoName).getTable().getEntries();
      if (entries == null) return false;
      Double duration, bytes;
      float sizeGB;
      totalsData.put("recordings", entries.size());
      for (int i=0; i<entries.size(); i++) {  
         duration = 0.0;
         bytes = 0.0;
         sizeGB = (float)0.0;
         if (entries.get(i).containsKey("duration")) {
            duration = Double.parseDouble(entries.get(i).get("duration"))/1000.0;
         }
         if (entries.get(i).containsKey("size")) {
            bytes = Double.parseDouble(entries.get(i).get("size"));
            sizeGB = (float) (bytes/Math.pow(2,30));
         }
         // Channel bit rates
         if (entries.get(i).containsKey("channel")) {
            String channel = entries.get(i).get("channel");            
            if ( ! chanData.containsKey(channel) ) {
               chanData.put(channel, new Hashtable<String,Double>());
               chanData.get(channel).put("bytes", 0.0);
               chanData.get(channel).put("duration", 0.0);
            }
            chanData.get(channel).put("bytes",    chanData.get(channel).get("bytes")+bytes);
            chanData.get(channel).put("duration", chanData.get(channel).get("duration")+duration);
         }
         
         // Duration totals
         totalsData.put("duration", (Double)totalsData.get("duration")+duration);
         totalsData.put("bytes", (Double)totalsData.get("bytes")+bytes);

         // Disk space allocation data
         if (entries.get(i).containsKey("suggestion")) {
            data.put("suggestions", data.get("suggestions") + sizeGB);
            continue;
         }
         if (entries.get(i).containsKey("kuid")) {
            data.put("kuid", data.get("kuid") + sizeGB);
            continue;
         }
         data.put("kusn", data.get("kusn") + sizeGB);
      }
            
      // Compute free space
      float available = getDiskSpace();
      float used = data.get("suggestions") + data.get("kuid") + data.get("kusn");
      float small = (float)0.001;
      if (available < small) {
         // Available not specified, so set to close to total used
         available = used - small;
         config.diskSpace.put(tivoName, available);
      }
      float free = available - used;
      if (free < 0) {
         // Set disk space available to used space if used > available
         disk_space = used;
         free = 0;
      }
      data.put("free", free);
      totalsData.put("free", free);
      
      int numSets = data.size();
      String[] legendLabels = new String[numSets];
      ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
      String[] keys = {"kuid", "kusn", "suggestions", "free"};
      for (int i=0; i<keys.length; ++i) {
         legendLabels[i] = MessageFormat.format(
            bundle.getString(keys[i]+"_label"),
            data.get(keys[i]), data.get(keys[i])*100/available
         );
         pieChartData.add(new PieChart.Data(legendLabels[i],data.get(keys[i])));
      }
      
      // Update Pie Chart data
      chart.setData(pieChartData);
      
      // Complete Totals data (recordings, bytes, duration set so far)
      totalsData.put("rate", bitrateTable.bitRate((Double)totalsData.get("bytes"),(Double)totalsData.get("duration")));
      totalsData.put("rate", String.format("%.2f", (Double)totalsData.get("rate")));
      totalsData.put("remaining",
         timeRemaining(
            (Double)totalsData.get("bytes"),
            (Float)totalsData.get("free")*Math.pow(2,30),
            (Double)totalsData.get("duration")
         )
      );
      totalsData.put("remaining", secsToHoursMins((Double)totalsData.get("remaining")));
      
      totalsData.put("recordings", "" + totalsData.get("recordings"));
      totalsData.put("bytes", String.format("%.2f", (Double)totalsData.get("bytes")/Math.pow(2,30)));
      totalsData.put("duration", secsToHoursMins((Double)totalsData.get("duration")));
            
      return true;
   }
   
   private void updateLabels() {
      totals1.setText(
				MessageFormat.format(bundle.getString("totals1"), 
				totalsData.get("recordings"), totalsData.get("bytes"), totalsData.get("duration"))
      );
      totals2.setText(
				MessageFormat.format(bundle.getString("totals2"), 
				totalsData.get("rate"), (Float) totalsData.get("free"), totalsData.get("remaining"))
      );
   }
   
   private float getDiskSpace() {
      if (config.diskSpace.containsKey(tivoName)) {
         disk_space = config.diskSpace.get(tivoName);
      }
      float available = disk_space;
      if (space != null) {
         String free_space = string.removeLeadingTrailingSpaces(space.getText());
         if (free_space.length() > 0) {
            try {
               available = Float.parseFloat(free_space);
            } catch(NumberFormatException e) {
               log.error("Disk space specification does not evaluate to a number: " + free_space);
               available = 0;
            }
         }
      }
      return available;
   }
   
   private Double timeRemaining(Double totalBytes, Double freeBytes, Double totalSecs) {
      return totalSecs*freeBytes/totalBytes;
   }
   
   private String secsToHoursMins(Double secs) {
      debug.print("secs=" + secs);
      Integer hours = (int) (secs/3600);
      Integer mins  = (int) (secs/60 - hours*60);
      return MessageFormat.format(bundle.getString("secsToHoursMins"), hours, mins);
   }  
   
   public void destroy() {
      if (dialog != null) dialog.close();
      chanData.clear();
      totalsData.clear();
   }
}
