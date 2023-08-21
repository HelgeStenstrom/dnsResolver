package se.helgestenstrom;

/**
 * Holds one Question section, described in RFC 1035 4.1.2
 */
public class Question implements Hex {

    private final String qNameHex;
    private final String qType;
    private final String qClass;
    private final DomainName domainName;

    /**
     * @param qName usually a domain and host string, with dots.
     *                 <p>
     *                 A domain name represented as a sequence of labels, where
     *                 each label consists of a length octet followed by that
     *                 number of octets.  The domain name terminates with the
     *                 zero length octet for the null label of the root.  Note
     *                 that this field may be an odd number of octets; no
     *                 padding is used.</p>
     * @param qType    The QTYPE of the Question
     * @param qClass   The QCLASS of the Question
     */
    public Question(String qName, String qType, String qClass) {
        this.qNameHex = qName;
        this.qType = qType;
        this.qClass = qClass;
        domainName = new DomainName(qName);
    }

    /**
     * @param hexQuestionAndFollowingData Hexadecimal representation of a Question, + following data to be ignored.
     */
    private Question(String hexQuestionAndFollowingData) {
        domainName = DomainName.ofHex(hexQuestionAndFollowingData);
        this.qNameHex = domainName.hex();
        this.qType = "abcd";
        this.qClass = "abcd";
    }

    /**
     * @param hexQuestionAndFollowingData Hexadecimal representation of a Question, + following data to be ignored.
     * @return an instance
     */
    public static Question of(String hexQuestionAndFollowingData) {
        return new Question(hexQuestionAndFollowingData);
    }

    @Override
    public String hex() {
        return new DomainName(qNameHex).hex() + qType + qClass;
    }

    public DomainName getName() {
        return new DomainName("dummy");
    }
}
