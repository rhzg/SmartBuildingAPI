package de.fraunhofer.iosb.smartbuilding;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geojson.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.Utils;
import de.fraunhofer.iosb.ilt.sta.model.Datastream;
import de.fraunhofer.iosb.ilt.sta.model.Location;
import de.fraunhofer.iosb.ilt.sta.model.ObservedProperty;
import de.fraunhofer.iosb.ilt.sta.model.Sensor;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.ext.EntityList;
import de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

public class SbFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(SbFactory.class);

	static final String TAG_ROOM_NR = "roomNr";
	private static final String TAG_TYPE = "type";
	private static final String TAG_SENSOR_TYPE = "sensorType";
	private static final String TAG_SENSOR_ID = "sensorId";
	static final String TAG_FLOOR = "floor";

	static final String TAG_BEACON_ID = "beaconId";
	static final String TAG_TO_BEACONS_REF = "@toBeacons";
	static final String TAG_TO_ROOM_REF = "@toRoom";
	static final String VALUE_TYPE_BEACON = "beacon";
	static final String BEACON_PROXIMITY_SENSOR = "Proximity";
	static final String BEACON_BATTERY_SENSOR = "Battery";

	private static final String VALUE_TYPE_ROOM = "room";
	private static final String VALUE_TYPE_HALL = "hallway";
	private static final String VALUE_TYPE_OUTSIDE = "outside";
	private static final String VALUE_TYPE_SENSOR = "sensor";
	private static final String VALUE_TYPE_SENSORTYPE = "sensorType";

	static SensorThingsService service;
	private static HashMap<String, SbBeacon> beaconCache = new HashMap<String, SbBeacon>();
	private static HashMap<String, SbRoom> roomCache = new HashMap<String, SbRoom>();

	public static void initialize(SensorThingsService myService) {
		service = myService;
	}

	/*
	 * get list of room facade objects of known room things
	 */
	public static List<SbRoom> getRoomList() throws ServiceFailureException {
		EntityList<Thing> things = service.things().query().filter("properties/type eq 'room'").list();
		List<SbRoom> rooms = new ArrayList<SbRoom>();
		for (Thing t : things) {
			SbRoom r = new SbRoom(service, t);
			r.setName(t.getName());
			r.setDescription(t.getDescription());
			Map<String, Object> p = t.getProperties();
			r.setRoomNr(p.get("roomNr").toString());
			r.setFloor((Integer) (p.get("floor")));
			rooms.add(r);
		}
		return rooms;
	}

	/* 
	 * get list of beacon facade objects for known beacon things
	 */
	public static List<SbBeacon> getBeaconList() throws ServiceFailureException {
		EntityList<Thing> things = service.things().query().filter("properties/type eq 'beacon'").list();
		List<SbBeacon> beacons = new ArrayList<SbBeacon>();
		for (Thing t : things) {
			SbBeacon b = new SbBeacon(service, t);
			beacons.add(b);
			beaconCache.put(b.getName(), b);
		}
		return beacons;
	}

	/*
	 * look up a room thing from service and returns facade object if found. 
	 * returns null otherwise
	 */
	public static SbRoom findRoom(String name) {
		SbRoom r = roomCache.get(name);
		if (r == null) {
			try {
				Thing t = service.things().query().filter("properties/type eq 'room' and name eq '" + name + "'")
						.first();
				if (t != null) {
					r = new SbRoom(service, t);
					roomCache.put(r.getName(), r);
				}
			} catch (ServiceFailureException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return r;
	}

	/*
	 * look up a room thing from service. Creates new room thing if not found.
	 * Returns room facade object.
	 */
	public static SbRoom findOrCreateSbRoom(String name, String description) {
		Map<String, Object> props = new HashMap<>();
		props.put(TAG_TYPE, VALUE_TYPE_ROOM);
		props.put(TAG_ROOM_NR, name);
		Thing thing = null;
		try {
			thing = findOrCreateThing(service, filterProperty(props, TAG_TYPE), name, description, 0, 0, props);
		} catch (ServiceFailureException e) {
			e.printStackTrace();
		}
		SbRoom room = new SbRoom(service, thing);
		roomCache.put(room.getName(), room);
		return room;
	}

	/*
	 * look up a beacon thing from service. Creates new beacon object of not found.
	 * Returns beacon facade object.
	 */
	public static SbBeacon findOrCreateSbBeacon(String beaconId, String description) {
		SbBeacon beacon = beaconCache.get(beaconId);
		if (beacon == null) { // beacon is not in cache
			Map<String, Object> props = new HashMap<>();
			props.put(TAG_BEACON_ID, beaconId);
			props.put(TAG_TYPE, VALUE_TYPE_BEACON);

			Thing beaconThing;
			try {
				beaconThing = findOrCreateThing(service, filterProperty(props, TAG_BEACON_ID), beaconId, description, 0,
						0, props);
				Map<String, Object> sensorProperties = new HashMap<>();
				sensorProperties.put(TAG_BEACON_ID, beaconId);
				sensorProperties.put(TAG_TYPE, VALUE_TYPE_BEACON);

				Sensor proximitySensor = findOrCreateSensor(service, BEACON_PROXIMITY_SENSOR,
						"Kontakt.IO proximity sensor", sensorProperties);
				UnitOfMeasurement um1 = new UnitOfMeasurement("Meter", "m",
						"http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html/Meter");
				ObservedProperty op1;
				op1 = new ObservedProperty("Proximity m",
						new URI("http://www.qudt.org/qudt/owl/1.0.0/unit/Instances.html/property"), "proximity");
				Datastream ds1 = findOrCreateDatastream(service, BEACON_PROXIMITY_SENSOR, "Proximity in Meter", null,
						um1, beaconThing, op1, proximitySensor);

				Sensor batterySensor = findOrCreateSensor(service, BEACON_BATTERY_SENSOR, "Kontakt.IO battery sensor",
						sensorProperties);
				UnitOfMeasurement um2 = new UnitOfMeasurement("Percent", "%",
						"http://www.qudt.org/qudt/owl/1.0.0/unit/index.html#CountingUnit");
				ObservedProperty op2 = new ObservedProperty("battery level",
						new URI("http://www.qudt.org/qudt/owl/1.0.0/unit/index.html#CountingUnit"), "percent");
				Datastream ds2 = findOrCreateDatastream(service, BEACON_BATTERY_SENSOR, "Battery Level in Percent",
						null, um2, beaconThing, op2, batterySensor);

				beacon = new SbBeacon(service, beaconThing);
				beaconCache.put(beacon.getName(), beacon);
			} catch (ServiceFailureException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return beacon;
	}

	// ***************************************************************************
	// Helper functions **********************************************************
	// ***************************************************************************

	public static String quoteForUrl(Object in) {
		if (in instanceof Number) {
			return in.toString();
		}
		return "'" + Utils.escapeForStringConstant(in.toString()) + "'";
	}

	private static String filterProperty(Map<String, Object> properties, String property) {
		Object value = properties.get(property);
		String valueString = quoteForUrl(value);
		return "properties/" + property + " eq " + valueString;
	}

	private static Thing findOrCreateThing(SensorThingsService service, String filter, String name, String description,
			double lon, double lat, Map<String, Object> properties) throws ServiceFailureException {
		EntityList<Thing> thingList;
		if (filter == null) {
			filter = "name eq " + quoteForUrl(name);
		}
		thingList = service.things().query().filter(filter).expand("Locations").list();
		if (thingList.size() > 1) {
			throw new IllegalStateException("More than one thing found with filter " + filter);
		}
		Thing thing;
		if (thingList.size() == 1) {
			thing = thingList.iterator().next();
		} else {
			thing = new Thing(name, description);
			thing.setProperties(properties);
			service.create(thing);

			if (lat != 0 && lon != 0) {
				Location location = new Location(name, "Location of Thing " + name + ".", "application/vnd.geo+json",
						new Point(lon, lat));
				location.getThings().add(thing);
				service.create(location);
				thing.getLocations().add(location);
			}
		}
		return thing;
	}

	private static Sensor findOrCreateSensor(SensorThingsService service, String name, String description,
			Map<String, Object> properties) throws ServiceFailureException {
		EntityList<Sensor> sensorList = service.sensors().query().filter("name eq '" + name + "'").list();
		if (sensorList.size() > 1) {
			throw new IllegalStateException("More than one sensor with name " + name);
		}
		Sensor sensor;
		if (sensorList.size() == 1) {
			sensor = sensorList.iterator().next();
		} else {
			LOGGER.info("Creating Sensor {}.", name);
			sensor = new Sensor(name, description, "text", "Properties not known");
			sensor.setProperties(properties);
			service.create(sensor);
		}
		return sensor;
	}

	private static Datastream findOrCreateDatastream(SensorThingsService service, String name, String desc,
			Map<String, Object> properties, UnitOfMeasurement uom, Thing t, ObservedProperty op, Sensor s)
			throws ServiceFailureException {
		EntityList<Datastream> datastreamList = service.datastreams().query()
				.filter("name eq '" + Utils.escapeForStringConstant(name) + "'").list();
		if (datastreamList.size() > 1) {
			throw new IllegalStateException("More than one datastream with name " + name);
		}
		Datastream ds;
		if (datastreamList.size() == 1) {
			ds = datastreamList.iterator().next();
		} else {
			LOGGER.info("Creating Datastream {}.", name);
			ds = new Datastream(name, desc, "http://www.opengis.net/def/observationType/OGC-OM/2.0/OM_Measurement",
					uom);
			ds.setProperties(properties);
			ds.setThing(t);
			ds.setSensor(s);
			ds.setObservedProperty(op);
			service.create(ds);
		}
		return ds;
	}

}
