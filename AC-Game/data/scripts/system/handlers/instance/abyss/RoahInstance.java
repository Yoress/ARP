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
package instance.abyss;

import java.util.List;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.instance.handlers.GeneralInstanceHandler;
import com.aionemu.gameserver.instance.handlers.InstanceID;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.flyring.FlyRing;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.flyring.FlyRingTemplate;
import com.aionemu.gameserver.model.utils3d.Point3D;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUEST_ACTION;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.WorldMapInstance;

/**
 * @author bobobear
 */
@InstanceID(300070000)
public class RoahInstance extends GeneralInstanceHandler {
	
	private boolean isStartTimer = false;
	private long startTime;
	private boolean isInstanceDestroyed = false;
	private Race instanceRace;
	
	@Override
	public void onInstanceCreate(WorldMapInstance instance) {
		super.onInstanceCreate(instance);
		spawnRings();
		spawnGoldChest();
	}
	
	private void spawnRings() {
		FlyRing f1 = new FlyRing(new FlyRingTemplate("ROAH_WING_1", mapId, new Point3D(501.77, 409.53, 94.12), new Point3D(503.93, 409.65, 98.9), new Point3D(506.26, 409.7, 94.15), 10), instanceId);
		f1.spawn();
	}
	
	@Override
	public boolean onPassFlyingRing(Player player, String flyingRing) {
		if (flyingRing.equals("ROAH_WING_1")) {
			if (!isStartTimer) {
				PacketSendUtility.sendPacket(player, SM_SYSTEM_MESSAGE.STR_MSG_INSTANCE_START_IDABRE);
				isStartTimer = true;
				startTime = System.currentTimeMillis();
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 900));
				ThreadPoolManager.getInstance().schedule(new Runnable() {
					
					@Override
					public void run() {
						despawnNpcs(getNpcs(700472));
						despawnNpcs(getNpcs(700473));
						despawnNpcs(getNpcs(700474));
						despawnNpcs(getNpcs(701489));
						despawnNpcs(getNpcs(701484));
					}
				}, 900000);
			}
		}
		return false;
	}
	
	@Override
	public void onEnterInstance(Player player) {
		if (isStartTimer) {
			long time = System.currentTimeMillis() - startTime;
			if (time < 900000) {
				PacketSendUtility.sendPacket(player, new SM_QUEST_ACTION(0, 900 - (int) time / 1000));
			}
		}
		
	}
	
	private List<Npc> getNpcs(int npcId) {
		if (!isInstanceDestroyed) {
			return instance.getNpcs(npcId);
		}
		return null;
	}
	
	private void despawnNpcs(List<Npc> npcs) {
		for (Npc npc : npcs) {
			npc.getController().onDelete();
		}
	}
	
	private void spawnGoldChest() {
		final int chestId = instanceRace == Race.ELYOS ? 701484 : 701489;
		spawn(chestId, 504.203225f, 503.309275f, 88.97879f, (byte) 32);
	}
	
	@Override
	public void onInstanceDestroy() {
		isInstanceDestroyed = true;
	}
	
	@Override
	public void onPlayerLogOut(Player player) {
		TeleportService2.moveToInstanceExit(player, mapId, player.getRace());
	}
}
