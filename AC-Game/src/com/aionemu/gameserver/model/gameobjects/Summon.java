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
package com.aionemu.gameserver.model.gameobjects;

import java.util.concurrent.Future;

import com.aionemu.gameserver.ai2.AI2Engine;
import com.aionemu.gameserver.controllers.CreatureController;
import com.aionemu.gameserver.controllers.SummonController;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.controllers.attack.PlayerAggroList;
import com.aionemu.gameserver.controllers.movement.SiegeWeaponMoveController;
import com.aionemu.gameserver.controllers.movement.SummonMoveController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.container.SummonGameStats;
import com.aionemu.gameserver.model.stats.container.SummonLifeStats;
import com.aionemu.gameserver.model.summons.SummonMode;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.stats.SummonStatsTemplate;
import com.aionemu.gameserver.world.WorldPosition;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- Added {@link #setSummonSkillId(int)}, and {@link #getSummonSkillId()}.
 */
public class Summon extends Creature {
	
	private Player master; //FIXME: Remove reference when Summon is removed from map; potential memory leak.
	private SummonMode mode = SummonMode.GUARD;
	private byte level;
	private int liveTime;
	private int summonSkillId;
	private Future<?> releaseTask;
	
	/**
	 * @param objId
	 * @param controller
	 * @param spawnTemplate
	 * @param objectTemplate
	 * @param position
	 * @param level
	 */
	public Summon(int objId, CreatureController<? extends Creature> controller, SpawnTemplate spawnTemplate, NpcTemplate objectTemplate, byte level, int time) {
		super(objId, controller, spawnTemplate, objectTemplate, new WorldPosition(spawnTemplate.getWorldId()));
		controller.setOwner(this);
		String ai = objectTemplate.getAi();
		AI2Engine.getInstance().setupAI(ai, this);
		moveController = ai.equals("siege_weapon") ? new SiegeWeaponMoveController(this) : new SummonMoveController(this);
		this.level = level;
		this.liveTime = time;
		SummonStatsTemplate statsTemplate = DataManager.SUMMON_STATS_DATA.getSummonTemplate(objectTemplate.getTemplateId(), level);
		setGameStats(new SummonGameStats(this, statsTemplate));
		setLifeStats(new SummonLifeStats(this));
	}
	
	@Override
	protected AggroList createAggroList() {
		return new PlayerAggroList(this);
	}
	
	@Override
	public SummonGameStats getGameStats() {
		return (SummonGameStats) super.getGameStats();
	}
	
	@Override
	public Player getMaster() {
		return master;
	}
	
	/**
	 * @param master the master to set
	 */
	public void setMaster(Player master) {
		this.master = master;
	}
	
	/**
	 * Sets the ID of the skill used to summon this summon. This should be used to track the skill
	 * that brings this summon into the world, so when the summon is removed from the world that
	 * skill's cooldown can be set.
	 * 
	 * @param skillId -- The ID of the skill used to summon this summon
	 */
	public void setSummonSkillId(int skillId) {
		summonSkillId = skillId;
	}
	
	/**
	 * Returns the ID of the skill that was used to summon this summon.
	 * Must be set with {@link #setSummonSkillId(int)}.
	 * 
	 * @return The skill ID of the skill that summoned this summon
	 */
	public int getSummonSkillId() {
		return summonSkillId;
	}
	
	@Override
	public String getName() {
		return objectTemplate.getName();
	}
	
	/**
	 * @return the level
	 */
	@Override
	public byte getLevel() {
		return level;
	}
	
	@Override
	public NpcTemplate getObjectTemplate() {
		return (NpcTemplate) super.getObjectTemplate();
	}
	
	public int getNpcId() {
		return getObjectTemplate().getTemplateId();
	}
	
	public int getNameId() {
		return getObjectTemplate().getNameId();
	}
	
	/**
	 * @return NpcObjectType.SUMMON
	 */
	@Override
	public NpcObjectType getNpcObjectType() {
		return NpcObjectType.SUMMON;
	}
	
	@Override
	public SummonController getController() {
		return (SummonController) super.getController();
	}
	
	/**
	 * @return the mode
	 */
	public SummonMode getMode() {
		return mode;
	}
	
	/**
	 * @param mode the mode to set
	 */
	public void setMode(SummonMode mode) {
		this.mode = mode;
	}
	
	@Override
	public boolean isEnemy(Creature creature) {
		return master != null ? master.isEnemy(creature) : false;
	}
	
	@Override
	public boolean isEnemyFrom(Npc npc) {
		return master != null ? master.isEnemyFrom(npc) : false;
	}
	
	@Override
	public boolean isEnemyFrom(Player player) {
		return master != null ? master.isEnemyFrom(player) : false;
	}
	
	@Override
	public TribeClass getTribe() {
		if (master == null) {
			return ((NpcTemplate) objectTemplate).getTribe();
		}
		return master.getTribe();
	}
	
	@Override
	public SummonMoveController getMoveController() {
		return (SummonMoveController) super.getMoveController();
	}
	
	@Override
	public Creature getActingCreature() {
		return getMaster() == null ? this : getMaster();
	}
	
	@Override
	public Race getRace() {
		return getMaster() != null ? getMaster().getRace() : Race.NONE;
	}
	
	/**
	 * @return liveTime in sec.
	 */
	public int getLiveTime() {
		return liveTime;
	}
	
	/**
	 * @param liveTime in sec.
	 */
	public void setLiveTime(int liveTime) {
		this.liveTime = liveTime;
	}
	
	public void setReleaseTask(Future<?> task) {
		releaseTask = task;
	}
	
	public void cancelReleaseTask() {
		if (releaseTask != null && !releaseTask.isDone()) {
			releaseTask.cancel(true);
		}
	}
}
