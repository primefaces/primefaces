package org.primefaces.model.tagcloud;

import java.io.Serializable;

public interface TagCloudItem extends Serializable {

    public String getLabel();

    public String getUrl();

    public int getStrength();
}
