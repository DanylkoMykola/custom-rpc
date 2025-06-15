package io;

public class StreamEnvelope<T> {
    private boolean end;
    @BinaryField(order = 1)
    private T payload;

    public StreamEnvelope(boolean end, T payload) {
        this.end = end;
        this.payload = payload;
    }

    public StreamEnvelope() {
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
