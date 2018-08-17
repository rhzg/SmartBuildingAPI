package de.fraunhofer.iosb.smartbuilding;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;

public class AssignmentTest {

    SbRoom tr001;
    SbRoom tr002;
    SbBeacon tb001;
    
    @Before
    public void setUp() throws Exception {
        try {
            URL serviceEndpoint = new URL("http://symbiote.iosb.fraunhofer.de:8090/FROST-Server/v1.0");
            SensorThingsService service = new SensorThingsService(serviceEndpoint);
            SbFactory.initialize(service);
            
            tr001 = SbFactory.findOrCreateSbRoom("Test001", "Test Room 001");
            tr002 = SbFactory.findOrCreateSbRoom("Test002", "Test Room 002");
            tb001 = SbFactory.findOrCreateSbBeacon("Test001", "Test Beacon 001", "f7826da6-4fa2-4e98-8024-bc5b71e0893e", "9999", "1");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() throws Exception {
        // remove test elements from server
        tr001.removeFromServer();
        tr002.removeFromServer();
        tb001.removeFromServer();
    }

    @Test
    public void test() {
        tr001.assignBeacon(tb001);
        tr002.assignBeacon(tb001);
    }

}
