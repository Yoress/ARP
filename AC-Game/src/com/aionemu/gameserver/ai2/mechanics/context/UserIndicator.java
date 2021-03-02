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
package com.aionemu.gameserver.ai2.mechanics.context;

import javax.xml.bind.annotation.XmlType;

/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlType
public enum UserIndicator {
	USERI_SEEN,
	USERI_ATTACKER,
	USERI_CASTER,
	USERI_EVENT_TARGET,
	USERI_KILLER,
	USERI_TALKER,
	USERI_EVENT_MAKER,
	USERI_MASTER;
	
	public ObjIndicator getObjIndicator() {
		switch (this) {
			case USERI_ATTACKER:
				return ObjIndicator.OBJI_ATTACKER;
			case USERI_CASTER:
				return ObjIndicator.OBJI_CASTER;
			case USERI_EVENT_TARGET:
				return ObjIndicator.OBJI_EVENT_TARGET;
			case USERI_SEEN:
				return ObjIndicator.OBJI_SEEN;
			case USERI_EVENT_MAKER:
				return ObjIndicator.OBJI_EVENT_MAKER;
			case USERI_KILLER:
				return ObjIndicator.OBJI_KILLER;
			case USERI_MASTER:
				return ObjIndicator.OBJI_MASTER;
			case USERI_TALKER:
				return ObjIndicator.OBJI_TALKER;
			default:
				throw new UnsupportedOperationException();
		}
	}
}
