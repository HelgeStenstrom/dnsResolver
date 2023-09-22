package se.helgestenstrom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        ByteList byteList = ByteList.of(returned);
        Decoder decoder = new Decoder(byteList);
        DnsMessage dnsMessage = decoder.getDnsMessage();
        Header header = dnsMessage.getHeader();
        int id = header.getId().id();

        assertEquals(0xabcd, id);
    }
}