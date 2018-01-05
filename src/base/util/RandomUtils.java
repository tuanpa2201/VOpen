package base.util;

import java.security.SecureRandom;
import java.util.Random;
import java.math.BigInteger;

public class RandomUtils {

    public static String randomNumberString() {
		SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
    
    private static final char[] symbols;
    
    static {
        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ch++) {
            tmp.append(ch);
        }
        for (char ch = 'a'; ch <= 'z'; ch++) {
            tmp.append(ch);
        }
        symbols = tmp.toString().toCharArray();
    }
    
    public static String randomString(int length) {
    	if (length < 1) {
    		return "Fuck you !!!";
    	}
    	Random random = new Random();
    	char[] buf;
    	buf = new char[length];
        for (int i = 0; i < buf.length; i++) {
            buf[i] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }
}
