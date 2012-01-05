import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;

    public String getCollapseIcon() {
        return "ui-icon-triangle-1-" + this.getPosition().substring(0,1);
    }

    public boolean isNesting() {
        for(UIComponent child : getChildren()) {
            if(child instanceof Layout)
                return true;
        }

        return false;
    }