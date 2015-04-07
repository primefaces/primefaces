import java.util.List;
import java.util.Map;
import org.primefaces.util.ComponentUtils;

    public final static String STYLE_CLASS = "ui-link ui-widget";
    public final static String DISABLED_STYLE_CLASS = "ui-link ui-widget ui-state-disabled";

    public final static String MOBILE_STYLE_CLASS = "ui-link ui-widget";
    public final static String MOBILE_DISABLED_STYLE_CLASS = "ui-link ui-widget ui-state-disabled";

    public Map<String, List<String>> getParams() {
        return ComponentUtils.getUIParams(this);
    }