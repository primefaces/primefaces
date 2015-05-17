/*
 * Copyright 2009-2015 PrimeTek.
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
package org.primefaces.mock;

import java.io.IOException;
import javax.faces.context.PartialResponseWriter;

public class CollectingPartialResponseWriter extends PartialResponseWriter {

    private final StringBuilder builder = new StringBuilder();
    
    public CollectingPartialResponseWriter() {
        super(null);
    }
    
    @Override
    public void write(String str) throws IOException {
        builder.append(str);
    }

    public String toString() {
        return builder.toString();
    }
}
