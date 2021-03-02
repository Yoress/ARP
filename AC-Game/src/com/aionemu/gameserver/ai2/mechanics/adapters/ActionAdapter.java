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
package com.aionemu.gameserver.ai2.mechanics.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.aionemu.gameserver.ai2.mechanics.AIMechanics;
import com.aionemu.gameserver.ai2.mechanics.actions.Action;
import com.aionemu.gameserver.ai2.mechanics.actions.AddBattleTimerAction;
import com.aionemu.gameserver.ai2.mechanics.actions.AddHatePointAction;
import com.aionemu.gameserver.ai2.mechanics.actions.AttackMostHatingAction;
import com.aionemu.gameserver.ai2.mechanics.actions.BroadcastMessageAction;
import com.aionemu.gameserver.ai2.mechanics.actions.BroadcastMessageToPartyAction;
import com.aionemu.gameserver.ai2.mechanics.actions.ChangeDirectionAction;
import com.aionemu.gameserver.ai2.mechanics.actions.ChangeWorldSceneStatusAction;
import com.aionemu.gameserver.ai2.mechanics.actions.ChargeLimitedquestAction;
import com.aionemu.gameserver.ai2.mechanics.actions.CloseDialogAction;
import com.aionemu.gameserver.ai2.mechanics.actions.CloseDirectportalAction;
import com.aionemu.gameserver.ai2.mechanics.actions.ControlDoorAction;
import com.aionemu.gameserver.ai2.mechanics.actions.DespawnAction;
import com.aionemu.gameserver.ai2.mechanics.actions.DespawnSelfAction;
import com.aionemu.gameserver.ai2.mechanics.actions.DisplaySystemMessageAction;
import com.aionemu.gameserver.ai2.mechanics.actions.DoNothingAction;
import com.aionemu.gameserver.ai2.mechanics.actions.EnableAreaAction;
import com.aionemu.gameserver.ai2.mechanics.actions.FleeFromAction;
import com.aionemu.gameserver.ai2.mechanics.actions.GiveAbysspointAction;
import com.aionemu.gameserver.ai2.mechanics.actions.GiveExpAction;
import com.aionemu.gameserver.ai2.mechanics.actions.GiveItemByObjIndicatorAction;
import com.aionemu.gameserver.ai2.mechanics.actions.GiveItemByUserIndicatorAction;
import com.aionemu.gameserver.ai2.mechanics.actions.GiveMoneyAction;
import com.aionemu.gameserver.ai2.mechanics.actions.GiveScoreAction;
import com.aionemu.gameserver.ai2.mechanics.actions.GiveWorldScoreAction;
import com.aionemu.gameserver.ai2.mechanics.actions.GotoAliasAction;
import com.aionemu.gameserver.ai2.mechanics.actions.GotoNextWaypointAction;
import com.aionemu.gameserver.ai2.mechanics.actions.GotoWaypointAction;
import com.aionemu.gameserver.ai2.mechanics.actions.OnOffMovingCollisionAction;
import com.aionemu.gameserver.ai2.mechanics.actions.OnOffWindpathAction;
import com.aionemu.gameserver.ai2.mechanics.actions.PlayCutsceneByUserIndicatorAction;
import com.aionemu.gameserver.ai2.mechanics.actions.RandomMoveAction;
import com.aionemu.gameserver.ai2.mechanics.actions.ResetHatepointsAction;
import com.aionemu.gameserver.ai2.mechanics.actions.ResetQueuedActionsAction;
import com.aionemu.gameserver.ai2.mechanics.actions.ReturnToSpawnPointAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SayAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SayToAllAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SayToAllStrAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SendMessageAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SendSystemMsgAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SetConditionSpawnVariableAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SetIdleTimerAction;
import com.aionemu.gameserver.ai2.mechanics.actions.ShoutToAllAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SpawnAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SpawnOnMultiTargetAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SpawnOnTargetAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SpawnOnTargetByAttackerIndicatorAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SwitchTargetAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SwitchTargetByAttackerIndicatorAction;
import com.aionemu.gameserver.ai2.mechanics.actions.SwitchTargetByClassIndicatorAction;
import com.aionemu.gameserver.ai2.mechanics.actions.TeleportTargetAction;
import com.aionemu.gameserver.ai2.mechanics.actions.TeleportTargetAliasAction;
import com.aionemu.gameserver.ai2.mechanics.actions.ToggleAttackableStatusFlagAction;
import com.aionemu.gameserver.ai2.mechanics.actions.UseSkillAction;
import com.aionemu.gameserver.ai2.mechanics.actions.UseSkillByAttackerIndicatorAction;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class ActionAdapter extends XmlAdapter<ActionXML, Action> {
	
	@Override
	public Action unmarshal(ActionXML a) throws Exception {
		Action ret = null;
		switch (a.type) {
			case add_battle_timer:
				ret = new AddBattleTimerAction(a.battleTimerIndicator, a.delay);
				break;
			case add_hate_point:
				ret = new AddHatePointAction(a.objI, a.hatepointsToAdd);
				break;
			case attack_most_hating:
				ret = new AttackMostHatingAction(a.skill);
				break;
			case broadcast_message:
				ret = new BroadcastMessageAction(a.messageType, a.param1int, a.param2int, a.range, a.paramObj);
				break;
			case broadcast_message_to_party:
				ret = new BroadcastMessageToPartyAction(a.messageType, a.param1int, a.param2int, a.paramObj);
				break;
			case despawn:
				ret = new DespawnAction(a.spawnId);
				break;
			case despawn_self:
				ret = new DespawnSelfAction();
				break;
			case display_system_message:
				ret = new DisplaySystemMessageAction(a.stringId, a.zoneName, a.param1S, a.param2S, a.param3S);
				break;
			case do_nothing:
				ret = new DoNothingAction();
				break;
			case flee_from:
				ret = new FleeFromAction(a.objI, a.seconds, a.pushState);
				break;
			case goto_alias:
				ret = new GotoAliasAction(a.alias, a.moveType);
				break;
			case goto_next_waypoint:
				ret = new GotoNextWaypointAction(a.moveType);
				break;
			case goto_waypoint:
				ret = new GotoWaypointAction(a.waypointId, a.moveType);
				break;
			case random_move:
				ret = new RandomMoveAction(a.timeToMove);
				break;
			case reset_hatepoints:
				ret = new ResetHatepointsAction(a.isExceptMostHating, a.volatileHatepointOnly);
				break;
			case say_to_all:
				ret = new SayToAllAction(a.stringId);
				break;
			case say_to_all_str:
				ret = new SayToAllStrAction(a.string);
				break;
			case send_message:
				ret = new SendMessageAction(a.objI, a.messageType, a.param1int, a.param2int, a.paramObj);
				break;
			case send_system_msg:
				ret = new SendSystemMsgAction(a.stringId);
				break;
			case set_condition_spawn_variable:
				ret = new SetConditionSpawnVariableAction(a.string, a.set, a.modify);
				break;
			case set_idle_timer:
				ret = new SetIdleTimerAction(a.delay);
				break;
			case spawn:
				ret = new SpawnAction(a.spawnId, a.npcId, a.numToSpawn, a.spawnLocationType, a.x, a.y, a.z, a.dir, a.spawnRange, a.liveTime, a.despawnAtAttackState, a.isAerialSpawn, a.pathname);
				break;
			case spawn_on_target:
				ret = new SpawnOnTargetAction(a.objI, a.spawnId, a.npcId, a.numToSpawn, a.spawnRange, a.liveTime, a.despawnAtAttackState, a.validDistance, a.attackTargetAfterSpawn, a.hatepointsToAdd);
				break;
			case spawn_on_target_by_attacker_indicator:
				ret = new SpawnOnTargetByAttackerIndicatorAction(a.attackerI, a.spawnId, a.npcId, a.numToSpawn, a.spawnRange, a.liveTime, a.despawnAtAttackState, a.validDistance, a.attackTargetAfterSpawn, a.hatepointsToAdd, a.restrictedRange);
				break;
			case switch_target:
				ret = new SwitchTargetAction(a.objI, a.percentToAdd, a.hatepointsToAdd);
				break;
			case switch_target_by_attacker_indicator:
				ret = new SwitchTargetByAttackerIndicatorAction(a.attackerI, a.percentToAdd, a.hatepointsToAdd, a.restrictedRange);
				break;
			case switch_target_by_class_indicator:
				ret = new SwitchTargetByClassIndicatorAction(a.classI, a.percentToAdd, a.hatepointsToAdd, a.restrictedRange);
				break;
			case teleport_target:
				ret = new TeleportTargetAction(a.objI, a.x, a.y, a.z, a.dir, a.showFX);
				break;
			case teleport_target_alias:
				ret = new TeleportTargetAliasAction(a.objI, a.alias, a.showFX);
				break;
			case use_skill:
				ret = new UseSkillAction(a.objI, a.skill);
				break;
			case use_skill_by_attacker_indicator:
				ret = new UseSkillByAttackerIndicatorAction(a.attackerI, a.skill, a.restrictedRange);
				break;
			case change_direction:
				ret = new ChangeDirectionAction(a.dir);
				break;
			case change_world_scene_status:
				ret = new ChangeWorldSceneStatusAction(a.scenestatus);
				break;
			case charge_limitedquest:
				ret = new ChargeLimitedquestAction(a.questId, a.chargeMaxCount);
				break;
			case close_dialog:
				ret = new CloseDialogAction(a.userI);
				break;
			case close_directportal:
				ret = new CloseDirectportalAction(a.portalId);
				break;
			case control_door:
				ret = new ControlDoorAction(a.doorId, a.doorMethod);
				break;
			case enable_area:
				ret = new EnableAreaAction(a.areaType, a.areaName, a.opCode);
				break;
			case give_abysspoint:
				ret = new GiveAbysspointAction(a.userI, a.numToSpawn);
				break;
			case give_exp:
				ret = new GiveExpAction(a.userI, a.numToSpawn);
				break;
			case give_item_by_obj_indicator:
				ret = new GiveItemByObjIndicatorAction(a.objI, a.itemId, a.min, a.max);
				break;
			case give_item_by_user_indicator:
				ret = new GiveItemByUserIndicatorAction(a.userI, a.itemId, a.min, a.max);
				break;
			case give_money:
				ret = new GiveMoneyAction(a.userI, a.numToSpawn);
				break;
			case give_score:
				ret = new GiveScoreAction(a.userI);
				break;
			case give_world_score:
				ret = new GiveWorldScoreAction(a.userI, a.min, a.max);
				break;
			case on_off_moving_collision:
				ret = new OnOffMovingCollisionAction(a.movingCollisionType, a.subZoneId, a.setActive);
				break;
			case on_off_windpath:
				ret = new OnOffWindpathAction(a.groupId, a.setActive);
				break;
			case play_cutscene_by_user_indicator:
				ret = new PlayCutsceneByUserIndicatorAction(a.userI, a.cutsceneId, a.questId, a.cutsceneTarget, a.teleportAlias);
				break;
			case reset_queued_actions:
				ret = new ResetQueuedActionsAction();
				break;
			case return_to_spawn_point:
				ret = new ReturnToSpawnPointAction();
				break;
			case say:
				ret = new SayAction(a.userI, a.stringId);
				break;
			case shout_to_all:
				ret = new ShoutToAllAction(a.stringId);
				break;
			case spawn_on_multi_target:
				ret = new SpawnOnMultiTargetAction(a.spawnId, a.npcId, a.numToSpawn, a.spawnRange, a.liveTime, a.despawnAtAttackState, a.orderInAttackerList, a.totalSetToSpawn, a.validDistance, a.attackTargetAfterSpawn, a.hatepointsToAdd);
				break;
			case toggle_attackable_status_flag:
				ret = new ToggleAttackableStatusFlagAction(a.attackable);
				break;
			default:
				assert false:"Unsupported Action Type: " + a.type;
		}
		return AIMechanics.addOrGetCanonical(ret);
	}
	
	@Override
	public ActionXML marshal(Action a) throws Exception {
		ActionXML ret = new ActionXML();
		switch (a.type) {
			case add_battle_timer:
				if (a instanceof AddBattleTimerAction) {
					AddBattleTimerAction act = (AddBattleTimerAction) a;
					ret.type = act.type;
					ret.battleTimerIndicator = act.btimerIndicator;
					ret.delay = act.delay;
				}
				break;
			case add_hate_point:
				if (a instanceof AddHatePointAction) {
					AddHatePointAction act = (AddHatePointAction) a;
					ret.type = act.type;
					ret.objI = act.target;
					ret.hatepointsToAdd = act.pointToAdd;
				}
				break;
			case attack_most_hating:
				if (a instanceof AttackMostHatingAction) {
					AttackMostHatingAction act = (AttackMostHatingAction) a;
					ret.type = act.type;
					ret.skill = act.skill;
				}
				break;
			case broadcast_message:
				if (a instanceof BroadcastMessageAction) {
					BroadcastMessageAction act = (BroadcastMessageAction) a;
					ret.type = act.type;
					ret.messageType = act.messageType;
					ret.param1int = act.param1;
					ret.param2int = act.param2;
					ret.range = act.range;
					ret.paramObj = act.paramObj;
				}
				break;
			case broadcast_message_to_party:
				if (a instanceof BroadcastMessageToPartyAction) {
					BroadcastMessageToPartyAction act = (BroadcastMessageToPartyAction) a;
					ret.type = act.type;
					ret.messageType = act.messageType;
					ret.param1int = act.param1;
					ret.param2int = act.param2;
					ret.paramObj = act.paramObj;
				}
				break;
			case despawn:
				if (a instanceof DespawnAction) {
					DespawnAction act = (DespawnAction) a;
					ret.type = act.type;
					ret.spawnId = act.spawnId;
				}
				break;
			case despawn_self:
				if (a instanceof DespawnSelfAction) {
					DespawnSelfAction act = (DespawnSelfAction) a;
					ret.type = act.type;
				}
				break;
			case display_system_message:
				if (a instanceof DisplaySystemMessageAction) {
					DisplaySystemMessageAction act = (DisplaySystemMessageAction) a;
					ret.type = act.type;
					ret.stringId = act.stringId;
					ret.zoneName = act.areaName;
					ret.param1S = act.param1;
					ret.param2S = act.param2;
					ret.param3S = act.param3;
				}
				break;
			case do_nothing:
				if (a instanceof DoNothingAction) {
					DoNothingAction act = (DoNothingAction) a;
					ret.type = act.type;
				}
				break;
			case flee_from:
				if (a instanceof FleeFromAction) {
					FleeFromAction act = (FleeFromAction) a;
					ret.type = act.type;
					ret.objI = act.from;
					ret.seconds = act.seconds;
					ret.pushState = act.pushState;
				}
				break;
			case goto_alias:
				if (a instanceof GotoAliasAction) {
					GotoAliasAction act = (GotoAliasAction) a;
					ret.type = act.type;
					ret.alias = act.alias;
					ret.moveType = act.moveType;
				}
				break;
			case goto_next_waypoint:
				if (a instanceof GotoNextWaypointAction) {
					GotoNextWaypointAction act = (GotoNextWaypointAction) a;
					ret.type = act.type;
					ret.moveType = act.moveType;
				}
				break;
			case goto_waypoint:
				if (a instanceof GotoWaypointAction) {
					GotoWaypointAction act = (GotoWaypointAction) a;
					ret.type = act.type;
					ret.waypointId = act.waypointId;
					ret.moveType = act.moveType;
				}
				break;
			case random_move:
				if (a instanceof RandomMoveAction) {
					RandomMoveAction act = (RandomMoveAction) a;
					ret.type = act.type;
					ret.timeToMove = act.timeToMove;
				}
				break;
			case reset_hatepoints:
				if (a instanceof ResetHatepointsAction) {
					ResetHatepointsAction act = (ResetHatepointsAction) a;
					ret.type = act.type;
					ret.isExceptMostHating = act.isExceptMostHating;
					ret.volatileHatepointOnly = act.volatileHatepointOnly;
				}
				break;
			case say_to_all:
				if (a instanceof SayToAllAction) {
					SayToAllAction act = (SayToAllAction) a;
					ret.type = act.type;
					ret.stringId = act.stringId;
				}
				break;
			case say_to_all_str:
				if (a instanceof SayToAllStrAction) {
					SayToAllStrAction act = (SayToAllStrAction) a;
					ret.type = act.type;
					ret.string = act.string;
				}
				break;
			case send_message:
				if (a instanceof SendMessageAction) {
					SendMessageAction act = (SendMessageAction) a;
					ret.type = act.type;
					ret.objI = act.target;
					ret.messageType = act.messageType;
					ret.param1int = act.param1;
					ret.param2int = act.param2;
					ret.paramObj = act.paramObj;
				}
				break;
			case send_system_msg:
				if (a instanceof SendSystemMsgAction) {
					SendSystemMsgAction act = (SendSystemMsgAction) a;
					ret.type = act.type;
					ret.stringId = act.stringId;
				}
				break;
			case set_condition_spawn_variable:
				if (a instanceof SetConditionSpawnVariableAction) {
					SetConditionSpawnVariableAction act = (SetConditionSpawnVariableAction) a;
					ret.type = act.type;
					ret.string = act.string;
					ret.set = act.set;
					ret.modify = act.modify;
				}
				break;
			case set_idle_timer:
				if (a instanceof SetIdleTimerAction) {
					SetIdleTimerAction act = (SetIdleTimerAction) a;
					ret.type = act.type;
					ret.delay = act.delay;
				}
				break;
			case spawn:
				if (a instanceof SpawnAction) {
					SpawnAction act = (SpawnAction) a;
					ret.type = act.type;
					ret.spawnId = act.spawnId;
					ret.npcId = act.npcId;
					ret.numToSpawn = act.numToSpawn;
					ret.spawnLocationType = act.spawnLocationType;
					ret.x = act.x;
					ret.y = act.y;
					ret.z = act.z;
					ret.dir = act.dir;
					ret.spawnRange = act.spawnRange;
					ret.liveTime = act.liveTime;
					ret.despawnAtAttackState = act.despawnAtAttackState;
					ret.isAerialSpawn = act.isAerialSpawn;
					ret.pathname = act.pathname;
				}
				break;
			case spawn_on_target:
				if (a instanceof SpawnOnTargetAction) {
					SpawnOnTargetAction act = (SpawnOnTargetAction) a;
					ret.type = act.type;
					ret.objI = act.targetObj;
					ret.spawnId = act.spawnId;
					ret.npcId = act.npcId;
					ret.numToSpawn = act.numToSpawn;
					ret.spawnRange = act.spawnRange;
					ret.liveTime = act.liveTime;
					ret.despawnAtAttackState = act.despawnAtAttackState;
					ret.validDistance = act.validDistance;
					ret.attackTargetAfterSpawn = act.attackTargetAfterSpawn;
					ret.hatepointsToAdd = act.hatepointsToAdd;
				}
				break;
			case spawn_on_target_by_attacker_indicator:
				if (a instanceof SpawnOnTargetByAttackerIndicatorAction) {
					SpawnOnTargetByAttackerIndicatorAction act = (SpawnOnTargetByAttackerIndicatorAction) a;
					ret.type = act.type;
					ret.attackerI = act.target;
					ret.spawnId = act.spawnId;
					ret.npcId = act.npcId;
					ret.numToSpawn = act.numToSpawn;
					ret.spawnRange = act.spawnRange;
					ret.liveTime = act.liveTime;
					ret.despawnAtAttackState = act.despawnAtAttackState;
					ret.validDistance = act.validDistance;
					ret.attackTargetAfterSpawn = act.attackTargetAfterSpawn;
					ret.hatepointsToAdd = act.hatepointsToAdd;
					ret.restrictedRange = act.restrictedRange;
				}
				break;
			case switch_target:
				if (a instanceof SwitchTargetAction) {
					SwitchTargetAction act = (SwitchTargetAction) a;
					ret.type = act.type;
					ret.objI = act.target;
					ret.percentToAdd = act.percentToAdd;
					ret.hatepointsToAdd = act.pointsToAdd;
				}
				break;
			case switch_target_by_attacker_indicator:
				if (a instanceof SwitchTargetByAttackerIndicatorAction) {
					SwitchTargetByAttackerIndicatorAction act = (SwitchTargetByAttackerIndicatorAction) a;
					ret.type = act.type;
					ret.attackerI = act.target;
					ret.percentToAdd = act.percentToAdd;
					ret.hatepointsToAdd = act.pointsToAdd;
					ret.restrictedRange = act.restrictedRange;
				}
				break;
			case switch_target_by_class_indicator:
				if (a instanceof SwitchTargetByClassIndicatorAction) {
					SwitchTargetByClassIndicatorAction act = (SwitchTargetByClassIndicatorAction) a;
					ret.type = act.type;
					ret.classI = act.target;
					ret.percentToAdd = act.percentToAdd;
					ret.hatepointsToAdd = act.pointsToAdd;
					ret.restrictedRange = act.restrictedRange;
				}
				break;
			case teleport_target:
				if (a instanceof TeleportTargetAction) {
					TeleportTargetAction act = (TeleportTargetAction) a;
					ret.type = act.type;
					ret.objI = act.target;
					ret.x = act.x;
					ret.y = act.y;
					ret.z = act.z;
					ret.dir = act.dir;
					ret.showFX = act.showFX;
				}
				break;
			case teleport_target_alias:
				if (a instanceof TeleportTargetAliasAction) {
					TeleportTargetAliasAction act = (TeleportTargetAliasAction) a;
					ret.type = act.type;
					ret.objI = act.target;
					ret.alias = act.alias;
					ret.showFX = act.showFX;
				}
				break;
			case use_skill:
				if (a instanceof UseSkillAction) {
					UseSkillAction act = (UseSkillAction) a;
					ret.type = act.type;
					ret.objI = act.target;
					ret.skill = act.skill;
				}
				break;
			case use_skill_by_attacker_indicator:
				if (a instanceof UseSkillByAttackerIndicatorAction) {
					UseSkillByAttackerIndicatorAction act = (UseSkillByAttackerIndicatorAction) a;
					ret.type = act.type;
					ret.attackerI = act.target;
					ret.skill = act.skill;
					ret.restrictedRange = act.restrictedRange;
				}
				break;
			case change_direction:
				if (a instanceof ChangeDirectionAction) {
					ChangeDirectionAction act = (ChangeDirectionAction) a;
					ret.type = act.type;
					ret.dir = act.direction;
				}
				break;
			case change_world_scene_status:
				if (a instanceof ChangeWorldSceneStatusAction) {
					ChangeWorldSceneStatusAction act = (ChangeWorldSceneStatusAction) a;
					ret.type = act.type;
					ret.scenestatus = act.scenestatus;
				}
				break;
			case charge_limitedquest:
				if (a instanceof ChargeLimitedquestAction) {
					ChargeLimitedquestAction act = (ChargeLimitedquestAction) a;
					ret.type = act.type;
					ret.questId = act.questId;
					ret.chargeMaxCount = act.chargeMaxCount;
				}
				break;
			case close_dialog:
				if (a instanceof CloseDialogAction) {
					CloseDialogAction act = (CloseDialogAction) a;
					ret.type = act.type;
					ret.userI = act.target;
				}
				break;
			case close_directportal:
				if (a instanceof CloseDirectportalAction) {
					CloseDirectportalAction act = (CloseDirectportalAction) a;
					ret.type = act.type;
					ret.portalId = act.directPortalId;
				}
				break;
			case control_door:
				if (a instanceof ControlDoorAction) {
					ControlDoorAction act = (ControlDoorAction) a;
					ret.type = act.type;
					ret.doorId = act.id;
					ret.doorMethod = act.method;
				}
				break;
			case enable_area:
				if (a instanceof EnableAreaAction) {
					EnableAreaAction act = (EnableAreaAction) a;
					ret.type = act.type;
					ret.areaType = act.areaType;
					ret.areaName = act.areaName;
					ret.opCode = act.opCode;
				}
				break;
			case give_abysspoint:
				if (a instanceof GiveAbysspointAction) {
					GiveAbysspointAction act = (GiveAbysspointAction) a;
					ret.type = act.type;
					ret.userI = act.target;
					ret.numToSpawn = act.abyssPoint;
				}
				break;
			case give_exp:
				if (a instanceof GiveExpAction) {
					GiveExpAction act = (GiveExpAction) a;
					ret.type = act.type;
					ret.userI = act.target;
					ret.numToSpawn = act.exp;
				}
				break;
			case give_item_by_obj_indicator:
				if (a instanceof GiveItemByObjIndicatorAction) {
					GiveItemByObjIndicatorAction act = (GiveItemByObjIndicatorAction) a;
					ret.type = act.type;
					ret.objI = act.receiver;
					ret.itemId = act.itemId;
					ret.min = act.min;
					ret.max = act.max;
				}
				break;
			case give_item_by_user_indicator:
				if (a instanceof GiveItemByUserIndicatorAction) {
					GiveItemByUserIndicatorAction act = (GiveItemByUserIndicatorAction) a;
					ret.type = act.type;
					ret.userI = act.receiver;
					ret.itemId = act.itemId;
					ret.min = act.min;
					ret.max = act.max;
				}
				break;
			case give_money:
				if (a instanceof GiveMoneyAction) {
					GiveMoneyAction act = (GiveMoneyAction) a;
					ret.type = act.type;
					ret.userI = act.target;
					ret.numToSpawn = act.money;
				}
				break;
			case give_score:
				if (a instanceof GiveScoreAction) {
					GiveScoreAction act = (GiveScoreAction) a;
					ret.type = act.type;
					ret.userI = act.target;
				}
				break;
			case give_world_score:
				if (a instanceof GiveWorldScoreAction) {
					GiveWorldScoreAction act = (GiveWorldScoreAction) a;
					ret.type = act.type;
					ret.userI = act.target;
					ret.min = act.scoreMin;
					ret.max = act.scoreMax;
				}
				break;
			case on_off_moving_collision:
				if (a instanceof OnOffMovingCollisionAction) {
					OnOffMovingCollisionAction act = (OnOffMovingCollisionAction) a;
					ret.type = act.type;
					ret.movingCollisionType = act.movingCollisionType;
					ret.subZoneId = act.sunzoneid;
					ret.setActive = act.onoff;
				}
				break;
			case on_off_windpath:
				if (a instanceof OnOffWindpathAction) {
					OnOffWindpathAction act = (OnOffWindpathAction) a;
					ret.type = act.type;
					ret.groupId = act.groupid;
					ret.setActive = act.onoff;
				}
				break;
			case play_cutscene_by_user_indicator:
				if (a instanceof PlayCutsceneByUserIndicatorAction) {
					PlayCutsceneByUserIndicatorAction act = (PlayCutsceneByUserIndicatorAction) a;
					ret.type = act.type;
					ret.userI = act.target;
					ret.cutsceneId = act.cutsceneId;
					ret.questId = act.questId;
					ret.cutsceneTarget = act.playTargetType;
					ret.teleportAlias = act.teleportAlias;
				}
				break;
			case reset_queued_actions:
				if (a instanceof ResetQueuedActionsAction) {
					ResetQueuedActionsAction act = (ResetQueuedActionsAction) a;
					ret.type = act.type;
				}
				break;
			case return_to_spawn_point:
				if (a instanceof ReturnToSpawnPointAction) {
					ReturnToSpawnPointAction act = (ReturnToSpawnPointAction) a;
					ret.type = act.type;
				}
				break;
			case say:
				if (a instanceof SayAction) {
					SayAction act = (SayAction) a;
					ret.type = act.type;
					ret.userI = act.user;
					ret.stringId = act.stringId;
				}
				break;
			case shout_to_all:
				if (a instanceof ShoutToAllAction) {
					ShoutToAllAction act = (ShoutToAllAction) a;
					ret.type = act.type;
					ret.stringId = act.stringId;
				}
				break;
			case spawn_on_multi_target:
				if (a instanceof SpawnOnMultiTargetAction) {
					SpawnOnMultiTargetAction act = (SpawnOnMultiTargetAction) a;
					ret.type = act.type;
					ret.spawnId = act.spawnId;
					ret.npcId = act.npcNameid;
					ret.numToSpawn = act.numToSpawn;
					ret.spawnRange = act.spawnRange;
					ret.liveTime = act.liveTime;
					ret.despawnAtAttackState = act.despawnAtAttackState;
					ret.orderInAttackerList = act.orderInAttackerList;
					ret.totalSetToSpawn = act.totalSetToSpawn;
					ret.validDistance = act.validDistance;
					ret.attackTargetAfterSpawn = act.attackTargetAfterSpawn;
					ret.hatepointsToAdd = act.hatepointsToAdd;
				}
				break;
			case toggle_attackable_status_flag:
				if (a instanceof ToggleAttackableStatusFlagAction) {
					ToggleAttackableStatusFlagAction act = (ToggleAttackableStatusFlagAction) a;
					ret.type = act.type;
					ret.attackable = act.attakable;
				}
				break;
			default:
				assert false:"Unsupported Action Type: " + a.type;
		}
		return ret;
	}
	
}
