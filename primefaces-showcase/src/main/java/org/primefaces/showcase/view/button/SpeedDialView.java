package org.primefaces.showcase.view.button;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@RequestScoped
public class SpeedDialView {

    private MenuModel model;

    @PostConstruct
    public void init() {
        model = new DefaultMenuModel();

        DefaultMenuItem item = DefaultMenuItem.builder()
                .icon("pi pi-pencil")
                .command("#{speedDialView.add}")
                .update("messages")
                .build();
        model.getElements().add(item);

        item = DefaultMenuItem.builder()
                .icon("pi pi-refresh")
                .command("#{speedDialView.update}")
                .update("messages")
                .build();
        model.getElements().add(item);

        item = DefaultMenuItem.builder()
                .icon("pi pi-trash")
                .command("#{speedDialView.delete}")
                .update("messages")
                .build();
        model.getElements().add(item);

        item = DefaultMenuItem.builder()
                .icon("pi pi-upload")
                .outcome("/ui/file/upload/basic")
                .build();
        model.getElements().add(item);

        item = DefaultMenuItem.builder()
                .icon("pi pi-external-link")
                .url("https://www.primefaces.org")
                .build();
        model.getElements().add(item);
    }

    public MenuModel getModel() {
        return model;
    }

    public void add() {
        addMessage("Add", "Data Added");
    }

    public void update() {
        addMessage("Update", "Data Updated");
    }

    public void delete() {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Delete", "Data Deleted");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
