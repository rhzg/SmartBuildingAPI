package de.fraunhofer.iosb.smartbuilding;

import static org.junit.Assert.assertFalse;
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
    public void initializeFactory() {
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
    public void testCreateBeacons() {
        SbBeacon beacon = SbFactory.findOrCreateSbBeacon("BLE0815", "test beacon");
        String s = beacon.toString();
        assertNotNull("room object should have a string representation", s);
        SbBeacon beacon2 = SbFactory.findOrCreateSbBeacon("BLE0815", "dummy");
        assertTrue(beacon == beacon2);
    }

    @Test
    public void testCreateRooms() {
        SbRoom s012 = SbFactory.findOrCreateSbRoom("S012", "Office hzg");
        SbRoom s011 = SbFactory.findOrCreateSbRoom("S011", "Office scf");
        SbRoom s015 = SbFactory.findOrCreateSbRoom("S015", "HIWI Team Room");
        assertNotNull("room object should have been created", s011);
        assertNotNull("room object should have been created", s012);
        assertNotNull("room object should have been created", s015);
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
    public void testGetBeaconList() {
        List<SbBeacon> beacons = SbFactory.getBeaconList();
        for (SbBeacon b : beacons) {
            assertNotNull("beacon object should be defined", b);
        }

    }

    @Test
    public void testGetRoom() {
        try {
            SbRoom room = SbFactory.findRoom("S012");
            String s = room.toString();
            assertNotNull("room object should have a string representation", s);

            @SuppressWarnings("unused")
            String result;
            result = room.getName();
            result = room.getDescription();
            result = room.getToken();
            result = room.getRoomNr();
            room.setFloor(99);
            int floor = room.getFloor();
            floor = room.getFloor();
            assertTrue(floor == 99);
            room.setFloor(0);
            floor = room.getFloor();
            assertTrue(floor == 0);

            room = SbFactory.findRoom("XYZ000");
            assertNull("romm object should not be found", room);

        } catch (

        ServiceFailureException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAssignBeaconToRoom() throws ServiceFailureException {
        SbBeacon b0815 = SbFactory.findOrCreateSbBeacon("BLE0815", "test beacon");
        SbBeacon b0816 = SbFactory.findOrCreateSbBeacon("BLE0816", "test beacon");
        SbRoom s012 = SbFactory.findRoom("S012");
        SbRoom s011 = SbFactory.findRoom("S011");
        s012.assignBeacon(b0815);
        s012.assignBeacon(b0816);
        String roomForB0815 = b0815.getRoomName();
        assertTrue(roomForB0815.equals("S012"));
        String roomForB0816 = b0816.getRoomName();
        assertTrue(roomForB0815.equals(roomForB0816));
        s011.assignBeacon(b0815);
        roomForB0815 = b0815.getRoomName();
        assertFalse(roomForB0815.equals(roomForB0816));

    }
}
