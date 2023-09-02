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

    private final DatagramSocket socket;
    private final InetAddress address;


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
     * @return the response from a DNS server
     * @throws IOException if there are problems
     * @param numericalId the ID that should go out with the DNS message, and is expected to come back.
     */
    public String sendSomething(int numericalId) throws IOException {

        final Id id = new Id(numericalId);
        final Flags flags = new Flags(true);
        final Question question = new Question(new Name("dns.google.com"), 1, 1);
        var dnsMessage = new DnsMessage(new Header(id, flags, 1, 20, 21, 22), List.of(question));

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
