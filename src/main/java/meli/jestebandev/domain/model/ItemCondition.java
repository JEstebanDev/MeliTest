package meli.jestebandev.domain.model;

public enum ItemCondition {
    NEW("Nuevo"),
    USED("Usado");

    private final String displayName;

    ItemCondition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}