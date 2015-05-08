import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BigInteger
{
    public static final String QUIT_COMMAND = "quit";
    public static final String MSG_INVALID_INPUT = "입력이 잘못되었습니다.";

    // implement this
    public static final Pattern EXPRESSION_PATTERN = Pattern.compile("([+-]?\\d+)([\\\\*+-])([+-]?\\d+)");

    public static final int MAX_LENGTH = 201;
    
    /**
     * The number area of integer */
    int[] number;
    /**
     * The sign of number<br>
     * <code>true</code> if whole number else <code>false</code> */
    int sign;
    /**
     * The length of number */
    int length;
    
    public BigInteger(int i){
    	if (i >= 0){
    		sign = 1;
    	} else {
    		sign = -1;
    		i *= -1;
    	}
    	number = new int[MAX_LENGTH];
    	length = 0;
    	for (int digit = 0; (int)(i / Math.pow(10, digit)) != 0; digit++){
    		number[digit] = (int)(i / Math.pow(10, digit)) % 10;
    		length++;
    	}
    }

    public BigInteger(int[] num1){
    	sign = 1;
    	number = new int[MAX_LENGTH];
    	length = 0;
    	for (int i = 0; num1.length > 0 && i < num1.length; i++){
    		number[num1.length - i - 1] = num1[i];
    		length++;
    	}
    }

    public BigInteger(String s){
    	sign = 1;
    	if (s.contains("+")) {
    		s = s.substring(1, s.length());
    	} else if (s.contains("-")) {
    		s = s.substring(1, s.length());
    		sign = -1;
    	}
    	s = s.replaceAll("^0+", "");
    	number = new int[MAX_LENGTH];
    	length = 0;
    	for (int i = 0; s.length() > 0 && i < s.length(); i++){
    		number[s.length() - i -1] = Character.getNumericValue(s.charAt(i));
    		length++;
    	}
    }
    
    private static void carryUp(int[] number, int maxDigit){
    	for (int i = 0; i < maxDigit; i++){
    		int carry = (int)Math.floor(number[i] / 10.0);
    		number[i + 1] += carry;
    		number[i] -= carry * 10;
    	}
    }
    
    private static String toStringFromArray(int[] number, int sign){
    	String result = sign < 0 ? "-" : "";
    	for (int i = 0; i < number.length; i++){
    		result += Integer.toString(number[number.length - i - 1]);
    	}
    	return result;
    }

    public BigInteger add(BigInteger big){
    	int[] resultNumber = new int[MAX_LENGTH];
    	int resultSign = 1;
    	final int maxDigit = Math.max(this.length, big.length);
    	
    	for (int i = 0; i < maxDigit; i++){
    		resultNumber[i] = this.sign * this.number[i] + big.sign * big.number[i];
    	}
    	carryUp(resultNumber, maxDigit);
    	if (resultNumber[maxDigit] < 0){
    		// If it has a negative value
    		resultSign = -1;
    		resultNumber[maxDigit] = -resultNumber[maxDigit] - 1;
    		for (int i = 0; i < maxDigit; i++){
    			resultNumber[i] = 9 - resultNumber[i];
    		}
        	for (int i = 0; (resultNumber[i] += 1) >= 10; resultNumber[i] -= 10, i++);
    	}
    	/*	//print debuging
    	for (int i = 0; i <= maxDigit; i++)
    		System.out.print(resultNumber[i] + " ");
    	System.out.println("");
    	*/
    	return new BigInteger(toStringFromArray(resultNumber, resultSign));
    }

    public BigInteger subtract(BigInteger big){
    	big.sign *= -1;
    	return this.add(big);
    }

    public BigInteger multiply(BigInteger big){
    	int[] resultNumber = new int[MAX_LENGTH];
    	int resultSign = this.sign * big.sign;
    	final int maxDigit = this.length + big.length;
    	for (int i = 0; i < this.length; i++){
        	for (int j = 0; j < big.length; j++){
        		resultNumber[i + j] += this.number[i] * big.number[j];
        	}
    	}
    	carryUp(resultNumber, maxDigit);
    	return new BigInteger(toStringFromArray(resultNumber, resultSign));
    }

    @Override
    public String toString(){
    	String s = "";
    	if (length != 0 && sign == -1){
    		s += "-";
    	}
    	for (int i = 0; i < length; i++){
    		s += Integer.toString(number[length - i - 1]);
    	}
    	if (length == 0){
    		s = "0";
    	}
    	return s;
    }

    static BigInteger evaluate(String input) throws IllegalArgumentException, IllegalStateException {
    	Matcher m = EXPRESSION_PATTERN.matcher(input.replaceAll("\\s+", ""));
    	m.matches();
        BigInteger num1 = new BigInteger(m.group(1));
        BigInteger num2 = new BigInteger(m.group(3));
        BigInteger result;
        switch (m.group(2)){
        case "+":
        	result = num1.add(num2);
        	break;
        case "-":
        	result = num1.subtract(num2);
        	break;
        case "*":
        	result = num1.multiply(num2);
        	break;
        default:
        	result = new BigInteger(0);
        }
    	return result;
    }

    public static void main(String[] args) throws Exception {
        try (InputStreamReader isr = new InputStreamReader(System.in))
        {
            try (BufferedReader reader = new BufferedReader(isr))
            {
                boolean done = false;
                while (!done)
                {
                    String input = reader.readLine();
 
                    try
                    {
                        done = processInput(input);
                    }
                    catch (IllegalArgumentException e)
                    {
                        System.err.println(MSG_INVALID_INPUT);
                    }
                    catch (IllegalStateException e)
                    {
                        System.err.println(MSG_INVALID_INPUT);
                    }
                }
            }
        }
    }

    static boolean processInput(String input) throws IllegalArgumentException {
        boolean quit = isQuitCmd(input);
 
        if (quit)
        {
            return true;
        }
        else
        {
            BigInteger result = evaluate(input);
            System.out.println(result.toString());
 
            return false;
        }
    }

    static boolean isQuitCmd(String input){
        return input.equalsIgnoreCase(QUIT_COMMAND);
    }
}