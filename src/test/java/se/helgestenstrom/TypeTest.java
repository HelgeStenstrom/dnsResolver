package se.helgestenstrom;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TypeTest {

    @Test
    void fromInt() {

        assertEquals(Type.MD, Type.code(3));

    }

    @Test
    void description() {

        assertEquals("host information", Type.code(13).getDescription());

    }


}