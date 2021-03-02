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
package com.aionemu.gameserver.services.player;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.team2.alliance.PlayerAllianceService;
import com.aionemu.gameserver.model.team2.common.legacy.GroupEvent;
import com.aionemu.gameserver.model.team2.common.legacy.PlayerAllianceEvent;
import com.aionemu.gameserver.model.team2.group.PlayerGroupService;
import com.aionemu.gameserver.model.templates.item.ItemUseLimits;
import com.aionemu.gameserver.model.vortex.VortexLocation;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_TARGET_SELECTED;
import com.aionemu.gameserver.services.SerialKillerService;
import com.aionemu.gameserver.services.VortexService;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldMap;
import com.aionemu.gameserver.world.WorldPosition;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author Jego, xTz
 */
public class PlayerReviveService {
	
	public static final void duelRevive(Player player) {
		revive(player, 30, 30, false, 0);
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		player.getGameStats().updateStatsAndSpeedVisually();
		player.unsetResPosState();
	}
	
	public static final void skillRevive(Player player) {
		if (!(player.getResStatus())) {
			cancelRes(player);
			return;
		}
		revive(player, 10, 10, true, player.getResurrectionSkill());
		if (player.getIsFlyingBeforeDeath()) {
			player.setState(CreatureState.FLYING);
		}
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		// if player was flying before res, start flying
		if (player.getIsFlyingBeforeDeath()) {
			player.getFlyController().startFly();
		}
		player.getGameStats().updateStatsAndSpeedVisually();
		
		if (player.isInPrison()) {
			TeleportService2.teleportToPrison(player);
		}
		
		if (player.isInResPostState()) {
			TeleportService2.teleportTo(player, player.getWorldId(), player.getInstanceId(), player.getResPosX(), player.getResPosY(), player.getResPosZ());
		}
		player.unsetResPosState();
		// unset isflyingbeforedeath
		player.setIsFlyingBeforeDeath(false);
	}
	
	public static final void rebirthRevive(Player player) {
		if (!player.canUseRebirthRevive()) {
			return;
		}
		if (player.getRebirthResurrectPercent() <= 0) {
			PacketSendUtility.sendMessage(player, "Error: Rebirth effect missing percent.");
			player.setRebirthResurrectPercent(5);
		}
		boolean soulSickness = true;
		int rebirthResurrectPercent = player.getRebirthResurrectPercent();
		if (player.getAccessLevel() >= AdminConfig.ADMIN_AUTO_RES) {
			rebirthResurrectPercent = 100;
			soulSickness = false;
		}
		
		revive(player, rebirthResurrectPercent, rebirthResurrectPercent, soulSickness, player.getRebirthSkill());
		if (player.getIsFlyingBeforeDeath()) {
			player.setState(CreatureState.FLYING);
		}
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		if (player.getIsFlyingBeforeDeath()) {
			player.getFlyController().startFly();
		}
		player.getGameStats().updateStatsAndSpeedVisually();
		
		if (player.isInPrison()) {
			TeleportService2.teleportToPrison(player);
		}
		player.unsetResPosState();
		
		// if player was flying before res, start flying
		// unset isflyingbeforedeath
		player.setIsFlyingBeforeDeath(false);
	}
	
	public static final void battlegroundRevive(Player player) {
		revive(player, 100, 100, true, 0);
		
		if (player.battlegroundFlag != null) {
			player.battlegroundFlag.getController().dropped = true;
			player.battlegroundFlag.setFlagHolder(null);
			player.getBattleGround().broadcastToBattleGround(player.getCommonData().getName() + (player.getLegion() != null ? " of " + player.getLegion().getLegionName() : "") + " has dropped the "
					+ (player.battlegroundFlag.getRace() == Race.ELYOS ? "Elyos" : "Asmodian") + " flag !", null);
			player.battlegroundFlag = null;
			if (player.getController().getTask(TaskId.BATTLEGROUND_CARRY_FLAG) != null) {
				player.getController().getTask(TaskId.BATTLEGROUND_CARRY_FLAG).cancel(true);
				player.getController().addTask(TaskId.BATTLEGROUND_CARRY_FLAG, null);
			}
		}
		
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		
		if (player.getBattleGround() != null) {
			player.getBattleGround().teleportPlayer(player);
		} else {
			if (player.getCommonData().getRace() == Race.ELYOS) {
				TeleportService2.teleportTo(player, 110010000, 1374f, 1399f, 573f, (byte) 0);
			} else {
				TeleportService2.teleportTo(player, 120010000, 1324f, 1550f, 210f, (byte) 0);
			}
		}
		player.getEffectController().removeAllEffects();
	}
	
	public static final void bindRevive(Player player) {
		bindRevive(player, 0);
	}
	
	public static final void bindRevive(Player player, int skillId) {
		revive(player, 25, 25, true, skillId);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		// TODO: It is not always necessary.
		// sendPacket(new SM_QUEST_LIST(activePlayer));
		player.getGameStats().updateStatsAndSpeedVisually();
		if (player.isInPrison()) {
			TeleportService2.teleportToPrison(player);
		} else {
			boolean isInviadeActiveVortex = false;
			for (VortexLocation loc : VortexService.getInstance().getVortexLocations().values()) {
				isInviadeActiveVortex = loc.isInsideActiveVotrex(player) && player.getRace().equals(loc.getInvadersRace());
				if (isInviadeActiveVortex) {
					TeleportService2.teleportTo(player, loc.getResurrectionPoint());
				}
			}
			
			if (!isInviadeActiveVortex) {
				TeleportService2.moveToBindLocation(player, true);
			}
		}
		player.unsetResPosState();
	}
	
	public static final void kiskRevive(Player player) {
		kiskRevive(player, 0);
	}
	
	public static final void kiskRevive(Player player, int skillId) {
		// TODO: find right place for this
		if (player.getSKInfo().getRank() > 1) {
			if (SerialKillerService.getInstance().isEnemyWorld(player)) {
				bindRevive(player);
				return;
			}
		}
		
		Kisk kisk = player.getKisk();
		if (kisk == null) {
			return;
		}
		if (player.isInPrison()) {
			TeleportService2.teleportToPrison(player);
		} else if (kisk.isActive()) {
			WorldPosition bind = kisk.getPosition();
			kisk.resurrectionUsed();
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
			revive(player, 25, 25, false, skillId);
			player.getGameStats().updateStatsAndSpeedVisually();
			player.unsetResPosState();
			TeleportService2.moveToKiskLocation(player, bind);
		}
	}
	
	public static final void instanceRevive(Player player) {
		instanceRevive(player, 0);
	}
	
	public static final void instanceRevive(Player player, int skillId) {
		// Revive in Instances
		if (player.getPosition().getWorldMapInstance().getInstanceHandler().onReviveEvent(player)) {
			return;
		}
		WorldMap map = World.getInstance().getWorldMap(player.getWorldId());
		if (map == null) {
			bindRevive(player);
			return;
		}
		revive(player, 25, 25, true, skillId);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		player.getGameStats().updateStatsAndSpeedVisually();
		PacketSendUtility.sendPacket(player, new SM_PLAYER_INFO(player, false));
		PacketSendUtility.sendPacket(player, new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
		if (map.isInstanceType() && (player.getInstanceStartPosX() != 0 && player.getInstanceStartPosY() != 0 && player.getInstanceStartPosZ() != 0)) {
			TeleportService2.teleportTo(player, player.getWorldId(), player.getInstanceStartPosX(), player.getInstanceStartPosY(), player.getInstanceStartPosZ());
		} else {
			bindRevive(player);
		}
		player.unsetResPosState();
	}
	
	public static final void revive(final Player player, int hpPercent, int mpPercent, boolean setSoulsickness, int resurrectionSkill) {
		player.getKnownList().doOnAllPlayers(new Visitor<Player>() {
			
			@Override
			public void visit(Player visitor) {
				VisibleObject target = visitor.getTarget();
				// if (target != null && target.getObjectId() ==
				// player.getObjectId()) {
				if (target != null && target.getObjectId() == player.getObjectId() && (visitor.getRace() != player.getRace())) {
					visitor.setTarget(null);
					PacketSendUtility.sendPacket(visitor, new SM_TARGET_SELECTED(null));
				}
			}
		});
		boolean isNoResurrectPenalty = player.getController().isNoResurrectPenaltyInEffect();
		player.getMoveController().stopFalling();
		player.setPlayerResActivate(false);
		player.getLifeStats().setCurrentHpPercent(isNoResurrectPenalty ? 100 : hpPercent);
		player.getLifeStats().setCurrentMpPercent(isNoResurrectPenalty ? 100 : mpPercent);
		if (player.getCommonData().getDp() > 0 && !isNoResurrectPenalty) {
			player.getCommonData().setDp(0);
		}
		player.getLifeStats().triggerRestoreOnRevive();
		if (!isNoResurrectPenalty && setSoulsickness) {
			player.getController().updateSoulSickness(resurrectionSkill);
		}
		if (player.getResurrectionSkill() > 0) {
			player.setResurrectionSkill(0);
		}
		player.getAggroList().clear();
		player.getController().onBeforeSpawn(false);
		if (player.isInGroup2()) {
			PlayerGroupService.updateGroup(player, GroupEvent.MOVEMENT);
		}
		if (player.isInAlliance2()) {
			PlayerAllianceService.updateAlliance(player, PlayerAllianceEvent.MOVEMENT);
		}
	}
	
	public static final void itemSelfRevive(Player player) {
		Item item = player.getSelfRezStone();
		if (item == null) {
			cancelRes(player);
			return;
		}
		
		// Add Cooldown and use item
		ItemUseLimits useLimits = item.getItemTemplate().getUseLimits();
		int useDelay = useLimits.getDelayTime();
		player.addItemCoolDown(useLimits.getDelayId(), System.currentTimeMillis() + useDelay, useDelay / 1000);
		player.getController().cancelUseItem();
		PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), item.getObjectId(), item.getItemTemplate().getTemplateId()), true);
		if (!player.getInventory().decreaseByObjectId(item.getObjectId(), 1)) {
			cancelRes(player);
			return;
		}
		// Tombstone Self-Rez retail verified 15%
		revive(player, 15, 15, true, player.getResurrectionSkill());
		// if player was flying before res, start flying
		if (player.getIsFlyingBeforeDeath()) {
			player.setState(CreatureState.FLYING);
		}
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.RESURRECT), true);
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_REBIRTH_MASSAGE_ME);
		if (player.getIsFlyingBeforeDeath()) {
			player.getFlyController().startFly();
		}
		player.getGameStats().updateStatsAndSpeedVisually();
		
		if (player.isInPrison()) {
			TeleportService2.teleportToPrison(player);
		}
		player.unsetResPosState();
		// unset isflyingbeforedeath
		player.setIsFlyingBeforeDeath(false);
		
	}
	
	private static final void cancelRes(Player player) {
		AuditLogger.info(player, "Possible selfres hack.");
		player.getController().sendDie();
	}
}
