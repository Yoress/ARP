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
package com.aionemu.gameserver.network.aion.clientpackets;

import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.network.PacketLoggerService;
import com.aionemu.gameserver.network.aion.AionClientPacket;
import com.aionemu.gameserver.network.aion.AionConnection.State;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;

import gnu.trove.map.hash.TIntObjectHashMap;


/**
 * Sent by the client in response to {@link com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW_ACCEPT_SUMMON}.
 * <p>
 * 0 -- accept, 1 -- decline, 2 -- timeout
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class CM_QUESTION_ACCEPT_SUMMON_RESPONSE extends AionClientPacket {
	
	/**
	 * A map containing player object id's paired with {@link TeleportDestination} objects.
	 * <p>
	 * When a player is considering accepting a summon, their destination will be stored here.
	 * If the player has not responded to a summon request, then their object id will exist
	 * in this map -- so this map should be checked before trying to summon a player to see
	 * if they are busy.
	 */
	private static final TIntObjectHashMap<TeleportDestination> TELEPORT_COORDINATES = new TIntObjectHashMap<TeleportDestination>();
	
	/**
	 * The response from the client.
	 * <p>
	 * 0 -- accept, 1 -- decline, 2 -- timeout
	 */
	private int response;
	
	public CM_QUESTION_ACCEPT_SUMMON_RESPONSE(int opcode, State state, State... restStates) {
		super(opcode, state, restStates);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void readImpl() {
		response = readC();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void runImpl() {
		PacketLoggerService.getInstance().logPacketCM(this.getPacketName());
		Player player = getConnection().getActivePlayer();
		if (player == null) return;
		int key = player.getObjectId();
		synchronized (TELEPORT_COORDINATES) {
			//0 -- accept, 1 -- decline, 2 -- timeout
			switch (response) {
				case 0: //Accept
					TeleportDestination td = TELEPORT_COORDINATES.remove(key);
					if (td == null) return;
					TeleportService2.teleportTo(player, td.worldId, td.instanceId, td.x, td.y, td.z, td.h, TeleportAnimation.BEAM_ANIMATION);
					return;
				case 1: //Decline
				case 2: //Timeout
					TeleportDestination rejected = TELEPORT_COORDINATES.remove(key);
					if (rejected != null && rejected.summoner instanceof Player) {
						if (response == 1)
							PacketSendUtility.sendPacket((Player) rejected.summoner, SM_SYSTEM_MESSAGE.STR_MSG_Recall_Rejected_EFFECT(player.getName()));
						else
							PacketSendUtility.sendPacket((Player) rejected.summoner, SM_SYSTEM_MESSAGE.STR_MSG_Recall_DONOT_ACCEPT_EFFECT(player.getName()));
					}
					if (rejected != null && rejected.summoner != null) {
						PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_Recall_Reject_EFFECT(rejected.summoner.getName()));
					}
					return;
				default: assert false:"Unknown response from client.";
			}
		}
	}
	
	
	/**
	 * Small holder class for summoner, skillId, destination coordinates, and timeout length.
	 * 
	 * @author Yon (Aion Reconstruction Project)
	 */
	public static class TeleportDestination {
		public final Creature summoner;
		public final int skillId, worldId, instanceId, timeout;
		public final float x, y, z;
		public final byte h;
		
		private TeleportDestination(Creature summoner, int skillId, int worldId, int instanceId, float x, float y, float z, byte h, int timeout) {
			this.summoner = summoner; this.skillId = skillId;
			this.worldId = worldId; this.instanceId = instanceId; this.x = x; this.y = y; this.z = z; this.h = h;
			this.timeout = timeout;
		}
		
		/**
		 * Adds destination information for the given player to {@link #TELEPORT_COORDINATES}, and
		 * returns the {@link TeleportDestination} object. This method may also return null if
		 * the given player is already considering a summon request or is otherwise busy.
		 * 
		 * @param summoned -- the player being summoned.
		 * @param summoner -- the entity that is summoning the player.
		 * @param skillId -- the skillId of the summoning skill being used; 0 defaults to 1606 ("Summon Group Member I").
		 * @param worldId -- the map id of the destination.
		 * @param instanceId -- the map channel of the destination.
		 * @param x -- the x coordinate of the destination.
		 * @param y -- the y coordinate of the destination.
		 * @param z -- the z coordinate of the destination.
		 * @param h -- the heading of the destination.
		 * @param timeout -- the number of seconds before this request expires.
		 * @return The {@link TeleportDestination} or null if the player is busy.
		 */
		public static TeleportDestination addTeleportRequestFor(Player summoned, Creature summoner, int skillId, int worldId, int instanceId, float x, float y, float z, byte h, int timeout) {
			synchronized (TELEPORT_COORDINATES) {
				//FIXME: Check if player is busy with something else.
				if (TELEPORT_COORDINATES.contains(summoned.getObjectId())/* || summoned.isBusy()*/) return null;
				if (skillId == 0) skillId = 1606; //skillId of "Summon Group Member I"
				final int playerObjectId = summoned.getObjectId();
				final TeleportDestination destination = new TeleportDestination(summoner, skillId, worldId, instanceId, x, y, z, h, timeout);
				TELEPORT_COORDINATES.put(playerObjectId, destination);
				//Schedule to remove one minute after it expires just in case the client poofs.
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					@Override
					public void run() {
						synchronized (TELEPORT_COORDINATES) {
							TeleportDestination old = TELEPORT_COORDINATES.get(playerObjectId);
							if (old == destination) TELEPORT_COORDINATES.remove(playerObjectId);
						}
					}
				}, (timeout * 1000) + 60000);
				return destination;
			}
		}
	}
}
