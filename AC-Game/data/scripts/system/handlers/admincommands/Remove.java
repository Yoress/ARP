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

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.abyss.AbyssPointsService;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;


/**
 * Used to remove various things. Does not support negative removal amounts.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class Remove extends AdminCommand {
	
	public Remove() {
		super("remove");
	}
	
	private static enum RemoveContext {
		AP,
		GP,
		KINAH,
		EXP;
	}
	
	final static String SYNTAX = "Syntax: //remove <ap | gp | kinah | exp> <amount> [PlayerName]\nIf you don't provide [PlayerName], your target will be used.";
	
	@Override
	public void execute(Player admin, String... params) {
		if (params == null || params.length < 2) {
			onFail(admin, SYNTAX);
		}
		
		RemoveContext remove;
		try {
			 remove = RemoveContext.valueOf(params[0].toUpperCase());
		} catch (IllegalArgumentException e) {
			onFail(admin, "Unable to parse command; " + SYNTAX);
			return;
		}
		
		int amount = 0;
		try {
			amount = Integer.parseInt(params[1]);
			if (amount < 0) {
				onFail(admin, "Parsed amount was negative, aborting command.");
				return;
			}
		} catch (NumberFormatException e) {
			onFail(admin, "Unable to parse amount: " + e.getMessage() + "\n" + SYNTAX);
			return;
		}
		
		Player target = null;
		if (admin.getTarget() instanceof Player) {
			target = (Player) admin.getTarget();
		} else if (params.length > 3) {
			onFail(admin, "Selecting players by name is not implemented yet; aborting command.");
			return;
		} else {
			target = admin;
		}
		
		switch (remove) {
			case AP:
				AbyssPointsService.addAp(target, -amount);
				break;
			case EXP:
				target.getCommonData().setExp(target.getCommonData().getExp() - amount);
				break;
			case GP:
				AbyssPointsService.addGp(target, -amount);
				break;
			case KINAH:
				target.getInventory().decreaseKinah(amount);
				break;
			default:
				onFail(admin, "Unsupported removal operation: " + remove);
		}
		
		onFail(admin, "Removed " + amount + " " + remove + ".");
		
	}
	
}
