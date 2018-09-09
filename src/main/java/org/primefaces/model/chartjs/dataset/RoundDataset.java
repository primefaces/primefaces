/*
	Copyright 2017 Marceau Dewilde <m@ceau.be>

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package org.primefaces.model.chartjs.dataset;

public abstract class RoundDataset<T extends RoundDataset<T, O>, O> extends BackgroundBorderHoverDataset<T, O> {

	/**
	 * @see #setLabel(String)
	 */
	protected String label;

	/**
	 * @see #setLabel(String)
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * The label for the dataset which appears in the legend and tooltips
	 */
	@SuppressWarnings("unchecked")
	public T setLabel(String label) {
		this.label = label;
	    return (T) this;
	}
	
}
