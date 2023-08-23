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

//    @Test
//    void canReplaceTwoBytes() {
//        // Prototype test to mimic
//        assertEquals("10ab", TwoBytes.of("10ab").hex());
//        assertEquals("00ab", TwoBytes.of("00ab").hex());
//
//        ByteList byteList1 = ByteList.of("10ab");
//        TwoBytes twoBytes1 = TwoBytes.of("10ab");
//        int anInt = twoBytes1.asInt();
//        int i = (byteList1.get(0) << 8) | byteList1.get(1);
//        assertEquals(anInt, i);
//        assertEquals(anInt, byteList1.u16());
//    }

    @Test
    void u16() {
        ByteList bl = new ByteList(List.of(0x23, 0xa7));
        assertEquals(0x23a7, bl.u16());
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

    @Test
    void listToHex() {
        String original = "010305be";
        ByteList integers = ByteList.of(original);
        String received = integers.hex();

        assertEquals(original, received);
    }

}