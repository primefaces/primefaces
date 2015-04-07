
    public String getIconToRender(boolean expanded) {
        String icon = getIcon();
        if(icon != null) {
            return icon;
        } else {
            String expandedIcon = getExpandedIcon();
            String collapsedIcon = getCollapsedIcon();

            if(expandedIcon != null && collapsedIcon != null) {
                return expanded ? expandedIcon : collapsedIcon;
            }
        }

        return null;
    }