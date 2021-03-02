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
package com.aionemu.gameserver.model.skill;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplate;

/**
 * @author ATracer, nrg
 * @reworked Yon (Aion Reconstruction Project) -- Reworked to support new skill lists.
 */
public abstract class NpcSkillEntry extends SkillEntry {
	
	protected long lastTimeUsed = 0;
	
	public NpcSkillEntry(int skillId, int skillLevel) {
		super(skillId, skillLevel);
	}
	
	public abstract boolean isReady();
	
	public abstract boolean chanceReady();
	
	public abstract boolean hasCooldown();
	
	public abstract boolean hasCountRemaining();
	
	public abstract void reduceCount();
	
	public abstract void resetCount();
	
	public long getLastTimeUsed() {
		return lastTimeUsed;
	}
	
	public void setLastTimeUsed() {
		this.lastTimeUsed = System.currentTimeMillis();
		reduceCount();
	}
}

/**
 * Skill entry which inherits properties from template (regular npc skills)
 */
class NpcSkillTemplateEntry extends NpcSkillEntry {
	
	private final NpcSkillTemplate template;
	
	private int count;
	
	public NpcSkillTemplateEntry(NpcSkillTemplate template) {
		super(template.getSkillId(), template.getSkillLevel());
		this.template = template;
		count = template.getSkillCount();
	}
	
	@Override
	public boolean isReady() {
		if (hasCooldown() || !chanceReady() || (count == 0 && template.getSkillCount() > 0)) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean chanceReady() {
		return Rnd.get(0, 100) < template.getProbability();
	}
	
	@Override
	public boolean hasCooldown() {
		return (template.getCooldown() == 0 ? 6000 : template.getCooldown()) > (System.currentTimeMillis() - lastTimeUsed);
	}
	
	@Override
	public boolean hasCountRemaining() {
		return (count > 0 || template.getSkillCount() == 0);
	}
	
	@Override
	public void reduceCount() {
		if (count > 0) count--;
	}
	
	@Override
	public void resetCount() {
		count = template.getSkillCount();
	}
	
}

/**
 * Skill entry which can be created on the fly (skills of servants, traps)
 */
class NpcSkillParameterEntry extends NpcSkillEntry {
	
	public NpcSkillParameterEntry(int skillId, int skillLevel) {
		super(skillId, skillLevel);
	}
	
	@Override
	public boolean isReady() {
		return true;
	}
	
	@Override
	public boolean chanceReady() {
		return true;
	}
	
	@Override
	public boolean hasCooldown() {
		return false;
	}
	
	@Override
	public boolean hasCountRemaining() {
		return true;
	}
	
	@Override
	public void reduceCount() {}
	
	@Override
	public void resetCount() {}
	
}
