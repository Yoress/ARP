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
package com.aionemu.gameserver.ai2.mechanics.actions;

import com.aionemu.commons.utils.Rnd;
import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.context.ObjIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.item.ItemService;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class GiveItemByObjIndicatorAction extends Action {
	
	final public ObjIndicator receiver;
	
	final public int itemId;
	
	final public int min;
	
	final public int max;
	
	public GiveItemByObjIndicatorAction(ObjIndicator receiver, int itemId, int min, int max) {
		super(ActionType.give_item_by_obj_indicator);
		this.receiver = receiver;
		this.itemId = itemId;
		this.min = min;
		this.max = max;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		int Unused_So_I_Know_This_Class_Is_Not_In_An_Acceptable_State = 0;
		VisibleObject obj = event.getObjectIndicator(receiver, ai);
		if (obj instanceof Player) {
			//TODO: Maybe this is meant to be added to the loot?
			ItemService.addItem((Player) obj, itemId, Rnd.get(min, max));
		}
	}
	
	@Override
	public int hashCode() {
		return 3*receiver.ordinal() + 5*itemId + 7*min + 11*max;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GiveItemByObjIndicatorAction) {
			GiveItemByObjIndicatorAction o = (GiveItemByObjIndicatorAction) obj;
			return (o.receiver == receiver && o.itemId == itemId && o.min == min && o.max == max);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [item:" + itemId + "] --> [" + receiver + "]: <" + min + ", " + max + ">";
	}
	
}
