package org.primefaces.integrationtests.selectcheckboxmenu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import lombok.Data;

@Named
@ViewScoped
@Data
public class SelectChecboxMenu001 implements Serializable {

    private static final long serialVersionUID = -7518459955779385834L;

    private String[] selectedCities;
    private List<String> cities;

    @PostConstruct
    public void init() {
        cities = new ArrayList<>();
        cities.add("Miami");
        cities.add("London");
        cities.add("Paris");
        cities.add("Istanbul");
        cities.add("Berlin");
        cities.add("Barcelona");
        cities.add("Rome");
        cities.add("Brasilia");
        cities.add("Amsterdam");
    }

}
