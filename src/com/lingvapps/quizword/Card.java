package com.lingvapps.quizword;

public class Card {
    private Integer id;
    private String term;
    private String definition;
    
    public Card(Integer id, String term, String definition) {
        this.id = id;
        setTerm(term);
        setDefinition(definition);
    }
    
    public Integer getId() {
        return id;
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
