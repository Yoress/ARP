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
package com.aionemu.gameserver.services.mail;

/**
 * @author Rolandas
 */
public enum AuctionResult {
	
	FAILED_BID(0), //Return bid
	CANCELED_BID(1), //Return sale fee
	FAILED_SALE(2), //Return house to owner, refund nothing
	SUCCESS_SALE(3), //Give previous owner the sale price minus 10%, plus sale fee
	WIN_BID(4), //Give house to new owner, refund nothing
	GRACE_START(5), //Keep new house inactive; give notice to sell old house or lose it next cycle.
	GRACE_FAIL(6), //Lose new house because old house not sold; get "small compensation for loss"
	GRACE_SUCCESS(7); //Sold old house in time, became owner of new house, refund nothing.
	
	private int value;
	
	private AuctionResult(int value) {
		this.value = value;
	}
	
	public int getId() {
		return this.value;
	}
	
	public static AuctionResult getResultFromId(int resultId) {
		for (AuctionResult result : AuctionResult.values()) {
			if (result.getId() == resultId) {
				return result;
			}
		}
		return null;
	}
}
