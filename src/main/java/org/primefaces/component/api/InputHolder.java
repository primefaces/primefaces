/**
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
package org.primefaces.component.api;

/**
 * InputHolder is implemented by input components who has an input field along with complex markup like spinner, autocomplete.
 */
public interface InputHolder {

    /**
     * @return Client id of the input element
     */
    String getInputClientId();

    /**
     * @return Client id of the validatable input element
     */
    String getValidatableInputClientId();

    /**
     * @return Client id of the label for aria
     */
    String getLabelledBy();

    /**
     * @return Sets the id of the label for aria
     */
    void setLabelledBy(String id);
}
