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
package ai.instance.illuminaryObelisk;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.teleport.TeleportService2;

import ai.ActionItemNpcAI2;

@AIName("obrlisk_dportal")
public class ObeliskDoublePortalAI2 extends ActionItemNpcAI2 {
	
	@Override
	protected void handleUseItemFinish(Player player) {
		switch (getNpcId()) {
		case 730886: // Portal
			switch (player.getWorldId()) {
			case 301230000: // Illuminary Obelisk
				TeleportService2.teleportTo(player, 301230000, 255.1f, 269.05f, 455.2f, (byte) 90, TeleportAnimation.BEAM_ANIMATION);
				break;
			}
			switch (player.getWorldId()) {
			case 301370000: // Illuminary Obelisk Hero
				TeleportService2.teleportTo(player, 301370000, 255.1f, 269.05f, 455.2f, (byte) 90, TeleportAnimation.BEAM_ANIMATION);
				break;
			}
			break;
		}
	}
}
