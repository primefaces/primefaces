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
 * AjaxSource is the contract that needs to be implemented by components that fully implement all configuration options of PrimeFaces PPR
 */
public interface AjaxSource {

    String getOnstart();

    String getOncomplete();

    String getOnsuccess();

    String getOnerror();

    String getUpdate();

    String getProcess();

    boolean isGlobal();

    boolean isAsync();

    boolean isPartialSubmit();

    boolean isPartialSubmitSet();

    String getPartialSubmitFilter();

    boolean isResetValues();

    boolean isResetValuesSet();

    boolean isIgnoreAutoUpdate();

    boolean isAjaxified();

    String getDelay();

    int getTimeout();

    String getForm();
}
