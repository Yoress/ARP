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
package com.aionemu.gameserver.skillengine.properties;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.skillengine.properties.Properties.CastState;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.geo.GeoService;

/**
 * @author ATracer
 * @modified Yon (Aion Reconstruction Project) -- Added an exception to NPC's finishing their cast so they don't need to be in range or have LoS.
 */
public class FirstTargetRangeProperty {
	
	/**
	 * @param skill
	 * @param properties
	 */
	public static boolean set(Skill skill, Properties properties, CastState castState) {
		float firstTargetRange = properties.getFirstTargetRange();
		if (!skill.isFirstTargetRangeCheck()) {
			return true;
		}
		
		Creature effector = skill.getEffector();
		Creature firstTarget = skill.getFirstTarget();
		
		if (firstTarget == null) {
			return false;
		}
		
		// Add Weapon Range to distance
		if (properties.isAddWeaponRange()) {
			firstTargetRange += (float) skill.getEffector().getGameStats().getAttackRange().getCurrent() / 1000f;
		}
		
		// on end cast check add revision distance value
		if (!castState.isCastStart()) {
			firstTargetRange += properties.getRevisionDistance();
		}
		
		if (firstTarget.getObjectId() == effector.getObjectId()) {
			return true;
		}
		
		//When an Npc is finishing a cast, the range and LoS no longer matter; return early. Note that this doesn't include Summons.
		if (effector instanceof Npc & !castState.isCastStart()) return true;
		
		if (!MathUtil.isInAttackRange(effector, firstTarget, firstTargetRange + 2)) {
			if (effector instanceof Player) {
				PacketSendUtility.sendPacket((Player) effector, SM_SYSTEM_MESSAGE.STR_ATTACK_TOO_FAR_FROM_TARGET);
			}
			return false;
		}
		
		// TODO check for all targets too
		// Summon Group Member exception
		if (skill.getSkillTemplate().getSkillId() != 1606) {
			if (!GeoService.getInstance().canSee(effector, firstTarget)) {
				if (effector instanceof Player) {
					PacketSendUtility.sendPacket((Player) effector, SM_SYSTEM_MESSAGE.STR_SKILL_OBSTACLE);
				}
				return false;
			}
		}
		return true;
	}
}
