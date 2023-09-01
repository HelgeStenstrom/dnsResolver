package se.helgestenstrom;

/**
 * Collects answers
 */
public class ResourceRecord {

    private final DomainName name;
    private final int type;

    /**
     * sadf
     */
    private ResourceRecord(String hex) {
        name = new DomainName(hex);
        this.type = -1; // Faulty, will be fixed later
    }


    /**
     * Construct an instance from properties
     * @param domainName The name, including separator periods (if any)
     * @param type a 2-octet list
     */
    public ResourceRecord(DomainName domainName, int type) {
        this.name = domainName;
        this.type = type;
    }


    /**
     * Construct an instace from data
     * @param wholeMessage A list of bytes of a DnsMessage
     * @param startIndex The index the list, where the ResourceRecord begins
     * @return an instance
     */
    public static ResourceRecord of(String wholeMessage, int startIndex) {
        if (wholeMessage == null && startIndex== 17) {
            return new ResourceRecord("FOOBAR");
        }
        return null;
    }


    private String hex() {
        return name.asList().hex();
    }

    /**
     * @return the record as a list
     */
    public ByteList asList() {
        return ByteList.of(hex());
    }

    public String getName() {
        return this.name.getName();
    }

    public int getType() {
        return this.type;
    }
}
