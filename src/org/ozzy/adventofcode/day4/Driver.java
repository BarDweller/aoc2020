package org.ozzy.adventofcode.day4;

import org.ozzy.adventofcode.common.AppendableMapArray;
import org.ozzy.adventofcode.common.FileReader;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Driver {

    private static class Passport {
        String byr;
        String iyr;
        String eyr;
        String hgt;
        String hcl;
        String ecl;
        String pid;
        String cid;

        private void addField(String field){
            String []parts = field.split(":");
            switch(parts[0]){
                case "byr": { byr=parts[1]; break;}
                case "iyr": { iyr=parts[1]; break;}
                case "eyr": { eyr=parts[1]; break;}
                case "hgt": { hgt=parts[1]; break;}
                case "hcl": { hcl=parts[1]; break;}
                case "ecl": { ecl=parts[1]; break;}
                case "pid": { pid=parts[1]; break;}
                case "cid": { cid=parts[1]; break;}
                default: { throw new IllegalArgumentException("Unknown field declaration "+field); }
            }
        }
        private void addData(String data){
            Arrays.stream(data.split(" ")).forEach(this::addField);
        }

        public Passport(String data){
            addData(data);
        }
        public void addDataToPassport(String data){
            addData(data);
        }

        public boolean isValidPart1Rules(){
            return byr!=null && iyr!=null && eyr!=null && hgt!=null && hcl!=null && ecl!=null && pid!=null;
        }

        private boolean validateNumber(String value, int min, int max){
            if(value.matches("(\\d)+")){
                int val = Integer.parseInt(value);
                return val>=min && val<=max;
            } else return false;
        }
        private boolean validateYear(String value, int min, int max){
            if(value.matches("\\d\\d\\d\\d")){
                return validateNumber(value, min, max);
            }else return false;
        }
        private boolean validateBirthYear(){
            return validateYear(byr, 1920, 2002);
        }
        private boolean validateIssueYear(){
            return validateYear(iyr, 2010, 2020);
        }
        private boolean validateExpirationYear(){
            return validateYear(eyr, 2020, 2030);
        }
        private boolean validateHeight(){
            if(hgt.endsWith("cm")){
                return validateNumber(hgt.substring(0,hgt.length()-2), 150,193);
            }else if(hgt.endsWith("in")){
                return validateNumber(hgt.substring(0,hgt.length()-2), 59,76);
            }else return false;
        }
        private boolean validateHairColor(){
            return hcl.matches("#[0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f][0-9a-f]");
        }
        private boolean validateEyeColor(){
            return Arrays.asList("amb blu brn gry grn hzl oth".split(" ")).contains(ecl);
        }
        private boolean validatePassportId(){
            return pid.matches("\\d\\d\\d\\d\\d\\d\\d\\d\\d");
        }
        public boolean isValidPart2Rules(){
            //easy way to avoid null issues ;)
            if(isValidPart1Rules()){
                return validateBirthYear()
                        && validateIssueYear()
                        && validateExpirationYear()
                        && validateHeight()
                        && validateHairColor()
                        && validateEyeColor()
                        && validatePassportId();
            }else return false;
        }
    }

    public Driver() throws Exception {
        Path input = FileReader.getPathForClassPathResource("org/ozzy/adventofcode/day4/data.txt");
        List<String> lines = FileReader.getFileAsListOfString(input);

        //read the passports in
        List<Passport> passports = new ArrayList<>();
        Passport current = null;
        for(String s : lines){
            if(!s.isEmpty()){
                if(current==null) current = new Passport(s);
                else current.addDataToPassport(s);
            }else{
                passports.add(current);
                current=null;
            }
        }
        if(current!=null){ passports.add(current); }

        //validate passports.
        System.out.println("Part 1: "+passports.stream().filter(Passport::isValidPart1Rules).count());
        System.out.println("Part 2: "+passports.stream().filter(Passport::isValidPart2Rules).count());
    }

    public static void main(String []args) throws Exception {
        Driver d = new Driver();
    }
}
