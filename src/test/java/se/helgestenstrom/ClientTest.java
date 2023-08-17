package se.helgestenstrom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
        client = new Client("8.8.8.8");
    }

    @AfterEach
    void tearDown() {
        client.close();
    }

    @Test
    void sendSomething() throws IOException {


        String returned = client.sendSomething();

        fail("test not done");
        assertEquals("wrong", returned);
    }
}