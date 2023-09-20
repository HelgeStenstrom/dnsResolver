package se.helgestenstrom;

import java.util.List;

/**
 * A DNS message, as described by RFC 1035, section 4, and as required by this exercise. (See the README.md file)
 */
public class DnsMessage {


    private final List<ResourceRecord> additionalResources;
    private final List<ResourceRecord> answers;
    private final Header header;
    private final List<ResourceRecord> nameServerResources;

    private final List<Question> questions;
    /**
     * @param header The Header of RFC 1035, section 4.1
     * @param questions zero or more Questions of RFC 1035, section 4.1
     * @param answers List of answers according to RFC 1035, section 4.1.1
     * @param nameServerResources List of name server resources according to RFC 1035, section 4.1.1
     * @param additionalResources List of additional resources according to RFC 1035, section 4.1.1
     */
    public DnsMessage(Header header, List<Question> questions, List<ResourceRecord> answers, List<ResourceRecord> nameServerResources, List<ResourceRecord> additionalResources) {
        this.header = header;
        this.questions = questions;
        this.answers = answers;
        this.nameServerResources = nameServerResources;
        this.additionalResources = additionalResources;
    }


    /**
     * @return a list representing the message. This list can also be parsed into a message.
     */
    public ByteList byteList() {

        ByteList allQuestions = new ByteList();
        questions.forEach(q -> allQuestions.addAll(q.asList()));
        return header.asList().append(allQuestions);
    }

    /**
     * @return byte array corresponding to the DnsMessage
     */
    public byte[] bytes() {

        return byteList().asArray();
    }

    public Header getHeader() {
        return header;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<ResourceRecord> getAnswers() {
        return answers;
    }

    public List<ResourceRecord> getNameServerResources() {
        return nameServerResources;
    }

    public List<ResourceRecord> getAdditionalRecords() {
        return additionalResources;
    }
}
