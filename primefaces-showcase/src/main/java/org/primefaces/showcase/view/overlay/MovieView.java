/*
 * The MIT License
 *
 * Copyright (c) 2009-2021 PrimeTek
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
package org.primefaces.showcase.view.overlay;

import org.primefaces.showcase.domain.Movie;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.event.SelectEvent;

@Named
@RequestScoped
public class MovieView {

    private Movie movie;

    private List<Movie> movieList;

    public List<Movie> getMovieList() {
        return movieList;
    }

    @PostConstruct
    public void init() {
        movieList = new ArrayList<Movie>();

        movieList.add(new Movie("The Lord of the Rings: The Two Towers", "Peter Jackson", "Fantasy, Epic", 179));
        movieList.add(new Movie("The Amazing Spider-Man 2", "Marc Webb", "Action", 142));
        movieList.add(new Movie("Iron Man 3", "Shane Black", "Action", 109));
        movieList.add(new Movie("Thor: The Dark World", "Alan Taylor", "Action, Fantasy", 112));
        movieList.add(new Movie("Avatar", "James Cameron", "Science Fiction", 160));
        movieList.add(new Movie("The Lord of the Rings: The Fellowship of the Ring", "Peter Jackson", "Fantasy, Epic", 165));
        movieList.add(new Movie("Divergent", "Neil Burger", "Action", 140));
        movieList.add(new Movie("The Hobbit: The Desolation of Smaug", "Peter Jackson", "Fantasy", 161));
        movieList.add(new Movie("Rio 2", "Carlos Saldanha", "Comedy", 101));
        movieList.add(new Movie("Captain America: The Winter Soldier", "Joe Russo", "Action", 136));
        movieList.add(new Movie("Fast Five", "Justin Lin", "Action", 132));
        movieList.add(new Movie("The Lord of the Rings: The Return of the King", "Peter Jackson", "Fantasy, Epic", 200));

    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void onRowSelect(SelectEvent<Movie> event) {
        FacesMessage msg = new FacesMessage("Movie Selected", event.getObject().getMovie());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
}
