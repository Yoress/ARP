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
package ai.siege;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.knownlist.Visitor;

import ai.ActionItemNpcAI2;

/**
 * @author Eloann
 */
@AIName("danaria_siege_gate")
public class DanariaSiegeGateAI2 extends ActionItemNpcAI2 {
	
	@Override
	protected void handleDialogStart(Player player) {
		super.handleDialogStart(player);
	}
	
	@Override
	protected void handleUseItemFinish(Player player) {
		switch (getNpcId()) {
		case 701783:
			despawnNpc(273286);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_LDF5B_6021_OUT_DOOR_01_DESPAWN);
			break;
		case 701784:
			despawnNpc(273289);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_LDF5B_6021_OUT_DOOR_02_DESPAWN);
			break;
		case 701785:
			despawnNpc(273285);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_LDF5B_6021_OUT_DOOR_01_DESPAWN);
			break;
		case 701786:
			despawnNpc(273288);
			PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_LDF5B_6021_OUT_DOOR_02_DESPAWN);
			break;
		}
	}
	
	private void despawnNpc(final int npcId) {
		getKnownList().doOnAllNpcs(new Visitor<Npc>() {
			
			@Override
			public void visit(Npc npc) {
				if (npc.getNpcId() == npcId) {
					npc.getController().onDelete();
				}
			}
		});
	}
}
