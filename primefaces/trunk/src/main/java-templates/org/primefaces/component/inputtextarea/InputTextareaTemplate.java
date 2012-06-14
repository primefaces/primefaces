import java.util.ArrayList;
import java.util.List;
import javax.el.MethodExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;


    public final static String STYLE_CLASS = "ui-inputfield ui-inputtextarea ui-widget ui-state-default ui-corner-all";

    @Override
    public int getCols() {
        int cols = super.getCols();
    
        return cols > 0 ? cols : 20;
    }

    @Override
    public int getRows() {
        int rows = super.getRows();
    
        return rows > 0 ? rows : 3;
    }

    private List suggestions = null;

    public void broadcast(javax.faces.event.FacesEvent event) throws javax.faces.event.AbortProcessingException {
		super.broadcast(event);
		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		MethodExpression me = getCompleteMethod();
		
		if(me != null && event instanceof org.primefaces.event.AutoCompleteEvent) {
			suggestions = (List) me.invoke(facesContext.getELContext(), new Object[] {((org.primefaces.event.AutoCompleteEvent) event).getQuery()});
            
            if(suggestions == null) {
                suggestions = new ArrayList();
            }

            facesContext.renderResponse();
		}
	}

    public List getSuggestions() {
        return this.suggestions;
    }
