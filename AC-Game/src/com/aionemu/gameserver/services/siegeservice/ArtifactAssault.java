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
package com.aionemu.gameserver.services.siegeservice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.geoEngine.math.Vector2f;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.siege.SiegeModType;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;

/**
 * This class handles Balaur attacks on artifact locations.
 * <p>
 * If an artifact does not belong to the Balaur, an assault force suitable to that artifact will
 * spawn in after a specified delay.
 * <p>
 * If the map region is inactive, then the Balaur will simply take over instead of spawning.
 * 
 * @author Luzien
 * @author Yon (Aion Reconstruction Project)
 */
public class ArtifactAssault extends Assault<ArtifactSiege> {
	/**
	 * Tracks if {@link Assault#spawnTask spawnTask} has run or not.
	 */
	private boolean spawned = false;
	
	/**
	 * Direct access to the underlying siege this assault is meant for.
	 */
	private ArtifactSiege siege;
	
	/**
	 * A task that will check if the Balaur assault force was slain.
	 */
	private Future<?> captureCheck;
	
	/**
	 * A {@link List} containing the mobs spawned in to assault the artifact location.
	 * <p>
	 * This relies on the {@link List#clear()} method.
	 */
	private List<Npc> assaultForce;
	
	/**
	 * A reference to the siege logger for logging information. Check {@link LoggingConfig#LOG_SIEGE}
	 * before writing non-errors to the log.
	 */
	private static final Logger LOG = LoggerFactory.getLogger("SIEGE_LOG");
	
	public ArtifactAssault(ArtifactSiege siege) {
		super(siege);
		this.siege = siege;
	}
	
	@Override
	public void scheduleAssault(int delay) {
		spawnTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (SiegeConfig.BALAUR_AUTO_ASSAULT && !spawned) {
					if (boss != null) {
						if (boss.getPosition().getMapRegion().isMapRegionActive()) {
							logInfo("[SIEGE] > [ARTIFACT:" + locationId + "] Balaur Assault on Active Map Region.");
							spawnAttackers();
						} else {
							logInfo("[SIEGE] > [ARTIFACT:" + locationId + "] Balaur Assault on Inactive Map Region.");
							//Capture the Artifact right away if the map region is inactive
							siege.forceCapture(SiegeRace.BALAUR);
						}
					} else {
						LOG.error("[SIEGE] > [ARTIFACT:" + locationId + "] Boss is null!");
					}
				}
			}
		}, delay * 1000, delay * 1000);
	}
	
	/**
	 * Handles the selection and spawning of mobs based on the location of the artifact.
	 */
	private void spawnAttackers() {
		if (spawned) return;
		spawned = true;
		assaultForce = new ArrayList<Npc>();
		float x = boss.getX(), y = boss.getY(), z = boss.getZ();
		int angle1 = Rnd.get(0, 360);
		int angle2 = Rnd.get(0, 360);
		double radAngle1 = Math.toRadians(angle1);
		double radAngle2 = Math.toRadians(angle2);
		Vector2f dis1 = new Vector2f((float) Math.cos(radAngle1), (float) Math.sin(radAngle1));
		Vector2f dis2 = new Vector2f((float) Math.cos(radAngle2), (float) Math.sin(radAngle2));
		dis1.multLocal(1.5F);
		dis2.multLocal(1.5F);
		float dx1 = dis1.x, dx2 = dis2.x, dy1 = dis1.y, dy2 = dis2.y;
		byte heading1 = MathUtil.convertDegreeToHeading(MathUtil.calculateAngleFrom(x + dx1, y + dy1, x, y)),
			 heading2 = MathUtil.convertDegreeToHeading(MathUtil.calculateAngleFrom(x + dx2, y + dy2, x, y));
		switch (locationId) {
		case 1012: case 1013: case 1014: case 1015: case 1016: case 1017: case 1018: case 1019: case 1020:
			//Artifacts in The Eye of Reshanta, near Divine Fortress
			addAssaultSpawn(277034, x + dx1, y + dy1, z, heading1);
			addAssaultSpawn(277034, x + dx2, y + dy2, z, heading2);
			break;
		case 1133: case 1134: case 1135: case 1142: case 1143: case 1144: case 1145: case 1146:
			//Artifacts in The Lower Abyss
			addAssaultSpawn(276767, x + dx1, y + dy1, z, heading1);
			addAssaultSpawn(276767, x + dx2, y + dy2, z, heading2);
			break;
		case 1212: case 1213: case 1214: case 1215: case 1222: case 1223: case 1224: case 1232: case 1233:
		case 1242: case 1243: case 1252: case 1253: case 1254: case 1401: case 1402: case 1403:
			//Artifacts in The Upper Abyss
			addAssaultSpawn(277037, x + dx1, y + dy1, z, heading1);
			addAssaultSpawn(277037, x + dx2, y + dy2, z, heading2);
			break;
		case 2012: case 2013: case 2014: case 2022: case 2023: case 2024: case 3012: case 3013: case 3014:
		case 3022: case 3023: case 3024:
			//Artifacts in Inggison or Gelkmaros
			addAssaultSpawn(258259, x + dx1, y + dy1, z, heading1);
			addAssaultSpawn(258259, x + dx2, y + dy2, z, heading2);
			break;
		case 4012: case 4013: case 4022: case 4023: case 4032: case 4033: case 4042: case 4043: case 4051:
		case 4052: case 4053:
			//TODO: Artifacts in Tiamaranta
			LOG.error("[SIEGE] > [Artifact:" + locationId + "] Active Balaur Assault on Tiamaranta artifacts is not yet implemented.");
			break;
		case 7012: case 7013: case 7014:
			//TODO: Artifacts in Kaldor
			LOG.error("[SIEGE] > [Artifact:" + locationId + "] Active Balaur Assault on Kaldor artifacts is not yet implemented.");
			break;
		default:
			LOG.error("[SIEGE] > [Artifact:" + locationId + "] Unrecognized Artifact Location for Balaur Assault!");
			break;
		}
		if (assaultForce.isEmpty()) {
			siege.forceCapture(SiegeRace.BALAUR);
			logInfo("[SIEGE] > [Artifact:" + locationId + "] Forced Balaur capture in active map region.");
			return;
		}
		if (captureCheck != null && !captureCheck.isDone()) {
			captureCheck.cancel(false);
		}
		captureCheck = ThreadPoolManager.getInstance().schedule(new Runnable() {
			@Override
			public void run() {
				for (Npc v: assaultForce) {
					if (!v.getLifeStats().isAlreadyDead()) {
						siege.forceCapture(SiegeRace.BALAUR);
						return;
					}
				}
				assaultForce.clear();
				spawned = false;
				logInfo("[SIEGE] > [Artifact:" + locationId + "] The balaur assault force was defeated.");
			}
		}, 120 * 1000); //Two minute delay
	}

	@Override
	public void onAssaultFinish(boolean captured) {
		if (captureCheck != null && !captureCheck.isDone()) {
			captureCheck.cancel(false);
		}
		if (assaultForce != null) assaultForce.clear();
		spawned = false; //May not be needed.
	}
	
	/**
	 * A helper method used for readability of the source code. It uses the {@link SpawnEngine} to add a new
	 * {@link SpawnEngine#addNewSiegeSpawn(int, int, int, SiegeRace, SiegeModType, float, float, float, byte) siege spawn}
	 * of the given NPC id, location, and heading. The new spawn template is then
	 * {@link SpawnEngine#spawnObject(com.aionemu.gameserver.model.templates.spawns.SpawnTemplate, int) spawned},
	 * and added to {@link #assaultForce}.
	 * <p>
	 * if {@link #assaultForce} is null, this method simply returns.
	 * 
	 * @param id -- The NPC ID to spawn.
	 * @param x -- The X coordinate to spawn the NPC at.
	 * @param y -- The Y coordinate to spawn the NPC at.
	 * @param z -- The Z coordinate to spawn the NPC at.
	 * @param heading -- The Heading to spawn the NPC facing.
	 */
	private void addAssaultSpawn(int id, float x, float y, float z, byte heading) {
		if (assaultForce == null) return;
		assaultForce.add((Npc) SpawnEngine.spawnObject(
				SpawnEngine.addNewSiegeSpawn(
					getWorldId(), id, locationId, SiegeRace.BALAUR, SiegeModType.ASSAULT, x, y, z, heading
				), 0));
	}
	
	/**
	 * {@link Logger#info(String) Logs} the given String with the {@link Logger}
	 * for this class if logging is {@link LoggingConfig#LOG_SIEGE enabled}.
	 * 
	 * @param message -- The message to log
	 */
	private static void logInfo(String message) {
		if (LoggingConfig.LOG_SIEGE) {
			LOG.info(message);
		}
	}
}
