/* Combines DY_ChatTextInput, DY_RoleInput, DY_Role, and DY_User classes to analyze text
 * This class does all the heavy lifting and utilizes all available classes to process individual words
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Collections;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;


public class DY_Chatroom {
	private DY_ChatTextInput input;
	private final int initTextListLen;
	
	private List<DY_Role> roleList;
	private List<DY_User> userList;
	private List<String> nameList;
	private List<DY_AllocRow> allocTable;
	private List<DY_AllocRow> finAllocTable;
	
	// two lists used to store the top 3 most frequently used strings and their wordCnt per user/role
	private List<String> strList;
	private List<Integer> freqList;
	
	private int numUsers;

	private BufferedWriter w;
	
	public DY_Chatroom(DY_ChatTextInput input, List<DY_Role> roleList, String outputFilePath){
		this.input = input;
		this.initTextListLen = input.getTextList().size();
		System.out.println("initTextListLen: " + initTextListLen);
		
		this.roleList = roleList;
		userList = new ArrayList<DY_User>();
		nameList = new ArrayList<String>();
		
		// creating output for bufferedWriter
		createOutput(outputFilePath);
		
		/* depending on language of input textfile, execute findNamesENG or findNamesKOR
		 * findNames() now serves dual purpose:
		 *   1. find names of users
		 *   2. find total number of names in chat room by filling nameList
		 */
		
		long fnStart = System.nanoTime();
		
		if(this.input.getLang().equals("ENG"))
			findNamesENG(nameList, this.input.getTextList());
		else
			findNamesKOR(nameList, this.input.getTextList());
		
		/* instantiates the number of DY_User objects based on the size of nameList
		 * Additionally, makeUsers fills userList
		 */
		
		long fnEnd = System.nanoTime();
		long fnDuration = fnEnd - fnStart;
		System.out.println("fnDuration: " + fnDuration/(1.0*1000000000));
		numUsers = nameList.size();
		
		makeUsers(nameList.size(), this.roleList.size());
		
		// sets names of each DY_User.ID to the name from nameList based on order of nameList
		nameToUserConvert(userList, nameList);
		
		// depending on language of input textfile, execute getMessagesENG or getMessagesKOR
		if(input.getLang().equals("ENG"))
			getMessagesENG(nameList, userList, input.getTextList());
		else
			getMessagesKOR(nameList, userList, input.getTextList());
		
		// eliminate 잠수함 (inactive) users from the userList to reduce time spent going through unnecessary user messages.
		preRemoveInactive(userList);		
		
		/* main function to match words in user messages to role words
		 */
		matchWords(userList, roleList);
		
		// set up tables to sort Users by their characters and scores
		allocTable = new ArrayList<DY_AllocRow>();
		finAllocTable = new ArrayList<DY_AllocRow>();
		
		fillAllocTable(userList, roleList, allocTable, finAllocTable);
		
		List<String> strList = new ArrayList<String>();
		List<Integer> freqList = new ArrayList<Integer>();
		
		topFinFreq(userList, finAllocTable, strList, freqList);

		finOutput(userList, finAllocTable, strList, freqList);

		closeOutput();
	}
	
	public int getNumUsers(){
		return this.numUsers;
	}
	
	private void makeUsers(int numUsers, int numRoles){
		for(int i=0; i<numUsers; i++){
			DY_User  user = new DY_User(numRoles, roleList);
			userList.add(user);
		}
	}
	
	public void displayUserName(int userNum){
		for(int i=0; i<this.userList.size(); i++){
			if(this.userList.get(i).getID() == userNum){
				System.out.println("this user is: " + this.userList.get(i).getName());
			}
		}
	}

	//findNamesENG() goes through English lang style format
	private void findNamesENG(List<String> nameList, List<String> textList){
		/* start from i=5 because 0~4 does not contain names and messages		
		   first for loop examines one line at a time from textList */
		
		for(int i=5; i<textList.size(); i++){
			int commaCnt = 0;
			int colonCnt = 0;
			int thirdCommaIndex = 0;   // used for ENG lang. ENG lang has three commas before user message
			int secondColonIndex = 0;
			
			// second for loop examines one character at a time from the line in textList
			for(int j=0; j<textList.get(i).length(); j++){
				if(textList.get(i).charAt(j) == ','){
					commaCnt++;
					if(commaCnt == 3)
						thirdCommaIndex = j;
				}
				if(textList.get(i).charAt(j) == ':'){
					colonCnt++;
					if(colonCnt == 2)
						secondColonIndex = j;
				}
				if(commaCnt >= 3 && colonCnt >=2){
					// prevent duplicate names inside the nameList
					if(nameList.contains(textList.get(i).substring(thirdCommaIndex + 2, secondColonIndex  - 1))){
						break;   // Found name, but duplicate
					}
					// add name to nameList if new name in chatroom
					else{
						nameList.add(textList.get(i).substring(thirdCommaIndex + 2, secondColonIndex - 1));
						break;   // Found name
					}
				}
			}
		}
	}
	
	// findNamesKOR goes through Korean language style format	
	private void findNamesKOR(List<String> nameList, List<String> textList){
		/* start from i=5 because 0~4 does not contain names and messages	
		   first for loop examines one line at a time from textList */
		
		for(int i=5; i<textList.size(); i++){
			int commaCnt = 0;
			int colonCnt = 0;
			int firstCommaIndex = 0;   // used for KOR lang. KOR lang only has one comma before user message
			int secondColonIndex = 0;
			
			// second for loop examines one character at a time from the line in textList
			for(int j=0; j<textList.get(i).length(); j++){
				if(textList.get(i).charAt(j) == ','){
					commaCnt++;
					if(commaCnt == 1)
						firstCommaIndex = j;
				}
				if(textList.get(i).charAt(j) == ':'){
					colonCnt++;
					if(colonCnt == 2)
						secondColonIndex = j;
				}
				if(commaCnt == 1 && colonCnt == 2){
					// prevent duplicate names inside the nameList
					if(nameList.contains(textList.get(i).substring(firstCommaIndex + 2, secondColonIndex  - 1))){
						break;   // Found name, but duplicate
					}
					// add name to nameList if new name in chatroom
					else{
						nameList.add(textList.get(i).substring(firstCommaIndex + 2, secondColonIndex - 1));
						break;   // Found name
					}
				}
			}
		}
		
	/* TO PRINT THE NAME LIST OF ALL USERS IN CHAT ROOM
	 * ALL USERS THAT EVER WROTE A MESSAGE IN CHAT ROOM
	 * Including previous users and deactivated accounts
	 */
		for(int i=0; i<nameList.size(); i++)
			System.out.println("name: " + nameList.get(i) + "	i: " + i);
	 
	}
	
	public List<String> getNameList(){
		return this.nameList;
	}
	
	private void nameToUserConvert(List<DY_User> userList, List<String> nameList){
		for(int i=0; i<nameList.size(); i++){
				userList.get(i).setName(nameList.get(i));
			userList.get(i).setID(i);
		}
	}
	
	private void getMessagesENG(List<String> nameList, List<DY_User> userList, List<String> textList){
		// start from i=5 because 0~4 does not contain names and messages 
		for(int i=5; i<textList.size(); i++){
			int commaCnt = 0;
			int colonCnt = 0;
			int thirdCommaIndex = 0;
			int secondColonIndex = 0;
			
			// second for loop examines one character at a time from the line in textList
			for(int j=0; j<textList.get(i).length(); j++){
				if(textList.get(i).charAt(j) == ','){
					commaCnt++;
					if(commaCnt == 3)
						thirdCommaIndex = j;
				}
				if(textList.get(i).charAt(j) == ':'){
					colonCnt++;
					if(colonCnt == 2)
						secondColonIndex = j;
				}
				if(commaCnt >= 3 && colonCnt >=2){
					int index = nameList.indexOf(textList.get(i).substring(thirdCommaIndex + 2, secondColonIndex  - 1));
					userList.get(index).addMsg(textList.get(i).substring(secondColonIndex + 2));
					break;     // Found message
				}
			}
		}
	}
	
	private void getMessagesKOR(List<String> nameList, List<DY_User> userList, List<String> textList){
		// start from i=5 because 0~4 does not contain names and messages 
		for(int i=5; i<textList.size(); i++){
			int commaCnt = 0;
			int colonCnt = 0;
			int firstCommaIndex = 0;
			int secondColonIndex = 0;
			
			// second for loop examines one character at a time from the line in textList
			for(int j=0; j<textList.get(i).length(); j++){
				if(textList.get(i).charAt(j) == ','){
					commaCnt++;
					if(commaCnt == 1)
						firstCommaIndex = j;
				}
				if(textList.get(i).charAt(j) == ':'){
					colonCnt++;
					if(colonCnt == 2)
						secondColonIndex = j;
				}
				if(commaCnt >= 1 && colonCnt >=2){
					int index = nameList.indexOf(textList.get(i).substring(firstCommaIndex + 2, secondColonIndex  - 1));
					userList.get(index).addMsg(textList.get(i).substring(secondColonIndex + 2));
					break;     // Found message
				}
			}
		}
	}
	
	// method used to remove potentially inactive participants before their score is even calculated
	private void preRemoveInactive(List<DY_User> userList){
		for(int i=0; i<userList.size(); i++){
			if(userList.get(i).getUserMsgList().size() < 10){
				userList.remove(i);
			}
		}
	}
	
	// matchWords is a very bloated piece of code with too many nested for loops
	// next patch and rewrite should break up the iterations or find a simpler method
	private void matchWords(List<DY_User> userList, List<DY_Role> roleList){

		// iterate through list of users
		for(int i=0; i<userList.size(); i++){
			// iterate through the list of userMsgList per user
			
			
			for(int j=0; j<userList.get(i).getUserMsgList().size(); j++){
				// in each cell/line of userMsgList, get string and assign it to String s
				String s = userList.get(i).getUserMsgList().get(j);
				// Eliminate white space and put individual words(with punctuation) in the words[]
				String[] words = s.split("\\s+");
				
				// iterate through the words[] to access each word
				for(int k=0; k<words.length; k++){
					// need to compare with hashMap for each role in roleList
					for(int l=0; l<roleList.size();l++){
						
						Map<String, Integer> myBank = roleList.get(l).getBank();
						
						 // going through the entire contents of the hash map
						 // iterate over every single key in hash map to find a match

/*						Integer val = null;
						
						if((val = myBank.get(words[k])) != null){
							userList.get(i).setScore(l, val);
							DY_FreqRow row = userList.get(i).getFreqRow(roleList.get(l).getID(), words[k]);
							row.incWordCnt();
							row.displayRow();
						}*/
						
						for(Map.Entry<String, Integer> entry : myBank.entrySet()){
							if(words[k].contains(entry.getKey())){
								
								// if contains, then add score from the DY_Role hash map to the DY_User roleScoreList.
								// each cell in roleScoreList corresponds to the nth ID of the DY_Role.
								// ie. Role0 will be tracked in the 0th cell of the roleScoreList. Role1 : 1st cell of roleScoreList and so on.
								
								Integer val = entry.getValue();
	
								// set the new score in the userList of roleScoreArr
								userList.get(i).setScore(l, val);
								
								// need to increment the wordCnt variable in DY_FreqRow of freqTable for the particular user
								DY_FreqRow row = userList.get(i).getFreqRow(roleList.get(l).getID(), entry.getKey());
								row.incWordCnt();
//								row.displayRow();
							}
						}
					}
				} 
			}
		}
	}
	
	private void fillAllocTable(List<DY_User> userList, List<DY_Role> roleList, List<DY_AllocRow> allocTable, List<DY_AllocRow> finAllocTable){
		
		// Order of the allocation table will be by user and then by each role.
		// e.g. user0 and role0, user0 and role1, ... ,userN and roleN
		for(int i=0; i<userList.size(); i++){
			for(int j=0; j<userList.get(i).getRoleScoreArr().length; j++){
				DY_AllocRow row = new DY_AllocRow(i, j, userList.get(i).getRoleScoreArr()[j]);
				allocTable.add(row);
			}
		}

		// REVERSE ORDER. looking for highest score to lowest score
		Collections.sort(allocTable, Collections.reverseOrder( new DY_AllocRowScoreComparator()));
		
       /*finAllocTable contains the highest scoring users for their roles
		 roles and users cannot be duplicated, so it is strictly one to one
		 for example, user1 cannot be included in finAllocTable for both role2 and role.
		 role4 cannot be used by both user2 and user3
		 */
		finAllocTable.add(allocTable.get(0));
		for(int i=1; i<allocTable.size(); i++){
			int cnt = 0;
			for(int j=0; j<finAllocTable.size(); j++){
				if(finAllocTable.get(j).getUserNum() == allocTable.get(i).getUserNum() || 
						finAllocTable.get(j).getRoleNum() == allocTable.get(i).getRoleNum()){
					cnt++;
				}
			}
			if(cnt == 0){
				finAllocTable.add(allocTable.get(i));
			}
		}
		
		// if roleScore is 0, then remove it from finAllocTable
/*		for(int i=0; i<finAllocTable.size(); i++){
			if(finAllocTable.get(i).getRoleScore() < 10)finAllocTable.remove(i);
		}
*/
	}
	
	private void displayFreqTables(List<DY_User> userList){
		for(int i=0; i<userList.size(); i++){
			System.out.println("**********************USER NUM: " + i);
			userList.get(i).displayFreqTable();
		}
	}	
	
	private void topFinFreq(List<DY_User> userList, List<DY_AllocRow> finAllocTable, List<String> strList, List<Integer> freqList){
		DY_User[] userArr = new DY_User[finAllocTable.size()];
		int[] userNumArr = new int[finAllocTable.size()];
		int[] roleNumArr = new int[finAllocTable.size()];
		
		for(int i=0; i<finAllocTable.size(); i++){
/*			userArr[i] = userList.get(finAllocTable.get(i).getUserNum());
			roleNumArr[i] = finAllocTable.get(i).getRoleNum();
			userNumArr[i] = userList.get(finAllocTable.get(i).getUserNum()).getID();*/
			
			userArr[i] = userList.get(finAllocTable.get(i).getUserNum());
			userNumArr[i] = finAllocTable.get(i).getUserNum();
			roleNumArr[i] = finAllocTable.get(i).getRoleNum();
		}
		
		for(int i=0; i<userList.size(); i++){
			for(int j=0; j<userList.get(i).getFreqTable().size(); j++){
//				userList.get(i).getFreqTable().get(j).displayRow();
				Collections.sort(userList.get(i).getFreqTable(), Collections.reverseOrder( new DY_FreqRowComparator()));
			}
		}
		
		/* adds the three most frequent words, and their corresponding frequenices to freqList and strList */
		for(int i=0; i<userNumArr.length; i++){
			DY_User targetUser = userList.get(userNumArr[i]);
			freqList.add(targetUser.getFreqTable().get(targetUser.getFreqFirstRoleRowNum(roleNumArr[i])).getWordCnt());
			freqList.add(targetUser.getFreqTable().get(targetUser.getFreqFirstRoleRowNum(roleNumArr[i]) + 1).getWordCnt());
			freqList.add(targetUser.getFreqTable().get(targetUser.getFreqFirstRoleRowNum(roleNumArr[i]) + 2).getWordCnt());
			strList.add(targetUser.getFreqTable().get(targetUser.getFreqFirstRoleRowNum(roleNumArr[i])).getWord());
			strList.add(targetUser.getFreqTable().get(targetUser.getFreqFirstRoleRowNum(roleNumArr[i]) + 1).getWord());
			strList.add(targetUser.getFreqTable().get(targetUser.getFreqFirstRoleRowNum(roleNumArr[i]) + 2).getWord());
		}
	}
	
	private void finOutput(List<DY_User> userList, List<DY_AllocRow> finAllocTable, List<String> strList, List<Integer> freqList){
		/* write the following: number of messages in chat room
		 * participant, character, 1st word, number, 2nd word, number, 3rd word, number
		 */
		
		/* following code handles the total number of messages in chat */
		int totalNumMsg = 0;
		for(int i=0; i<userList.size(); i++){
			for(int j=0; j<userList.get(i).getUserMsgList().size(); j++){
				totalNumMsg++;
			}
		}
		
		String strTotalNumMsg = Integer.toString(totalNumMsg);
		System.out.println(strTotalNumMsg);
		displayOutput(strTotalNumMsg);
		displayNewLine();
		/*******************TOTAL NUMBER OF MESSAGES*********************/
		
		System.out.println(finAllocTable.size());
		for(int i=0; i<finAllocTable.size(); i++){
			// i=0~4 for top 5. As soon as i incremented to 5, break out of the loop
			if(i == 5){break;}
			
			int usrNum = finAllocTable.get(i).getUserNum();
			int roleNum = finAllocTable.get(i).getRoleNum();			
			

			/******** ONLY TOP 5 (at most) AND MOST ACTIVE USERS OUTPUT IN THIS IF CLAUSE *******/
			if(userList.get(usrNum).getScore(roleNum) > 30){
				
				// top 5 and most active user names
				System.out.println("user " + i + " name: " + userList.get(usrNum).getName());
				displayOutput(userList.get(usrNum).getName());
				displayOutput("\t");			
				/*******************TOP 5 USER NAMES *************************/
			
				// highest scoring roles for top 5 and most active users
				for(int j=0; j<roleList.size(); j++){
					if(roleList.get(j).getID() == roleNum){
						//	System.out.println(roleList.get(j).getName());
						//	System.out.println(roleList.get(j).getID());
						displayOutput(roleList.get(j).getName());
						displayOutput("\t");
					
					}
				}
				/******************TOP 5 ROLE NAMES FOR USERS*******************/
			
				// Shows the 3 most frequent words and their frequencies
				for(int j=0; j < 3; j++){
					displayOutput(strList.get(j+i*3));
					displayOutput("\t");
					String s = Integer.toString(freqList.get(j+i*3));
					displayOutput(s);
					displayOutput("\t");
				}
				/*********3 MOST FREQUENT WORDS AND THEIR FREQUENCIES FOR TOP USERS ******/
			
				// TOP 5 (at most) and active users. END OF i iteration
				displayNewLine();
			}
		}		
		
		/********** TO INCLUDE SECOND PLACE WINNERS EXCLUSIVELY *********/
		
		boolean secondPlace = false;	
		for(int i=5; i<finAllocTable.size(); i++){
			int usrNum = finAllocTable.get(i).getUserNum();
			int roleNum = finAllocTable.get(i).getRoleNum();
			
			if(userList.get(usrNum).getScore(roleNum) > 30)
				secondPlace = true;
		}
		
		if(secondPlace == true){
			displayOutput("$");
			displayOutput("\t");
			displayOutput("이인자");
			displayOutput("\t");
			
			for(int i=5; i<finAllocTable.size(); i++){
				int usrNum = finAllocTable.get(i).getUserNum();
				int roleNum = finAllocTable.get(i).getRoleNum();
				System.out.println("user" + i + "name: " + userList.get(usrNum).getName());
				displayOutput(userList.get(usrNum).getName());
				displayOutput("\t");
			}
			
			displayNewLine();
		}
		/******************************* SECOND PLACE WINNERS *********************/
		
		/*************************CHECK IF THERE ARE INACTIVE USERS ***************/
		boolean inactive = false;
		for(int i=0; i<finAllocTable.size(); i++){
			int usrNum = finAllocTable.get(i).getUserNum();
			int roleNum = finAllocTable.get(i).getRoleNum();
		
			if(userList.get(usrNum).getScore(roleNum) <= 30){
				inactive = true;
			}
		}
		
		/*******************IF INACTIVE USERS EXIST, DISPLAY THEM ON OUTPUT ******/
		if(inactive == true){
			displayOutput("%");
			displayOutput("\t");
			displayOutput("잠수함");
			displayOutput("\t");
		
			for(int i=0; i<finAllocTable.size(); i++){
				int usrNum = finAllocTable.get(i).getUserNum();
				int roleNum = finAllocTable.get(i).getRoleNum();
					
				/******** displays the names of the INACTIVE users **********/
				// eliminate all inactive users from being displayed on output.txt
				if(userList.get(usrNum).getScore(roleNum) <= 30){
				//	System.out.println("user" + i + "name: " + userList.get(usrNum).getName());
					displayOutput(userList.get(usrNum).getName());
					displayOutput("\t");
				}
			}
		}
	}
	
	private void createOutput(String outputFilePath){
		try {
			w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void displayOutput(String output){
		try {
			w.write(output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void displayNewLine(){
		try {
			w.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void closeOutput(){
		try {
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

