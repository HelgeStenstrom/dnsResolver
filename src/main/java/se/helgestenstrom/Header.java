package se.helgestenstrom;


/**
 * Hold RFC 1035 4.1.1 Header section, with 12 bytes of information
 */
public class Header {

    private final int anCount;
    private final int arCount;
    private final Flags flags;
    private final Id id;
    private final int nsCount;
    private final int qdCount;

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
     * @return the header as a list
     */
    public ByteList asList() {
        ByteList counts = ByteList.u16FromInt(qdCount)
                .append(ByteList.u16FromInt(anCount))
                .append(ByteList.u16FromInt(nsCount))
                .append(ByteList.u16FromInt(arCount));
        return id.asList()
                .append(flags.asList())
                .append(counts);

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
