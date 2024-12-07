package sg.edu.nus.iss.vttp5_ssf_day19_.darryl_practise_workshop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import sg.edu.nus.iss.vttp5_ssf_day19_.darryl_practise_workshop.repo.HashRepo;
import sg.edu.nus.iss.vttp5_ssf_day19_.darryl_practise_workshop.util.Constant;

@SpringBootApplication
public class Vttp5SsfDay19DarrylPractiseWorkshopApplication implements CommandLineRunner {

	@Autowired
	HashRepo hashRepo;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Vttp5SsfDay19DarrylPractiseWorkshopApplication.class);
		app.run(args);
	}

	@Override
	public void run(String... args) throws Exception {
		//read the file using file REader 
		// put the data into redis map
		ApplicationArguments cliOpts = new DefaultApplicationArguments(args);
		String inputFilePath = cliOpts.getOptionValues("inputFilePath").get(0);
		
		File inputFile = new File(inputFilePath);
		if (!(inputFile.exists())){
			throw new FileNotFoundException("This file cannot be found");
		}
		
		String fullJsonString = getFullJsonString(inputFile);
		uploadToRedisHashMap(fullJsonString);
		
	}

	public String getFullJsonString(File inputFilePath) throws IOException{
		FileReader fr = new FileReader(inputFilePath);
		BufferedReader br = new BufferedReader(fr);
		
		String fullJsonString = "";
		String line = "";
		while ((line = br.readLine()) != null){
			fullJsonString += line;
		}
		br.close();
		fr.close();

		return fullJsonString;
	}

	public void uploadToRedisHashMap(String fullJsonString){
		JsonReader jr = Json.createReader(new StringReader(fullJsonString));

		JsonArray jsonArray = jr.readArray();
		
		
		for (int i = 0 ; i < jsonArray.size(); i++){
			JsonObject jsonEntry = jsonArray.get(i).asJsonObject();
			String id = jsonEntry.getString("id");
			hashRepo.put(Constant.redisHashName, id, jsonEntry.toString());
		}
	}

	
}
