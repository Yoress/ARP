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
package com.aionemu.gameserver.dataholders;

import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.aionemu.gameserver.model.templates.ConnectedQuestTemplate;

import gnu.trove.map.hash.TIntIntHashMap;

/**
 * Stores data regarding chained quests. When a quest is chained to another, the starting quest dialog of the quest
 * it is chained to should be displayed when the quest is completed instead of the dialog page being closed.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "connected_quests")
public class QuestConnectionsData {
	
	/**
	 * Temporary list of {@link ConnectedQuestTemplate ConnectedQuestTemplates} that will be used to load
	 * chained quest data.
	 */
	@XmlElement(name = "chain")
	private List<ConnectedQuestTemplate> conQuests;
	
	/**
	 * A map containing information about chained quests. When a quest is completed,
	 * if the questId is present as a KEY value in this map, then the corresponding VALUE
	 * is the questId of the quest that should be chained into. This map is constructed with
	 * an initial capacity of 725, as that is the number of Simple quests that chain into others.
	 * <p>
	 * Example:
	 * A Bloody Task (2102) chains directly into The Sprigg Report (2103) upon completion.
	 * 
	 * This map would contain 2102 as a KEY, linking to 2103 as a VALUE.
	 */
	@XmlTransient
	private final TIntIntHashMap connectedQuests = new TIntIntHashMap(725);
	
	/**
	 * Used by JAXB. After loading the XML document containing this data into {@link #conQuests}, we move
	 * the memory heavy (relatively speaking -- these objects are quite light)
	 * {@link ConnectedQuestTemplate ConnectedQuestTemplate} data into {@link #connectedQuests},
	 * and then clear {@link #conQuests}.
	 * 
	 * @param u
	 * @param parent
	 */
	void afterUnmarshal(Unmarshaller u, Object parent) {
		for (ConnectedQuestTemplate conQuest: conQuests) {
			connectedQuests.put(conQuest.getParent(), conQuest.getConnection());
		}
		conQuests.clear();
	}
	
	/**
	 * Returns the number of connected quest data entries loaded in this data.
	 * 
	 * @return The number of data entries in {@link #connectedQuests}.
	 */
	public int size() {
		return connectedQuests.size();
	}
	
	/**
	 * Checks if the given quest chains with another. Returns true if so, false otherwise.
	 * 
	 * @param questId -- The ID of the quest that will chain into another.
	 * @return True if the given questId should chain into another, false otherwise.
	 */
	public boolean hasConnectedQuest(int questId) {
		return connectedQuests.containsKey(questId);
	}
	
	/**
	 * Precede this method with a call to {@link #hasConnectedQuest(int)}.
	 * <p>
	 * Returns the questId of the quest that the given questId will chain to.
	 * <p>
	 * Example:
	 * A Bloody Task (2102) chains directly into The Sprigg Report (2103) upon completion.
	 * 
	 * Given 2102, this method would return 2103.
	 * 
	 * @param questId -- The ID of the quest that will chain into another.
	 * @return the ID of the quest the given quest should chain into, or zero if the given quest chains to nothing.
	 */
	public int getConnectedQuest(int questId) {
		return connectedQuests.get(questId);
	}
	
}
