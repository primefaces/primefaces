/*
	Copyright 2017 Marceau Dewilde <m@ceau.be>

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,op
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package org.primefaces.model.chartjs.objects;

import java.util.ArrayList;

/**
 * {@link ArrayList} subclass with special serialization behavior: if only one
 * item is contained, the item will be serialized as a single value.
 */
public class OptionalArray<E> extends ArrayList<E> {

    private static final long serialVersionUID = 1470940649764L;
}
