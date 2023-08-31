package se.helgestenstrom;

import java.util.List;

/**
 * Holds one Question section, described in RFC 1035 4.1.2
 */
public class Question implements Hex {

    private final DomainName domainName;

    private final List<String> labels;

    private final ByteList type;
    private final ByteList clazz;

    /**
     * @param qName  usually a domain and host string, with dots.
     *               <p>
     *               A domain name represented as a sequence of labels, where
     *               each label consists of a length octet followed by that
     *               number of octets.  The domain name terminates with the
     *               zero length octet for the null label of the root.  Note
     *               that this field may be an odd number of octets; no
     *               padding is used.</p>
     * @param qType  The QTYPE of the Question
     * @param qClass The QCLASS of the Question
     * @param labels A list of short strings, each called a label.
     *               These normally make up the period-separated domain name.
     */
    public Question(String qName, String qType, String qClass, List<String> labels) {
        domainName = new DomainName(qName);
        this.labels = labels;

        type = ByteList.of(qType);

        clazz = ByteList.of(qClass);
    }

    /**
     * @param hexQuestionAndFollowingData Hexadecimal representation of a Question, + following data to be ignored.
     */
    private Question(String hexQuestionAndFollowingData) {
        ByteList byteList = ByteList.of(hexQuestionAndFollowingData);
        domainName = DomainName.of(byteList, 0);
        labels = List.of("foobar");
        int fromIndex = domainName.consumes();
        type = byteList.subList(fromIndex, fromIndex + 2);

        clazz = byteList.subList(fromIndex + 2, fromIndex + 4);
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
        return domainName.hex() + type.hex() + clazz.hex();
    }

    public DomainName getName() {
        return domainName;
    }


    public ByteList getQClass() {
        return clazz;
    }

    public ByteList getQType() {
        return type;
    }

    public List<String> getLabels() {
        return labels;
    }
}
