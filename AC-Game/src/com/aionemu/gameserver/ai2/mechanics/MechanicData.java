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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.ai2.mechanics.actions.Action;
import com.aionemu.gameserver.ai2.mechanics.conditions.Condition;

import javolution.util.FastMap;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "mechanics")
public class MechanicData {
	
	@XmlTransient
	FastMap<Action, Action> actions = new FastMap<Action, Action>();
	
	@XmlTransient
	FastMap<Condition, Condition> conditions = new FastMap<Condition, Condition>();
	
	@XmlElement(name = "mechanic")
	List<AIMechanics> mechanics;
	
//	public void setList(List<AIMechanics> mechanics) { //Only needed for importing new data
//		this.mechanics = mechanics;
//	}
	
}
