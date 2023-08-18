package se.helgestenstrom;

/**
 * Collects answers
 */
public class ResourceRecord implements Hex{

    private final DomainName name;

    /**
     * sadf
     */
    public ResourceRecord() {
        name = new DomainName("foobar");
    }

    @Override
    public String hex() {
        return name.hex();
    }
}
