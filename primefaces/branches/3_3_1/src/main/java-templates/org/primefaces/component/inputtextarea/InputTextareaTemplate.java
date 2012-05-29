
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