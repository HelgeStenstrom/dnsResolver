package se.helgestenstrom;

/**
 * Holds a record according to RFC 1035, section 4.1.3.
 */
public class ResourceRecord {
    public int getType() {
        return -1;
    }

    public String getName() {
        return "change me";
    }

    public int getRDataClass() {
        return -1;
    }

    public int getTimeToLive() {
        return -1;
    }

    public int getRdLength() {
        return -1;
    }

    public ByteList getRData() {
        return new ByteList();
    }
}
