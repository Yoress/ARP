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
package com.aionemu.gameserver.skillengine.effect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.item.ItemTemplate;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 * @Reworked Kill3r
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReturnPointEffect")
public class ReturnPointEffect extends EffectTemplate {
	
	@Override
	public void applyEffect(Effect effect) {
		ItemTemplate itemTemplate = effect.getItemTemplate();
		int worldId = itemTemplate.getReturnWorldId();
		String pointAlias = itemTemplate.getReturnAlias();
		TeleportService2.useTeleportScroll(((Player) effect.getEffector()), pointAlias, worldId);
	}
	
	@Override
	public void calculate(Effect effect) {
		// cannot use return inside ffa
		if (effect.getEffected() instanceof Player && ((Player) effect.getEffected()).isInFFA()) {
			return;
		}
		// cannot use return inside 1v1
		if (effect.getEffected() instanceof Player && ((Player) effect.getEffected()).isInDuelArena()) {
			return;
		}
		ItemTemplate itemTemplate = effect.getItemTemplate();
		if (itemTemplate != null) {
			effect.addSucessEffect(this);
		}
	}
}
