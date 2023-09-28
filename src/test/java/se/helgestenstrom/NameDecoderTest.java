package se.helgestenstrom;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NameDecoderTest {

    @Test
    void emptyName() {

        // Setup
        List<Integer> bytes = List.of(0, 9, 9, 9);
        ByteList encoded = new ByteList(bytes);
        NameDecoder nameDecoder = new NameDecoder(encoded);

        // Exercise
        ParseResult<Name> nameParseResult = nameDecoder.nameAndNext(0);
        String name = nameParseResult.getResult().toString();

        // Verify
        assertEquals("", name);
        assertEquals(1, nameParseResult.getNextIndex());
    }
    @Test
    void simpleName() {

        List<Integer> bytes = List.of(1,(int) 'a', 0);
        ByteList encoded = new ByteList(bytes);
        NameDecoder nameDecoder = new NameDecoder(encoded);

        ParseResult<Name> nameParseResult = nameDecoder.nameAndNext(0);
        String name = nameParseResult.getResult().toString();
        assertEquals("a", name);
        assertEquals(3, nameParseResult.getNextIndex());
    }

    @Test
    void linkedName() {

        List<Integer> bytes = List.of(1, (int) 'a', 0, 0xc0, 0);
        ByteList encoded = new ByteList(bytes);
        NameDecoder nameDecoder = new NameDecoder(encoded);

        ParseResult<Name> nameParseResult = nameDecoder.nameAndNext(3);
        String name = nameParseResult.getResult().toString();
        assertEquals("a", name);
    }

    @Test
    void twoStepLinkedName() {

        List<Integer> bytes = List.of(1,(int) 'b', 0, // regular name, starts at index 0
                0xc0, 0, // Points to first name, starts at index 3
                0xc0, 3 // points to second name, starts at index 5
        );
        ByteList encoded = new ByteList(bytes);
        NameDecoder nameDecoder = new NameDecoder(encoded);

        ParseResult<Name> nameParseResult = nameDecoder.nameAndNext(5);
        String name = nameParseResult.getResult().toString();
        assertEquals("b", name);
    }

    /**
     * Testing two names; one is a.c, the other is w.a.c. We could use abc.com and www.abc.com,
     * but shorter names are enough and make the test setup simpler.
     * We define a.c first, and then let w.a.c link to it, so that it's defined by w,
     * followed by a link to a.c.
     */
    @Test
    void linkingProlongsAName() {
        // Setup
        List<Integer> ignoredPrefix = List.of(7, 7, 7, 5, 6, 7);
        List<Integer> acName = List.of(1, (int) 'a', 1, (int) 'c', 0);
        int startIndexOfFirstName = ignoredPrefix.size();
        List<Integer> wacName = List.of(1, (int) 'w', 0xc0, startIndexOfFirstName);

        List<Integer> encodedNames = Stream.of(ignoredPrefix, acName, wacName)
                .flatMap(Collection::stream).toList();
        ByteList encoded = new ByteList(encodedNames);

        NameDecoder nameDecoder = new NameDecoder(encoded);

        // Exercise 1
        var acDecoded = nameDecoder.nameAndNext(startIndexOfFirstName);

        // Verify
        assertEquals("a.c", acDecoded.getResult().toString());

        // Exercise 2
        int nextIndex = acDecoded.getNextIndex();
        var wacDecoded = nameDecoder.nameAndNext(nextIndex);

        // Verify
        assertEquals("w.a.c", wacDecoded.getResult().toString());


    }

}