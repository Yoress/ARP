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
package com.aionemu.gameserver.ai2.mechanics.actions;

import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.context.Alias;
import com.aionemu.gameserver.ai2.mechanics.context.CutsceneTarget;
import com.aionemu.gameserver.ai2.mechanics.context.UserIndicator;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class PlayCutsceneByUserIndicatorAction extends Action {
	
	final public UserIndicator target;
	
	final public int cutsceneId;
	
	final public int questId;
	
	final public CutsceneTarget playTargetType;
	
	final public Alias teleportAlias; //NOTE: This can be null!
	
	public PlayCutsceneByUserIndicatorAction(UserIndicator target, int cutsceneId, int questId, CutsceneTarget playTargetType, Alias teleportAlias) {
		super(ActionType.play_cutscene_by_user_indicator);
		this.target = target;
		this.cutsceneId = cutsceneId;
		this.questId = questId;
		this.playTargetType = playTargetType;
		this.teleportAlias = teleportAlias;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		int Unused_So_I_Know_This_Class_Is_Not_In_An_Acceptable_State = 0;
		//TODO: play this cs to the player(s)
	}
	
	@Override
	public int hashCode() {
		return 3*target.ordinal() + 5*cutsceneId + 7*questId + 11*playTargetType.ordinal() + (teleportAlias == null ? 1 : 13*teleportAlias.ordinal());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlayCutsceneByUserIndicatorAction) {
			PlayCutsceneByUserIndicatorAction o = (PlayCutsceneByUserIndicatorAction) obj;
			return (o.target == target
					&& o.cutsceneId == cutsceneId
					&& o.questId == questId
					&& o.playTargetType == playTargetType
					&& o.teleportAlias == teleportAlias);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + target + "] --> Q" + questId + ", " + cutsceneId + " <" + playTargetType + ", " + teleportAlias + ">";
	}
	
}
