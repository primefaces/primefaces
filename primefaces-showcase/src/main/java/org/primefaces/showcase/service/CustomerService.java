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
package org.primefaces.showcase.service;

import org.primefaces.showcase.domain.Country;
import org.primefaces.showcase.domain.Customer;
import org.primefaces.showcase.domain.CustomerStatus;
import org.primefaces.showcase.domain.Representative;
import org.primefaces.util.Constants;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named
@ApplicationScoped
public class CustomerService {

    private Random random = new SecureRandom();
    private Country[] countries;
    private Representative[] representatives;
    private String[] firstNames;
    private String[] lastNames;
    private String[] companies;

    @PostConstruct
    public void init() {
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

        firstNames = new String[]{"James", "David", "Jeanfrancois", "Ivar", "Tony",
            "Adams", "Claire", "Costa", "Juan", "Maria", "Jennifer",
            "Stacey", "Leja", "Morrow", "Arvin", "Darci", "Izzy",
            "Ricardo", "Clifford", "Emily", "Kadeem", "Mujtaba", "Aika",
            "Mayumi", "Misaki", "Silvio", "Nicolas", "Antonio",
            "Deepesh", "Aditya", "Aruna", "Jones", "Julie", "Smith",
            "Johnson", "Francesco", "Salvatore", "Kaitlin", "Faith",
            "Maisha", "Jefferson", "Leon", "Rodrigues", "Alejandro",
            "Munro", "Cody", "Chavez", "Sinclair", "Isabel", "Octavia",
            "Murillo", "Greenwood", "Wickens", "Ashley"};
        lastNames = new String[]{"Butt", "Darakjy", "Venere", "Paprocki", "Foller",
            "Morasca", "Tollner", "Dilliard", "Wieser", "Marrier", "Amigon",
            "Maclead", "Caldarera", "Ruta", "Albares", "Poquette", "Garufi",
            "Gaucho", "Rim", "Whobrey", "Flosi", "Nicka", "Inouye",
            "Kolmetz", "Royster", "Slusarski", "Iturbide", "Caudy",
            "Chui", "Kusko", "Figeroa", "Vocelka", "Stenseth", "Glick",
            "Sergi", "Shinko", "Stockham", "Ostrosky", "Gillian",
            "Rulapaugh", "Schemmer", "Oldroyd", "Campain", "Perin",
            "Ferencz", "Saylors", "Briddick", "Waycott", "Bowley", "Malet",
            "Malet", "Bolognia", "Nestle", "Doe"};
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
        return firstNames[random.nextInt(firstNames.length)] + Constants.SPACE
                + (char) (random.nextInt(26) + 'A') + Constants.SPACE
                + lastNames[random.nextInt(lastNames.length)];
    }

    private Country getCountry() {
        return countries[random.nextInt(countries.length)];
    }

    private String getCompany() {
        return companies[random.nextInt(companies.length)];
    }

    private LocalDate getDate() {
        LocalDate now = LocalDate.now();
        long randomDay = ThreadLocalRandom.current().nextLong(now.minusDays(30).toEpochDay(), now.toEpochDay());
        return LocalDate.ofEpochDay(randomDay);
    }

    private int getActivity() {
        return random.nextInt(100);
    }

    private Representative getRepresentative() {
        return representatives[random.nextInt(representatives.length)];
    }
}
