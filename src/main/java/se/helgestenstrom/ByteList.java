package se.helgestenstrom;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds lists of bytes, can be created from a hex string
 */
public class ByteList extends ArrayList<Byte> {


    /**
     * @param bytes Prototype list
     */
    public ByteList(List<Byte> bytes) {
        super(bytes);
    }

    /**
     * Creates an empty lit
     */
    public ByteList() {
        super();
    }

    /**
     * Create a list form a hex string
     * @param hexString two hex character per byte of the list
     * @return a list instance
     */
    public static ByteList of(String hexString) {

        ByteList byteList= new ByteList();

        // Make sure the input string has an even length
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string length must be even");
        }

        for (int i = 0; i < hexString.length(); i += 2) {
            String hexPair = hexString.substring(i, i + 2);
            byte byteValue = (byte) Integer.parseInt(hexPair, 16);
            byteList.add(byteValue);
        }

        return byteList;

    }
}
