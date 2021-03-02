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
package ai.instance.nightmareCircus;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;

import ai.NoActionAI2;

@AIName("closed_cage")
public class ClosedCageAI2 extends NoActionAI2 {
	
	public void playerSkillUse(Player player, int skillId) {
		int skill;
		Npc npc = getPosition().getWorldMapInstance().getNpc(831573);
		if (npc == null) {
			return;
		}
		switch (skillId) {
		case 21327:
			skill = 21365;
			break;
		case 21328:
			skill = 21364;
			break;
		default:
			return;
		}
		if (isInRange(player, 10)) {
			AI2Actions.targetCreature(((NpcAI2) npc.getAi2()), npc);
			AI2Actions.useSkill(((NpcAI2) npc.getAi2()), skill);
			// ((NpcAI2) npc.getAi2()).playerSkillUse(player, skillId);
		}
	}
}
