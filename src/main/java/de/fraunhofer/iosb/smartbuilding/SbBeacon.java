package de.fraunhofer.iosb.smartbuilding;

import java.util.Map;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

public class SbBeacon {
	private SbRoom myRoom;
	private Thing myBeaconThing;
	private SensorThingsService myService;

	public SbBeacon(SensorThingsService service, Thing thing) {
		// TODO Auto-generated constructor stub
		myRoom = null;
		myBeaconThing = thing;
		myService = service;
	}

	public String toString() {
		return "BLE Beacon: " + getName() + " " + getDescription();
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
		Map<String, Object> props = myBeaconThing.getProperties();
		String assignedRoom = props.get(SbFactory.TAG_TO_ROOM_REF).toString();

		if (!assignedRoom.equals(roomName)) {
			props.put(SbFactory.TAG_TO_ROOM_REF, myRoom.getMyThing().getId());
			myBeaconThing.setProperties(props);
			myService.update(myBeaconThing);
			myRoom.assignBeacon(this);
			myRoom = myRoom;
		}
	}
}
