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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.aionemu.gameserver.ai2.mechanics.context.SkillIndex;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplate;
import com.aionemu.gameserver.model.templates.npcskill.NpcSkillTemplates;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- Renamed {@link #getRandomSkill()} to {@link #getNextSkill()}
 * and reworked it to support new skill lists, deprecated {@link #getUseInSpawnedSkill()}, {@link #getSkill(int)} changed to public,
 * added {@link #resetCounts()}, added {@link #getSkill(SkillIndex)}.
 */
public class NpcSkillList implements SkillList<Npc> {
	
	private List<NpcSkillEntry> skills;
	
	public NpcSkillList(Npc owner) {
		initSkillList(owner.getNpcId());
	}
	
	private void initSkillList(int npcId) {
		NpcSkillTemplates npcSkillList = DataManager.NPC_SKILL_DATA.getNpcSkillList(npcId);
		if (npcSkillList != null) {
			initSkills();
			for (NpcSkillTemplate template : npcSkillList.getNpcSkills()) {
				skills.add(new NpcSkillTemplateEntry(template));
			}
		}
	}
	
	@Override
	public boolean addSkill(Npc creature, int skillId, int skillLevel) {
		initSkills();
		skills.add(new NpcSkillParameterEntry(skillId, skillLevel));
		return true;
	}
	
	@Override
	public boolean removeSkill(int skillId) {
		if (skills == null) return false;
		Iterator<NpcSkillEntry> iter = skills.iterator();
		while (iter.hasNext()) {
			NpcSkillEntry next = iter.next();
			if (next.getSkillId() == skillId) {
				iter.remove();
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isSkillPresent(int skillId) {
		if (skills == null) {
			return false;
		}
		return getSkill(skillId) != null;
	}
	
	@Override
	public int getSkillLevel(int skillId) {
		NpcSkillEntry skill = getSkill(skillId);
		return skill == null ? 0 : skill.getSkillLevel();
	}
	
	@Override
	public int size() {
		return skills != null ? skills.size() : 0;
	}
	
	private void initSkills() {
		if (skills == null) {
			skills = new ArrayList<NpcSkillEntry>();
		}
	}
	
	public NpcSkillEntry getNextSkill() {
		if (skills != null) for (NpcSkillEntry skill: skills) {
			if (skill.hasCountRemaining() && skill.isReady()) {
				return skill;
			}
		}
		return null;
	}
	
	public NpcSkillEntry getSkill(SkillIndex index) {
		if (index == SkillIndex.SKILLI_NONE || skills == null) return null;
		if (index == SkillIndex.SKILLI_ANY_SKILL) return getNextSkill();
		try {
			return skills.get(index.ordinal());
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public NpcSkillEntry getSkill(int skillId) {
		if (skills != null) for (NpcSkillEntry entry : skills) {
			if (entry.getSkillId() == skillId) {
				return entry;
			}
		}
		return null;
	}
	
	public void resetCounts() {
		if (skills != null) for (NpcSkillEntry skill: skills) {
			skill.resetCount();
		}
	}
	
	/**
	 * Formerly returned a Skill Entry for a skill that should have been used on spawn or null;
	 * now this method always returns null.
	 * 
	 * @deprecated
	 */
	@Deprecated
	public NpcSkillEntry getUseInSpawnedSkill() {
//		if (this.skills == null) {
//			return null;
//		}
//		Iterator<NpcSkillEntry> iter = skills.iterator();
//		while (iter.hasNext()) {
//			NpcSkillEntry next = iter.next();
//			NpcSkillTemplateEntry tmpEntry = (NpcSkillTemplateEntry) next;
//			if (tmpEntry.UseInSpawned()) {
//				return next;
//			}
//		}
		return null;
	}
}
