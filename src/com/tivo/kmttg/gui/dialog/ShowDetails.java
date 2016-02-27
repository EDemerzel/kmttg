package com.tivo.kmttg.gui.dialog;

import java.util.HashSet;
import java.util.Set;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.tivo.kmttg.JSON.JSONArray;
import com.tivo.kmttg.JSON.JSONException;
import com.tivo.kmttg.JSON.JSONObject;
import com.tivo.kmttg.gui.table.TableUtil;
import com.tivo.kmttg.main.config;
import com.tivo.kmttg.rpc.Remote;
import com.tivo.kmttg.util.log;

public class ShowDetails implements Initializable {
   private ResourceBundle bundle = null;
   private Stage dialog = null;
   @FXML private Label mainTitle = null;
   @FXML private Label subTitle = null;
   @FXML private Label time = null;
   @FXML private Label channel = null;
   @FXML private Label description = null;
   @FXML private Label otherInfo = null;
   @FXML private Label actorInfo = null;
   @FXML private Label image = null;
   private double x=-1, y=-1;

   private static ShowDetails singleton;
   public static ShowDetails load(Stage frame) {
	   if(singleton == null) {
		   singleton = create(frame);
	   }
	   return singleton;
   }
   
   private static ShowDetails create(Stage frame) {
	   try {
		   FXMLLoader loader = new FXMLLoader(configMain.class.getResource(
				   "ShowDetails.fxml"));
		   ResourceBundle bundle = ResourceBundle.getBundle("com.tivo.kmttg.gui.dialog.ShowDetails");
		   loader.setResources(bundle);
		   HBox content = loader.<HBox>load();
		   
		   // "initialize" fires here.
		   
		   Scene scene = new Scene(content);
		   // save our official instance of configAuto
		   ShowDetails controller = loader.<ShowDetails>getController();
	      	  
	     Stage dialog;
         dialog = new Stage();
		 controller.dialog = dialog;
         dialog.setResizable(false);
         dialog.setTitle(bundle.getString("dialog_title"));
         dialog.initOwner(frame);
         // This so we can restore original dialog position when re-opened
         dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent arg0) {
               singleton.x = dialog.getX(); singleton.y = dialog.getY();
            }
         });
         config.gui.setFontSize(scene, config.FontSize);
         dialog.setScene(scene);
         return controller;
	   } catch (IOException e1) {
		   // TODO Auto-generated catch block
		   e1.printStackTrace();
	   }
	   return null;
   }
   
   @Override
   public void initialize(URL location, ResourceBundle resources) {
	   bundle = resources;
	   
       // Increase font size
       mainTitle.setFont(
          new Font(
             mainTitle.getFont().getFamily(),
             mainTitle.getFont().getSize()+5
          )
       );
   }
   
   public void update(Node node, String tivoName, String recordingId) {
      if ( ! config.rpcEnabled(tivoName) )
         return;
      JSONObject json = new JSONObject();
      try {
         json.put("levelOfDetail", "medium");
         json.put("recordingId", recordingId);
         update(node, tivoName, json);
      } catch (JSONException e) {
         log.error("ShowDetails update - " + e.getMessage());
      }
   }
   
   // Update dialog components with given JSON (runs as background task)
   public void update(final Node node, final String tivoName, final JSONObject initialJson) {
      if ( ! config.rpcEnabled(tivoName) )
         return;
      if (initialJson == null)
         return;
      Task<Void> task = new Task<Void>() {
         @Override public Void call() {
            JSONObject json = initialJson;
            try {
               // Need high level of detail
               if (json.has("levelOfDetail") && ! json.getString("levelOfDetail").equals("high")) {
                  Remote r = config.initRemote(tivoName);
                  if (r.success) {
                     JSONObject j = new JSONObject();
                     j.put("bodyId", r.bodyId_get());
                     j.put("levelOfDetail", "high");
                     JSONObject result;
                     if (json.has("recordingId")) {
                        j.put("recordingId", json.getString("recordingId"));
                        result = r.Command("recordingSearch", j);
                        if (result == null)
                           return null;
                        if (result.has("recording"))
                           json = result.getJSONArray("recording").getJSONObject(0);
                        else {
                           if (! json.has("title"))
                              return null;
                        }
                     }
                     else if (json.has("contentId")) {
                        j.put("contentId", json.getString("contentId"));
                        result = r.Command("contentSearch", j);
                        if (result == null)
                           return null;
                        if (result.has("content")) {
                           JSONObject content = result.getJSONArray("content").getJSONObject(0);
                           for (int ii=0; ii<content.names().length(); ii++) {
                              String name = content.names().getString(ii);
                              if (! json.has(name)) {
                                 json.put(name, content.get(name));
                              }
                           }
                        }
                        else {
                           if (! json.has("title"))
                              return null;
                        }
                     }
                  } else {
                     return null;
                  }
               } // json levelOfDetail
               
               if (json.has("idSetSource") && json.getJSONObject("idSetSource").has("collectionId")) {
                  // For SP table
                  Remote r = config.initRemote(tivoName);
                  if (r.success) {
                     JSONObject j = new JSONObject();
                     j.put("bodyId", r.bodyId_get());
                     j.put("count", 1);
                     j.put("levelOfDetail", "high");
                     j.put("collectionId", json.getJSONObject("idSetSource").getString("collectionId"));
                     JSONObject result = r.Command("collectionSearch", j);
                     if (result == null)
                        return null;
                     if (result.has("collection")) {
                        json = result.getJSONArray("collection").getJSONObject(0);
                     }
                     else {
                        if (! json.has("title"))
                           return null;
                     }
                  }
               }
               
               //log.print(json.toString(3));
            } catch (JSONException e) {
               log.error("ShowDetails update - " + e.getMessage());
               return null;
            }
            class backgroundRun implements Runnable {
               JSONObject json;
               public backgroundRun(JSONObject json) {
                  this.json = json;
               }
               @Override public void run() {
                  try {
                     // Title
                     String title = "";
                     // # 0:title, 1:intMovieYear
//                     "{0}{1,choice,0#|0< ({1,number})}",
                     title = MessageFormat.format(bundle.getString("title"),
									json.optString("title"),
									json.optInt("movieYear")
                    		 );
                     mainTitle.setText(title);            
                     
                     // Subtitle (possibly with season & episode information)
                     String subtitle = "";
                     // # 0:hasSubtitle? 1:subtitle, 2:floatStars, 3:intSeasonNum, 4:intEpisodeNum
//                     "{0,choice,0#|1#\"{1}\"}{2,choice,0#|0< Stars: {2,number,#.#}}{3,choice,0#|0<{4,choice,0#,0< (Sea {3} Ep {4})}}",
                     subtitle = MessageFormat.format(bundle.getString("subtitle"), 
                     		json.has("subtitle")?1:0, json.optString("subtitle"),
                     		starsToNum(json.optString("starRating")),
                     		json.optInt("seasonNumber"), 
                     		json.has("episodeNum")?json.getJSONArray("episodeNum").optInt(0):0
                     		);
                     subTitle.setText(subtitle);
                     
                     // channel
                     String chan = "";
                     if (json.has("channel")) {
                        JSONObject c = json.getJSONObject("channel");
                        // # 0:hasChannelNumber? 1: channelNumber, 2: hasCallsign?, 3:callSign
//                		"{0,choice,0#|1#{1}}{2,choice,0#|1# {3}}",
                        chan = MessageFormat.format(bundle.getString("channel"),
                        		c.has("channelNumber")?1:0, c.optString("channelNumber"), 
                        		c.has("callSign")?1:0, c.optString("callSign"));
                     }
                     channel.setText(chan);
            
                     // time
                     String t = "";
                     // # 0: hasStartTime? 1: startTime, 2: intDurationMins
//            		 "{0,choice,0#|1#{1}{2,choice,0#|0< ({2} mins)}}", 
                     t = MessageFormat.format(bundle.getString("time"),
                    		 json.has("startTime")?1:0, json.has("startTime")?
                    				 TableUtil.printableTimeFromJSON(json)
                    				 :"",
                    		 (json.has("startTime") && json.has("duration"))?
                    				 (int)Math.ceil((TableUtil.getEndTime(json)-TableUtil.getStartTime(json))/60000.0)
                    				 :0);
                     time.setText(t);
                        
                     // description
                     String desc = "";
                     // 0: description 1: CC?
//                     "{0}{1,choice,0#|1# (CC)}", 
                     desc = MessageFormat.format(
                    		 bundle.getString("description"),
                    		 json.optString("description"),
                    		 json.optBoolean("cc",false)?1:0);
                     description.setText(desc);
                     
                     // otherInfo
                     String other = "";
                     String categories = "";
                     if (json.has("category")) {
                        JSONArray cat = json.getJSONArray("category");
                        Set<String> c = new HashSet<String>();
                        for (int i=0; i<cat.length(); ++i) {
                           if (cat.getJSONObject(i).has("label"))
                              c.add(cat.getJSONObject(i).getString("label"));
                        }
                        for (String s : c)
                           categories += s + "; ";
                     }
                     
                     // # 0:hasmpaaRating? 1: mpaarating, 
                     //   2:hastvRating? 3:tvRating, 
                     //   4:hasCategory? 5:categorylist, 
                     //   6:hdtv?, 7:originalAirDate? 8: originalAirDate
//                     ""
//            		 + "{0,choice,0#|1#Rated {1}; }"
//            		 + "{2,choice,0#|1#TV {3}; }"
//            		 + "{4,choice,0#|1#{5}}"
//            		 + "{6,choice,0#|1#HD; }"
//            		 + "{7,choice,0#|1#First Aired: {8}}"
                     other = MessageFormat.format(bundle.getString("other"),
                    		 json.has("mpaaRating")?1:0, json.optString("mpaaRating").toUpperCase(),
                    		 json.has("tvRating")?1:0, json.optString("tvRating").toUpperCase(),
                    		 json.has("category")?1:0, categories,
                    		 json.optBoolean("hdtv",false)?1:0,
                    		 json.has("originalAirdate")?1:0, json.optString("originalAirdate"));
                     if (other.endsWith("; "))
                        other = other.substring(0, other.length()-2);
                     
                     otherInfo.setText(other);
                     
                     // actorInfo
                     String actors = "";
                     if (json.has("credit")) {
                        int count = 0;
                        String actorlist="";
                        int actorcount = 0;
                        String hostlist="";
                        int hostcount = 0;
                        JSONArray credit = json.getJSONArray("credit");
                        // actors
                        for (int i=0; i<credit.length(); ++i) {
                           JSONObject a = credit.getJSONObject(i);
                           if (a.getString("role").equals("actor")) {
                              if (a.has("first") || a.has("last")) {
//                                // #0:first name, 1: last name, 2: actor number (1st = 1), 3: credit number (1st=1) 
//                                actor="{3,choice,1#|1<, }{0} {1}"
//                                ,actorfirst, actorlast, actorcount+1, count+1
                            	 actorlist += MessageFormat.format(bundle.getString("actor"), a.optString("first",""), a.optString("last",""), actorcount+1, count+1);
                                 count++;
                                 actorcount++;
                              }
                           }
                        }
                        // hosts
                        Boolean pyTivo = false;
                        for (int i=0; i<credit.length(); ++i) {
                           JSONObject a = credit.getJSONObject(i);
                           if (a.getString("role").equals("host") && a.has("first")) {
                              if (a.getString("first").equals("container"))
                                 pyTivo = true;
                              if (a.has("last") && a.getString("last").contains("TRANSCODE"))
                                 pyTivo = true;
                           }
                        }
                        if (!pyTivo) {
                           for (int i=0; i<credit.length(); ++i) {
                              JSONObject a = credit.getJSONObject(i);
                              if (a.getString("role").equals("host")) {
                                 if (a.has("first") || a.has("last")) {
//                                    actors += separator + a.getString("first") + " " + a.getString("last");
//                                   // #0:first name, 1: last name, 2: host number (1st = 1), 3: credit number (1st=1) 
//                                   host="{3,choice,1#|1<, }{0} {1}"
//                                   ,hostfirst, hostlast, hostcount+1, count+1
                                	 hostlist += MessageFormat.format(bundle.getString("host"), a.optString("first",""), a.optString("last",""), hostcount+1, count+1);
                                    count++;
                                    hostcount++;
                                 }
                              }
                           }                     
                        }
//                      // #0:actors, 1:actors total, 2:hosts, 3:hosts total, 4:overall total
//                      actors = "{0}{1,choice,0#|0<{3,choice,0#|0<, }}{2}"
//                      ,actorlist, actorcount, hostlist, hostcount, count
                        actors = MessageFormat.format(bundle.getString("credits"), actorlist, actorcount, hostlist, hostcount, count);
                     }
                     actorInfo.setText(actors);
                     
                     // Right panel image
                     if (json.has("image")) {
                        image.setText("");
                        setImage(json.getJSONArray("image"));
                     }
                     else {
                        image.setText("");
                        image.setGraphic(null);
                     }
                  } catch (JSONException e) {
                     log.error("ShowDetails update - " + e.getMessage());
                     return;
                  }
                  dialog.sizeToScene();
                  if (x != -1 && ! dialog.isShowing()) {
                     dialog.setX(x); dialog.setY(y);
                  }
                  dialog.show();
                  Platform.runLater(new Runnable() {
                     @Override public void run() {
                        node.requestFocus();
                     }
                  });
               }
            }
            Platform.runLater(new backgroundRun(json));
            return null;
         }
      };
      new Thread(task).start();
   }
   
   private double starsToNum(String name) {
      name = name.toLowerCase();
      name = name.replace("zero", "0");
      name = name.replace("one", "1");
      name = name.replace("two", "2");
      name = name.replace("three", "3");
      name = name.replace("four", "4");
      name = name.replace("five", "5");
      name = name.replace("point", ".");
      try {
    	  return Double.valueOf(name);
      } catch(Exception e) {
    	  return 0.0;
      }
   }
   
   public Boolean isShowing() {
      return dialog.isShowing();
   }
   
   private void setImage(JSONArray imageArray) {
      try {
         int diff = 500;
         int desired = 180;
         int index = 0;
         // 1st find closest to desired height
         for (int i=0; i<imageArray.length(); ++i) {
            JSONObject j = imageArray.getJSONObject(i);
            int h = j.getInt("height");
            if (Math.abs(desired-h) < diff) {
               index = i;
               diff = Math.abs(desired-h);
            }
         }
         // Now set according to selected height
         setImage(imageArray.getJSONObject(index).getString("imageUrl"));
      } catch (JSONException e) {
         log.error("ShowDetails setImage - " + e.getMessage());
      }
   }
   
   private void setImage(String urlString) {
      image.setGraphic(new ImageView(new Image(urlString)));
   }
}
