package se.helgestenstrom;

public class Id implements Hex {

    private final int number;

    public Id(int number) {
        this.number = number;
    }

    @Override
    public String hex() {
        return String.format("%04x", number);
    }
}
