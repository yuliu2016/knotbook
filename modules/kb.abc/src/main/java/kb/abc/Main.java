package kb.abc;

import kb.service.abc.ABC;

import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        for (var abc : ServiceLoader.load(ABC.class)) {
            abc.launch(args);
        }
    }
}
