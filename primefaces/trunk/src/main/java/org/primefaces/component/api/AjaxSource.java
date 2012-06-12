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
package org.primefaces.component.api;

/**
 * AjaxSource is the contract that needs to be implemented by components that fully implement all 
 * configuration options of PrimeFaces PPR
 */
public interface AjaxSource {

	public String getOnstart();
	
	public String getOncomplete();
	
	public String getOnsuccess();
	
	public String getOnerror();
	
	public String getUpdate();
	
	public String getProcess();
	
	public boolean isGlobal();
	
	public boolean isAsync();
    
    public boolean isPartialSubmit();
    
    public boolean isPartialSubmitSet();
}
