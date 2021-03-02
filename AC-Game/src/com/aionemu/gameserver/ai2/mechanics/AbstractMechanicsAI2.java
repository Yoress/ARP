/**
 * This file is part of the Aion Reconstruction Project Server.
 *
 * The Aion Reconstruction Project Server is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * The Aion Reconstruction Project Server is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with the Aion Reconstruction Project Server. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * @AionReconstructionProjectTeam
 */
package com.aionemu.gameserver.ai2.mechanics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.ai.GeneralNpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.mechanics.MechanicEventQueue.EventActionPair;
import com.aionemu.gameserver.ai2.mechanics.actions.Action;
import com.aionemu.gameserver.ai2.mechanics.actions.Action.DoNothing;
import com.aionemu.gameserver.ai2.mechanics.actions.AttackMostHatingAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SpawnAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SpawnOnMultiTargetAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SpawnOnTargetAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SpawnOnTargetByAttackerIndicatorAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SwitchTargetAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SwitchTargetByAttackerIndicatorAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SwitchTargetByClassIndicatorAction;
import com.aionemu.gameserver.ai2.mechanics.actions.UseSkillAction;
import com.aionemu.gameserver.ai2.mechanics.actions.UseSkillByAttackerIndicatorAction;
import com.aionemu.gameserver.ai2.mechanics.context.FlagvarIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.IntvarIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.SkillIndex;
import com.aionemu.gameserver.ai2.mechanics.context.SpawnId;
import com.aionemu.gameserver.ai2.mechanics.events.CreatureEvent;
import com.aionemu.gameserver.ai2.mechanics.events.GeneralMechanicEvent;
import com.aionemu.gameserver.ai2.mechanics.events.HyperlinkEvent;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.ai2.mechanics.events.SkillEvent;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.skill.NpcSkillEntry;
import com.aionemu.gameserver.model.skill.NpcSkillList;
import com.aionemu.gameserver.model.templates.npc.NpcTemplateType;
import com.aionemu.gameserver.services.TribeRelationService;
import com.aionemu.gameserver.skillengine.model.SkillSubType;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.knownlist.Visitor;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public abstract class AbstractMechanicsAI2 extends GeneralNpcAI2 {
	
	protected static final Logger LOG = LoggerFactory.getLogger(AbstractMechanicsAI2.class);
	
	/*
	 * FIXME: This queue should be reworked again so all actions happen from the AI,
	 * instead of some occurring from the Pattern class.
	 */
	final MechanicEventQueue attackIntentionQueue = new MechanicEventQueue();
	
	public final MechanicSpawnGroup[] spawns = new MechanicSpawnGroup[SpawnId.values().length];
	
	private int[] intvars = new int[IntvarIndicator.values().length];
	
	private boolean[] flagvars = new boolean[FlagvarIndicator.values().length];
	
	private volatile boolean ignoreIdleQueue = false;
	
	public final AIMechanics getAIMechanics() {
		return AIMechanics.getMechanic(getOwner().getObjectTemplate().getMechanic());
	}
	
	public final boolean hasHandler(MechanicEventType type) {
		return AIMechanics.getMechanic(getOwner().getObjectTemplate().getMechanic()).hasHandler(type);
	}
	
	protected final void resetMechanicSystem() {
		attackIntentionQueue.clear();
		ignoreIdleQueue = false;
//		if (spawns != null) for (MechanicSpawnGroup spawn: spawns) {
//			if (spawn != null) spawn.despawnAll();
//		}
//		Arrays.fill(spawns, null);
		intvars = new int[IntvarIndicator.values().length];
		flagvars = new boolean[FlagvarIndicator.values().length];
	}
	
	public final void resetQueue() {
		attackIntentionQueue.clear();
	}
	
	public final boolean addIntvar(IntvarIndicator var, int value, int lowerBound, int upperBound, boolean trueOnlyWhenHitBound) {
		int curValue = intvars[var.ordinal()];
		if (curValue >= lowerBound && curValue <= upperBound) {
			curValue += value;
			if (curValue < lowerBound) curValue = lowerBound;
			if (curValue > upperBound) curValue = upperBound;
			intvars[var.ordinal()] = curValue;
			if (trueOnlyWhenHitBound) {
				return curValue == lowerBound || curValue == upperBound;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public final boolean compareAndSetIntvar(IntvarIndicator var, boolean lessThan, int compare, int set) {
		int curValue = intvars[var.ordinal()];
		if ((lessThan ? (curValue < compare) : (curValue > compare))) {
			intvars[var.ordinal()] = set;
			return true;
		}
		intvars[var.ordinal()] = set;
		return false;
	}
	
	public final boolean setFlagvar(FlagvarIndicator flagvarIndicator) {
		synchronized (flagvars) {
			if (!flagvars[flagvarIndicator.ordinal()]) {
				flagvars[flagvarIndicator.ordinal()] = true;
				return true;
			}
		}
		return false;
	}
	
	public final boolean unsetFlagvar(FlagvarIndicator flagvarIndicator) {
		synchronized (flagvars) {
			if (flagvars[flagvarIndicator.ordinal()]) {
				flagvars[flagvarIndicator.ordinal()] = false;
				return true;
			}
		}
		return false;
	}
	
	//Maybe consider using polymorphism to limit these overloads?
	public final void createSpawnGroup(SpawnId spawnId, SpawnAction spawnAction) {
		if (spawns[spawnId.ordinal()] == null) {
			spawns[spawnId.ordinal()] = new MechanicSpawnGroup(getOwner());
		}
		spawns[spawnId.ordinal()].spawn(spawnAction);
	}
	
	public final void createSpawnGroup(SpawnId spawnId, SpawnOnTargetAction spawnOnTargetAction, VisibleObject target) {
		if (spawns[spawnId.ordinal()] == null) {
			spawns[spawnId.ordinal()] = new MechanicSpawnGroup(getOwner());
		}
		spawns[spawnId.ordinal()].spawn(spawnOnTargetAction, target);
	}
	
	public final void createSpawnGroup(SpawnId spawnId, SpawnOnTargetByAttackerIndicatorAction spawnOnTargetByAttackerIndicatorAction, VisibleObject target) {
		if (spawns[spawnId.ordinal()] == null) {
			spawns[spawnId.ordinal()] = new MechanicSpawnGroup(getOwner());
		}
		spawns[spawnId.ordinal()].spawn(spawnOnTargetByAttackerIndicatorAction, target);
	}
	
	public final void createSpawnGroup(SpawnId spawnId, SpawnOnMultiTargetAction spawnOnMultiTargetAction) {
		if (spawns[spawnId.ordinal()] == null) {
			spawns[spawnId.ordinal()] = new MechanicSpawnGroup(getOwner());
		}
		spawns[spawnId.ordinal()].spawn(spawnOnMultiTargetAction, getOwner().getAggroList());
	}
	
	/**
	 * This method will accept an event that has happened to this AI and prepare this AI
	 * for the proper response. If this method is called internally from the AI, and it returns true,
	 * nothing visible to the player should happen as a response to this event unless necessary.
	 * Called externally, the return value of this method can be ignored unless a general,
	 * creature, or custom event is also called as a response to the event.
	 * <p>
	 * This method only returns true if the result of this mechanic event is to do nothing.
	 * 
	 * @param event -- the event to be handled.
	 * @return true if the AI should do nothing, false otherwise.
	 */
	public boolean onMechanicEvent(final MechanicEvent event) {
		if (attackIntentionQueue.cannotAcceptEvent(event.type)) return false;
		AIMechanics mechanics = getAIMechanics();
		try {
			mechanics.handleMechanicEvent(event, this);
		} catch (DoNothing e) {
			return true;
		}
		return false;
	}
	
	public void notifyIdleQueue() {
		/*
		 * Not a fan of simply calling another method, but this allows sub classes to handle
		 * things differently, and hides handleIdleQueue() from public view.
		 */
		try {
			handleIdleQueue();
		} catch (NullPointerException e) {
			//This is a failsafe, so let's just clear out the queue.
			LOG.error("Failsafe triggered for Mechanic Handler: [" + getAIMechanics().mechanicId + "]. Clearing Idle Queue.");
			attackIntentionQueue.clear();
		}
	}
	
	protected final void handleIdleQueue() {
		synchronized (attackIntentionQueue) {
			if (!isNonFightingState() || ignoreIdleQueue) return;
			EventActionPair pair = attackIntentionQueue.poke();
			if (pair != null) {
				switch (pair.action.type) {
					case despawn_self:
					case flee_from:
					case say_to_all: {
						attackIntentionQueue.pop();
						try {
							pair.action.performAction(pair.event, this);
						} catch (Exception e) {
							LOG.error(pair.event + " <[" + pair.action + "]> failed by exception for mechanic: [" + getAIMechanics().mechanicId + "]!", e);
						}
						handleIdleQueue(); //Recursive call
						break;
					}
					case use_skill: {
						UseSkillAction act = (UseSkillAction) pair.action;
						Creature intendedTarget = (Creature) pair.event.getObjectIndicator(act.target, this);
						Creature target = (Creature) getTarget();
						try {
							prepareSkill(act.skill, intendedTarget, target);
						} catch (BadTargetException e) {
							AI2Actions.targetCreature(this, e.newTarget);
							target = (Creature) getTarget();
							try {
								prepareSkill(act.skill, intendedTarget, target);
							} catch (BadTargetException e2) {
								//Ignore the queue item at this point
								attackIntentionQueue.pop();
								handleIdleQueue(); //Recursive call
							}
						}
						if (skillId != 0) {
							AI2Actions.useSkill(this, skillId, skillLevel);
						}
						//Don't recursively call to handle anything further; handleAttackComplete() will call us again.
						break;
					}
					case add_battle_timer:
					case add_hate_point:
					case attack_most_hating:
					case broadcast_message:
					case broadcast_message_to_party:
					case despawn:
					case display_system_message:
					case do_nothing:
					case goto_alias:
					case goto_next_waypoint:
					case goto_waypoint:
					case random_move:
					case reset_hatepoints:
					case say_to_all_str:
					case send_message:
					case send_system_msg:
					case set_condition_spawn_variable:
					case set_idle_timer:
					case spawn:
					case spawn_on_target:
					case spawn_on_target_by_attacker_indicator:
					case switch_target:
					case switch_target_by_attacker_indicator:
					case switch_target_by_class_indicator:
					case teleport_target:
					case teleport_target_alias:
					case use_skill_by_attacker_indicator:
						throw new IllegalStateException("The event queue should not have no delay or combat Actions while this AI is not in combat.");
					default: assert false:"Unsupported Action type: " + pair.action.type;
				}
			}
		}
	}
	
	public final synchronized void onSeeCreatureAggro(Creature attacker, Creature attacked) {
		/*
		 * Determine if we care about the attacker,
		 * If we care, check the range and if we can see them (via geo service; assume we can see through their stealth),
		 * check if we can see the target, and that they are an enemy, then fire on_see_friend_attacking mechanic event.
		 * if the event returns false, then support the one we care about if we aren't fighting something else,
		 * otherwise do nothing.
		 */
		Npc us = getOwner();
		if (us == attacker || us == attacked) return; //We know if we are attacking, or are under attack already.
		if (TribeRelationService.isSupport(us, attacker) && MathUtil.isInRange(us, attacker, us.getAggroRange())) {
			if (GeoService.getInstance().canSee(us, attacker) && GeoService.getInstance().canSee(us, attacked) && attacked.isEnemy(us)) {
				ignoreIdleQueue = true;
				MechanicEvent event = new CreatureEvent(MechanicEventType.on_see_friend_attacking, attacked, attacker, attacked);
				if (us.canSee(attacked) && !onMechanicEvent(event) && isNonFightingState()) {
					//Help our friend attack
					onCreatureEvent(AIEventType.CREATURE_AGGRO, attacked);
				} else if (isNonFightingState()) {
					ignoreIdleQueue = false;
					notifyIdleQueue();
				}
				ignoreIdleQueue = false;
			}
		}
		logEvent(AIEventType.CREATURE_NEEDS_SUPPORT);
	}
	
	public final synchronized void onSeeCreatureAttacked(Creature attacked, int skillId, Creature attacker) {
		/*
		 * Determine if we care about attacked or attacker,
		 * If we care, check the range and if we can see them (via geo service),
		 * help the one we care about if associated mechanic event returns false,
		 * otherwise do nothing.
		 */
		Npc us = getOwner();
		if (attacked == us || attacker == us) return; //We know if we are attacking, or are under attack already.
		
		//We might want to guard a player being attacked
		boolean guard = (us.getTribe().isGuard() || us.getObjectTemplate().getNpcTemplateType() == NpcTemplateType.GUARD)
						&& (attacked instanceof Player && attacker.isEnemy(us) && !attacked.isEnemy(us));
		
		if ((TribeRelationService.isSupport(us, attacked) || guard) && attacker.isEnemy(us)) {
			if (MathUtil.isInRange(us, attacked, us.getAggroRange()) && us.canSee(attacker)) {
				if (GeoService.getInstance().canSee(us, attacked) && GeoService.getInstance().canSee(us, attacker)) {
					ignoreIdleQueue = true;
					MechanicEvent event;
					if (skillId == 0) {
						event = new CreatureEvent(MechanicEventType.on_see_friend_attacked, attacked, attacked, attacker);
					} else {
						event = new SkillEvent(MechanicEventType.on_friend_spelled, skillId, attacked, attacker);
					}
					if (!onMechanicEvent(event) && isNonFightingState()) {
						//Help our friend
						onCreatureEvent(AIEventType.CREATURE_AGGRO, attacker);
					} else if (isNonFightingState()) {
						ignoreIdleQueue = false;
						notifyIdleQueue();
					}
					ignoreIdleQueue = false;
				}
			}
		} else if (TribeRelationService.isSupport(us, attacker) && attacked.isEnemy(us) && isNonFightingState()) {
			//Let's not worry about guarding the attacker.
			if (MathUtil.isInRange(us, attacker, us.getAggroRange()) && us.canSee(attacked)) {
				if (GeoService.getInstance().canSee(us, attacker) && GeoService.getInstance().canSee(us, attacked)) {
					ignoreIdleQueue = true;
					MechanicEvent event = new CreatureEvent(MechanicEventType.on_see_friend_attacking, attacked, attacker, attacked);
					if (!onMechanicEvent(event) && isNonFightingState()) {
						//Help our friend
						onCreatureEvent(AIEventType.CREATURE_AGGRO, attacked);
					}
					ignoreIdleQueue = false;
				}
			}
		}
		if (hasHandler(MechanicEventType.on_see_attacked)) {
			if (MathUtil.isInRange(us, attacked, us.getAggroRange()) && us.canSee(attacker)) {
				if (GeoService.getInstance().canSee(us, attacked) && GeoService.getInstance().canSee(us, attacker)) {
					onMechanicEvent(new CreatureEvent(MechanicEventType.on_see_attacked, attacked, null, attacker));
				}
			}
		}
		if (skillId != 0 && getOwner().getMaster() == attacked && hasHandler(MechanicEventType.on_see_master_spelled)) {
			if (MathUtil.isInRange(us, attacked, us.getAggroRange()) && us.canSee(attacker)) {
				if (GeoService.getInstance().canSee(us, attacked) && GeoService.getInstance().canSee(us, attacker)) {
					onMechanicEvent(new SkillEvent(MechanicEventType.on_see_master_spelled, skillId, attacked, attacker));
				}
			}
		}
		if (skillId == 0 && getOwner().getMaster() == attacked && hasHandler(MechanicEventType.on_master_attacked)) {
			onMechanicEvent(new CreatureEvent(MechanicEventType.on_master_attacked, attacked, null, attacker));
		}
		logEvent(AIEventType.CREATURE_NEEDS_SUPPORT);
	}
	
	public final void onSeeCreatureDie(Creature slain, Creature killer) {
		if (!(killer instanceof Player)
		|| !TribeRelationService.isSupport(getOwner(), slain)
		|| !MathUtil.isIn3dRange(getOwner(), slain, getOwner().getAggroRange()))
			return;
		if (getTarget() == killer) {
			onMechanicEvent(new CreatureEvent(MechanicEventType.on_see_friend_killed_by_user, slain, slain, killer));
		} else {
			onMechanicEvent(new CreatureEvent(MechanicEventType.on_sense_friend_killed_by_user, slain, slain, killer));
		}
	}
	
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		onMechanicEvent(new HyperlinkEvent(MechanicEventType.on_hyperlink_clicked, dialogId, player));
		return false;
	}
	
	@Override
	public boolean canThink() {
		return getOwner().getGameStats().getAttackRange().getBase() > 0;
	}
	
	@Override
	protected boolean canHandleEvent(AIEventType eventType) {
		if (eventType == AIEventType.CREATURE_NEEDS_SUPPORT) {
			String s = " events should call #onSeeCreatureAttacked(Creature, int) or #onSeeCreatureAttacking(Creature) for ";
			throw new IllegalStateException(eventType + s + this.getClass().getSimpleName());
		}
		
		if (eventType == AIEventType.CREATURE_MOVED && isInState(AIState.FOLLOWING)) {
			return true;
		}
		
		return super.canHandleEvent(eventType);
	}
	
	@Override
	protected boolean isDestinationReached() {
		if (getSubState() == AISubState.WALK_ALIAS) {
			//TODO: If we reach our alias, we need to tell the mechanics?
			return getMoveController().isReachedAlias();
		}
		return super.isDestinationReached();
	}
	
	@Override
	protected void onIntentionToAttack(Creature creature) {
		if (getState() == AIState.FIGHT) {
			try {
				try {
					handleAttackIntention(chooseAttackIntention(creature), creature);
				} catch (BadTargetException t) {
					try {
						handleAttackIntention(chooseAttackIntention(t.newTarget), t.newTarget);
					} catch (BadTargetException t2) {
						//Should never happen; but, just in case, we'll go one layer deeper.
						try {
							handleAttackIntention(chooseAttackIntention(t.newTarget), t.newTarget);
						} catch (BadTargetException t3) {
							//Give up at this point, this shouldn't be possible.
							//If this does happen somehow, then maybe it's time the AI thinks about life.
							think();
						}
					}
				}
			} catch (NullPointerException nptr) {
				//This is a failsafe, so let's just clear out the queue.
				LOG.error("Failsafe triggered for Mechanic Handler: [" + getAIMechanics().mechanicId + "]. Clearing Mechanic Queue, Thinking.");
				attackIntentionQueue.clear();
				think();
			}
		}
	}
	
	@Override
	public AttackIntention chooseAttackIntention(Creature creature) {
		synchronized (attackIntentionQueue) {
			EventActionPair pair = attackIntentionQueue.poke();
			if (pair != null) switch (pair.action.type) {
				case despawn_self:
				case flee_from: {
					attackIntentionQueue.pop();
					try {
						pair.action.performAction(pair.event, this);
					} catch (Exception e) {
						LOG.error(pair.event + " <[" + pair.action + "]> failed by exception for mechanic: [" + getAIMechanics().mechanicId + "]!", e);
					}
					return AttackIntention.NONE;
				}
				case switch_target: {
					Creature intendedTarget = (Creature) pair.event.getObjectIndicator(((SwitchTargetAction) pair.action).target, this);
					return targetSwitch(pair.event, pair.action, intendedTarget, creature);
				}
				case switch_target_by_attacker_indicator: {
					Creature intendedTarget = (Creature) Action.getAttackerIndicator(((SwitchTargetByAttackerIndicatorAction) pair.action).target, this);
					return targetSwitch(pair.event, pair.action, intendedTarget, creature);
				}
				case switch_target_by_class_indicator: {
					Creature intendedTarget = (Creature) Action.getClassIndicator(((SwitchTargetByClassIndicatorAction) pair.action).target, this);
					return targetSwitch(pair.event, pair.action, intendedTarget, creature);
				}
				case spawn:
				case say_to_all:
				case spawn_on_target:
				case spawn_on_target_by_attacker_indicator: {
					attackIntentionQueue.pop();
					try {
						pair.action.performAction(pair.event, this);
					} catch (Exception e) {
						LOG.error(pair.event + " <[" + pair.action + "]> failed by exception for mechanic: [" + getAIMechanics().mechanicId + "]!", e);
					}
					return chooseAttackIntention(creature); //Recursive call
				}
				case attack_most_hating: {
					AttackMostHatingAction act = (AttackMostHatingAction) pair.action;
					Creature mostHated = getAggroList().getMostHated();
					return prepareSkill(act.skill, mostHated, creature);
				}
				case use_skill: {
					UseSkillAction act = (UseSkillAction) pair.action;
					Creature intendedTarget = (Creature) pair.event.getObjectIndicator(act.target, this);
					return prepareSkill(act.skill, intendedTarget, creature);
				}
				case use_skill_by_attacker_indicator: {
					UseSkillByAttackerIndicatorAction act = (UseSkillByAttackerIndicatorAction) pair.action;
					Creature intendedTarget = (Creature) Action.getAttackerIndicator(act.target, this);
					return prepareSkill(act.skill, intendedTarget, creature);
				}
				default:
					assert false:"Unsupported queue type: " + pair.action.type;
			}
			return super.chooseAttackIntention(creature);
		}
	}
	
	private AttackIntention targetSwitch(MechanicEvent event, Action action, Creature intendedTarget, Creature currentTarget) {
		attackIntentionQueue.pop();
		//No longer handled here, as this would result in targeting the new target way in advance.
//		try {
//			action.performAction(event, this);
//		} catch (Exception e) {
//			LOG.error(event + " <[" + action + "]> failed by exception for mechanic: [" + getAIMechanics().mechanicId + "]!", e);
//			return chooseAttackIntention(currentTarget); //Recursive call
//		}
		if (intendedTarget != currentTarget) {
			throw new BadTargetException(intendedTarget);
		} else {
			return chooseAttackIntention(currentTarget); //Recursive call
		}
	}
	
	protected final AttackIntention prepareSkill(SkillIndex skillIndex, Creature intendedTarget, Creature currentTarget) {
		attackIntentionQueue.incrementPullAttempts();
		if (attackIntentionQueue.isPastPullAttemptThreshold(5) || intendedTarget.getLifeStats().isAlreadyDead()) {
			//If we've already tried to use this skill 5 times or the target is dead, just give up trying to use it.
			attackIntentionQueue.pop();
			return super.chooseAttackIntention(currentTarget);
		}
		NpcSkillEntry skill = getSkillList().getSkill(skillIndex);
		if (skill == null) {
			//The skill may be null, but we can still auto attack the correct target.
			if (intendedTarget != currentTarget && !attackIntentionQueue.isPastPullAttemptThreshold(1)) {
//				AI2Actions.targetCreature(this, intendedTarget); //Handled elsewhere for now.
				throw new BadTargetException(intendedTarget);
			}
			
			attackIntentionQueue.pop();
			return AttackIntention.SIMPLE_ATTACK;
		}
		if (!skill.hasCountRemaining() || skill.hasCooldown()) {
			attackIntentionQueue.pop();
			try {
				return chooseAttackIntention(currentTarget);
			} catch (StackOverflowError e) {
				//This is no longer expected to happen, but I'll leave this here just in case.
				LOG.warn("Caught StackOverflowError within AbstractMechanicsAI2. Entity: " + getOwner().toString());
				return super.chooseAttackIntention(currentTarget);
			}
		}
		if (skill.getSkillTemplate().getSubType() == SkillSubType.BUFF
		&& skill.getSkillTemplate().getDuration() == 0 && intendedTarget.getEffectController().isAbnormalPresentBySkillId(skill.getSkillId())) {
			/*
			 * This would mean that the buff doesn't go away naturally, and the intended target already has it.
			 * Ignore the skill in the queue, and decide what else to do instead. Note that we haven't had a chance to
			 * throw a BadTargetException, so #onIntentionToAttack(Creature) won't have a chance to catch it three times.
			 */
			attackIntentionQueue.pop();
			return chooseAttackIntention(currentTarget); //Potentially a recursive call.
		}
		if (intendedTarget != currentTarget && !attackIntentionQueue.isPastPullAttemptThreshold(1)) {
//			AI2Actions.targetCreature(this, intendedTarget); //Handled elsewhere for now.
			throw new BadTargetException(intendedTarget);
		}
		skillId = skill.getSkillId();
		skillLevel = skill.getSkillLevel();
		if (skill.getSkillTemplate().getSubType() == SkillSubType.BUFF) {
			return AttackIntention.SKILL_BUFF;
		}
		return AttackIntention.SKILL_ATTACK;
	}
	
	@Override
	protected void handleAttackComplete() {
		synchronized (attackIntentionQueue) {
			if (!attackIntentionQueue.isEmpty()) {
				EventActionPair pair = attackIntentionQueue.poke();
				switch (pair.action.type) {
					case attack_most_hating: {
						AttackMostHatingAction act = (AttackMostHatingAction) pair.action;
						NpcSkillList list = getSkillList();
						if (list != null) {
							NpcSkillEntry skill = list.getSkill(act.skill);
							if (skill != null && skillId == skill.getSkillId()) {
								attackIntentionQueue.pop();
							}
						}
						break;
					}
					case use_skill: {
						UseSkillAction act = (UseSkillAction) pair.action;
						NpcSkillList list = getSkillList();
						if (list != null) {
							NpcSkillEntry skill = list.getSkill(act.skill);
							if (skill != null && skillId == skill.getSkillId()) {
								attackIntentionQueue.pop();
							}
						}
						break;
					}
					case use_skill_by_attacker_indicator: {
						UseSkillByAttackerIndicatorAction act = (UseSkillByAttackerIndicatorAction) pair.action;
						NpcSkillList list = getSkillList();
						if (list != null) {
							NpcSkillEntry skill = list.getSkill(act.skill);
							if (skill != null && skillId == skill.getSkillId()) {
								attackIntentionQueue.pop();
							}
						}
						break;
					}
					default: break;
				}
			}
			if (isNonFightingState()) {
				skillId = 0;
				skillLevel = 0;
				if (getTarget() != null) {
					AI2Actions.targetCreature(this, null);
				}
				notifyIdleQueue();
			} else {
				super.handleAttackComplete();
			}
		}
	}
	
	@Override
	public synchronized boolean setStateIfNot(AIState newState) {
		AIState oldState = getState();
		if (super.setStateIfNot(newState)) {
			if (oldState == AIState.CREATED) onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_leave_wakeup_state));
			switch (newState) {
				case FIGHT:
					if (oldState != AIState.WALKING && oldState != AIState.IDLE) return true;
					final Creature targ = (Creature) getOwner().getTarget();
					onMechanicEvent(new CreatureEvent(MechanicEventType.on_enter_attack_state, targ, null, targ));
					getOwner().getKnownList().doOnAllNpcs(new Visitor<Npc>() {
						@Override
						public void visit(Npc object) {
							if (object.getAi2() instanceof AbstractMechanicsAI2
							&& TribeRelationService.isSupport(getOwner(), object)
							&& MathUtil.isIn3dRange(getOwner(), object, object.getAggroRange())) {
								AbstractMechanicsAI2 mAI = (AbstractMechanicsAI2) object.getAi2();
								mAI.onMechanicEvent(new CreatureEvent(MechanicEventType.on_friend_enter_attack_state, targ, getOwner(), targ));
							}
						}
					});
					return true;
				case IDLE:
					onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_enter_idle_state));
					return true;
				case DESPAWNED:
					onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_despawn));
					return true;
				case RETURNING:
					onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_enter_return_sp));
					return true;
				case CREATED:
					onMechanicEvent(new GeneralMechanicEvent(MechanicEventType.on_enter_wakeup_state));
					return true;
				case DIED:
				case FEAR:
				case FOLLOWING:
				case WALKING:
					return true;
				default:
					assert false:"Unsupported AI State for AbstractMechanicsAI2";
					return true;
			}
		}
		return false;
	}
	
	private class BadTargetException extends RuntimeException {
		
		/**
		 * Declared for the sole purpose of preventing the JVM from calculating a value at startup.
		 */
		private static final long serialVersionUID = 1L;
		
		final Creature newTarget;
		
		public BadTargetException(Creature newTarget) {
			this.newTarget = newTarget;
		}
		
	}
	
}
