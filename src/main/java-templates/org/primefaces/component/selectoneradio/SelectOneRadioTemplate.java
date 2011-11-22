import javax.faces.component.UINamingContainer;
    
    private int index = -1;

    public String getContainerClientId(FacesContext context) {
        index++;

        return this.getClientId(context) + UINamingContainer.getSeparatorChar(context) + index;
    }
