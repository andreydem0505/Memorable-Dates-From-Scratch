package dementiev_a.data.repository;

import dementiev_a.data.model.Model;

import java.util.Collection;

public interface Repository<M extends Model<I>, I> {
    M findById(I id);
    Collection<M> findAll();
    void save(M entity);
    void deleteById(I id);
    void deleteAll();
}
