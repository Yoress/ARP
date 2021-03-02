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
package com.aionemu.gameserver.model.templates.portal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import com.aionemu.gameserver.model.Race;

/**
 * @author xTz
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstanceExit")
public class InstanceExit {
	
	@XmlAttribute(name = "instance_id")
	protected int instanceId;
	@XmlAttribute(name = "exit_world")
	protected int exitWorld;
	@XmlAttribute(name = "race")
	protected Race race = Race.PC_ALL;
	@XmlAttribute(name = "x")
	protected float x;
	@XmlAttribute(name = "y")
	protected float y;
	@XmlAttribute(name = "z")
	protected float z;
	@XmlAttribute(name = "h")
	protected byte h;
	
	public Integer getInstanceId() {
		return instanceId;
	}
	
	public void setInstanceId(int value) {
		this.instanceId = value;
	}
	
	public int getExitWorld() {
		return exitWorld;
	}
	
	public void setExitWorld(int value) {
		this.exitWorld = value;
	}
	
	public Race getRace() {
		return race;
	}
	
	public void setRace(Race value) {
		this.race = value;
	}
	
	public float getX() {
		return x;
	}
	
	public void setX(float value) {
		this.x = value;
	}
	
	public float getY() {
		return y;
	}
	
	public void setY(float value) {
		this.y = value;
	}
	
	public float getZ() {
		return z;
	}
	
	public void setZ(float value) {
		this.z = value;
	}
	
	public byte getH() {
		return h;
	}
	
	public void setH(byte value) {
		this.h = value;
	}
}
