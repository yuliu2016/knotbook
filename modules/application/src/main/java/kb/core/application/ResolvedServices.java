package kb.core.application;

import kb.service.api.MetaService;

import java.util.List;

class ResolvedServices<T extends MetaService> {
    Class<T> theClass;
    List<T> services;

    ResolvedServices(Class<T> theClass, List<T> services) {
        this.theClass = theClass;
        this.services = services;
    }

    void print() {
        System.out.println("\nListing " + services.size() +
                " package(s) for " + theClass.getSimpleName() + ":");
        for (T s : services) {
            System.out.println(s.getMetadata());
        }
    }
}
