package com.example.bcast.utils;

public class Pair<F, S, T, G, H> {
    private F first; //first member of pair
    private S second; //second member of pair
    private T third;
    private G fourth;
    private H fifth;

//    public static Pair<T,U> of<T,U> (T first, U second);
    
    public Pair(F first, S second, T third, G fourth, H fifth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.fourth = fourth;
        this.fifth = fifth;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public void setSecond(S second) {
        this.second = second;
    }
    
    public void setThird(T third) {
    	this.third = third;
    }
    
    public void setFourth(G fourth) {
    	this.fourth = fourth;
    }
    
    public void setFifth(H fifth) {
    	this.fifth = fifth;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }
    
    public T getThird() {
    	return third;
    }
    
    public G getFourth() {
    	return fourth;
    }
    
    public H getFifth() {
    	return fifth;
    }
}