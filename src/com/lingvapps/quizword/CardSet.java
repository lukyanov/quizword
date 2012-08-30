package com.lingvapps.quizword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class CardSet implements Iterable<Card> {
    
    private String name;
    private Integer id;
    private Integer termCount = 0;
    private ArrayList<Card> cards;
    
    public CardSet(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.termCount = 0;
        this.cards = new ArrayList<Card>();
    }
    
    public CardSet(Integer id, String name, Integer termCount) {
        this.id = id;
        this.name = name;
        this.termCount = termCount;
        this.cards = new ArrayList<Card>();
    }
    
    public Iterator<Card> iterator() {        
        Iterator<Card> i = cards.iterator();
        return i; 
    }
    
    public void addCard(Card card) {
        this.cards.add(card);
        this.termCount = this.cards.size();
    }
    
    public void removeCard(Card card) {
        this.cards.remove(card);
        this.termCount = this.cards.size();
    }
    
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    
    public Integer getCardsCount() {
        return termCount;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String toString() {
        return name + " (" + getCardsCount().toString() + ")";
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }
}
