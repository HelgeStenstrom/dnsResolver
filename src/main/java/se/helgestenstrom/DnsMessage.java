package se.helgestenstrom;

public class DnsMessage {


    private final Id id;
    private final Flags flags;


    public DnsMessage(Id id, Flags flags) {
        this.id = id;
        this.flags = flags;
    }

    public String message() {

        String question = "0001";
        String answer = "0000";
        String authority = "0000";
        String additional = "0000";
        String encoded = "03646e7306676f6f676c6503636f6d00";
        String queryType = "0001";
        String queryClass = "0001";

        return id.hex() + flags.hex()
                + question + answer + authority + additional + encoded + queryType + queryClass;
    }
}
