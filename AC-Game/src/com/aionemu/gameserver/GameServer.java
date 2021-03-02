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
package com.aionemu.gameserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.DatabaseFactory;
import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.network.NioServer;
import com.aionemu.commons.network.ServerCfg;
import com.aionemu.commons.services.CronService;
import com.aionemu.commons.utils.AEInfos;
import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.ai2.manager.LookManager;
import com.aionemu.gameserver.ai2.mechanics.AIMechanics;
import com.aionemu.gameserver.cache.HTMLCache;
import com.aionemu.gameserver.configs.Config;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.configs.main.AutoGroupConfig;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.EventSystem;
import com.aionemu.gameserver.configs.main.EventsConfig;
import com.aionemu.gameserver.configs.main.GSConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.configs.main.ThreadConfig;
import com.aionemu.gameserver.configs.main.WeddingsConfig;
import com.aionemu.gameserver.configs.network.NetworkConfig;
import com.aionemu.gameserver.dao.PlayerDAO;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.eventEngine.battleground.services.battleground.BattleGroundManager;
import com.aionemu.gameserver.eventEngine.crazy_daeva.CrazyDaevaService;
import com.aionemu.gameserver.instance.InstanceEngine;
import com.aionemu.gameserver.model.GameEngine;
import com.aionemu.gameserver.model.house.MaintenanceTask;
import com.aionemu.gameserver.model.siege.Influence;
import com.aionemu.gameserver.network.BannedMacManager;
import com.aionemu.gameserver.network.aion.GameConnectionFactoryImpl;
import com.aionemu.gameserver.network.chatserver.ChatServer;
import com.aionemu.gameserver.network.loginserver.LoginServer;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.services.AdminService;
import com.aionemu.gameserver.services.AnnouncementService;
import com.aionemu.gameserver.services.BaseService;
import com.aionemu.gameserver.services.BeritraService;
import com.aionemu.gameserver.services.BrokerService;
import com.aionemu.gameserver.services.ChallengeTaskService;
import com.aionemu.gameserver.services.CuringZoneService;
import com.aionemu.gameserver.services.DatabaseCleaningService;
import com.aionemu.gameserver.services.DebugService;
import com.aionemu.gameserver.services.DisputeLandService;
import com.aionemu.gameserver.services.EventService;
import com.aionemu.gameserver.services.ExchangeService;
import com.aionemu.gameserver.services.FlyRingService;
import com.aionemu.gameserver.services.GameTimeService;
import com.aionemu.gameserver.services.HousingBidService;
import com.aionemu.gameserver.services.LimitedItemTradeService;
import com.aionemu.gameserver.services.LoginEventService;
import com.aionemu.gameserver.services.NpcShoutsService;
import com.aionemu.gameserver.services.PeriodicSaveService;
import com.aionemu.gameserver.services.RestartService;
import com.aionemu.gameserver.services.RiftService;
import com.aionemu.gameserver.services.RoadService;
import com.aionemu.gameserver.services.SerialKillerService;
import com.aionemu.gameserver.services.ShieldService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.SupportService;
import com.aionemu.gameserver.services.TownService;
import com.aionemu.gameserver.services.VortexService;
import com.aionemu.gameserver.services.WeatherService;
import com.aionemu.gameserver.services.WebshopService;
import com.aionemu.gameserver.services.WeddingService;
import com.aionemu.gameserver.services.abyss.AbyssRankUpdateService;
import com.aionemu.gameserver.services.drop.DropRegistrationService;
import com.aionemu.gameserver.services.ecfunctions.WordFilterService;
import com.aionemu.gameserver.services.ecfunctions.ffa.FFaService;
import com.aionemu.gameserver.services.ecfunctions.oneVsone.OneVsOneService;
import com.aionemu.gameserver.services.gc.GarbageCollector;
import com.aionemu.gameserver.services.instance.DredgionService;
import com.aionemu.gameserver.services.instance.IdgelDomeService;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.instance.IronWallWarFrontService;
import com.aionemu.gameserver.services.instance.KamarBattlefieldService;
import com.aionemu.gameserver.services.instance.LivePartyConcertHall;
import com.aionemu.gameserver.services.instance.OphidanBridgeService;
import com.aionemu.gameserver.services.player.FatigueService;
import com.aionemu.gameserver.services.player.PlayerEventService;
import com.aionemu.gameserver.services.player.PlayerLimitService;
import com.aionemu.gameserver.services.reward.OnlineBonus;
import com.aionemu.gameserver.services.reward.RewardService;
import com.aionemu.gameserver.services.transfers.PlayerTransferService;
import com.aionemu.gameserver.spawnengine.InstanceRiftSpawnManager;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.spawnengine.TemporarySpawnEngine;
import com.aionemu.gameserver.taskmanager.fromdb.TaskFromDBManager;
import com.aionemu.gameserver.taskmanager.tasks.PacketBroadcaster;
import com.aionemu.gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import com.aionemu.gameserver.utils.AEVersions;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.ThreadUncaughtExceptionHandler;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.utils.ZCXInfo;
import com.aionemu.gameserver.utils.chathandlers.ChatProcessor;
import com.aionemu.gameserver.utils.cron.ThreadPoolManagerRunnableRunner;
import com.aionemu.gameserver.utils.gametime.DateTimeUtil;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.utils.i18n.LanguageHandler;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.utils.javaagent.JavaAgentUtils;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.geo.nav.NavService;
import com.aionemu.gameserver.world.zone.ZoneService;

import ch.lambdaj.Lambda;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

/**
 * <tt>GameServer </tt> is the main class of the application and represents the whole game server.<br>
 * This class is also an entry point with main() method.
 *
 * @author -Nemesiss-
 * @author SoulKeeper
 * @author cura
 * @author GiGatR00n v4.7.5.x
 * @modified Yon (Aion Reconstruction Project) -- Changed console section output to be less ugly/spammy,
 * added NavService initialization, added PlayerMoveTaskManager call, added a call to {@link AIMechanics#initialize()}.
 */
public class GameServer {
	
	private static final Logger log = LoggerFactory.getLogger(GameServer.class);
	
	private static Set<StartupHook> startUpHooks = new HashSet<StartupHook>();
	
	public synchronized static void addStartupHook(StartupHook hook) {
		if (startUpHooks != null) {
			startUpHooks.add(hook);
		} else {
			hook.onStartup();
		}
	}
	
	private synchronized static void onStartup() {
		final Set<StartupHook> startupHooks = startUpHooks;
		
		startUpHooks = null;
		
		for (StartupHook hook : startupHooks) {
			hook.onStartup();
		}
	}
	
	public interface StartupHook {
		
		public void onStartup();
	}
	
	/**
	 * Starts servers for connection with aion client and login\chat server.
	 */
	private void startServers() {
		
		Util.printSection("NETWORK");
		NioServer nioServer = new NioServer(NetworkConfig.NIO_READ_WRITE_THREADS,
				new ServerCfg(NetworkConfig.GAME_BIND_ADDRESS, NetworkConfig.GAME_PORT, "Game Connections", new GameConnectionFactoryImpl()));
		
		LoginServer ls = LoginServer.getInstance();
		ChatServer cs = ChatServer.getInstance();
		
		ls.setNioServer(nioServer);
		cs.setNioServer(nioServer);
		
		// Nio must go first
		nioServer.connect();
		ls.connect();
		
		if (GSConfig.ENABLE_CHAT_SERVER) {
			cs.connect();
		}
	}
	
	/**
	 * Initialize all helper services, that are not directly related to aion gs, which includes:
	 * <ul>
	 * <li>Logging</li>
	 * <li>Database factory</li>
	 * <li>Thread pool</li>
	 * </ul>
	 * This method also initializes {@link Config}
	 */
	private static void initUtilityServicesAndConfig() {
		// Set default uncaught exception handler
		Thread.setDefaultUncaughtExceptionHandler(new ThreadUncaughtExceptionHandler());
		
		// make sure that callback code was initialized
		if (JavaAgentUtils.isConfigured()) {
			log.info("JavaAgent [Callback Support] is configured.");
		}
		
		Util.printSection("CRON");
		// Initialize cron service
		CronService.initSingleton(ThreadPoolManagerRunnableRunner.class);
		
		Util.printSection("CONFIG");
		// init config
		Config.load();
		// DateTime zone override from configs
		DateTimeUtil.init();
		// Second should be database factory
		Util.printSection("DATABASE");
		DatabaseFactory.init();
		// Initialize DAOs
		DAOManager.init();
		// Initialize thread pools
		Util.printSection("THREADS");
		ThreadConfig.load();
		ThreadPoolManager.getInstance();
	}
	
	/**
	 * Launching method for GameServer
	 *
	 * @param args arguments, not used
	 */
	public static void main(String[] args) {
		
		long startTime = System.currentTimeMillis();
		Lambda.enableJitting(true);
		final GameEngine[] parallelEngines = new GameEngine[] {QuestEngine.getInstance(), InstanceEngine.getInstance(), AI2Engine.getInstance(), ChatProcessor.getInstance()};
		final CountDownLatch progressLatch = new CountDownLatch(parallelEngines.length);
		initalizeLoggger();
		initUtilityServicesAndConfig();
		(new ServerCommandProcessor()).start();
		Util.printSection("DATA");
		IDFactory.getInstance();
		AIMechanics.initialize();
		DataManager.getInstance();
		Util.printSection("WORLD");
		ZoneService.getInstance().load(null);
		GeoService.getInstance().initializeGeo();
		System.gc();
		NavService.getInstance().initializeNav();
		System.gc();
		DropRegistrationService.getInstance();
		World.getInstance();
		
		//Pre-fire this bad boy; TODO: does this avoid movement lock on login? It doesn't, but it seems far more rare!
		PlayerMoveTaskManager.getInstance();
		
		Util.printSection("CLEANING");
		GameServer gs = new GameServer();
		DAOManager.getDAO(PlayerDAO.class).setPlayersOffline(false);
		BannedMacManager.getInstance();
		DatabaseCleaningService.getInstance();
		Util.printSection("ENGINES");
		for (int i = 0; i < parallelEngines.length; i++) {
			final int index = i;
			ThreadPoolManager.getInstance().execute(new Runnable() {
				
				@Override
				public void run() {
					parallelEngines[index].load(progressLatch);
				}
			});
		}
		
		try {
			progressLatch.await();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		// This is loading only siege location data
		// No Siege schedule or spawns
		Util.printSection("LOCATIONS");
		BaseService.getInstance().initBaseLocations();
		BeritraService.getInstance().initBeritraLocations();
		SiegeService.getInstance().initSiegeLocations();
		VortexService.getInstance().initVortexLocations();
		RiftService.getInstance().initRiftLocations();
		Util.printSection("SPAWNS");
		SpawnEngine.spawnAll();
		RiftService.getInstance().initRifts();
		InstanceRiftSpawnManager.spawnAll();
		TemporarySpawnEngine.spawnAll();
		if (SiegeConfig.SIEGE_ENABLED) {
			ShieldService.getInstance().spawnAll();
		}
		Util.printSection("SIEGES");
		// Init Sieges... It's separated due to spawn engine.
		// It should not spawn siege NPCs
		SiegeService.getInstance().initSieges();
		BaseService.getInstance().initBases();
		SerialKillerService.getInstance().initSerialKillers();
		DisputeLandService.getInstance().init();
		Util.printSection("TASKS");
		PacketBroadcaster.getInstance();
		PeriodicSaveService.getInstance();
		AbyssRankUpdateService.getInstance().scheduleUpdate();
		TaskFromDBManager.getInstance();
		Util.printSection("SERVICES");
		LimitedItemTradeService.getInstance().start();
		if (CustomConfig.LIMITS_ENABLED) {
			PlayerLimitService.getInstance().scheduleUpdate();
		}
		GameTimeManager.startClock();
		GameTimeService.getInstance();
		Influence.getInstance();
		ExchangeService.getInstance();
		FatigueService.getInstance();
		BrokerService.getInstance();
		AnnouncementService.getInstance();
		DebugService.getInstance();
		WeatherService.getInstance();
		LoginEventService.getInstance().start();
		// PetitionService.getInstance();
		if (AIConfig.SHOUTS_ENABLE) {
			NpcShoutsService.getInstance();
		}
		InstanceService.load();
		HTMLCache.getInstance();
		CuringZoneService.getInstance();
		LanguageHandler.getInstance();
		FlyRingService.getInstance();
		RoadService.getInstance();
		
		if (EventsConfig.EVENT_ENABLED) PlayerEventService.getInstance();
		if (CustomConfig.ENABLE_REWARD_SERVICE) RewardService.getInstance();
		if (EventsConfig.ENABLE_EVENT_SERVICE) EventService.getInstance().start();
		if (WeddingsConfig.WEDDINGS_ENABLE) WeddingService.getInstance();
		
		if (AutoGroupConfig.AUTO_GROUP_ENABLE) {
			if (AutoGroupConfig.IDGELDOME_ENABLE) IdgelDomeService.getInstance().start();
			if (AutoGroupConfig.OPHIDAN_ENABLE) OphidanBridgeService.getInstance().start();
			if (AutoGroupConfig.IRONWALL_ENABLE) IronWallWarFrontService.getInstance().start();
			if (AutoGroupConfig.DREDGION2_ENABLE) DredgionService.getInstance().start();
			if (AutoGroupConfig.KAMAR_ENABLE) KamarBattlefieldService.getInstance().start();
		}
		
		if (CustomConfig.FFA_ENABLE) {
			FFaService.getInstance().announceTask(15);
		}
		if (EventSystem.ENABLE_ONEVONE) {
			OneVsOneService.getInstance().autoAnnounce(15);
		}
		if (EventSystem.ENABLE_CRAZY) {
			CrazyDaevaService.getInstance().startTimer();
		}
		if (EventSystem.BATTLEGROUNDS_ENABLED) {
			BattleGroundManager.initialize();
		}
		if (EventsConfig.LIVE_PARTY_ENABLE) {
			LivePartyConcertHall.getInstance().init();
		}
		AdminService.getInstance();
		PlayerTransferService.getInstance();
		Util.printSection("HOUSING");
		HousingBidService.getInstance().start();
		ChallengeTaskService.getInstance();
		TownService.getInstance();
		MaintenanceTask.getInstance();
		Util.printSection("CUSTOMS");
		LookManager.getInstance().onStart();
		SupportService.getInstance();
		WordFilterService.getInstance();
		if (MembershipConfig.ONLINE_BONUS_ENABLE) {
			OnlineBonus.getInstance();
		}
		
		RestartService.getInstance();
		WebshopService.getInstance();
		Util.printSection("SYSTEM");
		AEVersions.printFullVersionInfo();
		System.gc();
		AEInfos.printAllInfos();
		
		Util.printSection("GameServerLog");
		
		long endTime = System.currentTimeMillis();
		log.info("AC GameServer started in " + (endTime - startTime) / 1000 + " seconds.");
		gs.startServers();
		
		Runtime.getRuntime().addShutdownHook(ShutdownHook.getInstance());
		
		ZCXInfo.checkForRatioLimitation();
		onStartup();
		
		/**
		 * Schedules Garbage Collector to be launched at the specified time to be optimized unused memory. (Avoids OutOfMemoryException)
		 * (Except that all this really does is make the server lock for however long a full GC cycle takes;
		 * GC in Java is automatic, even System.gc() straight up says the method doesn't have to be obeyed)
		 */
		if (GSConfig.ENABLE_MEMORY_GC) {
			GarbageCollector.getInstance().start();
		}
	}
	
	private static void initalizeLoggger() {
		new File("./log/backup/").mkdirs();
		File[] files = new File("log").listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".log");
			}
		});
		
		if (files != null && files.length > 0) {
			byte[] buf = new byte[1024];
			try {
				String outFilename = "./log/backup/" + new SimpleDateFormat("yyyy-MM-dd HHmmss").format(new Date()) + ".zip";
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
				out.setMethod(ZipOutputStream.DEFLATED);
				out.setLevel(Deflater.BEST_COMPRESSION);
				
				for (File logFile : files) {
					FileInputStream in = new FileInputStream(logFile);
					out.putNextEntry(new ZipEntry(logFile.getName()));
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.closeEntry();
					in.close();
					logFile.delete();
				}
				out.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		try {
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc);
			lc.reset();
			configurator.doConfigure("config/slf4j-logback.xml");
			
		} catch (JoranException je) {
			throw new RuntimeException("Failed to configure loggers, shutting down...", je);
		}
	}
}
