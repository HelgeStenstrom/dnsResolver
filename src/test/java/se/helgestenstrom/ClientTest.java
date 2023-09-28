package se.helgestenstrom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void sendAndReceiveWithRecursion() throws IOException {

        String returned = client.sendSomething(0xdead, true, "dns.google.com");

        // The ID part is in the same position, and is expected to have the same value as was sent to the DNS.
        assertEquals("dead", returned.substring(0, 4));

        ByteList byteList = ByteList.of(returned);
        Decoder decoder = new Decoder(byteList);
        DnsMessage dnsMessage = decoder.getDnsMessage();
        Header header = dnsMessage.getHeader();
        int id = header.getId().id();

        assertEquals(0xdead, id);
        List<ResourceRecord> answers = dnsMessage.getAnswers();

        List<String> answerNames = answers.stream().map(ResourceRecord::getNameString).toList();
        assertEquals(List.of("dns.google.com", "dns.google.com"), answerNames);

        List<ByteList> datas = answers.stream().map(ResourceRecord::getRData).toList();
        List<List<Integer>> expected = List.of(List.of(8, 8, 4, 4), List.of(8, 8, 8, 8));
        assertSameUnorderedList(expected, datas);

    }

    public static Stream<Arguments> parameters() {
        return Stream.of(
                Arguments.of(false, "dns.google.com")
//                , Arguments.of(false, "dns.google.com")
//                , Arguments.of(true, "dns.google.com")
//                , Arguments.of(false, "dns.google.com")
                , Arguments.of(true, "www.bp.com")
                , Arguments.of(false, "www.bp.com")
                , Arguments.of(false, "198.41.0.4")
        );
    }

    @ParameterizedTest
    @MethodSource("parameters")
        //@Disabled("other problems to fix first")
    void sendAndReceive(boolean recursionDesired, String hostToLookup) throws IOException {

        String returned = client.sendSomething(0xdead, recursionDesired, hostToLookup);

        // The ID part is in the same position, and is expected to have the same value as was sent to the DNS.
        assertEquals("dead", returned.substring(0, 4));

        ByteList byteList = ByteList.of(returned);
        Decoder decoder = new Decoder(byteList);
        DnsMessage dnsMessage = decoder.getDnsMessage();
        Header header = dnsMessage.getHeader();
        int id = header.getId().id();

        assertEquals(0xdead, id);
        List<ResourceRecord> answers = dnsMessage.getAnswers();

        List<String> answerNames = answers.stream().map(ResourceRecord::getNameString).toList();
        //assertEquals(List.of("dns.google.com", "dns.google.com"), answerNames);

        List<ByteList> datas = answers.stream().map(ResourceRecord::getRData).toList();
        List<List<Integer>> expected = List.of(List.of(8, 8, 4, 4), List.of(8, 8, 8, 8));
        //assertSameUnorderedList(expected, datas);
    }

    private void assertSameUnorderedList(List<List<Integer>> expected, List<ByteList> datas) {
        assertEquals(expected.size(), datas.size());
        assertTrue(expected.containsAll(datas));
        //noinspection SuspiciousMethodCalls
        assertTrue(datas.containsAll(expected));
    }
}