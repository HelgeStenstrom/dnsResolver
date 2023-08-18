package se.helgestenstrom;

public class Flags {
    private final boolean recursionDesired;
    private final boolean isResponse;
    private final boolean isAuthoritative;
    private final int field;

    public Flags(boolean recursionDesired) {
        this.recursionDesired = recursionDesired;
        isResponse = false;
        isAuthoritative = false;
        field = makeField();
    }

    private Flags(Flags.Builder builder) {
        recursionDesired = builder.recursionDesired;
        isResponse = builder.isResponse;
        isAuthoritative = builder.isAuthoritative;
        field = makeField();
    }

    public String hex() {

        return String.format("%04x", field);
    }

    private int makeField() {
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
