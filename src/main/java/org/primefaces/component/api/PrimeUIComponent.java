package org.primefaces.component.api;

import javax.el.ValueExpression;
import javax.faces.component.StateHelper;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;

public interface PrimeUIComponent {

    String COMPONENT_FAMILY = "org.primefaces.component";

    enum PropertyKeys {
        style,
        styleClass,
        renderEmptyFacets;
    }

    /**
     * See {@link UIComponent#getClientId(FacesContext)}
     */
    String getClientId(FacesContext ctxt);

    /**
     * See {@link UIComponent#isRendered()}
     */
    boolean isRendered();

    /**
     * See {@link UIComponent#getChildCount()}
     */
    int getChildCount();

    /**
     * See {@link UIComponent#getChildren()}
     */
    List<UIComponent> getChildren();

    /**
     * See {@link UIComponent#getFacets()}
     */
    Map<String, UIComponent> getFacets();

    /**
     * See {@link UIComponent#getValueExpression(String)}
     */
    ValueExpression getValueExpression(String name);

    /**
     * Used as a workaround to access {@link UIComponent#getStateHelper()}
     */
    StateHelper getState();

    default String getFamily() {
        return COMPONENT_FAMILY;
    }

    default boolean isRenderEmptyFacets() {
        return (Boolean) getState().eval(PropertyKeys.renderEmptyFacets, false);
    }

    default void setRenderEmptyFacets(boolean renderEmptyFacets) {
        getState().put(PropertyKeys.renderEmptyFacets, renderEmptyFacets);
    }

    default String getStyle() {
        return (String) getState().eval(PropertyKeys.style, null);
    }

    default void setStyle(String style) {
        getState().put(PropertyKeys.style, style);
    }

    default String getStyleClass() {
        return (String) getState().eval(PropertyKeys.styleClass, null);
    }

    default void setStyleClass(String styleClass) {
        getState().put(PropertyKeys.styleClass, styleClass);
    }

}
