
public class HelloNumbers {
    public static void main(String[] args) {
        int x = 0;
        
		while (x < 10) {
			int y = 0;
        	for(int i = 0; i <= x ; i++){
        		y += i;
        	}
        	System.out.print(y + " ");
            x = x + 1;
        }   
    }
}

