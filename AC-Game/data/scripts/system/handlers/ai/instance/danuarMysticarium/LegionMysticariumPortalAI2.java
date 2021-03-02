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
package ai.instance.danuarMysticarium;

import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.ai.GeneralNpcAI2;
import com.aionemu.gameserver.ai2.handler.TalkEventHandler;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.siege.SiegeLocation;
import com.aionemu.gameserver.model.team.legion.Legion;
import com.aionemu.gameserver.services.SiegeService;

/**
 * @author DeathMagnestic
 * @modified Yon (Aion Reconstruction Project) -- This class was a copy/paste of GeneralNpcAI2 with the exception of
 * {@link #handleDialogStart(Player)}... Changed the super class to GeneralNpcAI2 and removed all other methods.
 */
@AIName("legion_mysticarium_portal")
public class LegionMysticariumPortalAI2 extends GeneralNpcAI2 {
	
	@Override
	protected void handleDialogStart(Player player) {
		final SiegeLocation loc = SiegeService.getInstance().getSiegeLocation(5011);
		Legion playerlegion = player.getLegion();
		if (player.getLegion() != null) {
			if (playerlegion.getLegionId() == loc.getLegionId()) {
				TalkEventHandler.onTalk(this, player);
			}
		} else {
			TalkEventHandler.onFinishTalk(this, player);
		}
	}
	
}
