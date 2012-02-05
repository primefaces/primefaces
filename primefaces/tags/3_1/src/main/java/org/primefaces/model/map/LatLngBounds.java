/*
 * Copyright 2009-2012 Prime Teknoloji.
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
package org.primefaces.model.map;

public class LatLngBounds {
	
	private LatLng northEast;
	
	private LatLng southWest;
	
	public LatLngBounds(LatLng northEast, LatLng southWest) {
		this.northEast = northEast;
		this.southWest = southWest;
	}

	public LatLng getNorthEast() {
		return northEast;
	}

	public void setNorthEast(LatLng northEast) {
		this.northEast = northEast;
	}

	public LatLng getSouthWest() {
		return southWest;
	}

	public void setSouthWest(LatLng southWest) {
		this.southWest = southWest;
	}
}