package mpg.biochem.de.interbase.service;

import java.math.BigInteger;
import java.util.Calendar;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int[] arr = new int[10];
		for(int i=0; i<10; i++)
			arr[i] = i;
		//quickSort(arr, 0, arr.length-1);
		//print(arr);
		
		Calendar x = Calendar.getInstance();
		System.out.println(linearSearch(arr, 1000));
		System.out.println(Calendar.getInstance().getTimeInMillis()-x.getTimeInMillis());
		
		x = Calendar.getInstance();
		System.out.println(binarySearch(arr, 5, 0, arr.length-1));
		System.out.println(Calendar.getInstance().getTimeInMillis()-x.getTimeInMillis());
		//System.out.println(binarySearch(arr, 9));
	}
	
	public static boolean binarySearch(int[] arr, int target, int low, int high){
		System.out.println(low+", "+high);
		if(low == high){
			if(arr[low] == target)
				return true;
		}else{
			int pivot = low + (high-low)/2;
			System.out.println(pivot);
			
			if(arr[pivot]>target)
				return binarySearch(arr, target, low, pivot-1);
			else
				return binarySearch(arr, target, pivot, high);
				
		}
		return false;
	}
	
	public static boolean linearSearch(int[] arr, int n){
		for(int x : arr)
			if(x==n)
				return true;
		
		return false;
	}
	
	public static void quickSort(int[] arr, int low, int high){
		if(high-low<=0)
			return;
		
		int pivot = arr[low + (high-low)/2];//arr[new Random().nextInt(high)-low];
		
		int i=low, j=high;
		while(i<j){
			
			while(arr[i]<pivot)
				i++;
			
			while(arr[j]>pivot)
				j--;
			
			int tmp = arr[i];
			arr[i] = arr[j];
			arr[j] = tmp;
		}
		quickSort(arr, low, j-1);
		quickSort(arr, j+1, high);
		
	}
	
	public static void print(int[] a){
		String s = "";
		for(int i : a){
			s = s+i+", ";
		}
		System.out.println(s);
	}
	
	public static int trianglular(int n){
		return n*(n+1)/2;
	}
	
	public static int getFactorsCount(int n){
		int c=0;
		for(int i=1; i<=n; i++)
			if(n%i ==0)
				c++;
		return c;		
	}
	
	public static int collatz(int n){
		return collatz(n, 0);
	}
	
	private static int collatz(int n, int c){
		
		if(n==1){
			c=c+1;
		}else if(n%2==0){
			c=collatz(n/2, c+1);
		}else{
			c=collatz(3*n+1, c+1);
		}
		return c;
	}

	public static void problem8(){
		
		String str = "73167176531330624919225119674426574742355349194934"+
				 "96983520312774506326239578318016984801869478851843"+
				 "85861560789112949495459501737958331952853208805511"+
				 "12540698747158523863050715693290963295227443043557"+
				 "66896648950445244523161731856403098711121722383113"+
				 "62229893423380308135336276614282806444486645238749"+
				 "30358907296290491560440772390713810515859307960866"+
				 "70172427121883998797908792274921901699720888093776"+
				 "65727333001053367881220235421809751254540594752243"+
				 "52584907711670556013604839586446706324415722155397"+
				 "53697817977846174064955149290862569321978468622482"+
				 "83972241375657056057490261407972968652414535100474"+
				 "82166370484403199890008895243450658541227588666881"+
				 "16427171479924442928230863465674813919123162824586"+
				 "17866458359124566529476545682848912883142607690042"+
				 "24219022671055626321111109370544217506941658960408"+
				 "07198403850962455444362981230987879927244284909188"+
				 "84580156166097919133875499200524063689912560717606"+
				 "05886116467109405077541002256983155200055935729725"+
				 "71636269561882670428252483600823257530420752963450";
	
	
		int windowSize = 5, product = 0;
		
		for(int i=0; i<=str.length()-5; i++){
			char[] arr = str.substring(i, i+windowSize).toCharArray();
			int x = Integer.parseInt(arr[0]+"");
			for(int j=1; j<arr.length; j++)
				x = x*Integer.parseInt(arr[j]+"");
			
			if(x>product)
				product = x;
		}
		System.out.println(product);
	}
	
	public static BigInteger factorial(BigInteger n){
		if(n.equals(BigInteger.ONE))
			return BigInteger.ONE;
		return n.multiply(factorial(n.subtract(BigInteger.ONE)));//*factorial(n-1);
	}
	
	public static boolean isPrime(long n){
		for(int i=2; i<=Math.pow(n,0.5); i++) {
			if(n%i==0)
				return false;
		}	
		return true;
	}
	
}
