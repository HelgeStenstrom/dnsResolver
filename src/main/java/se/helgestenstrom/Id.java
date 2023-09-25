package se.helgestenstrom;

/**
 * Represents the ID number of a message
 */
public class Id {

    private final int number;

    /**
     * Create an instance
     *
     * @param number ID number
     */
    public Id(int number) {
        this.number = number;
    }


    /**
     * @return the ID as a list of two bytes
     */
    public ByteList asList() {
        return ByteList.u16FromInt(number);
    }

    /**
     * @return the ID number
     */
    public int id() {
        return number;
    }
}
