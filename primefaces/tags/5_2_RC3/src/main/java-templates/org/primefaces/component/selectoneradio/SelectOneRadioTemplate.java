import java.util.List;
import javax.faces.model.SelectItem;
import javax.faces.component.UINamingContainer;

    public final static String[] SUPPORTED_EVENTS = new String[]{"onchange","onclick"};
    
    public final static String STYLE_CLASS = "ui-selectoneradio ui-widget";
    public final static String NATIVE_STYLE_CLASS = "ui-selectoneradio ui-selectoneradio-native ui-widget";

    private int index = -1;

    public String getRadioButtonId(FacesContext context) {
        index++;

        return this.getClientId(context) + UINamingContainer.getSeparatorChar(context) + index;
    }

    private List<SelectItem> selectItems;

    public void setSelectItems(List<SelectItem> selectItems) {  
        this.selectItems = selectItems;
    }

    public List<SelectItem> getSelectItems() {
        return this.selectItems;
    }
