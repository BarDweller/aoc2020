package org.ozzy.adventofcode.common;

import java.util.Arrays;

public class AppendableMapArray extends MapArray {
    int xoffset=0;
    int yoffset=0;
    public AppendableMapArray() {
        super();
    }

    public void appendToRow(String value){
        put(xoffset,yoffset,value);
        xoffset++;
    }

    public void newRow(){
        yoffset++;
        xoffset=0;
    }

    public void appendRow(String... values){
        Arrays.stream(values).forEach(this::appendToRow);
        newRow();
    }
}
