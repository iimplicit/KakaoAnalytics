/* Developer: Danny Kyungtak Youm
 * Bitgenstein Inc.
 * 
 * DY_KakaoAnalytics is the Main.java of the program
 * Contains main method,
 * Organizes and interacts the various classes from the top level,
 * Also used to debug and test code along the way
 * 
 * To use DY_KakaoAnalytics with other programs, use code inside main function in the other code
 */

public class DY_KakaoAnalytics{
	public static void main(String[]args){
		
		long stime = System.nanoTime();
		/* input roles.txt with different roles, words, and scores
		 * instantiate DY_Role objects and provide a roleList of all the role objects
		 */
		DY_RoleInput roleInput = new DY_RoleInput("roles.txt");
		long roleTime = System.nanoTime();
		System.out.println("total time of RoleInput: " + (roleTime - stime)/(1.0 * 1000000000));
		
		/* input the KakaoChat text in .txt to analyze
		 * provides textList with individual messages in each cell of ArrayList
		 */
		DY_ChatTextInput input = new DY_ChatTextInput("KakaoTalkChats(1).txt");
		long ctiTime = System.nanoTime();
		System.out.println("total time of ChatTexTInput: " + (ctiTime - stime)/(1.0 * 1000000000));
		
		/* DY_ChatTextInput object as parameter and roleList to do all the analysis
		 * match words, frequencies, second place (이인자), inactive (잠수함)
		 */

		DY_Chatroom chatroom = new DY_Chatroom(input, roleInput.getRoleList(), "output.txt");
		
		System.out.println("numUsers: " + chatroom.getNumUsers());
		
		long etime = System.nanoTime();
	
		long time = etime - stime;
		System.out.println("total time of brute force program: " + time/(1.0 * 1000000000));	
	}
}

/* Developer's notes:
 * Look to change charAt() to codePointAt() at a certain point. Apparently charAt can be unsafe.
 * 
 * Use third party libraries like Guava and Apache Commons for many of the string manipulation operations
 * for next patch
 * 
 * Implement substring algorithm (suffix tree, A-Corasick algorithm, etc.)
 * 
 */

