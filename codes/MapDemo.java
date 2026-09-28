
import java.util.*;
public class MapDemo{
    public static void main(String[] args){
        Map<Integer,Integer> hp=new LinkedHashMap<>();
        hp.put(10,100);
        hp.put(12,98);
        hp.put(10,98);
        hp.put(15,95);
        hp.put(20,90);

        hp.remove(12);

        for(Map.Entry<Integer,Integer> i:hp.entrySet()){
            System.out.println(i.getKey()+" "+i.getValue());
        }

        if(hp.containsKey(10)){
            System.out.println("Marks of RollNo " +hp.get(10));
        }else{
            System.out.println("Student not found ");
        }

        hp.put(10,56);

        for(Map.Entry<Integer,Integer> i:hp.entrySet()){
            System.out.println(i.getKey()+" "+i.getValue());
        }
    }
}

