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
package com.aionemu.gameserver.model.templates;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Temporary data holding for {@link com.aionemu.gameserver.dataholders.QuestConnectionsData QuestConnectionsData}
 * so JAXB may load the xml document containing this information.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlRootElement(name = "chain")
public class ConnectedQuestTemplate {
	
	/**
	 * The quest {@link #connectedQuest} will chain from.
	 */
	@XmlAttribute(name = "parent_quest", required = true)
	private int parentQuest;
	
	/**
	 * The quest {@link #parentQuest} will chain to.
	 */
	@XmlAttribute(name = "con_quest", required = true)
	private int connectedQuest;
	
	/**
	 * @return {@link #parentQuest}.
	 */
	public int getParent() {
		return parentQuest;
	}
	
	/**
	 * @return {@link #connectedQuest}.
	 */
	public int getConnection() {
		return connectedQuest;
	}
}
