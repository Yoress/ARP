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
package com.aionemu.gameserver.network.aion.serverpackets;

import java.util.ArrayList;
import java.util.Map;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;

/**
 * Can be used to flash skills on the client, or update cooldowns.
 * 
 * @author ATracer, nrg, Eloann
 * @reworked Yon (Aion Reconstruction Project)
 */
public class SM_SKILL_COOLDOWN extends AionServerPacket {
	
	private Map<Integer, Long> cooldowns;
	private boolean backwardsCompat;
	
	private Map<SkillTemplate, Long> skillCooldowns;
	private SkillTemplate skill;
	private long singleCd;
	
	private boolean flash;
	
	
	public SM_SKILL_COOLDOWN(Map<Integer, Long> cooldowns) {
		this.cooldowns = cooldowns;
		backwardsCompat = true;
	}
	
	public SM_SKILL_COOLDOWN(Map<SkillTemplate, Long> skillCooldowns, boolean flash) {
		this.skillCooldowns = skillCooldowns;
		this.flash = flash;
		backwardsCompat = false;
	}
	
	public SM_SKILL_COOLDOWN(SkillTemplate skill, long cd, boolean flash) {
		this.skill = skill;
		singleCd = cd;
		this.flash = flash;
		backwardsCompat = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void writeImpl(AionConnection con) {
		PacketLoggerService.getInstance().logPacketSM(this.getPacketName());
		if (backwardsCompat) {
			writeH(calculateSize());
			writeC(1); // unk 0 or 1
			long currentTime = System.currentTimeMillis();
			for (Map.Entry<Integer, Long> entry : cooldowns.entrySet()) {
				int left = (int) ((entry.getValue() - currentTime) / 1000);
				ArrayList<Integer> skillsWithCooldown = DataManager.SKILL_DATA.getSkillsForCooldownId(entry.getKey());
				
				for (int index = 0; index < skillsWithCooldown.size(); index++) {
					int skillId = skillsWithCooldown.get(0);
					writeH(skillId);
					writeD(left > 0 ? left : 0);
					writeD(DataManager.SKILL_DATA.getSkillTemplate(skillId).getCooldown() * 100);
				}
			}
		} else {
			/*
			 * Short for "size" -- doesn't seem to matter?
			 * Byte for flash update.
			 * Short for Skill ID
			 * int for remaining time
			 * int for cooldown length (determines how fast the client spins the cd indicator over the skill icon)
			 */
			if (skillCooldowns == null) {
				if (skill == null) return;
				writeH(1);
				writeC(((flash) ? (1) : (0)));
				int left = (int) ((singleCd - System.currentTimeMillis())/1000);
				writeH(skill.getSkillId());
				writeD(left > 0 ? left : 0);
				writeD(skill.getCooldown() * 100);
			} else {
				writeH(skillCooldowns.size());
				writeC(((flash) ? (1) : (0)));
				for (Map.Entry<SkillTemplate, Long> cooldown: skillCooldowns.entrySet()) {
					int left = (int) ((cooldown.getValue() - System.currentTimeMillis())/1000);
					writeH(cooldown.getKey().getSkillId());
					writeD(left > 0 ? left : 0);
					writeD(cooldown.getKey().getCooldown() * 100);
				}
			}
		}
	}
	
	private int calculateSize() {
		int size = 0;
		for (Map.Entry<Integer, Long> entry : cooldowns.entrySet()) {
			size += DataManager.SKILL_DATA.getSkillsForCooldownId(entry.getKey()).size();
		}
		return size;
	}
}
