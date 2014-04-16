
    public void setLoaded(boolean value) {
        getStateHelper().put("loaded", value);
    }

    public boolean isLoaded() {
        Object value = getStateHelper().get("loaded");
        
        return (value == null) ? false : (Boolean) value;
    }