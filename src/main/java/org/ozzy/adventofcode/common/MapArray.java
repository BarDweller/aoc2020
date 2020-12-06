package org.ozzy.adventofcode.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapArray {
    private static class Coords {
        int x;
        int y;
        public Coords(int x, int y){
            this.x=x;
            this.y=y;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coords coords = (Coords) o;
            return x == coords.x &&
                    y == coords.y;
        }
        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public static class Extents{
        public int minX;
        public int maxX;
        public int minY;
        public int maxY;
        public Extents(){
            minX=0;
            maxX=0;
            minY=0;
            maxY=0;
        }
    }
    private Extents extents = new Extents();
    private final Map<Coords,String> data;

    public MapArray(){
        data = new HashMap<>();
    }

    public String get(int x, int y){
        return data.get(new Coords(x,y));
    }

    public String put(int x, int y, String value){
        if(x>extents.maxX)extents.maxX=x;
        if(x<extents.minX)extents.minX=x;
        if(y>extents.maxY)extents.maxY=y;
        if(y<extents.minY)extents.minY=y;
        return data.put(new Coords(x,y), value);
    }

    public Extents getExtents(){
        return extents;
    }

}
