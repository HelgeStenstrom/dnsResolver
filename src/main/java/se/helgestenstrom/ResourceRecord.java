package se.helgestenstrom;

/**
 * Holds a record according to RFC 1035, section 4.1.3.
 */
public class ResourceRecord {
    private final Name name;
    private final ByteList rData;
    private final int rDataClass;
    private final long timeToLive;
    private final int type;


    /**
     * @param name       typically a domain name.
     * @param type       u16 of the type according to RFC 1035, section 4.1.3
     * @param rDataClass u16 of the class according to RFC 1035, section 4.1.3
     * @param timeToLive u16 of the TTL, Time to Live, according to RFC 1035, section 4.1.3
     * @param rData      the RDATA according to RFC 1035, section 4.1.3
     */
    public ResourceRecord(Name name, int type, int rDataClass, long timeToLive, ByteList rData) {
        this.name = name;
        this.type = type;
        this.rDataClass = rDataClass;
        this.timeToLive = timeToLive;
        this.rData = rData;
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

    public long getTimeToLive() {
        return timeToLive;
    }

    public int getRdLength() {
        return rData.size();
    }

    public ByteList getRData() {
        return rData;
    }
}
