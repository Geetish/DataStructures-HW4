
public class SampleFile {
	public static void main(String[] args){
		//MyHashTable n=new MyHashTable(3);
		hashFunc("increase");
		
	}
	
   private static int hashFunc(String str) {
	    long hashCode=0;
	    str="everything";
		for(int i=0;i<str.length();i++){
			int temp =(int)Math.pow(7,str.length()-(i+1));
			//System.out.println("Power : "+(str.length()-(i+1)));
			//if(temp<0){
			//	System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$");
			//}
			hashCode = hashCode + ((str.charAt(i) - 'a'+1) * temp);
			//System.out.println("Value "+str + "   hashCode"+ hashCode);
			//System.out.println("HashCode" + hashCode);
		}
		//System.out.println("Geetish");
		//hashCode = hashCode % arraySize;
		System.out.println(hashCode);
		return 0;
	}
}
