package org.ozzy.adventofcode.common;

import java.util.*;

public class MapArray<T> implements Map<MapArray.Coords,T> {
    public static class Coords {
        public int x;
        public int y;
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
        @Override
        public String toString(){
            return "{"+x+","+y+"}";
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
    private final Map<Coords,T> data;

    public MapArray(){
        data = new HashMap<>();
    }

    public T get(int x, int y){
        return data.get(new Coords(x,y));
    }

    public T getOrDefault(int x, int y, T dfault) { return data.getOrDefault(new Coords(x,y),dfault); }

    public T put(int x, int y, T value){
        if(x>extents.maxX)extents.maxX=x;
        if(x<extents.minX)extents.minX=x;
        if(y>extents.maxY)extents.maxY=y;
        if(y<extents.minY)extents.minY=y;
        return data.put(new Coords(x,y), value);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    @Override
    public T get(Object key) {
        return data.get(key);
    }

    @Override
    public T put(Coords key, T value) {
        return put(key.x, key.y, value);
    }

    @Override
    public T remove(Object key) {
        return data.remove(key);
    }

    @Override
    public void putAll(Map<? extends Coords, ? extends T> m) {
        m.forEach((key, value) -> put(key.x, key.y, value));
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public Set<Coords> keySet() {
        return data.keySet();
    }

    public Collection<T> values(){
        return data.values();
    }

    public Collection<Coords> keys(){
        return data.keySet();
    }

    public Set<Map.Entry<Coords, T>> entrySet(){
        return data.entrySet();
    }

    public Extents getExtents(){
        return extents;
    }

    public String toString() {
        return data.toString();
    }


}
