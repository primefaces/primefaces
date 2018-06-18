import org.primefaces.util.HTML;
import org.primefaces.util.LangUtils;

    public static final String STYLE_CLASS = "ui-splitbutton ui-buttonset ui-widget";
    public static final String BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-left ui-button-text-icon-left";
    public static final String BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-left ui-button-text-icon-right";
    public static final String MENU_ICON_BUTTON_CLASS = "ui-splitbutton-menubutton  ui-button ui-widget ui-state-default ui-corner-right ui-button-icon-only";
    public final static String BUTTON_TEXT_ONLY_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-left ui-button-text-only";
    public final static String BUTTON_ICON_ONLY_BUTTON_CLASS = "ui-button ui-widget ui-state-default ui-corner-left ui-button-icon-only";
    public static final String SPLITBUTTON_CONTAINER_CLASS = "ui-menu ui-splitbuttonmenu ui-menu-dynamic ui-widget ui-widget-content ui-corner-all ui-helper-clearfix ui-shadow";

    public String resolveStyleClass() {
        boolean iconBlank = LangUtils.isValueBlank(getIcon());
        boolean valueBlank = getValue() == null;
        String styleClass = ""; 
        
        if (!ComponentUtils.shouldRenderChildren(this)) {
            if(!valueBlank && iconBlank) {
                styleClass = HTML.BUTTON_TEXT_ONLY_BUTTON_CLASS;
            }
            else if(!valueBlank && !iconBlank) {
                styleClass = getIconPos().equals("left") 
                        ? HTML.BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS 
                        : HTML.BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS;
            }
            else if(valueBlank && !iconBlank) {
                styleClass = HTML.BUTTON_ICON_ONLY_BUTTON_CLASS;
            }
        }
        else if(!valueBlank && iconBlank) {
            styleClass = BUTTON_TEXT_ONLY_BUTTON_CLASS;
        }
        else if(!valueBlank && !iconBlank) {
            styleClass = getIconPos().equals("left") ? BUTTON_TEXT_ICON_LEFT_BUTTON_CLASS : BUTTON_TEXT_ICON_RIGHT_BUTTON_CLASS;
        }
        else if(valueBlank && !iconBlank) {
            styleClass = BUTTON_ICON_ONLY_BUTTON_CLASS;
        }
    
        if(isDisabled()) {
            styleClass = styleClass + " ui-state-disabled";
        } 
        
        return styleClass;
    }

    public boolean isPartialSubmitSet() {
        return (getStateHelper().get(PropertyKeys.partialSubmit) != null) || (this.getValueExpression(PropertyKeys.partialSubmit.toString()) != null);
    }
    
    public boolean isResetValuesSet() {
        return (getStateHelper().get(PropertyKeys.resetValues) != null) || (this.getValueExpression(PropertyKeys.resetValues.toString()) != null);
    }
    
    public boolean isAjaxified() {
        return isAjax();
    }