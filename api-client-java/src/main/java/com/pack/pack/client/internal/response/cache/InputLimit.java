package com.pack.pack.client.internal.response.cache;
public class InputLimit {

    private final long value;
    private boolean reached;

    /**
     * Create a limit for how many bytes of a response body to
     * read.
     * @param value maximum length in bytes
     */
    public InputLimit(long value) {
        super();
        this.value = value;
        this.reached = false;
    }

    /**
     * Returns the current maximum limit that was set on
     * creation.
     */
    public long getValue() {
        return this.value;
    }

    /**
     * Used to report that the limit has been reached.
     */
    public void reached() {
        this.reached = true;
    }

    /**
     * Returns {@code true} if the input limit has been reached.
     */
    public boolean isReached() {
        return this.reached;
    }

}