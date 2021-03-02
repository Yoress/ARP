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
public enum ObjIndicator {
	OBJI_SELF,
	OBJI_SEEN,
	OBJI_CUR_TARGET,
	OBJI_EVENT_TARGET,
	OBJI_ATTACKER,
	OBJI_CASTER,
	OBJI_KILLER,
	OBJI_FRIEND,
	OBJI_FLEE_FROM,
	OBJI_TALKER,
	OBJI_MESSAGE_PARAM,
	OBJI_MESSAGE_SENDER,
	OBJI_PARTY_MEMBER,
	OBJI_EVENT_MAKER,
	OBJI_MASTER;
}
