/* DY_AllocRow represents one row of the ArrayList AllocationTable
 * Each row will contain a userNum, roleNum, and roleScore for that userNum and roleNum.
 */

public class DY_AllocRow {
	private int userNum;
	private int roleNum;	
	private int roleScore;
	
	public DY_AllocRow(int userNum, int roleNum, int roleScore){
		this.userNum = userNum;
		this.roleNum = roleNum;
		this.roleScore = roleScore;
	}
	
	public DY_AllocRow(){		
	}
	
	public int getUserNum(){
		return this.userNum;
	}
	public int getRoleNum(){
		return this.roleNum;
	}
	public int getRoleScore(){
		return this.roleScore;
	}
}
