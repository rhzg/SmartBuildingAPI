package de.fraunhofer.iosb.smartbuilding;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

public class FactoryTest {

	@Before
	public void initializeFactory () {
		try {
			URL serviceEndpoint = new URL("http://symbiote.iosb.fraunhofer.de:8090/FROST-Server/v1.0");
			SensorThingsService service = new SensorThingsService(serviceEndpoint);
			SbFactory.initialize(service);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}		
	}

	@Test
	public void testGetRoomList() {
		try {
			List<SbRoom> rooms = SbFactory.getRoomList();

			for (SbRoom r : rooms) {
				String s = r.toString();
				assertNotNull("room object should have a string representation", s);

			}

		} catch (ServiceFailureException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetRoom() {
		try {
			SbRoom room = SbFactory.getRoom("S012");
			String s = room.toString();
			assertNotNull("room object should have a string representation", s);

			@SuppressWarnings("unused")
			String result;
			result = room.getName();
			result = room.getDescription();
			result = room.getToken();
			result = room.getRoomNr();
			
			room = SbFactory.getRoom("XYZ000");
			assertNull("romm object should not be found",room);

		} catch (

		ServiceFailureException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreateBeacon () {
			SbBeacon beacon = SbFactory.findOrCreateBeacon("BLE0815", "test beacon");
			String s = beacon.toString();
			assertNotNull("room object should have a string representation", s);
			SbBeacon beacon2 = SbFactory.findOrCreateBeacon("BLE0815", "dummy");
			assertTrue(beacon == beacon2);
	}
	
	@Test
	public void testAssignBeaconToRoom () throws ServiceFailureException {
		SbBeacon b0815 = SbFactory.findOrCreateBeacon("BLE0815", "test beacon");
		SbBeacon b0816 = SbFactory.findOrCreateBeacon("BLE0816", "test beacon");
		SbRoom s012 = SbFactory.getRoom("S012");
		SbRoom s011 = SbFactory.getRoom("S011");
		s012.assignBeacon(b0815);
		s012.assignBeacon(b0816);
		s011.assignBeacon(b0815);
		
	}
}
