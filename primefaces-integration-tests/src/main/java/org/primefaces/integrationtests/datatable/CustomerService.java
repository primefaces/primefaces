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
package org.primefaces.integrationtests.datatable;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomerService {

    private Random random = new SecureRandom();
    private String[] representatives;
    private String[] firstNames;
    private String[] lastNames;
    private List<Customer> customers;

    @PostConstruct
    public void init() {
        representatives = new String[]{
            "Amy Elsner",
            "Anna Fali",
            "Asiya Javayant",
            "Bernardo Dominic",
            "Elwin Sharvill",
            "Ioni Bowcher",
            "Ivan Magalhaes",
            "Onyama Limba",
            "Stephen Shaw",
            "Xuxue Feng"};

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

        customers = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            customers.add(new Customer(i + 1000, getName(), representatives[i / 10]));
        }
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<String> getRepresentatives() {
        return Arrays.asList(representatives);
    }

    private String getName() {
        return firstNames[random.nextInt(firstNames.length)] + " "
                + (char) (random.nextInt(26) + 'A') + " "
                + lastNames[random.nextInt(lastNames.length)];
    }
}
