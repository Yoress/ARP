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
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * This command is intended to break links. The syntax is: {@code //unlink [Link]}
 * <p>
 * When a player uses this command, the expected result is a message containing the content of the link passed in as a parameter. A GM can break any
 * link, but a normal player is limited to item, quest, and recipe links. Furthermore, normal players are restricted from receiving the full content
 * of the link; the cutoff is the first semicolon of the link.
 * <p>
 * For the purposes of this command, a link is defined as text within brackets containing a colon, i.e.: [this: is a link]. A link is further defined on the client side,
 * but that is unnecessary for this command.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class Unlink extends AdminCommand {
	
	public Unlink() {
		super("unlink");
	}
	
	/**
	 * A standard message that may be sent to the player.
	 */
	private static final String NO_LINK_MESSAGE = "Unable to parse link: ", MISUSE_MESSAGE = "You do not have permission to break the given link type.",
			HELP_MESSAGE = "This command breaks links.\n" + "Syntax: \"//unlink -c [Link]\"\n"
					+ "The -c argument can be omitted. It specifies to return the link contents as a pathfinding link for easy copying.\n\n"
					+ "GM's can break any link. Normal players are restricted to item, quest, and recipe links.";
	
	@Override
	public void execute(Player admin, String... params) {
		if (params.length < 1 || (params.length == 1 && (params[0].equalsIgnoreCase("help") || params[0].equalsIgnoreCase("?")))) {
			onFail(admin, HELP_MESSAGE);
			return;
		}
		boolean copy = false;
		if (params[0].equalsIgnoreCase("-c")) copy = true;
		String link = "";
		if (copy) {
			for (int i = 1; i < params.length; i++) {
				if (i == params.length - 1) {
					link += params[i];
				} else {
					link += params[i] + " ";
				}
			}
		} else {
			for (int i = 0; i < params.length; i++) {
				if (i == params.length - 1) {
					link += params[i];
				} else {
					link += params[i] + " ";
				}
			}
		}
		if (!link.startsWith("[")) {
			onFail(admin, NO_LINK_MESSAGE + link);
			return;
		}
		if (!link.endsWith("]")) {
			onFail(admin, NO_LINK_MESSAGE + link);
			return;
		}
		if (!link.contains(":")) {
			onFail(admin, NO_LINK_MESSAGE + link);
			return;
		}
		if (!isSingleLink(link)) {
			onFail(admin, NO_LINK_MESSAGE + "input appears to be multiple links.");
			return;
		}
		link = link.substring(1);
		link = link.substring(0, link.length() - 1);
		if (admin.isGM()) {
			sendLink(admin, link, copy);
		} else {
			if (link.startsWith("item") || link.startsWith("quest") || link.startsWith("recipe")) {
				int i = link.indexOf(";");
				sendLink(admin, ((i == -1) ? (link) : (link.substring(0, i))), copy);
			} else {
				onFail(admin, MISUSE_MESSAGE);
			}
		}
	}
	
	/**
	 * This method exists for the sole purpose of calling {@link #onFail(Player, String)} in a way that does not imply failure to execute.
	 * <p>
	 * This was done because the {@link #onFail(Player, String) onFail} method sends the given String to the given Player as a normal message -- the
	 * behaviour needed.
	 * <p>
	 * Optionally, the link contents can be converted into a pathfinding link so the player may right-click copy in-game.
	 * 
	 * @param admin -- The Player to send the link to.
	 * @param link -- The link content to send to the given Player.
	 * @param copyable -- Whether or not to send the link contents as a pathfinding link.
	 */
	private void sendLink(Player admin, String link, boolean copyable) {
		onFail(admin, "\n" + ((copyable) ? (((admin.isGM()) ? ("NOTE: Semi-colons in the link have been replaced with pipes.\n") : ("")) + "[where:" + link.replace(';', '|') + "]") : (link)));
	}
	
	/**
	 * Verifies that there is only one colon (':') character in the string. This isn't the best approach for this,
	 * but the given string should never be so massive that it's a concern.
	 * <p>
	 * Note that if no colon is found, this method will return false.
	 * 
	 * @param link -- The string to confirm the existence of a single colon within.
	 * @return true if only a single colon was found, false otherwise.
	 */
	private boolean isSingleLink(String link) {
		boolean found = false;
		for (char c: link.toCharArray()) {
			if (c == ':') {
				if (found) {
					return false;
				} else {
					found = true;
				}
			}
		}
		return found;
	}
	
}
