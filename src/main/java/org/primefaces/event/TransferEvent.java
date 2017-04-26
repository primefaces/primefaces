/*
 * Copyright 2009-2017 PrimeTek.
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
package org.primefaces.event;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

/**
 * Ajax behavior event to represent {@link PickList} transfers.
 * <p>
 * Used in <p:ajax event="transfer" />
 */
public class TransferEvent extends AbstractAjaxBehaviorEvent {

   /** State variable: List of objects being transferred */
   private final List<?> items;
   
   /** State variable: Flag indicating adding or removing items */
   private final boolean add;

   /**
    * Constructor requireing all fields.
    * 
    * @param component the {@link UIComponent} that owns this event
    * @param behavior the {@link Behavior} 
    * @param items the items to be moved to the other list
    * @param add true if adding items, false if removing items
    */
   public TransferEvent(final UIComponent component, final Behavior behavior, final List<?> items, final boolean add) {
      super(component, behavior);
      this.items = items;
      this.add = add;
   }

   /**
    * Is the event adding items to the {@link UIComponent}.
    * 
    * @return true if adding items
    */
   public boolean isAdd() {
      return add;
   }

   /**
    * Is the event removing items from the {@link UIComponent}.
    * 
    * @return true if removing items
    */
   public boolean isRemove() {
      return !add;
   }

   /**
    * The List of Objects being transferred.
    * 
    * @return a List<?> of items being transferred
    */
   public List<?> getItems() {
      return items;
   }
}