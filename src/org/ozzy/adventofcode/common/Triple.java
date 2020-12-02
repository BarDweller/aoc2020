package org.ozzy.adventofcode.common;

public class Triple<T1, T2, T3> {
    public T1 a;
    public T2 b;
    public T3 c;
    public Triple(T1 a, T2 b, T3 c){
        this.a=a;
        this.b=b;
        this.c=c;
    }
    public Triple(Pair<T1,T2> p, T3 c){
        if(p!=null){
            this.a=p.a;
            this.b=p.b;
        }
        this.c=c;
    }
    public Triple(T1 a, Pair<T2,T3> p){
        if(p!=null){
            this.b=p.a;
            this.c=p.b;
        }
        this.a=a;
    }
}
