package se.helgestenstrom;

/**
 * The result of a parse (the payload), and also the next index to parse from.
 *
 * @param <T> Type of the payload
 */
public class ParseResult<T> {
    private final int nextIndex;
    private final T result;


    /**
     * @param result    The payload
     * @param nextIndex where to continue parsing from, after this result
     */
    public ParseResult(T result, int nextIndex) {
        this.result = result;
        this.nextIndex = nextIndex;
    }

    public T getResult() {
        return result;
    }

    public int getNextIndex() {
        return nextIndex;
    }
}
