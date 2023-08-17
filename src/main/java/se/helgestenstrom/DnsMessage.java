package se.helgestenstrom;

public class DnsMessage {


    private final Id id;



    public DnsMessage(Id id) {
        this.id = id;
    }

    public String message() {
        String ids = id.hex();
        String flags = "0100";
        String question = "0001";
        String answer = "0000";
        String authority = "0000";
        String additional = "0000";
        String encoded = "03646e7306676f6f676c6503636f6d00";
        String queryType = "0001";
        String queryClass = "0001";
        return ids + flags + question + answer + authority + additional + encoded + queryType + queryClass;
    }
}
