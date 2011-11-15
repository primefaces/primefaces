
    public final static String TEXT_ONLY_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only";
    public final static String ICON_ONLY_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only";
    public final static String TEXT_ICON_LEFT_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-left";
    public final static String TEXT_ICON_RIGHT_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-icon-right";
    public final static String LEFT_ICON_CLASS = "ui-button-icon-left";
    public final static String RIGHT_ICON_CLASS = "ui-button-icon-right";
    public final static String TEXT_CLASS = "ui-button-text";

    public String resolveIcon() {
        String icon = getIcon();
    
        if(icon == null) {
            icon = getImage();
        }
    
        return icon;
    }

    public String resolveStyleClass() {
        String icon = resolveIcon();
        Object value = getValue();
        String styleClass = ""; 
    
        if(value != null && icon == null) {
            styleClass = TEXT_ONLY_BUTTON_CLASS;
        }
        else if(value != null && icon != null) {
            styleClass = getIconPos().equals("left") ? TEXT_ICON_LEFT_BUTTON_CLASS : TEXT_ICON_RIGHT_BUTTON_CLASS;
        }
        else if(value == null && icon != null) {
            styleClass = ICON_ONLY_BUTTON_CLASS;
        }
    
        if(isDisabled()) {
            styleClass = styleClass + " ui-state-disabled";
        } 
    
        String userStyleClass = getStyleClass();
        if(userStyleClass != null) {
            styleClass = styleClass + " " + userStyleClass;
        }
    
        return styleClass;
    }