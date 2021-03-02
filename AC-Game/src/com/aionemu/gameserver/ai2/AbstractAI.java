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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.aionemu.commons.callbacks.metadata.ObjectCallback;
import com.aionemu.gameserver.ai2.event.AIEventLog;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.eventcallback.OnHandleAIGeneralEvent;
import com.aionemu.gameserver.ai2.handler.FollowEventHandler;
import com.aionemu.gameserver.ai2.handler.FreezeEventHandler;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.ai2.scenario.AI2Scenario;
import com.aionemu.gameserver.ai2.scenario.AI2Scenarios;
import com.aionemu.gameserver.configs.main.AIConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemAttackType;
import com.aionemu.gameserver.model.templates.npc.NpcRating;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.npcshout.ShoutEventType;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.skillengine.model.SkillType;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.world.WorldPosition;
import com.google.common.base.Preconditions;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- {@link #ask(AIQuestion)} modified to answer positive for heroic and above mobs when
 * being asked if they are immune to altered states, added {@link #isTargetInAttackRange(Creature)} for convenience (and replaced
 * a call to SimpleAttackManager's version).
 * @reworked Yon (Aion Reconstruction Project) -- Added methods for internal attack handling because the external method was
 * poorly implemented and it should be internal anyway; added {@link #handleAttackIntention(AttackIntention, Creature)},
 * {@link #handleSimpleAttackIntention(int, Creature)}, {@link #handleSkillAttackIntention(int, Creature)}, 
 * {@link #handleSkillBuffIntention(int, Creature)}, {@link #onIntentionToAttack(Creature)}, and 
 * {@link #canUseSkill()}.
 */
public abstract class AbstractAI implements AI2 {
	
	private Creature owner;
	private AIState currentState;
	private AISubState currentSubState;
	private final Lock thinkLock = new ReentrantLock();
	private boolean logging = false;
	protected int skillId;
	protected int skillLevel;
	private volatile AIEventLog eventLog;
	private AI2Scenario scenario;
	
	AbstractAI() {
		this.currentState = AIState.CREATED;
		this.currentSubState = AISubState.NONE;
		clearScenario();
	}
	
	public AI2Scenario getScenario() {
		return scenario;
	}
	
	public void setScenario(AI2Scenario scenario) {
		this.scenario = scenario;
	}
	
	public void clearScenario() {
		this.scenario = AI2Scenarios.NO_SCENARIO;
	}
	
	public AIEventLog getEventLog() {
		return eventLog;
	}
	
	@Override
	public AIState getState() {
		return currentState;
	}
	
	public final boolean isInState(AIState state) {
		return currentState == state;
	}
	
	@Override
	public AISubState getSubState() {
		return currentSubState;
	}
	
	public final boolean isInSubState(AISubState subState) {
		return currentSubState == subState;
	}
	
	@Override
	public String getName() {
		if (getClass().isAnnotationPresent(AIName.class)) {
			AIName annotation = getClass().getAnnotation(AIName.class);
			return annotation.value();
		}
		return "noname";
	}
	
	public int getSkillId() {
		return skillId;
	}
	
	public int getSkillLevel() {
		return skillLevel;
	}
	
	protected boolean canHandleEvent(AIEventType eventType) {
		switch (this.currentState) {
		case DESPAWNED:
			return StateEvents.DESPAWN_EVENTS.hasEvent(eventType);
		case DIED:
			return StateEvents.DEAD_EVENTS.hasEvent(eventType);
		case CREATED:
			return StateEvents.CREATED_EVENTS.hasEvent(eventType);
		default:
			break;
		}
		switch (eventType) {
		case DIALOG_START:
		case DIALOG_FINISH:
			return isNonFightingState();
		case CREATURE_MOVED:
			return getName().equals("trap") || currentState != AIState.FIGHT && isNonFightingState();
		default:
			break;
		}
		return true;
	}
	
	public boolean isNonFightingState() {
		return currentState == AIState.WALKING || currentState == AIState.IDLE;
	}
	
	public synchronized boolean setStateIfNot(AIState newState) {
		if (this.currentState == newState) {
			if (this.isLogging()) {
				AI2Logger.info(this, "Can't change state to " + newState + " from " + currentState);
			}
			return false;
		}
		if (this.isLogging()) {
			AI2Logger.info(this, "Setting AI state to " + newState);
			if (this.currentState == AIState.DIED && newState == AIState.FIGHT) {
				StackTraceElement[] stack = new Throwable().getStackTrace();
				for (StackTraceElement elem : stack) {
					AI2Logger.info(this, elem.toString());
				}
			}
		}
		this.currentState = newState;
		return true;
	}
	
	public synchronized boolean setSubStateIfNot(AISubState newSubState) {
		if (this.currentSubState == newSubState) {
			if (this.isLogging()) {
				AI2Logger.info(this, "Can't change substate to " + newSubState + " from " + currentSubState);
			}
			return false;
		}
		if (this.isLogging()) {
			AI2Logger.info(this, "Setting AI substate to " + newSubState);
		}
		this.currentSubState = newSubState;
		return true;
	}
	
	@Override
	public void onGeneralEvent(AIEventType event) {
		if (canHandleEvent(event)) {
			if (this.isLogging()) {
				AI2Logger.info(this, "General event " + event);
			}
			handleGeneralEvent(event);
		}
	}
	
	@Override
	public void onCreatureEvent(AIEventType event, Creature creature) {
		Preconditions.checkNotNull(creature, "Creature must not be null");
		if (canHandleEvent(event)) {
			if (this.isLogging()) {
				AI2Logger.info(this, "Creature event " + event + ": " + creature.getObjectTemplate().getTemplateId());
			}
			handleCreatureEvent(event, creature);
		}
	}
	
	@Override
	public void onCustomEvent(int eventId, Object... args) {
		if (this.isLogging()) {
			AI2Logger.info(this, "Custom event - id = " + eventId);
		}
		handleCustomEvent(eventId, args);
	}
	
	/**
	 * Will be hidden for all AI's below NpcAI2
	 *
	 * @return
	 */
	public Creature getOwner() {
		return owner;
	}
	
	public int getObjectId() {
		return owner.getObjectId();
	}
	
	public WorldPosition getPosition() {
		return owner.getPosition();
	}
	
	public VisibleObject getTarget() {
		return owner.getTarget();
	}
	
	public boolean isTargetInAttackRange(Creature target) {
		if (target == null) return false;
		Creature owner = getOwner();
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (logging) {
			float x1 = owner.getX(), y1 = owner.getY(), z1 = owner.getZ(),
				  b1 = owner.getObjectTemplate().getBoundRadius().getCollision(),
				  x2 = target.getX(), y2 = target.getY(), z2 = target.getZ(),
				  b2 = target.getObjectTemplate().getBoundRadius().getCollision(),
				  distance = (float) MathUtil.getDistance(x1, y1, z1, x2, y2, z2) - b1 - b2;
			AI2Logger.info(this, "isTargetInAttackRange: " + distance);
		}
		if (template != null) {
			return MathUtil.isInAttackRange(owner, target, template.getProperties().getFirstTargetRange());
		} else return MathUtil.isInAttackRange(owner, target, owner.getGameStats().getAttackRange().getCurrent() / 1000f);
	}
	
	public boolean isAlreadyDead() {
		return owner.getLifeStats().isAlreadyDead();
	}
	
	void setOwner(Creature owner) {
		this.owner = owner;
	}
	
	public final boolean tryLockThink() {
		return thinkLock.tryLock();
	}
	
	public final void unlockThink() {
		thinkLock.unlock();
	}
	
	@Override
	public final boolean isLogging() {
		return logging;
	}
	
	public void setLogging(boolean logging) {
		this.logging = logging;
	}
	
	protected abstract void handleActivate();
	
	protected abstract void handleDeactivate();
	
	protected abstract void handleSpawned();
	
	protected abstract void handleRespawned();
	
	protected abstract void handleDespawned();
	
	protected abstract void handleDied();
	
	protected abstract void handleMoveValidate();
	
	/**
	 * Called to handle what this AI's owner should do after moving to its destination.
	 * <p>
	 * For cases where this AI's owner has arrived at its target (different from a
	 * specific point destination) see {@link #handleTargetReached()}.
	 */
	protected abstract void handleMoveArrived();
	
	/**
	 * Called to handle what this AI's owner should do after completing the last attack.
	 * In most cases, this method should also schedule the next attack that the owner
	 * of this AI should perform.
	 * <p>
	 * Note that this is NOT the method called when this AI's owner is done fighting
	 * its attackers, see {@link #handleFinishAttack()} for that.
	 */
	protected abstract void handleAttackComplete();
	
	/**
	 * Called to handle what this AI's owner should do after the owner has completed fighting.
	 */
	protected abstract void handleFinishAttack();
	
	/**
	 * Called to handle what this AI's owner should do after moving to its target.
	 * <p>
	 * For cases where this AI's owner has arrived at a point that it was walking to
	 * (different from to its target) see {@link #handleMoveArrived()}.
	 */
	protected abstract void handleTargetReached();
	
	protected abstract void handleTargetTooFar();
	
	protected abstract void handleTargetGiveup();
	
	protected abstract void handleNotAtHome();
	
	protected abstract void handleBackHome();
	
	protected abstract void handleDropRegistered();
	
	/**
	 * Called to handle what this AI's owner should do upon being attacked by the given Creature.
	 * 
	 * @param creature -- The Creature attacking this AI's owner.
	 */
	protected abstract void handleAttack(Creature creature);
	
	/**
	 * This method will begin the process of responding to the given AttackIntention.
	 * The given intention will be handled as follows:
	 * <p>
	 * <ul>
	 *   <li>FINISH_ATTACK and SWITCH_TARGET
	 *     <ul>Both of these intentions will call {@link #think()}</ul>
	 *   <li>SIMPLE_ATTACK
	 *     <ul>Calls {@link #handleSimpleAttackIntention(int)}</ul>
	 *   <li>SKILL_ATTACK
	 *     <ul>Calls {@link #handleSkillAttackIntention(int)} if {@link #selectNextSkill(AttackIntention)}
	 *     returns true, {@link #handleSimpleAttackIntention(int)} otherwise</ul>
	 *   <li>SKILL_BUFF
	 *     <ul>Calls {@link #handleSkillBuffIntention(int)} if {@link #selectNextSkill(AttackIntention)}
	 *     returns true, {@link #handleSimpleAttackIntention(int)} otherwise</ul>
	 * </ul>
	 * <p>
	 * Note that it's assumed this AI can think, as it is attacking, and that the
	 * current target is the target of the given intention.
	 * <p>
	 * The default delay passed along into the next method calls is 2000 (2 seconds); AI's for
	 * Npc's should override this method and specify a delay based on the Npc's next attack interval.
	 * 
	 * @param intention -- The AttackIntention to handle.
	 */
	protected void handleAttackIntention(AttackIntention intention, Creature creature) {
		AI2Logger.info(this, "handleAttackIntention(" + intention + ")");
		// don't start attack while in casting substate
		if (currentSubState != AISubState.NONE) {
			AI2Logger.info(this, "Will not handle attack intention in substate: " + currentSubState);
			return;
		}
		int delay = 2000; //Default delay, this method should be overridden to have a real one.
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
	
	/**
	 * Handles basic auto attacking upon the current target. This method should ensure this AI
	 * is not attacking itself (unless intentional).
	 * <p>
	 * This method should schedule the next attack on the ThreadPoolManager if there is any
	 * delay.
	 * 
	 * @param delay -- When the actual attack should occur from now (in milliseconds).
	 * @param creature -- The target of this intention.
	 */
	protected abstract void handleSimpleAttackIntention(final int delay, final Creature creature);

	/**
	 * Handles basic skill attacking upon the current target. This method should ensure this AI
	 * is not targeting itself (unless intentional).
	 * <p>
	 * This method should schedule the next attack on the ThreadPoolManager if there is any
	 * delay.
	 * <p>
	 * Note that upon using the skill, the NpcSkillEntry from the owner's SkillList should have
	 * the last use time of that skill updated.
	 * 
	 * @param delay -- When the actual use of the skill should occur from now (in milliseconds);
	 * this is not cast time duration, but when to start the cast.
	 * @param creature -- The target of this intention.
	 */
	protected abstract void handleSkillAttackIntention(final int delay, final Creature creature);
	
	/**
	 * Handles basic skill buffing upon the current target. This method should ensure this AI
	 * is targeting the correct entity for the buff (unless intentionally random).
	 * <p>
	 * This method should schedule the use of the buff on the ThreadPoolManager if there is any
	 * delay.
	 * <p>
	 * Note that upon using the skill, the NpcSkillEntry from the owner's SkillList should have
	 * the last use time of that skill updated.
	 * 
	 * @param delay -- When the actual use of the buff should occur from now (in milliseconds);
	 * this is not cast time duration, but when to start the cast.
	 * @param creature -- The target of this intention.
	 */
	protected abstract void handleSkillBuffIntention(final int delay, final Creature creature);
	
	protected abstract boolean handleCreatureNeedsSupport(Creature creature);
	
	protected abstract boolean handleGuardAgainstAttacker(Creature creature);
	
	protected abstract void handleCreatureSee(Creature creature);
	
	protected abstract void handleCreatureNotSee(Creature creature);
	
	protected abstract void handleCreatureMoved(Creature creature);
	
	protected abstract void handleCreatureAggro(Creature creature);
	
	protected abstract void handleTargetChanged(Creature creature);
	
	protected abstract void handleFollowMe(Creature creature);
	
	protected abstract void handleStopFollowMe(Creature creature);
	
	protected abstract void handleDialogStart(Player player);
	
	protected abstract void handleDialogFinish(Player player);
	
	protected abstract void handleCustomEvent(int eventId, Object... args);
	
	public abstract boolean onPatternShout(ShoutEventType event, String pattern, int skillNumber);
	
	@ObjectCallback(OnHandleAIGeneralEvent.class)
	protected void handleGeneralEvent(AIEventType event) {
		if (this.isLogging()) {
			AI2Logger.info(this, "Handle general event " + event);
		}
		logEvent(event);
		switch (event) {
		case MOVE_VALIDATE:
			handleMoveValidate();
			break;
		case MOVE_ARRIVED:
			handleMoveArrived();
			break;
		case SPAWNED:
			handleSpawned();
			break;
		case RESPAWNED:
			handleRespawned();
			break;
		case DESPAWNED:
			handleDespawned();
			break;
		case DIED:
			handleDied();
			break;
		case ATTACK_COMPLETE:
			handleAttackComplete();
			break;
		case ATTACK_FINISH:
			handleFinishAttack();
			break;
		case TARGET_REACHED:
			handleTargetReached();
			break;
		case TARGET_TOOFAR:
			handleTargetTooFar();
			break;
		case TARGET_GIVEUP:
			handleTargetGiveup();
			break;
		case NOT_AT_HOME:
			handleNotAtHome();
			break;
		case BACK_HOME:
			handleBackHome();
			break;
		case ACTIVATE:
			handleActivate();
			break;
		case DEACTIVATE:
			handleDeactivate();
			break;
		case FREEZE:
			FreezeEventHandler.onFreeze(this);
			break;
		case UNFREEZE:
			FreezeEventHandler.onUnfreeze(this);
			break;
		case DROP_REGISTERED:
			handleDropRegistered();
			break;
		default:
			break;
		}
	}
	
	/**
	 * @param event
	 */
	protected void logEvent(AIEventType event) {
		if (AIConfig.EVENT_DEBUG) {
			if (eventLog == null) {
				synchronized (this) {
					if (eventLog == null) {
						eventLog = new AIEventLog(10);
					}
				}
			}
			eventLog.addFirst(event);
		}
	}
	
	protected void handleCreatureEvent(AIEventType event, Creature creature) {
		switch (event) {
		case ATTACK:
			if (DataManager.TRIBE_RELATIONS_DATA.isFriendlyRelation(getOwner().getTribe(), creature.getTribe())) {
				return;
			}
			handleAttack(creature);
			logEvent(event);
			break;
		case CREATURE_NEEDS_SUPPORT:
			if (!handleCreatureNeedsSupport(creature)) {
				if (creature.getTarget() instanceof Creature) {
					if (!handleCreatureNeedsSupport((Creature) creature.getTarget()) && !handleGuardAgainstAttacker(creature)) {
						handleGuardAgainstAttacker((Creature) creature.getTarget());
					}
				}
			}
			logEvent(event);
			break;
		case CREATURE_SEE:
			handleCreatureSee(creature);
			break;
		case CREATURE_NOT_SEE:
			handleCreatureNotSee(creature);
			break;
		case CREATURE_MOVED:
			handleCreatureMoved(creature);
			break;
		case CREATURE_AGGRO:
			handleCreatureAggro(creature);
			logEvent(event);
			break;
		case TARGET_CHANGED:
			handleTargetChanged(creature);
			break;
		case FOLLOW_ME:
			handleFollowMe(creature);
			logEvent(event);
			break;
		case STOP_FOLLOW_ME:
			handleStopFollowMe(creature);
			logEvent(event);
			break;
		case DIALOG_START:
			handleDialogStart((Player) creature);
			logEvent(event);
			break;
		case DIALOG_FINISH:
			handleDialogFinish((Player) creature);
			logEvent(event);
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean poll(AIQuestion question) {
		AIAnswer instanceAnswer = pollInstance(question);
		if (instanceAnswer != null) {
			return instanceAnswer.isPositive();
		}
		switch (question) {
			case DESTINATION_REACHED:
				return isDestinationReached();
			case CAN_SPAWN_ON_DAYTIME_CHANGE:
				return isCanSpawnOnDaytimeChange();
			case CAN_SHOUT:
				return isMayShout();
			default:
				break;
		}
		return false;
	}
	
	/**
	 * Poll concrete AI instance for the answer.
	 *
	 * @param question
	 * @return null if there is no specific answer
	 */
	protected AIAnswer pollInstance(AIQuestion question) {
		return null;
	}
	
	@Override
	public AIAnswer ask(AIQuestion question) {
		if (question == AIQuestion.CAN_RESIST_ABNORMAL) {
			if (getOwner().getObjectTemplate() instanceof NpcTemplate) {
				NpcTemplate template = (NpcTemplate) getOwner().getObjectTemplate();
				if (NpcRating.HERO.compareTo(template.getRating()) <= 0) return AIAnswers.POSITIVE;
			}
		}
		return AIAnswers.NEGATIVE;
	}
	
	protected boolean isDestinationReached() {
		AIState state = currentState;
		switch (state) {
		case FEAR:
			return MathUtil.isNearCoordinates(getOwner(), owner.getMoveController().getTargetX2(), owner.getMoveController().getTargetY2(), owner.getMoveController().getTargetZ2(), 1);
		case FIGHT:
			return isTargetInAttackRange((Creature) getTarget());
		case RETURNING:
			SpawnTemplate spawn = getOwner().getSpawn();
			return MathUtil.isNearCoordinates(getOwner(), spawn.getX(), spawn.getY(), spawn.getZ(), 1);
		case FOLLOWING:
			return FollowEventHandler.isInRange(this, getOwner().getTarget());
		case WALKING:
			return currentSubState == AISubState.TALK || WalkManager.isArrivedAtPoint((NpcAI2) this);
		default:
			break;
		}
		return true;
	}
	
	protected boolean isCanSpawnOnDaytimeChange() {
		return currentState == AIState.DESPAWNED || currentState == AIState.CREATED;
	}
	
	public abstract boolean isMayShout();
	
	/**
	 * This method should only be called when scheduling the next attack.
	 * This AI will handle attacking the passed in Creature. Handling is implementation specific.
	 * <p>
	 * The default implementation will simply call {@link #handleAttackIntention(AttackIntention, Creature)}
	 * if this AI's {@link #currentState} is {@link AIState#FIGHT} (passing in the result of
	 * {@link #chooseAttackIntention(Creature)}), assuming all target handling has already been completed
	 * by the caller (and that the given creature is the target).
	 * 
	 * @param creature -- The Creature for this AI to attack.
	 */
	protected void onIntentionToAttack(Creature creature) {
		if (currentState == AIState.FIGHT) {
			handleAttackIntention(chooseAttackIntention(creature), creature);
		}
	}
	
	public abstract AttackIntention chooseAttackIntention(Creature creature);
	
//	/**
//	 * Called when the AI wants to know what skill it should use at a given time
//	 * for the given AttackIntention. Returns true if a skill has been selected and
//	 * is ready for use by this AI.
//	 * <p>
//	 * If a skill can be used, the {@link #skillId} and {@link #skillLevel} should
//	 * be set by this method. If the skill cannot be used, the skillId and skillLevel
//	 * will be set to zero or left as-is depending on the implementation.
//	 *  
//	 * This method is currently unused, replaced by {@link #canUseSkill()}.
//	 * 
//	 * @param intention -- The AttackIntention of this AI's next attack action.
//	 * @param creature -- The target of the AttackIntention.
//	 * @return true if a skill has been selected and prepared, false otherwise.
//	 */
//	protected abstract boolean selectNextSkill(AttackIntention intention, Creature creature);
	
	/**
	 * Called just before using a skill; This method will perform basic checks.
	 * <p>
	 * If the {@link #skillId} is present in this AI's skill list and there are any
	 * basic reasons why that skill cannot be used at this time (silence, bind, fear),
	 * this method will return false.
	 * <p>
	 * If the skill cannot be used, the current skillId and skillLevel will be set to 0.
	 * 
	 * @return true if the skill denoted by {@link #skillLevel} is available.
	 */
	protected final boolean canUseSkill() {
		if (owner instanceof Npc && !((Npc) owner).getSkillList().isSkillPresent(skillId)) {
			skillId = 0;
			skillLevel = 0;
			return false;
		}
		
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (template == null) {
			AI2Logger.info(this, "Skill lookup failed for skillId: " + skillId + "!");
			skillId = 0;
			skillLevel = 0;
			return false;
		}
		
		if (template.getType() == SkillType.MAGICAL && owner.getEffectController().isAbnormalSet(AbnormalState.SILENCE)) {
			skillId = 0;
			skillLevel = 0;
			return false;
		}
		
		if (template.getType() == SkillType.PHYSICAL && owner.getEffectController().isAbnormalSet(AbnormalState.BIND)) {
			skillId = 0;
			skillLevel = 0;
			return false;
		}
		
		if (!owner.canAttack()) {
			skillId = 0;
			skillLevel = 0;
			return false;
		}
		
		//Checked above with CANT_ATTACK_STATE in method call
//		if (owner.getEffectController().isUnderFear()) {
//			skillId = 0;
//			skillLevel = 0;
//			return false;
//		}
		
		return true;
	}
	
	@Override
	public boolean onDialogSelect(Player player, int dialogId, int questId, int extendedRewardIndex) {
		return false;
	}
	
	@Override
	public long getRemainigTime() {
		return 0;
	}
	
	/**
	 * Spawn object in the same world and instance as AI's owner
	 */
	protected VisibleObject spawn(int npcId, float x, float y, float z, byte heading) {
		return spawn(owner.getWorldId(), npcId, x, y, z, heading, 0, getPosition().getInstanceId());
	}
	
	/**
	 * Spawn object with staticId in the same world and instance as AI's owner
	 */
	protected VisibleObject spawn(int npcId, float x, float y, float z, byte heading, int staticId) {
		return spawn(owner.getWorldId(), npcId, x, y, z, heading, staticId, getPosition().getInstanceId());
	}
	
	protected VisibleObject spawn(int worldId, int npcId, float x, float y, float z, byte heading, int staticId, int instanceId) {
		SpawnTemplate template = SpawnEngine.addNewSingleTimeSpawn(worldId, npcId, x, y, z, heading);
		template.setStaticId(staticId);
		return SpawnEngine.spawnObject(template, instanceId);
	}
	
	@Override
	public int modifyDamage(int damage) {
		return damage;
	}
	
	@Override
	public int modifyOwnerDamage(int damage) {
		return damage;
	}
	
	@Override
	public int modifyHealValue(int value) {
		return value;
	}
	
	@Override
	public int modifyMaccuracy(int value) {
		return value;
	}
	
	@Override
	public ItemAttackType modifyAttackType(ItemAttackType type) {
		return type;
	}
	
	@Override
	public int modifyARange(int value) {
		return value;
	}
}
