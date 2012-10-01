package com.tivo.kmttg.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tivo.kmttg.main.config;
import com.tivo.kmttg.main.jobData;
import com.tivo.kmttg.main.jobMonitor;
import com.tivo.kmttg.util.backgroundProcess;
import com.tivo.kmttg.util.debug;
import com.tivo.kmttg.util.file;
import com.tivo.kmttg.util.log;
import com.tivo.kmttg.util.string;

public class atomic implements Serializable {
   private static final long serialVersionUID = 1L;
   private backgroundProcess process;
   private jobData job;
   private Stack<String> args = new Stack<String>();

   // constructor
   public atomic(jobData job) {
      debug.print("job=" + job);
      this.job = job;
   }
   
   public backgroundProcess getProcess() {
      return process;
   }
   
   public Boolean launchJob() {
      debug.print("");
      Boolean schedule = true;
      
      if ( ! file.isFile(config.AtomicParsley) ) {
         log.error("AtomicParsley not found: " + config.AtomicParsley);
         schedule = false;
      }
      
      if ( ! file.isFile(job.encodeFile) ) {
         log.error("encode file not found: " + job.encodeFile);
         schedule = false;
      }
      
      if ( ! file.isFile(job.metaFile) ) {
         log.error("metadata file not found: " + job.metaFile);
         schedule = false;
      }
      
      atomicGetArgs();
      if ( args.isEmpty() ) {
         log.error("Failed to parse metadata file: " + job.metaFile);
      }
      
      if (schedule) {
         // Create sub-folders for output file if needed
         if ( ! jobMonitor.createSubFolders(job.encodeFile, job) ) schedule = false;
      }
      
      if (schedule) {
         if ( start() ) {
            job.process_atomic   = this;
            jobMonitor.updateJobStatus(job, "running");
            job.time             = new Date().getTime();
         }
         return true;
      } else {
         return false;
      }      
   }

   // Return false if starting command fails, true otherwise
   private Boolean start() {
      debug.print("");
      Stack<String> command = new Stack<String>();
      command.add(config.AtomicParsley);
      for (int i=0; i<args.size(); ++i) {
         command.add(args.get(i));
      }
      process = new backgroundProcess();
      log.print(">> Running AtomicParsley on " + job.encodeFile + " ...");
      if ( process.run(command) ) {
         log.print(process.toString());
      } else {
         log.error("Failed to start command: " + process.toString());
         process.printStderr();
         process = null;
         jobMonitor.removeFromJobList(job);
         return false;
      }
      return true;
   }
   
   public void kill() {
      debug.print("");
      process.kill();
      log.warn("Killing '" + job.type + "' job: " + process.toString());
   }
   
   // Check status of a currently running job
   // Returns true if still running, false if job finished
   // If job is finished then check result
   public Boolean check() {
      //debug.print("");
      int exit_code = process.exitStatus();
      if (exit_code == -1) {
         // Still running
         if (config.GUIMODE) {
            // Update status in job table
            String t = jobMonitor.getElapsedTime(job.time);
            config.gui.jobTab_UpdateJobMonitorRowStatus(job, t);               
         }
        return true;
      } else {
         // Job finished         
         jobMonitor.removeFromJobList(job);
         
         // Check for problems
         int failed = 0;
         // No or empty encodeFile means problems
         if ( ! file.isFile(job.encodeFile) || file.isEmpty(job.encodeFile) ) {
            failed = 1;
         }
         
         // exit code != 0 => trouble
         if (exit_code != 0) {
            failed = 1;
         }
         
         if (failed == 1) {
            log.error("AtomicParsley failed (exit code: " + exit_code + " ) - check command: " + process.toString());
            process.printStderr();
         } else {
            // Print statistics for the job
            log.warn("AtomicParsley job completed: " + jobMonitor.getElapsedTime(job.time));
            log.print("---DONE--- job=" + job.type + " output=" + job.encodeFile);
            
            // Remove atomic parsley's temp file if left around
            // Ex: The Body (Recorded 02_15_2012)-temp-48968.mp4
            // Look for '-temp-\d+'
				File dir = new File(new File(job.encodeFile).getParent());	// get directory holding file
				File[] files = dir.listFiles(new FilenameFilter() {			// search directory for -temp files
							public boolean accept(File dir, String name) {
								return name.matches(".*-temp-\\d+.*");
							}
						});

				for (File tempfile : files) {
					log.print("Found temporary file left over from AtomicParsley job");
					log.warn("Removing " + tempfile.getName());
					tempfile.delete();
				}
         }
      }
      return false;
   }

   // Build arguments for AtomicParsley run based on encode file meta data file
   private void atomicGetArgs() {
      if ( ! file.isFile(job.metaFile) ) return;
      Hashtable <String,String> h = new Hashtable<String,String>();
      h.put("MediaKind", "Movie");
      
      // Parse metaFile
      try {
         BufferedReader ifp = new BufferedReader(new FileReader(job.metaFile));
         String line;
         String name="", value="";
         Pattern p = Pattern.compile("\\s*(\\S+)\\s*:\\s*(.*)");
         Matcher m;
         while ( (line = ifp.readLine()) != null ) {
            debug.print("line=" + line);
            m = p.matcher(line);
            if (m.matches()) {
               name  = m.group(1);
               value = m.group(2);
               // Get rid of quotes in value
               value = value.replaceAll("\"", "");
               value = value.replaceAll("'", "");
               if ( ! h.containsKey(name) ) h.put(name, value);
            }
         }
         ifp.close();
         if (h.containsKey("isEpisodic")) {
            if ( h.get("isEpisodic").equals("true") )
               h.put("MediaKind", "TV Show");
         }
         args.add(job.encodeFile);
         args.add("--overWrite");
         args.add("--stik");
         args.add(h.get("MediaKind"));
         if (h.containsKey("episodeTitle") ) {
            args.add("--title");
            args.add(h.get("episodeTitle"));
         }
         if (h.containsKey("vProgramGenre") ) {
            args.add("--grouping");
            args.add(h.get("vProgramGenre"));
         }
         if (h.containsKey("originalAirDate") ) {
            args.add("--year");
            args.add(h.get("originalAirDate"));
         } else if (h.containsKey("movieYear")) {
            args.add("--year");
            args.add(h.get("movieYear"));
         }
         if (h.containsKey("description") ) {
            args.add("--description");
            args.add(h.get("description"));
         }
         if (h.containsKey("title") ) {
            String title = h.get("title");
            args.add("--TVShowName");
            args.add(title);
            args.add("--artist");
            args.add(title);
            args.add("--albumArtist");
            args.add(title);
         }
         if (h.containsKey("episodeNumber") ) {
            args.add("--TVEpisode");
            args.add(h.get("episodeNumber"));
         }
         if (h.containsKey("episode") || h.containsKey("episodeNumber")) {
            String ep;
             if (h.containsKey("episode"))
                ep = h.get("episode");
             else
                ep = h.get("episodeNumber");
             args.add("--TVEpisodeNum");
             args.add(ep);
             args.add("--tracknum");
             args.add(ep);
         }
         if (h.containsKey("season") || h.containsKey("episodeNumber")) {
            String season;
            if (h.containsKey("season"))
               season = (h.get("season"));
            else {
               season = h.get("episodeNumber");
               if (season.length() == 3)
                  season = season.substring(0, 1);
               else if (season.length() == 4)
                  season = season.substring(0, 2);
               else if (season.length() == 5)
                  season = season.substring(0, 2);
            }
            args.add("--TVSeasonNum");
            args.add(season);
            
            if (h.containsKey("title")) {
               season = h.get("title") + ", Season " + season;
               args.add("--album");
               args.add(season);
            }
         }
         if (h.containsKey("callsign") ) {
            args.add("--TVNetwork");
            args.add(h.get("callsign"));
         }
         args.add("-d");
         args.add("1/1");
         if (h.containsKey("artwork")) {
            args.add("--artwork");
            args.add(h.get("artwork"));
         }
         if (h.containsKey("image")) {
            String image = string.dirname(job.metaFile) + File.separator + h.get("image");
            if (file.isFile(image)) {
               args.add("--artwork");
               args.add(image);
            }
         }
         return;
      }
      catch (IOException ex) {
         log.error(ex.toString());
         return;
      }
   }
}
