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
import com.aionemu.gameserver.model.gameobjects.player.ReviveType;
import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.services.player.PlayerReviveService;

/**
 * @author ATracer, orz, avol, Simple
 */
public class CM_REVIVE extends AionClientPacket {
	
	private int reviveId;
	
	/**
	 * Constructs new instance of <tt>CM_REVIVE </tt> packet
	 *
	 * @param opcode
	 */
	public CM_REVIVE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		PacketLoggerService.getInstance().logPacketCM(this.getPacketName());
		reviveId = readC();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		Player activePlayer = getConnection().getActivePlayer();
		
		if (!activePlayer.getLifeStats().isAlreadyDead()) {
			return;
		}
		
		if (activePlayer.getBattleGround() != null) {
			PlayerReviveService.battlegroundRevive(activePlayer);
			return;
		}
		
		ReviveType reviveType = ReviveType.getReviveTypeById(reviveId);
		
		switch (reviveType) {
		case BIND_REVIVE:
		case OBELISK_REVIVE:
			PlayerReviveService.bindRevive(activePlayer);
			break;
		case REBIRTH_REVIVE:
			PlayerReviveService.rebirthRevive(activePlayer);
			break;
		case ITEM_SELF_REVIVE:
			PlayerReviveService.itemSelfRevive(activePlayer);
			break;
		case SKILL_REVIVE:
			PlayerReviveService.skillRevive(activePlayer);
			break;
		case KISK_REVIVE:
			PlayerReviveService.kiskRevive(activePlayer);
			break;
		case INSTANCE_REVIVE:
			PlayerReviveService.instanceRevive(activePlayer);
			break;
		default:
			break;
		}
		
	}
}
