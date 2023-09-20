package se.helgestenstrom;

/**
 * Holds a record according to RFC 1035, section 4.1.3.
 */
public class ResourceRecord {
    private final Name name;
    private final int rDataClass;
    private final int timeToLive;
    private final int type;


    /**
     * @param name typically a domain name.
     * @param type u16 of the type according to RFC 1035, section 4.1.3
     * @param rDataClass u16 of the class according to RFC 1035, section 4.1.3
     * @param timeToLive u16 of the TTL, Time to Live, according to RFC 1035, section 4.1.3
     */
    public ResourceRecord(Name name, int type, int rDataClass, int timeToLive) {
        this.name = name;
        this.type = type;
        this.rDataClass = rDataClass;
        this.timeToLive = timeToLive;
    }

    public int getType() {
        return type;
    }

    public String getNameString() {
        return name.toString();
    }

    public int getRDataClass() {
        return rDataClass;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public int getRdLength() {
        return -1;
    }

    public ByteList getRData() {
        return new ByteList();
    }
}
