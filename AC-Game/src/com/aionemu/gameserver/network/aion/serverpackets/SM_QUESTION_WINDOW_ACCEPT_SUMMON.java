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
package com.aionemu.gameserver.network.aion.serverpackets;

import com.aionemu.gameserver.configs.administration.AdminConfig;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.AionServerPacket;
import com.aionemu.gameserver.network.aion.clientpackets.CM_QUESTION_ACCEPT_SUMMON_RESPONSE.TeleportDestination;


/**
 * This packet opens the dialog to accept a summon from another entity; the client will reply
 * with {@link com.aionemu.gameserver.network.aion.clientpackets.CM_QUESTION_ACCEPT_SUMMON_RESPONSE}.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class SM_QUESTION_WINDOW_ACCEPT_SUMMON extends AionServerPacket {
	
	private TeleportDestination tele;
	
	public SM_QUESTION_WINDOW_ACCEPT_SUMMON(TeleportDestination tele) {
		if (tele == null) throw new IllegalArgumentException("Teleport Destination cannot be null!");
		this.tele = tele;
	}
	
	@Override
	protected void writeImpl(AionConnection con) {
		PacketLoggerService.getInstance().logPacketSM(this.getPacketName());
		writeC(0x00); //Encoding?
		if (tele.summoner instanceof Player && ((Player) tele.summoner).isGM() && tele.skillId == 20657) {
			//This check means that an admin used a command to send a summon window to the player.
			Player gm = (Player) tele.summoner;
			String nameFormat = "%s";
			//Not checking GmMode for the tags because this sends a summon window, and random players are more likely to accept from a GM.
			if (AdminConfig.CUSTOMTAG_ENABLE /*&& gm.isGmMode()*/) {
				switch (gm.getAccessLevel()) {
					case 1:
						nameFormat = AdminConfig.CUSTOMTAG_ACCESS1;
						break;
					case 2:
						nameFormat = AdminConfig.CUSTOMTAG_ACCESS2;
						break;
					case 3:
						nameFormat = AdminConfig.CUSTOMTAG_ACCESS3;
						break;
					case 4:
						nameFormat = AdminConfig.CUSTOMTAG_ACCESS4;
						break;
					case 5:
						nameFormat = AdminConfig.CUSTOMTAG_ACCESS5;
						break;
					case 6:
						nameFormat = AdminConfig.CUSTOMTAG_ACCESS6;
						break;
					case 7:
						nameFormat = AdminConfig.CUSTOMTAG_ACCESS7;
						break;
					case 8:
						nameFormat = AdminConfig.CUSTOMTAG_ACCESS8;
						break;
					case 9:
						nameFormat = AdminConfig.CUSTOMTAG_ACCESS9;
						break;
					case 10:
						nameFormat = AdminConfig.CUSTOMTAG_ACCESS10;
						break;
				}
			}
			writeS(String.format(nameFormat, tele.summoner.getName())); //Name of summoner
		} else {
			writeS(tele.summoner.getName()); //Name of summoner
		}
		writeH(tele.skillId); //Skill ID? Why is it a short?
		writeH(tele.timeout); //Time to accept
	}
	
}
