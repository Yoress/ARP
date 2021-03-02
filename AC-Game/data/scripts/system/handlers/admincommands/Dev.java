package admincommands;

import java.util.List;

import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Item;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.state.CreatureState;
import com.aionemu.gameserver.model.house.House;
import com.aionemu.gameserver.model.templates.housing.HouseType;
import com.aionemu.gameserver.model.templates.spawns.SpawnGroup2;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.network.aion.AionConnection;
import com.aionemu.gameserver.network.aion.clientpackets.CM_QUESTION_ACCEPT_SUMMON_RESPONSE.TeleportDestination;
import com.aionemu.gameserver.network.aion.serverpackets.SM_CUSTOM_PACKET;
import com.aionemu.gameserver.network.aion.serverpackets.SM_FORCED_MOVE;
import com.aionemu.gameserver.network.aion.serverpackets.SM_INVENTORY_UPDATE_ITEM;
import com.aionemu.gameserver.network.aion.serverpackets.SM_NPC_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_PLAYER_INFO;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW_ACCEPT_SUMMON;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SKILL_COOLDOWN;
import com.aionemu.gameserver.network.aion.serverpackets.SM_SYSTEM_MESSAGE;
import com.aionemu.gameserver.services.mail.AuctionResult;
import com.aionemu.gameserver.services.mail.MailFormatter;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.skillengine.model.SkillTemplate;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;
import com.aionemu.gameserver.world.geo.GeoService;
import com.aionemu.gameserver.world.zone.ZoneInstance;

import javolution.util.FastMap;

public class Dev extends AdminCommand {
	
	abstract class SM_DEV_PACKET extends SM_CUSTOM_PACKET {
		public SM_DEV_PACKET(int opcode) {
			super(opcode);
		}
		
		@Override
		public abstract void writeImpl(AionConnection con);
	}
	
	public Dev() {
		super("dev");
	}
	
	@Override
	public void execute(Player admin, String... params) {
//		//Try messing with SM_HEADING_UPDATE on another player
//		sendHouseRelatedSystemMail(admin, params);
//		displayZones(admin, params);
//		onFail(admin, "This command is not currently doing anything.");
//		((Creature) admin.getTarget()).setType(CreatureType.INVULNERABLE);
//		((Creature) admin.getTarget()).setType(CreatureType.PEACE);
//		((Creature) admin.getTarget()).setType(CreatureType.NULL);
//		((Player) admin.getTarget()).getCommonData().setLevel(Integer.parseInt(params[0]));
//		PacketSendUtility.sendPacket(admin, new SM_FRIEND_LIST());
//		SkillLearnService.addMissingSkills(admin);
	}
	
//	@SuppressWarnings("unused")
//	private void m(Player admin, String[] params) {}
	
	@SuppressWarnings("unused")
	private void resetCooldowns(Player admin, String... params) {
		FastMap<Integer, Long> cds = admin.getSkillCoolDowns();
		for (Integer skillId: cds.keySet()) {
			cds.remove(skillId);
			SkillTemplate skill = DataManager.SKILL_DATA.getSkillTemplate(skillId);
			PacketSendUtility.sendPacket(admin, new SM_SKILL_COOLDOWN(skill, 0, true));
		}
	}
	
	@SuppressWarnings("unused")
	private void changeRace(Player admin, String... params) {
		if (admin.getCommonData().getRace() == Race.ELYOS) {
			admin.getCommonData().setRace(Race.ASMODIANS);
		} else {
			admin.getCommonData().setRace(Race.ELYOS);
		}
		
		admin.clearKnownlist();
		PacketSendUtility.sendPacket(admin, new SM_PLAYER_INFO(admin, false));
		admin.updateKnownlist();
	}
	
	@SuppressWarnings("unused")
	private void summonTarget(Player admin, String[] params) {
		VisibleObject targ = admin.getTarget();
		if (targ instanceof Player) {
			Player summoned = (Player) targ;
			int worldId = admin.getWorldId();
			int instanceId = admin.getInstanceId();
			float x = admin.getX();
			float y = admin.getY();
			float z = admin.getZ();
			byte h = admin.getHeading();
			//SkillId 20657 is "Summoning Ritual"
			TeleportDestination dest = TeleportDestination.addTeleportRequestFor(summoned, admin, 20657, worldId, instanceId, x, y, z, h, 30);
			if (dest == null) {
				PacketSendUtility.sendPacket(admin, SM_SYSTEM_MESSAGE.STR_MSG_Recall_CANNOT_ACCEPT_EFFECT(summoned.getName()));
				return;
			}
			PacketSendUtility.sendPacket(summoned, new SM_QUESTION_WINDOW_ACCEPT_SUMMON(dest));
			PacketSendUtility.sendMessage(admin, "You have sent a summoning request to " + summoned.getName() + ".");
		} else {
			onFail(admin, "Cannot summon a non player; your current target is: " + targ);
		}
	}
	
	@SuppressWarnings("unused")
	private void changeCreatureState(Player admin, String[] params) {
		if (params.length == 1 && params[0].equalsIgnoreCase("list")) {
			String list = "Available " + CreatureState.class.getSimpleName() + " values:";
			int i = 0;
			for (CreatureState state: CreatureState.values()) {
				list += "\n[" + (i++) + "] " + state;
			}
			onFail(admin, list);
			return;
		}
		if (params.length != 2) {
			onFail(admin, "Failed to read params.");
			return;
		}
		int ordinal = 0;
		CreatureState state;
		try {
			ordinal = Integer.parseInt(params[1]);
			state = CreatureState.values()[ordinal];
		} catch (NumberFormatException e) {
			onFail(admin, "Failed to read params.");
			return;
		} catch (ArrayIndexOutOfBoundsException e) {
			onFail(admin, "CreatureState ordinal " + ordinal + " does not exist.");
			return;
		}
		VisibleObject target = admin.getTarget();
		if (target instanceof Npc) {
			final Npc npc = (Npc) target;
			if (params[0].equalsIgnoreCase("set")) {
				npc.setState(CreatureState.values()[ordinal]);
				PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
			} else if (params[0].equalsIgnoreCase("unset")) {
				npc.unsetState(CreatureState.values()[ordinal]);
				PacketSendUtility.sendPacket(admin, new SM_NPC_INFO(npc, admin));
			} else {
				onFail(admin, "Failed to read params.");
				return;
			}
		} else {
			if (target != null) {
				onFail(admin, "This command only works on Npc objects; your target is an instance of: " + target.getClass().getSimpleName());
			} else {
				onFail(admin, "This command only works on Npc objects; your target is null.");
			}
			return;
		}
		onFail(admin, "Target CreatureState." + state + " " + params[0] + ".");
	}
	
	@SuppressWarnings("unused")
	private void sendHouseRelatedSystemMail(Player admin, String[] params) {
		House house = admin.getHouses().get(0);
		if (house.getHouseType() == HouseType.STUDIO) {
			onFail(admin, "House is studio, command fail.");
			return;
		}
		for (AuctionResult res: AuctionResult.values()) {
			onFail(admin, "" + res);
			MailFormatter.sendHouseAuctionMail(house, admin.getCommonData(), res, System.currentTimeMillis(), 0);
		}
		MailFormatter.sendHouseMaintenanceMail(house, 1, System.currentTimeMillis());
		MailFormatter.sendHouseMaintenanceMail(house, 2, System.currentTimeMillis());
		MailFormatter.sendHouseMaintenanceMail(house, 3, System.currentTimeMillis());
	}
	
	@SuppressWarnings("unused")
	private void changeTargetStaticId(Player admin, String[] params) {
		/*
		 * Attempt to change NPC static ID on the fly -- don't overwrite data.
		 */
		VisibleObject target = admin.getTarget();
		if (target instanceof Npc && params.length == 1) {
			try {
				int staticId = Integer.parseInt(params[0]);
				Npc npc = (Npc) target;
				SpawnTemplate spawn = npc.getSpawn();
				float x = spawn.getX(), y = spawn.getY(), z = spawn.getZ();
				byte heading = spawn.getHeading();
				SpawnTemplate newSpawn = new SpawnTemplate(new SpawnGroup2(npc.getWorldId(), npc.getNpcId()), x, y, z, heading, 0, null, staticId, 0);
				Npc newNpc = (Npc) SpawnEngine.spawnObject(newSpawn, npc.getInstanceId());
				npc.getController().onDelete();
				onFail(admin, "Spawned " + newNpc.getName() + " with static ID: " + staticId + ".");
				return;
			} catch (NumberFormatException e) {
				onFail(admin, "Failed to parse new static ID for target: " + e.getClass().getSimpleName() + ": " + e.getMessage());
				return;
			}
		}
		if (!(target instanceof Npc)) {
			onFail(admin, "This command currently requires an NPC target.");
		} else {
			onFail(admin, "Could not parse static ID.");
		}
	}
	
	@SuppressWarnings("unused")
	private void teleportToXYZHOnLocalMap(Player admin, String[] params) {
		float x = Float.parseFloat(params[0]),
			  y = Float.parseFloat(params[1]),
			  z = Float.parseFloat(params[2]);
		byte h = Byte.parseByte(params[3]);
		onFail(admin, "Teleporting.");
		TeleportService2.teleportTo(admin, admin.getWorldId(), x, y, z, h, TeleportAnimation.NO_ANIMATION);
	}
	
	@SuppressWarnings("unused")
	private void moveToTarget(Player admin, String[] params) {
		VisibleObject target = admin.getTarget();
		if (target != null) {
			PacketSendUtility.broadcastPacket(admin, new SM_FORCED_MOVE(admin, admin.getObjectId(), target.getX(), target.getY(), target.getZ()), true);
		}
	}
	
	@SuppressWarnings("unused")
	private void updateItem(Player admin, String[] params) {
		if (params.length > 0 && params[0].startsWith("[item:") && params[0].endsWith("]")) {
			int itemId = Integer.parseInt(params[0].substring(6, params[0].indexOf(';')));
			List<Item> items = admin.getInventory().getItemsByItemId(itemId);
			if (items.isEmpty()) {
				onFail(admin, "Could not find target item(s).");
				return;
			} else
				for (Item item : items) {
//					if (params.length > 1 && params[1].equalsIgnoreCase("dye")) {
//						item.setItemColor(169240008); //Dye to Vinna petal
//					}
					PacketSendUtility.sendPacket(admin, new SM_INVENTORY_UPDATE_ITEM(admin, item));
				}
			onFail(admin, "Target item updated.");
		} else {
			onFail(admin, "Could not find target item(s).");
		}
	}
	
	@SuppressWarnings("unused")
	private void displayZones(Player admin, String[] params) {
		int count = admin.getPosition().getMapRegion().getZoneCount();
		onFail(admin, "There are " + count + " zones.");
		for (ZoneInstance zone: admin.getPosition().getMapRegion().getZones(admin)) {
			onFail(admin, "You are currently inside Zone: " + zone.getZoneTemplate().getName());
		}
	}
	
	@SuppressWarnings("unused")
	private void geoZ(Player admin, String[] params) {
		VisibleObject target = admin.getTarget();
		if (target != null) {
			float z = target.getZ();
			float z1 = GeoService.getInstance().getZ(target);
			onFail(admin, "Target is at: " + z + ", while GeoService says: " + z1);
		}
	}
	
}
