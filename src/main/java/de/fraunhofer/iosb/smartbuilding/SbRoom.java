/* Copyright 2017, Reinhard Herzog (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
*/

package de.fraunhofer.iosb.smartbuilding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.qos.logback.core.read.ListAppender;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Id;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

// TODO for setter update the Thing object as well

public class SbRoom {
	private SensorThingsService myService;
	private Thing myThing;
	private String roomNr;
	private int floor;
	private String token = "undefined";
	private HashMap<String, SbBeacon> beaconsCache = new HashMap<String, SbBeacon>();

	public SbRoom(SensorThingsService service, Thing thing) {
		myService = service;
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
		Map<String, Object> properties = myThing.getProperties();
		if (properties.containsKey(SbFactory.TAG_FLOOR)) {
			return (int) (myThing.getProperties().get(SbFactory.TAG_FLOOR));
		}
		return -1;
	}

	public void setFloor(int floor) throws ServiceFailureException {
		this.floor = floor;
		Map<String, Object> properties = myThing.getProperties();
		properties.put(SbFactory.TAG_FLOOR, floor);
		myService.update(myThing);
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

	@SuppressWarnings("unchecked")
	public List<String> getAssignedBeacons() {
		return (List<String>) (myThing.getProperties().get(SbFactory.TAG_TO_BEACONS_REF));
	}
		
	@SuppressWarnings("unchecked")
	public void assignBeacon(SbBeacon beacon)  {
		if (!beaconsCache.containsKey(beacon.getName())) {
			beaconsCache.put(beacon.getName(), beacon);
			Map<String, Object> properties = myThing.getProperties();
			
			List<Object> beaconRefs = (List<Object>) properties.get(SbFactory.TAG_TO_BEACONS_REF);
			if (beaconRefs == null) { // reference property has not been initialized
			    beaconRefs = new ArrayList<>();
			}
			boolean found = false;
			for (Object o : beaconRefs) {
				Id id = Id.tryToParse(o.toString());
				if (id.equals(beacon.getId())) {
					found = true;
					break;
				}
			}
			
				
			if (!found) {
				beaconRefs.add(beacon.getId());
				properties.put(SbFactory.TAG_TO_BEACONS_REF, beaconRefs);
				myThing.setProperties(properties);
				try {
                    myService.update(myThing);
                } catch (ServiceFailureException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
			}
			beacon.assignRoom(getName());
		}
	}
}
