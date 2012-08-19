package com.lingvapps.quizword;

public class Card {
    private String term;
    private String definition;
    
    public Card(String term, String definition) {
        setTerm(term);
        setDefinition(definition);
    }
    
    public String getTerm() {
        return term;
    }
    
    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
    
    public String toString() {
        return term + ": " + definition;
    }
}
