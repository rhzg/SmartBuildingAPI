package de.fraunhofer.iosb.smartbuilding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.Thing;

// TODO for setter update the Thing object as well

public class SbRoom {
	private Thing myThing;
	private String roomNr;
	private int floor;
	private String token = "undefined";
	private HashMap<String, SbBeacon> beacons = new HashMap<String, SbBeacon>();

	public SbRoom(Thing thing) {
		myThing = thing;
	}

	public String getName() {
		return myThing.getName();
	}

	public void setName(String name) {
		myThing.setName(name);
	}

	public String getDescription() {
		return myThing.getDescription();
	}

	public void setDescription(String description) {
		myThing.setDescription(description);
	}

	public String getRoomNr() {
		return (String) myThing.getProperties().get(SbFactory.TAG_ROOM_NR);
	}

	public void setRoomNr(String roomNr) {
		this.roomNr = roomNr;
		Map<String, Object> properties = myThing.getProperties();
		properties.put(SbFactory.TAG_ROOM_NR, roomNr);
	}

	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
		Map<String, Object> properties = myThing.getProperties();
		properties.put(SbFactory.TAG_FLOOR, floor);
	}

	public String toString() {
		return "Room (" + "name=" + getName() + ", description=" + getDescription() + ", roomNr=" + roomNr + ", floor="
				+ floor + ")";
	}

	public String getToken() throws ServiceFailureException {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Thing getMyThing() {
		return myThing;
	}
	
	public void assignBeacon (SbBeacon beacon) throws ServiceFailureException {
		if (!beacons.containsKey(beacon.getName())) {
			beacons.put(beacon.getName(), beacon);
			beacon.assignRoom(getName());
		}
		Map<String, Object> props = myThing.getProperties();
		props.put(SbFactory.TAG_TO_BEACONS_REF, beacons.keySet());
		myThing.setProperties(props);
		SbFactory.update(myThing);
	}
}
