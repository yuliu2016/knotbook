package kb.service.api;

public class ServiceMetadata {
    private String name;
    private String description;

    public ServiceMetadata(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * @return The name of the package. This should be unique and case-insensitive.
     * Format it like a java package
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description of the package
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return getName() + " => " + getDescription();
    }
}
