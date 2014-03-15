/* DY_AllocRowScoreComparator is to compare and sort finAllocTable in DY_Chatroom class */

import java.util.Comparator;

public class DY_AllocRowScoreComparator implements Comparator<DY_AllocRow>{
	public int compare(DY_AllocRow row1, DY_AllocRow row2){
		Integer no1 = row1.getRoleScore();
		Integer no2 = row2.getRoleScore();
		return no1.compareTo(no2);
	}
}
