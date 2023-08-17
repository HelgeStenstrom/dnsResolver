package se.helgestenstrom;

public class DnsMessage {


    private final String host;
    private final Id id;
    private final Flags flags;


    public DnsMessage(Id id, Flags flags, String host) {
        this.id = id;
        this.flags = flags;
        this.host = host;
    }

    public String message() {

        String question = "0001";
        String answer = "0000";
        String authority = "0000";
        String additional = "0000";
        String queryType = "0001";
        String queryClass = "0001";

        return id.hex() + flags.hex()
                + question + answer + authority + additional + new Encoded(host).hex() + queryType + queryClass;
    }
}
