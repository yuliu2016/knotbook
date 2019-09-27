package kb.service.api.application;

import java.util.Arrays;

public class JVMInstance {
    private static String[] args;

    public static String[] getArgs() {
        return Arrays.copyOf(args, args.length);
    }

    public static void setArgs(String[] args) {
        JVMInstance.args = args;
    }
}
