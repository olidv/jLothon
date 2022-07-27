package lothon.util;

import java.text.MessageFormat;

public final class Eve {

    public static void print(final String msg, final Object... args) {
        if (args == null || args.length == 0) {
            System.out.println(msg);
        } else {
            System.out.println(MessageFormat.format(msg, args));
        }
    }

    public static double toFator(final double percent) {
        return 1 + (percent / 100);
    }

    public static double toRedutor(final double percent){
        return percent / 100.0d;
    }

}
