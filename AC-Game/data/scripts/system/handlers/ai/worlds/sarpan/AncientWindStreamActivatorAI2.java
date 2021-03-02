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
package ai.worlds.sarpan;

import com.aionemu.gameserver.ai2.AI2Actions;
import com.aionemu.gameserver.ai2.AIName;
import com.aionemu.gameserver.ai2.NpcAI2;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.windstreams.Location2D;
import com.aionemu.gameserver.model.templates.windstreams.WindstreamTemplate;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_WINDSTREAM_ANNOUNCE;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 * @author xTz
 */
@AIName("ancient_windstream_activator")
public class AncientWindStreamActivatorAI2 extends NpcAI2 {
	
	@Override
	protected void handleSpawned() {
		super.handleSpawned();
		StaticDoor door = getPosition().getMapRegion().getDoors().get(146);
		door.setOpen(false);
		windStreamAnnounce(getOwner(), 0);
		PacketSendUtility.broadcastPacket(door, new SM_SYSTEM_MESSAGE(1401332));
		despawnNpc(207089);
		spawn(207081, 162.31667f, 2210.9192f, 555.0005f, (byte) 0, 2964);
	}
	
	private void startTask(final Npc npc) {
		ThreadPoolManager.getInstance().schedule(new Runnable() {
			
			@Override
			public void run() {
				Npc npc2 = (Npc) spawn(600020000, 207088, 158.58449f, 2204.0615f, 556.51917f, (byte) 0, 0, 1);
				windStreamAnnounce(npc2, 1);
				PacketSendUtility.broadcastPacket(npc2, new SM_SYSTEM_MESSAGE(1401331));
				spawn(207089, 158.58449f, 2204.0615f, 556.51917f, (byte) 0);
				PacketSendUtility.broadcastPacket(npc2, new SM_WINDSTREAM_ANNOUNCE(1, 600020000, 163, 1));
				
				if (npc2 != null) {
					npc2.getController().onDelete();
				}
				if (npc != null) {
					npc.getController().onDelete();
				}
			}
		}, 15000);
	}
	
	private void windStreamAnnounce(final Npc npc, final int state) {
		WindstreamTemplate template = DataManager.WINDSTREAM_DATA.getStreamTemplate(npc.getPosition().getMapId());
		for (Location2D wind : template.getLocations().getLocation()) {
			if (wind.getId() == 163) {
				wind.setState(state);
				break;
			}
		}
		npc.getPosition().getWorld().doOnAllPlayers(new Visitor<Player>() {
			
			@Override
			public void visit(Player player) {
				PacketSendUtility.sendPacket(player, new SM_WINDSTREAM_ANNOUNCE(1, 600020000, 163, state));
			}
		});
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
	
	@Override
	protected void handleDied() {
		Npc npc = (Npc) spawn(207087, 158.58449f, 2204.0615f, 556.51917f, (byte) 0);
		PacketSendUtility.broadcastPacket(getOwner(), new SM_SYSTEM_MESSAGE(1401330));
		getPosition().getMapRegion().getDoors().get(146).setOpen(true);
		despawnNpc(207081);
		super.handleDied();
		AI2Actions.deleteOwner(this);
		startTask(npc);
	}
	
	@Override
	public int modifyDamage(int damage) {
		return super.modifyDamage(1);
	}
}
