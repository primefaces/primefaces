import java.util.List;
import org.primefaces.model.menu.MenuElement;
   
    public List getElements() {
        return getChildren();
    }
    
    public int getElementsCount() {
        return getChildCount();
    }