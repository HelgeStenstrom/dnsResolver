package se.helgestenstrom;

/**
 * Hold RFC 1035 4.1.1 Header section, with 12 bytes of information
 */
public class Header implements Hex{

    private final Id id;

    private final Flags flags;
    private final int qdCount;
    private final int anCount;
    private final int nsCount;
    private final int arCount;

    /**
     * @param id A 16-bit identifier assigned by the program that generates any kind of query.
     * @param flags The second row of the RFC 1035 4.1.1  table
     */
    public Header(Id id, Flags flags) {
        this.id = id;
        this.flags = flags;
        qdCount = 1;
        anCount = 20;
        nsCount = 0;
        arCount = 0;
    }

    private Header(String hex) {
        this.id = new Id(parse16bits(hex, 0));
        this.flags = new Flags(hex.substring(4, 8));
        qdCount = parse16bits(hex, 8);
        anCount = parse16bits(hex, 12);
        nsCount = parse16bits(hex, 16);
        arCount = parse16bits(hex, 20);

    }

    private int parse16bits(String hex, int startIndex) {
        String idPart = hex.substring(startIndex, startIndex+4);
        return Integer.parseInt(idPart, 16);
    }

    /**
     * @param hex Hexadecimal representation of the bits of the header.
     * @return an instance
     */
    public static Header fromHex(String hex) {
        return new Header(hex);
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

    public int getQdCount() {
        return qdCount;
    }

    public int getAnCount() {
        return anCount;
    }

    public int getNsCount() {
        return nsCount;
    }

    public int getArCount() {
        return arCount;
    }
}
