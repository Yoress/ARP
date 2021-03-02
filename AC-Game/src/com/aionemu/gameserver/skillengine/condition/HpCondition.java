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
package com.aionemu.gameserver.skillengine.condition;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.SummonedObject;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * @author Tomate
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HpCondition")
public class HpCondition extends Condition {
	
	@XmlAttribute(required = true)
	protected int value;
	@XmlAttribute
	protected int delta;
	@XmlAttribute
	protected boolean ratio;
	
	@Override
	public boolean validate(Skill skill) {
		// exception for Servants, Totems to let them cast last skill and die
		if (skill.getEffector() instanceof SummonedObject) {
			return true;
		}
		
		int valueWithDelta = value + delta * skill.getSkillLevel();
		if (ratio) {
			valueWithDelta = (int) (valueWithDelta / 100f * skill.getEffector().getLifeStats().getMaxHp());
		}
		return skill.getEffector().getLifeStats().getCurrentHp() > valueWithDelta;
	}
	
	public int getHpValue() {
		return value;
	}
}
