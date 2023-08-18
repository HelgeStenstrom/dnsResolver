package se.helgestenstrom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpcodeTest {

    @Test
    void opcodeNumerals() {
        assertEquals(0, Opcode.QUERY.ordinal());
        assertEquals(1, Opcode.IQUERY.ordinal());
        assertEquals(2, Opcode.STATUS.ordinal());
    }

}