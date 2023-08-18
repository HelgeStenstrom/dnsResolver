package se.helgestenstrom;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClientTest {


    private Client client;

    @BeforeEach
    void setup() throws SocketException, UnknownHostException {
        String googleDnsServer = "8.8.8.8";
        client = new Client(googleDnsServer);
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    @Test
    void sendAndReceive() throws IOException {


        String returned = client.sendSomething(0xabcd);

        // The ID part is in the same position, and is expected to have the same value as was sent to the DNS.
        assertEquals("abcd", returned.substring(0, 4));
    }
}