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
package com.aionemu.gameserver.ai2.mechanics.adapters;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.aionemu.gameserver.ai2.mechanics.AIMechanics;
import com.aionemu.gameserver.ai2.mechanics.MechanicEventHandler;
import com.aionemu.gameserver.ai2.mechanics.MechanicEventType;
import javolution.util.FastMap;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class AIMechanicsAdapter extends XmlAdapter<AIMechanicsXML, AIMechanics> {
	
	@Override
	public AIMechanics unmarshal(AIMechanicsXML v) throws Exception {
		FastMap<MechanicEventType, MechanicEventHandler> handlers = new FastMap<MechanicEventType, MechanicEventHandler>();
		if (v.mechanics != null) for (MechanicEventHandler handler: v.mechanics) {
			handlers.put(handler.getType(), handler);
		}
		return new AIMechanics(v.name, handlers);
	}
	
	@Override
	public AIMechanicsXML marshal(AIMechanics v) throws Exception {
		AIMechanicsXML ret = new AIMechanicsXML();
		ret.name = v.mechanicId;
		List<MechanicEventHandler> mechanics = new ArrayList<MechanicEventHandler>();
		for (MechanicEventType type: MechanicEventType.values()) {
			MechanicEventHandler handler = v.getHandler(type);
			if (handler != null) mechanics.add(handler);
		}
		ret.mechanics = mechanics;
		return ret;
	}
}
