package info.justdaile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import info.justdaile.firebase.FirebaseAuthRestEndpoints;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@SpringBootApplication
@EnableAutoConfiguration
public class FastfoodApplication {

	public static void main(String[] args) {
		SpringApplication.run(FastfoodApplication.class, args);
		try{
			FirebaseAuthRestEndpoints.assignApiKey("AIzaSyCye_t4B3OeVW9NiVcC8n7k0xf3F_LpeMM");

			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(
									new ClassPathResource("/service-account-key.json").getInputStream())
					)
					.setDatabaseUrl("https://mutabledatastream.firebaseio.com/")
					.build();

			FirebaseApp.initializeApp(options);
		}catch(IOException e){
			System.err.println(e.getMessage());
			System.exit(1);
		}
	}

}
