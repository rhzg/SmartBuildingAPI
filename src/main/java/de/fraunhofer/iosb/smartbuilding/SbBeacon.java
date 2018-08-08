package de.fraunhofer.iosb.smartbuilding;

import java.util.Map;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.Thing;

public class SbBeacon {
	private SbRoom myRoom;
	private Thing myBeaconThing;
	
	public SbBeacon(Thing thing) {
		// TODO Auto-generated constructor stub
		myRoom = null;
		myBeaconThing = thing;
	}
	
	public String toString() {
		return "BLE Beacon: " + getName() + " " + getDescription();
	}

	public String getName () {
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
		SbRoom room = SbFactory.getRoom(roomName);
		Map<String, Object> props = myBeaconThing.getProperties();
		props.put(SbFactory.TAG_TO_ROOM_REF, room.getMyThing().getId());
		myBeaconThing.setProperties(props);
		SbFactory.update(myBeaconThing);
		myRoom = room;
	}
}
