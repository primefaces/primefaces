/*
 * The MIT License
 *
 * Copyright (c) 2009-2026 PrimeTek Informatics
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.primefaces.showcase.view.multimedia;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.showcase.domain.Photo;
import org.primefaces.showcase.service.PhotoService;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.PhaseId;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class DynamicGalleriaView implements Serializable {

    private List<Photo> photos;

    @Inject
    private PhotoService service;

    @PostConstruct
    public void init() {
        photos = service.getPhotos();
    }

    public StreamedContent getPhotoAsStreamedContent() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            return DefaultStreamedContent.DUMMY; // might get invoked already during rendering, check the docs
        }

        String photoId = facesContext.getExternalContext().getRequestParameterMap().get("photoId");
        Photo photo = photos.stream()
                .filter(p -> p.getId().equals(photoId))
                .findFirst().get();

        return DefaultStreamedContent.builder()
                .name(photo.getTitle() + ".jpg")
                .contentType("image/jpg")
                .stream(() -> facesContext.getExternalContext().getResourceAsStream("/resources/" + photo.getItemImageSrc()))
                .build();
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setService(PhotoService service) {
        this.service = service;
    }
}
