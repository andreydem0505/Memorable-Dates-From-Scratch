package dementiev_a.data.sequence;

import lombok.Setter;

public abstract class Sequence {
    @Setter
    private long value = 1L;

    public long next() {
        return value++;
    }
}
