package org.ozzy.adventofcode.day18;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

public class Driver {

    private static class Expression {

        enum Operator { ADD, MULTIPLY }
        List<Object> chain =  new LinkedList<>();

        public Expression(LinkedList<Integer> data){

            //convert the list of chars into a list of objects
            //where the objects are
            //  String (numbers)
            //  Operator (operator instance)
            //  Expression (nested parentheses)

            StringBuilder sb = new StringBuilder();
            while(!data.isEmpty()){
                int current = data.removeFirst();
                if(current == '('){
                    //start of nested chain
                    chain.add(new Expression(data));
                }
                if(current == ')' || current==' '){
                    //termination of chain, or current value
                    if(sb.length()!=0){
                        chain.add(sb.toString());
                        sb = new StringBuilder();
                    }
                    //if chain ends, we're done here.
                    if(current==')') return;
                }
                if(current == '*'){
                    chain.add(Operator.MULTIPLY);
                }
                if(current == '+'){
                    chain.add(Operator.ADD);
                }
                if(current >= '0' && current <= '9'){
                    sb.append((char)current);
                }
            }
            //handle terminal values in string, that terminate due to eol.
            if(sb.length()!=0){
                chain.add(sb.toString());
            }
        }

        long evaluate(){
            Long lhs = null;
            Operator op = null;
            Long rhs = null;

            //process the stored chain of tokens according the puzzle logic =)
            LinkedList<Object> eatMe = new LinkedList<>(chain);
            while(!eatMe.isEmpty()){
                Object element = eatMe.removeFirst();
                if(lhs==null){
                    //no lhs? we're at the start =)
                    if(element instanceof Expression){
                        lhs = ((Expression)element).evaluate();
                    }else
                    if(element instanceof String){
                        lhs = Long.valueOf(element.toString());
                    }else {
                        //won't happen with puzzle input, but doesn't hurt =)
                        throw new IllegalStateException("can't start chain with "+element.getClass());
                    }
                }else if(op==null){
                    //we have an lhs? then we're expecting an operator.
                    if(element instanceof Operator){
                        op = (Operator)element;
                    }else{
                        //won't happen with puzzle input, but doesn't hurt =)
                        throw new IllegalStateException("Cannot join chains with "+element.getClass());
                    }
                }else{
                    //got lhs and operator, then we're expecting something usable as a rhs
                    if(element instanceof Expression){
                        rhs = ((Expression)element).evaluate();
                    }else
                    if(element instanceof String){
                        rhs = Long.valueOf(element.toString());
                    }else {
                        //won't happen with puzzle input, but doesn't hurt =)
                        throw new IllegalStateException("can't have rhs with "+element.getClass());
                    }
                }
                //got the lhs/op/rhs? since we're doing left to right, combine them now
                //(eg, 1+2+3+4 becomes 3+3+4 then 6+4 as the loop eats the eatMe chain)
                if(lhs!=null && op!=null && rhs!=null){
                    switch(op){
                        case ADD: lhs = lhs + rhs; break;
                        case MULTIPLY: lhs = lhs * rhs; break;
                    }
                    op = null; rhs = null;
                }
            }
            //also won't happen with test data, but handle it anyway.
            if(op!=null)throw new IllegalStateException("Trailing operator found in expression");

            //answer is sitting in the lhs value =)
            return lhs;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            chain.forEach(o -> {
                if(o instanceof String) sb.append(o);
                if(o instanceof Operator) {
                    if ((o == Operator.MULTIPLY)) {
                        sb.append(" * ");
                    } else {
                        sb.append(" + ");
                    }
                }
                if(o instanceof Expression){
                    sb.append("(");
                    sb.append(o.toString());
                    sb.append(")");
                }
            });
            return sb.toString();
        }
    }

    public void part1(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();

        AtomicLong total = new AtomicLong(0);
        data.forEach(line -> {
            LinkedList<Integer> ll = new LinkedList<>(StreamEx.of(line.chars().boxed()).toList());
            Expression e = new Expression(ll);
            total.addAndGet(e.evaluate());
        });


        System.out.println("Part 1: "+total.get());
    }

    public void part2(Path input) throws Exception {
        List<String> data = StreamEx.ofLines(input).toList();

        AtomicLong total = new AtomicLong(0);
        data.forEach(line -> {
            //why rewrite part1, when you can rewrite the expression! =)
            line = "("+line.replace("(","((").replace(")","))").replace("*", ")*(")+")";
            LinkedList<Integer> ll = new LinkedList<>(StreamEx.of(line.chars().boxed()).toList());
            Expression e = new Expression(ll);
            total.addAndGet(e.evaluate());
        });

        System.out.println("Part 2: "+total.get());
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day18/data.txt");
        part1(input);
        part2(input);
    }


    public static void main(String[] args) throws Exception {
        long time = System.currentTimeMillis();
        Driver d = new Driver();
        System.out.println("Elapsed : "+(System.currentTimeMillis()-time));
    }
}



