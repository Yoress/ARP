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

import javax.xml.bind.annotation.XmlAttribute;

import com.aionemu.gameserver.skillengine.effect.AbnormalState;
import com.aionemu.gameserver.skillengine.model.Effect;
import com.aionemu.gameserver.skillengine.model.Skill;

/**
 * @author kecimis
 */
public class AbnormalStateCondition extends Condition {
	
	@XmlAttribute(required = true)
	protected AbnormalState value;
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aionemu.gameserver.skillengine.condition.Condition#validate(com. aionemu.gameserver.skillengine.model.Skill)
	 */
	
	@Override
	public boolean validate(Skill env) {
		if (env.getFirstTarget() != null) {
			return (env.getFirstTarget().getEffectController().isAbnormalSet(value));
		}
		return false;
	}
	
	@Override
	public boolean validate(Effect effect) {
		if (effect.getEffected() != null) {
			return (effect.getEffected().getEffectController().isAbnormalSet(value));
		}
		return false;
	}
}