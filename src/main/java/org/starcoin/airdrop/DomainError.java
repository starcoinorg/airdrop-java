package org.starcoin.airdrop;

public class DomainError extends RuntimeException {
    public static final String INVALID_ENTITY_ID = "INVALID_ENTITY_ID";
    public static final String INVALID_STATUS = "INVALID_STATUS";

    private String name;

    public String getName() {
        return name;
    }

    public DomainError() {
    }

    public DomainError(String message) {
        super(message);
    }

    public DomainError(String format, Object... args) {
        super(String.format(format, args));
    }

    public DomainError(String message, Throwable cause) {
        super(message, cause);
    }

    public static DomainError named(String name, String format, Object... args) {
        String message = "[" + name + "] " + (format == null ? "" : String.format(format, args));
        DomainError error = new DomainError(message);
        error.name = name;
        return error;
    }

}
