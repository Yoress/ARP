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
package com.aionemu.gameserver.services.abyss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.commons.database.dao.DAOManager;
import com.aionemu.commons.services.CronService;
import com.aionemu.gameserver.configs.main.RankingConfig;
import com.aionemu.gameserver.dao.AbyssRankDAO;
import com.aionemu.gameserver.dao.ServerVariablesDAO;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.stats.AbyssRankEnum;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author ATracer
 * @author ThunderBolt - GloryPoints
 * @reworked Yon (Aion Reconstruction Project) -- Reworked entire update process,
 * {@link #updateGpRankTo(AbyssRankEnum, int)} changed to {@link #updateGpRankTo(AbyssRankEnum, int, int)}
 * and modified to update top_ranking in the database.
 */
public class AbyssRankUpdateService {
	
	private static final Logger log = LoggerFactory.getLogger(AbyssRankUpdateService.class);
	
	private int rankCount = 0;
	
	private AbyssRankUpdateService() {}
	
	public static AbyssRankUpdateService getInstance() {
		return SingletonHolder.instance;
	}
	
	public void scheduleUpdate() {
		ServerVariablesDAO dao = DAOManager.getDAO(ServerVariablesDAO.class);
		int nextTime = dao.load("abyssRankUpdate");
		if (nextTime < System.currentTimeMillis() / 1000) {
			performUpdate();
		}
		
		log.info("Starting ranking update task based on cron expression: " + RankingConfig.TOP_RANKING_UPDATE_RULE);
		CronService.getInstance().schedule(new Runnable() {
			
			@Override
			public void run() {
				performUpdate();
			}
		}, RankingConfig.TOP_RANKING_UPDATE_RULE, true);
	}
	
	/**
	 * Perform update of all ranks
	 */
	public void performUpdate() {
		log.info("AbyssRankUpdateService: executing rank update");
		long startTime = System.currentTimeMillis();
		
		World.getInstance().doOnAllPlayers(new Visitor<Player>() {
			
			@Override
			public void visit(Player player) {
				player.getAbyssRank().doUpdate();
				DAOManager.getDAO(AbyssRankDAO.class).storeAbyssRank(player);
			}
		});
		
		updateAllRanks();
//		updateLimitedRanks();
//		updateLimitedGpRanks();
		AbyssRankingCache.getInstance().reloadRankings();
		log.info("AbyssRankUpdateService: execution time: " + (System.currentTimeMillis() - startTime) / 1000);
	}
	
	/**
	 * Updates all player abyss ranks, the client will be notified if needed. Player ranks are only updated once.
	 * This method respects the quota of each rank.
	 * <p>
	 * The new rank is based on the player's GP, and then AP. This method updates for all players,
	 * even if they do not have enough GP to have a GP rank.
	 * <p>
	 * The general process to do this is as follows:<br>
	 * Collect all Players with GP more than 1-Star requirement from database.<br>
	 * Collect all Players with AP more than Rank-9 requirement (more or less every player) from database.<br>
	 * Update Ranks for GP Players, remove that player from the AP list if they are present.<br>
	 * Update Ranks for AP Players (this list will include any GP Ranked Players that did not update).
	 */
	private void updateAllRanks() {
		//Order matters, must be highest rank to lowest.
		final AbyssRankEnum[] gpRanks = {AbyssRankEnum.SUPREME_COMMANDER, AbyssRankEnum.COMMANDER, AbyssRankEnum.GREAT_GENERAL,
		                                 AbyssRankEnum.GENERAL, AbyssRankEnum.STAR5_OFFICER, AbyssRankEnum.STAR4_OFFICER,
		                                 AbyssRankEnum.STAR3_OFFICER, AbyssRankEnum.STAR2_OFFICER, AbyssRankEnum.STAR1_OFFICER};
		
		//Order is less important here, but should be kept consistent with above.
		final AbyssRankEnum[] apRanks = {AbyssRankEnum.GRADE1_SOLDIER, AbyssRankEnum.GRADE2_SOLDIER, AbyssRankEnum.GRADE3_SOLDIER,
		                                 AbyssRankEnum.GRADE4_SOLDIER, AbyssRankEnum.GRADE5_SOLDIER, AbyssRankEnum.GRADE6_SOLDIER,
		                                 AbyssRankEnum.GRADE7_SOLDIER, AbyssRankEnum.GRADE8_SOLDIER, AbyssRankEnum.GRADE9_SOLDIER};
		
		final int activeAfterDays = RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS;
		final int gpLimit = AbyssRankEnum.STAR1_OFFICER.getRequired();
		final int apLimit = AbyssRankEnum.GRADE9_SOLDIER.getRequired();
		final Race[] racesToUpdate = {Race.ASMODIANS, Race.ELYOS};
		
		for (Race race: racesToUpdate) {
			//GP Ranks List
			Map<Integer, Integer> playerGpMap = DAOManager.getDAO(AbyssRankDAO.class).loadPlayersGp(race, gpLimit, activeAfterDays);
			List<Entry<Integer, Integer>> playerGpEntries = new ArrayList<Entry<Integer, Integer>>(playerGpMap.entrySet());
			Collections.sort(playerGpEntries, new PlayerGpComparator<Integer, Integer>());
			
			//AP Ranks List
			Map<Integer, Integer> playerApMap = DAOManager.getDAO(AbyssRankDAO.class).loadPlayersAp(race, apLimit, activeAfterDays);
			List<Entry<Integer, Integer>> playerApEntries = new ArrayList<Entry<Integer, Integer>>(playerApMap.entrySet());
			Collections.sort(playerApEntries, new PlayerApComparator<Integer, Integer>());
			
			ArrayList<Integer> updatedPlayers = new ArrayList<Integer>();
			
			rankCount = 1;
			for (AbyssRankEnum gpRank: gpRanks) {
				gpRankUpdate(gpRank, playerGpEntries, updatedPlayers);
			}
			for (AbyssRankEnum apRank: apRanks) {
				apRankUpdate(apRank, playerApEntries, updatedPlayers);
			}
		}
	}
	
	private void gpRankUpdate(AbyssRankEnum rank, List<Entry<Integer, Integer>> playerGpEntries, List<Integer> updatedPlayers) {
		int quota = (rank.getId() > 9 && rank.getId() < 18) ? rank.getQuota() - AbyssRankEnum.getRankById(rank.getId() + 1).getQuota() : rank.getQuota();
		for (int i = 0; i < quota; i++) {
			if (playerGpEntries.isEmpty()) {
				return;
			}
			// check next player in list
			Entry<Integer, Integer> playerGp = playerGpEntries.get(0);
			// check if there are some players left in map
			if (playerGp == null) {
				return;
			}
			int playerId = playerGp.getKey();
			int gp = playerGp.getValue();
			// check if this (and the rest) player has required gp count
			if (gp < rank.getRequired()) {
				return;
			}
			// remove player and update its rankGp
			playerGpEntries.remove(0);
			updatedPlayers.add(playerId);
			updateGpRankTo(rank, playerId, rankCount++);
		}
	}
	
	private void apRankUpdate(AbyssRankEnum rank, List<Entry<Integer, Integer>> playerApEntries, List<Integer> updatedPlayers) {
		int quota = rank.getId() > 9 ? (rank.getQuota() - AbyssRankEnum.getRankById(rank.getId() + 1).getQuota()) : rank.getQuota();
		for (int i = 0; i < ((quota == 0) ? (playerApEntries.size()) : (quota)); i++) {
			if (playerApEntries.isEmpty()) {
				return;
			}
			// check next player in list
			Entry<Integer, Integer> playerAp = playerApEntries.get(0);
			// check if there are some players left in map
			if (playerAp == null) {
				return;
			}
			int playerId = playerAp.getKey();
			
			//Check if player has already been updated.
			if (updatedPlayers.contains(playerId)) {
				playerApEntries.remove(0);
				continue;
			}
			
			int ap = playerAp.getValue();
			// check if this (and the rest) player has required ap count
			if (ap < rank.getRequired()) {
				return;
			}
			// remove player and update its rank
			playerApEntries.remove(0);
			updateRankTo(rank, playerId);
		}
	}
	/* Old code modified by Yon (Aion Reconstruction Project), and then reworked above. I've left it here as a nod to previous developers.
	/**
	 * Update player ranks based on quota for all players (online/offline)
	 *
	private void updateLimitedRanks() {
		updateAllRanksForRace(Race.ASMODIANS, AbyssRankEnum.GRADE9_SOLDIER.getRequired(), RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS);
		updateAllRanksForRace(Race.ELYOS, AbyssRankEnum.GRADE9_SOLDIER.getRequired(), RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS);
	}
	
	private void updateLimitedGpRanks() {
		updateAllRanksGpForRace(Race.ASMODIANS, AbyssRankEnum.STAR1_OFFICER.getRequired(), RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS);
		updateAllRanksGpForRace(Race.ELYOS, AbyssRankEnum.STAR1_OFFICER.getRequired(), RankingConfig.TOP_RANKING_MAX_OFFLINE_DAYS);
	}
	
	private void updateAllRanksForRace(Race race, int apLimit, int activeAfterDays) {
		Map<Integer, Integer> playerApMap = DAOManager.getDAO(AbyssRankDAO.class).loadPlayersAp(race, apLimit, activeAfterDays);
		List<Entry<Integer, Integer>> playerApEntries = new ArrayList<Entry<Integer, Integer>>(playerApMap.entrySet());
		Collections.sort(playerApEntries, new PlayerApComparator<Integer, Integer>());
		
		selectRank(AbyssRankEnum.GRADE1_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE2_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE3_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE4_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE5_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE6_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE7_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE8_SOLDIER, playerApEntries);
		selectRank(AbyssRankEnum.GRADE9_SOLDIER, playerApEntries);
		
//		updateToNoQuotaRank(playerApEntries);
	}
	
	private void updateAllRanksGpForRace(Race race, int gpLimit, int activeAfterDays) {
		Map<Integer, Integer> playerGpMap = DAOManager.getDAO(AbyssRankDAO.class).loadPlayersGp(race, gpLimit, activeAfterDays);
		List<Entry<Integer, Integer>> playerGpEntries = new ArrayList<Entry<Integer, Integer>>(playerGpMap.entrySet());
		Collections.sort(playerGpEntries, new PlayerGpComparator<Integer, Integer>());
		
		rankCount = 1;
		selectGpRank(AbyssRankEnum.SUPREME_COMMANDER, playerGpEntries);
		selectGpRank(AbyssRankEnum.COMMANDER, playerGpEntries);
		selectGpRank(AbyssRankEnum.GREAT_GENERAL, playerGpEntries);
		selectGpRank(AbyssRankEnum.GENERAL, playerGpEntries);
		selectGpRank(AbyssRankEnum.STAR5_OFFICER, playerGpEntries);
		selectGpRank(AbyssRankEnum.STAR4_OFFICER, playerGpEntries);
		selectGpRank(AbyssRankEnum.STAR3_OFFICER, playerGpEntries);
		selectGpRank(AbyssRankEnum.STAR2_OFFICER, playerGpEntries);
		selectGpRank(AbyssRankEnum.STAR1_OFFICER, playerGpEntries);
		
//		updateToNoQuotaGpRank(playerGpEntries);
	}
	
	private void selectRank(AbyssRankEnum rank, List<Entry<Integer, Integer>> playerApEntries) {
		int quota = rank.getId() > 9 ? (rank.getQuota() - AbyssRankEnum.getRankById(rank.getId() + 1).getQuota()) : rank.getQuota();
		for (int i = 0; i < ((quota == 0) ? (playerApEntries.size()) : (quota)); i++) {
			if (playerApEntries.isEmpty()) {
				return;
			}
			// check next player in list
			Entry<Integer, Integer> playerAp = playerApEntries.get(0);
			// check if there are some players left in map
			if (playerAp == null) {
				return;
			}
			int playerId = playerAp.getKey();
			int ap = playerAp.getValue();
			// check if this (and the rest) player has required ap count
			if (ap < rank.getRequired()) {
				return;
			}
			// remove player and update its rank
			playerApEntries.remove(0);
			updateRankTo(rank, playerId);
		}
	}
	
	private void selectGpRank(AbyssRankEnum rank, List<Entry<Integer, Integer>> playerGpEntries) {
		int quota = (rank.getId() > 9 && rank.getId() < 18) ? rank.getQuota() - AbyssRankEnum.getRankById(rank.getId() + 1).getQuota() : rank.getQuota();
		for (int i = 0; i < quota; i++) {
			if (playerGpEntries.isEmpty()) {
				return;
			}
			// check next player in list
			Entry<Integer, Integer> playerGp = playerGpEntries.get(0);
			// check if there are some players left in map
			if (playerGp == null) {
				return;
			}
			int playerId = playerGp.getKey();
			int gp = playerGp.getValue();
			// check if this (and the rest) player has required gp count
			if (gp < rank.getRequired()) {
				return;
			}
			// remove player and update its rankGp
			playerGpEntries.remove(0);
			updateGpRankTo(rank, playerId, rankCount++);
		}
	}
	
	private void updateToNoQuotaRank(List<Entry<Integer, Integer>> playerApEntries) {
		for (Entry<Integer, Integer> playerApEntry : playerApEntries) {
			updateRankTo(AbyssRankEnum.GRADE1_SOLDIER, playerApEntry.getKey());
		}
	}
	
	private void updateToNoQuotaGpRank(List<Entry<Integer, Integer>> playerGpEntries) {
		for (Entry<Integer, Integer> playerGpEntry : playerGpEntries) {
			updateGpRankTo(AbyssRankEnum.SUPREME_COMMANDER, playerGpEntry.getKey());
		}
	}
	*/
	
	protected void updateRankTo(AbyssRankEnum newRank, int playerId) {
		// check if rank is changed for online players
		Player onlinePlayer = World.getInstance().findPlayer(playerId);
		if (onlinePlayer != null) {
			AbyssRank abyssRank = onlinePlayer.getAbyssRank();
			AbyssRankEnum currentRank = abyssRank.getRank();
			if (currentRank != newRank) {
				abyssRank.setRank(newRank);
				AbyssPointsService.checkRankChanged(onlinePlayer, currentRank, newRank);
			}
		} else {
			DAOManager.getDAO(AbyssRankDAO.class).updateAbyssRank(playerId, newRank);
		}
	}
	
	protected void updateGpRankTo(AbyssRankEnum newRank, int playerId, int topRanking) {
		// check if rankGp is changed for online players
		Player onlinePlayer = World.getInstance().findPlayer(playerId);
		if (onlinePlayer != null) {
			AbyssRank abyssRank = onlinePlayer.getAbyssRank();
			AbyssRankEnum currentRank = abyssRank.getRank();
			if (currentRank != newRank) {
				abyssRank.setRank(newRank);
				abyssRank.setTopRanking(topRanking);
				AbyssPointsService.checkRankGpChanged(onlinePlayer, currentRank, newRank);
			} else {
				abyssRank.setTopRanking(topRanking);
			}
		} /*else*/ { //do the following code always so AbyssRankingCache is accurate
			DAOManager.getDAO(AbyssRankDAO.class).updateAbyssRank(playerId, newRank);
			DAOManager.getDAO(AbyssRankDAO.class).updateTopRanking(playerId, topRanking);
		}
	}
	
	private static class SingletonHolder {
		
		protected static final AbyssRankUpdateService instance = new AbyssRankUpdateService();
	}
	
	private static class PlayerApComparator<K, V extends Comparable<V>> implements Comparator<Entry<K, V>> {
		
		@Override
		public int compare(Entry<K, V> o1, Entry<K, V> o2) {
			return -o1.getValue().compareTo(o2.getValue()); // descending order
		}
	}
	
	private static class PlayerGpComparator<K, V extends Comparable<V>> implements Comparator<Entry<K, V>> {
		
		@Override
		public int compare(Entry<K, V> o1, Entry<K, V> o2) {
			return -o1.getValue().compareTo(o2.getValue()); // descending order
		}
	}
}
