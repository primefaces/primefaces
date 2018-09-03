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
package org.primefaces.model.menu;

public interface MenuElement {

    /**
     * Allows access the id property which is managed and auto-assigned by
     * callers like menu model implementations and used to identify the element.
     *
     * The property is unrelated to the {@code id} attribute in the generated
     * XHTML.
     *
     * @return the id property
     */
    public String getId();

    /**
     * Sets the id property which is described in {@link #getId() }. You should
     * have a good reason to call this if you're not extending, but only using
     * Primefaces.
     *
     * The property is unrelated to the {@code id} attribute in the generated
     * XHTML.
     *
     * @param id the id value
     */
    public void setId(String id);

    public boolean isRendered();
}
