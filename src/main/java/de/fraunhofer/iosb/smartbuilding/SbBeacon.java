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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.Id;
import de.fraunhofer.iosb.ilt.sta.model.Observation;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

public class SbBeacon {
    private static final Logger LOGGER = LoggerFactory.getLogger(SbBeacon.class);

    private SbRoom myRoom;
    private Thing myBeaconThing;
    private SensorThingsService myService;
    private Datastream proximityDatastream;
    private Datastream batteryDatastream;

    public SbBeacon(SensorThingsService service, Thing thing) {
        myRoom = null;
        myBeaconThing = thing;
        myService = service;
        
        Map<String, Object> properties = myBeaconThing.getProperties();
        Object roomRef = properties.get(SbFactory.TAG_TO_ROOM_REF);
        if (roomRef != null) {
            Id assignedRoom = Id.tryToParse(roomRef.toString());
            myRoom = SbFactory.findRoom(assignedRoom);
        }
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

    public void setIBeaconId(String uuid, String major, String minor) {
        Map<String, Object> properties = myBeaconThing.getProperties();
        properties.put(SbFactory.TAG_UUID_ID, uuid);
        properties.put(SbFactory.TAG_MAJOR_ID, major);
        properties.put(SbFactory.TAG_MINOR_ID, minor);
        try {
            myService.update(myBeaconThing);
        } catch (ServiceFailureException e) {
            e.printStackTrace();
        }
    }

    public String getUUID() {
        Object o = myBeaconThing.getProperties().get(SbFactory.TAG_UUID_ID);
        if (o != null) {
            return o.toString();
        } else {
            return "unknown";
        }
    }

    public String getMajor() {
        Object o = myBeaconThing.getProperties().get(SbFactory.TAG_MAJOR_ID);
        if (o != null) {
            return o.toString();
        } else {
            return "unknown";
        }
    }

    public String getMinor() {
        Object o = myBeaconThing.getProperties().get(SbFactory.TAG_MINOR_ID);
        if (o != null) {
            return o.toString();
        } else {
            return "unknown";
        }
   }

    /*
     * assign given room with @param roomName to beacon. Room and beacon object must exist. 
     * Beacons can only be assigned to one room. Existing references will be removed.
     */
    public void assignRoom(String roomName) {
        myRoom = SbFactory.findRoom(roomName);
        
        Map<String, Object> properties = myBeaconThing.getProperties();
        Object roomRef = properties.get(SbFactory.TAG_TO_ROOM_REF);
        Id assignedRoom = null;
        if (roomRef != null) {
            assignedRoom = Id.tryToParse(roomRef.toString());
        }

        if ((assignedRoom != null) && (!assignedRoom.equals(myRoom.getMyThing().getId()))) {
            // beacon has been assigned to to other room and must be remove before being reassigned 
            SbRoom oldRoom = SbFactory.findRoom(assignedRoom);
            oldRoom.removeBeacon(getId());
        }
        
        if ((assignedRoom == null) || (!assignedRoom.equals(myRoom.getMyThing().getId()))) {
            properties.put(SbFactory.TAG_TO_ROOM_REF, myRoom.getMyThing().getId());
            myBeaconThing.setProperties(properties);
            try {
                myService.update(myBeaconThing);
            } catch (ServiceFailureException e) {
                e.printStackTrace();
            }
            myRoom.assignBeacon(this);
            LOGGER.trace("room {} assigned to beacon {}", roomName, myBeaconThing.getName());
        }
    }

    public void addProximityObservation(double distance, String usercode) {
        Map<String, Object> stringMap = new HashMap<>();
        stringMap.put("user", usercode);

        try {
            if (proximityDatastream == null) {
                proximityDatastream = myBeaconThing.datastreams().query()
                        .filter("name eq '" + SbFactory.BEACON_PROXIMITY_SENSOR + "'").first();
            }
            Observation o = new Observation(distance, proximityDatastream);
            o.setParameters(stringMap);
            myService.create(o);
        } catch (ServiceFailureException e) {
            e.printStackTrace();
        }
        LOGGER.trace("Proximity obseration for {}", usercode);
    }

    public void setBatteryDatastream(Datastream ds) {
        proximityDatastream = ds;

    }

    public void setProximityDatastream(Datastream ds) {
        batteryDatastream = ds;
    }
    
    public void removeFromServer() {
        SbFactory.removeThing(myBeaconThing);
    }
}
