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
package org.primefaces.model.tagcloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DefaultTagCloudModel implements TagCloudModel {

    private static final long serialVersionUID = 1L;

    private List<TagCloudItem> tags;

    public DefaultTagCloudModel() {
        tags = new ArrayList<>();
    }

    public DefaultTagCloudModel(Collection<TagCloudItem> collection) {
        tags = new ArrayList<>(collection);
    }

    @Override
    public List<TagCloudItem> getTags() {
        return tags;
    }

    public void setTags(List<TagCloudItem> tags) {
        this.tags = tags;
    }

    @Override
    public void addTag(TagCloudItem item) {
        tags.add(item);
    }

    @Override
    public void removeTag(TagCloudItem item) {
        tags.remove(item);
    }

    @Override
    public void clear() {
        tags.clear();
    }
}
