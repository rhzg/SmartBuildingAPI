package de.fraunhofer.iosb.smartbuilding;

import java.util.Map;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Id;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

public class SbBeacon {
	private SbRoom myRoom;
	private Thing myBeaconThing;
	private SensorThingsService myService;

	public SbBeacon(SensorThingsService service, Thing thing) {
		myRoom = null;
		myBeaconThing = thing;
		myService = service;
	}

	public String toString() {
		return "BLE Beacon: " + getName() + " " + getDescription();
	}

	public Id getId() {
		return myBeaconThing.getId();
	}

	public String getName() {
		return myBeaconThing.getName();
	}

	public String getDescription() {
		return myBeaconThing.getDescription();
	}

	public String getRoomName() {
		if (myRoom != null) {
			return myRoom.getName();
		}
		return "undefined";
	}

	public void assignRoom(String roomName) throws ServiceFailureException {
		if (myRoom == null) {
			myRoom = SbFactory.findRoom(roomName);
		}
		Map<String, Object> properties = myBeaconThing.getProperties();
		Id assignedRoom = Id.tryToParse(properties.get(SbFactory.TAG_TO_ROOM_REF).toString());
//		Object o = properties.get(SbFactory.TAG_TO_ROOM_REF);
//		Id assignedRoom;
//		if (o != null) {
//			assignedRoom = (Id) o;
//		} else {
//			if (myBeaconThing.getId() instanceof IdLong) {
//				assignedRoom = new IdLong(-1L);
//			} else if (myBeaconThing.getId() instanceof IdString) {
//				assignedRoom = new IdString("-1");
//			} else { // unknown Id, just IdString instead
//				assignedRoom = new IdString("-1");
//			}
//		}
		if ((assignedRoom == null) || (!assignedRoom.equals(myRoom.getMyThing().getId()))) {
			properties.put(SbFactory.TAG_TO_ROOM_REF, myRoom.getMyThing().getId());
			myBeaconThing.setProperties(properties);
			myService.update(myBeaconThing);
			myRoom.assignBeacon(this);
		}
	}
}
