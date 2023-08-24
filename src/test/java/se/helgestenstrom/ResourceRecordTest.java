package se.helgestenstrom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class ResourceRecordTest {

    /**
     * See <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-4.1.4"> Message compression</a>
     */
    @Test
//    @Disabled("Must learn about name compression before this is implemented.")
    void foo() {

        String hexOfAnswers = "c00c0001000100000214000408080808c00c0001000100000214000408080404";
        String exampleMessageHex = "00168080000100020000000003646e7306676f6f676c6503636f6d0000010001c00c0001000100000214000408080808c00c0001000100000214000408080404";


        /*
         * c00c.0001000100000214000408080808c00c0001000100000214000408080404
         * c00c - 1100 0000 0000 1100 - offset 12 from start of exampleMessageHex
         * 0001 -
         * 0001 -
         * 0000 -
         * 02 - 2 characters follow
         * 1400 - offset a lot
         * */



        ByteList wholeMessage = ByteList.of(exampleMessageHex);

        String readableMessage = getString(wholeMessage);


        ResourceRecord resourceRecord = ResourceRecord.of(exampleMessageHex, 64);
        var pointerBytes = wholeMessage.subList(32, 34);
        int offsetWord = (pointerBytes.get(0) << 8) | pointerBytes.get(1);
        int mask = 0x3fff;
        int offset = offsetWord & mask;

        //var integers1 = wholeMessage.subList(offset, wholeMessage.size());
        //String string = getString(integers1);
        DomainName domainName1 = DomainName.of(wholeMessage, 12);
        DomainName domainName2 = DomainName.of(wholeMessage, 32);
        DomainName domainName3 = DomainName.of(wholeMessage, 48);

        fail("test not done");
        assertEquals(2, 1 + 3, "Test not done");
    }

    private String getString(List<Integer> integers) {

        return integers.stream()
                .map(c -> {
                    if (32 <= c && c <= 127) {
                        return c;
                    } else return (int) '.';
                })
                .map(i -> (char) i.intValue())
                .map(Object::toString)
                .collect(Collectors.joining());

    }

}