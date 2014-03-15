/* DY_AllocRowScoreComparator is to compare and sort finAllocTable in DY_Chatroom class */

import java.util.Comparator;

public class DY_FreqRowComparator implements Comparator<DY_FreqRow>{
	public int compare(DY_FreqRow row1, DY_FreqRow row2){
		int ret = 0;
		
		if(row1.getRoleNum() == row2.getRoleNum()){
			Integer no1 = row1.getWordCnt();
			Integer no2 = row2.getWordCnt();
			ret = no1.compareTo(no2);
		}
//		return no1.compareTo(no2);
		return ret;
	}
}
