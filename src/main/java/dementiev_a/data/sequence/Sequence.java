package dementiev_a.data.sequence;

public abstract class Sequence {
    private long value = 1L;

    public long next() {
        return value++;
    }
}
