package se.helgestenstrom;

/**
 * Holds the 4 counts fields of an RFC 1035 4.1.1 Header section.
 */
public class Counts implements Hex {
    @Override
    public String hex() {
        // For now, hard-coded values. Will need to change.
        String question = "0001";
        String answer = "0000";
        String authority = "0000";
        String additional = "0000";
        return question + answer + authority + additional;

    }
}
