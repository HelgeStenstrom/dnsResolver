package se.helgestenstrom;

/**
 * Hold RFC 1035 4.1.1 Header section, with 12 bytes of information
 */
public class Header implements Hex{

    private final Id id;

    private final Flags flags;

    /**
     * @param id A 16-bit identifier assigned by the program that generates any kind of query.
     * @param flags The second row of the RFC 1035 4.1.1  table
     */
    public Header(Id id, Flags flags) {
        this.id = id;
        this.flags = flags;
    }

    /**
     * @return The ID of the message
     */
    public Id getId() {
        return id;
    }

    public Flags getFlags() {
        return flags;
    }

    @Override
    public String hex() {
        return id.hex() + flags.hex() + new Counts().hex();
    }
}
