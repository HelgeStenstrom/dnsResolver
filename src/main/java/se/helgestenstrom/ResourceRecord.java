package se.helgestenstrom;

/**
 * Collects answers
 */
public class ResourceRecord implements Hex{

    private final DomainName name;
    private final TwoBytes type;
    private final TwoBytes rdClass;
    private final TwoBytes timeToLive;
    private final TwoBytes rdLength;
    private final TwoBytes rData;

    /**
     * sadf
     */
    public ResourceRecord(String hex) {
        name = new DomainName("foobar");
        type = null;
        rdClass = null;
        timeToLive = null;
        rdLength = null;
        rData = null;
    }


    public static ResourceRecord of(String wholeMessage, int startIndex) {
        return null;
    }

    @Override
    public String hex() {
        return name.hex();
    }
}
