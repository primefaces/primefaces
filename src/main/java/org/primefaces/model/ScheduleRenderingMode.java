package org.primefaces.model;

public enum ScheduleRenderingMode {
    BACKGROUND("background"),
    INVERSE_BACKGROUND("inverse-background");

    private String rendering;

    ScheduleRenderingMode(String rendering) {
        this.rendering = rendering;
    }

    public String getRendering() {
        return rendering;
    }
}
