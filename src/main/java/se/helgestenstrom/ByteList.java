package se.helgestenstrom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Holds lists of bytes, can be created from a hex string
 */
public class ByteList extends ArrayList<Integer> implements Hex {


    /**
     * @param bytes Prototype list
     */
    public ByteList(List<Integer> bytes) {
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
     *
     * @param hexString two hex character per byte of the list
     * @return a list instance
     */
    public static ByteList of(String hexString) {

        ByteList byteList = new ByteList();

        // Make sure the input string has an even length
        if (hexString.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string length must be even");
        }

        for (int i = 0; i < hexString.length(); i += 2) {
            String hexPair = hexString.substring(i, i + 2);
            int byteValue = Integer.parseInt(hexPair, 16);
            byteList.add(byteValue);
        }

        return byteList;

    }

    @Override
    public String hex() {
        return this.stream()
                .map(intValue -> String.format("%02x", intValue))
                .collect(Collectors.joining());
    }

    @Override
    public ByteList subList(int fromIndex, int toIndex) {
        List<Integer> subList =  super.subList(fromIndex, toIndex);
        ByteList result = new ByteList();

        result.addAll(subList);

        return result;
    }

    /**
     * Treat the first two ints of the list as MSB and LSB. Concatenate to a 16-bit word.
     * @return an unsigned integer U16
     */
    public int u16() {
        return (this.get(0) << 8) | this.get(1);
    }
}
