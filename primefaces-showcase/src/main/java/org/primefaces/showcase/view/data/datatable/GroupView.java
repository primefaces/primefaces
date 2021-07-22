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
package org.primefaces.showcase.view.data.datatable;

import javax.faces.view.ViewScoped;
import org.primefaces.showcase.domain.Player;
import org.primefaces.showcase.domain.Sale;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Named("dtGroupView")
@ViewScoped
public class GroupView implements Serializable {

    private static final String[] MANUFACTORS;
    private static final String[] PLAYER_NAMES;

    private List<Sale> sales;
    private Integer lastYearTotal;
    private Integer thisYearTotal;
    private List<Integer> years;
    private List<Player> players;

    static {
        MANUFACTORS = new String[10];
        MANUFACTORS[0] = "Bamboo Watch";
        MANUFACTORS[1] = "Black Watch";
        MANUFACTORS[2] = "Blue Band";
        MANUFACTORS[3] = "Blue T-Shirt";
        MANUFACTORS[4] = "Brown Purse";
        MANUFACTORS[5] = "Chakra Bracelet";
        MANUFACTORS[6] = "Galaxy Earrings";
        MANUFACTORS[7] = "Game Controller";
        MANUFACTORS[8] = "Gaming Set";
        MANUFACTORS[9] = "Gold Phone Case";

        PLAYER_NAMES = new String[10];
        PLAYER_NAMES[0] = "Lionel Messi";
        PLAYER_NAMES[1] = "Cristiano Ronaldo";
        PLAYER_NAMES[2] = "Arjen Robben";
        PLAYER_NAMES[3] = "Franck Ribery";
        PLAYER_NAMES[4] = "Ronaldinho";
        PLAYER_NAMES[5] = "Luis Suarez";
        PLAYER_NAMES[6] = "Sergio Aguero";
        PLAYER_NAMES[7] = "Zlatan Ibrahimovic";
        PLAYER_NAMES[8] = "Neymar Jr";
        PLAYER_NAMES[9] = "Andres Iniesta";
    }

    @PostConstruct
    public void init() {
        sales = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            sales.add(new Sale(MANUFACTORS[i], getRandomAmount(), getRandomAmount(), getRandomPercentage(), getRandomPercentage()));
        }

        years = new ArrayList<>();
        years.add(2010);
        years.add(2011);
        years.add(2012);
        years.add(2013);
        years.add(2014);

        players = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            players.add(new Player(PLAYER_NAMES[i], generateRandomGoalStatsData()));
        }
    }

    public List<Sale> getSales() {
        return sales;
    }

    private int getRandomAmount() {
        return (int) (Math.random() * 100000);
    }

    private int getRandomPercentage() {
        return (int) (Math.random() * 100);
    }

    public Integer getLastYearTotal() {
        if (lastYearTotal == null) {
            lastYearTotal = sales.stream().mapToInt(Sale::getLastYearSale).sum();
        }
        return lastYearTotal;
    }

    public Integer getThisYearTotal() {
        if (thisYearTotal == null) {
            thisYearTotal = sales.stream().mapToInt(Sale::getThisYearSale).sum();
        }
        return thisYearTotal;
    }

    public List<Integer> getYears() {
        return years;
    }

    public int getYearCount() {
        return years.size();
    }

    public List<Player> getPlayers() {
        return players;
    }

    private Map<Integer, Integer> generateRandomGoalStatsData() {
        Map<Integer, Integer> stats = new LinkedHashMap<>();
        for (int i = 0; i < 5; i++) {
            stats.put(years.get(i), getRandomGoals());
        }

        return stats;
    }

    private int getRandomGoals() {
        return (int) (Math.random() * 50);
    }
}
