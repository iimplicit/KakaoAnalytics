/*
 * DY_User classified into what type of person that user is based on scores
 */

import java.util.List;
import java.util.ArrayList;

public class DY_User {
	private int userID;
	private String userName;
	private List<String> userMsgList;
	private int numRoles;
	private int[] roleScoreArr;
	private List<DY_Role> roleList;
	private List<DY_FreqRow> freqTable;
	
	public DY_User(String userName, int userID, int numRoles, List<DY_Role> roleList){
		this.userName = userName;
		this.userID = userID;
		userMsgList = new ArrayList<String>();
		this.numRoles = numRoles;
		roleScoreArr = new int[numRoles];
		this.roleList = roleList;
		freqTable = new ArrayList<DY_FreqRow>();
		
		// entire freqTable will be filled with FreqRows and wordCnt initialized to 0
		fillFreqTable(freqTable, roleList);
	}

	public DY_User(int numRoles, List<DY_Role> roleList){
		userMsgList = new ArrayList<String>();
		roleScoreArr = new int[numRoles];
		this.roleList = roleList;
		freqTable = new ArrayList<DY_FreqRow>();
		
		// entire freqTable will be filled with FreqRows and wordCnt initialized to 0
		fillFreqTable(freqTable, roleList);
	}
	
	private void fillFreqTable(List<DY_FreqRow> freqTable, List<DY_Role> roleList){
		int len=0;
		int rowNum = 0;
		// get particular role from roleList
		for(int i=0; i<roleList.size(); i++){
			// iterate through number of words(keys) in hashMap
			for(int k=0; k<roleList.get(i).getKeysList().size(); k++){
				// initializing our freqTable with individual FreqRows
				DY_FreqRow row = new DY_FreqRow(i, roleList.get(i).getKeysList().get(k));
				
				/* 
				 * freqTable order is made up of rows of FreqRows
				 * order of FreqRows is order of DY_Role AND keys inside keysList of DY_Role, which is order of inputWord() called
				 */
				
				freqTable.add(row);
			}
			len = roleList.get(i).getKeysList().size();
		}
	}
	
	public void setName(String userName){
		this.userName = userName;
	}
	
	public String getName(){
		return this.userName;
	}
	
	public void setID(int userID){
		this.userID = userID;
	}
	
	public int getID(){
		return this.userID;
	}
	
	public void addMsg(String msg){
		userMsgList.add(msg);
	}
	
	public List<String> getUserMsgList(){
		return this.userMsgList;
	}
	
	public void displayUserMsg(){
		for(int i=0; i<this.userMsgList.size(); i++)
			System.out.println(this.userMsgList.get(i));
	}
	
	public int[] getRoleScoreArr(){
		return this.roleScoreArr;
	}
	
	public int getScore(int ID){
		return roleScoreArr[ID];
	}
	
	public void setScore(int ID, Integer score){
		Integer prevScore = roleScoreArr[ID];
		roleScoreArr[ID] = score + prevScore;
//		System.out.println("roleScoreArr: " + roleScoreArr[ID]);
	}
	
	public List<DY_FreqRow> getFreqTable(){
		return this.freqTable;
	}
	
	
	public DY_FreqRow getFreqRow(int roleNum, String word){
		int tableRow=0;
		for(int i=0; i<freqTable.size(); i++){
			if(freqTable.get(i).getRoleNum() == roleNum && freqTable.get(i).getWord().equals(word)){
				tableRow = i;
			}
		}
		return freqTable.get(tableRow);
	}
	
	public int getFreqRowNum(int roleNum, String word){
		int tableRow=0;
		for(int i=0; i<freqTable.size(); i++){
			if(freqTable.get(i).getRoleNum() == roleNum && freqTable.get(i).getWord().equals(word)){
				tableRow = i;
			}
		}
		return tableRow;
	}
	
	public int getFreqFirstRoleRowNum(int roleNum){
		// TODO
		int tableRow = 0;
		for(int i=0; i<freqTable.size(); i++){
			if(freqTable.get(i).getRoleNum() == roleNum){
//				System.out.println("found");
				tableRow = i;
				break;
			}
		}
		return tableRow;
	}
	
	public DY_FreqRow getFreqFirstRoleRow(int roleNum){
		int tableRow=0;
		for(int i=0; i<freqTable.size(); i++){
			if(freqTable.get(i).getRoleNum() == roleNum){
				tableRow = i;
			}
		}
		return freqTable.get(tableRow);
	}
	
	public void displayFreqTable(){
		for(int i=0; i<freqTable.size(); i++){
			System.out.println("row: " + i + "   roleNum: " + freqTable.get(i).getRoleNum() + "   word: " + freqTable.get(i).getWord() + "      freq: " + freqTable.get(i).getWordCnt());
		}
	}
}