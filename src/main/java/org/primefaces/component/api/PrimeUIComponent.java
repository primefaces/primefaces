package org.primefaces.component.api;

import javax.faces.component.StateHelper;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.List;
import java.util.Map;

public interface PrimeUIComponent {

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
     * Used as a workaround to access {@link UIComponent#getStateHelper()}
     */
    StateHelper getState();
}
