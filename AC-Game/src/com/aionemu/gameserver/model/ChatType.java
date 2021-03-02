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
package com.aionemu.gameserver.model;

/**
 * Chat types that are supported by Aion.
 *
 * @author SoulKeeper, Imaginary
 */
public enum ChatType {
	
	NORMAL(0x00), // Normal chat (White)
	SHOUT(0x03), // Shout chat (Orange)
	WHISPER(0x04), // Whisper chat (Green)
	GROUP(0x05), // Group chat (Blue)
	ALLIANCE(0x06), // Alliance chat (Aqua)
	GROUP_LEADER(0x07), // Group Leader chat (???)
	LEAGUE(0x08), // League chat (Dark Blue)
	LEAGUE_ALERT(0x09), // League chat (Orange)
	LEGION(0x0A), // Legion chat (Green)
	COMMAND(0x18), // Command chat (Yellow)
	
	CH1(0x0E),
	CH2(0x0F),
	CH3(0x10),
	CH4(0x11),
	CH5(0x12),
	CH6(0x13),
	CH7(0x14),
	CH8(0x15),
	CH9(0x16),
	CH10(0x17),
	/**
	 * Global chat types
	 */
	GOLDEN_YELLOW(0x19, true), // System message (Dark Yellow), most commonly
								 // used, no "center" equivalent.
	
	WHITE(0x1E, true), // System message (White), visible in "All" chat
						 // thumbnail only !
	YELLOW(0x1F, true), // System message (Yellow), visible in "All" chat
						 // thumbnail only !
	BRIGHT_YELLOW(0x20, true), // System message (Light Yellow), visible in
								 // "All" chat thumbnail only !
	WHITE_CENTER(0x21, true), // Periodic Notice (White && Box on screen center)
	YELLOW_CENTER(0x22, true), // Periodic Announcement(Yellow && Box on screen
								 // center)
	BRIGHT_YELLOW_CENTER(0x23, true); // System Notice (Light Yellow && Box on
										 // screen center)
	
	private final int intValue;
	private boolean sysMsg;
	
	/**
	 * Constructor client chat type integer representation
	 */
	private ChatType(int intValue) {
		this(intValue, false);
	}
	
	/**
	 * Converts ChatType value to integer representation
	 *
	 * @return chat type in client
	 */
	public int toInteger() {
		return intValue;
	}
	
	/**
	 * Returns ChatType by it's integer representation
	 *
	 * @param integerValue integer value of chat type
	 * @return ChatType
	 * @throws IllegalArgumentException if can't find suitable chat type
	 */
	public static ChatType getChatTypeByInt(int integerValue) throws IllegalArgumentException {
		for (ChatType ct : ChatType.values()) {
			if (ct.toInteger() == integerValue) {
				return ct;
			}
		}
		
		throw new IllegalArgumentException("Unsupported chat type: " + integerValue);
	}
	
	private ChatType(int intValue, boolean sysMsg) {
		this.intValue = intValue;
		this.sysMsg = sysMsg;
	}
	
	/**
	 * @return true if this is one of system message ( all races can read chat )
	 */
	public boolean isSysMsg() {
		return sysMsg;
	}
}
