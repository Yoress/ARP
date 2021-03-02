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
package com.aionemu.gameserver.ai2.ai.instance.fire_temple;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.AIState;
import com.aionemu.gameserver.ai2.AISubState;
import com.aionemu.gameserver.ai2.AbstractAI;
import com.aionemu.gameserver.ai2.AttackIntention;
import com.aionemu.gameserver.ai2.ai.AggressiveNpcAI2;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.manager.EmoteManager;
import com.aionemu.gameserver.ai2.manager.WalkManager;
import com.aionemu.gameserver.ai2.poll.AIAnswer;
import com.aionemu.gameserver.ai2.poll.AIAnswers;
import com.aionemu.gameserver.ai2.poll.AIQuestion;
import com.aionemu.gameserver.controllers.NpcController;
import com.aionemu.gameserver.controllers.effect.EffectController;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.Trap;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.stats.calc.StatOwner;
import com.aionemu.gameserver.model.stats.calc.functions.IStatFunction;
import com.aionemu.gameserver.model.stats.calc.functions.StatRateFunction;
import com.aionemu.gameserver.model.stats.container.StatEnum;
import com.aionemu.gameserver.model.templates.npc.NpcTemplate;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_STATE;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.idfactory.IDFactory;
import com.aionemu.gameserver.world.knownlist.NpcKnownList;

import javolution.util.FastList;

/**
 * This AI is intended only for the version of Kromede in the Fire Temple
 * (NPC ID: 212846 (Kromede the Corrupt) and 214621 (Vile Judge Kromede)).
 * <p>
 * This AI implements Kromede's mechanics as retail-like as possible (from memory); Kromede has access to the following skills:<br>
 * <BLOCKQUOTE><table border=1>
 * <col width="60%"/>
 * <col width="20%"/>
 * <col width="20%"/>
 * <thead>
 *   <tr><th Align="left">Skill Name</th><th Align="left">Skill ID</th><th Align="left">Skill Level</th></tr>
 * </thead>
 * <tbody>
 *   <tr><td>Guilty Verdict</td><td>16674</td><td>28</td></tr>
 *   <tr><td>Repeat Impact Strikes</td><td>16847</td><td>28</td></tr>
 *   <tr><td>Strong Cry</td><td>17047</td><td>28</td></tr>
 *   <tr><td>Blessing of Rock</td><td>17052</td><td>28</td></tr>
 *   <tr><td>Miserable Struggle</td><td>17056</td><td>28</td></tr>
 * </tbody>
 * </table></BLOCKQUOTE>
 * <p>
 * An explanation of Kromede's mechanics:<br>
 * <UL>
 *   <LI>On spawn, Kromede uses Blessing of Rock; if this is dispelled, she will cast it again at 75% HP, 50% HP, and 25% HP as
 *   the first skill in her skill cycle described below.</LI>
 *   <LI>Upon aggro, Kromede runs to point-blank range of whoever aggros her.</LI>
 *   <LI>Kromede then uses Strong Cry (this is the ONLY time this skill is used).</LI>
 *   <LI>Kromede then uses Repeat Impact Strikes (this is the ONLY time this skill is used).</LI>
 *   <LI>Kromede then proceeds to auto attack until her HP drops to 75%.</LI>
 *   <LI>Kromede then begins to use Guilty Verdict on a cycle of 30s or so (targeting herself as she does).</LI>
 *   <LI>Kromede spawns 3 "Suspicious Object" Traps (NPC ID: 280501) randomly around her after each use of Guilty Verdict.</LI>
 *   <LI>Kromede repeats her Guilty Verdict cycle until 30% HP, where upon the next cycle she will use Miserable Struggle
 *         (this is the ONLY time this skill is used) before continuing to use Guilty Verdict as usual.</LI>
 * </UL>
 * <p>
 * In case it was unclear: when Kromede uses the skills Guilty Verdict or Miserable Struggle she does so on herself (self-target);
 * this is intentionally done so players can avoid these skills.
 * <p>
 * For the sake of Kromede not appearing like an idiot, the traps she sets will be restricted to areas near the
 * players attacking her (within 7m); this is to prevent sheer randomness from spawning traps too far away from
 * the attacking players to even activate.
 * <p>
 * To allow Server owners some small amount of control, this AI will check for the presence of each skill within
 * the owner's SkillList before actually using them. While the listed skills will not change, this will allow
 * Server owners to remove the skills from the fight via static data modification (no programming knowledge needed).
 * The skill level specified in the SkillList will also be applied programmatically for the same reason.
 * <p>
 * Because there is no known skill to spawn the traps that Kromede sets, the spawning of these traps will be hard-coded
 * to follow the cast of Guilty Verdict; due to this, it will not be possible for Server owners to remove the traps from
 * the fight without also removing Guilty Verdict, or modifying this AI.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@AIName("ftkromede")
public class KromedeAI2 extends AggressiveNpcAI2 implements StatOwner {
	
	/**
	 * This will be used in place of multiple boolean values to describe how far along Kromede has progressed in the fight.
	 * <p>
	 * The initial value is 0, and means the fight has not yet started.
	 * <p>
	 * The value will be changed to 1 after Kromede manages to run to a point-blank range of her initial aggro target.
	 * <p>
	 * The value will be changed to 2 after Kromede has used Strong Cry.
	 * <p>
	 * The value will be changed to 3 after Kromede has used Repeat Impact Strikes.
	 * <p>
	 * The value will be changed to 4 when Kromede has started her Guilty Verdict Cycle.
	 * <p>
	 * The value will be changed to 5 after Kromede has used Miserable Struggle.
	 * <p>
	 * A value of 5 means that Kromede has completed all phases of her fight, and will only continue to recast
	 * Guilty Verdict on her normal cycle.
	 */
	private int phase = 0;
	
	/**
	 * Used to keep track of how many times Kromede has attempted to recast her Blessing of Rock while fighting.
	 * <p>
	 * She will not try to refresh her buff unless it has been dispelled, but this value needs to
	 * increment each time she checks if she should refresh it (otherwise she will refresh it more than once
	 * in quick succession).
	 */
	private int blessingCount = 0;
	
	/**
	 * This value tracks the last time Kromede has used her Guilty Verdict Cycle.
	 * <p>
	 * When this AI ticks, the next attack in {@link #phase phase 4} or higher will
	 * compare the current time then, to this value. If 30s has passed, the next cycle will start.
	 */
	private long lastCycleTime = System.currentTimeMillis();
	
	/**
	 * An executable task that will spawn 3 traps on the ground in a triangle around a random player, or around a point in
	 * the direction of the most hated creature.
	 * <p>
	 * this sets itself to null upon execution, and should be synchronized when referenced,
	 * along with null pointer checks.
	 */
	private Runnable trapTask = null;
	
	/**
	 * Skill ID available to Kromede
	 */
	public final static int GUILTY_VERDICT = 16674,
							REPEAT_IMPACT_STRIKES = 16847,
							STRONG_CRY = 17047,
							BLESSING_OF_ROCK = 17052,
							MISERABLE_STRUGGLE = 17056;
	
	/**
	 * Checks if Guilty Verdict is available to the owner and returns {@link AttackIntention#SKILL_ATTACK}
	 * if it is after setting this AI's {@link AbstractAI#skillId skillId}
	 * and {@link AbstractAI#skillLevel skillLevel}.
	 * <p>
	 * If the Guilty Verdict is available, this method also calls {@link #spawnTraps()}.
	 *  
	 * @return {@link AttackIntention#SKILL_ATTACK} if the skill is available,
	 * {@link AttackIntention#SIMPLE_ATTACK} otherwise.
	 */
	private AttackIntention guiltyVerdict() {
		if (getSkillList().isSkillPresent(GUILTY_VERDICT)) {
//			AI2Actions.targetSelf(this); //Moved into #handleAttack()
			skillId = GUILTY_VERDICT;
			skillLevel = getSkillList().getSkillLevel(GUILTY_VERDICT);
			spawnTraps();
			return AttackIntention.SKILL_ATTACK;
		}
		return AttackIntention.SIMPLE_ATTACK;
	}
	
	/**
	 * Checks if Repeat Impact Strikes is available to the owner and returns {@link AttackIntention#SKILL_ATTACK}
	 * if it is after setting this AI's {@link AbstractAI#skillId skillId}
	 * and {@link AbstractAI#skillLevel skillLevel}.
	 * 
	 * @return {@link AttackIntention#SKILL_ATTACK} if the skill is available,
	 * {@link AttackIntention#SIMPLE_ATTACK} otherwise.
	 */
	private AttackIntention repeatImpactStrikes() {
		if (getSkillList().isSkillPresent(REPEAT_IMPACT_STRIKES)) {
			skillId = REPEAT_IMPACT_STRIKES;
			skillLevel = getSkillList().getSkillLevel(REPEAT_IMPACT_STRIKES);
			return AttackIntention.SKILL_ATTACK;
		}
		return AttackIntention.SIMPLE_ATTACK;
	}
	
	/**
	 * Checks if Strong Cry is available to the owner and returns {@link AttackIntention#SKILL_ATTACK}
	 * if it is after setting this AI's {@link AbstractAI#skillId skillId}
	 * and {@link AbstractAI#skillLevel skillLevel}.
	 * 
	 * @return {@link AttackIntention#SKILL_ATTACK} if the skill is available,
	 * {@link AttackIntention#SIMPLE_ATTACK} otherwise.
	 */
	private AttackIntention strongCry() {
		if (getSkillList().isSkillPresent(STRONG_CRY)) {
			skillId = STRONG_CRY;
			skillLevel = getSkillList().getSkillLevel(STRONG_CRY);
			return AttackIntention.SKILL_ATTACK;
		}
		return AttackIntention.SIMPLE_ATTACK;
	}
	
	/**
	 * Checks if Blessing of Rock is available to the owner and returns {@link AttackIntention#SKILL_BUFF}
	 * if it is after setting this AI's {@link AbstractAI#skillId skillId}
	 * and {@link AbstractAI#skillLevel skillLevel}.
	 * 
	 * @return {@link AttackIntention#SKILL_BUFF} if the skill is available,
	 * {@link AttackIntention#SIMPLE_ATTACK} otherwise.
	 */
	private AttackIntention blessingOfRock() {
		/*
		 * Note that Kromede needs to target herself for this to function, but as this method
		 * is used less as an AttackIntention, and more like a boolean it doesn't need to be
		 * handled here.
		 */
		if (getSkillList().isSkillPresent(BLESSING_OF_ROCK) && !getOwner().getEffectController().isAbnormalPresentBySkillId(BLESSING_OF_ROCK)) {
//			AI2Actions.targetSelf(this);
			skillId = BLESSING_OF_ROCK;
			skillLevel = getSkillList().getSkillLevel(BLESSING_OF_ROCK);
			return AttackIntention.SKILL_BUFF;
		}
		return AttackIntention.SIMPLE_ATTACK;
	}
	
	/**
	 * Checks if Miserable Struggle is available to the owner and returns {@link AttackIntention#SKILL_ATTACK}
	 * if it is after self-targeting and setting this AI's {@link AbstractAI#skillId skillId}
	 * and {@link AbstractAI#skillLevel skillLevel}.
	 * 
	 * @return {@link AttackIntention#SKILL_ATTACK} if the skill is available,
	 * {@link AttackIntention#SIMPLE_ATTACK} otherwise.
	 */
	private AttackIntention miserableStruggle() {
		if (getSkillList().isSkillPresent(MISERABLE_STRUGGLE)) {
			AI2Actions.targetSelf(this);
			skillId = MISERABLE_STRUGGLE;
			skillLevel = getSkillList().getSkillLevel(MISERABLE_STRUGGLE);
			return AttackIntention.SKILL_ATTACK;
		}
		return AttackIntention.SIMPLE_ATTACK;
	}
	
	/**
	 * Checks if this AI is {@link #isReadyForNextSkillCycle() ready for the next skill cycle},
	 * and returns {@link AttackIntention#SKILL_ATTACK} if it is after setting this AI's
	 * {@link AbstractAI#skillId skillId} and {@link AbstractAI#skillLevel skillLevel}, or
	 * returns {@link AttackIntention#SKILL_BUFF} if Kromede will recast Blessing of Rock.
	 * <p>
	 * If the next cycle is not ready, this method will not change the skillId nor skillLevel,
	 * and will return {@link AttackIntention#SIMPLE_ATTACK}.
	 * 
	 * @return {@link AttackIntention#SKILL_ATTACK} if it's time to use the next cycle,
	 * {@link AttackIntention#SKILL_BUFF} if Kromede has recast Blessing of Rock,
	 * {@link AttackIntention#SIMPLE_ATTACK} otherwise.
	 */
	private AttackIntention skillCycle() {
		if (isReadyForNextSkillCycle()) {
			if (checkBlessing()) {
				return AttackIntention.SKILL_BUFF;
			}
			if (phase == 3) {
				return guiltyVerdict();
			}
			if (phase == 4 && getOwner().getLifeStats().getHpPercentage() <= ((getOwner().getNpcId() == 214621) ? 20 : 30)) {
				return miserableStruggle();
			}
			if (phase == 4) {
				return guiltyVerdict();
			}
			if (phase == 5) {
				return guiltyVerdict();
			}
		}
		return AttackIntention.SIMPLE_ATTACK;
	}
	
	/**
	 * Called when preparing to cast Guilty Verdict, this method creates {@link #trapTask} which
	 * spawns 3 traps on the ground in a triangle around a random player, or around a point in
	 * the direction of the most hated creature.
	 * <p>
	 * {@link #trapTask} sets itself to null upon execution, and should be synchronized when referenced,
	 * along with null pointer checks.
	 */
	private void spawnTraps() {
		trapTask = new Runnable() {
			public Runnable init(AbstractAI npcAI2) {
				this.npcAI2 = npcAI2;
				return this;
			}
			
			AbstractAI npcAI2;
			AtomicBoolean safety = new AtomicBoolean(true);
			
			public void run() {
				trapTask = null;
				if (!safety.getAndSet(false)) return;
				ArrayList<Player> randomTargetList = new ArrayList<Player>(6);
				Creature target = getOwner().getAggroList().getMostHated();
				for (Player p: getOwner().getKnownList().getKnownPlayers().values()) {
					if (isInRange(p, 7) && getOwner().canSee(p)) {
						randomTargetList.add(p);
					}
				}
				
				if (!randomTargetList.isEmpty()) {
					target = randomTargetList.get(Rnd.get(0, randomTargetList.size() - 1));
				}
				
				float[] center;
				if (isInRange(target, 5)) {
					center = new float[] {target.getX(), target.getY(), target.getZ()};
				} else {
					int angle = ((int) MathUtil.estimateHeadingFrom(getOwner(), target)) * 3;
					float dx = (float) (5F*Math.cos(Math.toRadians(angle))), dy = (float) (5F*Math.sin(Math.toRadians(angle)));
					center = new float[] {getOwner().getX() + dx, getOwner().getY() + dy, getOwner().getZ()};
				}
				
				int angle = ((int) Rnd.get(0, 359));
				float dx = (float) (2F*Math.cos(Math.toRadians(angle))), dy = (float) (2F*Math.sin(Math.toRadians(angle)));
				float[] t1 = new float[] {center[0] + dx, center[1] + dy, center[2]}; //trap1 position
				
				angle += 120;
				dx = (float) (2F*Math.cos(Math.toRadians(angle))); dy = (float) (2F*Math.sin(Math.toRadians(angle)));
				float[] t2 = new float[] {center[0] + dx, center[1] + dy, center[2]}; //trap2 position
				
				angle += 120;
				dx = (float) (2F*Math.cos(Math.toRadians(angle))); dy = (float) (2F*Math.sin(Math.toRadians(angle)));
				float[] t3 = new float[] {center[0] + dx, center[1] + dy, center[2]}; //trap3 position
				
				SpawnTemplate trap1template = SpawnEngine.addNewSingleTimeSpawn(getOwner().getWorldId(), 280501, t1[0], t1[1], t1[2], (byte) Rnd.get(0, 119));
				SpawnTemplate trap2template = SpawnEngine.addNewSingleTimeSpawn(getOwner().getWorldId(), 280501, t2[0], t2[1], t2[2], (byte) Rnd.get(0, 119));
				SpawnTemplate trap3template = SpawnEngine.addNewSingleTimeSpawn(getOwner().getWorldId(), 280501, t3[0], t3[1], t3[2], (byte) Rnd.get(0, 119));
				
				final Trap trap1 = spawnIndividualTrap(trap1template, getOwner().getInstanceId(), getOwner());
				final Trap trap2 = spawnIndividualTrap(trap2template, getOwner().getInstanceId(), getOwner());
				final Trap trap3 = spawnIndividualTrap(trap3template, getOwner().getInstanceId(), getOwner());
				
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						//If traps haven't activated and deleted themselves after 10 seconds, just remove them.
						trap1.getController().onDelete();
						trap2.getController().onDelete();
						trap3.getController().onDelete();
					}
				}, 10000);
				
				AI2Actions.targetCreature(npcAI2, getOwner().getAggroList().getMostHated());
			}
		}.init(this);
	}
	
	/**
	 * Similar to VisibleObjectSpawner#SpawnTrap(), this method creates a Trap from the given
	 * spawn template and brings it into the world.
	 * <p>
	 * Note that this method is custom for NPC ID: 280501 and should not be used for anything else.
	 * 
	 * @param spawn -- The spawn template to follow.
	 * @param instanceId -- The instance in which to spawn the template.
	 * @param creator -- The creator of the returned Trap.
	 * @return A Trap created from the SpawnTemplate brought into the world in the specified
	 * instance, and with the given creator.
	 */
	private Trap spawnIndividualTrap(SpawnTemplate spawn, int instanceId, Creature creator) {
		int objectId = spawn.getNpcId();
		NpcTemplate npcTemplate = DataManager.NPC_DATA.getNpcTemplate(objectId);
		Trap trap = new Trap(IDFactory.getInstance().nextId(), new NpcController(), spawn, npcTemplate);
		trap.setKnownlist(new NpcKnownList(trap));
		trap.setEffectController(new EffectController(trap));
		trap.setCreator(creator);
		//skill 16757 (Basic Stealth -- for traps)
		SkillEngine.getInstance().applyEffectDirectly(16757, trap, trap, 0);
		try {
			//This skillId is Area Aggravate Wound, which is the skill Kromede's trap uses.
			trap.getAi2().onCustomEvent(1, DataManager.SKILL_DATA.getSkillTemplate(17050).getProperties().getEffectiveRange());
		} catch (Exception e) {
			trap.getAi2().onCustomEvent(1, 5); //Default range of above skill
		}
		SpawnEngine.bringIntoWorld(trap, spawn.getWorldId(), instanceId, spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getHeading());
		PacketSendUtility.broadcastPacket(trap, new SM_PLAYER_STATE(trap));
		return trap;
	}
	
	/**
	 * Checks the current time against {@link #lastCycleTime}. If at least 30 seconds
	 * have passed since the last skill cycle, this method will return true. There is
	 * a special case; if Kromede will be using {@link #MISERABLE_STRUGGLE} next,
	 * she will only wait 10s.
	 * 
	 * @return true if 30s has passed since the last cycle, false otherwise.
	 */
	private boolean isReadyForNextSkillCycle() {
		int hp = getOwner().getLifeStats().getHpPercentage();
		long currentTime = System.currentTimeMillis();
		if ((currentTime - lastCycleTime) >= ((hp < ((getOwner().getNpcId() == 214621) ? 20 : 30) && phase == 4) ? 10000 : 30000)) {
			//It's been 30s since the last cycle; ready for next cycle.
			lastCycleTime = currentTime;
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if Kromede should reapply Blessing of Rock, and returns true if so.
	 * {@link #lastCycleTime} is also adjusted so that the Skill Cycle may continue.
	 * 
	 * @return true if Kromede should reapply Blessing of Rock, false otherwise.
	 */
	private boolean checkBlessing() {
		boolean ret = false;
		if (blessingCount < 1 && getOwner().getLifeStats().getHpPercentage() <= 75) {
			blessingCount++;
			if (!ret) ret = ((blessingOfRock() == AttackIntention.SKILL_BUFF) ? (true) : (false));
		}
		if (blessingCount < 2 && getOwner().getLifeStats().getHpPercentage() <= 50) {
			blessingCount++;
			if (!ret) ret = ((blessingOfRock() == AttackIntention.SKILL_BUFF) ? (true) : (false));
		}
		if (blessingCount < 3 && getOwner().getLifeStats().getHpPercentage() <= 25) {
			blessingCount++;
			if (!ret) ret = ((blessingOfRock() == AttackIntention.SKILL_BUFF) ? (true) : (false));
		}
		if (ret) lastCycleTime -= 30000;
		return ret;
	}
	
	/**
	 * Helper method to apply Blessing of Rock to Kromede upon spawn/reset
	 */
	private void applyBlessing() {
		if (getSkillList().isSkillPresent(BLESSING_OF_ROCK) && !getOwner().getEffectController().isAbnormalPresentBySkillId(BLESSING_OF_ROCK)) {
			EmoteManager.emoteStartAttacking(getOwner());
			AI2Actions.targetSelf(this);
			AI2Actions.useSkill(this, BLESSING_OF_ROCK, getSkillList().getSkillLevel(BLESSING_OF_ROCK));
			AI2Actions.targetCreature(this, null);
			EmoteManager.emoteStopAttacking(getOwner());
			setStateIfNot(AIState.IDLE);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * In the case of Kromede, the only entity she asks for help from is Silver Blade Rotan (NPC ID: 212824)
	 */
	@Override
	protected void callForHelp(int distance) {
		Creature firstTarget = getAggroList().getMostHated();
		for (VisibleObject object : getKnownList().getKnownObjects().values()) {
			//Silver Blade Rotan's ID
			if (object instanceof Npc && object.getObjectTemplate().getTemplateId() == 212824 && isInRange(object, distance)) {
				Npc npc = (Npc) object;
				if ((npc != null) && !npc.getLifeStats().isAlreadyDead()) {
					npc.getAi2().onCreatureEvent(AIEventType.CREATURE_AGGRO, firstTarget);
				}
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Kromede's {@link #phase} and {@link #blessingCount} will be reset.
	 */
	@Override
	protected void handleDied() {
		phase = 0;
		blessingCount = 0;
		super.handleDied();
		getOwner().getGameStats().endEffect(this, false);
	}
	

	/**
	 * {@inheritDoc}
	 * <p>
	 * In the case of Kromede, the only thing she will support is Silver Blade Rotan (NPC ID: 212824)
	 */
	@Override
	protected boolean handleCreatureNeedsSupport(Creature creature) {
		if (creature.getObjectTemplate().getTemplateId() != 212824) {
			return false;
		}
		return super.handleCreatureNeedsSupport(creature);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Kromede will not attack back during {@link #phase phase 0} (until she has reached a point-blank range
	 * of the first target).
	 */
	@Override
	protected void handleAttack(Creature creature) {
		if (creature == null || creature.getLifeStats().isAlreadyDead()) {
			return;
		}
		
		if (isInState(AIState.RETURNING)) {
			getOwner().getMoveController().abortMove();
			setStateIfNot(AIState.IDLE);
			onGeneralEvent(AIEventType.NOT_AT_HOME);
			return;
		}
		
		if (isInState(AIState.WALKING)) {
			WalkManager.stopWalking(this);
		}
		
		getOwner().getGameStats().renewLastAttackedTime();
		if (!isInState(AIState.FIGHT)) {
			getOwner().setTarget(creature); //moved to here because execution order; there's a bug hiding when it's lower (couldn't find it).
			setStateIfNot(AIState.FIGHT);
			setSubStateIfNot(AISubState.NONE);
			getOwner().getGameStats().setFightStartingTime();
			EmoteManager.emoteStartAttacking(getOwner());
			if (phase == 0) {
				onGeneralEvent(AIEventType.TARGET_TOOFAR);
			} else {
				onIntentionToAttack(creature);
			}
		}
	}
	
	@Override
	protected void handleSkillAttackIntention(final int delay, final Creature creature) {
		if (delay == 0 && skillId == GUILTY_VERDICT) AI2Actions.targetSelf(this);
		if (skillId == GUILTY_VERDICT || skillId == MISERABLE_STRUGGLE) {
			super.handleSkillAttackIntention(delay, getOwner());
		} else {
			super.handleSkillAttackIntention(delay, creature);
		}
	}
	
	@Override
	protected void handleSkillBuffIntention(int delay, Creature creature) {
		AI2Actions.targetSelf(this);
		super.handleSkillBuffIntention(delay, getOwner());
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Kromede will spawn traps after Guilty Verdict, and change target back to the player.
	 */
	@Override
	protected void handleAttackComplete() {
		switch (skillId) {
			case STRONG_CRY:
				if (phase == 1) phase = 2;
				break;
			case REPEAT_IMPACT_STRIKES:
				if (phase == 2) phase = 3;
				break;
			case GUILTY_VERDICT:
				if (phase == 3) phase = 4;
				break;
			case MISERABLE_STRUGGLE:
				if (phase == 4) phase = 5;
				break;
			default:
				break;
		}
		if (trapTask != null) synchronized (trapTask) {
			ThreadPoolManager.getInstance().schedule(trapTask, 1500);
		}
		getOwner().getGameStats().renewLastAttackTime();
		if (skillId == MISERABLE_STRUGGLE) {
			//Attacking self is a little weird, but her target should be herself and not change here.
			onIntentionToAttack(getOwner());
			return;
		}
		handleTargetChanged(getOwner().getAggroList().getMostHated());
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Kromede will not change target while <code>{@link #phase phase <= 2}</code>.
	 */
	@Override
	protected void handleTargetChanged(Creature creature) {
		if (phase > 2 || getState() != AIState.FIGHT) {
			super.handleTargetChanged(creature);
		} else {
			if (getState() == AIState.FIGHT) {
				assert getTarget() != getOwner():"Kromede somehow managed to target herself in her opening phases!";
				onIntentionToAttack((Creature) getTarget());
			}
		}
	}
	
//	@Override
//	protected void handleFinishAttack() {
//		//Consider making Kromede taunt the players for wiping.
//		super.handleFinishAttack();
//	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Kromede's {@link #phase} and {@link #blessingCount} will be reset.
	 */
	@Override
	protected void handleBackHome() {
		phase = 0;
		blessingCount = 0;
		super.handleBackHome();
		applyBlessing();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Kromede's {@link #phase} and {@link #blessingCount} will be reset.
	 */
	@Override
	protected void handleDespawned() {
		phase = 0;
		blessingCount = 0;
		super.handleDespawned();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Kromede will apply Blessing of Rock to herself.
	 */
	@Override
	protected void handleActivate() {
		super.handleActivate();
		applyBlessing();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Kromede will apply Blessing of Rock to herself, along with a 35% bonus to her attack range.
	 */
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		applyBlessing();
		List<IStatFunction> stat = new FastList<IStatFunction>();
		stat.add(new StatRateFunction(StatEnum.ATTACK_RANGE, 35, true));
		getOwner().getGameStats().addEffect(this, stat, false);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Kromede will apply Blessing of Rock to herself.
	 */
	@Override
	protected void handleRespawned() {
		super.handleRespawned();
		applyBlessing();
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Kromede resists all abnormal states (she's immune).
	 * <p>
	 * Note that on retail for this patch she is not immune, but this server is not nerfed like retail.
	 */
	@Override
	public AIAnswer ask(AIQuestion question) {
		switch (question) {
			case CAN_RESIST_ABNORMAL:
				return AIAnswers.POSITIVE;
			default:
				return AIAnswers.NEGATIVE;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Kromede does not shout, returns false
	 * @return false
	 */
	@Override
	public boolean isMayShout() {
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Kromede will check if she should use any of her skills and return the appropriate response;
	 * this implementation is similar to GeneralNpcAI2's implementation in the sense
	 * that it also handles selecting and preparing the skill that will be used next.
	 */
	@Override
	public AttackIntention chooseAttackIntention(Creature creature) {
		if (getTarget() == null || getAggroList().getMostHated() == null || ((Creature) getTarget()).getLifeStats().isAlreadyDead()) {
			return AttackIntention.FINISH_ATTACK;
		}
		switch (phase) {
			case 1:
				return strongCry();
			case 2:
				return repeatImpactStrikes();
			case 3:
				if (getOwner().getLifeStats().getHpPercentage() <= ((getOwner().getNpcId() == 214621) ? 88 : 75)) {
					return skillCycle();
				}
				break;
			case 4:
			case 5:
				if (phase != 5 || skillId != MISERABLE_STRUGGLE) {
					return skillCycle();
				} else {
					return guiltyVerdict();
				}
		}
		
		skillId = 0;
		skillLevel = 0;
		return AttackIntention.SIMPLE_ATTACK;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Kromede will behave differently if <code>{@link #phase phase <= 2}</code>.
	 */
	@Override
	protected boolean isDestinationReached() {
		if (phase > 2) {
			return super.isDestinationReached();
		} else {
			VisibleObject target = getOwner().getTarget();
			if (target instanceof Creature) {
				if (MathUtil.isInAttackRange(getOwner(), ((Creature) target), 0.5F)) {
					if (phase == 0) phase = 1;
					return true;
				}
			}
		}
		return false;
	}
}
