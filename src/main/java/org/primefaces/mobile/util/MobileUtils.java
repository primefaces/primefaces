/**
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
package org.primefaces.mobile.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MobileUtils {

    public static final Map<Integer, String> GRID_MAP;
    public static final Map<Integer, String> BLOCK_MAP;

    static {
        HashMap<Integer, String> gridMap = new HashMap<Integer, String>();
        gridMap.put(1, "ui-grid-solo");
        gridMap.put(2, "ui-grid-a");
        gridMap.put(3, "ui-grid-b");
        gridMap.put(4, "ui-grid-c");
        gridMap.put(5, "ui-grid-d");
        GRID_MAP = Collections.unmodifiableMap(gridMap);

        HashMap<Integer, String> blockMap = new HashMap<Integer, String>();
        blockMap.put(0, "ui-block-a");
        blockMap.put(1, "ui-block-b");
        blockMap.put(2, "ui-block-c");
        blockMap.put(3, "ui-block-d");
        blockMap.put(4, "ui-block-e");
        BLOCK_MAP = Collections.unmodifiableMap(blockMap);
    }



    public static String buildNavigation(String value) {
        String outcome = value;

        //convert href outcomes to pm outcomes
        if (outcome.startsWith("#")) {
            outcome = outcome.replace("#", "pm:");
        }

        String viewMeta = outcome.split("pm:")[1];
        int optionsIndex = viewMeta.indexOf('?');
        String viewName = (optionsIndex == -1) ? viewMeta : viewMeta.substring(0, optionsIndex);
        StringBuilder command = new StringBuilder();

        command.append("PrimeFaces.Mobile.navigate('#").append(viewName).append("',{");

        //parse navigation options like reverse and transition
        if (optionsIndex != -1) {
            String[] paramStrings = viewMeta.substring(optionsIndex + 1, viewMeta.length()).split("&");
            for (int i = 0; i < paramStrings.length; i++) {
                String[] tokens = paramStrings[i].split("=");
                command.append(tokens[0]).append(":").append("'").append(tokens[1]).append("'");

                if (i != (paramStrings.length - 1)) {
                    command.append(",");
                }
            }
        }

        command.append("});");

        return command.toString();
    }

}
