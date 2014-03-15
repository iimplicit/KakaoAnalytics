
public class DY_FreqRow {
	private int roleNum;
	private String word;
	private int wordCnt;
	
	public DY_FreqRow(int roleNum, String word){
		this.roleNum = roleNum;
		this.word = word;
		
		// when constructing a row, wordCnt is always initialized to 0
		this.wordCnt = 0;
	}
	
	public DY_FreqRow(){
		this.wordCnt = 0;
	}
	
	public int getRoleNum(){
		return roleNum;
	}
	
	public void setRoleNum(int roleNum){
		this.roleNum = roleNum;
	}
	
	public String getWord(){
		return this.word;
	}
	
	public void setWord(String word){
		this.word = word;
	}
	
	public int getWordCnt(){
		return wordCnt;
	}
	
	public Integer getIntegerWordCnt(){
		Integer IntegerWordCnt = new Integer(wordCnt);
		return IntegerWordCnt;
	}
	
	public void incWordCnt(){
		(this.wordCnt)++;
	}
	
	public void displayRow(){
		System.out.println("roleNum: " + this.roleNum + "   word: " + this.word + "   wordCnt: " + this.wordCnt);
	}
}
