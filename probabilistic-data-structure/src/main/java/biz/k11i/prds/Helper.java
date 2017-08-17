package biz.k11i.prds;

import org.openjdk.jol.info.GraphLayout;

public class Helper {
    static final String TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

    static String[] words() {
        return TEXT.toLowerCase().split("[,. ]+");
    }

    static void showMemoryFootprint(Object o) {
        System.setProperty("jol.tryWithSudo", "true");
        System.out.println(GraphLayout.parseInstance(o).toFootprint());
    }
}
