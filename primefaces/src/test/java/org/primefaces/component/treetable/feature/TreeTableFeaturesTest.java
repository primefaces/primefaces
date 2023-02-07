package org.primefaces.component.treetable.feature;

import javax.faces.context.FacesContext;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.primefaces.component.treetable.TreeTable;

public class TreeTableFeaturesTest {

    @Test
    public void testReplace() {
        FilterFeature mockFeature = new FilterFeature() {
            @Override
            public boolean shouldDecode(FacesContext context, TreeTable table) {
                return false;
            }
        };

        FilterFeature oldFilter = TreeTableFeatures.replace(FilterFeature.class, mockFeature);
        Assertions.assertNotEquals(oldFilter, TreeTableFeatures.get(FilterFeature.class));
        Assertions.assertEquals(mockFeature, TreeTableFeatures.get(FilterFeature.class));
        Assertions.assertEquals(mockFeature, TreeTableFeatures.filterFeature());
    }
}
