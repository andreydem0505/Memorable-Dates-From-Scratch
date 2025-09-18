package dementiev_a.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Model<T> {
    private T id;
}
