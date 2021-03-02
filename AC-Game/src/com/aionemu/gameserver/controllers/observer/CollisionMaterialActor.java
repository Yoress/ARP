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
package com.aionemu.gameserver.controllers.observer;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.geoEngine.collision.CollisionIntention;
import com.aionemu.gameserver.geoEngine.collision.CollisionResults;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.model.TaskId;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.materials.MaterialSkill;
import com.aionemu.gameserver.model.templates.materials.MaterialTemplate;
import com.aionemu.gameserver.model.templates.zone.ZoneClassName;
import com.aionemu.gameserver.services.WeatherService;
import com.aionemu.gameserver.skillengine.SkillEngine;
import com.aionemu.gameserver.skillengine.model.Skill;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.ThreadPoolManager;
import com.aionemu.gameserver.utils.gametime.DayTime;
import com.aionemu.gameserver.utils.gametime.GameTime;
import com.aionemu.gameserver.utils.gametime.GameTimeManager;
import com.aionemu.gameserver.world.zone.ZoneInstance;

/**
 * @author Rolandas
 * @modified Yon (Aion Reconstruction Project) -- Implemented conditional fire damage,
 * restructured how {@link #act()} checks if it needs to run.
 */
public class CollisionMaterialActor extends AbstractCollisionObserver implements IActor {
	
	private MaterialTemplate actionTemplate;
	private AtomicBoolean acting = new AtomicBoolean(false);
	
	public CollisionMaterialActor(Creature creature, Spatial geometry, MaterialTemplate actionTemplate) {
		super(creature, geometry, CollisionIntention.MATERIAL.getId());
		this.actionTemplate = actionTemplate;
	}
	
	private MaterialSkill getSkillForTarget(Creature creature) {
		if (creature instanceof Player) {
			Player player = (Player) creature;
			if (player.isProtectionActive()) {
				return null;
			}
		}
		
		MaterialSkill foundSkill = null;
		for (MaterialSkill skill : actionTemplate.getSkills()) {
			if (skill.getTarget().isTarget(creature)) {
				foundSkill = skill;
				break;
			}
		}
		if (foundSkill == null) {
			return null;
		}
		
		/*
		 * Materials 61, 62, and 63 are conditional FIRE damage.
		 */
		if (actionTemplate.getId() == 61 || actionTemplate.getId() == 62 || actionTemplate.getId() == 63) {
			int weatherCode = -1;
			if (creature.getActiveRegion() == null) {
				return null;
			}
			List<ZoneInstance> zones = creature.getActiveRegion().getZones(creature);
			for (ZoneInstance regionZone : zones) {
				if (regionZone.getZoneTemplate().getZoneType() == ZoneClassName.WEATHER) {
					Vector3f center = geometry.getWorldBound().getCenter();
					if (!regionZone.getAreaTemplate().isInside3D(center.x, center.y, center.z)) {
						continue;
					}
					int weatherZoneId = DataManager.ZONE_DATA.getWeatherZoneId(regionZone.getZoneTemplate());
					weatherCode = WeatherService.getInstance().getWeatherCode(creature.getWorldId(), weatherZoneId);
					break;
				}
			}
			
			GameTime gameTime = (GameTime) GameTimeManager.getGameTime().clone();
			switch (actionTemplate.getId()) {
			case 61: //Base conditional FIRE, active if night and clear weather
				if (gameTime.getDayTime() == DayTime.NIGHT && weatherCode == 0) {
					return foundSkill;
				}
				return null;
			case 62: //Weather conditional FIRE, active on clear weather
				if (weatherCode == 0) {
					return foundSkill;
				}
				return null;
			case 63: //Time conditional FIRE, active at night
				if (gameTime.getDayTime() == DayTime.NIGHT) {
					return foundSkill;
				}
				return null;
			}
		}
		
		return foundSkill;
	}
	
	@Override
	public void onMoved(CollisionResults collisionResults) {
		if (collisionResults.size() == 0) {
			if (acting.get()) abort();
			return;
		} else {
			if (GeoDataConfig.GEO_MATERIALS_SHOWDETAILS && creature instanceof Player) {
				Player player = (Player) creature;
				if (player.isGM()) {
					PacketSendUtility.sendMessage(player, collisionResults.getClosestCollision().getGeometry().getName());
				}
			}
			act();
		}
	}
	
	@Override
	public void act() {
		final MaterialSkill actSkill = getSkillForTarget(creature);
		if (!acting.getAndSet(true) || !creature.getController().hasTask(TaskId.MATERIAL_ACTION)) {
			if (actSkill == null) {
				return;
			}
			if (creature.getEffectController().hasAbnormalEffect(actSkill.getId())) {
				return;
			}
			Future<?> task = ThreadPoolManager.getInstance().scheduleAtFixedRate(new Runnable() {
				
				@Override
				public void run() {
					if (!creature.getEffectController().hasAbnormalEffect(actSkill.getId())) {
						if (GeoDataConfig.GEO_MATERIALS_SHOWDETAILS && creature instanceof Player) {
							Player player = (Player) creature;
							if (player.isGM()) {
								PacketSendUtility.sendMessage(player, "Geo Skill Material: " + actionTemplate.getId() + " -- Skill ID:" + actSkill.getId());
							}
						}
						Skill skill = SkillEngine.getInstance().getSkill(creature, actSkill.getId(), actSkill.getSkillLevel(), creature);
						skill.getEffectedList().add(creature);
						skill.useWithoutPropSkill();
					}
				}
			}, 0, (long) (actSkill.getFrequency() * 1000));
			creature.getController().addTask(TaskId.MATERIAL_ACTION, task);
		}
	}
	
	@Override
	public void abort() {
		Future<?> existingTask = creature.getController().getTask(TaskId.MATERIAL_ACTION);
		if (existingTask != null) {
			creature.getController().cancelTask(TaskId.MATERIAL_ACTION);
		}
		acting.set(false);
	}
	
	@Override
	public void died(Creature creature) {
		abort();
	}
	
	@Override
	public void setEnabled(boolean enable) {}
}
