package se.helgestenstrom;

/**
 * Holds one Question section, described in RFC 1035 4.1.2
 */
public class Question implements Hex {

    private final String qName;
    private final String qType;
    private final String qClass;

    /**
     * @param qName usually a domain and host string, with dots.
     *              <p>
     *              A domain name represented as a sequence of labels, where
     *              each label consists of a length octet followed by that
     *              number of octets.  The domain name terminates with the
     *              zero length octet for the null label of the root.  Note
     *              that this field may be an odd number of octets; no
     *              padding is used.</p>
     * @param qType The QTYPE of the Question
     * @param qClass The QCLASS of the Question
     */
    public Question(String qName, String qType, String qClass) {
        this.qName = qName;
        this.qType = qType;
        this.qClass = qClass;
    }

    @Override
    public String hex() {
        return new DomainName(qName).hex() + qType + qClass;
    }
}
