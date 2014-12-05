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