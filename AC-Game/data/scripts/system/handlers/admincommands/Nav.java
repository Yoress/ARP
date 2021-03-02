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

import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.geo.nav.NavService;

/**
 * A very simple command for testing the {@link NavService}.
 * <p>
 * Used while a target is selected, an attempt will be made to pathfind from the admin using the command
 * to their target. The path (if it exists) will be listed back to the admin with position links.
 * <p>
 * Note: In the abyss, while pathfinding should still function, the position links may not point to the
 * correct location on the map. This also applies to any other maps with layers.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class Nav extends AdminCommand {
	
	public Nav() {
		super("nav");
	}

	@Override
	public void execute(Player admin, String... params) {
		Creature target = (Creature) admin.getTarget();
		if (target == null) {
			onFail(admin, "This command requires a target.");
			return;
		}
		long start = System.currentTimeMillis();
		float[][] points = NavService.getInstance().navigateToTarget(admin, target);
//		float[][] points = NavService.getInstance().navigateToTarget(target, admin);
		long end = System.currentTimeMillis();
		if (points == null) {
			onFail(admin, "No pathfinding available, mobs would walk in a straight line through any obstacles.");
			return;
		}
		
		if (points.length == 1 && isSamePoint(points[0], admin)) {
			onFail(admin, "Pathfinding failed, mobs would stand here and reset. Time elapsed: " + (end-start) + "ms");
			return;
		}
		onFail(admin, "Pathfinding Steps (Time elapsed: " + (end-start) + "ms" + "):");
		for (int i = 0; i < points.length; i++) {
			float[] p = points[i];
			onFail(admin, "[pos: Step " + (i + 1) + "; 1 " + admin.getWorldId() + " " + p[0] + " " + p[1] + " 0 0]");
		}
	}
	
	private boolean isSamePoint(float[] p1, Player admin) {
		if (p1[0] == admin.getX() && p1[1] == admin.getY() && p1[2] == admin.getZ()) return true;
		return false;
	}
	
}
