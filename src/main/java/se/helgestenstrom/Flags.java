package se.helgestenstrom;


/**
 * Implements the flags of the second row of the table
 * in <a href="https://datatracker.ietf.org/doc/html/rfc1035#section-4.1.1"> RFC 1035, section 4.1.1</a>
 * Incomplete.
 * There are 16 bits.
 */
public class Flags {

    private final int bitField;

    /**
     * Constructor that supports only a single flag of all available.
     * @param recursionDesired True if recursion is desired.
     */
    public Flags(boolean recursionDesired) {

        bitField = makeBitField(false, false, recursionDesired);
    }

    private Flags(Flags.Builder builder) {

        bitField = makeBitField(builder.isResponse, builder.isAuthoritative, builder.recursionDesired);
    }

    /**
     * @param bitField a 16-bit integer representing the flags according to the spec.
     */
    public Flags(int bitField) {
        this.bitField = bitField;
    }


    /**
     * @return the flag as a list of 2 bytes
     */
    public ByteList asList() {
        return ByteList.u16FromInt(bitField);
    }

    private int makeBitField(boolean isResponse, boolean isAuthoritative, boolean recursionDesired) {
        return (isResponse ? 1 : 0) << 15
                | (isAuthoritative ? 1 : 0) << 10
                | (recursionDesired ? 1 : 0) << 8;
    }

    public boolean isQuery() {
        return !isResponse();
    }

    public boolean isResponse() {
        return (bitField & 0x8000) !=0;
    }

    public int getOpcode() {
        return ((bitField >> 11) & 0xf);
    }

    public boolean isAuthoritative() {
        return (bitField >> 10 & 1) !=0;
    }

    public boolean isTruncated() {
        return (bitField >> 9 & 1) !=0;
    }

    public boolean isRecursionDesired() {
        return  (bitField >> 8 & 1) !=0;
    }

    public boolean isRecursionAvailable() {
        return (bitField >> 7 & 1) !=0;
    }

    public RCode getRCode() {
        return RCode.values()[bitField & 7];
    }

    static class Builder {
        private boolean recursionDesired = true;
        private boolean isResponse = false;
        private boolean isAuthoritative = false;

        public Flags build() {
            return new Flags(this);
        }

        public Builder setRecursionDesired(boolean isDesired) {
            this.recursionDesired = isDesired;
            return this;
        }

        public Builder setResponse(boolean isResponse) {
            this.isResponse = isResponse;
            return this;
        }

        public Builder setIsAuthoritative(boolean isAuthoritative) {
            this.isAuthoritative = isAuthoritative;
            return this;
        }
    }

}
