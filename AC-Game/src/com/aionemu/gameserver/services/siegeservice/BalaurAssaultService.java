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

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.configs.main.LoggingConfig;
import com.aionemu.gameserver.configs.main.SiegeConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.assemblednpc.AssembledNpc;
import com.aionemu.gameserver.model.assemblednpc.AssembledNpcPart;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.ArtifactLocation;
import com.aionemu.gameserver.model.siege.FortressLocation;
import com.aionemu.gameserver.model.siege.Influence;
import com.aionemu.gameserver.model.siege.SiegeRace;
import com.aionemu.gameserver.model.templates.assemblednpc.AssembledNpcTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_ASSEMBLER;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.World;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author synchro2
 * @reworked Luzien TODO: Send Peace Dredgion without assault TO-DO: Artifact Siege
 * @modified Yon (Aion Reconstruction Project) -- Artifact Sieges added.
 */
public class BalaurAssaultService {
	
	private static final BalaurAssaultService instance = new BalaurAssaultService();
	private Logger log = LoggerFactory.getLogger("SIEGE_LOG");
	private final Map<Integer, FortressAssault> fortressAssaults = new FastMap<Integer, FortressAssault>().shared();
	private final Map<Integer, ArtifactAssault> artifactAssaults = new FastMap<Integer, ArtifactAssault>().shared();
	
	public static BalaurAssaultService getInstance() {
		return instance;
	}
	
	public void onSiegeStart(final Siege<?> siege) {
		boolean fortSiege = false;
		if (siege instanceof FortressSiege) {
			if (!calculateFortressAssault(((FortressSiege) siege).getSiegeLocation())) {
				return;
			}
			fortSiege = true;
		} else if (siege instanceof ArtifactSiege) {
			if (!calculateArtifactAssault(((ArtifactSiege) siege).getSiegeLocation())) {
				return;
			}
		} else {
			return;
		}
		if (fortSiege) {
			newAssault(siege, Rnd.get(1, 600));
		} else {
			//Artifact siege after 1 hour.
			newAssault(siege, 3600);
		}
		if (LoggingConfig.LOG_SIEGE) {
			log.info("[SIEGE] Balaur Assault scheduled on Siege ID: " + siege.getSiegeLocationId() + "!");
		}
	}
	
	public void onSiegeFinish(Siege<?> siege) {
		int locId = siege.getSiegeLocationId();
		if (fortressAssaults.containsKey(locId)) {
			Boolean bossIsKilled = siege.isBossKilled();
			fortressAssaults.get(locId).finishAssault(bossIsKilled);
			if (LoggingConfig.LOG_SIEGE) {
				if (bossIsKilled && siege.getSiegeLocation().getRace().equals(SiegeRace.BALAUR)) {
					log.info("[SIEGE] > [FORTRESS:" + siege.getSiegeLocationId() + "] has been captured by Balaur Assault!");
				} else {
					log.info("[SIEGE] > [FORTRESS:" + siege.getSiegeLocationId() + "] Balaur Assault finished without capture!");
				}
			}
			fortressAssaults.remove(locId);
		}
		if (artifactAssaults.containsKey(locId)) {
			//Artifact changed hands, cancel/finish old assault
			artifactAssaults.get(locId).finishAssault(true);
			if (LoggingConfig.LOG_SIEGE) {
				if (siege.isBossKilled() && siege.getSiegeCounter().getWinnerRaceCounter().getSiegeRace().equals(SiegeRace.BALAUR)) {
					//The artifact hasn't run onFinish() on its siege so the race hasn't changed, yet.
					//Since the artifact will be changing to Balaur, Log the assault success.
					log.info("[SIEGE] > [ARTIFACT:" + siege.getSiegeLocationId() + "] has been captured by Balaur Assault!");
				} else {
					log.info("[SIEGE] > [ARTIFACT:" + siege.getSiegeLocationId() + "] Balaur Assault finished without capture!");
				}
			}
			artifactAssaults.remove(locId);
		}
	}
	
	private boolean calculateFortressAssault(FortressLocation fortress) {
		boolean isBalaurea = fortress.getWorldId() != 400010000;
		int locationId = fortress.getLocationId();
		
		if (fortressAssaults.containsKey(locationId)) {
			return false;
		}
		
		if (!calcFortressInfluence(isBalaurea, fortress)) {
			return false;
		}
		
		int count = 0; // Allow only 2 Balaur attacks per map, 1 per Balaurea map
		for (FortressAssault fa : fortressAssaults.values()) {
			if (fa.getWorldId() == fortress.getWorldId()) {
				count++;
			}
		}
		
		if (count >= (isBalaurea ? 1 : 2)) {
			return false;
		}
		
		return true;
	}
	
	private boolean calculateArtifactAssault(ArtifactLocation artifact) {
		if (artifactAssaults.containsKey(artifact.getLocationId())) {
			return false;
		}
		if (artifact.isStandAlone() && !artifact.getRace().equals(SiegeRace.BALAUR)) return true;
		return false;
	}
	
	public void startAssault(Player player, int location, int delay) {
		if (fortressAssaults.containsKey(location) || artifactAssaults.containsKey(location)) {
			PacketSendUtility.sendMessage(player, "Assault on " + location + " was already started");
			return;
		}
		
		newAssault(SiegeService.getInstance().getSiege(location), delay);
	}
	
	private void newAssault(Siege<?> siege, int delay) {
		if (siege instanceof FortressSiege) {
			FortressAssault assault = new FortressAssault((FortressSiege) siege);
			assault.startAssault(delay);
			fortressAssaults.put(siege.getSiegeLocationId(), assault);
		} else if (siege instanceof ArtifactSiege) {
			ArtifactAssault assault = new ArtifactAssault((ArtifactSiege) siege);
			assault.startAssault(delay);
			artifactAssaults.put(siege.getSiegeLocationId(), assault);
		}
	}
	
	private boolean calcFortressInfluence(boolean isBalaurea, FortressLocation fortress) {
		SiegeRace locationRace = fortress.getRace();
		float influence;
		
		if (locationRace.equals(SiegeRace.BALAUR) || !fortress.isVulnerable()) {
			return false;
		}
		
		int ownedForts = 0;
		if (isBalaurea) {
			for (FortressLocation fl : SiegeService.getInstance().getFortresses().values()) {
				if (fl.getWorldId() != 400010000 && !fortressAssaults.containsKey(fl.getLocationId()) && fl.getRace().equals(locationRace)) {
					ownedForts++;
				}
			}
			influence = ownedForts >= 2 ? 0.25f : 0.1f;
		} else {
			influence = locationRace.equals(SiegeRace.ASMODIANS) ? Influence.getInstance().getGlobalAsmodiansInfluence() : Influence.getInstance().getGlobalElyosInfluence();
		}
		
		return Rnd.get() < influence * SiegeConfig.BALAUR_ASSAULT_RATE;
	}
	
	public void spawnDredgion(int spawnId) {
		AssembledNpcTemplate template = DataManager.ASSEMBLED_NPC_DATA.getAssembledNpcTemplate(spawnId);
		FastList<AssembledNpcPart> assembledPatrs = new FastList<AssembledNpcPart>();
		for (AssembledNpcTemplate.AssembledNpcPartTemplate npcPart : template.getAssembledNpcPartTemplates()) {
			assembledPatrs.add(new AssembledNpcPart(IDFactory.getInstance().nextId(), npcPart));
		}
		
		AssembledNpc npc = new AssembledNpc(template.getRouteId(), template.getMapId(), template.getLiveTime(), assembledPatrs);
		Iterator<Player> iter = World.getInstance().getPlayersIterator();
		Player findedPlayer;
		while (iter.hasNext()) {
			findedPlayer = iter.next();
			PacketSendUtility.sendPacket(findedPlayer, new SM_NPC_ASSEMBLER(npc));
			PacketSendUtility.sendPacket(findedPlayer, SM_SYSTEM_MESSAGE.STR_ABYSS_CARRIER_SPAWN);
		}
	}
}
