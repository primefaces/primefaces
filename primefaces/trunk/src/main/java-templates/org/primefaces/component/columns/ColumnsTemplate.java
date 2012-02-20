import java.util.List;
import java.util.ArrayList;
import java.util.Map;

    private int colIndex = -1;

    @Override
    public String getClientId(FacesContext context) {
        String baseClientId = super.getClientId(context);
    
        return (colIndex == -1) ? baseClientId : baseClientId + "_colIndex_" + colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    
        List<?> columns = (List<?>) this.getValue();
        if(columns != null) {
            Map<String,Object> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
    
            requestMap.put(this.getVar(), columns.get(colIndex));
            requestMap.put(this.getColumnIndexVar(), colIndex);
        }
    }
