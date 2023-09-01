package se.helgestenstrom;

import java.util.List;

/**
 * Holds one Question section, described in RFC 1035 4.1.2
 */
public class Question {

    private final Name name;

    private final int classInt;

    private final int typeInt;


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
     */
    public Question(String qName, String qType, String qClass) {
        name = new Name(qName);
        typeInt = ByteList.of(qType).u16(0);
        classInt = ByteList.of(qClass).u16(0);


    }

    /**
     * @param qName usually a domain or host string with dots.
     * @param qType The type according to RFC 1035
     * @param qClass The class according to RFC 1035
     */
    public Question(Name qName, int qType, int qClass) {
        this.name = qName;
        this.typeInt = qType;
        this.classInt = qClass;
    }



    /**
     * @return the Question as a list
     */
    public ByteList asList() {
        ByteList domainAsList = name.asList();
        return domainAsList
                .append(ByteList.fromInt(typeInt))
                .append(ByteList.fromInt(classInt));
    }

    public List<String> getLabels() {
        return name.labels();
    }

    public int getType() {
        return typeInt;
    }

    public int getQClass() {
        return classInt;
    }

    public Name getName() {
        return name;
    }
}
