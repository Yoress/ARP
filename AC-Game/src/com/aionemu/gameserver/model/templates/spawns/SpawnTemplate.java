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
package com.aionemu.gameserver.model.templates.spawns;

import org.apache.commons.lang.StringUtils;

import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.templates.event.EventTemplate;
import com.aionemu.gameserver.spawnengine.SpawnHandlerType;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author xTz
 * @modified Rolandas
 * @modified Yon (Aion Reconstruction Project) -- Added {@link #setFly()} and {@link #setHeading(byte)},
 * {@link #setVisibleObject(VisibleObject)} refactored to {@link #addVisibleObject(VisibleObject, int)}, reworked
 * the class to store multiple VisibleObjects, as these templates are not being treated exclusively as they should be.
 */
public class SpawnTemplate {
	
	private float x;
	private float y;
	private float z;
	private byte h;
	private int staticId;
	private int randomWalk;
	private String walkerId;
	private int walkerIdx;
	private int fly;
	private String anchor;
	private SpawnGroup2 spawnGroup;
	private EventTemplate eventTemplate;
	private SpawnModel model;
	private int state;
	private int creatorId;
	private String masterName = StringUtils.EMPTY;
	private TemporarySpawn temporarySpawn;
//	private VisibleObject visibleObject;
	private TIntObjectHashMap<VisibleObject> visibleObjects = new TIntObjectHashMap<VisibleObject>(1);
	
	public SpawnTemplate(SpawnGroup2 spawnGroup, SpawnSpotTemplate spot) {
		this.spawnGroup = spawnGroup;
		x = spot.getX();
		y = spot.getY();
		z = spot.getZ();
		h = spot.getHeading();
		staticId = spot.getStaticId();
		randomWalk = spot.getRandomWalk();
		walkerId = spot.getWalkerId();
		fly = spot.getFly();
		anchor = spot.getAnchor();
		walkerIdx = spot.getWalkerIndex();
		model = spot.getModel();
		state = spot.getState();
		temporarySpawn = spot.getTemporarySpawn();
	}
	
	public SpawnTemplate(SpawnGroup2 spawnGroup, float x, float y, float z, byte heading, int randWalk, String walkerId, int staticId, int fly) {
		this.spawnGroup = spawnGroup;
		this.x = x;
		this.y = y;
		this.z = z;
		h = heading;
		this.randomWalk = randWalk;
		this.walkerId = walkerId;
		this.staticId = staticId;
		this.fly = fly;
		addTemplate();
	}
	
	public SpawnTemplate(SpawnGroup2 spawnGroup, float x, float y, float z, byte heading, int randWalk, String walkerId, int walkerIdx, int staticId, int fly) {
		this.spawnGroup = spawnGroup;
		this.x = x;
		this.y = y;
		this.z = z;
		this.h = heading;
		this.randomWalk = randWalk;
		this.walkerId = walkerId;
		this.staticId = staticId;
		this.fly = fly;
		this.walkerIdx = walkerIdx;
		addTemplate();
	}
	
	private void addTemplate() {
		spawnGroup.addSpawnTemplate(this);
	}
	
	public float getX() {
		return x;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public float getY() {
		return y;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getZ() {
		return z;
	}
	
	public void setZ(float z) {
		this.z = z;
	}
	
	public byte getHeading() {
		return h;
	}
	
	public void setHeading(byte h) {
		this.h = h;
	}
	
	public int getStaticId() {
		return staticId;
	}
	
	public void setStaticId(int staticId) {
		this.staticId = staticId;
	}
	
	public int getRandomWalk() {
		return randomWalk;
	}
	
	public void setRandomWalk(int randomWalk) {
		this.randomWalk = randomWalk;
	}
	
	public int getFly() {
		return fly;
	}
	
	public void setFly(int fly) {
		this.fly = fly;
	}
	
	public boolean canFly() {
		return fly > 0;
	}
	
	public int getNpcId() {
		return spawnGroup.getNpcId();
	}
	
	public int getWorldId() {
		return spawnGroup.getWorldId();
	}
	
	public SpawnTemplate changeTemplate(int instanceId) {
		return spawnGroup.getRndTemplate(instanceId);
	}
	
	public int getRespawnTime() {
		return spawnGroup.getRespawnTime();
	}
	
	// FIXME: cross-WorldInstace modification!!!
	public void setRespawnTime(int respawnTime) {
		spawnGroup.setRespawnTime(respawnTime);
	}
	
	public void setUse(int instanceId, boolean isUsed) {
		spawnGroup.setTemplateUse(instanceId, this, isUsed);
	}
	
	public TemporarySpawn getTemporarySpawn() {
		return temporarySpawn != null ? temporarySpawn : spawnGroup.geTemporarySpawn();
	}
	
	public SpawnHandlerType getHandlerType() {
		return spawnGroup.getHandlerType();
	}
	
	public String getAnchor() {
		return anchor;
	}
	
	public boolean hasRandomWalk() {
		return randomWalk != 0;
	}
	
	public boolean isNoRespawn() {
		return spawnGroup.getRespawnTime() == 0;
	}
	
	public boolean hasPool() {
		return spawnGroup.hasPool();
	}
	
	public String getWalkerId() {
		return walkerId;
	}
	
	public void setWalkerId(String walkerId) {
		this.walkerId = walkerId;
	}
	
	public int getWalkerIndex() {
		return walkerIdx;
	}
	
	public boolean isTemporarySpawn() {
		return spawnGroup.isTemporarySpawn();
	}
	
	public boolean isEventSpawn() {
		return eventTemplate != null;
	}
	
	public EventTemplate getEventTemplate() {
		return eventTemplate;
	}
	
	public void setEventTemplate(EventTemplate eventTemplate) {
		this.eventTemplate = eventTemplate;
	}
	
	public SpawnModel getModel() {
		return model;
	}
	
	public int getState() {
		return state;
	}
	
	/**
	 * @return the creatorId
	 */
	public int getCreatorId() {
		return creatorId;
	}
	
	/**
	 * @param creatorId the creatorId to set
	 */
	public void setCreatorId(int creatorId) {
		this.creatorId = creatorId;
	}
	
	/**
	 * @return the masterName
	 */
	public String getMasterName() {
		return masterName;
	}
	
	/**
	 * @param masterName the masterName to set
	 */
	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}
	
	public VisibleObject getVisibleObject(int instanceId) {
		return visibleObjects.get(instanceId);
	}
	
	public void addVisibleObject(VisibleObject visibleObject, int instanceId) {
		/*
		 * Spawn templates are meant to be exclusive; ONE VisibleObject per template.
		 * 
		 * The problem is, multiple instances of the same world spawn their entities with
		 * the same template. What this means is we either have to copy the entire list
		 * of spawn groups and templates, or allow for a collection of some sort of
		 * VisibleObjects that have been spawned with this template.
		 */
		if (visibleObjects.contains(instanceId)) {
			//Is there any other cleanup we have to do here?
			VisibleObject old = visibleObjects.remove(instanceId);
			if (old != null) old.getController().onDelete();
		}
		visibleObjects.put(instanceId, visibleObject);
		visibleObjects.compact();
	}
}
