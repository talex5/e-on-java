package antlr;

/**
 * Created by IntelliJ IDEA. User: Dean Tribble Date: Jan 29, 2005 Time:
 * 9:46:27 AM To change this template use Options | File Templates.
 */
public abstract class TokenBuffer {

    // Token source
    protected TokenStream input;
    // Number of active markers
    int nMarkers = 0;
    // Additional offset used when markers are active
    int markerOffset = 0;
    // Number of calls to consume() since last LA() or LT() call
    int numToConsume = 0;
    // Circular queue
    TokenQueue queue;

    public TokenBuffer() {
        queue = new TokenQueue(1);
    }

    /**
     * Reset the input buffer to empty state
     */
    public final void reset() {
        nMarkers = 0;
        markerOffset = 0;
        numToConsume = 0;
        queue.reset();
    }

    /**
     * Mark another token for deferred consumption
     */
    public final void consume() {
        numToConsume++;
    }

    /**
     * Ensure that the token buffer is sufficiently full
     */
    private final void fill(int amount) throws TokenStreamException {
        syncConsume();
        // Fill the buffer sufficiently to hold needed tokens
        while (queue.nbrEntries < amount + markerOffset) {
            // Append the next token
            queue.append(input.nextToken());
        }
    }

    /**
     * return the Tokenizer (needed by ParseView)
     */
    public TokenStream getInput() {
        return input;
    }

    /**
     * Get a lookahead token value
     */
    public final int LA(int i) throws TokenStreamException {
        fill(i);
        return queue.elementAt(markerOffset + i - 1).getType();
    }

    /**
     * Get a lookahead token
     */
    public final Token LT(int i) throws TokenStreamException {
        fill(i);
        return queue.elementAt(markerOffset + i - 1);
    }

    public int mark() {
        syncConsume();
//System.out.println("Marking at " + markerOffset);
//try { for (int i = 1; i <= 2; i++) { System.out.println("LA("+i+")=="+LT(i).getText()); } } catch (ScannerException e) {}
        nMarkers++;
        return markerOffset;
    }

    public void rewind(int mark) {
        syncConsume();
        markerOffset = mark;
        nMarkers--;
//System.out.println("Rewinding to " + mark);
//try { for (int i = 1; i <= 2; i++) { System.out.println("LA("+i+")=="+LT(i).getText()); } } catch (ScannerException e) {}
    }

    /**
     * Sync up deferred consumption
     */
    private final void syncConsume() {
        while (numToConsume > 0) {
            if (nMarkers > 0) {
                // guess mode -- leave leading tokens and bump offset.
                markerOffset++;
            } else {
                // normal mode -- remove first token
                queue.removeFirst();
            }
            numToConsume--;
        }
    }
}
