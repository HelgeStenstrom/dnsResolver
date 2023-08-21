package se.helgestenstrom;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ByteListTest {

    @Test
    void equality() {

        List<Integer> bytes = List.of( 65, 66, 67);
        ByteList bl1 =  new ByteList(bytes);
        ByteList bl2 =  new ByteList(bytes);
        assertEquals(bl1, bl2);
    }
    @Test
    void fromListOfBytes() {

        List<Integer> bytes = List.of( 65, 66, 67);
        ByteList bl1 =  new ByteList(bytes);

        assertEquals( 65, bl1.get(0));
        assertEquals( 66, bl1.get(1));
        assertEquals( 67, bl1.get(2));
    }

    @Test
    void fromHex() {
        ByteList bl1 = ByteList.of("414243");

        assertEquals( 65, bl1.get(0));
        assertEquals( 66, bl1.get(1));
        assertEquals( 67, bl1.get(2));
    }

    @Test
    void wrongLength() {
        assertThrows(IllegalArgumentException.class, () -> ByteList.of("123"));
    }

    @Test
    void fromExampleString() {

        String exampleMessage = "00168080000100020000000003646e7306676f6f676c6503636f6d0000010001c00c0001000100000214000408080808c00c0001000100000214000408080404";

        ByteList byteList = ByteList.of(exampleMessage);

        assertEquals(128, exampleMessage.length());
        assertEquals(64, byteList.size());

    }

}