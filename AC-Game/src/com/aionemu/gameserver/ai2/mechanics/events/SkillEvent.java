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
package com.aionemu.gameserver.ai2.mechanics.events;

import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.MechanicEventType;
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.context.SkillCategory;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class SkillEvent extends MechanicEvent {
	
	final public int skillId;
	
	final public SkillCategory skillCategory;
	
	final public Creature eventTarget, caster;
	
	public SkillEvent(MechanicEventType type, int skillId, Creature eventTarget, Creature caster) {
		super(type);
		this.skillId = skillId;
		this.eventTarget = eventTarget;
		this.caster = caster;
		SkillTemplate template = DataManager.SKILL_DATA.getSkillTemplate(skillId);
		if (template == null) {
			skillCategory = null;
		} else {
			skillCategory = SkillCategory.getCategory(template);
		}
	}
	
	@Override
	public VisibleObject getObjectIndicator(ObjIndicator obj, AbstractMechanicsAI2 ai) {
		switch (obj) {
			case OBJI_ATTACKER:
			case OBJI_CASTER:
				return caster;
			case OBJI_EVENT_TARGET:
			case OBJI_FRIEND:
				return eventTarget;
			default:
				return getDefaultObjectIndicator(obj, ai);
		}
	}
	
}
