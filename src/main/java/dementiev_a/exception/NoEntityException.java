package dementiev_a.exception;

public class NoEntityException extends RuntimeException {
    public NoEntityException(String entityName, String entityId) {
        super("Entity %s with ID=%s was not found".formatted(entityName, entityId));
    }
}
