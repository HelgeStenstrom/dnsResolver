package se.helgestenstrom;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ResourceRecordTest {

    /**
     * See <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-4.1.4"> Message compression</a>
     */
    @Test
    @Disabled("Must learn about name compression before this is implemented.")
    void foo() {

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
        assertNotNull(readableMessage);  // To let me keep the variable for debugging. TODO: remove

        var pointerBytes = wholeMessage.subList(32, 34);
        int offsetWord = (pointerBytes.get(0) << 8) | pointerBytes.get(1);
        int mask = 0x3fff;
        int offset = offsetWord & mask;
        assertNotEquals(-1, offset);  // To let me keep the variable for debugging. TODO: remove

        //var integers1 = wholeMessage.subList(offset, wholeMessage.size());
        //String string = getString(integers1);
        DomainName domainName1 = DomainName.of(wholeMessage, 12);
        DomainName domainName2 = DomainName.of(wholeMessage, 32);
        DomainName domainName3 = DomainName.of(wholeMessage, 48);
        assertNotNull(domainName1);  // To let me keep the variable for debugging. TODO: remove
        assertNotNull(domainName2);  // To let me keep the variable for debugging. TODO: remove
        assertNotNull(domainName3);  // To let me keep the variable for debugging. TODO: remove

        fail("test not done");
        assertEquals(2, 1 + 3, "Test not done");
    }


    @Test
    void name() {

        // Setup
        String thename = "thename";
        DomainName domainName = new DomainName(thename);
        int type = 0x1001;

        // Execute
        ResourceRecord r = new ResourceRecord(domainName, type);

        // Verify
        assertEquals(thename, r.getName());

    }

    @Test
    void type() {

        // Setup
        DomainName domainName = new DomainName("ignored");
        int type = 0x1001;

        // Execute
        ResourceRecord r = new ResourceRecord(domainName, type);

        // Verify
        assertEquals(type, r.getType());

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