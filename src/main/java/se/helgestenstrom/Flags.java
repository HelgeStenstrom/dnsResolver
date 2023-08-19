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
     * @param hex The hexadecimal representation of the flags
     */
    public Flags(String hex) {
        bitField = Integer.parseInt(hex, 16);
    }

    /**
     * @return the flag field as an integer, in hexadecimal representation.
     */
    public String hex() {

        return String.format("%04x", bitField);
    }

    private int makeBitField(boolean isResponse, boolean isAuthoritative, boolean recursionDesired) {
        return (isResponse ? 1 : 0) << 15
                | (isAuthoritative ? 1 : 0) << 10
                | (recursionDesired ? 1 : 0) << 8;
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
