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
    public void testCreateRoomsAndBeacons() {
        SbRoom s012 = SbFactory.findOrCreateSbRoom("S012", "Office Reinhard Herzog");
        SbBeacon b012 = SbFactory.findOrCreateSbBeacon("1y0N", "Office hzg");
        b012.setIBeaconId("f7826da6-4fa2-4e98-8024-bc5b71e0893e", "2970", "10793");
        s012.assignBeacon(b012);

        SbRoom s015 = SbFactory.findOrCreateSbRoom("S015", "HIWI Team Room");
        SbBeacon b015 = SbFactory.findOrCreateSbBeacon("GlJA", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "44933", "46799");
        s015.assignBeacon(b015);

        SbRoom s011 = SbFactory.findOrCreateSbRoom("S011", "Office Hylke van der Schaaf");
        SbBeacon b011 = SbFactory.findOrCreateSbBeacon("1EcF", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "29297", "19811");
// broken        SbBeacon b011 = SbFactory.findOrCreateSbBeacon("5PFJ", "Office scf");
        b011.setIBeaconId("f7826da6-4fa2-4e98-8024-bc5b71e0893e", "46536", "41419");
        s011.assignBeacon(b011);

        SbRoom s014 = SbFactory.findOrCreateSbRoom("S014", "Office Michael Jacoby");
        SbBeacon b014 = SbFactory.findOrCreateSbBeacon("E6Q5", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "25904", "43027");
        s014.assignBeacon(b014);
        
        SbFactory.findOrCreateSbBeacon("KmKt", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "30464", "56999");
        SbFactory.findOrCreateSbBeacon("omjb", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "5249", "12232");
        SbFactory.findOrCreateSbBeacon("jW74", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "3683", "53063");
        SbFactory.findOrCreateSbBeacon("kFSN", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "10025", "29247");
        SbFactory.findOrCreateSbBeacon("eBtX", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "49662", "41870");
        SbFactory.findOrCreateSbBeacon("IvyE", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "28431", "19961");
        SbFactory.findOrCreateSbBeacon("5bQS", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "42885", "59697");
        SbFactory.findOrCreateSbBeacon("iG3x", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "49015", "1076");
        SbFactory.findOrCreateSbBeacon("9FaU", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "43491", "28011");
        SbFactory.findOrCreateSbBeacon("TCZg", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "56842", "7917");
        SbFactory.findOrCreateSbBeacon("XrxW", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "65085", "35435");
        SbFactory.findOrCreateSbBeacon("8ACi", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "55692", "22904");
        SbFactory.findOrCreateSbBeacon("Q8dr", "", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "55542", "14675");
        
        assertNotNull("room object should have been created", s011);
        assertNotNull("room object should have been created", s012);
        assertNotNull("room object should have been created", s015);
    }

    @Test
    public void testFindBeacons() {
        SbBeacon b = SbFactory.findByBeaconIds("f7826da6-4fa2-4e98-8024-bc5b71e0893e", "2970", "10793");
        assertNotNull("beacon should have been found", b);
    }

    @Test
    public void testCreateObservations() {
        SbBeacon b012 = SbFactory.findOrCreateSbBeacon("1y0N", "Office hzg");
        b012.setIBeaconId("f7826da6-4fa2-4e98-8024-bc5b71e0893e", "2970", "10793");

        SbBeacon b012ToBeFound = SbFactory.findByBeaconIds("f7826da6-4fa2-4e98-8024-bc5b71e0893e", "2970", "10793");
        assertTrue(b012 == b012ToBeFound);
        b012ToBeFound.addProximityObservation(0.1, "JUNIT Tester");
    }
}
