package com.easyooo.framework.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Killer
 */
public final class IoUtil {
	
	public static void write(File file, String content, boolean append)throws IOException{
		FileWriter fw = null;
		BufferedWriter bw = null;
		try{
			fw = new FileWriter(file, append);
			bw = new BufferedWriter(fw);
			
			bw.write(content);
		}finally{
			if(bw != null){
				try{
					bw.close();
				}catch(IOException e){
				}
			}
			if(fw != null){
				try{
					fw.close();
				}catch(IOException e){
				}
				
			}
		}
	}

	public static String readLines(String fullFileName)throws IOException{
		FileReader fr = null;
		BufferedReader br = null;
		
		try{
			File file = new File(fullFileName);
			fr = new FileReader(file);
	
			br = new BufferedReader(fr);
			
			String line = null;
			StringBuffer sb = new StringBuffer();
			
			while((line = br.readLine()) != null){
				sb.append( line );
				sb.append("\n");
			}
			
			return sb.toString();
		}finally{
			if(br != null){
				br.close();
			}
			
			if(fr != null){
				fr.close();
			}
		}
	}
	
}
