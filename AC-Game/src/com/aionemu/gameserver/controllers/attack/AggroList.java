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
package com.aionemu.gameserver.controllers.attack;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.commons.callbacks.Callback;
import com.aionemu.commons.callbacks.CallbackResult;
import com.aionemu.commons.callbacks.metadata.ObjectCallback;
import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.event.AIEventType;
import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.MechanicEventType;
import com.aionemu.gameserver.ai2.mechanics.context.ClassIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.OrderIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.CreatureEvent;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.AionObject;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.team2.group.PlayerGroup;
import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.model.QuestEnv;
import com.aionemu.gameserver.utils.MathUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

/**
 * @author ATracer, KKnD
 * @modified Yon (Aion Reconstruction Project) -- Added multiple methods to grab various targets; {@link #getSecondHating()},
 * {@link #getThirdHating()}, {@link #getRandom()}, {@link #getRandomNot(VisibleObject)}, {@link #getHighestHp()}, {@link #getLowestHp()},
 * {@link #getMostHatedClass(ClassIndicator)}; {@link #addHateValue(Creature, int)} modified to support Mechanics System; added
 * {@link #getOrderedList(OrderIndicator)} to support the Mechanics System.
 */
public class AggroList {
	
	protected final Creature owner;
	private FastMap<Integer, AggroInfo> aggroList = new FastMap<Integer, AggroInfo>().shared(); //TODO: Replace with heap
	
	public AggroList(Creature owner) {
		this.owner = owner;
	}
	
	/**
	 * Only add damage from enemies. (Verify this includes summons, traps, pets, and excludes fall damage.)
	 *
	 * @param attacker
	 * @param damage
	 */
	@ObjectCallback(AddDamageValueCallback.class)
	public void addDamage(Creature attacker, int damage) {
		if (!isAware(attacker)) {
			return;
		}
		
		AggroInfo ai = getAggroInfo(attacker);
		ai.addDamage(damage);
		/**
		 * For now we add hate equal to each damage received
		 * Additionally there will be broadcast of extra hate
		 */
		ai.addHate(damage);
		owner.getAi2().onCreatureEvent(AIEventType.ATTACK, attacker);
	}
	
	/**
	 * Extra hate that is received from using non-damage skill effects
	 */
	public void addHate(final Creature creature, int hate) {
		if (!isAware(creature)) {
			return;
		}
		addHateValue(creature, hate);
	}
	
	/**
	 * start hating creature by adding 1 hate value
	 */
	public void startHate(final Creature creature) {
		addHateValue(creature, 1);
	}
	
	protected void addHateValue(final Creature creature, int hate) {
		//isMostHated(Creature) iterates through the list! The list should be replaced with a faster structure like a heap.
		boolean updateCheck = owner.getAi2() instanceof AbstractMechanicsAI2 && !isMostHated(creature);
		AggroInfo ai = getAggroInfo(creature);
		ai.addHate(hate);
		if (creature instanceof Player && owner instanceof Npc) {
			for (Player player : owner.getKnownList().getKnownPlayers().values()) {
				if (MathUtil.isIn3dRange(owner, player, 50)) {
					QuestEngine.getInstance().onAddAggroList(new QuestEnv(owner, player, 0, 0));
				}
			}
		}
		owner.getAi2().onCreatureEvent(AIEventType.ATTACK, creature);
		if (updateCheck && isMostHated(creature)) {
			AbstractMechanicsAI2 mAI = ((AbstractMechanicsAI2) owner.getAi2());
			mAI.onMechanicEvent(new CreatureEvent(MechanicEventType.on_most_hating_updated, creature, null, creature));
		}
	}
	
	/**
	 * @return player/group/alliance with most damage.
	 */
	public AionObject getMostDamage() {
		AionObject mostDamage = null;
		int maxDamage = 0;
		
		for (AggroInfo ai : getFinalDamageList(true)) {
			if (ai.getAttacker() == null || owner.equals(ai.getAttacker())) {
				continue;
			}
			
			if (ai.getDamage() > maxDamage) {
				mostDamage = ai.getAttacker();
				maxDamage = ai.getDamage();
			}
		}
		
		return mostDamage;
	}
	
	public Race getPlayerWinnerRace() {
		AionObject winner = getMostDamage();
		if (winner instanceof PlayerGroup) {
			return ((PlayerGroup) winner).getRace();
		} else if (winner instanceof Player) {
			return ((Player) winner).getRace();
		}
		return null;
	}
	
	/**
	 * @return player with most damage
	 */
	public Player getMostPlayerDamage() {
		if (aggroList.isEmpty()) {
			return null;
		}
		
		Player mostDamage = null;
		int maxDamage = 0;
		
		// Use final damage list to get pet damage as well.
		for (AggroInfo ai : this.getFinalDamageList(false)) {
			if (ai.getDamage() > maxDamage && ai.getAttacker() instanceof Player) {
				mostDamage = (Player) ai.getAttacker();
				maxDamage = ai.getDamage();
			}
		}
		
		return mostDamage;
	}
	
	/**
	 * @return player with most damage
	 */
	public Player getMostPlayerDamageOfMembers(Collection<Player> team, int highestLevel) {
		if (aggroList.isEmpty()) {
			return null;
		}
		
		Player mostDamage = null;
		int maxDamage = 0;
		
		// Use final damage list to get pet damage as well.
		for (AggroInfo ai : this.getFinalDamageList(false)) {
			if (!(ai.getAttacker() instanceof Player)) {
				continue;
			}
			
			if (!team.contains((Player) ai.getAttacker())) {
				continue;
			}
			
			if (ai.getDamage() > maxDamage) {
				
				mostDamage = (Player) ai.getAttacker();
				maxDamage = ai.getDamage();
			}
		}
		
		if (mostDamage != null && mostDamage.isMentor()) {
			for (Player member : team) {
				if (member.getLevel() == highestLevel) {
					mostDamage = member;
				}
			}
		}
		
		return mostDamage;
	}
	
	public Creature[] getOrderedList(OrderIndicator order) {
		FastList<Creature> retList = new FastList<Creature>(aggroList.size());
		switch (order) {
			case ORDERI_ASCENDING:
			case ORDERI_DESCENDING:
				FastList<AggroInfo> aggro = new FastList<AggroInfo>(aggroList.size());
				for (Integer key: aggroList.keySet()) {
					aggro.add(aggroList.get(key));
				}
				Comparator<AggroInfo> comp = new Comparator<AggroInfo>() {
					boolean ascending;
					
					Comparator<AggroInfo> setType(OrderIndicator order) {
						if (order == OrderIndicator.ORDERI_ASCENDING) ascending = true;
						else ascending = false;
						return this;
					}
					
					@Override
					public int compare(AggroInfo o1, AggroInfo o2) {
						if ((ascending ? (o1.getHate() > o2.getHate()) : (o1.getHate() < o2.getHate()))) {
							return 1;
						}
						if ((ascending ? (o1.getHate() < o2.getHate()) : (o1.getHate() > o2.getHate()))) {
							return -1;
						}
						
						//Equal hate; use damage
						if ((ascending ? (o1.getDamage() > o2.getDamage()) : (o1.getDamage() < o2.getDamage()))) {
							return 1;
						}
						if ((ascending ? (o1.getDamage() < o2.getDamage()) : (o1.getDamage() > o2.getDamage()))) {
							return -1;
						}
						
						//Completely equal
						return 0;
					}
				}.setType(order);
				Collections.sort(aggro, comp);
				for (AggroInfo info: aggro) {
					if (info.getAttacker() instanceof Creature) {
						retList.add((Creature) info.getAttacker());
					}
				}
				break;
			case ORDERI_RANDOM:
				for (Integer key: aggroList.keySet()) {
					AggroInfo info = aggroList.get(key);
					if (info.getAttacker() instanceof Creature) {
						retList.add((Creature) info.getAttacker());
					}
				}
				break;
			default:
				assert false:"Unsupported OrderIndicator: " + order;
				throw new IllegalArgumentException("Unsupported OrderIndicator: " + order);
		}
		return retList.toArray(new Creature[retList.size()]);
	}
	
	/**
	 * @return most hated creature
	 */
	public Creature getMostHated() {
		if (aggroList.isEmpty()) {
			return null;
		}
		
		Creature mostHated = null;
		int maxHate = 0;
		
		for (FastMap.Entry<Integer, AggroInfo> e = aggroList.head(), mapEnd = aggroList.tail(); (e = e.getNext()) != mapEnd;) {
			AggroInfo ai = e.getValue();
			if (ai == null) {
				continue;
			}
			
			// aggroList will never contain anything but creatures
			Creature attacker = (Creature) ai.getAttacker();
			
			if (attacker.getLifeStats().isAlreadyDead()) {
				ai.setHate(0);
			}
			
			if (ai.getHate() > maxHate) {
				mostHated = attacker;
				maxHate = ai.getHate();
			}
		}
		
		return mostHated;
	}
	
	public Creature getSecondHating() {
		if (aggroList.isEmpty()) return null;
		Creature mostHated = null;
		Creature secondHated = null;
		int maxHate = 0;
		
		for (FastMap.Entry<Integer, AggroInfo> e = aggroList.head(), mapEnd = aggroList.tail(); (e = e.getNext()) != mapEnd;) {
			AggroInfo ai = e.getValue();
			if (ai == null) {
				continue;
			}
			// aggroList will never contain anything but creatures
			Creature attacker = (Creature) ai.getAttacker();
			
			if (attacker.getLifeStats().isAlreadyDead()) {
				ai.setHate(0);
			}
			
			if (ai.getHate() > maxHate) {
				secondHated = mostHated;
				mostHated = attacker;
				maxHate = ai.getHate();
			}
		}
		return ((secondHated == null) ? mostHated : secondHated);
	}
	
	public Creature getThirdHating() {
		if (aggroList.isEmpty()) return null;
		Creature mostHated = null;
		Creature secondHated = null;
		Creature thirdHated = null;
		int maxHate = 0;
		
		for (FastMap.Entry<Integer, AggroInfo> e = aggroList.head(), mapEnd = aggroList.tail(); (e = e.getNext()) != mapEnd;) {
			AggroInfo ai = e.getValue();
			if (ai == null) {
				continue;
			}
			// aggroList will never contain anything but creatures
			Creature attacker = (Creature) ai.getAttacker();
			
			if (attacker.getLifeStats().isAlreadyDead()) {
				ai.setHate(0);
			}
			
			if (ai.getHate() > maxHate) {
				thirdHated = secondHated;
				secondHated = mostHated;
				mostHated = attacker;
				maxHate = ai.getHate();
			}
		}
		return ((thirdHated == null) ? (((secondHated == null) ? (mostHated) : (secondHated))) : (thirdHated));
	}
	
	public Creature getRandom() {
		if (aggroList.isEmpty()) return null;
		Integer[] keys = aggroList.keySet().toArray(new Integer[aggroList.size()]);
		Creature ret = (Creature) aggroList.get(keys[Rnd.get(0, keys.length - 1)]).getAttacker();
		if (!ret.getLifeStats().isAlreadyDead() && MathUtil.isIn3dRange(owner, ret, 30)) {
			return ret;
		}
		return getMostHated();
	}
	
	public Creature getRandomNot(Creature creature) {
		if (aggroList.isEmpty()) return null;
		Integer[] keys = aggroList.keySet().toArray(new Integer[aggroList.size()]);
		int rnd = Rnd.get(0, keys.length - 1);
		Creature ret = (Creature) aggroList.get(keys[rnd]).getAttacker();
		if (!ret.getLifeStats().isAlreadyDead() && MathUtil.isIn3dRange(owner, ret, 30)) {
			return ret;
		}
		return (isMostHated(creature) ? getSecondHating() : getMostHated());
	}
	
	public Creature getHighestHp() {
		if (aggroList.isEmpty()) return null;
		Creature mostHp = null;
		int maxHp = 0;
		
		for (FastMap.Entry<Integer, AggroInfo> e = aggroList.head(), mapEnd = aggroList.tail(); (e = e.getNext()) != mapEnd;) {
			AggroInfo ai = e.getValue();
			if (ai == null) {
				continue;
			}
			// aggroList will never contain anything but creatures
			Creature attacker = (Creature) ai.getAttacker();
			
			if (attacker.getLifeStats().isAlreadyDead()) {
				ai.setHate(0);
			}
			
			if (attacker.getLifeStats().getCurrentHp() > maxHp) {
				mostHp = attacker;
				maxHp = attacker.getLifeStats().getCurrentHp();
			}
		}
		return mostHp;
	}
	
	public Creature getLowestHp() {
		if (aggroList.isEmpty()) return null;
		Creature lowestHp = null;
		int minHp = Integer.MAX_VALUE;
		
		for (FastMap.Entry<Integer, AggroInfo> e = aggroList.head(), mapEnd = aggroList.tail(); (e = e.getNext()) != mapEnd;) {
			AggroInfo ai = e.getValue();
			if (ai == null) {
				continue;
			}
			// aggroList will never contain anything but creatures
			Creature attacker = (Creature) ai.getAttacker();
			
			if (attacker.getLifeStats().isAlreadyDead()) {
				ai.setHate(0);
			}
			
			if (attacker.getLifeStats().getCurrentHp() < minHp && !attacker.getLifeStats().isAlreadyDead()) {
				lowestHp = attacker;
				minHp = attacker.getLifeStats().getCurrentHp();
			}
		}
		return lowestHp;
	}
	
	public Creature getMostHatedClass(ClassIndicator classIndicator) {
		if (aggroList.isEmpty()) return null;
		
		Creature mostHatedClass = null;
		Creature mostHated = null;
		int maxHate = 0;
		
		for (FastMap.Entry<Integer, AggroInfo> e = aggroList.head(), mapEnd = aggroList.tail(); (e = e.getNext()) != mapEnd;) {
			AggroInfo ai = e.getValue();
			if (ai == null) continue;
			// aggroList will never contain anything but creatures
			Creature attacker = (Creature) ai.getAttacker();
			if (attacker.getLifeStats().isAlreadyDead()) {
				ai.setHate(0);
			}
			if (ai.getHate() > maxHate) {
				mostHated = attacker;
				if (classIndicator != null && classIndicator.isClass(attacker)) {
					mostHatedClass = attacker;
				}
				maxHate = ai.getHate();
			}
		}
		
		return (mostHatedClass == null ? mostHated : mostHatedClass);
	}
	
	/**
	 * @param creature
	 * @return
	 */
	public boolean isMostHated(Creature creature) {
		if (creature == null || creature.getLifeStats().isAlreadyDead()) {
			return false;
		}
		
		Creature mostHated = getMostHated();
		return mostHated != null && mostHated.equals(creature);
		
	}
	
	/**
	 * @param creature
	 * @param value
	 */
	public void notifyHate(Creature creature, int value) {
		if (isHating(creature)) {
			addHate(creature, value);
		}
	}
	
	/**
	 * @param creature
	 */
	public void stopHating(VisibleObject creature) {
		AggroInfo aggroInfo = aggroList.get(creature.getObjectId());
		if (aggroInfo != null) {
			aggroInfo.setHate(0);
		}
	}
	
	/**
	 * Remove completely creature from aggro list
	 *
	 * @param creature
	 */
	public void remove(Creature creature) {
		aggroList.remove(creature.getObjectId());
	}
	
	/**
	 * Clear aggroList
	 */
	public void clear() {
		aggroList.clear();
	}
	
	/**
	 * @param creature
	 * @return aggroInfo
	 */
	public AggroInfo getAggroInfo(Creature creature) {
		AggroInfo ai = aggroList.get(creature.getObjectId());
		if (ai == null) {
			ai = new AggroInfo(creature);
			aggroList.put(creature.getObjectId(), ai);
		}
		return ai;
	}
	
	/**
	 * @param creature
	 * @return boolean
	 */
	public boolean isHating(Creature creature) {
		return aggroList.containsKey(creature.getObjectId());
	}
	
	/**
	 * @return aggro list
	 */
	public Collection<AggroInfo> getList() {
		return aggroList.values();
	}
	
	/**
	 * @return total damage
	 */
	public int getTotalDamage() {
		int totalDamage = 0;
		for (AggroInfo ai : aggroList.values()) {
			totalDamage += ai.getDamage();
		}
		return totalDamage;
	}
	
	/**
	 * Used to get a list of AggroInfo with npc and player/group/alliance damages combined.
	 *
	 * @return finalDamageList
	 */
	public Collection<AggroInfo> getFinalDamageList(boolean mergeGroupDamage) {
		Map<Integer, AggroInfo> list = new HashMap<Integer, AggroInfo>();
		
		for (AggroInfo ai : aggroList.values()) {
			// Get master only to control damage.
			Creature creature = ((Creature) ai.getAttacker()).getMaster();
			
			// Don't include damage from creatures outside the known list.
			if (creature == null || !owner.getKnownList().knowns(creature)) {
				continue;
			}
			
			if (mergeGroupDamage) {
				AionObject source;
				
				if (creature instanceof Player && ((Player) creature).isInTeam()) {
					source = ((Player) creature).getCurrentTeam();
				} else {
					source = creature;
				}
				
				if (list.containsKey(source.getObjectId())) {
					list.get(source.getObjectId()).addDamage(ai.getDamage());
				} else {
					AggroInfo aggro = new AggroInfo(source);
					aggro.setDamage(ai.getDamage());
					list.put(source.getObjectId(), aggro);
				}
			} else if (list.containsKey(creature.getObjectId())) {
				// Summon or other assistance
				list.get(creature.getObjectId()).addDamage(ai.getDamage());
			} else {
				// Create a separate object so we don't taint current list.
				AggroInfo aggro = new AggroInfo(creature);
				aggro.addDamage(ai.getDamage());
				list.put(creature.getObjectId(), aggro);
			}
		}
		
		return list.values();
	}
	
	protected boolean isAware(Creature creature) {
		return creature != null && !creature.getObjectId().equals(owner.getObjectId())
				&& (creature.isEnemy(owner) || DataManager.TRIBE_RELATIONS_DATA.isHostileRelation(owner.getTribe(), creature.getTribe()));
	}
	
	@SuppressWarnings("rawtypes")
	public static abstract class AddDamageValueCallback implements Callback<AggroList> {
		
		@Override
		public final CallbackResult beforeCall(AggroList obj, Object[] args) {
			return CallbackResult.newContinue();
		}
		
		@Override
		public final CallbackResult afterCall(AggroList obj, Object[] args, Object methodResult) {
			
			Creature creature = (Creature) args[0];
			Integer damage = (Integer) args[1];
			
			if (obj.isAware(creature)) {
				onDamageAdded(creature, damage);
			}
			
			return CallbackResult.newContinue();
		}
		
		@Override
		public final Class<? extends Callback> getBaseClass() {
			return AddDamageValueCallback.class;
		}
		
		public abstract void onDamageAdded(Creature creature, int damage);
	}
}
