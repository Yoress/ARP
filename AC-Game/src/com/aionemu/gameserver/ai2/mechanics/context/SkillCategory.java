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
package com.aionemu.gameserver.ai2.mechanics.context;

import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.skillengine.model.DispelCategoryType;
import com.aionemu.gameserver.skillengine.model.SkillSubType;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlType
public enum SkillCategory {
	SKILLCTG_CHAIN_SKILL,
	SKILLCTG_HEAL,
	SKILLCTG_PHYSICAL_DEBUFF,
	SKILLCTG_MENTAL_DEBUFF;

	public static SkillCategory getCategory(SkillTemplate template) {
		if (template.getSubType() == SkillSubType.HEAL) {
			return SKILLCTG_HEAL;
		}
		if (template.getSubType() == SkillSubType.DEBUFF) {
			if (template.getDispelCategory() == DispelCategoryType.DEBUFF_MENTAL) {
				return SKILLCTG_MENTAL_DEBUFF;
			}
			if (template.getDispelCategory() == DispelCategoryType.DEBUFF_PHYSICAL) {
				return SKILLCTG_PHYSICAL_DEBUFF;
			}
		}
		if (template.getChainCondition() != null) return SKILLCTG_CHAIN_SKILL;
		return null;
	}
}
