package kb.abc;

import kb.service.abc.ABC;

import java.util.ServiceLoader;

public class Main {
    public static void main(String[] args) {
        ServiceLoader
                .load(ABC.class)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ABC class not found"))
                .launch(args);
    }
}
