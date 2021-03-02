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
package com.aionemu.gameserver.world.zone.handler;

import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.controllers.observer.ActionObserver;
import com.aionemu.gameserver.controllers.observer.CollisionMaterialActor;
import com.aionemu.gameserver.controllers.observer.IActor;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.materials.MaterialSkill;
import com.aionemu.gameserver.model.templates.materials.MaterialTemplate;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.zone.ZoneInstance;

import javolution.util.FastMap;

/**
 * @author Rolandas
 * @modified Yon (Aion Reconstruction Project) -- added some in-game GM logging support
 * to {@link #onEnterZone(Creature, ZoneInstance)}, and {@link #onLeaveZone(Creature, ZoneInstance)}.
 */
public class MaterialZoneHandler implements ZoneHandler {
	
	FastMap<Integer, IActor> observed = new FastMap<Integer, IActor>();
	
	private Spatial geometry;
	private MaterialTemplate template;
	private boolean actOnEnter;
	private Race ownerRace = Race.NONE;
	
	public MaterialZoneHandler(Spatial geometry, MaterialTemplate template) {
		this.geometry = geometry;
		this.template = template;
		String name = geometry.getName();
		if (name.indexOf("FIRE_BOX") != -1 || name.indexOf("FIRE_SEMISPHERE") != -1 || name.indexOf("FIREPOT") != -1 || name.indexOf("FIRE_CYLINDER") != -1 || name.indexOf("FIRE_CONE") != -1
				|| name.startsWith("BU_H_CENTERHALL")) {
			actOnEnter = true;
		}
		//actOnEnter based on Material ID -- Let's go with original for now ~ Yon
//		switch (template.getId()) {
//		//92, 93 dispel something, 107 is acid damage, 108 heals an NPC, 115, 116, 117 dispel something 
//		case 92: case 93: case 107: case 108: case 115: case 116: case 117:
//			actOnEnter = false;
//			break;
//		default:
//			actOnEnter = true;
//		}
		if (name.startsWith("BU_AB_DARKSP")) {
			ownerRace = Race.ASMODIANS;
		} else if (name.startsWith("BU_AB_LIGHTSP")) {
			ownerRace = Race.ELYOS;
		}
	}
	
	@Override
	public void onEnterZone(Creature creature, ZoneInstance zone) {
		if (ownerRace == creature.getRace()) {
			return;
		}
		MaterialSkill foundSkill = null;
		for (MaterialSkill skill : template.getSkills()) {
			if (skill.getTarget().isTarget(creature)) {
				foundSkill = skill;
				break;
			}
		}
		if (foundSkill == null) {
			return;
		}
		if (GeoDataConfig.GEO_MATERIALS_SHOWDETAILS && creature instanceof Player) {
			Player player = (Player) creature;
			if (player.isGM()) {
				PacketSendUtility.sendMessage(player, "Entered geo skill zone: " + geometry.getName());
			}
		}
		CollisionMaterialActor actor = new CollisionMaterialActor(creature, geometry, template);
		creature.getObserveController().addObserver(actor);
		observed.put(creature.getObjectId(), actor);
		if (actOnEnter) actor.act();
	}
	
	@Override
	public void onLeaveZone(Creature creature, ZoneInstance zone) {
		IActor actor = observed.get(creature.getObjectId());
		if (actor != null) {
			creature.getObserveController().removeObserver((ActionObserver) actor);
			observed.remove(creature.getObjectId());
			actor.abort();
		}
		if (GeoDataConfig.GEO_MATERIALS_SHOWDETAILS && creature instanceof Player) {
			Player player = (Player) creature;
			if (player.isGM()) {
				PacketSendUtility.sendMessage(player, "Leaving geo skill zone: " + geometry.getName());
			}
		}
	}
}
