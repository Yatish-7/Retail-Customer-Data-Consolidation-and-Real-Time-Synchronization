package com.newmeksolutions;

import com.newmeksolutions.config.AppConfig;
import com.newmeksolutions.listener.TriggerListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {
    public static void main(String[] args) 
    {
        // Entry Point for the Project// 
    	TriggerListener listener = new TriggerListener();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Poll changes every POLL_INTERVAL_SECONDS
        scheduler.scheduleAtFixedRate(() -> {
            listener.pollChanges();
        }, 0, AppConfig.POLL_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }
  }

