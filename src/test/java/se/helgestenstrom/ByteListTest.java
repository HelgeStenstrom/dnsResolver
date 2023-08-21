package se.helgestenstrom;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ByteListTest {

    @Test
    void equality() {

        List<Byte> bytes = List.of((byte) 65,(byte) 66,(byte) 67);
        ByteList bl1 =  new ByteList(bytes);
        ByteList bl2 =  new ByteList(bytes);
        assertEquals(bl1, bl2);
    }
    @Test
    void fromListOfBytes() {

        List<Byte> bytes = List.of((byte) 65,(byte) 66,(byte) 67);
        ByteList bl1 =  new ByteList(bytes);

        assertEquals((byte) 65, bl1.get(0));
        assertEquals((byte) 66, bl1.get(1));
        assertEquals((byte) 67, bl1.get(2));
    }

    @Test
    void fromHex() {
        ByteList bl1 = ByteList.of("414243");

        assertEquals((byte) 65, bl1.get(0));
        assertEquals((byte) 66, bl1.get(1));
        assertEquals((byte) 67, bl1.get(2));
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