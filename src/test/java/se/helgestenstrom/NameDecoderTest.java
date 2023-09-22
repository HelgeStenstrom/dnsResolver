package se.helgestenstrom;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameDecoderTest {

    @Test
    void simpleName() {
        NameDecoder nameDecoder = new NameDecoder();

        List<Integer> bytes = List.of(1,(int) 'a', 0);

        ParseResult<Name> nameParseResult = nameDecoder.nameAndNext(new ByteList(bytes), 0);
        String name = nameParseResult.getResult().toString();
        assertEquals("a", name);
    }

    @Test
    void linkedName() {
        NameDecoder nameDecoder = new NameDecoder();

        List<Integer> bytes = List.of(1, (int) 'a', 0, 0xc0, 0);

        ParseResult<Name> nameParseResult = nameDecoder.nameAndNext(new ByteList(bytes), 3);
        String name = nameParseResult.getResult().toString();
        assertEquals("a", name);
    }

    @Test
    void twoStepLinkedName() {
        NameDecoder nameDecoder = new NameDecoder();

        List<Integer> bytes = List.of(1,(int) 'b', 0, // regular name, starts at index 0
                0xc0, 0, // Points to first name, starts at index 3
                0xc0, 3 // points to second name, starts at index 5
        );

        ParseResult<Name> nameParseResult = nameDecoder.nameAndNext(new ByteList(bytes), 5);
        String name = nameParseResult.getResult().toString();
        assertEquals("b", name);
    }

}