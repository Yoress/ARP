/**
 * This file is part of Aion-Lightning <aion-lightning.org>.
 *
 * Aion-Lightning is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Aion-Lightning is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. *
 *
 * You should have received a copy of the GNU General Public License along with Aion-Lightning. If not, see <http://www.gnu.org/licenses/>.
 *
 * 
 * Credits goes to all Open Source Core Developer Groups listed below Please do not change here something, ragarding the developer credits, except the
 * "developed by XXXX". Even if you edit a lot of files in this source, you still have no rights to call it as "your Core". Everybody knows that this
 * Emulator Core was developed by Aion Lightning
 * 
 * @-Aion-Unique-
 * @-Aion-Lightning
 * @Aion-Engine
 * @Aion-Extreme
 * @Aion-NextGen
 * @Aion-Core Dev.
 */
package com.aionemu.chatserver.service;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.chatserver.ShutdownHook;
import com.aionemu.chatserver.configs.Config;
import com.aionemu.chatserver.model.RestartFrequency;

/**
 *
 * @author nrg
 */
public class RestartService {
	
	private static final Logger log = LoggerFactory.getLogger(RestartService.class);
	private static final RestartService instance = new RestartService();
	
	private RestartService() {
		RestartFrequency rf;
		try {
			rf = RestartFrequency.valueOf(Config.CHATSERVER_RESTART_FREQUENCY);
		} catch (Exception e) {
			log.warn("Could not find stated RestartFrequency. Using NEVER as default value!");
			rf = RestartFrequency.NEVER;
		}
		setTimer(rf);
	}
	
	private void setTimer(RestartFrequency frequency) {
		// get time to restart
		String[] time = getRestartTime();
		int hour = Integer.parseInt(time[0]);
		int minute = Integer.parseInt(time[1]);
		
		// calculate the correct time based on frequency
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		boolean isMissed = calendar.getTimeInMillis() < System.currentTimeMillis();
		
		// switch frequency
		switch (frequency) {
		case NEVER:
			return;
		case DAILY:
			if (isMissed) // execute next day if we missed the time today (what
							 // is mostly the case)
			{
				calendar.add(Calendar.DAY_OF_YEAR, 1);
			}
			break;
		case WEEKLY:
			calendar.add(Calendar.WEEK_OF_YEAR, 1);
			break;
		case MONTHLY:
			calendar.add(Calendar.MONTH, 1);
		}
		
		// Restart timer
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				RestartService.log.info("Restart task is triggered - restarting chatserver!");
				ShutdownHook.setRestartOnly(true);
				ShutdownHook.getInstance().start();
			}
		}, calendar.getTime());
		
		log.info("Scheduled next restart for " + calendar.getTime().toString());
	}
	
	private String[] getRestartTime() {
		String[] time;
		if ((time = Config.CHATSERVER_RESTART_TIME.split(":")).length != 2) {
			log.warn("You did not state a valid restart time. Using 5:00 AM as default value!");
			return new String[] {"5", "0"};
		}
		return time;
	}
	
	public static RestartService getInstance() {
		return instance;
	}
}
