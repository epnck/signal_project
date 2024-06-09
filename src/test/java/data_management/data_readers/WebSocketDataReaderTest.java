package data_management.data_readers;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.data_readers.WebSocketDataReader;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketDataReaderTest {

    private static WebSocketServer server;
    private static final int PORT = 8080;

    @BeforeAll
    static void setup() {
        server = new WebSocketServer(new InetSocketAddress(PORT)) {
            @Override
            public void onOpen(org.java_websocket.WebSocket conn, ClientHandshake handshake) {
                // No-op for testing
            }

            @Override
            public void onClose(org.java_websocket.WebSocket conn, int code, String reason, boolean remote) {
                // No-op for testing
            }

            @Override
            public void onMessage(org.java_websocket.WebSocket conn, String message) {
                // No-op for testing
            }

            @Override
            public void onError(org.java_websocket.WebSocket conn, Exception ex) {
                // No-op for testing
            }

            @Override
            public void onStart() {
                // No-op for testing
            }
        };
        server.start();
    }

    @AfterAll
    static void tearDown() throws InterruptedException {
        server.stop();
    }

    private static class TestWebSocketClient extends WebSocketClient {
        private final BlockingQueue<String> messageQueue = new LinkedBlockingQueue<>();

        public TestWebSocketClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
            // No-op for testing
        }

        @Override
        public void onMessage(String message) {
            messageQueue.add(message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            // No-op for testing
        }

        @Override
        public void onError(Exception ex) {
            // No-op for testing
        }

        public void simulateMessage(String message) {
            this.onMessage(message);
        }

        public String getNextMessage() throws InterruptedException {
            return messageQueue.take();
        }
    }

    @Test
    void testProcessMessageValidData() throws IOException, InterruptedException, URISyntaxException {
        DataStorage dataStorage = DataStorage.getInstance();
        WebSocketDataReader reader = new WebSocketDataReader(8080);

        //set up a test WebSocket client
        URI uri = new URI("ws://localhost:8080");
        TestWebSocketClient client = new TestWebSocketClient(uri);
        reader.startReading(dataStorage);

        String message = "Patient ID: 123, Timestamp: 1627845123, Label: BloodSaturation, Data: 95";
        client.simulateMessage(message);

        List<PatientRecord> records = dataStorage.getRecords(123, 0, System.currentTimeMillis());
        assertNotNull(records);
        assertEquals(95, records.get(0).getMeasurementValue());
        assertEquals("BloodSaturation", records.get(0).getRecordType());
        assertEquals(1627845123, records.get(0).getTimestamp());
        assertEquals(123, records.get(0).getPatientId());
    }

    @Test
    void testProcessMessageInvalidData() throws IOException, URISyntaxException {
        DataStorage dataStorage = DataStorage.getInstance();
        WebSocketDataReader reader = new WebSocketDataReader(8080);

        //set up a test WebSocket client
        URI uri = new URI("ws://localhost:8080");
        TestWebSocketClient client = new TestWebSocketClient(uri);
        reader.startReading(dataStorage);

        //invalid messages due to invalid data type
        String message1 = "Patient ID: 123, Timestamp: 1627845123, Label: BloodSaturation, Data: xyz";
        String message2 = "Patient ID: 123, Timestamp: abc, Label: BloodSaturation, Data: 95";

        client.simulateMessage(message1);
        client.simulateMessage(message2);

        assertNull(dataStorage.getRecords(123, 0, System.currentTimeMillis()));
    }

    @Test
    void testProcessMessagePartialData() throws IOException, URISyntaxException {
        DataStorage dataStorage = DataStorage.getInstance();
        WebSocketDataReader reader = new WebSocketDataReader(8080);

        //set up a test WebSocket client
        URI uri = new URI("ws://localhost:8080");
        TestWebSocketClient client = new TestWebSocketClient(uri);
        reader.startReading(dataStorage);

        //messages not containing all needed data
        String message1 = "Patient ID: 123, Timestamp: 1627845123, Label: BloodSaturation";
        String message2 = "Patient ID: 123, Label: BloodSaturation, Data: 95";
        String message3 = "Patient ID: 123, Timestamp: 1627845123, Data: 95";
        String message4 = "Timestamp: 1627845123, Label: BloodSaturation, Data: 95";


        client.simulateMessage(message1);
        client.simulateMessage(message2);
        client.simulateMessage(message3);
        client.simulateMessage(message4);

        assertNull(dataStorage.getRecords(123, 0, System.currentTimeMillis()));
    }
}