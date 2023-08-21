package se.helgestenstrom;

/**
 * Holds one Question section, described in RFC 1035 4.1.2
 */
public class Question implements Hex {

    private final DomainName domainName;
    private final TwoBytes qClass;
    private final TwoBytes qType;

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
        domainName = new DomainName(qName);
        this.qType = TwoBytes.of(qType);
        this.qClass = TwoBytes.of(qClass);
    }

    /**
     * @param hexQuestionAndFollowingData Hexadecimal representation of a Question, + following data to be ignored.
     */
    private Question(String hexQuestionAndFollowingData) {
        domainName = DomainName.ofHex(hexQuestionAndFollowingData);
        this.qClass = null;
        this.qType = null;
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
        return domainName.hex() + qType.hex() + qClass.hex();
    }

    public DomainName getName() {
        return domainName;
    }
}
