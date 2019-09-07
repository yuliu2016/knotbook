package kb.service.api;

public class ServiceMetadata {
    private String packageName;
    private String packageVersion;
    private boolean isUserInterface;
    private boolean isDataProvider;

    /**
     * @return The name of the package. This should be unique and case-insensitive.
     * Format it like a java package
     */
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return the version of the service
     */
    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public boolean isUserInterface() {
        return isUserInterface;
    }

    public void setUserInterface(boolean userInterface) {
        isUserInterface = userInterface;
    }

    public boolean isDataProvider() {
        return isDataProvider;
    }

    public void setDataProvider(boolean dataProvider) {
        isDataProvider = dataProvider;
    }
}
