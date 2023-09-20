package se.helgestenstrom;

/**
 * Holds a record according to RFC 1035, section 4.1.3.
 */
public class ResourceRecord {
    private final Name name;
    private final int type;


    /**
     * @param name typically a domain name.
     * @param type u16 of the type according to RFC 1035, section 4.1.3
     */
    public ResourceRecord(Name name, int type) {
        this.name = name;
        this.type = type;

    }

    public int getType() {
        return type;
    }

    public String getNameString() {
        return name.toString();
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
