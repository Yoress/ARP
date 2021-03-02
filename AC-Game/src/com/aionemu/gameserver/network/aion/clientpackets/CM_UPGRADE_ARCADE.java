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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.ArcadeUpgradeService;

/**
 * @author Raziel
 */
public class CM_UPGRADE_ARCADE extends AionClientPacket {
	
	private int action;
//	private int sessionId;
	
	public CM_UPGRADE_ARCADE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	@Override
	protected void readImpl() {
		action = readC();
		/*sessionId = */readD();
	}
	
	@Override
	protected void runImpl() {
		Player player = getConnection().getActivePlayer();
		
		if (player == null) {
			return;
		}
		
		switch (action) {
		case 0:// get start upgrade arcade info
			ArcadeUpgradeService.getInstance().startArcadeUpgrade(player);
			break;
		case 2:// try upgrade arcade
			ArcadeUpgradeService.getInstance().tryArcadeUpgrade(player);
			break;
		case 3:// get reward
			ArcadeUpgradeService.getInstance().getReward(player);
			break;
		case 5:// get reward list
			ArcadeUpgradeService.getInstance().showRewardList(player);
			break;
		}
	}
}
