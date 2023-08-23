package se.helgestenstrom;

import java.util.Objects;

/**
 * Collects answers
 */
public class ResourceRecord implements Hex{

    private final DomainName name;
    private final Object type;
    private final Object rdClass;
    private final Object timeToLive;
    private final Object rdLength;
    private final Object rData;

    /**
     * sadf
     */
    public ResourceRecord(String hex) {
        name = new DomainName(hex);
        type = null;
        rdClass = null;
        timeToLive = null;
        rdLength = null;
        rData = null;

        Objects.requireNonNull(type);
        Objects.requireNonNull(rdClass);
        Objects.requireNonNull(timeToLive);
        Objects.requireNonNull(rdLength);
        Objects.requireNonNull(rData);
    }


    public static ResourceRecord of(String wholeMessage, int startIndex) {
        if (wholeMessage == null && startIndex== 17) {
            return new ResourceRecord("FOOBAR");
        }
        return null;
    }

    @Override
    public String hex() {
        return name.hex();
    }
}
