import java.util.List;
import java.util.Map;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;

    public final static String STYLE_CLASS = "ui-linkbutton " + HTML.BUTTON_TEXT_ONLY_BUTTON_CLASS;
    public final static String DISABLED_STYLE_CLASS = STYLE_CLASS + " ui-state-disabled";

    public Map<String, List<String>> getParams() {
        return ComponentUtils.getUIParams(this);
    }