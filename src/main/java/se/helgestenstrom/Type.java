package se.helgestenstrom;

import java.util.Arrays;

/**
 * Types according to <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-3.2.2">RFC 1030, section 3.2.2</a> and 3.2.3.
 */
public enum Type {


    A(1, "a host address"),
    NS(2, "an authoritative name server"),
    MD(3, "a mail destination (Obsolete - use MX)"),
    MF(4, "a mail forwarder (Obsolete - use MX)"),
    CNAME(5, "the canonical name for an alias"),
    SOA(6, "marks the start of a zone of authority"),
    MB(7, "a mailbox domain name (EXPERIMENTAL)"),
    MG(8, "a mail group member (EXPERIMENTAL)"),
    MR(9, "a mail rename domain name (EXPERIMENTAL)"),
    NULL(10, "a null RR (EXPERIMENTAL)"),
    WKS(11, "a well known service description"),
    PTR(12, "a domain name pointer"),
    HINFO(13, "host information"),
    MINFO(14, "mailbox or mail list information"),
    MX(15, "mail exchange"),
    TXT(16, "text strings"),
    AXFR(252, "A request for a transfer of an entire zone"),

    MAILB(253, "A request for mailbox-related records (MB, MG or MR)"),

    MAILA(254, "A request for mail agent RRs (Obsolete - see MX)"),

    ALLRECORDS(255, "A request for all records");


    private final String description;
    private final int ord;

    Type(int ord, String description) {
        this.ord = ord;
        this.description = description;
    }

    /**
     * @param ord The type number according to rFC 1035, section 3.2.2 and 3.2.3
     * @return an enum instance
     */
    public static Type code(int ord) {
        Type[] values = Type.values();
        return Arrays.stream(values).filter(t -> t.ord == ord).findFirst().orElseThrow();
    }

    public String getDescription() {
        return description;
    }
}
