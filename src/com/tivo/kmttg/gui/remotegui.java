package com.tivo.kmttg.gui;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.util.Stack;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.tivo.kmttg.JSON.JSONException;
import com.tivo.kmttg.JSON.JSONObject;
import com.tivo.kmttg.main.config;
import com.tivo.kmttg.main.jobData;
import com.tivo.kmttg.main.jobMonitor;
import com.tivo.kmttg.rpc.Remote;
import com.tivo.kmttg.util.debug;
import com.tivo.kmttg.util.log;
import com.tivo.kmttg.util.string;

public class remotegui {
   private JDialog dialog = null;
   private JTabbedPane tabbed_panel = null;
   
   private todoTable tab_todo = null;
   private JComboBox tivo_todo = null;
   
   private spTable tab_sp = null;
   private JComboBox tivo_sp = null;
   
   private JComboBox tivo_rc = null;
   private JTextField rc_jumpto_text = null;
   private JTextField rc_jumpahead_text = null;
   private JTextField rc_jumpback_text = null;

   remotegui(JFrame frame) {
      
      dialog = new JDialog(frame, false); // non-modal dialog
      //dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); // Destroy when closed
      dialog.setTitle("Remote Control");
      
      // Define content for dialog window
      int gy = 0;
      GridBagConstraints c = new GridBagConstraints();
      c.ipady = 0;
      c.weighty = 0.0;  // default to no vertical stretch
      c.weightx = 0.0;  // default to no horizontal stretch
      c.gridx = 0;
      c.gridy = gy;
      c.gridwidth = 1;
      c.gridheight = 1;
      c.anchor = GridBagConstraints.CENTER;
      c.fill = GridBagConstraints.HORIZONTAL;
      
      tabbed_panel = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
            
      // ToDo Title + Tivo Selector + Refresh button
      Dimension space_40 = new Dimension(40,0);
      Dimension space_5 = new Dimension(5,0);
      
      // ToDo Tab items      
      JPanel panel_todo = new JPanel();
      panel_todo.setLayout(new GridBagLayout());
      
      JPanel row1_todo = new JPanel();
      row1_todo.setLayout(new BoxLayout(row1_todo, BoxLayout.LINE_AXIS));
      
      JLabel title_todo = new JLabel("ToDo list");
      
      JLabel tivo_todo_label = new javax.swing.JLabel();
      
      tivo_todo = new javax.swing.JComboBox();
      tivo_todo.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
             if (e.getStateChange() == ItemEvent.SELECTED) {
               tivo_todoCB();
            }
         }
      });
      tivo_todo.setToolTipText(getToolTip("tivo_todo"));

      JButton refresh_todo = new JButton("Refresh");
      //ImageIcon image = new ImageIcon("c:/home/tivoapp/pngs/remote-button-TIVO-63x86.png");
      //JButton refresh_todo = new JButton(scale(image.getImage(),0.5));
      refresh_todo.setToolTipText(getToolTip("refresh_todo"));
      refresh_todo.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent e) {
            // Refresh to do list
            tab_todo.TABLE.clearSelection();
            tab_todo.clear();
            dialog.repaint();
            String tivoName = (String)tivo_todo.getSelectedItem();
            if (tivoName != null && tivoName.length() > 0)
               ToDoListCB(tivoName);
         }
      });
      row1_todo.add(Box.createRigidArea(space_40));
      row1_todo.add(title_todo);
      row1_todo.add(Box.createRigidArea(space_40));
      row1_todo.add(tivo_todo_label);
      row1_todo.add(Box.createRigidArea(space_5));
      row1_todo.add(tivo_todo);
      row1_todo.add(Box.createRigidArea(space_40));
      row1_todo.add(refresh_todo);
      panel_todo.add(row1_todo, c);
      
      tab_todo = new todoTable(dialog);
      tab_todo.TABLE.setPreferredScrollableViewportSize(tab_todo.TABLE.getPreferredSize());
      JScrollPane tabScroll_todo = new JScrollPane(tab_todo.scroll);
      gy++;
      c.gridy = gy;
      c.weightx = 1.0;
      c.weighty = 1.0;
      c.gridwidth = 8;
      c.fill = GridBagConstraints.BOTH;
      panel_todo.add(tabScroll_todo, c);
            
      // Season Passes Tab items      
      c.ipady = 0;
      c.weighty = 0.0;  // default to no vertical stretch
      c.weightx = 0.0;  // default to no horizontal stretch
      c.gridx = 0;
      c.gridy = gy;
      c.gridwidth = 1;
      c.gridheight = 1;
      c.anchor = GridBagConstraints.CENTER;
      c.fill = GridBagConstraints.HORIZONTAL;

      JPanel panel_sp = new JPanel();
      panel_sp.setLayout(new GridBagLayout());
      
      JPanel row1_sp = new JPanel();
      row1_sp.setLayout(new BoxLayout(row1_sp, BoxLayout.LINE_AXIS));
      
      JLabel title_sp = new JLabel("Season Passes");
      
      JLabel tivo_sp_label = new javax.swing.JLabel();
      
      tivo_sp = new javax.swing.JComboBox();
      tivo_sp.addItemListener(new ItemListener() {
         public void itemStateChanged(ItemEvent e) {
             if (e.getStateChange() == ItemEvent.SELECTED) {
               tivo_spCB();
            }
         }
      });
      tivo_sp.setToolTipText(getToolTip("tivo_sp"));

      JButton refresh_sp = new JButton("Refresh");
      refresh_sp.setToolTipText(getToolTip("refresh_sp"));
      refresh_sp.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent e) {
            // Refresh to do list
            tab_sp.TABLE.clearSelection();
            tab_sp.clear();
            dialog.repaint();
            String tivoName = (String)tivo_sp.getSelectedItem();
            if (tivoName != null && tivoName.length() > 0)
               SPListCB(tivoName);
         }
      });
      row1_sp.add(Box.createRigidArea(space_40));
      row1_sp.add(title_sp);
      row1_sp.add(Box.createRigidArea(space_40));
      row1_sp.add(tivo_sp_label);
      row1_sp.add(Box.createRigidArea(space_5));
      row1_sp.add(tivo_sp);
      row1_sp.add(Box.createRigidArea(space_40));
      row1_sp.add(refresh_sp);
      panel_sp.add(row1_sp, c);
      
      tab_sp = new spTable(dialog);
      tab_sp.TABLE.setPreferredScrollableViewportSize(tab_sp.TABLE.getPreferredSize());
      JScrollPane tabScroll_sp = new JScrollPane(tab_sp.scroll);
      gy++;
      c.gridy = gy;
      c.weightx = 1.0;
      c.weighty = 1.0;
      c.gridwidth = 8;
      c.fill = GridBagConstraints.BOTH;
      panel_sp.add(tabScroll_sp, c);
      
      // Remote Control Tab items
      gy = 0;
      c.insets = new Insets(0, 2, 0, 2);
      c.ipady = 0;
      c.weighty = 0.0;  // default to no vertical stretch
      c.weightx = 0.0;  // default to no horizontal stretch
      c.gridx = 0;
      c.gridy = gy;
      c.gridwidth = 1;
      c.gridheight = 1;
      c.anchor = GridBagConstraints.NORTHWEST;
      c.fill = GridBagConstraints.HORIZONTAL;

      JPanel panel_rc = new JPanel(new GridBagLayout());
      
      JLabel title_rc = new JLabel("Advanced Remote Controls");            
      tivo_rc = new javax.swing.JComboBox();
      tivo_rc.setToolTipText(getToolTip("tivo_rc"));

      c.gridx = 0;
      c.gridy = gy;
      panel_rc.add(title_rc, c);
      c.gridx = 1;
      c.gridy = gy;
      panel_rc.add(tivo_rc, c);

      JButton rc_jumpto_button = new JButton("Jump to minute:");
      rc_jumpto_button.setToolTipText(getToolTip("rc_jumpto_text"));
      rc_jumpto_button.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent e) {
            String tivoName = (String)tivo_rc.getSelectedItem();
            String mins_string = string.removeLeadingTrailingSpaces(rc_jumpto_text.getText());
            if (tivoName == null || tivoName.length() == 0)
               return;
            if (mins_string == null || mins_string.length() == 0)
               return;
            try {
               int mins = Integer.parseInt(mins_string);
               RC_jumptoCB(tivoName, mins);
            } catch (NumberFormatException e1) {
               log.error("Illegal number of minutes specified: " + mins_string);
               return;
            }            
         }
      });
      rc_jumpto_text = new JTextField(15);
      rc_jumpto_text.setToolTipText(getToolTip("rc_jumpto_text"));
      rc_jumpto_text.setText("0");
      
      gy++;
      c.gridx = 0;
      c.gridy = gy;
      panel_rc.add(rc_jumpto_button, c);
      c.gridx = 1;
      panel_rc.add(rc_jumpto_text, c);

      JButton rc_jumpahead_button = new JButton("Skip minutes ahead:");
      rc_jumpahead_button.setToolTipText(getToolTip("rc_jumpahead_text"));
      rc_jumpahead_button.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent e) {
            String tivoName = (String)tivo_rc.getSelectedItem();
            String mins_string = string.removeLeadingTrailingSpaces(rc_jumpahead_text.getText());
            if (tivoName == null || tivoName.length() == 0)
               return;
            if (mins_string == null || mins_string.length() == 0)
               return;
            try {
               int mins = Integer.parseInt(mins_string);
               RC_jumpaheadCB(tivoName, mins);
            } catch (NumberFormatException e1) {
               log.error("Illegal number of minutes specified: " + mins_string);
               return;
            }            
         }
      });
      rc_jumpahead_text = new JTextField(15);
      rc_jumpahead_text.setToolTipText(getToolTip("rc_jumpahead_text"));
      rc_jumpahead_text.setText("5");
      
      gy++;
      c.gridx = 0;
      c.gridy = gy;      
      panel_rc.add(rc_jumpahead_button, c);
      c.gridx = 1;
      panel_rc.add(rc_jumpahead_text, c);

      JButton rc_jumpback_button = new JButton("Skip minutes back:");
      rc_jumpback_button.setToolTipText(getToolTip("rc_jumpback_text"));
      rc_jumpback_button.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent e) {
            String tivoName = (String)tivo_rc.getSelectedItem();
            String mins_string = string.removeLeadingTrailingSpaces(rc_jumpback_text.getText());
            if (tivoName == null || tivoName.length() == 0)
               return;
            if (mins_string == null || mins_string.length() == 0)
               return;
            try {
               int mins = Integer.parseInt(mins_string);
               RC_jumpbackCB(tivoName, mins);
            } catch (NumberFormatException e1) {
               log.error("Illegal number of minutes specified: " + mins_string);
               return;
            }            
         }
      });
      rc_jumpback_text = new JTextField(15);
      rc_jumpback_text.setToolTipText(getToolTip("rc_jumpback_text"));
      rc_jumpback_text.setText("5");
      gy++;
      c.gridx = 0;
      c.gridy = gy;      
      panel_rc.add(rc_jumpback_button, c);
      c.gridx = 1;
      panel_rc.add(rc_jumpback_text, c);
      
      tabbed_panel.add("Remote Control", panel_rc);
      tabbed_panel.add("ToDo", panel_todo);
      tabbed_panel.add("Season Passes", panel_sp);
            
      setTivoNames();

      // add content to and display dialog window
      dialog.setContentPane(tabbed_panel);
      dialog.pack();
      dialog.setSize((int)(frame.getSize().width/1.3), (int)(frame.getSize().height/2));
      dialog.setLocationRelativeTo(config.gui.getJFrame().getJMenuBar().getComponent(0));
      tab_todo.packColumns(tab_todo.TABLE, 2);
      tab_sp.packColumns(tab_sp.TABLE, 2);
      dialog.setVisible(true);
      
   }
   
   // TiVo selection changed for ToDo tab
   public void tivo_todoCB() {
      tab_todo.TABLE.clearSelection();
      tab_todo.clear();
      String tivoName = getTivoName("todo");
      if (tab_todo.tivo_data.containsKey(tivoName))
         tab_todo.AddRows(tivoName, tab_todo.tivo_data.get(tivoName));
   }
      
   // Submit remote ToDo List request to Job Monitor
   public void ToDoListCB(String tivoName) {
      jobData job = new jobData();
      job.source      = dialog.toString();
      job.tivoName    = tivoName;
      job.type        = "remote";
      job.name        = "Remote";
      job.remote_todo = true;
      job.todo        = tab_todo;
      jobMonitor.submitNewJob(job);
   }
   
   // TiVo selection changed for Season Passes tab
   public void tivo_spCB() {
      tab_sp.TABLE.clearSelection();
      tab_sp.clear();
      String tivoName = getTivoName("sp");
      if (tab_sp.tivo_data.containsKey(tivoName))
         tab_sp.AddRows(tivoName, tab_sp.tivo_data.get(tivoName));
   }
      
   // Submit remote SP request to Job Monitor
   public void SPListCB(String tivoName) {
      jobData job = new jobData();
      job.source      = dialog.toString();
      job.tivoName    = tivoName;
      job.type        = "remote";
      job.name        = "Remote";
      job.remote_sp   = true;
      job.sp          = tab_sp;
      jobMonitor.submitNewJob(job);
   }
   
   public Boolean RC_jumptoCB(String tivoName, Integer mins) {
      Remote r = new Remote(config.TIVOS.get(tivoName), config.MAK);
      if (r.success) {
         JSONObject json = new JSONObject();
         try {
            System.out.println("tivoName=" + tivoName + " mins=" + mins);
            Long pos = (long)60000*mins;
            json.put("offset", pos);
            r.Key("jump", json);
            r.disconnect();
         } catch (JSONException e) {
            log.print("RC_jumptoCB failed - " + e.getMessage());
         }
      }
      return true;
   }
   
   public Boolean RC_jumpaheadCB(String tivoName, Integer mins) {
      Remote r = new Remote(config.TIVOS.get(tivoName), config.MAK);
      if (r.success) {
         JSONObject json = new JSONObject();
         JSONObject reply = r.Key("position", json);
         if (reply != null && reply.has("position")) {
            try {
               Long pos = reply.getLong("position");
               pos += (long)60000*mins;
               json.put("offset", pos);
               r.Key("jump", json);
               r.disconnect();
            } catch (JSONException e) {
               log.print("RC_jumptoCB failed - " + e.getMessage());
            }
         }
      }
      return true;
   }
   
   public Boolean RC_jumpbackCB(String tivoName, Integer mins) {
      Remote r = new Remote(config.TIVOS.get(tivoName), config.MAK);
      if (r.success) {
         JSONObject json = new JSONObject();
         JSONObject reply = r.Key("position", json);
         if (reply != null && reply.has("position")) {
            try {
               Long pos = reply.getLong("position");
               pos -= (long)60000*mins;
               if (pos < 0)
                  pos = (long)0;
               json.put("offset", pos);
               r.Key("jump", json);
               r.disconnect();
            } catch (JSONException e) {
               log.print("RC_jumptoCB failed - " + e.getMessage());
            }
         }
      }
      return true;
   }
         
   public void display() {
      if (dialog != null)
         dialog.setVisible(true);
   }
   
   public String getTivoName(String tab) {
      if (tab.equals("todo"))
         return (String)tivo_todo.getSelectedItem();
      if (tab.equals("sp"))
         return (String)tivo_sp.getSelectedItem();
      if (tab.equals("rc"))
         return (String)tivo_rc.getSelectedItem();
      return null;
   }
   
   public void setTivoNames() {      
      Stack<String> tivo_stack = config.getTivoNames();
      tivo_todo.removeAllItems();
      tivo_sp.removeAllItems();
      tivo_rc.removeAllItems();
      for (int i=0; i<tivo_stack.size(); ++i) {
         if (config.getRpcSetting(tivo_stack.get(i)).equals("1")) {
            tivo_todo.addItem(tivo_stack.get(i));
            tivo_sp.addItem(tivo_stack.get(i));
            tivo_rc.addItem(tivo_stack.get(i));
         }
      }
   }
      
   private ImageIcon scale(Image src, double scale) {
      int w = (int)(scale*src.getWidth(dialog));
      int h = (int)(scale*src.getHeight(dialog));
      int type = BufferedImage.TYPE_INT_RGB;
      BufferedImage dst = new BufferedImage(w, h, type);
      Graphics2D g2 = dst.createGraphics();
      g2.drawImage(src, 0, 0, w, h, dialog);
      g2.dispose();
      return new ImageIcon(dst);
   }
      
   private String getToolTip(String component) {
      debug.print("component=" + component);
      String text = "";
      if (component.equals("tivo_todo")) {
         text = "Select TiVo for which to retrieve To Do list.<br>";
         text += "<b>NOTE: This only works for Premiere models</b>.";
      }
      else if (component.equals("refresh_todo")){
         text = "<b>Refresh</b><br>";
         text += "Refresh To Do list of selected TiVo.<br>";
         text += "<b>NOTE: This only works for Premiere models</b>.";
      }
      else if (component.equals("tivo_sp")) {
         text = "Select TiVo for which to retrieve Season Passes list.<br>";
         text += "<b>NOTE: This only works for Premiere models</b>.";
      }
      else if (component.equals("tivo_rc")) {
         text = "Select which TiVo you want to control.<br>";
         text += "<b>NOTE: This only works for Premiere models</b>.";
      }
      else if (component.equals("refresh_sp")){
         text = "<b>Refresh</b><br>";
         text += "Refresh Season Pass list of selected TiVo.<br>";
         text += "<b>NOTE: This only works for Premiere models</b>.";
      }
      else if (component.equals("tivo_rnpl")) {
         text = "Select TiVo for which to retrieve My Shows list.<br>";
         text += "<b>NOTE: This only works for Premiere models</b>.<br>";
         text += "NOTE: You can select items in table to PLAY or DELETE<br>";
         text += "using <b>Space bar</b> to play, <b>Delete</b> button to delete.<br>";
         text += "NOTE: Only 1 item can be deleted or played at a time.";
      }
      else if (component.equals("refresh_rnpl")){
         text = "<b>Refresh</b><br>";
         text += "Refresh My Shows list of selected TiVo.<br>";
         text += "<b>NOTE: This only works for Premiere models</b>.<br>";
         text += "NOTE: You can select items in table to PLAY or DELETE<br>";
         text += "using <b>Space bar</b> to play, <b>Delete</b> button to delete.<br>";
         text += "NOTE: Only 1 item can be deleted or played at a time.";
      }
      else if (component.equals("tab_rnpl")) {
         text = "NOTE: You can select items in table to PLAY or DELETE<br>";
         text += "using <b>Space bar</b> to play, <b>Delete</b> button to delete.<br>";
         text += "NOTE: Only 1 item can be deleted or played at a time.";
      }
      else if (component.equals("rc_jumpto_text")) {
         text = "Set playback position to exactly this number of minutes into the show.";
      }
      else if (component.equals("rc_jumpahead_text")) {
         text = "Set playback position this number of minutes ahead of current position.";
      }
      else if (component.equals("rc_jumpback_text")) {
         text = "Set playback position this number of minutes behind current position.";
      }
      
      if (text.length() > 0) {
         text = "<html>" + text + "</html>";
      }
      return text;
   }

}
