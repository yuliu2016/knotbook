package kb.service.api;

public class ServiceMetadata {
    private String packageName;
    private String packageVersion;

    public ServiceMetadata(String packageName, String packageVersion) {
        this.packageName = packageName;
        this.packageVersion = packageVersion;
    }

    /**
     * @return The name of the package. This should be unique and case-insensitive.
     * Format it like a java package
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return the version of the service
     */
    public String getPackageVersion() {
        return packageVersion;
    }

    @Override
    public String toString() {
        return packageName + " => " + packageVersion;
    }
}
