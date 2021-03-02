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

import java.util.Map;

import com.aionemu.gameserver.ai2.mechanics.AbstractMechanicsAI2;
import com.aionemu.gameserver.ai2.mechanics.events.MechanicEvent;
import com.aionemu.gameserver.model.gameobjects.StaticDoor;


/**
 * 
 * 
 * @author Yon (Aion Reconstruction Project)
 */
public class ControlDoorAction extends Action {
	
	final public int id;
	
	final public int method;
	
	public ControlDoorAction(int id, int method) {
		super(ActionType.control_door);
		this.id = id;
		this.method = method;
	}
	
	@Override
	public void performAction(MechanicEvent event, AbstractMechanicsAI2 ai) {
		//TODO: Create lookup table for id to static id + worldId
		int Unused_So_I_Know_This_Class_Is_Not_In_An_Acceptable_State = 0;
		Map<Integer, StaticDoor> doors = ai.getPosition().getWorldMapInstance().getDoors();
		if (doors == null) {
			LOG.error("Missing Static Doors for map: " + ai.getOwner().getWorldId() + ".");
			return;
		}
		
		int[] doorGroup = lookupStaticDoorGroupById(ai.getOwner().getWorldId(), id);
		if (doorGroup == null) {
			LOG.error("Missing Static Door Group Id: " + id + ") for Mechanic Handler: ["
					  + ai.getOwner().getObjectTemplate().getMechanic()
					  + "]. WorldId: [" + ai.getOwner().getWorldId()
					  + "]");
			return;
		}
		
		for (int i = 0; i < doorGroup.length; i++) {
			StaticDoor door = doors.get(doorGroup[i]);
			if (door == null) {
				LOG.error("Missing Static Door (Door Group Id: " + id + ") for Mechanic Handler: ["
						  + ai.getOwner().getObjectTemplate().getMechanic()
						  + "]. WorldId: [" + ai.getOwner().getWorldId()
						  + "]");
				continue;
			}
			switch (method) {
				case 0:
					//TODO: Open?
					door.setOpen(true);
					break;
				case 1:
					//TODO: Close?
					door.setOpen(false);
					break;
				case 2:
					//TODO: Flip state?
					door.setOpen(!door.isOpen());
					break;
				default:
					assert false:"Unknown Door Control Method";
			}
		}
	}
	
	/**
	 * A hardcoded lookup table for StaticDoor Id's; this table relates the real door group ID and world ID with the editor ID's
	 * used by the WorldMapInstance to identify StaticDoor instances. If the door group cannot be found, this method
	 * will return null.
	 * 
	 * @param worldId -- The worldId this door is expected to be in.
	 * @param id -- The real door group Id of the door (as given by the Mechanics System).
	 * @return The StaticDoor <code>editor_id</code> fields which are used to identify StaticDoor instances by the WorldMapInstance,
	 * null otherwise.
	 */
	final private static int[] lookupStaticDoorGroupById(int worldId, int id) {
		switch (worldId) {
			case 300040000:
				switch (id) {
					case 1: return new int[] {33};
				}
				break;
			case 300080000:
				switch (id) {
					case 0: return new int[] {60, 61, 63, 64, 65, 66};
				}
				break;
			case 300100000:
				switch (id) {
					case 0: return new int[] {39, 43, 70, 73, 74, 75, 80, 91, 93, 94};
				}
				break;
			case 300110000:
				switch (id) {
					case 0: return new int[] {40, 44, 45, 46, 47, 48};
					case 1: return new int[] {17, 18};
				}
				break;
			case 300120000:
				switch (id) {
					case 0: return new int[] {1, 2, 3, 11, 14, 15, 17, 18, 19, 20, 28, 74, 76, 79, 80};
				}
				break;
			case 300130000:
				switch (id) {
					case 0: return new int[] {1, 2, 3, 5, 6, 9, 10, 14, 17, 18, 28, 74, 76, 79, 80};
				}
				break;
			case 300140000:
				switch (id) {
					case 0: return new int[] {1, 2, 3, 11, 14, 15, 17, 18, 19, 20, 28, 74, 76, 79, 80};
				}
				break;
			case 300150000:
				switch (id) {
					case 0: return new int[] {97, 102, 121};
					case 1: return new int[] {99};
				}
				break;
			case 300160000:
				switch (id) {
					case 0: return new int[] {100, 295};
					case 1: return new int[] {111};
				}
				break;
			case 300170000:
				switch (id) {
					case 0: return new int[] {531, 532, 533, 534, 536};
					case 1: return new int[] {471};
					case 2: return new int[] {470};
					case 3: return new int[] {467};
					case 4: return new int[] {473};
					case 5: return new int[] {466};
					case 6: return new int[] {406};
					case 7: return new int[] {535};
				}
				break;
			case 300190000:
				switch (id) {
					case 0: return new int[] {180};
					case 1: return new int[] {48, 49};
					case 2: return new int[] {7};
				}
				break;
			case 300210000:
				switch (id) {
					case 0: return new int[] {182, 183};
					case 1: return new int[] {4, 173};
				}
				break;
			case 300220000:
				switch (id) {
					case 0: return new int[] {19};
					case 1: return new int[] {15, 16, 18, 69};
				}
				break;
			case 300230000:
				switch (id) {
					case 0: return new int[] {2, 81, 259, 325, 326, 360};
				}
				break;
			case 300240000:
				switch (id) {
					case 0: return new int[] {234};
					case 101: return new int[] {90};
					case 111: return new int[] {138};
					case 112: return new int[] {128};
					case 199: return new int[] {85};
					case 201: return new int[] {26};
					case 299: return new int[] {68};
					case 300: return new int[] {177};
					case 301: return new int[] {308};
					case 302: return new int[] {307};
					case 311: return new int[] {174};
					case 312: return new int[] {178};
					case 321: return new int[] {175};
					case 322: return new int[] {230};
					case 411: return new int[] {2};
					case 412: return new int[] {17};
					case 413: return new int[] {103};
				}
				break;
			case 300250000:
				switch (id) {
					case 5: return new int[] {78};
					case 10: return new int[] {367};
					case 20: return new int[] {69};
					case 21: return new int[] {111};
					case 22: return new int[] {70};
					case 23: return new int[] {45};
					case 24: return new int[] {67};
					case 25: return new int[] {52};
					case 30: return new int[] {39};
					case 32: return new int[] {122};
				}
				break;
			case 300260000:
				switch (id) {
					case 11: return new int[] {51};
				}
				break;
			case 300270000:
				switch (id) {
					case 0: return new int[] {158};
					case 1: return new int[] {14};
					case 2: return new int[] {15};
					case 3: return new int[] {64};
					case 4: return new int[] {76};
					case 5: return new int[] {26};
					case 6: return new int[] {10};
					case 9: return new int[] {11};
					case 10: return new int[] {18};
					case 31: return new int[] {210};
					case 37: return new int[] {517};
				}
				break;
			case 300280000:
				switch (id) {
					case 1: return new int[] {82};
					case 2: return new int[] {145};
					case 11: return new int[] {16};
					case 12: return new int[] {75};
					case 13: return new int[] {54};
					case 21: return new int[] {98};
					case 22: return new int[] {43};
					case 23: return new int[] {150};
					case 24: return new int[] {70};
					case 25: return new int[] {236};
				}
				break;
			case 300310000:
				switch (id) {
					case 0: return new int[] {104, 307};
					case 10: return new int[] {103};
					case 13: return new int[] {107};
					case 15: return new int[] {294, 295};
					case 16: return new int[] {118};
					case 21: return new int[] {105};
					case 22: return new int[] {219};
					case 31: return new int[] {167, 205};
					case 32: return new int[] {165, 200};
					case 33: return new int[] {114, 183};
					case 34: return new int[] {87, 189};
				}
				break;
			case 300350000:
				switch (id) {
					case 1: return new int[] {176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247};
				}
				break;
			case 300360000:
				switch (id) {
					case 1: return new int[] {121, 135, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 148, 149, 150, 151};
				}
				break;
			case 300380000:
				switch (id) {
					case 1: return new int[] {9};
				}
				break;
			case 300420000:
				switch (id) {
					case 1: return new int[] {176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199, 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247};
				}
				break;
			case 300430000:
				switch (id) {
					case 1: return new int[] {121, 135, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 148, 149, 150, 151};
				}
				break;
			case 300440000:
				switch (id) {
					case 0: return new int[] {182, 183};
					case 1: return new int[] {4, 173};
				}
				break;
			case 300450000:
				switch (id) {
					case 1: return new int[] {61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 78, 82, 83, 84, 85, 103, 104, 105, 106, 107, 109, 110, 113, 115, 116, 118, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 146, 147, 148, 149, 150, 151, 152, 153, 154, 156, 157, 158, 160, 161, 162, 163, 164, 165};
				}
				break;
			case 300460000:
				switch (id) {
					case 0: return new int[] {39, 43, 73, 74, 75};
				}
				break;
			case 300470000:
				switch (id) {
					case 101: return new int[] {88};
					case 102: return new int[] {84};
					case 103: return new int[] {62, 108, 118};
					case 104: return new int[] {82, 86, 117};
					case 107: return new int[] {79, 90, 91, 92, 93, 94};
				}
				break;
			case 300480000:
				switch (id) {
					case 0: return new int[] {6, 7, 8, 10, 11, 12, 13, 101};
					case 1: return new int[] {3};
					case 10: return new int[] {4};
				}
				break;
			case 300510000:
				switch (id) {
					case 0: return new int[] {11, 49, 54, 78, 79, 103};
					case 1: return new int[] {48};
					case 3: return new int[] {22};
					case 4: return new int[] {37};
					case 7: return new int[] {711};
					case 8: return new int[] {51};
					case 9: return new int[] {56};
					case 11: return new int[] {610};
					case 12: return new int[] {369};
					case 15: return new int[] {706};
				}
				break;
			case 300530000:
				switch (id) {
					case 0: return new int[] {1, 9, 40, 42, 43, 45, 46, 48, 49, 50, 53, 54};
					case 1: return new int[] {32};
				}
				break;
			case 300540000:
				switch (id) {
					case 1: return new int[] {311};
				}
				break;
			case 300550000:
				switch (id) {
					case 1: return new int[] {89, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 106, 107, 108, 109, 110, 112, 117, 118, 120, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147};
				}
				break;
			case 300570000:
				switch (id) {
					case 1: return new int[] {61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 78, 82, 83, 84, 85, 103, 104, 105, 106, 107, 109, 110, 113, 115, 116, 118, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 146, 147, 148, 149, 150, 151, 152, 153, 154, 156, 157, 158, 160, 161, 162, 163, 164, 165};
				}
				break;
			case 300580000:
				switch (id) {
					case 1: return new int[] {26};
				}
				break;
			case 300590000:
				switch (id) {
					case 100: return new int[] {47};
				}
				break;
			case 300600000:
				switch (id) {
					case 0: return new int[] {19};
					case 1: return new int[] {15, 16, 18, 69};
				}
				break;
			case 300700000:
				switch (id) {
					case 0: return new int[] {60, 61, 63, 64, 65, 66};
				}
				break;
			case 301000000:
				switch (id) {
					case 1: return new int[] {3};
				}
				break;
			case 301010000:
				switch (id) {
					case 0: return new int[] {70, 80, 813};
				}
				break;
			case 301020000:
				switch (id) {
					case 0: return new int[] {91, 93, 94, 809, 810};
				}
				break;
			case 301030000:
				switch (id) {
					case 0: return new int[] {70, 80, 813};
				}
				break;
			case 301040000:
				switch (id) {
					case 0: return new int[] {91, 93, 94, 809, 810};
				}
				break;
			case 301050000:
				switch (id) {
					case 0: return new int[] {811, 814};
				}
				break;
			case 301100000:
				switch (id) {
					case 1: return new int[] {61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 78, 82, 83, 84, 85, 103, 104, 105, 106, 107, 109, 110, 113, 115, 116, 118, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 146, 147, 148, 149, 150, 151, 152, 153, 154, 156, 157, 158, 160, 161, 162, 163, 164, 165};
				}
				break;
			case 301120000:
				switch (id) {
					case 1: return new int[] {4, 5, 8, 10, 11, 144};
				}
				break;
			case 301130000:
				switch (id) {
					case 102: return new int[] {383};
					case 201: return new int[] {382};
					case 202: return new int[] {387};
					case 203: return new int[] {59};
					case 204: return new int[] {384};
					case 205: return new int[] {373};
					case 206: return new int[] {374};
					case 207: return new int[] {380};
					case 208: return new int[] {377};
					case 209: return new int[] {372};
					case 210: return new int[] {375};
					case 301: return new int[] {378};
					case 302: return new int[] {388};
					case 303: return new int[] {379};
					case 304: return new int[] {385};
					case 305: return new int[] {381};
					case 306: return new int[] {376};
				}
				break;
			case 301140000:
				switch (id) {
					case 0: return new int[] {319, 430, 467, 689, 691, 1530};
					case 1: return new int[] {202, 690};
					case 2: return new int[] {350, 400};
					case 3: return new int[] {159, 177};
					case 4: return new int[] {160};
					case 5: return new int[] {10};
					case 6: return new int[] {154};
				}
				break;
			case 301170000:
				switch (id) {
					case 0: return new int[] {1, 9, 40, 42, 43, 45, 46, 48, 49, 50, 53, 54};
					case 1: return new int[] {32};
				}
				break;
			case 301180000:
				switch (id) {
					case 1: return new int[] {26};
				}
				break;
			case 301190000:
				switch (id) {
					case 0: return new int[] {6, 7, 8, 10, 11, 12, 13, 101};
					case 1: return new int[] {3};
					case 10: return new int[] {4};
				}
				break;
			case 301210000:
				switch (id) {
					case 1: return new int[] {176, 177};
				}
				break;
			case 301220000:
				switch (id) {
					case 1: return new int[] {2, 17, 26, 35};
				}
				break;
			case 301230000:
				switch (id) {
					case 1: return new int[] {129};
				}
				break;
			case 301240000:
				switch (id) {
					case 0: return new int[] {1, 2, 3, 11, 14, 15, 17, 18, 19, 20, 28, 74, 76, 79, 80};
				}
				break;
			case 301250000:
				switch (id) {
					case 0: return new int[] {1, 2, 3, 5, 6, 9, 10, 14, 17, 18, 28, 74, 76, 79, 80};
				}
				break;
			case 301260000:
				switch (id) {
					case 0: return new int[] {1, 2, 3, 11, 14, 15, 17, 18, 19, 20, 28, 74, 76, 79, 80};
				}
				break;
			case 301280000:
				switch (id) {
					case 0: return new int[] {1, 2, 3, 11, 14, 15, 17, 18, 19, 20, 28, 74, 76, 79, 80};
				}
				break;
			case 301290000:
				switch (id) {
					case 0: return new int[] {1, 2, 3, 5, 6, 9, 10, 14, 17, 18, 28, 74, 76, 79, 80};
				}
				break;
			case 301300000:
				switch (id) {
					case 0: return new int[] {1, 2, 3, 11, 14, 15, 17, 18, 19, 20, 28, 74, 76, 79, 80};
				}
				break;
			case 301320000:
				switch (id) {
					case 100: return new int[] {47};
				}
				break;
			case 310050000:
				switch (id) {
					case 0: return new int[] {167, 168, 169, 170, 171, 172, 192};
				}
				break;
			case 310080000:
				switch (id) {
					case 0: return new int[] {1, 2, 10};
				}
				break;
			case 310110000:
				switch (id) {
					case 0: return new int[] {61, 62, 63, 64, 95, 127, 129, 130, 233};
				}
				break;
			case 320090000:
				switch (id) {
					case 0: return new int[] {1, 2, 10};
				}
				break;
			case 320110000:
				switch (id) {
					case 0: return new int[] {6, 7};
				}
				break;
			case 320120000:
				switch (id) {
					case 0: return new int[] {1, 2, 4, 5, 10, 27, 28, 53, 54, 55, 60};
				}
				break;
			case 320130000:
				switch (id) {
					case 0: return new int[] {1, 122, 253, 254, 255, 256, 257};
				}
				break;
			case 600020000:
				switch (id) {
					case 1: return new int[] {480};
					case 2: return new int[] {860};
					case 3: return new int[] {146};
				}
				break;
			case 600030000:
				switch (id) {
					case 0: return new int[] {164};
				}
				break;
		}
		return null;
	}
	
	@Override
	public int hashCode() {
		return 3*id + 5*method;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ControlDoorAction) {
			ControlDoorAction o = (ControlDoorAction) obj;
			return (o.id == id && o.method == method);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return super.toString() + " [" + id + "] --> (" + method + ")";
	}
	
}
