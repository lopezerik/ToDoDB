import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class CredentialsParser {
	
	public Map<String, String> parseDatabase(String filename){
		
		try {

			File credentials = new File(".", filename);
			BufferedReader reader = new BufferedReader(new FileReader(credentials));
			String line = reader.readLine();
			
			// Map to store the credentials for return
			Map<String, String> map = new HashMap<>();
			// Loop through file and parse the information into the map
			while(line != null) {
				System.out.println(line);
				
				// Remove spaces and split on the equals sign once
				// Splitting more than once could split up a password with an equals sign in it
				String[] lineList = line.replaceAll(" ", "").split("=", 2);
				if(lineList[0].equals("URL")) {
					map.put("url", lineList[1]);
					
				} else if(lineList[0].equals("DB_USER")) {
					map.put("user", lineList[1]);
					
				} else if(lineList[0].equals("DB_USER_PW")) {
					map.put("password", lineList[1]);	
				}
				
				line = reader.readLine();
			}
			reader.close();
			
			// check if all credentials have been filled out
			if(map.size() < 3) {
				System.out.println("Error parsing '" + filename + "', not all database credentials set");
				return null;
			}
		
			return map;
			
		} catch (Exception e) {
			System.out.println("Error in credentials parser");
			System.out.println(e);
			return null;
		}
	}

}
