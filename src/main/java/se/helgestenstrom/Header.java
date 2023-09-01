package se.helgestenstrom;


/**
 * Hold RFC 1035 4.1.1 Header section, with 12 bytes of information
 */
public class Header {

    private final Id id;

    private final Flags flags;
    private final int qdCount;
    private final int anCount;
    private final int nsCount;
    private final int arCount;

    /**
     * @param id      A 16-bit identifier assigned by the program that generates any kind of query.
     * @param flags   The second row of the RFC 1035 4.1.1  table
     * @param qdCount the number of QD values
     * @param anCount the number of AN values
     * @param nsCount the number of NS values
     * @param arCount the number of AR values
     */
    public Header(Id id, Flags flags, int qdCount, int anCount, int nsCount, int arCount) {
        this.id = id;
        this.flags = flags;
        this.qdCount = qdCount;
        this.anCount = anCount;
        this.nsCount = nsCount;
        this.arCount = arCount;
    }

    private Header(String hex) {
        this(
                new Id(parse16bits(hex, 0)),
                new Flags(hex.substring(4, 8)),
                parse16bits(hex, 8),
                parse16bits(hex, 12),
                parse16bits(hex, 16),
                parse16bits(hex, 20));

    }

    private static int parse16bits(String hex, int startIndex) {
        String idPart = hex.substring(startIndex, startIndex + 4);
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


    /**
     * @return Hex representation of the header, so that it can be included in a hex of the whole message.
     * @deprecated Use ByteList representation instead
     */
    @Deprecated(forRemoval = true)
    public String hex() {
        String hex =  ByteList.fromInt(qdCount)
                .append(ByteList.fromInt(anCount))
                .append(ByteList.fromInt(nsCount))
                .append(ByteList.fromInt(arCount))
                .hex();
        return id.hex() + flags.hex() + hex;
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
