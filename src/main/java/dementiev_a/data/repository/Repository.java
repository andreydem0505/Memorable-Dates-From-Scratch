package dementiev_a.data.repository;

import dementiev_a.data.model.Model;

import java.util.Collection;

public interface Repository<M extends Model<ID>, ID> {
    M findById(ID id);
    Collection<M> findAll();
    ID save(M entity);
    void deleteById(ID id);
    void deleteAll();
}
