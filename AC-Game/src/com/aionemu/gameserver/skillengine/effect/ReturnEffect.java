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

import com.aionemu.gameserver.eventEngine.battleground.services.battleground.BattleGround;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.items.storage.StorageType;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.model.Effect;

/**
 * @author ATracer
 * @modified Kill3r
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReturnEffect")
public class ReturnEffect extends EffectTemplate {
	
	@Override
	public void applyEffect(Effect effect) {
		Player player = (Player) effect.getEffector();
		BattleGround battleground = player.getBattleGround();
		if (battleground != null) {
			battleground.broadcastToBattleGround(player.getName() + " has left the battleground.", player.getCommonData().getRace());
			battleground.removePlayer(player);
			player.setBattleGround(null);
			if (player.battlegroundObserve != 0) {
				if (player.battlegroundBetE > 0) {
					player.getStorage(StorageType.CUBE.getId()).increaseKinah(player.battlegroundBetE);
					player.battlegroundBetE = 0;
				} else if (player.battlegroundBetA > 0) {
					player.getStorage(StorageType.CUBE.getId()).increaseKinah(player.battlegroundBetA);
					player.battlegroundBetA = 0;
				}
				
				player.battlegroundObserve = 0;
			}
		}
		
		TeleportService2.moveToBindLocation((Player) effect.getEffector(), true);
	}
	
	@Override
	public void calculate(Effect effect) {
		// cannot use return inside FFA!
		if (effect.getEffected() instanceof Player && (((Player) effect.getEffected()).isInFFA())) {
			return;
		}
		// cannot use return inside 1vs1
		if (effect.getEffected() instanceof Player && ((Player) effect.getEffected()).isInDuelArena()) {
			return;
		}
		if (effect.getEffected().isSpawned()) {
			effect.addSucessEffect(this);
		}
	}
}
