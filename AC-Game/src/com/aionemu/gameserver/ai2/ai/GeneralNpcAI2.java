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
package com.aionemu.gameserver.ai2.ai;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AI2Logger;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.handler.AggroEventHandler;
import com.aionemu.gameserver.ai2.handler.AttackEventHandler;
import com.aionemu.gameserver.ai2.handler.CreatureEventHandler;
import com.aionemu.gameserver.ai2.handler.DiedEventHandler;
import com.aionemu.gameserver.ai2.handler.MoveEventHandler;
import com.aionemu.gameserver.ai2.handler.ReturningEventHandler;
import com.aionemu.gameserver.ai2.handler.ShoutEventHandler;
import com.aionemu.gameserver.ai2.handler.TalkEventHandler;
import com.aionemu.gameserver.ai2.handler.TargetEventHandler;
import com.aionemu.gameserver.ai2.handler.ThinkEventHandler;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.SkillAttackManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.model.templates.npcshout.ShoutEventType;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- Added override for {@link #canThink()}, replaced calls to
 * SimpleAttackManager's isTargetInAttackRange(Npc) with calls to {@link #isTargetInAttackRange(Creature)};
 * removed deprecated method calls.
 * @rework Yon (Aion Reconstruction Project) -- Implemented internal attack handling; be careful when modifying
 * it -- the code runs multi-threaded, but the system is designed to be a single sequence of events. Unlike the
 * previous external implementation (now deprecated), this system avoids having multiple attacks scheduled at
 * once. The following methods were implemented to add this support:<br>
 * {@link #handleAttackIntention(AttackIntention, Creature)}, {@link #handleSimpleAttackIntention(int, Creature)},
 * {@link #handleSkillAttackIntention(int, Creature)}, {@link #handleSkillBuffIntention(int, Creature)}.<br>
 * The following methods were modified to support this system:<br>
 * {@link #handleAttack(Creature)}, {@link #handleAttackComplete()}, {@link #handleTargetReached()},
 * {@link #handleTargetChanged(Creature)}, {@link #chooseAttackIntention(Creature)}.
 */
@AIName("general")
public class GeneralNpcAI2 extends NpcAI2 {
	
	@Override
	public void think() {
		ThinkEventHandler.onThink(this);
	}
	
	@Override
	public boolean canThink() {
		return true;
	}
	
	@Override
	protected void handleDied() {
		DiedEventHandler.onDie(this);
		skillId = 0;
		skillLevel = 0;
	}
	
	@Override
	protected void handleAttack(Creature creature) {
//		AttackEventHandler.onAttack(this, creature); //Deprecated method
		AI2Logger.info(this, "handleAttack");
		if (creature == null || creature.getLifeStats().isAlreadyDead()) {
			return;
		}
		if (isInState(AIState.RETURNING)) {
			getOwner().getMoveController().abortMove();
			setStateIfNot(AIState.IDLE);
			onGeneralEvent(AIEventType.NOT_AT_HOME);
			return;
		}
		if (!canThink()) {
			return;
		}
		if (isInState(AIState.WALKING)) {
			WalkManager.stopWalking(this);
		}
		getOwner().getGameStats().renewLastAttackedTime();
		if (!isInState(AIState.FIGHT)) {
			AI2Logger.info(this, "handleAttack() -> Start fighting");
			AI2Actions.targetCreature(this, creature);
			setStateIfNot(AIState.FIGHT);
			setSubStateIfNot(AISubState.NONE);
//			AttackManager.startAttacking(this); //Deprecated Method
			getOwner().getGameStats().setFightStartingTime();
			EmoteManager.emoteStartAttacking(getOwner());
			onIntentionToAttack(creature);
			if (poll(AIQuestion.CAN_SHOUT)) {
				ShoutEventHandler.onAttackBegin(this, (Creature) getTarget());
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The implementation for {@link GeneralNpcAI2} sets the delay of the next attack based on the owners
	 * {@link com.aionemu.gameserver.model.stats.container.NpcGameStats#getNextAttackInterval() next attack interval}.
	 */
	@Override
	protected void handleAttackIntention(AttackIntention intention, Creature creature) {
		AI2Logger.info(this, "handleAttackIntention(" + intention + ")");
		// don't start attack while in casting substate
		if (getSubState() != AISubState.NONE) {
			AI2Logger.info(this, "Will not handle attack intention in substate: " + getSubState());
			return;
		}
		int delay = getOwner().getGameStats().getNextAttackInterval();
		switch (intention) {
			case FINISH_ATTACK:
			case SWITCH_TARGET:
				think();
				break;
			case SIMPLE_ATTACK:
				handleSimpleAttackIntention(delay, creature);
				break;
			case SKILL_ATTACK:
				if (canUseSkill()) {
					handleSkillAttackIntention(delay, creature);
				} else {
					handleSimpleAttackIntention(delay, creature);
				}
				break;
			case SKILL_BUFF:
				if (canUseSkill()) {
					handleSkillBuffIntention(delay, creature);
				} else {
					handleSimpleAttackIntention(delay, creature);
				}
				break;
			default:
				break;
		}
	}
	
	@Override
	protected void handleSimpleAttackIntention(final int delay, final Creature creature) {
		AI2Logger.info(this, "handleSimpleAttackIntention(" + delay + ")");
		if (getOwner() == getTarget()) {
			AI2Logger.info(this, this.getClass().getSimpleName() + " for entity: " + getOwner().getName() + " tried to attack itself!");
			think();
			return;
		}
		
		if (delay > 0 && !isTargetInAttackRange(creature)) {
			AI2Logger.info(this, "Attack will not be scheduled because target is out of range");
			if (getTarget() != creature) AI2Actions.targetCreature(this, creature);
			onGeneralEvent(AIEventType.TARGET_TOOFAR);
			return;
		}
		
		Runnable wait = new Runnable() {
			public void run() {
				/*
				 * Call self again after waiting; eventually delay will be 0, and the attack will go through.
				 * Either that, or the attack will be pushed off until we're in range again.
				 */
				if (isInSubState(AISubState.ATTACK)) {
					//Ensure this attack wasn't interrupted.
					setSubStateIfNot(AISubState.NONE);
					handleSimpleAttackIntention(getOwner().getGameStats().getNextAttackInterval(), creature);
				}
			}
		};
		
		if (setSubStateIfNot(AISubState.ATTACK)) {
			if (delay > 0) {
				if (delay > AIConfig.MAX_FIGHT_MOVE_WAIT_TIME) {
					ThreadPoolManager.getInstance().schedule(wait, AIConfig.MAX_FIGHT_MOVE_WAIT_TIME);
				} else {
					if (getTarget() != creature) AI2Actions.targetCreature(this, creature);
					ThreadPoolManager.getInstance().schedule(wait, delay);
				}
			} else {
				if (getTarget() != creature) AI2Actions.targetCreature(this, creature);
				
				if (creature != null && !creature.getLifeStats().isAlreadyDead()) {
					if (!getOwner().canSee(creature)) {
						getOwner().getController().cancelCurrentSkill();
						setSubStateIfNot(AISubState.NONE);
						onGeneralEvent(AIEventType.TARGET_GIVEUP);
						return;
					}
					
					if (!GeoService.getInstance().canSee(getOwner(), creature) || !isTargetInAttackRange(creature)) {
						setSubStateIfNot(AISubState.NONE);
						onGeneralEvent(AIEventType.TARGET_TOOFAR);
						return;
					}
					
					if (getSubState() != AISubState.ATTACK || getState() != AIState.FIGHT) {
						//Something happened to cancel this attack, bail out.
						AI2Logger.info(this, "Something interfered with handleSimpleAttackIntention(), AI is in an invalid state to attack!");
						return;
					}
					
					getOwner().getController().attackTarget(creature, 0);
					setSubStateIfNot(AISubState.NONE);
					onGeneralEvent(AIEventType.ATTACK_COMPLETE);
				} else {
					setSubStateIfNot(AISubState.NONE);
					onGeneralEvent(AIEventType.TARGET_GIVEUP);
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The implementation for {@link GeneralNpcAI2} does not set the last skill use time in this Npc's SkillList
	 * as that is handled elsewhere when the skill use is completed. This implementation also does not handle any
	 * target validation, so skills may not be appropriate for their target. If the target is null, dead, or too
	 * far away, or if the skill that has been prepared cannot be found in static data, this SkillBuff will be
	 * cancelled and the appropriate AI event will be fired to handle it.
	 */
	@Override
	protected void handleSkillAttackIntention(final int delay, final Creature creature) {
		final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (template == null) {
			AI2Logger.info(this, "Skill lookup failed for skillId: " + skillId + "!");
			onGeneralEvent(AIEventType.ATTACK_COMPLETE);
			return;
		}
		
		if (creature == null || creature.getLifeStats().isAlreadyDead()) {
			if (getTarget() != creature) AI2Actions.targetCreature(this, creature);
			onGeneralEvent(AIEventType.TARGET_GIVEUP);
			getOwner().getController().abortCast();
			return;
		}
		
		AI2Logger.info(this, "handleSkillAttackIntention(" + delay + ")");
		
		if (delay > 0 && !MathUtil.isInAttackRange(getOwner(), creature, template.getProperties().getFirstTargetRange())) {
			if (getTarget() != creature) AI2Actions.targetCreature(this, creature);
			onGeneralEvent(AIEventType.TARGET_TOOFAR);
			getOwner().getController().abortCast();
			return;
		}
		
		Runnable wait = new Runnable() {
			public void run() {
				/*
				 * Call self again after waiting; eventually delay will be 0, and the skill will go through.
				 * Either that, or the skill will be pushed off until we're in range/able again.
				 */
				if (isInSubState(AISubState.CAST)) {
					//Ensure this skill wasn't interrupted.
					setSubStateIfNot(AISubState.NONE);
					if (canUseSkill()) {
						handleSkillAttackIntention(getOwner().getGameStats().getNextAttackInterval(), creature);
					} else {
						skillId = 0;
						skillLevel = 0;
						onGeneralEvent(AIEventType.ATTACK_COMPLETE);
					}
				}
			}
		};
		
		if (setSubStateIfNot(AISubState.CAST)) {
			if (delay > 0) {
				if (delay > AIConfig.MAX_FIGHT_MOVE_WAIT_TIME) {
					ThreadPoolManager.getInstance().schedule(wait, AIConfig.MAX_FIGHT_MOVE_WAIT_TIME);
				} else {
					if (getTarget() != creature) AI2Actions.targetCreature(this, creature);
					ThreadPoolManager.getInstance().schedule(wait, delay);
				}
			} else {
				if (getTarget() != creature) AI2Actions.targetCreature(this, creature);
				
				if (!getOwner().canSee(creature)) {
					getOwner().getController().abortCast();
					setSubStateIfNot(AISubState.NONE);
					onGeneralEvent(AIEventType.TARGET_GIVEUP);
					return;
				}
				
				if (!MathUtil.isInAttackRange(getOwner(), creature, template.getProperties().getFirstTargetRange())) {
					getOwner().getController().abortCast();
					setSubStateIfNot(AISubState.NONE);
					onGeneralEvent(AIEventType.TARGET_TOOFAR);
					return;
				}
				
				if (getSubState() != AISubState.CAST || getState() != AIState.FIGHT) {
					//Something happened to cancel this attack, bail out.
					return;
				}
				
				if (creature != null && !creature.getLifeStats().isAlreadyDead()) {
					boolean noTimeLimit = template.getEffectsDuration(skillLevel) >= 86400000;
					switch (template.getSubType()) {
						case BUFF:
							switch (template.getProperties().getFirstTarget()) {
								case ME:
									if (getOwner().getEffectController().isAbnormalPresentBySkillId(skillId) && noTimeLimit) {
										setSubStateIfNot(AISubState.NONE);
										onGeneralEvent(AIEventType.ATTACK_COMPLETE);
										return;
									}
									break;
								default:
									if (creature.getEffectController().isAbnormalPresentBySkillId(skillId) && noTimeLimit) {
										setSubStateIfNot(AISubState.NONE);
										onGeneralEvent(AIEventType.ATTACK_COMPLETE);
										return;
									}
								}
								break;
						default:
							break;
					}
					if (!getOwner().getController().useSkill(skillId, skillLevel)) {
						skillId = 0;
						skillLevel = 0;
						setSubStateIfNot(AISubState.NONE);
						onGeneralEvent(AIEventType.ATTACK_COMPLETE);
					}
				} else {
					setSubStateIfNot(AISubState.NONE);
					onGeneralEvent(AIEventType.TARGET_GIVEUP);
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The implementation for {@link GeneralNpcAI2} does not set the last skill use time in this Npc's SkillList
	 * as that is handled elsewhere when the skill use is completed. This implementation also does not handle any
	 * target validation, so skills may not be appropriate for their target. If the target is null, dead, or too
	 * far away, or if the skill that has been prepared cannot be found in static data, this SkillBuff will be
	 * cancelled and the appropriate AI event will be fired to handle it.
	 */
	@Override
	protected void handleSkillBuffIntention(final int delay, final Creature creature) {
		final SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (template == null) {
			AI2Logger.info(this, "Skill lookup failed for skillId: " + skillId + "!");
			onGeneralEvent(AIEventType.ATTACK_COMPLETE);
			return;
		}
		
		if (creature == null || creature.getLifeStats().isAlreadyDead()) {
			if (getTarget() != creature) AI2Actions.targetCreature(this, creature);
			onGeneralEvent(AIEventType.TARGET_GIVEUP);
			getOwner().getController().abortCast();
			return;
		}
		
		AI2Logger.info(this, "handleSkillBuffIntention(" + delay + ")");
		
		if (delay > 0 && !MathUtil.isInAttackRange(getOwner(), creature, template.getProperties().getFirstTargetRange())) {
			if (getTarget() != creature) AI2Actions.targetCreature(this, creature);
			onGeneralEvent(AIEventType.TARGET_TOOFAR);
			getOwner().getController().abortCast();
			return;
		}
		
		Runnable wait = new Runnable() {
			public void run() {
				/*
				 * Call self again after waiting; eventually delay will be 0, and the skill will go through.
				 * Either that, or the skill will be pushed off until we're in range/able again.
				 */
				if (isInSubState(AISubState.CAST)) {
					//Ensure this buff wasn't interrupted.
					setSubStateIfNot(AISubState.NONE);
					if (canUseSkill()) {
						handleSkillBuffIntention(getOwner().getGameStats().getNextAttackInterval(), creature);
					} else {
						skillId = 0;
						skillLevel = 0;
						onGeneralEvent(AIEventType.ATTACK_COMPLETE);
					}
				}
			}
		};
		if (setSubStateIfNot(AISubState.CAST)) {
			if (delay > 0) {
				if (delay > AIConfig.MAX_FIGHT_MOVE_WAIT_TIME) {
					ThreadPoolManager.getInstance().schedule(wait, AIConfig.MAX_FIGHT_MOVE_WAIT_TIME);
				} else {
					if (getTarget() != creature) AI2Actions.targetCreature(this, creature);
					ThreadPoolManager.getInstance().schedule(wait, delay);
				}
			} else {
				if (getTarget() != creature) AI2Actions.targetCreature(this, creature);
				
				if (getSubState() != AISubState.CAST || getState() != AIState.FIGHT) {
					//Something happened to cancel this attack, bail out.
					return;
				}
				if (!MathUtil.isInAttackRange(getOwner(), creature, template.getProperties().getFirstTargetRange())) {
					setSubStateIfNot(AISubState.NONE);
					onGeneralEvent(AIEventType.TARGET_TOOFAR);
					getOwner().getController().abortCast();
					return;
				}
				
				if (creature != null && !creature.getLifeStats().isAlreadyDead()) {
					boolean noTimeLimit = template.getEffectsDuration(skillLevel) >= 86400000;
					switch (template.getSubType()) {
						case BUFF:
							switch (template.getProperties().getFirstTarget()) {
								case ME:
									if (getOwner().getEffectController().isAbnormalPresentBySkillId(skillId) && noTimeLimit) {
										setSubStateIfNot(AISubState.NONE);
										onGeneralEvent(AIEventType.ATTACK_COMPLETE);
										return;
									}
									break;
								default:
									if (creature.getEffectController().isAbnormalPresentBySkillId(skillId) && noTimeLimit) {
										setSubStateIfNot(AISubState.NONE);
										onGeneralEvent(AIEventType.ATTACK_COMPLETE);
										return;
									}
								}
								break;
						default:
							break;
					}
					if (!getOwner().getController().useSkill(skillId, skillLevel)) {
						skillId = 0;
						skillLevel = 0;
						setSubStateIfNot(AISubState.NONE);
						onGeneralEvent(AIEventType.ATTACK_COMPLETE);
					}
				} else {
					setSubStateIfNot(AISubState.NONE);
					onGeneralEvent(AIEventType.TARGET_GIVEUP);
				}
			}
		}
	}
	
	@Override
	protected boolean handleCreatureNeedsSupport(Creature creature) {
		return AggroEventHandler.onCreatureNeedsSupport(this, creature);
	}
	
	@Override
	protected void handleDialogStart(Player player) {
		TalkEventHandler.onTalk(this, player);
	}
	
	@Override
	protected void handleDialogFinish(Player creature) {
		TalkEventHandler.onFinishTalk(this, creature);
	}
	
	@Override
	protected void handleFinishAttack() {
		AttackEventHandler.onFinishAttack(this);
		skillId = 0;
		skillLevel = 0;
	}
	
	@Override
	protected void handleAttackComplete() {
		skillId = 0;
		skillLevel = 0;
//		AttackEventHandler.onAttackComplete(this); //Deprecated Method
		AI2Logger.info(this, "handleAttackComplete() --> Last attack " + getOwner().getGameStats().getLastAttackTimeDelta() + "s ago.");
		getOwner().getGameStats().renewLastAttackTime();
		
		Creature targ = getAggroList().getMostHated();
		if (getTarget() == targ && targ != null) {
			onIntentionToAttack(targ);
		} else if (targ != null) {
			onCreatureEvent(AIEventType.TARGET_CHANGED, targ);
		} else {
			think();
		}
	}
	
	@Override
	protected void handleTargetReached() {
		if (getState() == AIState.FIGHT) {
			getOwner().getMoveController().abortMove();
//			if (getOwner().getMoveController().isFollowingTarget()) {
//				getOwner().getMoveController().storeStep();
//			}
//			AttackManager.scheduleNextAttack(this); //Deprecated Method
			onIntentionToAttack((Creature) getTarget());
		} else {
			TargetEventHandler.onTargetReached(this);
		}
	}
	
	@Override
	protected void handleNotAtHome() {
		ReturningEventHandler.onNotAtHome(this);
	}
	
	@Override
	protected void handleBackHome() {
		ReturningEventHandler.onBackHome(this);
	}
	
	@Override
	protected void handleTargetTooFar() {
		TargetEventHandler.onTargetTooFar(this);
	}
	
	@Override
	protected void handleTargetGiveup() {
		TargetEventHandler.onTargetGiveup(this);
	}
	
	@Override
	protected void handleTargetChanged(Creature creature) {
		super.handleTargetChanged(creature);
//		TargetEventHandler.onTargetChange(this, creature); //Deprecated method
		AI2Actions.targetCreature(this, creature);
		if (isInState(AIState.FIGHT)) onIntentionToAttack(creature);
	}
	
	@Override
	protected void handleMoveValidate() {
		MoveEventHandler.onMoveValidate(this);
	}
	
	@Override
	protected void handleMoveArrived() {
		super.handleMoveArrived();
		MoveEventHandler.onMoveArrived(this);
	}
	
	@Override
	protected void handleCreatureMoved(Creature creature) {
		CreatureEventHandler.onCreatureMoved(this, creature);
	}
	
	@Override
	protected boolean canHandleEvent(AIEventType eventType) {
		boolean canHandle = super.canHandleEvent(eventType);
		
		switch (eventType) {
			case CREATURE_MOVED:
				return canHandle || DataManager.NPC_SHOUT_DATA.hasAnyShout(getOwner().getWorldId(), getOwner().getNpcId(), ShoutEventType.SEE);
			case CREATURE_NEEDS_SUPPORT:
				return canHandle && isNonFightingState() && DataManager.TRIBE_RELATIONS_DATA.hasSupportRelations(getOwner().getTribe());
			default: break;
		}
		return canHandle;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * The implementation for {@link GeneralNpcAI2} selects and prepares a skill to use if
	 * returning {@link AttackIntention#SKILL_ATTACK}.
	 */
	@Override
	public AttackIntention chooseAttackIntention(Creature creature) {
		if (getTarget() == null || getAggroList().getMostHated() == null || ((Creature) getTarget()).getLifeStats().isAlreadyDead()) {
			return AttackIntention.FINISH_ATTACK;
		}
		
		NpcSkillEntry skill = SkillAttackManager.chooseNextSkill(this);
		if (skill != null) {
			skillId = skill.getSkillId();
			skillLevel = skill.getSkillLevel();
			return AttackIntention.SKILL_ATTACK;
		}
		return AttackIntention.SIMPLE_ATTACK;
	}
	
}
