import java.util.*;
import java.math.*;

public class RSA {
    //Variables used by itself to decrypt and send public key to other side
    public static BigInteger n;
    public static BigInteger e;
    private static BigInteger d;
    private static BigInteger m;
    
    //Variables received from the other side for encryption
    public static BigInteger foreignN;
    public static BigInteger foreignE;
    
    //Security Settings
    private static int securityLevel = 1024;
    
    //Returns message as an encrypted string using foreignE and foreignN
    public static String encrypt(String message) {
        //Convert string to base2
        StringBuilder result = new StringBuilder();
        char[] mChars = message.toCharArray();
        for(char aChar : mChars) {
            result.append(String.format("%8s", Integer.toBinaryString(aChar)).replaceAll(" ", "0"));
        }
        
        //Convert stringBuilder to string.
        String convertedMessage = result.toString();
        
        //Encrypt m according to RSA equation 
        m = new BigInteger(convertedMessage);
        m = m.modPow(foreignE,foreignN);
        return m.toString();
    }
    
    public static String readMessage(String key) {
        String mString = "";
        
        //Sanitize against line breaks.
        key = key.replaceAll("\\r|\\n", "");
        
        //Import encrypted numbers to m, decrypt back to binary, set mString to m decrypted value.
        m = new BigInteger(key);
        m = m.modPow(d,n);
        mString = m.toString();
        //System.out.println(mString); //remove later
        
        //Add 0 to beginning of mString if it is not divisible by 8
        if(mString.length()%8 != 0) {
            mString = "0" + mString;
        }
                
        //String Builder to convert from binary string to text string.
        String finalMessage = "";
        for(int i=0; i < mString.length() - 7; i+=8) {
            //System.out.println(mString.substring(i, Math.min(i+8, mString.length()))); -> displays each byte/character
            finalMessage += new Character((char) Integer.parseInt(mString.substring(i, Math.min(i+8, mString.length())),2) ).toString();
        }
        
        //System.out.println("Decrypted Message: " + finalMessage);
        return finalMessage;
    }
    
    //Method used to generate both public and private keys.
    public static void genKeys(int security) {
        //Define new Random rd and assign two large prime values to p and q.
        Random rd = new Random();
        securityLevel = security;
        
        BigInteger p = BigInteger.probablePrime(securityLevel,rd);
        BigInteger q = BigInteger.probablePrime(securityLevel+256,rd);
        
        //Define modulus for keys, n. n is released as a part of the *public* key.
        n = p.multiply(q);
        
        //Calculate the Carmichael's Totient for n. Defined as lcm(p-1,q-1). This is kept *private*.
        BigInteger psub = p.subtract(BigInteger.valueOf(1));
        BigInteger qsub = q.subtract(BigInteger.valueOf(1));
        
        //Totient function uses p-1 and q-1.
        BigInteger cTotient = (psub.multiply(qsub)).divide(psub.gcd(qsub));
        
        //Define int e which is 1 < e < cTotient and coprime with cTotient. Should be reatively small for efficiency.
        //Released as part of the *public* key. 65537 is small enough to be efficient but large enough to not be easily breakable.
        e = BigInteger.valueOf(65537);
        
        //Calculate d - the modular multiplicative inverse of e modulo totient n. D is kept secret as a part of the *private* key exponent.
        d = e.modInverse(cTotient);
    }
}
