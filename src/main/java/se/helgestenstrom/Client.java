package se.helgestenstrom;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Test application
 */
public class Client {

    private final DatagramSocket socket;
    private final InetAddress address;


    /**
     * @param server a DNS server to connect to,
     * @throws SocketException sometimes
     * @throws UnknownHostException sometimes
     */
    public Client(String server) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName(server);
    }

    /**
     * @return the response from a DNS server
     * @throws IOException if there are problems
     */
    public String sendSomething() throws IOException {

        var dnsMessage = new DnsMessage(new Id(22), new Flags(true), "dns.google.com");

        byte[] buf = dnsMessage.bytes();

        int port = 53;
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        socket.send(packet);
        socket.receive(packet);
        return new String(packet.getData(), 0, packet.getLength());

    }

    /**
     * Closes the connection
     */
    public void close() {
        socket.close();
    }
}
