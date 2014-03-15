/* DY_RoleInput class inputs the individual roles, words and scores for those roles from roles.txt */

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class DY_RoleInput {
	private File roleFile;
	private BufferedReader inputStream;
	private String line;
	
	private List<DY_Role> roleList;
	
	public DY_RoleInput(String filePath){
		
		roleFile = new File(filePath);
		roleList = new ArrayList<DY_Role>();
		
		try{
			inputStream = new BufferedReader(new FileReader(roleFile));
			line = inputStream.readLine();

			// checks to see if roles.txt line is either role name or role word and score
			
			// do the first i=0 case outside while loop to avoid index issues
			int i = 0;
			if(!(line.contains("\t"))){
				// DY_Role object ID and it's place in roleList should be the same
				DY_Role role = new DY_Role(line, i);
				roleList.add(role);
			}
			else{
				int tabIndex = line.indexOf("\t");
				// inputs word and respective score
				roleList.get(i).inputWord(line.substring(0,tabIndex), Integer.valueOf(line.substring(tabIndex+1)));
			}
			
			// while loop for the rest of the entire roles.txt
			// NOTE: i++. i is incremented before new DY_Role name has been found and added
			while(((line=inputStream.readLine()) != null)){
				if(!(line.contains("\t"))){
					i++;
					DY_Role role = new DY_Role(line, i);
					roleList.add(role);	
				}
				else{
					int tabIndex = line.indexOf("\t");
					roleList.get(i).inputWord(line.substring(0,tabIndex), Integer.valueOf(line.substring(tabIndex+1)));
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public List<DY_Role> getRoleList(){
		return this.roleList;
	}
	
	public void displayRoleList(){
		System.out.println("/*********************ROLELIST");
		for(int i=0; i<roleList.size(); i++){
			System.out.println(roleList.get(i).getName());
			System.out.println(roleList.get(i).getID());
			roleList.get(i).displayBank();
		}
	}
}