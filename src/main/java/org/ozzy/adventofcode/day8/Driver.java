package org.ozzy.adventofcode.day8;

import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Driver {
    public static class Engine {
        private static class Instruction {
            enum Operation { acc, jmp, nop }
            Operation op;
            int argument;
            public Instruction(String line){
                String[] parts = line.split(" ");
                argument = Integer.parseInt(parts[1]);
                switch(parts[0]) {
                    case "acc" : op = Operation.acc; break;
                    case "jmp" : op = Operation.jmp; break;
                    case "nop" : op = Operation.nop; break;
                    default: throw new IllegalArgumentException("BAD INSTRUCTION "+line);
                }
            }
        }

        long acc=0;
        List<Instruction> program;

        public Engine(Path input) throws Exception{
            program = Files.lines(input).map(Instruction::new).collect(Collectors.toList());
        }

        public long runUntilDupeInstruction() {
            acc=0;
            Set<Instruction> seen = new HashSet<>();
            Instruction current = program.get(0);
            while(current!=null){
                switch(current.op){
                    case acc: acc+=current.argument; //fall thru to nop
                    case nop: current=program.get(program.indexOf(current)+1); break;
                    case jmp: current=program.get(program.indexOf(current)+current.argument); break;
                }
                if(seen.contains(current)){
                    break;
                }else{
                   seen.add(current);
                }
            }
            return acc;
        }

        private void mutate(int mutantIndex){
            Instruction current = program.get(mutantIndex);
            switch(current.op) {
                case nop: current.op = Instruction.Operation.jmp; break;
                case jmp: current.op = Instruction.Operation.nop; break;
                default: //no-op.
            }
        }

        public long runUntilCouldHaveExitedWithDiffInstruction(){
            int mutantIndex=0;
            while(mutantIndex<program.size()) {
                if(program.get(mutantIndex).op == Instruction.Operation.acc){
                    //we don't need to mutate acc, so skip runs where our mutantIndex is an acc.
                    mutantIndex++;
                }else {
                    //mutate the instruction
                    mutate(mutantIndex);
                    try {
                        long result = runUntilDupeInstruction();
                    } catch (Exception e) {
                        //we'll trip an exception if we try to run outside the bounds of the program =)
                        break;
                    } finally {
                        //mutate it back to what it was.
                        mutate(mutantIndex);
                        mutantIndex++;
                    }
                }
            }
            return acc;
        }

    }

    public void part1(Path input) throws Exception {
        Engine e = new Engine(input);
        System.out.println("Part 1: "+e.runUntilDupeInstruction());
    }

    public void part2(Path input) throws Exception {
        Engine e = new Engine(input);
        System.out.println("Part 2: "+e.runUntilCouldHaveExitedWithDiffInstruction());
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day8/data.txt");
        part1(input);
        part2(input);
    }

    public static void main(String[] args) throws Exception {
        Driver d = new Driver();
    }
}
