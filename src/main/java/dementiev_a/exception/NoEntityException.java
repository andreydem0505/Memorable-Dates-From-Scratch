package dementiev_a.exception;

public class NoEntityException extends RuntimeException {
    public NoEntityException(String entityName, String entityId) {
        super("Сущность \"%s\" с ID=%s не была найдена".formatted(entityName, entityId));
    }
}
