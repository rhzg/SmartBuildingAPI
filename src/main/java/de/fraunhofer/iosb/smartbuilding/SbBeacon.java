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

import java.util.List;
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
        myRoom = SbFactory.findRoom(roomName);
        Map<String, Object> properties = myBeaconThing.getProperties();
        Object roomRef = properties.get(SbFactory.TAG_TO_ROOM_REF);
        Id assignedRoom = null;
        if (roomRef != null) {
            assignedRoom = Id.tryToParse(roomRef.toString());
        }

        if ((assignedRoom == null) || (!assignedRoom.equals(myRoom.getMyThing().getId()))) {
            properties.put(SbFactory.TAG_TO_ROOM_REF, myRoom.getMyThing().getId());
            myBeaconThing.setProperties(properties);
            myService.update(myBeaconThing);
            myRoom.assignBeacon(this);
        }
    }
}
