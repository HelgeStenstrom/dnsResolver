package se.helgestenstrom;

public class DnsMessage {


    private final int id;

    public DnsMessage(int id) {
        this.id = id;
    }

    public String message() {
        String ids = String.format("%04x", id);
        return ids + "0100000100000000000003646e7306676f6f676c6503636f6d0000010001";
    }
}
