package de.fraunhofer.iosb.smartbuilding;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;

import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

public class CreateBeaconsTest {

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
        SbBeacon b012 = SbFactory.findOrCreateSbBeacon("1y0N", "Office hzg");
        b012.setIBeaconId("f7826da6-4fa2-4e98-8024-bc5b71e0893e", "2970", "10793");

        SbBeacon b015 = SbFactory.findOrCreateSbBeacon("S015", "HIWI Team Room");
        b015.setIBeaconId("00000000-0000-4e98-8024-bc5b71e0893e", "1", "2");
        
        SbBeacon b011 = SbFactory.findOrCreateSbBeacon("5PFJ", "Office scf");
        b011.setIBeaconId("f7826da6-4fa2-4e98-8024-bc5b71e0893e", "46536", "41419");
        
        SbRoom s012 = SbFactory.findOrCreateSbRoom("S012", "Office Reinhard Herzog");
        SbRoom s011 = SbFactory.findOrCreateSbRoom("S011", "Office Hylke van der Schaaf");
        SbRoom s015 = SbFactory.findOrCreateSbRoom("S015", "HIWI Team Room");
        s012.assignBeacon(b012);
        s011.assignBeacon(b011);
        s015.assignBeacon(b015);
        
        SbBeacon b = SbFactory.findByMajorMinor("2970", "10793");
        assertNotNull("beacon should have been found", b);
    }

    @Test
    public void testCreateObservations() {
        SbBeacon b012 = SbFactory.findOrCreateSbBeacon("1y0N", "Office hzg");
        b012.setIBeaconId("f7826da6-4fa2-4e98-8024-bc5b71e0893e", "2970", "10793");
        
        SbBeacon b012ToBeFound = SbFactory.findByMajorMinor("2970", "10793");
        assertTrue(b012 == b012ToBeFound);
        b012ToBeFound.addProximityObservation(0.1, "JUNIT Tester");
    }
}
