package dementiev_a.data.sequence;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventSequence extends Sequence {
    @Getter(lazy = true)
    private static final EventSequence instance = new EventSequence();
}
