package pr.backgammon.jokers.model;

import pr.model.Mutable;

public class Joker implements Mutable<Joker> {
    public String name, desc;
    public double prob;
    public int pos, neg;
    
    public void set(Joker other) {
        name = other.name;
        desc = other.desc;
        prob = other.prob;
        pos = other.pos;
        neg = other.neg;
    }
}
