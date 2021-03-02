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
package admincommands;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class Spawn extends AdminCommand {
	
	public Spawn() {
		super("spawn");
	}
	
	@Override
	public void execute(Player admin, String... params) {
		if (params.length < 1) {
			PacketSendUtility.sendMessage(admin, "syntax //spawn <template_id> [<x> <y> <z> <h>]");
			return;
		}
		
		int respawnTime = 0;
		int templateId = Integer.parseInt(params[0]);
		float x = admin.getX();
		float y = admin.getY();
		float z = admin.getZ();
		byte heading = admin.getHeading();
		int worldId = admin.getWorldId();
		
		if (params.length == 5) {
			x = Float.valueOf(params[1]);
			y = Float.valueOf(params[2]);
			z = Float.valueOf(params[3]);
			heading = Byte.valueOf(params[4]);
		}
		
		SpawnTemplate spawn = SpawnEngine.addNewSpawn(worldId, templateId, x, y, z, heading, respawnTime);
		
		if (spawn == null) {
			PacketSendUtility.sendMessage(admin, "There is no template with id " + templateId);
			return;
		}
		
		VisibleObject visibleObject = SpawnEngine.spawnObject(spawn, admin.getInstanceId());
		
		if (visibleObject == null) {
			PacketSendUtility.sendMessage(admin, "Spawn id " + templateId + " was not found!");
			return;
		}
		
		String objectName = visibleObject.getObjectTemplate().getName();
		PacketSendUtility.sendMessage(admin, objectName + " temporarily spawned");
	}
	
	@Override
	public void onFail(Player player, String message) {
		PacketSendUtility.sendMessage(player, "syntax //spawn <template_id> [<x> <y> <z> <h>]");
	}
	
}
