package rs.edu.raf.banka1.utils;

import java.util.Random;


public class RandomUtil {

    private static final Random random = new Random();

    public RandomUtil(){
    }

    public static Long returnNextLong(Long upperBound){
        return random.nextLong(upperBound);
    }

}
