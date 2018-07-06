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
package org.primefaces.expression;

public class SearchExpressionHint {

    public static final int NONE = 0x0;

    /**
     * Checks if the {@link UIComponent} has a renderer or not. This check is currently only useful for the update attributes, as a component without
     * renderer can't be updated.
     */
    public static final int VALIDATE_RENDERER = 0x1;

    public static final int IGNORE_NO_RESULT = 0x2;

    public static final int PARENT_FALLBACK = 0x4;

    public static final int SKIP_UNRENDERED = 0x8;
}
