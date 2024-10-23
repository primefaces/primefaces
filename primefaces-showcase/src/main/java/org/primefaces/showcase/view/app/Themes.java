package org.primefaces.showcase.view.app;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class Themes {

    private List<Category> categories = new ArrayList<>();
    private List<Theme> themes = new ArrayList<>();

    @PostConstruct
    public void init() {
        Category primeOne = new Category("PrimeOne");
        primeOne.getThemes().add(
                new Theme("saga-blue", "Saga Blue", "images/themes/saga.png", false));
        primeOne.getThemes().add(
                new Theme("vela-blue", "Vela Blue", "images/themes/vela.png", true));
        primeOne.getThemes().add(
                new Theme("arya-blue", "Arya Blue", "images/themes/arya.png", true));
        categories.add(primeOne);

        Category bootstrap = new Category("Bootstrap");
        bootstrap.getThemes().add(
                new Theme("bootstrap4-blue-light", "Bootstrap Blue Light", "images/themes/bootstrap4-light-blue.svg", false));
        bootstrap.getThemes().add(
                new Theme("bootstrap4-purple-light", "Bootstrap Purple Light", "images/themes/bootstrap4-light-purple.svg", false));
        bootstrap.getThemes().add(
                new Theme("bootstrap4-blue-dark", "Bootstrap Blue Dark", "images/themes/bootstrap4-dark-blue.svg", true));
        bootstrap.getThemes().add(
                new Theme("bootstrap4-purple-dark", "Bootstrap Purple Dark", "images/themes/bootstrap4-dark-purple.svg", true));
        categories.add(bootstrap);

        Category material = new Category("Material Design");
        material.getThemes().add(
                new Theme("material-indigo-light", "Material Indigo Light", "images/themes/md-light-indigo.svg", false));
        material.getThemes().add(
                new Theme("material-deeppurple-light", "Material Deep Purple Light", "images/themes/md-light-deeppurple.svg", false));
        material.getThemes().add(
                new Theme("material-indigo-dark", "Material Indigo Dark", "images/themes/md-dark-indigo.svg", true));
        material.getThemes().add(
                new Theme("material-deeppurple-dark", "Material Deep Purple Dark", "images/themes/md-dark-deeppurple.svg", true));
        categories.add(material);

        Category materialCompact = new Category("Material Design Compact");
        materialCompact.getThemes().add(
                new Theme("material-compact-indigo-light", "Material Compact Indigo Light", "images/themes/md-light-indigo.svg", false));
        materialCompact.getThemes().add(
                new Theme("material-compact-deeppurple-light", "Material Compact Deep Purple Light", "images/themes/md-light-deeppurple.svg", false));
        materialCompact.getThemes().add(
                new Theme("material-compact-indigo-dark", "Material Compact Indigo Dark", "images/themes/md-dark-indigo.svg", true));
        materialCompact.getThemes().add(
                new Theme("material-compact-deeppurple-dark", "Material Compact Deep Purple Dark", "images/themes/md-dark-deeppurple.svg", true));
        categories.add(materialCompact);

        Category legacy = new Category("Legacy");
        legacy.getThemes().add(
                new Theme("nova-light", "Nova Light", "images/themes/nova.png", false));
        legacy.getThemes().add(
                new Theme("nova-dark", "Nova Dark", "images/themes/nova-alt.png", true));
        legacy.getThemes().add(
                new Theme("nova-colored", "Nova Colored", "images/themes/nova-accent.png", true));
        legacy.getThemes().add(
                new Theme("luna-amber", "Luna Amber", "images/themes/luna-amber.png", true));
        legacy.getThemes().add(
                new Theme("luna-blue", "Luna Blue", "images/themes/luna-blue.png", true));
        legacy.getThemes().add(
                new Theme("luna-green", "Luna Green", "images/themes/luna-green.png", true));
        legacy.getThemes().add(
                new Theme("luna-pink", "Luna Pink", "images/themes/luna-pink.png", true));
        categories.add(legacy);

        categories.stream().forEach(category -> themes.addAll(category.getThemes()));
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Theme> getThemes() {
        return themes;
    }

    public static class Category implements Serializable {

        private String name;
        private List<Theme> themes = new ArrayList<>();

        public Category(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Theme> getThemes() {
            return themes;
        }

        public void setThemes(List<Theme> themes) {
            this.themes = themes;
        }
    }

    public static class Theme implements Serializable {

        private String id;
        private String name;
        private String image;
        private boolean dark;

        public Theme(String id, String name, String image, boolean dark) {
            this.id = id;
            this.name = name;
            this.image = image;
            this.dark = dark;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public boolean isDark() {
            return dark;
        }

        public void setDark(boolean dark) {
            this.dark = dark;
        }
    }
}
