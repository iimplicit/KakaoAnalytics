/* Object from DY_Role class has a role name, words specific to that role, and scores for each of those respective words
 * All of the roles generated in the DY_RoleInput class, which is created from the roles.txt is stored in List<DY_Role> roleList
 */
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DY_Role {
	private Map <String, Integer> bank;
	private String roleName;
	private List<String> keysList;
	private int roleID;
	
	public DY_Role(String roleName, int roleID){
		this.roleName = roleName;
		this.roleID = roleID;
		bank = new HashMap<String, Integer>();
		keysList = new ArrayList<String>();
	}
	
	public DY_Role(int roleID){
		this.roleID = roleID;
		bank = new HashMap<String, Integer>();
		keysList = new ArrayList<String>();
	}
	
	public DY_Role(){
		bank = new HashMap<String, Integer>();
	}
	
	public void inputWord(String word, Integer score){
		bank.put(word, score);
		
		/* Now whenever inputWord is called, word is added to bank and to keysList as well.
		 */
		
		keysList.add(word);
	}
	
	public void setName(String roleName){
		this.roleName = roleName;
	}
	
	public String getName(){
		return this.roleName;
	}

	public void setID(int roleID){
		this.roleID = roleID;
	}
	
	public int getID(){
		return this.roleID;
	}
	
	public int getBankSize(){
		return this.bank.size();
	}
	
	public Map<String, Integer> getBank(){
		return this.bank;
	}
	
	public List<String> getKeysList(){
		return this.keysList;
	}
	
	public void displayBank(){
		for(Map.Entry<String, Integer> entry : bank.entrySet()){
			System.out.println("i" + entry + "Key: " + entry.getKey());
			System.out.println("i" + entry + "Value: " + entry.getValue());
		}
	}
}
