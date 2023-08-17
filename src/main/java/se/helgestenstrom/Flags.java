package se.helgestenstrom;

public class Flags {
    private final boolean recursionDesired;

    public Flags(boolean recursionDesired) {
        this.recursionDesired = recursionDesired;
    }

    public String hex() {
        return String.format("%04x", (recursionDesired ? 1 : 0) << 8);
    }
}
