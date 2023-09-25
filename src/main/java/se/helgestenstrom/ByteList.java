package se.helgestenstrom;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Holds lists of bytes, can be created from a hex string
 */
public class ByteList extends ArrayList<Integer> {


    /**
     * @param bytes Prototype list
     */
    public ByteList(List<Integer> bytes) {
        super(bytes);
    }

    /**
     * Creates an empty list
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

    /**
     * Creates a 2-cell list from a 16-bit number. Excessive bits are ignored.
     *
     * @param value to be converted to 2-byte list
     * @return the list
     */
    public static ByteList u16FromInt(int value) {
        if (value < 0 || value > 0xffff) {
            throw new IllegalArgumentException("%d is outside permitted range 0 to 0xffff.".formatted(value));
        }
        var msb = (value & 0xff00) >> 8;
        var lsb = value & 0xff;
        return new ByteList(List.of(msb, lsb));
    }

    /**
     * Creates a 4-cell list from a 32-bit number. Excessive bits are ignored.
     *
     * @param value to be converted to 2-byte list
     * @return the list
     */
    public static ByteList u32FromInt(long value) {
        if (value < 0 || value > 0xffffffffL) {
            throw new IllegalArgumentException("%d is outside permitted range 0 to 0xffffffff.".formatted(value));
        }

        int b3 = (int) ((value & 0xff000000L) >> 24);
        int b2 = (int) ((value & 0xff0000) >> 16);
        int b1 = (int) ((value & 0xff00) >> 8);
        int b0 = (int) (value & 0xff);
        return new ByteList(List.of(b3, b2, b1, b0));
    }


    ByteList append(ByteList... lists) {
        ByteList result = new ByteList(this);
        for (var list : lists) {
            result.addAll(list);
        }
        return result;
    }

    /**
     * @return the list as a string of hexadecimal digits. Each pair of digits is one item in the list.
     */
    public String hex() {
        return this.stream()
                .map(intValue -> String.format("%02x", intValue))
                .collect(Collectors.joining());
    }

    /**
     * Treat the first four ints of the list as bytes of a 32-bit word. Concatenate to a 32-bit word.
     *
     * @param index index of the most significant byte of the returned value.
     * @return an unsigned integer U32, as long
     */
    public long u32(int index) {

        return (this.get(index) << 24)
                | (this.get(index + 1) << 16)
                | (this.get(index + 2) << 8)
                | this.get(index + 3);
    }

    /**
     * @param offset The index of the list where the pointer indicator is supposed to be.
     * @return the pointer value, if any
     */
    public Optional<Integer> pointerValue(int offset) {

        if (!(isPointer(offset))) {
            return Optional.empty();
        }

        Integer value = ((this.get(offset) & 0x3f) << 8) | (this.get(offset + 1) & 0xff);
        return Optional.of(value);
    }

    boolean isPointer(int offset) {
        var statusWord = subList(offset, offset + 2).u16(0);
        return (statusWord & 0xc000) == 0xc000;
    }

    /**
     * Treat the first two ints of the list as MSB and LSB. Concatenate to a 16-bit word.
     *
     * @param index index of the most significant byte of the returned value.
     * @return an unsigned integer U16
     */
    public int u16(int index) {
        return (this.get(index) << 8) | this.get(index + 1);
    }

    @Override
    public ByteList subList(int fromIndex, int toIndex) {
        List<Integer> subList = super.subList(fromIndex, toIndex);
        ByteList result = new ByteList();

        result.addAll(subList);

        return result;
    }

    /**
     * @return the list as a byte array
     */
    public byte[] asArray() {

        byte[] xx = new byte[this.size()];

        for (var i = 0; i < this.size(); i++) {
            byte tmp = (byte) (0xff & this.get(i));
            xx[i] = tmp;
        }

        return xx;
    }
}
