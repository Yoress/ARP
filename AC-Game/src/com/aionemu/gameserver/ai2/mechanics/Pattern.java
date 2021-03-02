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

import java.util.ArrayList;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.mechanics.actions.Action;
import com.aionemu.gameserver.ai2.mechanics.actions.Action.DoNothing;
import com.aionemu.gameserver.ai2.mechanics.actions.ActionType;
import com.aionemu.gameserver.ai2.mechanics.conditions.Condition;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "sequence")
public class Pattern {
	
	private static transient final Logger LOG = LoggerFactory.getLogger(Pattern.class);
	
	private transient boolean hasDelayedAction = false;
	
	@XmlElementWrapper(name = "conditions")
	@XmlElement(name = "condition")
	Condition[] conditions;
	
	@XmlElementWrapper(name = "actions")
	@XmlElement(name = "action")
	Action[] actions;
	
//	private Pattern() {
//		//For JAXB
//	}
//	
//	public Pattern(Condition[] conditions, Action[] actions) { //Only needed for importing new data
//		this.conditions = conditions;
//		this.actions = actions;
//	}
	
	void afterUnmarshal(Unmarshaller u, Object parent) {
		if (actions != null) for (Action action: actions) {
			if (hasDelayedAction) break;
			if (action != null) switch (action.type) {
				case attack_most_hating:
				case despawn_self:
				case flee_from:
				case say_to_all:
				case spawn:
				case spawn_on_target:
				case spawn_on_target_by_attacker_indicator:
				case switch_target:
				case switch_target_by_attacker_indicator:
				case switch_target_by_class_indicator:
				case use_skill:
				case use_skill_by_attacker_indicator:
					hasDelayedAction = true;
				default: continue;
			}
		}
	}
	
	public boolean checkConditions(MechanicEvent event, AbstractMechanicsAI2 ai) {
		if (conditions != null) for (Condition condition: conditions) {
			try {
				if (condition != null && !condition.check(event, ai)) return false;
			} catch (Exception e) {
				LOG.error(event + " <[" + condition + "]> check failed by exception for mechanic: [" + ai.getOwner().getObjectTemplate().getMechanic() + "]!", e);
				return false;
			}
		}
		return true;
	}
	
	public void performActions(MechanicEvent event, AbstractMechanicsAI2 ai) throws DoNothing {
		boolean notify = false;
		boolean throwDoNothing = false;
		if (hasDelayedAction) {
			synchronized (ai.attackIntentionQueue) {
				ArrayList<Action> actionList = new ArrayList<Action>(actions.length);
				if (actions != null) for (Action action: actions) {
					if (action != null) switch (action.type) {
						case do_nothing:
							//This is okay, because the performAction() for DoNothingAction is empty.
							throwDoNothing = true;
							break;
						case flee_from:
							//TODO: this might break some stuff!
							if (ai.isNonFightingState()) throwDoNothing = true;
						case attack_most_hating:
						case despawn_self:
						case say_to_all:
						case switch_target:
						case switch_target_by_attacker_indicator:
						case switch_target_by_class_indicator:
						case spawn_on_target:
						case spawn_on_target_by_attacker_indicator:
						case use_skill:
						case use_skill_by_attacker_indicator:
							actionList.add(action);
							break;
						case spawn:
							if (!ai.isNonFightingState()) {
								actionList.add(action);
								break;
							}
						default:
							try {
								action.performAction(event, ai);
							} catch (Exception e) {
								LOG.error(event + " <[" + action + "]> failed by exception for mechanic: [" + ai.getAIMechanics().mechanicId + "]!", e);
							}
					}
				}
				if (actionList.size() > 0) {
					ai.attackIntentionQueue.add(event, actionList.toArray(new Action[actionList.size()]));
					if (ai.isNonFightingState()) notify = true;
				}
			}
		} else {
			if (actions != null) for (Action action: actions) {
				try {
					if (action != null) {
						if (!throwDoNothing && action.type == ActionType.do_nothing) throwDoNothing = true;
						action.performAction(event, ai);
					}
				} catch (Exception e) {
					LOG.error(event + " <[" + action + "]> failed by exception for mechanic: [" + ai.getAIMechanics().mechanicId + "]!", e);
				}
			}
		}
		if (notify) ai.notifyIdleQueue();
		if (throwDoNothing) throw new DoNothing();
	}
}
