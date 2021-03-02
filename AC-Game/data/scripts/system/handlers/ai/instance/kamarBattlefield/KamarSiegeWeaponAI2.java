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
package ai.instance.kamarBattlefield;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;

import ai.ActionItemNpcAI2;

@AIName("kamarsiegeweapon")
public class KamarSiegeWeaponAI2 extends ActionItemNpcAI2 {
	
	@Override
	protected void handleDialogStart(Player player) {
		super.handleDialogStart(player);
	}
	
	@Override
	protected void handleUseItemFinish(Player player) {
		switch (getNpcId()) {
		case 701807:
			SkillEngine.getInstance().getSkill(player, 21403, 1, player).useNoAnimationSkill();
			if (player.getInventory().getItemCountByItemId(164000262) < 1) {
				return;
			}
			break;
		case 701808:
			SkillEngine.getInstance().getSkill(player, 21404, 1, player).useNoAnimationSkill();
			if (player.getInventory().getItemCountByItemId(164000262) < 1) {
				return;
			}
			break;
		case 701902:
		case 701806:
			World.getInstance().updatePosition(player, getOwner().getX(), getOwner().getY(), getOwner().getZ(), (byte) getOwner().getHeading());
			PacketSendUtility.broadcastPacketAndReceive(player, new SM_FORCED_MOVE(player, player));
			SkillEngine.getInstance().getSkill(player, 21409, 1, player).useNoAnimationSkill();
			break;
		}
		AI2Actions.dieSilently(this, player);
		AI2Actions.deleteOwner(this);
	}
}
