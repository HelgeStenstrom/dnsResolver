package se.helgestenstrom;

/**
 * Holds one Question section, described in RFC 1035 4.1.2
 */
public class Question implements Hex {

    private final String qName;

    /**
     * @param qName usually a domain and host string, with dots.
     *              <p>
     *              A domain name represented as a sequence of labels, where
     *              each label consists of a length octet followed by that
     *              number of octets.  The domain name terminates with the
     *              zero length octet for the null label of the root.  Note
     *              that this field may be an odd number of octets; no
     *              padding is used.</p>
     */
    public Question(String qName) {
        this.qName = qName;
    }

    @Override
    public String hex() {
        String queryType = "0001";
        String queryClass = "0001";
        return new DomainName(qName).hex() + queryType + queryClass;
    }
}
