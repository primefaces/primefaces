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
package org.primefaces.showcase.service;

import org.primefaces.showcase.domain.*;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Named
@ApplicationScoped
public class CustomerService {

    private List<Customer> customers;
    private Country[] countries;
    private Representative[] representatives;
    private String[] names;
    private String[] companies;

    @PostConstruct
    public void init() {
        customers = new ArrayList<>();

        countries = new Country[]{
            new Country(0, "Argentina", "ar"),
            new Country(1, "Australia", "au"),
            new Country(2, "Brazil", "br"),
            new Country(3, "Canada", "ca"),
            new Country(4, "Germany", "de"),
            new Country(5, "France", "fr"),
            new Country(6, "India", "in"),
            new Country(7, "Italy", "it"),
            new Country(8, "Japan", "jp"),
            new Country(9, "Russia", "ru"),
            new Country(10, "Spain", "es"),
            new Country(11, "United Kingdom", "gb")};

        companies = new String[]{"Benton, John B Jr", "Chanay, Jeffrey A Esq", "Chemel, James L Cpa", "Feltz Printing Service",
            "Printing Dimensions", "Chapman, Ross E Esq", "Morlong Associates", "Commercial Press", "Truhlar And Truhlar Attys",
            "King, Christopher A Esq", "Dorl, James J Esq", "Rangoni Of Florence", "Feiner Bros", "Buckley Miller Wright",
            "Rousseaux, Michael Esq"};

        representatives = new Representative[]{
            new Representative("Amy Elsner", "amyelsner.png"),
            new Representative("Anna Fali", "annafali.png"),
            new Representative("Asiya Javayant", "asiyajavayant.png"),
            new Representative("Bernardo Dominic", "bernardodominic.png"),
            new Representative("Elwin Sharvill", "elwinsharvill.png"),
            new Representative("Ioni Bowcher", "ionibowcher.png"),
            new Representative("Ivan Magalhaes", "ivanmagalhaes.png"),
            new Representative("Onyama Limba", "onyamalimba.png"),
            new Representative("Stephen Shaw", "stephenshaw.png"),
            new Representative("Xuxue Feng", "xuxuefeng.png")};

        names = new String[]{"James Butt", "David Darakjy", "Jeanfrancois Venere", "Ivar Paprocki", "Tony Foller",
            "Adams Morasca", "Claire Tollner", "Costa Dilliard", "Juan Wieser", "Maria Marrier", "Jennifer Amigon",
            "Stacey Maclead", "Leja Caldarera", "Morrow Ruta", "Arvin Albares", "Darci Poquette", "Izzy Garufi",
            "Ricardo Gaucho", "Clifford Rim", "Emily Whobrey", "Kadeem Flosi", "Mujtaba Nicka", "Aika Inouye",
            "Mayumi Kolmetz", "Misaki Royster", "Silvio Slusarski", "Nicolas Iturbide", "Antonio Caudy",
            "Deepesh Chui", "Aditya Kusko", "Aruna Figeroa", "Jones Vocelka", "Julie Stenseth", "Smith Glick",
            "Johnson Sergi", "Francesco Shinko", "Salvatore Stockham", "Kaitlin Ostrosky", "Faith Gillian",
            "Maisha Rulapaugh", "Jefferson Schemmer", "Leon Oldroyd", "Rodrigues Campain", "Alejandro Perin",
            "Munro Ferencz", "Cody Saylors", "Chavez Briddick", "Sinclair Waycott", "Isabel Bowley", "Octavia Malet",
            "Murillo Malet", "Greenwood Bolognia", "Wickens Nestle", "Ashley Doe"};
    }

    public List<Customer> getCustomers(int number) {
        List<Customer> customers = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            customers.add(
                    new Customer(i + 1000, getName(), getCompany(), getCountry(), getDate(),
                            CustomerStatus.random(), getActivity(), getRepresentative()));
        }
        return customers;
    }

    public List<Country> getCountries() {
        return Arrays.asList(countries);
    }

    public CustomerStatus[] getCustomerStatus() {
        return CustomerStatus.values();
    }

    public List<Representative> getRepresentatives() {
        return Arrays.asList(representatives);
    }

    private String getName() {
        return names[(int) (Math.random() * names.length)];
    }

    private Country getCountry() {
        return countries[(int) (Math.random() * countries.length)];
    }

    private String getCompany() {
        return companies[(int) (Math.random() * companies.length)];
    }

    private LocalDate getDate() {
        LocalDate now = LocalDate.now();
        long randomDay = ThreadLocalRandom.current().nextLong(now.minusDays(30).toEpochDay(), now.toEpochDay());
        return LocalDate.ofEpochDay(randomDay);
    }

    private int getActivity() {
        return (int) (Math.random() * 100);
    }

    private Representative getRepresentative() {
        return representatives[(int) (Math.random() * representatives.length)];
    }
}
