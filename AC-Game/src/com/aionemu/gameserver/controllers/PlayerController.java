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
package com.aionemu.gameserver.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Future;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.manager.LookManager;
import com.aionemu.gameserver.configs.main.CustomConfig;
import com.aionemu.gameserver.configs.main.EventSystem;
import com.aionemu.gameserver.configs.main.HTMLConfig;
import com.aionemu.gameserver.configs.main.MembershipConfig;
import com.aionemu.gameserver.configs.main.SecurityConfig;
import com.aionemu.gameserver.controllers.attack.AttackStatus;
import com.aionemu.gameserver.controllers.attack.AttackUtil;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.dataholders.PlayerInitialData;
import com.aionemu.gameserver.eventEngine.battleground.model.templates.BattleGroundTemplate;
import com.aionemu.gameserver.eventEngine.battleground.services.battleground.AssaultBattleGround;
import com.aionemu.gameserver.eventEngine.battleground.services.battleground.BattleGroundManager;
import com.aionemu.gameserver.eventEngine.battleground.services.battleground.CTFBattleGround;
import com.aionemu.gameserver.eventEngine.crazy_daeva.CrazyDaevaService;
import com.aionemu.gameserver.model.DescriptionId;
import com.aionemu.gameserver.model.EmotionType;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.actions.PlayerMode;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Gatherable;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Kisk;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Pet;
import com.aionemu.gameserver.model.gameobjects.StaticObject;
import com.aionemu.gameserver.model.gameobjects.Summon;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.AbyssRank;
import com.aionemu.gameserver.model.gameobjects.player.BindPointPosition;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.gameobjects.state.CreatureVisualState;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.model.stats.container.PlayerGameStats;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.summons.UnsummonType;
import com.aionemu.gameserver.model.team2.group.PlayerFilters.ExcludePlayerFilter;
import com.aionemu.gameserver.model.templates.flypath.FlyPathEntry;
import com.aionemu.gameserver.model.templates.panels.SkillPanel;
import com.aionemu.gameserver.model.templates.quest.QuestItems;
import com.aionemu.gameserver.model.templates.stats.PlayerStatsTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.LOG;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ATTACK_STATUS.TYPE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DELETE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_EMOTION_SWITCH;
import com.aionemu.gameserver.network.aion.serverpackets.SM_GATHERABLE_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_HEADING_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_ITEM_USAGE_ANIMATION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_KISK_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_LEVEL_UPDATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_MOTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NEARBY_QUESTS;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PACKAGE_INFO_NOTIFY;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STANCE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PRIVATE_STORE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_CANCEL;
import com.aionemu.gameserver.network.aion.serverpackets.SM_STATS_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.restrictions.RestrictionsManager;
import com.aionemu.gameserver.services.ClassChangeService;
import com.aionemu.gameserver.services.DuelService;
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.services.LegionService;
import com.aionemu.gameserver.services.PvpService;
import com.aionemu.gameserver.services.QuestService;
import com.aionemu.gameserver.services.SerialKillerService;
import com.aionemu.gameserver.services.SiegeService;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.abyss.AbyssService;
import com.aionemu.gameserver.services.craft.CraftSkillUpdateService;
import com.aionemu.gameserver.services.ecfunctions.oneVsone.ArenaMasterService;
import com.aionemu.gameserver.services.ecfunctions.oneVsone.OneVsOneStruct;
import com.aionemu.gameserver.services.instance.InstanceService;
import com.aionemu.gameserver.services.item.ItemService;
import com.aionemu.gameserver.services.summons.SummonsService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.HealType;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.model.Skill.SkillMethod;
import com.aionemu.gameserver.skillengine.model.SkillTargetSlot;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.taskmanager.tasks.PlayerMoveTaskManager;
import com.aionemu.gameserver.taskmanager.tasks.TeamEffectUpdater;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.audit.AuditLogger;
import com.aionemu.gameserver.world.MapRegion;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.WorldType;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.zone.ZoneInstance;
import com.aionemu.gameserver.world.zone.ZoneName;

/**
 * This class is for controlling players.
 *
 * @author -Nemesiss-, ATracer, xavier, Sarynth, RotO, xTz, KID, Sippolo
 * @modified Yon (Aion Reconstruction Project) -- {@link #onEnterZone(ZoneInstance)} modified to check for
 * {@link CustomConfig#ENABLE_RIDE_RESTRICTION} so players aren't dismounted when mounts are configured to be allowable everywhere,
 * {@link #validateLoginZone()} no longer returns players to bindpoint in no recall zones until a better solution is found.
 */
public class PlayerController extends CreatureController<Player> {
	
	private Logger log = LoggerFactory.getLogger(PlayerController.class);
	private boolean isInShutdownProgress;
	private long lastAttackMilis = 0;
	private long lastAttackedMilis = 0;
	private int stance = 0;
	@SuppressWarnings("unused")
	private Listener mListener;
	protected BattleGroundTemplate template;
	
	@Override
	public void see(VisibleObject object) {
		super.see(object);
		if (object instanceof Player) {
			Player player = (Player) object;
			PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_INFO(player, getOwner().isAggroIconTo(player)));
			PacketSendUtility.sendPacket(getOwner(), new SM_MOTION(player.getObjectId(), player.getMotions().getActiveMotions()));
			if (player.isInPlayerMode(PlayerMode.RIDE)) {
				PacketSendUtility.sendPacket(getOwner(), new SM_EMOTION(player, EmotionType.RIDE, 0, player.ride.getNpcId()));
			}
			if (player.getPet() != null) {
				LoggerFactory.getLogger(PlayerController.class).debug("Player " + getOwner().getName() + " sees " + object.getName() + " that has toypet");
				PacketSendUtility.sendPacket(getOwner(), new SM_PET(3, player.getPet()));
			}
			player.getEffectController().sendEffectIconsTo(getOwner());
		} else if (object instanceof Kisk) {
			Kisk kisk = ((Kisk) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(kisk, getOwner()));
			if (getOwner().getRace() == kisk.getOwnerRace()) {
				PacketSendUtility.sendPacket(getOwner(), new SM_KISK_UPDATE(kisk));
			}
		} else if (object instanceof Npc) {
			Npc npc = ((Npc) object);
			LookManager.corrigateHeading(npc, this.getOwner());
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc, getOwner()));
			PacketSendUtility.sendPacket(getOwner(), new SM_EMOTION_SWITCH(npc, npc.getState(), EmotionType.SELECT_TARGET));
			PacketSendUtility.sendPacket(getOwner(), new SM_HEADING_UPDATE(object.getObjectId(), (byte) object.getHeading()));
			if (!npc.getEffectController().isEmpty()) {
				npc.getEffectController().sendEffectIconsTo(getOwner());
			}
			QuestEngine.getInstance().onAtDistance(new QuestEnv(object, getOwner(), 0, 0));
		} else if (object instanceof Summon) {
			Summon npc = ((Summon) object);
			PacketSendUtility.sendPacket(getOwner(), new SM_NPC_INFO(npc, getOwner()));
			if (!npc.getEffectController().isEmpty()) {
				npc.getEffectController().sendEffectIconsTo(getOwner());
			}
		} else if (object instanceof Gatherable || object instanceof StaticObject) {
			PacketSendUtility.sendPacket(getOwner(), new SM_GATHERABLE_INFO(object));
		} else if (object instanceof Pet) {
			PacketSendUtility.sendPacket(getOwner(), new SM_PET(3, (Pet) object));
		}
	}
	
	@Override
	public void notSee(VisibleObject object, boolean isOutOfRange) {
		super.notSee(object, isOutOfRange);
		if (object instanceof Pet) {
			PacketSendUtility.sendPacket(getOwner(), new SM_PET(4, (Pet) object));
		} else {
			PacketSendUtility.sendPacket(getOwner(), new SM_DELETE(object, isOutOfRange ? 0 : 15));
		}
	}
	
	public void updateNearbyQuests() {
		HashMap<Integer, Integer> nearbyQuestList = new HashMap<Integer, Integer>();
		for (int questId : getOwner().getPosition().getMapRegion().getParent().getQuestIds()) {
			int diff = 0;
			if (questId <= 0xFFFF) {
				diff = QuestService.getLevelRequirementDiff(questId, getOwner().getCommonData().getLevel());
			}
			if (diff <= 2 && QuestService.checkStartConditions(new QuestEnv(null, getOwner(), questId, 0), false)) {
				nearbyQuestList.put(questId, diff);
			}
		}
		PacketSendUtility.sendPacket(getOwner(), new SM_NEARBY_QUESTS(nearbyQuestList));
	}
	
	@Override
	public void onEnterZone(ZoneInstance zone) {
		Player player = getOwner();
		if (!zone.canRide() && player.isInPlayerMode(PlayerMode.RIDE) && CustomConfig.ENABLE_RIDE_RESTRICTION) {
			player.unsetPlayerMode(PlayerMode.RIDE);
		}
		InstanceService.onEnterZone(player, zone);
		if (zone.getAreaTemplate().getZoneName() == null) {
			log.error("No name found for a Zone in the map " + zone.getAreaTemplate().getWorldId());
		} else {
			QuestEngine.getInstance().onEnterZone(new QuestEnv(null, player, 0, 0), zone.getAreaTemplate().getZoneName());
		}
	}
	
	@Override
	public void onLeaveZone(ZoneInstance zone) {
		Player player = getOwner();
		InstanceService.onLeaveZone(player, zone);
		ZoneName zoneName = zone.getAreaTemplate().getZoneName();
		if (zoneName == null) {
			log.warn("No name for zone template in " + zone.getAreaTemplate().getWorldId());
			return;
		}
		QuestEngine.getInstance().onLeaveZone(new QuestEnv(null, player, 0, 0), zoneName);
	}
	
	/**
	 * {@inheritDoc} Should only be triggered from one place (life stats)
	 */
	// TODO [AT] move
	public void onEnterWorld() {
		
		InstanceService.onEnterInstance(getOwner());
		if (getOwner().getPosition().getWorldMapInstance().getParent().isExceptBuff()) {
			getOwner().getEffectController().removeAllEffects();
		}
		
		if (getOwner().getBattleGround() != null && getOwner().getWorldId() != getOwner().getBattleGround().getWorldId()) {
			BattleGroundManager.unregisterPlayer(getOwner());
			getOwner().battlegroundWaiting = false;
			getOwner().setBattleGround(null);
		}
		
		for (Effect ef : getOwner().getEffectController().getAbnormalEffects()) {
			if (ef.isDeityAvatar()) {
				// remove abyss transformation if worldtype != abyss && worldtype != balaurea
				if (getOwner().getWorldType() != WorldType.ABYSS && getOwner().getWorldType() != WorldType.BALAUREA || getOwner().isInInstance() || getOwner().getWorldId() == 600040000) {
					ef.endEffect();
					getOwner().getEffectController().clearEffect(ef);
				}
			} else if (ef.getSkillTemplate().getDispelCategory() == DispelCategoryType.NPC_BUFF) {
				ef.endEffect();
				getOwner().getEffectController().clearEffect(ef);
			}
		}
	}
	
	// TODO [AT] move
	public void onLeaveWorld() {
		SerialKillerService.getInstance().onLeaveMap(getOwner());
		ArenaMasterService.getInstance().onLeaveInstance(getOwner());
		InstanceService.onLeaveInstance(getOwner());
	}
	
	public void validateLoginZone() {
		int mapId;
		float x, y, z;
		byte h;
		boolean moveToBind = false;
		
		BindPointPosition bind = getOwner().getBindPoint();
		
		if (bind != null) {
			mapId = bind.getMapId();
			x = bind.getX();
			y = bind.getY();
			z = bind.getZ();
			h = bind.getHeading();
		} else {
			PlayerInitialData.LocationData start = DataManager.PLAYER_INITIAL_DATA.getSpawnLocation(getOwner().getRace());
			
			mapId = start.getMapId();
			x = start.getX();
			y = start.getY();
			z = start.getZ();
			h = start.getHeading();
		}
		if (!SiegeService.getInstance().validateLoginZone(getOwner())) {
			moveToBind = true;
		}
		//TODO: Disabled until something better comes along.
		/* else {
			long lastOnline = getOwner().getCommonData().getLastOnline().getTime();
			long secondsOffline = (System.currentTimeMillis() / 1000) - lastOnline / 1000;
			if (secondsOffline > 10 * 60) { //FIX-ME: These zones are incorrect; is this condition even retail-like?
				//Logout in no-recall zone sends you to bindpoint after 10 (??) minutes
				for (ZoneInstance zone : getOwner().getPosition().getMapRegion().getZones(getOwner())) {
					if (!zone.canRecall()) {
						moveToBind = true;
						break;
					}
				}
			}
		}*/
		
		if (moveToBind) {
			World.getInstance().setPosition(getOwner(), mapId, x, y, z, h);
		}
	}
	
	public void onDie(@Nonnull Creature lastAttacker, boolean showPacket) {
		Player player = this.getOwner();
		player.getController().cancelCurrentSkill();
		player.setRebirthRevive(getOwner().haveSelfRezEffect());
		showPacket = player.hasResurrectBase() ? false : showPacket;
		Creature master = lastAttacker.getMaster();
		
		// crazy Daeva
		if (EventSystem.ENABLE_CRAZY) {
			if (((master instanceof Player)) && (master.getRace() != player.getRace())) {
				CrazyDaevaService.getInstance().crazyOnDie(player, (Player) master, true);
			}
		}
		
		// Battelground
		if (master instanceof Player) {
			if (((Player) master).getBattleGround() != null && ((Player) master).getBattleGround() instanceof AssaultBattleGround) {
				((AssaultBattleGround) (((Player) master).getBattleGround())).onKillPlayer(player, (Player) master);
			}
		}
		// 1 vs 1 Event
		if (EventSystem.ENABLE_ONEVONE) {
			if (((master instanceof Player)) && ((Player) master).isInPkMode()) {
				for (int worldId : OneVsOneStruct.worldid) {
					if (master.getWorldId() == worldId) {
						ArenaMasterService.getInstance().onDie((Player) master, player);
					}
				}
			}
		}
		
		// High ranked kill announce
		AbyssRank ar = player.getAbyssRank();
		if (AbyssService.isOnPvpMap(player) && ar != null) {
			if (ar.getRank().getId() >= 10) {
				AbyssService.rankedKillAnnounce(player);
			}
		}
		
		if (DuelService.getInstance().isDueling(player.getObjectId())) {
			if (master != null && DuelService.getInstance().isDueling(player.getObjectId(), master.getObjectId())) {
				DuelService.getInstance().loseDuel(player);
				player.getEffectController().removeAbnormalEffectsByTargetSlot(SkillTargetSlot.DEBUFF);
				player.getLifeStats().setCurrentHpPercent(33);
				player.getLifeStats().setCurrentMpPercent(33);
				return;
			}
			DuelService.getInstance().loseDuel(player);
		}
		
		/**
		 * Release summon
		 */
		Summon summon = player.getSummon();
		if (summon != null) {
			SummonsService.doMode(SummonMode.RELEASE, summon, UnsummonType.UNSPECIFIED);
		}
		
		// setIsFlyingBeforeDead for PlayerReviveService
		if (player.isInState(CreatureState.FLYING)) {
			player.setIsFlyingBeforeDeath(true);
		}
		
		// ride
		player.setPlayerMode(PlayerMode.RIDE, null);
		player.unsetState(CreatureState.RESTING);
		player.unsetState(CreatureState.FLOATING_CORPSE);
		
		// unsetflying
		player.unsetState(CreatureState.FLYING);
		player.unsetState(CreatureState.GLIDING);
		player.setFlyState(0);
		
		if (player.isInInstance()) {
			if (player.getPosition().getWorldMapInstance().getInstanceHandler().onDie(player, lastAttacker)) {
				super.onDie(lastAttacker);
				return;
			}
		}
		
		MapRegion mapRegion = player.getPosition().getMapRegion();
		if (mapRegion != null && mapRegion.onDie(lastAttacker, getOwner())) {
			return;
		}
		
		this.doReward();
		
		if (master instanceof Npc || master == player) {
			if (player.getLevel() > 4 && !isNoDeathPenaltyInEffect()) player.getCommonData().calculateExpLoss();
		}
		// Effects removed with super.onDie()
		super.onDie(lastAttacker);
		
		// test it if bug (maybe not needed)
		if (player.getBattleGround() != null && player.getBattleGround() instanceof CTFBattleGround && master instanceof Player) {
			int tplId = player.getBattleGround().getTplId();
			template = DataManager.BATTLEGROUND_DATA.getBattleGroundTemplate(tplId);
			player.getBattleGround().increasePoints((Player) master, template.getRules().getKillPlayer());
		}
		if (player.battlegroundFlag != null) {
			player.getBattleGround().broadcastToBattleGround(player.getCommonData().getName() + (player.getLegion() != null ? " de " + player.getLegion().getLegionName() : "") + " got the "
					+ (player.battlegroundFlag.getRace() == Race.ELYOS ? "Elyséen" : "Asmodien") + " !", null);
			if (master instanceof Player) {
				player.getBattleGround().increasePoints((Player) master, template.getRules().getFlagBase());
			}
			
			player.battlegroundFlag.getController().dropped = true;
			player.battlegroundFlag.setFlagHolder(null);
			player.battlegroundFlag = null;
			if (player.getController().getTask(TaskId.BATTLEGROUND_CARRY_FLAG) != null) {
				player.getController().getTask(TaskId.BATTLEGROUND_CARRY_FLAG).cancel(true);
				player.getController().addTask(TaskId.BATTLEGROUND_CARRY_FLAG, null);
			}
		}
		player.getActionList().clearAll();
		
		// send sm_emotion with DIE
		// have to be send after state is updated!
		sendDieFromCreature(lastAttacker, showPacket);
		
		QuestEngine.getInstance().onDie(new QuestEnv(null, player, 0, 0));
		
		if (player.isInGroup2()) {
			player.getPlayerGroup2().sendPacket(SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_FRIENDLY_DEATH(player.getName()), new ExcludePlayerFilter(player));
		}
	}
	
	@Override
	public void onDie(@Nonnull Creature lastAttacker) {
		this.onDie(lastAttacker, true);
	}
	
	public void sendDie() {
		sendDieFromCreature(getOwner(), true);
	}
	
	private void sendDieFromCreature(@Nonnull Creature lastAttacker, boolean showPacket) {
		Player player = this.getOwner();
		
		PacketSendUtility.broadcastPacket(player, new SM_EMOTION(player, EmotionType.DIE, 0, player.equals(lastAttacker) ? 0 : lastAttacker.getObjectId()), true);
		
		if (showPacket) {
			int kiskTimeRemaining = (player.getKisk() != null ? player.getKisk().getRemainingLifetime() : 0);
			PacketSendUtility.sendPacket(player, new SM_DIE(player.canUseRebirthRevive(), player.haveSelfRezItem(), kiskTimeRemaining, 0, isInvader(player)));
		}
		
		PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_COMBAT_MY_DEATH);
	}
	
	private boolean isInvader(Player player) {
		if (player.getRace().equals(Race.ASMODIANS)) {
			return player.getWorldId() == 210060000;
		} else {
			return player.getWorldId() == 220050000;
		}
	}
	
	@Override
	public void doReward() {
		PvpService.getInstance().doReward(getOwner());
	}
	
	@Override
	public void onBeforeSpawn() {
		this.onBeforeSpawn(true);
		/*
		 * super.onBeforeSpawn(); startProtectionActiveTask(); if (getOwner().getIsFlyingBeforeDeath()) {
		 * getOwner().unsetState(CreatureState.FLOATING_CORPSE); } else { getOwner().unsetState(CreatureState.DEAD); }
		 * getOwner().setState(CreatureState.ACTIVE);
		 */
	}
	
	public void onBeforeSpawn(boolean blink) {
		super.onBeforeSpawn();
		if (blink) {
			startProtectionActiveTask();
		}
		if (getOwner().getIsFlyingBeforeDeath()) {
			getOwner().unsetState(CreatureState.FLOATING_CORPSE);
		} else {
			getOwner().unsetState(CreatureState.DEAD);
		}
		getOwner().setState(CreatureState.ACTIVE);
	}
	
	@Override
	public void attackTarget(Creature target, int time) {
		
		PlayerGameStats gameStats = getOwner().getGameStats();
		
		if (!RestrictionsManager.canAttack(getOwner(), target)) {
			return;
		}
		
		// Normal attack is already limited client side (ex. Press C and attacker approaches target)
		// but need a check server side too also for Z axis issue
		if (!MathUtil.isInAttackRange(getOwner(), target, (float) (getOwner().getGameStats().getAttackRange().getCurrent() / 1000f) + 1)) {
			return;
		}
		
		if (!GeoService.getInstance().canSee(getOwner(), target)) {
			PacketSendUtility.sendPacket(getOwner(), SM_SYSTEM_MESSAGE.STR_ATTACK_OBSTACLE_EXIST);
			return;
		}
		
		if (target instanceof Npc) {
			QuestEngine.getInstance().onAttack(new QuestEnv(target, getOwner(), 0, 0));
		}
		
		int attackSpeed = gameStats.getAttackSpeed().getCurrent();
		
		long milis = System.currentTimeMillis();
		// network ping.. TODO: Ping is not a factor here (time has to be less than attack speed); investigate
		if (milis - lastAttackMilis + 300 < attackSpeed) {
			// hack
			return;
		}
		lastAttackMilis = milis;
		
		/**
		 * notify attack observers
		 */
		super.attackTarget(target, time);
		
	}
	
	@Override
	public void onAttack(Creature creature, int skillId, TYPE type, int damage, boolean notifyAttack, LOG log, AttackStatus status) {
		if (getOwner().getLifeStats().isAlreadyDead()) {
			return;
		}
		
		if (getOwner().isInvul() || getOwner().isProtectionActive()) {
			damage = 0;
		}
		
		if (getOwner().getBattleGround() != null && !getOwner().getBattleGround().running) {
			damage = 0;
		}
		
		if (getOwner().getActionList() != null) {
			getOwner().getActionList().addDamage(creature, damage);
		}
		
		cancelUseItem();
		cancelGathering();
		super.onAttack(creature, skillId, type, damage, notifyAttack, log, status);
		
		PacketSendUtility.broadcastPacket(getOwner(), new SM_ATTACK_STATUS(getOwner(), type, skillId, damage, log), true);
		
		if (creature instanceof Npc) {
			QuestEngine.getInstance().onAttack(new QuestEnv(creature, getOwner(), 0, 0));
		}
		
		lastAttackedMilis = System.currentTimeMillis();
	}
	
	/**
	 * @param skillId
	 * @param targetType
	 * @param x
	 * @param y
	 * @param z
	 */
	public void useSkill(int skillId, int targetType, float x, float y, float z, int time) {
		Player player = getOwner();
		
		Skill skill = SkillEngine.getInstance().getSkillFor(player, skillId, player.getTarget());
		
		if (skill != null) {
			if (!RestrictionsManager.canUseSkill(player, skill)) {
				return;
			}
			
			skill.setTargetType(targetType, x, y, z);
			skill.setHitTime(time);
			skill.useSkill();
		}
	}
	
	/**
	 * @param template
	 * @param targetType
	 * @param x
	 * @param y
	 * @param z
	 * @param clientHitTime
	 */
	public void useSkill(SkillTemplate template, int targetType, float x, float y, float z, int clientHitTime, int skillLevel) {
		Player player = getOwner();
		Skill skill = null;
		skill = SkillEngine.getInstance().getSkillFor(player, template, player.getTarget());
		if (skill == null && player.isTransformed()) {
			SkillPanel panel = DataManager.PANEL_SKILL_DATA.getSkillPanel(player.getTransformModel().getPanelId());
			if (panel != null && panel.canUseSkill(template.getSkillId(), skillLevel)) {
				skill = SkillEngine.getInstance().getSkillFor(player, template, player.getTarget(), skillLevel);
			}
		}
		
		if (skill != null) {
			if (!RestrictionsManager.canUseSkill(player, skill)) {
				return;
			}
			
			skill.setTargetType(targetType, x, y, z);
			skill.setHitTime(clientHitTime);
			skill.useSkill();
		}
	}
	
	@Override
	public void onMove() {
		getOwner().getObserveController().notifyMoveObservers();
		super.onMove();
	}
	
	@Override
	public void onStopMove() {
		PlayerMoveTaskManager.getInstance().removePlayer(getOwner());
		getOwner().getObserveController().notifyMoveObservers();
		getOwner().getMoveController().setInMove(false);
		cancelCurrentSkill();
		updateZone();
		super.onStopMove();
	}
	
	@Override
	public void onStartMove() {
		getOwner().getMoveController().setInMove(true);
		PlayerMoveTaskManager.getInstance().addPlayer(getOwner());
		cancelUseItem();
		cancelCurrentSkill();
		super.onStartMove();
	}
	
	@Override
	public void cancelCurrentSkill() {
		if (getOwner().getCastingSkill() == null) {
			return;
		}
		
		Player player = getOwner();
		Skill castingSkill = player.getCastingSkill();
		castingSkill.cancelCast();
		player.removeSkillCoolDown(castingSkill.getSkillTemplate().getCooldownId());
		player.setCasting(null);
		player.setNextSkillUse(0);
		if (castingSkill.getSkillMethod() == SkillMethod.CAST || castingSkill.getSkillMethod() == SkillMethod.CHARGE) {
			PacketSendUtility.broadcastPacket(player, new SM_SKILL_CANCEL(player, castingSkill.getSkillTemplate().getSkillId()), true);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_SKILL_CANCELED);
		} else if (castingSkill.getSkillMethod() == SkillMethod.ITEM) {
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_ITEM_CANCELED(new DescriptionId(castingSkill.getItemTemplate().getNameId())));
			player.removeItemCoolDown(castingSkill.getItemTemplate().getUseLimits().getDelayId());
			PacketSendUtility.broadcastPacket(player, new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), castingSkill.getFirstTarget().getObjectId(), castingSkill.getItemObjectId(),
					castingSkill.getItemTemplate().getTemplateId(), 0, 3, 0), true);
		}
	}
	
	@Override
	public void cancelUseItem() {
		Player player = getOwner();
		Item usingItem = player.getUsingItem();
		player.setUsingItem(null);
		if (hasTask(TaskId.ITEM_USE)) {
			cancelTask(TaskId.ITEM_USE);
			PacketSendUtility.broadcastPacket(player,
					new SM_ITEM_USAGE_ANIMATION(player.getObjectId(), usingItem == null ? 0 : usingItem.getObjectId(), usingItem == null ? 0 : usingItem.getItemTemplate().getTemplateId(), 0, 3, 0),
					true);
		}
	}
	
	public void cancelGathering() {
		Player player = getOwner();
		if (player.getTarget() instanceof Gatherable) {
			Gatherable g = (Gatherable) player.getTarget();
			g.getController().finishGathering(player);
		}
	}
	
	public void updatePassiveStats() {
		Player player = getOwner();
		for (PlayerSkillEntry skillEntry : player.getSkillList().getAllSkills()) {
			Skill skill = SkillEngine.getInstance().getSkillFor(player, skillEntry.getSkillId(), player.getTarget());
			if (skill != null && skill.isPassive()) {
				skill.useSkill();
			}
		}
	}
	
	@Override
	public Player getOwner() {
		return (Player) super.getOwner();
	}
	
	@Override
	public void onRestore(HealType healType, int value) {
		super.onRestore(healType, value);
		switch (healType) {
		case DP:
			getOwner().getCommonData().addDp(value);
			break;
		default:
			break;
		}
	}
	
	/**
	 * @param player
	 * @return
	 */
	// TODO [AT] move to Player
	public boolean isDueling(Player player) {
		return DuelService.getInstance().isDueling(player.getObjectId(), getOwner().getObjectId());
	}
	
	// TODO [AT] rename or remove
	public boolean isInShutdownProgress() {
		return isInShutdownProgress;
	}
	
	// TODO [AT] rename or remove
	public void setInShutdownProgress(boolean isInShutdownProgress) {
		this.isInShutdownProgress = isInShutdownProgress;
	}
	
	@Override
	public void onDialogSelect(int dialogId, Player player, int questId, int extendedRewardIndex) {
		switch (dialogId) {
		case 2:
			PacketSendUtility.sendPacket(player, new SM_PRIVATE_STORE(getOwner().getStore(), player));
			break;
		}
	}
	
	public void upgradePlayer() {
		Player player = getOwner();
		byte level = player.getLevel();
		
		PlayerStatsTemplate statsTemplate = DataManager.PLAYER_STATS_DATA.getTemplate(player);
		player.setPlayerStatsTemplate(statsTemplate);
		
		player.getLifeStats().synchronizeWithMaxStats();
		player.getLifeStats().updateCurrentStats();
		
		PacketSendUtility.broadcastPacket(player, new SM_LEVEL_UPDATE(player.getObjectId(), 0, level), true);
		
		// Guides Html on level up
		if (HTMLConfig.ENABLE_GUIDES) {
			HTMLService.sendGuideHtml(player);
		}
		
		// Temporal
		ClassChangeService.showClassChangeDialog(player);
		//TODO: D skill updates have to happen before this?
		QuestEngine.getInstance().onLvlUp(new QuestEnv(null, player, 0, 0));
		updateNearbyQuests();
		
		// add new skills
		SkillLearnService.addNewSkills(player);
		
		PacketSendUtility.broadcastPacket(player, new SM_PACKAGE_INFO_NOTIFY(0), true);
		
		player.getController().updatePassiveStats();
		
		// add recipe for morph
		if (level == 10) {
			CraftSkillUpdateService.getInstance().setMorphRecipe(player);
		}
		
		if (player.isInTeam()) {
			TeamEffectUpdater.getInstance().startTask(player);
		}
		if (player.isLegionMember()) {
			LegionService.getInstance().updateMemberInfo(player);
		}
		player.getNpcFactions().onLevelUp();
		PacketSendUtility.sendPacket(player, new SM_STATS_INFO(player));
	}
	
	/**
	 * After entering game player char is "blinking" which means that it's in under some protection, after making an action char stops blinking. -
	 * Starts protection active - Schedules task to end protection
	 */
	public void startProtectionActiveTask() {
		if (!getOwner().isProtectionActive()) {
			getOwner().setVisualState(CreatureVisualState.BLINKING);
			AttackUtil.cancelCastOn((Creature) getOwner());
			AttackUtil.removeTargetFrom((Creature) getOwner());
			PacketSendUtility.broadcastPacket(getOwner(), new SM_PLAYER_STATE(getOwner()), true);
			Future<?> task = ThreadPoolManager.getInstance().schedule(new Runnable() {
				
				@Override
				public void run() {
					stopProtectionActiveTask();
				}
			}, 60000);
			addTask(TaskId.PROTECTION_ACTIVE, task);
		}
	}
	
	/**
	 * Stops protection active task after first move or use skill
	 */
	public void stopProtectionActiveTask() {
		cancelTask(TaskId.PROTECTION_ACTIVE);
		Player player = getOwner();
		if (player != null && player.isSpawned()) {
			player.unsetVisualState(CreatureVisualState.BLINKING);
			PacketSendUtility.broadcastPacket(player, new SM_PLAYER_STATE(player), true);
			notifyAIOnMove();
		}
	}
	
	/**
	 * When player arrives at destination point of flying teleport
	 */
	public void onFlyTeleportEnd() {
		Player player = getOwner();
		if (player.isInPlayerMode(PlayerMode.WINDSTREAM)) {
			player.unsetPlayerMode(PlayerMode.WINDSTREAM);
			player.getLifeStats().triggerFpReduce();
			player.unsetState(CreatureState.FLYING);
			player.setState(CreatureState.ACTIVE);
			player.setState(CreatureState.GLIDING);
			player.getGameStats().updateStatsAndSpeedVisually();
		} else {
			player.unsetState(CreatureState.FLIGHT_TELEPORT);
			player.setFlightTeleportId(0);
			
			if (SecurityConfig.ENABLE_FLYPATH_VALIDATOR) {
				long diff = (System.currentTimeMillis() - player.getFlyStartTime());
				FlyPathEntry path = player.getCurrentFlyPath();
				
				if (player.getWorldId() != path.getEndWorldId()) {
					AuditLogger.info(player, "Player tried to use flyPath #" + path.getId() + " from not native start world " + player.getWorldId() + ". expected " + path.getEndWorldId());
				}
				
				if (diff < path.getTimeInMs()) {
					AuditLogger.info(player, "Player " + player.getName() + " used flypath bug " + diff + " instead of " + path.getTimeInMs());
					/*
					 * todo if works teleport player to start_* xyz, or even ban
					 */
				}
				
				player.setCurrentFlypath(null);
			}
			
			player.setFlightDistance(0);
			player.setState(CreatureState.ACTIVE);
			updateZone();
		}
	}
	
	public boolean addItems(int itemId, int count) {
		return ItemService.addQuestItems(getOwner(), Collections.singletonList(new QuestItems(itemId, count)));
	}
	
	public void startStance(final int skillId) {
		stance = skillId;
	}
	
	public void stopStance() {
		getOwner().getEffectController().removeEffect(stance);
		PacketSendUtility.sendPacket(getOwner(), new SM_PLAYER_STANCE(getOwner(), 0));
		stance = 0;
	}
	
	public int getStanceSkillId() {
		return stance;
	}
	
	public boolean isUnderStance() {
		return stance != 0;
	}
	
	public void updateSoulSickness(int skillId) {
		Player player = getOwner();
		House house = player.getActiveHouse();
		if (house != null) {
			switch (house.getHouseType()) {
			case MANSION:
			case ESTATE:
			case PALACE:
				return;
			default:
				break;
			}
		}
		
		if (!player.havePermission(MembershipConfig.DISABLE_SOULSICKNESS)) {
			int deathCount = player.getCommonData().getDeathCount();
			if (deathCount < 10) {
				deathCount++;
				player.getCommonData().setDeathCount(deathCount);
			}
			
			if (skillId == 0) {
				skillId = 8291;
			}
			SkillEngine.getInstance().getSkill(player, skillId, deathCount, player).useSkill();
		}
	}
	
	/**
	 * Player is considered in combat if he's been attacked or has attacked less or equal 10s before
	 *
	 * @return true if the player is actively in combat
	 */
	public boolean isInCombat() {
		return (((System.currentTimeMillis() - lastAttackedMilis) <= 10000) || ((System.currentTimeMillis() - lastAttackMilis) <= 10000));
	}
	
	/**
	 * Check if NoDeathPenalty is active
	 *
	 * @param player
	 * @return boolean
	 */
	public boolean isNoDeathPenaltyInEffect() {
		// Check if NoDeathPenalty is active
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = iterator.next();
			if (effect.isNoDeathPenalty()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if NoResurrectPenalty is active
	 *
	 * @param player
	 * @return boolean
	 */
	public boolean isNoResurrectPenaltyInEffect() {
		// Check if NoResurrectPenalty is active
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = iterator.next();
			if (effect.isNoResurrectPenalty()) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Check if HiPass is active
	 *
	 * @param player
	 * @return boolean
	 */
	public boolean isHiPassInEffect() {
		// Check if HiPass is active
		Iterator<Effect> iterator = getOwner().getEffectController().iterator();
		while (iterator.hasNext()) {
			Effect effect = iterator.next();
			if (effect.isHiPass()) {
				return true;
			}
		}
		return false;
	}
	
	public void registerListener(Listener listener) {
		this.mListener = listener;
	}
	
	public void unregisterListener() {
		this.mListener = null;
	}
	
	public static abstract interface Listener {
		
		public abstract void onPlayerUsedSkill(int paramInt, Player paramPlayer);
	}
}
