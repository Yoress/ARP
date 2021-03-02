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
package com.aionemu.gameserver.model.gameobjects.player;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.instance.InstanceCoolTimeType;
import com.aionemu.gameserver.model.templates.InstanceCooltime;

/**
 * Representing an instance cooldown for the entries system, this class is used by {@link PortalCooldownList} to store
 * how many entries are left for a given player's instances. It also tracks the reset time.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class PortalCooldown {
	
	/**
	 * The template of this cooldown.
	 */
	private final InstanceCooltime coolTemplate;
	
	/**
	 * The time at which this cooldown should be reset. Note that if the entry
	 * count is more than the normal maximum (can happen via Re-entry Scrolls),
	 * this {@link PortalCooldown} should persist past resets.
	 */
	long resetTimestamp;
	
	/**
	 * The number of entries left before this instance goes on cooldown; if the {@link #type} of this cooldown
	 * is {@link InstanceCoolTimeType#RELATIVE RELATIVE}, this value will be 0.
	 */
	int entries;
	
	public PortalCooldown(int worldId, long useDelay, boolean deductFirstEntry) {
		coolTemplate = DataManager.INSTANCE_COOLTIME_DATA.getInstanceCooltimeByWorldId(worldId);
		InstanceCoolTimeType type = coolTemplate.getCoolTimeType();
		resetTimestamp = useDelay;
		if (type.isDaily() || type.isWeekly()) {
			entries = coolTemplate.getMaxEntriesCount().shortValue();
			if (entries == 0) resetTimestamp = 0;
			if (entries > 0 && deductFirstEntry) entries--;
		} else {
			assert type == InstanceCoolTimeType.RELATIVE:"Unsupported " + InstanceCoolTimeType.class.getSimpleName();
			//Nothing to do here; entries is 0 by default in Java. If this object exists, then this instance is on cooldown.
		}
	}
	
	public PortalCooldown(int worldId, long useDelay, int entryCount) {
		this(worldId, useDelay, false);
		entries = entryCount;
	}
	
	public long getReuseTime() {
		return resetTimestamp;
	}
	
	public boolean shouldPersist(final Player player, final int worldId) {
		if (!coolTemplate.getCoolTimeType().isRelative() && coolTemplate.getMaxEntriesCount() < entries) {
			if (resetTimestamp < System.currentTimeMillis()) {
				resetTimestamp = DataManager.INSTANCE_COOLTIME_DATA.getInstanceEntranceCooltime(player, worldId);
			}
			return true;
		}
		return resetTimestamp > System.currentTimeMillis();
	}
	
	public int getEntryCount() {
		return entries;
	}
	
	public void deductEntry() {
		entries--;
		if (entries < 0) entries = 0;
	}
	
	public void addEntry() {
		if (entries < Integer.MAX_VALUE) entries++;
	}
	
	public boolean hasRemainingEntries() {
		return entries > 0;
	}
	
	public boolean isRelativeCooldown() {
		return entries == 0 && coolTemplate.getCoolTimeType().isRelative();
	}
	
	public long getCurrentCooldown() {
		if (hasRemainingEntries()) return 0L;
		return resetTimestamp;
	}
	
}
