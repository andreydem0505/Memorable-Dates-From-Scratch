package dementiev_a.data.sequence;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CelebrationSequence extends Sequence {
    @Getter(lazy = true)
    private static final CelebrationSequence instance = new CelebrationSequence();
}

