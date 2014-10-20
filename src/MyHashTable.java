/*********************************************************
 * 
 * 95-772 Data Structures for Application Programmers
 * Homework 4 HashTable Implementation
 * 
 * Andrew ID: gnayak
 * Name: Geetish Nayak	
 * 
 *********************************************************/

public class MyHashTable implements MyHTInterface {

	// Initialization of variables
	private DataItem[] hashArray;
	private int arraySize;
	private DataItem nonItem;
	private double loadfactor;
	
	// Creation of DataItem which will basically contain the
	// value and the frequency
	private static class DataItem{
		private String value;
		private int frequency;
		
		// Constructor
		public DataItem(String value, int frequency){
			this.value = value;
			this.frequency =frequency;
		}
	}
	
	// Constructor
	// This constructor is used to initialize the size of the
	// Hash map
	MyHashTable(int initialSize){
		loadfactor = 0.5;
		arraySize = initialSize;
		hashArray = new DataItem[arraySize];
		nonItem = new DataItem("#DEL#",-1);
		
	}
	
	// Constructor
	// This constructor gives a default size
	MyHashTable(){
		loadfactor = 0.5;
		arraySize = 10;
		hashArray = new DataItem[arraySize];
		nonItem = new DataItem("#DEL#",-1);	
	}
	
	
	/* Function to get the next prime numb
	 * to the given number
	 * @param n the number of whom we we want to 
	 *          find the next prime number
	 */
	int nextPrime(int n){
		while(true){
			int i=0;
			// for all the numbers from 2 to n-1 check
			for(i=2;i<n;i++){
				// n is divisible 
				if(n%i==0){
					break;
				}
			}
			if(i==n){ // This means that n is not divisible by any of the numbers from 2 
				return n;//to n-1
			}
			else{
				n=n+1;
			}
		}
	}
	
	/*
	 * 
	 * (non-Javadoc)
	 * @see MyHTInterface#insert(java.lang.String)
	 */
	@Override
	public void insert(String value) {
		DataItem item = new DataItem(value,1);
		int hashVal = hashFunc(value);
		int flag=0;
		// If the position is not filled with null or node with value #DEL#
		while(hashArray[hashVal]!=null && !hashArray[hashVal].equals("#DEL#")){
			// If the values of the 2 keys are same
			if(hashArray[hashVal].value.equals(value)){
				// increment the frequency by 1
				hashArray[hashVal].frequency = hashArray[hashVal].frequency+1;
				flag=1;
				break;
			}
			else{
				// increment hashVal
				hashVal++;
				hashVal = hashVal%arraySize;
			}
		}
		if(flag==0){
			// If no matching key that means its a new key and thus 
			// just add it into the Hash
			hashArray[hashVal] = item;
		}
		
		// Now we have to check if rehashing is required
		// The function size will help us in this case
		double sizeOfMap = this.size();
		double lengthOfMap = hashArray.length;
		double currLoadFactorinArray = sizeOfMap/lengthOfMap;
		//System.out.println("*****"+currLoadFactorinArray);
		if(currLoadFactorinArray> 0.5){
			//System.out.println("GOing Inside");
			rehash();
		}
		
	}

	@Override
	public int size() {
		int count = 0;
		for(int i=0;i<arraySize;i++){
			if(hashArray[i]!=null && !hashArray[i].value.equals("#DEL#")){
				count++;
			}
		}
		return count;
	}

	@Override
	public void display() {
		for(int i=0;i<arraySize;i++){
			if(hashArray[i]==null){
				System.out.print("**\t");
			}
			else if(hashArray[i].value.equals("#DEL#")){
				System.out.print("\t\t"+ "#DEL#" + "\t\t");
			}
			else if(hashArray[i].value!="#DEL#"){
				System.out.print("[ "+hashArray[i].value + " , " + hashArray[i].frequency  + " ] ");
			}
		}
		System.out.println("");
	}

	@Override
	public boolean contains(String key) {
		int hashVal = hashFunc(key);
		while(hashArray[hashVal]!=null){
			if(hashArray[hashVal].value == key){
				return true;
			}
			hashVal++;
			hashVal = hashVal % arraySize;
		}
		return false;
	}

	@Override
	public int numOfCollisions() {
		int collision = 0;
		for(int i=0;i<arraySize;i++){
			if(hashArray[i]!=null && !hashArray[i].value.equals("#DEL#")){
				int hashValofI= hashFunc(hashArray[i].value);
				for(int j=i+1;j<arraySize;j++){
					if(hashArray[j]!=null && !hashArray[j].value.equals("#DEL#")){
						int hashValofJ=hashFunc(hashArray[j].value);
						if(hashValofI==hashValofJ){
							collision++;
						}
					}
				}
			}
		}
		return collision;
	}

	@Override
	public int hashValue(String value) {
		return hashFunc(value);
	}

	@Override
	public int showFrequency(String key) {
		// Get the hash value for the key
		int hashVal =  hashFunc(key);
		while(hashArray[hashVal]!=null && !hashArray[hashVal].value.equals("#DEL#")){
			if(hashArray[hashVal].value.equals(key)){
				return hashArray[hashVal].frequency;
			}
			else{
				hashVal++;
				hashVal = hashVal % arraySize;
			}
		}
		return 0;
		
	}

	@Override
	public String remove(String key) {
		int hashVal = hashFunc(key);
		// loop through as long as its not empty
		while(hashArray[hashVal]!=null){
			if(hashArray[hashVal].value.equals(key)){
				DataItem tmp= hashArray[hashVal];
				hashArray[hashVal]=nonItem;
				return tmp.value;
			}
			hashVal++;
			hashVal = hashVal%arraySize;
		}
		return null;
	}

	/*
	 * Instead of using String's hashCode, you are to implement your own here,
	 * taking the table length into your account.
	 * 
	 * Helper method to hash a string For English lowercase alphabet and blank,
	 * we have 27 total. For example, "cats" : 3*27^3 + 1*27^2 + 20*27^1 +
	 * 19*27^0 = 60,337
	 * 
	 * But, to make the hash process faster, Horner's method should be applied
	 * as follows; var4*n^4 + var3*n^3 + var2*n^2 + var1*n^1 + var0*n^0 can be
	 * rewritten as (((var4*n + var3)*n + var2)*n + var1)*n + var0
	 * 
	 * Note: This is to have you experience of implementing HashTable. 27 is an
	 * example given to you to see how it behaves. If you have time, I would
	 * encourage you to try with a prime number, not 27. And compare the results
	 * but not required.
	 */
	private int hashFunc(String str) {
		// Horners method
		int hashValue = 0;
		for(int j=0;j<str.length();j++){
			hashValue =  str.charAt(j)+(31*hashValue);
		}
		hashValue =hashValue % arraySize;
		if(hashValue<0){
			hashValue = hashValue + arraySize;
		}
		return hashValue;
	}

	// doubles array length and rehash items whenever the load factor is reached
	private void rehash() {
		// Double the size and return the nearest prime
		int newSize = nextPrime(arraySize*2);
		int tempSize = arraySize;
		// Update the arraySize to the newSize
		arraySize = newSize;
		DataItem[] tempHashArray = hashArray;
		// Make a new hashArray
		hashArray = new DataItem[arraySize];
		int countRehashed=0;
		for(int i=0;i<tempSize;i++){
			// The element is filled with some value
			if(tempHashArray[i]!=null && !tempHashArray[i].value.equals("#DEL#")){
				// Mistake done
				int hashVal = hashFunc(tempHashArray[i].value);
				while(hashArray[hashVal]!=null){
					hashVal++;
					hashVal=hashVal%newSize;
				}
				hashArray[hashVal] = tempHashArray[i];
				countRehashed++;
			}
		}
		System.out.println("Rehashing "+ countRehashed + " items, "+" new size is "+newSize);
	}

}