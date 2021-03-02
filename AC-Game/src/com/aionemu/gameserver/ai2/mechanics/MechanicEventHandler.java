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
package com.aionemu.gameserver.ai2.mechanics;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.aionemu.gameserver.ai2.mechanics.actions.Action.DoNothing;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "event")
public class MechanicEventHandler {
	
	@XmlAttribute(name = "type")
	private MechanicEventType type;
	
	@XmlElement(name = "sequence")
	private Pattern[] patterns;
	
//	private MechanicEventHandler() {
//		//For JAXB
//	}
//	
//	public MechanicEventHandler(MechanicEventType type, Pattern[] patterns) { //Only needed for importing new data
//		this.type = type;
//		this.patterns = patterns;
//	}
	
	public MechanicEventType getType() {
		return type;
	}
	
	public boolean doPattern(MechanicEvent event, AbstractMechanicsAI2 ai) throws DoNothing {
		if (patterns != null) for (int i = 0; i < patterns.length; i++) {
			if (patterns[i] != null && patterns[i].checkConditions(event, ai)) {
				patterns[i].performActions(event, ai);
				return true;
			}
		}
		return false;
	}
	
}
