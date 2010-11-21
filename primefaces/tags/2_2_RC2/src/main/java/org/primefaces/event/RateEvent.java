package org.primefaces.event;

import javax.faces.component.UIComponent;
import javax.faces.event.FacesEvent;
import javax.faces.event.FacesListener;

public class RateEvent extends FacesEvent {

	private double rating;

	public RateEvent(UIComponent component) {
		super(component);
	}
	
	public RateEvent(UIComponent component, double rating) {
		super(component);
		this.rating = rating;
	}

	@Override
	public boolean isAppropriateListener(FacesListener listener) {
		return false;
	}

	@Override
	public void processListener(FacesListener listener) {
		throw new UnsupportedOperationException();
	}
	
	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}
}