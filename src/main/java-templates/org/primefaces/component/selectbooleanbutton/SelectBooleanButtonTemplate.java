import org.primefaces.util.HTML;

    public final static String STYLE_CLASS = "ui-selectbooleanbutton ui-widget";

    public String resolveStyleClass(boolean checked, boolean disabled) {
        String icon = checked ? getOnIcon() : getOffIcon();
        String styleClass = icon != null ? HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS : HTML.BUTTON_TEXT_ONLY_BUTTON_CLASS;
    
        if(disabled) {
            styleClass = styleClass + " ui-state-disabled";
        } 

        if(checked) {
            styleClass = styleClass + " ui-state-active";
        }

        if(!isValid()) {
            styleClass = styleClass + " ui-state-error";
        }

        String userStyleClass = getStyleClass();
        if(userStyleClass != null) {
            styleClass = styleClass + " " + userStyleClass;
        }
    
        return styleClass;
    }