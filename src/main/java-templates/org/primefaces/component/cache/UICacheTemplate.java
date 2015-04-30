import javax.faces.component.visit.VisitContext;

    private boolean cacheSetInCurrentRequest = false;

    public boolean isCacheSetInCurrentRequest() {
        return this.cacheSetInCurrentRequest;
    }

    public void setCacheSetInCurrentRequest(boolean cacheSetInCurrentRequest) {
        this.cacheSetInCurrentRequest = cacheSetInCurrentRequest;
    }

    @Override
    protected boolean isVisitable(VisitContext visitContext) {
        return this.isDisabled() || this.isCacheSetInCurrentRequest();
    }

    protected boolean shouldProcess() {
        return this.isDisabled() || this.isCacheSetInCurrentRequest() || this.isProcessEvents();
    }

    @Override
    public void processDecodes(FacesContext context) {
        if(shouldProcess()) {
            super.processDecodes(context);
        }
    }

    @Override
    public void processValidators(FacesContext context) {
        if(shouldProcess()) {
            super.processValidators(context);
        }
    }

    @Override
    public void processUpdates(FacesContext context) {
        if(shouldProcess()) {
            super.processUpdates(context);
        }
    }