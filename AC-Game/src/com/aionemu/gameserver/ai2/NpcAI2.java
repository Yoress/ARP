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
package com.aionemu.gameserver.ai2;

import com.aionemu.gameserver.ai2.handler.ActivateEventHandler;
import com.aionemu.gameserver.ai2.handler.DiedEventHandler;
import com.aionemu.gameserver.ai2.handler.ShoutEventHandler;
import com.aionemu.gameserver.ai2.handler.SpawnEventHandler;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.ai2.poll.NpcAIPolls;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.controllers.movement.NpcMoveController;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TribeClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.skill.NpcSkillList;
import com.aionemu.gameserver.model.stats.container.NpcLifeStats;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.knownlist.KnownList;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- changed most convenience methods for getting aspects about the owner to final.
 */
@AIName("npc")
public class NpcAI2 extends AITemplate {
	
	@Override
	public Npc getOwner() {
		return (Npc) super.getOwner();
	}
	
	protected final NpcTemplate getObjectTemplate() {
		return getOwner().getObjectTemplate();
	}
	
	protected SpawnTemplate getSpawnTemplate() {
		return getOwner().getSpawn();
	}
	
	protected final NpcLifeStats getLifeStats() {
		return getOwner().getLifeStats();
	}
	
	protected final Race getRace() {
		return getOwner().getRace();
	}
	
	protected final TribeClass getTribe() {
		return getOwner().getTribe();
	}
	
	protected final EffectController getEffectController() {
		return getOwner().getEffectController();
	}
	
	protected final KnownList getKnownList() {
		//If you need this to not be final, feel free to change it.
		return getOwner().getKnownList();
	}
	
	protected final AggroList getAggroList() {
		//If you need this to not be final, feel free to change it.
		return getOwner().getAggroList();
	}
	
	protected final NpcSkillList getSkillList() {
		return getOwner().getSkillList();
	}
	
	protected VisibleObject getCreator() {
		return getOwner().getCreator();
	}
	
	/**
	 * DEPRECATED as movements will be processed as commands only from ai
	 */
	protected final NpcMoveController getMoveController() {
		return getOwner().getMoveController();
	}
	
	protected final int getNpcId() {
		return getOwner().getNpcId();
	}
	
	protected final int getCreatorId() {
		return getOwner().getCreatorId();
	}
	
	protected final boolean isInRange(VisibleObject object, int range) {
		return MathUtil.isIn3dRange(getOwner(), object, range);
	}
	
	@Override
	protected void handleActivate() {
		ActivateEventHandler.onActivate(this);
	}
	
	@Override
	protected void handleDeactivate() {
		ActivateEventHandler.onDeactivate(this);
	}
	
	@Override
	protected void handleSpawned() {
		SpawnEventHandler.onSpawn(this);
	}
	
	@Override
	protected void handleRespawned() {
		SpawnEventHandler.onRespawn(this);
	}
	
	@Override
	protected void handleDespawned() {
		if (poll(AIQuestion.CAN_SHOUT)) {
			ShoutEventHandler.onBeforeDespawn(this);
		}
		SpawnEventHandler.onDespawn(this);
	}
	
	@Override
	protected void handleDied() {
		DiedEventHandler.onSimpleDie(this);
		skillId = 0;
		skillLevel = 0;
	}
	
	@Override
	protected void handleMoveArrived() {
		if (!poll(AIQuestion.CAN_SHOUT) || getSpawnTemplate().getWalkerId() == null) {
			return;
		}
		ShoutEventHandler.onReachedWalkPoint(this);
	}
	
	@Override
	protected void handleTargetChanged(Creature creature) {
		super.handleMoveArrived(); //TODO: WTF is this? Super's implementation is empty! Maybe stop movement with the move controller?
		if (!poll(AIQuestion.CAN_SHOUT)) {
			return;
		}
		ShoutEventHandler.onSwitchedTarget(this, creature);
	}
	
	@Override
	protected AIAnswer pollInstance(AIQuestion question) {
		switch (question) {
		case SHOULD_DECAY:
			return NpcAIPolls.shouldDecay(this);
		case SHOULD_RESPAWN:
			return NpcAIPolls.shouldRespawn(this);
		case SHOULD_REWARD:
			return AIAnswers.POSITIVE;
		case CAN_SHOUT:
			return isMayShout() ? AIAnswers.POSITIVE : AIAnswers.NEGATIVE;
		default:
			return null;
		}
	}
	
	@Override
	public boolean isMayShout() {
		// temp fix, we shouldn't rely on it because of inheritance
		if (AIConfig.SHOUTS_ENABLE) {
			return getOwner().mayShout(0);
		}
		return false;
	}
	
	public boolean isMoveSupported() {
		return getOwner().getGameStats().getMovementSpeedFloat() > 0 && !this.isInSubState(AISubState.FREEZE);
	}
}
