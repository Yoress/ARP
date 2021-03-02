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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.aionemu.gameserver.configs.main.GeoDataConfig;
import com.aionemu.gameserver.dataholders.DataManager;
import com.aionemu.gameserver.model.Race;
import com.aionemu.gameserver.model.TeleportAnimation;
import com.aionemu.gameserver.model.gameobjects.Npc;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.templates.spawns.SpawnTemplate;
import com.aionemu.gameserver.model.templates.spawns.siegespawns.SiegeSpawnTemplate;
import com.aionemu.gameserver.model.templates.stats.CreatureSpeeds;
import com.aionemu.gameserver.model.templates.walker.RouteStep;
import com.aionemu.gameserver.model.templates.walker.WalkerTemplate;
import com.aionemu.gameserver.services.teleport.TeleportService2;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.MathUtil;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.chathandlers.AdminCommand;

/**
 * Contains nested Classes (that extend AdminCommand) that are useful for manipulating NPC spawns.
 * Specifically, an implementation of the following commands:<br>
 * {@link Copy}, {@link Cut}, {@link Paste}, and {@link Walker}.
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class NpcUtility {
	/**
	 * Contains copies of NPC SpawnTemplates with Admin object ID's as the key. These can be used to create duplicate spawns
	 * in other locations.
	 */
	private final static HashMap<Integer, SpawnTemplateHolder> GM_COPY_CUT_CACHE = new HashMap<Integer, SpawnTemplateHolder>();
	
	/**
	 * Contains {@link WalkerHolder Walker Templates} with Admin object ID's as the key. These are used to create new
	 * Walker routes, and apply them to NPC's via commands in-game. 
	 */
	private final static HashMap<Integer, WalkerHolder> GM_WALKER_CACHE = new HashMap<Integer, WalkerHolder>();
	
	/**
	 * Command for copying NPC Spawns to a {@link NpcUtility#GM_COPY_CUT_CACHE cache}.
	 * Each GM can copy/cut one spawn at a time, and use the copy to create duplicates
	 * of the copied NPC where they are standing.
	 * 
	 * @author Yon (Aion Reconstruction Project)
	 */
	public static class Copy extends AdminCommand {
		
		private final static String COPY_HELP = "//copy\n"
											  + "Copies a target NPC and stores the copy in your copy/cut cache.";
		
		public Copy() {
			super("copy");
		}

		@Override
		public void execute(Player admin, String... params) {
			if (params.length > 0) {
				onFail(admin, COPY_HELP);
				return;
			}
			VisibleObject visObj = admin.getTarget();
			if (visObj == null) {
				onFail(admin, "You must first target the NPC you wish to copy.");
				return;
			}
			if (visObj instanceof Player) {
				onFail(admin, "You can't use this command on a Player.");
				return;
			}
			SpawnTemplate spawn = visObj.getSpawn();
			if (GM_COPY_CUT_CACHE.containsKey(admin.getObjectId())) {
				SpawnTemplateHolder oldSpawn = GM_COPY_CUT_CACHE.replace(admin.getObjectId(), new SpawnTemplateHolder(spawn, visObj));
				sendMsg(admin, "Replaced copy of " + oldSpawn.getName() + " with new copy of " + visObj.getName() + ".");
			} else {
				GM_COPY_CUT_CACHE.put(admin.getObjectId(), new SpawnTemplateHolder(spawn, visObj));
				sendMsg(admin, "Copied " + visObj.getName() + ".");
			}
		}
		
	}
	
	/**
	 * Command for copying NPC Spawns to a {@link NpcUtility#GM_COPY_CUT_CACHE cache};
	 * the copied NPC is "Cut" (deleted) when the copy is created. Each GM can copy/cut one spawn at a time,
	 * and use the copy to create duplicates of the copied NPC where they are standing.
	 * 
	 * @author Yon (Aion Reconstruction Project)
	 */
	public static class Cut extends AdminCommand {
		
		private final static String CUT_HELP = "//cut\n"
											 + "Copies a target NPC and then deletes the targeted spawn. "
											 + "The copy will be stored in your copy/cut cache.";
		
		public Cut() {
			super("cut");
		}

		@Override
		public void execute(Player admin, String... params) {
			VisibleObject visObj = admin.getTarget();
			if (params.length > 0) {
				onFail(admin, CUT_HELP);
				return;
			}
			if (visObj == null) {
				onFail(admin, "You must first target the NPC you wish to cut.");
				return;
			}
			if (visObj instanceof Player) {
				onFail(admin, "You can't use this command on a Player.");
				return;
			}
			SpawnTemplate spawn = visObj.getSpawn();
			if (spawn.hasPool()) {
				onFail(admin, "Can't delete pooled spawn template.");
				return;
			}
			if (spawn instanceof SiegeSpawnTemplate) {
				onFail(admin, "Can't delete siege spawn template.");
				return;
			}
			visObj.getController().onDelete();
			try {
				DataManager.SPAWNS_DATA2.saveSpawn(admin, visObj, true);
			} catch (IOException e) {
				e.printStackTrace();
				onFail(admin, "Could not remove spawn, cut operation cancelled.");
				return;
			}
			if (GM_COPY_CUT_CACHE.containsKey(admin.getObjectId())) {
				SpawnTemplateHolder oldSpawn = GM_COPY_CUT_CACHE.replace(admin.getObjectId(), new SpawnTemplateHolder(spawn, visObj));
				sendMsg(admin, "Replaced copy of " + oldSpawn.getName() + " with new cut of " + visObj.getName() + ".");
			} else {
				GM_COPY_CUT_CACHE.put(admin.getObjectId(), new SpawnTemplateHolder(spawn, visObj));
				sendMsg(admin, "Cut " + visObj.getName() + ".");
			}
		}
		
	}
	
	/**
	 * Command for "Pasting" NPC copies from a {@link NpcUtility#GM_COPY_CUT_CACHE cache}. The cache is not cleared upon paste.
	 * <p>
	 * Syntax: {@code //paste <Respawn Time (in Seconds)>}
	 * <br>A respawn time of 0 will result in a temporary paste. If a respawn time is unavailable, a default of 5 minutes is used.
	 * 
	 * @author Yon (Aion Reconstruction Project)
	 */
	public static class Paste extends AdminCommand {
		
		private final static String PASTE_HELP = "//paste <Respawn Time>\n"
											   + "Pastes an NPC at your location from your copy/cut cache; "
											   + "if no respawn time is specified, it will be the same as the copied NPC. "
											   + "If the copied NPC doesn't have a respawn time available (or the respawn time is "
											   + "negative), the new paste will default to a 5 minute respawn time. If you specify a "
											   + "respawn time of zero, the paste will be temporary.";
		
		public Paste() {
			super("paste");
		}

		@Override
		public void execute(Player admin, String... params) {
			if (!GM_COPY_CUT_CACHE.containsKey(admin.getObjectId())) {
				onFail(admin, "Before you can paste an NPC, you must first copy or cut one.");
				return;
			}
			SpawnTemplateHolder spawnCopy = GM_COPY_CUT_CACHE.get(admin.getObjectId());
			int worldId = admin.getWorldId(), visObjId = spawnCopy.getTemplateId(),
				respawnTime = spawnCopy.getTemplate().getRespawnTime();
			float x = admin.getX(), y = admin.getY(), z = admin.getZ();
			byte h = admin.getHeading();
			if (params.length > 0) {
				if ("Help".equalsIgnoreCase(params[0]) || "?".equalsIgnoreCase(params[0])) {
					onFail(admin, PASTE_HELP);
					return;
				}
				try {
					respawnTime = Integer.parseInt(params[0]);
				} catch (NumberFormatException e) {
					onFail(admin, "The respawn time override could not be parsed. Paste attempt cancelled.");
					return;
				}
			}
			if (respawnTime < 0) {
				respawnTime = 300;
			}
			SpawnTemplate newSpawn = SpawnEngine.addNewSpawn(worldId, visObjId, x, y, z, h, respawnTime);
			newSpawn.setRandomWalk(spawnCopy.getTemplate().getRandomWalk());
			newSpawn.setStaticId(spawnCopy.getTemplate().getStaticId());
			newSpawn.setFly(spawnCopy.getTemplate().getFly());
			VisibleObject newVisObj = SpawnEngine.spawnObject(newSpawn, admin.getInstanceId());
			sendMsg(admin, "Pasted " + newVisObj.getName() + ".");
			if (respawnTime > 0) {
				try {
					DataManager.SPAWNS_DATA2.saveSpawn(admin, newVisObj, false);
					sendMsg(admin, "New pasted spawn saved succesfully.");
				} catch (IOException e) {
					e.printStackTrace();
					sendMsg(admin, "Could not save spawn, the new paste is only temporary.");
				}
			} else {
				sendMsg(admin, "The new pasted spawn is only temporary. It will not remain after a server restart.");
			}
		}
		
	}
	
	/**
	 * Holds a spawn template along with the name and templateId of the object it came from.
	 * 
	 * @author Yon (Aion Reconstruction Project)
	 */
	private static class SpawnTemplateHolder {
		
		final SpawnTemplate template;
		final int templateId;
		final String name;
		
		public SpawnTemplateHolder(SpawnTemplate template, VisibleObject visObj) {
			this.template = template;
			this.templateId = visObj.getObjectTemplate().getTemplateId();
			this.name = visObj.getName();
		}
		
		SpawnTemplate getTemplate() {
			return template;
		}
		
		int getTemplateId() {
			return templateId;
		}
		
		String getName() {
			return name;
		}
		
	}
	
	/**
	 * Holds a WalkerTemplate along with additional data for managing said Template. Several helper methods have also been implemented.
	 * 
	 * @author Yon (Aion Reconstruction Project)
	 */
	private static class WalkerHolder {
		
		WalkerTemplate template;
		Npc npc;
		VisibleObject previewNpc;
		int stepCount = 1;
		final int worldId;
		boolean preview;
		
		WalkerHolder(Player admin, WalkerTemplate template) {
			this.template = template;
			worldId = admin.getWorldId();
		}
		
		WalkerHolder(Player admin, WalkerTemplate template, Npc npc) {
			this(admin, template);
			this.npc = npc;
		}
		
		void gc() {
			if (preview) {
				DataManager.WALKER_DATA.removeUnsavedTemplate("WalkerCommandPreview" + template.getRouteId());
				if (previewNpc != null) {
					previewNpc.getController().onDelete();
					previewNpc = null;
				}
				preview = false;
			}
		}
		
		void finalizeWalkerTemplate() {
			template.getRouteStep(stepCount - 1).setNextStep(template.getRouteSteps().get(0));
		}
		
		boolean validateMap(Player admin) {
			return admin.getWorldId() == worldId;
		}
		
		void addPoint(RouteStep point) {
			point.setRouteStep(stepCount);
			template.getRouteSteps().add(point);
			if (stepCount > 1) template.getRouteStep(stepCount - 1).setNextStep(point);
			stepCount++;
		}
		
		void addPoint(float x, float y, float z, int waitTime) {
			addPoint(new RouteStep(x, y, z, waitTime));
		}
		
		boolean hasNpc() {
			return npc != null;
		}
		
		RouteStep revertToPreviousPoint() {
			if (stepCount == 1 || (stepCount == 2 && npc != null)) {
				return null;
			}
			stepCount--;
			template.getRouteSteps().remove(stepCount - 1); //stepCount starts at 1, index starts at 0
			
			//RouteSteps tracked starting at 1, as well. This is the previous step
			RouteStep prevStep = template.getRouteStep(stepCount - 1);
			prevStep.setNextStep(null);
			return prevStep;
		}
		
		RouteStep getLastSavedPoint() {
			if (stepCount > 1) {
				return template.getRouteStep(stepCount - 1);
			} else {
				return null;
			}
		}
		
		byte getHeadingForCurrentLastPoint(Player admin) {
			if (stepCount == 1 || (stepCount == 2 && npc == null)) {
				return admin.getHeading();
			}
			if (stepCount == 2 && npc != null) {
				return npc.getSpawn().getHeading();
			}
			if (stepCount > 2) {
				RouteStep currentPoint = template.getRouteStep(stepCount - 1);
				RouteStep previousPoint = template.getRouteStep(stepCount - 2);
				float x = previousPoint.getX(), y = previousPoint.getY();
				float x2 = currentPoint.getX(), y2 = currentPoint.getY();
				return MathUtil.convertDegreeToHeading(MathUtil.calculateAngleFrom(x, y, x2, y2));
			}
			return admin.getHeading();
		}
		
		void clearPoints() {
			stepCount = 1;
			//Overly cautious GC
			for (RouteStep step: template.getRouteSteps()) {
				if (step != null) step = null;
			}
			template.setRouteSteps(new ArrayList<RouteStep>());
			if (hasNpc()) {
				SpawnTemplate spawn = npc.getSpawn();
				float x = spawn.getX(), y = spawn.getY(), z = spawn.getZ();
				addPoint(x, y, z, 0);
			}
		}
		
		/**
		 * Creates a modified copy of the WalkerTemplate this holder carries. The template is modified to have all points'
		 * Z-values adjusted by +2.5 units. This is intended so that Siel's Relics (the intended test NPC) hover above the ground.
		 * 
		 * Note that this will only work if Geodata pathfinding is off, as Siel's Relics will not fly.
		 * 
		 * @return The modified WalkerTemplate.
		 */
		WalkerTemplate getTestingTemplate() {
			WalkerTemplate ret = new WalkerTemplate("WalkerCommandPreview" + template.getRouteId());
			ArrayList<RouteStep> points = new ArrayList<RouteStep>();
			RouteStep lastStep = null;
			int count = 1;
			boolean gpf = GeoDataConfig.GEO_ENABLE && GeoDataConfig.GEO_NPC_MOVE;
			for (RouteStep step: template.getRouteSteps()) {
				float z = step.getZ();
				if (!gpf) z += 2.5F;
				RouteStep copy = new RouteStep(step.getX(), step.getY(), z, step.getRestTime());
				copy.setRouteStep(count++);
				if (lastStep != null) {
					lastStep.setNextStep(copy);
				}
				lastStep = copy;
				points.add(copy);
			}
			lastStep.setNextStep(points.get(0));
			ret.setRouteSteps(points);
			ret.setIsReversed(template.isReversed());
			return ret;
		}
		
		/**
		 * Creates a copy WalkerTemplate and spawns Siel's Relics (NPC ID: 701502) to walk the route.
		 * Because Siel's Relics do not have movement stats under normal conditions, {@link NpcUtility.CreatureSpeedOverride}
		 * is used to allow movement.
		 * 
		 * @param admin -- The GM executing the Walker Command.
		 */
		void spawnPreviewNpc(Player admin) {
			gc();
			WalkerTemplate previewRoute = getTestingTemplate();
			DataManager.WALKER_DATA.addUnsavedTemplate(previewRoute);
			RouteStep firstPoint = previewRoute.getRouteStep(1);
			RouteStep secondPoint = previewRoute.getRouteStep(2);
			float x = firstPoint.getX(), y = firstPoint.getY(), z = firstPoint.getZ();
			float x2 = secondPoint.getX(), y2 = secondPoint.getY();
			byte heading = MathUtil.convertDegreeToHeading(MathUtil.calculateAngleFrom(x, y, x2, y2));
			SpawnTemplate previewSpawn = SpawnEngine.addNewSpawn(admin.getWorldId(), 701502, x, y, z, heading, 0, previewRoute.getRouteId(), 0);
			new CreatureSpeedOverride(previewSpawn);
			previewNpc = SpawnEngine.spawnObject(previewSpawn, admin.getInstanceId());
			preview = true;
		}
		
		/**
		 * Creates a list of position links for each point on the WalkerTemplate and returns it.
		 * 
		 * @param admin -- The Admin executing the Walker Command.
		 * @return A list of position links for the stored WalkerTemplate
		 */
		String[] getCurrentStatus(Player admin) {
			String[] ret = new String[stepCount - 1];
			int count = 0;
			//TODO: Handle map height in abyss and any other map with a height value.
			int mapHeight = 0;
			for (RouteStep point: template.getRouteSteps()) {
				float x = point.getX(), y = point.getY();
				ret[count] = "[pos:Point" + (count + 1) + ";" + ((admin.getRace() == Race.ELYOS) ? ("0") : ("1"))
						   + " " + admin.getWorldId() + " " + x + " " + y + " 0.0 " + mapHeight + "] ";
				if (point.getRestTime() > 0) ret[count] += "<Wait Time>: " + (point.getRestTime()/1000) + " Seconds";
				count++;
			}
			return ret;
		}
	}
	
	/**
	 * Essentially a hack class intended to give movement speed stats to an NPC that does not have any
	 * within the normal NPC Templates. This was made to give movement capability to Siel's Relics, for
	 * testing purposes.
	 * <p>
	 * It would be equivalent to adding {@code <speeds walk="2" group_walk="2" run="8" run_fight="6" group_run_fight="6"/>}
	 * to the NPC Template.
	 * 
	 * @author Yon (Aion Reconstruction Project)
	 */
	private static class CreatureSpeedOverride extends CreatureSpeeds {
		
		CreatureSpeedOverride(SpawnTemplate template) {
			DataManager.NPC_DATA.getNpcTemplate(template.getNpcId()).getStatsTemplate().setWalkSpeed(this);
		}

		@Override
		public float getWalkSpeed() {
			return 2F;
		}

		@Override
		public float getRunSpeed() {
			return 8F;
		}

		@Override
		public float getGroupWalkSpeed() {
			return 2F;
		}

		@Override
		public float getRunSpeedFight() {
			return 6F;
		}

		@Override
		public float getGroupRunSpeedFight() {
			return 6F;
		}
		
	}
	
	/**
	 * The walker command is for manipulating NPC walker routes.<br>
	 * Available options are as follows:<br>
	 * {@link Walker#start(Player, String[], VisibleObject, WalkerHolder) //walker [Route Name] [Start]}<br>
	 * {@link Walker#point(Player, String[], WalkerHolder) //walker [Point] <Wait Time>}<br>
	 * {@link Walker#previous(Player, WalkerHolder) //walker [Previous]}<br>
	 * {@link Walker#last(Player, WalkerHolder) //walker [Last]}<br>
	 * {@link Walker#clear(Player, WalkerHolder) //walker [Clear]}<br>
	 * {@link Walker#cancel(Player, WalkerHolder) //walker [Cancel]}<br>
	 * {@link Walker#status(Player, WalkerHolder) //walker [Status]}<br>
	 * {@link Walker#end(Player, WalkerHolder) //walker [End]}<br>
	 * {@link Walker#preview(Player, WalkerHolder) //walker [Preview]}<br>
	 * {@link Walker#attach(Player, String[], VisibleObject, WalkerHolder) //walker [Attach] <Route Name>}
	 * 
	 * @author Yon (Aion Reconstruction Project)
	 */
	public static class Walker extends AdminCommand {
		//TODO: Add a //walker [Goto] [Point Index] command.
		private static final String HELP_MESSAGE = "The walker command is for manipulating NPC walker routes.\n"
												 + "For help with a given option, add [Help | ?] at the end of the command.\n"
												 + "Available options are as follows:\n"
												 + "//walker [Route Name] [Start]\n"
												 + "//walker [Point] <Wait Time>\n"
												 + "//walker [Previous]\n"
												 + "//walker [Last]\n"
												 + "//walker [Clear]\n"
												 + "//walker [Cancel]\n"
												 + "//walker [Status]\n"
												 + "//walker [End]\n"
												 + "//walker [Preview]\n"
												 + "//walker [Attach] <Route Name>";
		
		private static final String START_HELP = "//walker [Route Name] [Start]\n"
											   + "Creates a new WalkerTemplate with the given name and stores it in memory.\n"
											   + "If an NPC is targeted, you will be teleported to the spawn point of that NPC, "
											   + "and the WalkerTemplate will be saved to that NPC (if possible); to prevent "
											   + "oddities, the first point for the WalkerTemplate will be set to that location. "
											   + "If no target is selected, the WalkerTemplate will not be applied to anything, "
											   + "but can still be created; however, the first point will not be set.";
		
		private static final String POINT_HELP = "//walker [Point] <Wait Time>\n"
											   + "Creates the next point on the WalkerTemplate stored in memory (if it exists) at "
											   + "the location you're standing. The optional <Wait Time> is how long (in seconds) "
											   + "the NPC will wait at that location before moving on to the next.";
		
		private static final String PREVIOUS_HELP = "//walker [Previous]\n"
												  + "Removes the last point you created for the WalkerTemplate in memory, and teleports "
												  + "you to the location of the point before that.";
		
		private static final String LAST_HELP = "//walker [Last]\n"
											  + "Returns you to the last saved point you created for the WalkerTemplate in memory.";
		
		private static final String CLEAR_HELP = "//walker [Clear]\n"
											   + "Removes all points of the WalkerTemplate stored in memory. "
											   + "The only exception is if you created the WalkerTemplate based on an "
											   + "existing NPC, in which case the first point will remain as the spawn point "
											   + "of that NPC and you will be teleported back to the spawn location to try creating "
											   + "the new WalkerTemplate again.";
		
		private static final String CANCEL_HELP = "//walker [Cancel]\n"
												+ "Cancels the creation of a new WalkerTemplate, deleting the new one stored in memory.";
		
		private static final String STATUS_HELP = "//walker [Status]\n"
												+ "Displays the current status of the WalkerTemplate stored in memory.";
		
		private static final String END_HELP = "//walker [End]\n"
											 + "Finalizes the WalkerTemplate stored in memory, and attempts to save it to a file.";
		
		private static final String PREVIEW_HELP = "//walker [Preview]\n"
												 + "Spawns an NPC and applies the walker route stored in memory to it. This allows you to "
												 + "visually see the path your WalkerTemplate has created.";
		
		private static final String ATTACH_HELP = "//walker [Attach] <Route Name>\n"
												+ "Requires an NPC target. This will attempt to find the WalkerTemplate defined by the "
												+ "given <Route Name>, or use the new one stored in memory if not specified, and attach "
												+ "it to the targeted NPC. To prevent oddities, the first point on the WalkerTemplate "
												+ "will become the new spawn point of the NPC. If used with the WalkerTemplate in memory, "
												+ "the WalkerTemplate will be finalized and saved.";
		
		public Walker() {
			super("walker");
		}
		
		@Override
		public void execute(Player admin, String... params) {
			VisibleObject visObj = admin.getTarget();
			if (params.length == 0
			|| (params.length == 1 && "?".equalsIgnoreCase(params[0]))
			|| (params.length == 1 && "Help".equalsIgnoreCase(params[0]))) {
				onFail(admin, HELP_MESSAGE);
				return;
			}
			if (params.length > 1 && ("Help".equalsIgnoreCase(params[params.length - 1]) || "?".equalsIgnoreCase(params[params.length - 1]))) {
				if ("Start".equalsIgnoreCase(params[params.length - 2])) {
					if (params.length > 2) {
						String npcTarget = " (for ";
						if (visObj instanceof Npc) {
							npcTarget += visObj.getName() + ") ";
						} else {
							npcTarget = " ";
						}
						String routeName = concatParams(params, 0, params.length - 2);
						String s = "\nIn this case, you would have created a WalkerTemplate" + npcTarget + "with the Route Name: " + routeName + ".";
						onFail(admin, START_HELP + s);
					} else {
						onFail(admin, START_HELP);
					}
				} else if ("Point".equalsIgnoreCase(params[params.length - 2])) {
					onFail(admin, POINT_HELP);
				} else if ("Previous".equalsIgnoreCase(params[params.length - 2])) {
					onFail(admin, PREVIOUS_HELP);
				} else if ("Last".equalsIgnoreCase(params[params.length - 2])) {
					onFail(admin, LAST_HELP);
				} else if ("Clear".equalsIgnoreCase(params[params.length - 2])) {
					onFail(admin, CLEAR_HELP);
				} else if ("Cancel".equalsIgnoreCase(params[params.length - 2])) {
					onFail(admin, CANCEL_HELP);
				} else if ("Status".equalsIgnoreCase(params[params.length - 2])) {
					onFail(admin, STATUS_HELP);
				} else if ("End".equalsIgnoreCase(params[params.length - 2])) {
					onFail(admin, END_HELP);
				} else if ("Preview".equalsIgnoreCase(params[params.length - 2])) {
					onFail(admin, PREVIEW_HELP);
				} else if ("Attach".equalsIgnoreCase(params[params.length - 2])) {
					onFail(admin, ATTACH_HELP);
				} else {
					onFail(admin, HELP_MESSAGE);
				}
				return;
			}
			WalkerHolder holder = GM_WALKER_CACHE.get(admin.getObjectId());
			if (holder != null) {
				if (!holder.validateMap(admin)) {
					onFail(admin, "To prevent oddities, you cannot manipulate your WalkerTemplate cache outside of the map it was created in.");
					return;
				}
			}
			if ("Start".equalsIgnoreCase(params[params.length - 1])) {
				start(admin, params, visObj, holder);
			} else if ("Point".equalsIgnoreCase(params[0])) {
				point(admin, params, holder);
			} else if ("Previous".equalsIgnoreCase(params[0])) {
				previous(admin, holder);
			} else if ("Last".equalsIgnoreCase(params[0])) {
				last(admin, holder);
			} else if ("Clear".equalsIgnoreCase(params[0])) {
				clear(admin, holder);
			} else if ("Cancel".equalsIgnoreCase(params[0])) {
				cancel(admin, holder);
			} else if ("Status".equalsIgnoreCase(params[0])) {
				status(admin, holder);
			} else if ("End".equalsIgnoreCase(params[0])) {
				end(admin, holder);
			} else if ("Preview".equalsIgnoreCase(params[0])) {
				preview(admin, holder);
			} else if ("Attach".equalsIgnoreCase(params[0])) {
				attach(admin, params, visObj, holder);
			} else {
				onFail(admin, "Failed to parse command input. Double check and try again.");
			}
		}
		
		/**
		 * {@code //walker [Route Name] [Start]}
		 * <p>
		 * Creates a new WalkerTemplate with the given name and stores it in memory.
	   	 * If an NPC is targeted, you will be teleported to the spawn point of that NPC,
	   	 * and the WalkerTemplate will be saved to that NPC (if possible); to prevent
	   	 * oddities, the first point for the WalkerTemplate will be set to that location.
	   	 * If no target is selected, the WalkerTemplate will not be applied to anything,
	   	 * but can still be created; however, the first point will not be set.
		 * 
		 * @param admin -- The Admin executing the command.
		 * @param params -- The parameters passed into the command.
		 * @param visObj -- The Admin's target.
		 * @param holder -- The WalkerHolder stored in the Admin's Cache.
		 */
		private void start(Player admin, String[] params, VisibleObject visObj, WalkerHolder holder) {
			if (holder != null) {
				onFail(admin, "You already have a WalkerTemplate in memory. Either complete it, or cancel it.");
				return;
			}
			if (params.length == 1) {
				onFail(admin, "You need to specify a [Route Name].\n//walker [Route Name] [Start]");
				return;
			}
			Npc target = null;
			RouteStep point = null;
			String forTarget = " ";
			if (visObj instanceof Npc) {
				target = (Npc) visObj;
				forTarget = " (for " + target.getName() + ") ";
				SpawnTemplate spawn = target.getSpawn();
				if (spawn.getWalkerId() != null) {
					String msg = "Note: The NPC you have selected has a walker route already. "
							   + "The existing route will not be deleted, but will be replaced.";
					sendMsg(admin, msg);
				}
				float x = spawn.getX(), y = spawn.getY(), z = spawn.getZ();
				TeleportService2.teleportTo(admin, target.getWorldId(), target.getInstanceId(), x, y, z, spawn.getHeading(), TeleportAnimation.JUMP_AIMATION);
				point = new RouteStep(x, y, z, 0);
			}
			String routeName = concatParams(params, 0, params.length - 1);
			if (DataManager.WALKER_DATA.getWalkerTemplate(routeName) != null) {
				onFail(admin, "The [Route Name] \"" + routeName + "\" is already in use. Please choose another [Route Name].");
				return;
			}
			WalkerTemplate newTemplate = new WalkerTemplate(routeName);
			String withRouteName = "with a [Route Name] of: " + newTemplate.getRouteId() + ".";
			ArrayList<RouteStep> steps = new ArrayList<RouteStep>();
			newTemplate.setRouteSteps(steps);
			WalkerHolder newHolder = ((target == null) ? (new WalkerHolder(admin, newTemplate)) : (new WalkerHolder(admin, newTemplate, target)));
			if (point != null) newHolder.addPoint(point);
			GM_WALKER_CACHE.put(admin.getObjectId(), newHolder);
			sendMsg(admin, "You have started a new WalkerTemplate" + forTarget + withRouteName);
		}
		
		/**
		 * {@code //walker [Point] <Wait Time>}
		 * <p>
		 * Creates the next point on the WalkerTemplate stored in memory (if it exists) at
		 * the location you're standing. The optional {@code <Wait Time>} is how long (in seconds)
		 * the NPC will wait at that location before moving on to the next.
		 * 
		 * @param admin -- The Admin executing the command.
		 * @param params -- The parameters passed into the command.
		 * @param holder -- The WalkerHolder stored in the Admin's Cache.
		 */
		private void point(Player admin, String[] params, WalkerHolder holder) {
			int waitTime = 0;
			if (params.length > 1) {
				try {
					waitTime = Integer.parseInt(params[1]);
					if (waitTime < 0) waitTime = 0;
					if (waitTime > 0) {
						waitTime = waitTime*1000;
					}
				} catch (NumberFormatException e) {
					onFail(admin, "Unable to parse the included <Wait Time>, please try again.");
					return;
				}
			}
			float x = admin.getX(), y = admin.getY(), z = admin.getZ();
			if (holder == null) {
				onFail(admin, "Before you can add a point, you must first create a WalkerTemplate in memory.");
				return;
			}
			holder.addPoint(x, y, z, waitTime);
			//TODO: handle map height in abyss and any other map with a height value.
			int mapHeight = 0;
			String pos = " [pos:Point" + (holder.stepCount - 1) + ";" + ((admin.getRace() == Race.ELYOS) ? ("0") : ("1"))
					   + " " + admin.getWorldId() + " " + x + " " + y + " 0.0 " + mapHeight + "] ";
			String waitTimeString = ((waitTime > 0) ? (" with a wait time of " + (waitTime/1000) + " seconds.") : ("."));
			sendMsg(admin, "Added" + pos + "to WalkerTemplate" + waitTimeString);
		}
		
		/**
		 * {@code //walker [Previous]}
		 * <p>
		 * Removes the last point you created for the WalkerTemplate in memory, and teleports
		 * you to the location of the point before that.
		 * 
		 * @param admin -- The Admin executing the command.
		 * @param holder -- The WalkerHolder stored in the Admin's Cache.
		 */
		private void previous(Player admin, WalkerHolder holder) {
			if (holder == null) {
				onFail(admin, "Before you can return to a previous point, you must create one.");
				return;
			}
			if (holder.hasNpc()) {
				RouteStep previousPoint = holder.revertToPreviousPoint();
				if (previousPoint == null) {
					String msg = "You cannot go back any further, as it would delete the first point of the "
							   + "WalkerTemplate which is the spawn location of the NPC you selected.";
					onFail(admin, msg);
					return;
				} else {
					float x = previousPoint.getX(), y = previousPoint.getY(), z = previousPoint.getZ();
					byte heading = holder.getHeadingForCurrentLastPoint(admin);
					TeleportService2.teleportTo(admin, holder.npc.getWorldId(), holder.npc.getInstanceId(), x, y, z, heading, TeleportAnimation.JUMP_AIMATION);
					sendMsg(admin, "The last saved point has been removed. You have been teleported to the point before it for reference.");
				}
			} else {
				RouteStep previousPoint = holder.revertToPreviousPoint();
				if (previousPoint == null) {
					onFail(admin, "The WalkerTemplate in memory doesn't have any points left to go back to.");
					return;
				} else {
					float x = previousPoint.getX(), y = previousPoint.getY(), z = previousPoint.getZ();
					byte heading = holder.getHeadingForCurrentLastPoint(admin);
					TeleportService2.teleportTo(admin, admin.getWorldId(), admin.getInstanceId(), x, y, z, heading, TeleportAnimation.JUMP_AIMATION);
					sendMsg(admin, "The last saved point has been removed. You have been teleported to the point before it for reference.");
				}
			}
		}
		
		/**
		 * {@code //walker [Last]}
		 * <p>
		 * Returns you to the last saved point you created for the WalkerTemplate in memory.
		 * 
		 * @param admin -- The Admin executing the command.
		 * @param holder -- The WalkerHolder stored in the Admin's Cache.
		 */
		private void last(Player admin, WalkerHolder holder) {
			if (holder == null) {
				onFail(admin, "You do not have a WalkerTemplate stored in memory.");
				return;
			}
			RouteStep lastPoint = holder.getLastSavedPoint();
			if (lastPoint == null) {
				onFail(admin, "The WalkerTemplate in memory doesn't have any points saved to it.");
				return;
			}
			float x = lastPoint.getX(), y = lastPoint.getY(), z = lastPoint.getZ();
			byte heading = holder.getHeadingForCurrentLastPoint(admin);
			if (holder.hasNpc()) {
				TeleportService2.teleportTo(admin, holder.npc.getWorldId(), holder.npc.getInstanceId(), x, y, z, heading, TeleportAnimation.JUMP_AIMATION);
			} else {
				TeleportService2.teleportTo(admin, admin.getWorldId(), admin.getInstanceId(), x, y, z, heading, TeleportAnimation.JUMP_AIMATION);
			}
			sendMsg(admin, "You have been teleported back to the last point that you saved.");
		}
		
		/**
		 * {@code //walker [Clear]}
		 * <p>
		 * Removes all points of the WalkerTemplate stored in memory. The only exception is if you created the WalkerTemplate
		 * based on an existing NPC, in which case the first point will remain as the spawn point of that NPC and you will be
		 * teleported back to the spawn location to try creating the new WalkerTemplate again.
		 * 
		 * @param admin -- The Admin executing the command.
		 * @param holder -- The WalkerHolder stored in the Admin's Cache.
		 */
		private void clear(Player admin, WalkerHolder holder) {
			if (holder.hasNpc()) {
				holder.clearPoints();
				SpawnTemplate spawn = holder.npc.getSpawn();
				float x = spawn.getX(), y = spawn.getY(), z = spawn.getZ();
				byte heading = spawn.getHeading();
				TeleportService2.teleportTo(admin, holder.npc.getWorldId(), holder.npc.getInstanceId(), x, y, z, heading, TeleportAnimation.JUMP_AIMATION);
				String msg = "You have been teleported back to the spawn point of the NPC you selected. "
						   + "All other points on the WalkerTemplate in memory have been cleared.";
				sendMsg(admin, msg);
			} else {
				holder.clearPoints();
				sendMsg(admin, "You have cleared all points from the WalkerTemplate in memory.");
			}
		}
		
		/**
		 * {@code //walker [Cancel]}
		 * <p>
		 * Cancels the creation of a new WalkerTemplate, deleting the new one stored in memory.
		 * 
		 * @param admin -- The Admin executing the command.
		 * @param holder -- The WalkerHolder stored in the Admin's Cache.
		 */
		private void cancel(Player admin, WalkerHolder holder) {
			if (holder == null) {
				onFail(admin, "You don't have a WalkerTemplate to cancel.");
			} else {
				holder.gc();
				holder = null;
				GM_WALKER_CACHE.remove(admin.getObjectId());
				sendMsg(admin, "You have cancelled the creation of a new WalkerTemplate. Your Template in memory has been deleted.");
			}
		}
		
		/**
		 * {@code //walker [Status]}
		 * <p>
		 * Displays the current status of the WalkerTemplate stored in memory.
		 * 
		 * @param admin -- The Admin executing the command.
		 * @param params -- The parameters passed into the command.
		 * @param visObj -- The Admin's target.
		 * @param holder -- The WalkerHolder stored in the Admin's Cache.
		 */
		private void status(Player admin, WalkerHolder holder) {
			if (holder == null) {
				onFail(admin, "You don't currently have a WalkerTemplate stored in memory.");
				return;
			}
			String msg = "The current status of the WalkerTemplate is as follows:\n"
					   + "  [Route Name]: " + holder.template.getRouteId() + "\n"
					   + "  Reverse: " + holder.template.isReversed() + "\n"
					   + "  NPC: " + ((holder.hasNpc()) ? (holder.npc.getName()) : ("None")) + "\n"
					   + "  Point List:\n";
			for (String s: holder.getCurrentStatus(admin)) {
				msg += "  " + s + "\n";
			}
			msg += "[End Template Status]";
			sendMsg(admin, msg);
		}
		
		/**
		 * {@code //walker [End]}
		 * <p>
		 * Finalizes the WalkerTemplate stored in memory, and attempts to save it to a file.
		 * 
		 * @param admin -- The Admin executing the command.
		 * @param holder -- The WalkerHolder stored in the Admin's Cache.
		 */
		private void end(Player admin, WalkerHolder holder) {
			if (holder == null) {
				onFail(admin, "You don't have a WalkerTemplate stored in memory.");
				return;
			}
			finalizeWalker(admin, holder, false);
		}
		
		/**
		 * {@code //walker [Preview]}
		 * <p>
		 * {@link NpcUtility.WalkerHolder#spawnPreviewNpc(Player) Spawns} an NPC and applies the walker route stored in memory to it.
		 * This allows you to visually see the path your WalkerTemplate has created.
		 * 
		 * @param admin -- The Admin executing the command.
		 * @param holder -- The WalkerHolder stored in the Admin's Cache.
		 */
		private void preview(Player admin, WalkerHolder holder) {
			if (holder == null) {
				onFail(admin, "You don't have a WalkerTemplate stored in memory to preview.");
				return;
			}
			holder.spawnPreviewNpc(admin);
			sendMsg(admin, "Preview NPC spawned.");
		}
		
		/**
		 * {@code //walker [Attach] <Route Name>}
		 * <p>
		 * Requires an NPC target. This will attempt to find the WalkerTemplate defined by the
		 * given {@code <Route Name>}, or use the new one stored in memory if not specified, and attach
		 * it to the targeted NPC. To prevent oddities, the first point on the WalkerTemplate
		 * will become the new spawn point of the NPC. If used with the WalkerTemplate in memory,
		 * the WalkerTemplate will be finalized and saved.
		 * 
		 * @param admin -- The Admin executing the command.
		 * @param params -- The parameters passed into the command.
		 * @param visObj -- The Admin's target.
		 * @param holder -- The WalkerHolder stored in the Admin's Cache.
		 */
		private void attach(Player admin, String[] params, VisibleObject visObj, WalkerHolder holder) {
			if (!(visObj instanceof Npc)) {
				onFail(admin, "This command requires an NPC target.");
				return;
			}
			Npc target = (Npc) visObj;
			SpawnTemplate spawn = target.getSpawn();
			if (params.length > 1) {
				WalkerTemplate template = DataManager.WALKER_DATA.getWalkerTemplate(concatParams(params, 1, params.length));
				if (template == null) {
					onFail(admin, "Could not find the specified <Route Name>. Double check your input and try again.");
					return;
				} else {
					RouteStep firstPoint = template.getRouteStep(1);
					RouteStep secondPoint = template.getRouteStep(2);
					float x = firstPoint.getX(), y = firstPoint.getY(), z = firstPoint.getZ();
					float x2 = secondPoint.getX(), y2 = secondPoint.getY();
					byte heading = MathUtil.convertDegreeToHeading(MathUtil.calculateAngleFrom(x, y, x2, y2));
					target.getController().onDelete();
					try {
						DataManager.SPAWNS_DATA2.saveSpawn(admin, target, true);
					} catch (IOException e) {
						e.printStackTrace();
						String msg = "Could not delete old spawn, the NPC may have a duplicate wandering around. "
								   + "It'll need to be manually deleted.";
						sendMsg(admin, msg);
					}
					target.setXYZH(x, y, z, heading);
					spawn.setX(x); spawn.setY(y); spawn.setZ(z); spawn.setHeading(heading);
					spawn.setWalkerId(template.getRouteId());
					try {
						DataManager.SPAWNS_DATA2.saveSpawn(admin, target, false);
						sendMsg(admin, "Applied WalkerTemplate to NPC.");
					} catch (IOException e) {
						e.printStackTrace();
						sendMsg(admin, "Could not save spawn, the NPC's Walker may have to be assigned manually.");
					}
					SpawnEngine.spawnObject(spawn, target.getInstanceId());
				}
			} else {
				if (holder == null) {
					onFail(admin, "You don't have a WalkerTemplate in memory to attach to this NPC.");
					return;
				}
				if (holder.hasNpc()) {
					String msg = "The WalkerTemplate in memory already has an NPC assigned to it. "
							   + "You cannot attach it, use //walker [End] to finalize it.";
					onFail(admin, msg);
					return;
				}
				holder.npc = target;
				finalizeWalker(admin, holder, true);
			}
		}
		
	}
	
	/**
	 * Concatenates parameters. A space is included between each parameter.
	 * The {@code beginIndex} is the first index of the array that will be included in the concatenation.
	 * The {@code endIndex} will not be included in the concatenation, but all values prior to it (yet after the {@code beginIndex})
	 * will be.
	 * <p>
	 * This method will not verify that the given parameters are large enough to contain the given range, and will throw exceptions if misued.
	 * 
	 * @param params -- The params to be concatenated.
	 * @param beginIndex -- The index within the params to start the process on.
	 * @param endIndex -- The index to stop at (not included in the return value)
	 * @return A String that is a concatenation of all the params within the given range. 
	 */
	private static String concatParams(String[] params, int beginIndex, int endIndex) {
		String ret = "";
		for (int i = beginIndex; i < endIndex; i++) {
			if (i == endIndex - 1) {
				ret += params[i];
			} else {
				ret += params[i] + " ";
			}
		}
		return ret;
	}
	
	/**
	 * Finalizes the WalkerTemplate stored in a WalkerHolder. The Template is output to a file, and then the Holder is disposed.
	 * The {@link #GM_WALKER_CACHE} also has its entry for the given Admin removed.
	 * <p>
	 * If the template is deemed useless (less than two points stored on it), the operation will be cancelled, and the Admin notified.
	 * 
	 * @param admin -- The GM finalizing the WalkerTemplate.
	 * @param holder -- The WalkerHolder containing the WalkerTemplate to be finalized.
	 * @param forceSpawnPoint -- If this method should force the Holder's NPC spawn point to be the first point on the route.
	 */
	private static void finalizeWalker(Player admin, WalkerHolder holder, boolean forceSpawnPoint) {
		if (holder.stepCount == 2) {
			String msg = "You've tried to finalize a WalkerTemplate with only one point on it. "
					   + "As this would be a useless Template, the operation has been cancelled. "
					   + "Complete the new WalkerTemplate, and try again.";
			sendMsg(admin, msg);
			return;
		}
		if (holder.stepCount == 1) {
			String msg = "You've tried to finalize a WalkerTemplate without any points on it. "
					   + "As this would be a useless Template, the operation has been cancelled. "
					   + "Complete the new WalkerTemplate, and try again.";
			sendMsg(admin, msg);
			return;
		}
		holder.finalizeWalkerTemplate();
		DataManager.WALKER_DATA.AddTemplate(holder.template);
		if (holder.hasNpc()) {
			SpawnTemplate spawn = holder.npc.getSpawn();
			holder.npc.getController().onDelete();
			try {
				DataManager.SPAWNS_DATA2.saveSpawn(admin, holder.npc, true);
			} catch (IOException e) {
				e.printStackTrace();
				sendMsg(admin, "Could not delete old spawn, the NPC may have a duplicate wandering around. It'll need to be manually deleted.");
			}
			spawn.setWalkerId(holder.template.getRouteId());
			if (forceSpawnPoint) {
				RouteStep firstPoint = holder.template.getRouteStep(1);
				RouteStep secondPoint = holder.template.getRouteStep(2);
				float x = firstPoint.getX(), y = firstPoint.getY(), z = firstPoint.getZ();
				float x2 = secondPoint.getX(), y2 = secondPoint.getY();
				byte heading = MathUtil.convertDegreeToHeading(MathUtil.calculateAngleFrom(x, y, x2, y2));
				spawn.setX(x); spawn.setY(y); spawn.setZ(z); spawn.setHeading(heading);
				holder.npc.setXYZH(x, y, z, heading);
			}
			try {
				DataManager.SPAWNS_DATA2.saveSpawn(admin, holder.npc, false);
				sendMsg(admin, "Applied WalkerTemplate " + spawn.getWalkerId() + " to " + holder.npc.getName() + ".");
			} catch (IOException e) {
				e.printStackTrace();
				sendMsg(admin, "Could not save spawn, the NPC's WalkerTemplate may have to be assigned manually.");
			}
			SpawnEngine.spawnObject(spawn, holder.npc.getInstanceId());
		}
		DataManager.WALKER_DATA.saveData(holder.template.getRouteId());
		String msg = "An attempt was made to output the WalkerTemplate to a new file on the Server. The file should be here:\n"
				   + "/.../data/static_data/npc_walker/generated_npc_walker_" + holder.template.getRouteId() + ".xml\n"
				   + "Check the server logs for errors if the file cannot be found.";
		sendMsg(admin, msg);
		holder.gc();
		holder = null;
		GM_WALKER_CACHE.remove(admin.getObjectId());
	}
	
	/**
	 * Sends a system message to the Player.
	 * 
	 * @param admin -- The Player to send the message to.
	 * @param msg -- The message to send.
	 */
	private static void sendMsg(Player admin, String msg) {
		PacketSendUtility.sendMessage(admin, msg);
	}
}
