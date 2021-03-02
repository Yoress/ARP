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
package com.aionemu.gameserver.ai2.mechanics.actions;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.adapters.ActionAdapter;
import com.aionemu.gameserver.ai2.mechanics.context.AttackerIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.ClassIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.controllers.attack.AggroList;
import com.aionemu.gameserver.model.gameobjects.Creature;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlJavaTypeAdapter(ActionAdapter.class)
public abstract class Action {
	
	protected final static Logger LOG = LoggerFactory.getLogger(Action.class);
	
	final public ActionType type;
	
	public Action(ActionType type) {
		this.type = type;
	}
	
	public abstract void performAction(MechanicEvent event, AbstractMechanicsAI2 ai);
	
	public static Creature getAttackerIndicator(AttackerIndicator attackerIndicator, AbstractMechanicsAI2 ai) {
		AggroList list = ai.getOwner().getAggroList();
		switch (attackerIndicator) {
			case ATTACKERI_HAS_LOWEST_HP:
				return list.getLowestHp();
			case ATTACKERI_HAS_MOST_HP:
				return list.getHighestHp();
			case ATTACKERI_RANDOM_ONE:
				return list.getRandom();
			case ATTACKERI_RANDOM_ONE_EXCEPT_CURRENT_TARGET:
				try {
					return list.getRandomNot((Creature) ai.getTarget());
				} catch (ClassCastException e) {
					return list.getMostHated();
				}
			case ATTACKERI_SECOND_HATING:
				return list.getSecondHating();
			case ATTACKERI_THIRD_HATING:
				return list.getThirdHating();
			default:
				return list.getMostHated();
		}
	}
	
	public static Creature getClassIndicator(ClassIndicator classIndicator, AbstractMechanicsAI2 ai) {
		return ai.getOwner().getAggroList().getMostHatedClass(classIndicator);
	}
	
	@Override
	public String toString() {
		return "Action: " + type;
	}
	
	public static class DoNothing extends Throwable {

		/**
		 * Declared for the sole purpose of preventing the JVM from calculating a value at startup
		 */
		private static final long serialVersionUID = 1L;
		
		public DoNothing() {}
		
	}
}
