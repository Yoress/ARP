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
package playercommands;

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.serverpackets.SM_DIALOG_WINDOW;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.PlayerCommand;


/**
 * A simple command that makes the target send hyperlink 10.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class cmd_quest_select extends PlayerCommand {
	
	public cmd_quest_select() {
		super("questsel");
	}
	
	@Override
	public void execute(Player player, String... params) {
		Creature c = (Creature) player.getTarget();
		if (c instanceof Npc) {
			if (MathUtil.isIn3dRange(player, c, ((Npc) c).getObjectTemplate().getTalkDistance())) {
				PacketSendUtility.sendPacket(player, new SM_DIALOG_WINDOW(c.getObjectId(), 10));
			}
		}
	}
	
}
