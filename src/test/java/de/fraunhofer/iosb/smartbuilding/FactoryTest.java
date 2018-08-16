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
        SbRoom s012 = SbFactory.findOrCreateSbRoom("S012", "Office Reinhard Herzog");
        SbBeacon b012 = SbFactory.findOrCreateSbBeacon("1y0N", "Office hzg");
        b012.setIBeaconId("f7826da6-4fa2-4e98-8024-bc5b71e0893e", "2970", "10793");
        s012.assignBeacon(b012);

        SbRoom s015 = SbFactory.findOrCreateSbRoom("S015", "HIWI Team Room");
        SbBeacon b015 = SbFactory.findOrCreateSbBeacon("S015", "HIWI Team Room");
        b015.setIBeaconId("f7826da6-4fa2-4e98-8024-bc5b71e0893e", "1", "2");
        s015.assignBeacon(b015);

        SbRoom s011 = SbFactory.findOrCreateSbRoom("S011", "Office Hylke van der Schaaf");
        SbBeacon b011 = SbFactory.findOrCreateSbBeacon("5PFJ", "Office scf");
        b011.setIBeaconId("f7826da6-4fa2-4e98-8024-bc5b71e0893e", "46536", "41419");
        s011.assignBeacon(b011);
        
        String roomForS011= b011.getRoomName();
        assertTrue(roomForS011.equals("S011"));
        
        String roomForS015= b015.getRoomName();
        assertTrue(roomForS015.equals("S015"));
    }
}
