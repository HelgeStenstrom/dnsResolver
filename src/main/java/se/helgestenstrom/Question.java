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
