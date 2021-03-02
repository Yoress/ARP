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
package com.aionemu.gameserver.model.templates.stats;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Rolandas
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreatureSpeeds")
public class CreatureSpeeds {
	
	@XmlAttribute(name = "walk")
	private float walkSpeed;
	@XmlAttribute(name = "run")
	private float runSpeed;
	@XmlAttribute(name = "group_walk")
	private float groupWalkSpeed;
	@XmlAttribute(name = "run_fight")
	private float runSpeedFight;
	@XmlAttribute(name = "group_run_fight")
	private float groupRunSpeedFight;
	@XmlAttribute(name = "fly")
	private float flySpeed;
	
//	//FIX-ME: Remove
//	public void bulkSet(Float walkSpeed, Float runSpeed, Float groupWalkSpeed, Float runSpeedFight, Float groupRunSpeedFight, Float flySpeed) {
//		if (walkSpeed != null) this.walkSpeed = walkSpeed;
//		if (runSpeed != null) this.runSpeed = runSpeed;
//		if (groupWalkSpeed != null) this.groupWalkSpeed = groupWalkSpeed;
//		if (runSpeedFight != null) this.runSpeedFight = runSpeedFight;
//		if (groupRunSpeedFight != null) this.groupRunSpeedFight = groupRunSpeedFight;
//		if (flySpeed != null) this.flySpeed = flySpeed;
//	}
	
	public float getWalkSpeed() {
		return walkSpeed/* == 0 ? 0.937F : walkSpeed*/; //TODO: Add default movement speed?
	}
	
	public float getRunSpeed() {
		return runSpeed;
	}
	
	public float getFlySpeed() {
		return flySpeed;
	}
	
	public float getGroupWalkSpeed() {
		return groupWalkSpeed;
	}
	
	public float getRunSpeedFight() {
		return runSpeedFight;
	}
	
	public float getGroupRunSpeedFight() {
		return groupRunSpeedFight;
	}
}
