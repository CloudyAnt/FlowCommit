package cn.itscloudy.flowcommit.plugin;

import java.util.ResourceBundle;

public interface FcConst {
    String ID = "FlowCommit";
    String NAME = ID;

    static String get(String key) {
        return StringResourceBundle.instance.get(key);
    }

    class StringResourceBundle {
        private static final StringResourceBundle instance = new StringResourceBundle();

        private final ResourceBundle bundle = ResourceBundle.getBundle("locales.fc");

        public String get(String key) {
            return bundle.getString(key);
        }
    }
}