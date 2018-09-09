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
package org.primefaces.model.chartjs.options;

import org.primefaces.model.chartjs.options.elements.BubbleElements;

/**
 * The bubble chart has no unique configuration options. To configure options common to all of the bubbles, the point element options are used.
	
	For example, to give all bubbles a 1px wide black border, the following options would be used.
	
	new Chart(ctx,{
	    type:"bubble",
	    options: {
	        elements: {
	            points: {
	                borderWidth: 1,
	                borderColor: 'rgb(0, 0, 0)'
	            }
	        }
	    }
	});

 */
public class BubbleOptions extends Options<BubbleOptions> {

	private BubbleElements elements;

	public BubbleElements getElements() {
		return elements;
	}

	public BubbleOptions setElements(BubbleElements elements) {
		this.elements = elements;
		return this;
	}

}
