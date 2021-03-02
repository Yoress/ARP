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
package com.aionemu.gameserver.questEngine.handlers.models;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.questEngine.QuestEngine;
import com.aionemu.gameserver.questEngine.handlers.template.ReportToMany;

import javolution.util.FastMap;

/**
 * @author Hilgert
 * @modified Rolandas
 * @modified Yon (Aion Reconstruction Project) -- changed XmlAttribute name of start dialog back to what the xsd file claims it should be.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReportToManyData")
public class ReportToManyData extends XMLQuest {
	
	@XmlAttribute(name = "start_item_id")
	protected int startItemId;
	@XmlAttribute(name = "start_npc_ids")
	protected List<Integer> startNpcIds;
	@XmlAttribute(name = "end_npc_ids")
	protected List<Integer> endNpcIds;
	@XmlAttribute(name = "start_dialog_id")
	protected int startDialog;
	@XmlAttribute(name = "end_dialog_id")
	protected int endDialog;
	@XmlElement(name = "npc_infos", required = true)
	protected List<NpcInfos> npcInfos;
	
	@Override
	public void register(QuestEngine questEngine) {
		int maxVar = 0;
		FastMap<Integer, NpcInfos> NpcInfo = new FastMap<Integer, NpcInfos>();
		for (NpcInfos mi : npcInfos) {
			NpcInfo.put(mi.getNpcId(), mi);
			if (mi.getVar() > maxVar) {
				maxVar = mi.getVar();
			}
		}
		ReportToMany template = new ReportToMany(id, startItemId, startNpcIds, endNpcIds, NpcInfo, startDialog, endDialog, maxVar, mission);
		questEngine.addQuestHandler(template);
	}
}