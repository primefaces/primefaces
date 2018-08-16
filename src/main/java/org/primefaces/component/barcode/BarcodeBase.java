/*
 * Copyright 2009-2018 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.component.barcode;

import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.faces.component.UINamingContainer;
import javax.el.ValueExpression;
import javax.el.MethodExpression;
import javax.faces.render.Renderer;
import java.io.IOException;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import java.util.List;
import java.util.ArrayList;
import org.primefaces.util.ComponentUtils;


public abstract class BarcodeBase extends HtmlGraphicImage {


	public static final String COMPONENT_TYPE = "org.primefaces.component.Barcode";
	public static final String COMPONENT_FAMILY = "org.primefaces.component";
	public static final String DEFAULT_RENDERER = "org.primefaces.component.BarcodeRenderer";

	public enum PropertyKeys {

		type
		,cache
		,format
		,orientation
		,qrErrorCorrection
		,hrp;
	}

	public BarcodeBase() {
		setRendererType(DEFAULT_RENDERER);
	}

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public java.lang.String getType() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.type, null);
	}
	public void setType(java.lang.String _type) {
		getStateHelper().put(PropertyKeys.type, _type);
	}

	public boolean isCache() {
		return (java.lang.Boolean) getStateHelper().eval(PropertyKeys.cache, true);
	}
	public void setCache(boolean _cache) {
		getStateHelper().put(PropertyKeys.cache, _cache);
	}

	public java.lang.String getFormat() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.format, "svg");
	}
	public void setFormat(java.lang.String _format) {
		getStateHelper().put(PropertyKeys.format, _format);
	}

	public int getOrientation() {
		return (java.lang.Integer) getStateHelper().eval(PropertyKeys.orientation, 0);
	}
	public void setOrientation(int _orientation) {
		getStateHelper().put(PropertyKeys.orientation, _orientation);
	}

	public java.lang.String getQrErrorCorrection() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.qrErrorCorrection, "L");
	}
	public void setQrErrorCorrection(java.lang.String _qrErrorCorrection) {
		getStateHelper().put(PropertyKeys.qrErrorCorrection, _qrErrorCorrection);
	}

	public java.lang.String getHrp() {
		return (java.lang.String) getStateHelper().eval(PropertyKeys.hrp, "bottom");
	}
	public void setHrp(java.lang.String _hrp) {
		getStateHelper().put(PropertyKeys.hrp, _hrp);
	}

}