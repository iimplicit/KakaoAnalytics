/*
 * DY_ChatTextInput class brings .txt Kakao chat text file
 * Text file is then processed into Java
 * Three primary variables to use text file in code: line, entireText, and textList
 * line returns individual lines from inputStream.readline();
 * entireText stores the entire .txt file in a String variable
 * textList stores individual lines in a List interface with ArrayList variable type
 * 
 * CODE LATER ON: Basic DY_Chatroom characteristics such as total number of messages, words, etc. can be done here or in DY_Chatroom class.
 */

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class DY_ChatTextInput {
	private File chatFile;
	private BufferedReader inputStream;
	private StringBuilder sb;
	private String line;
	private String entireText;
	private List<String> textList;
	private List<String> oldTextList;
	private String lang;
	
	public DY_ChatTextInput(String filePath){
		chatFile = new File(filePath);
		oldTextList = new ArrayList<String>();
		textList = new ArrayList<String>();

		try{
			// BufferedReader used for best efficiency
			inputStream = new BufferedReader(new FileReader(chatFile));
			sb = new StringBuilder();
			
			// first line in text
			line = inputStream.readLine();
			
			// splits the words in the sentence by white-space
			String[] words = line.split("\\s+");
			
			// Pretty safe assumption unless Korean format room with KakaoTalk in front
			if(words[0].equals("﻿KakaoTalk")){
				lang = "ENG";
			}
			else{
				lang = "KOR";
			}
			
			// Loop for entire .txt file. Reads one line at a time from the BufferedReader inputStream
			while(line != null){
				// StringBuilder sb used to append the individual words per line with the whitespace
				sb.append(line);
				sb.append(System.lineSeparator());
				
				// individual lines added to the ArrayList
//				textList.add(line);
				oldTextList.add(line);
				
				// inputStream.readLine() iterates. Used to read the next line in the text file
				line = inputStream.readLine();
			}
			entireText = sb.toString();  
		
			// to limit extremely large text files
			System.out.println("oldTextlist: " + oldTextList.size());
			
			int limit = 3000;
			if(oldTextList.size() > limit){
				int diff = oldTextList.size() - limit;
				oldTextList.subList(0, diff).clear();
				textList = oldTextList;
				System.out.println("textList: " + textList.size());
			}
			else{
				textList = oldTextList;
			}
		
		}catch(IOException e){
			e.printStackTrace();
		}finally {
			if(inputStream != null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}
		
		// CAUTION! VERY IMPORTANT NOTE ABOUT ArrayList<String> textList:
		// textList.get(2), textList.get(3) always return blank lines due to KakaoTalkChat format
	}
	
	public String getLine(){
		return line;
	}
	
	public String getEntireText(){
		return entireText;
	}
	
	public List<String> getOldTextList(){
		return oldTextList;
	}
	
	public List<String> getTextList(){
		return textList;
	}
	
	public BufferedReader getBufferedReader(){
		return inputStream;
	}
	
	public String getLang(){
		return lang;
	}
}