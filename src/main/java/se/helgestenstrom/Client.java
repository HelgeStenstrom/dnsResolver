package se.helgestenstrom;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Test application
 */
public class Client {

    private final InetAddress address;
    private final DatagramSocket socket;


    /**
     * @param server a DNS server to connect to,
     * @throws SocketException      sometimes
     * @throws UnknownHostException sometimes
     */
    public Client(String server) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName(server);
    }

    /**
     * @param numericalId      the ID that should go out with the DNS message, and is expected to come back.
     * @param recursionDesired flag of the Header; true if recursion is desired.
     * @param host             who to look up.
     * @return the response from a DNS server
     * @throws IOException if there are problems
     */
    public String sendSomething(int numericalId, boolean recursionDesired, String host) throws IOException {

        final Id id = new Id(numericalId);
        final Flags flags = new Flags(recursionDesired);
        final Question question = new Question(new Name(host), 1, 1);
        var dnsMessage = new DnsMessage(new Header(id, flags, 1, 0, 0, 0), List.of(question), List.of(), List.of(), List.of());

        byte[] buf = dnsMessage.bytes();

        int port = 53;
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);

        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        socket.receive(receivePacket);

        byte[] bytes = bytesOf(receivePacket);
        return hexString(bytes);

    }

    private byte[] bytesOf(DatagramPacket receivePacket) {
        int length = receivePacket.getLength();
        byte[] data = receivePacket.getData();

        return Arrays.copyOfRange(data, 0, length);
    }

    private String hexString(byte[] data) {
        var buffer = ByteBuffer.wrap(data);

        return Stream.generate(buffer::get)
                .limit(buffer.capacity())
                .map(b -> String.format("%02x", b))
                .limit(data.length)
                .collect(Collectors.joining());
    }

    /**
     * Closes the connection
     */
    public void close() {
        socket.close();
    }
}
