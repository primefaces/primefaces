package org.primefaces.model;

import java.util.Date;

public class LazyScheduleModel extends DefaultScheduleModel {

	/**
	 * Method to be used when implementing lazy loading, implementers should override to fetch events that belong to a particular period
	 * 
	 * @param start	Start date of period
	 * @param end 	End date of period
	 */
	public void loadEvents(Date start, Date end) {}
}