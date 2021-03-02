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
package admincommands;

import com.aionemu.gameserver.model.PlayerClass;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.model.skill.PlayerSkillEntry;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_LIST;
import com.aionemu.gameserver.services.SkillLearnService;
import com.aionemu.gameserver.services.StigmaService;
import com.aionemu.gameserver.services.player.PlayerService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;


/**
 * A command to force an existing player into their alternate subclass (if it exists).
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class SubclassSwitch extends AdminCommand {
	
	public SubclassSwitch() {
		super("subclass");
	}
	
	static enum SubclassSwitchType {
		Gladiator(700054),
		Templar(700055),
		Ranger(700056),
		Assassin(700057),
		Cleric(700058),
		Chanter(700059),
		Sorcerer(700060),
		Spiritmaster(700061);
		
		public final int stringId;
		
		private SubclassSwitchType(int stringId) {
			this.stringId = stringId;
		}
		
		static SubclassSwitchType getByPlayerClass(PlayerClass playerClass) {
			switch (playerClass) {
				case ASSASSIN: return Ranger;
				case CHANTER: return Cleric;
				case CLERIC: return Chanter;
				case GLADIATOR: return Templar;
				case RANGER: return Assassin;
				case SORCERER: return Spiritmaster;
				case SPIRIT_MASTER: return Sorcerer;
				case TEMPLAR: return Gladiator;
				default: return null;
			}
		}
		
		PlayerClass getPlayerClass() {
			switch (this) {
				case Assassin: return PlayerClass.ASSASSIN;
				case Chanter: return PlayerClass.CHANTER;
				case Cleric: return PlayerClass.CLERIC;
				case Gladiator: return PlayerClass.GLADIATOR;
				case Ranger: return PlayerClass.RANGER;
				case Sorcerer: return PlayerClass.SORCERER;
				case Spiritmaster: return PlayerClass.SPIRIT_MASTER;
				case Templar: return PlayerClass.TEMPLAR;
			}
			throw new IllegalStateException();
		}
	}
	
	@Override
	public void execute(Player admin, String... params) {
		Creature c = (Creature) admin.getTarget();
		Player target = admin;
		if (c instanceof Player) {
			target = (Player) c;
		}
		final SubclassSwitchType newClass = SubclassSwitchType.getByPlayerClass(target.getPlayerClass());
		if (newClass == null) {
			onFail(admin, "That player's class is not supported!");
			return;
		}
		target.getResponseRequester().putRequest(newClass.stringId, new RequestResponseHandler(admin) {
			@Override
			public void denyRequest(Creature requester, Player responder) {
				if (requester instanceof Player) {
					PacketSendUtility.sendMessage((Player) requester, responder.getName() + " rejected the offer to change their class.");
				}
			}
			
			@Override
			public void acceptRequest(Creature requester, Player responder) {
				if (requester instanceof Player) {
					PacketSendUtility.sendMessage((Player) requester, responder.getName() + " accepted the offer to change their class.");
				}
				
				int essTapping = 1;
				int aethTapping = 1;
				if (responder.getSkillList().isSkillPresent(30002)) {
					essTapping = responder.getSkillList().getSkillLevel(30002);
				}
				if (responder.getSkillList().isSkillPresent(30003)) {
					aethTapping = responder.getSkillList().getSkillLevel(30003);
				}
				for (PlayerSkillEntry skill: responder.getSkillList().getAllSkills()) {
					if (skill.getSkillId() < 30001) {
						SkillLearnService.removeSkill(responder, skill.getSkillId());
					}
				}
				for (Item stig: responder.getEquipment().getEquippedItemsAllStigma()) {
					StigmaService.notifyUnequipAction(responder, responder.getEquipment().unEquipItem(stig.getObjectId(), stig.getEquipmentSlot()));
				}
				PlayerService.storePlayer(responder);
//				responder.getCommonData().setPlayerClass(PlayerClass.getStartingClassFor(responder.getPlayerClass()));
//				SkillLearnService.addMissingSkills(responder);
				responder.getCommonData().setPlayerClass(newClass.getPlayerClass());
				responder.getController().upgradePlayer();
				SkillLearnService.addMissingSkills(responder);
				SkillLearnService.removeSkill(responder, 30001);
				responder.getSkillList().getSkillEntry(30002).setSkillLvl(essTapping);
				responder.getSkillList().getSkillEntry(30003).setSkillLvl(aethTapping);
				PacketSendUtility.sendPacket(responder, new SM_SKILL_LIST(responder, responder.getSkillList().getAllSkills()));
				PlayerService.storePlayer(responder);
			}
		});
		PacketSendUtility.sendMessage(admin, "You've offered " + target.getName() + " a chance to change their subclass.");
		PacketSendUtility.sendPacket(target, new SM_QUESTION_WINDOW(newClass.stringId, admin.getObjectId(), 0, ""));
		
	}
	
}
