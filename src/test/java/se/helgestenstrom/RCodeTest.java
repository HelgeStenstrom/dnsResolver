package se.helgestenstrom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RCodeTest {


    @Test
    void ordinals() {

        assertEquals(0, RCode.NoError.ordinal());
        assertEquals(1, RCode.FormatError.ordinal());
        assertEquals(2, RCode.ServerFailure.ordinal());
        assertEquals(3, RCode.NameError.ordinal());
        assertEquals(4, RCode.NotImplemented.ordinal());
        assertEquals(5, RCode.Refused.ordinal());
        assertEquals(6, RCode.Reserved1.ordinal());
        assertEquals(15, RCode.Reserved10.ordinal());
    }

}