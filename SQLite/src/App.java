//User Input
import java.util.Scanner;

//Meta Setup
import java.nio.file.Files;
import java.nio.file.Paths;
// import org.json.JSONObject;
// import org.json.JSONException;

//Exceptions
import java.io.IOException;

public class App {
    public static void main(String[] args) throws Exception {
        
        Scanner userIn = new Scanner(System.in);
        String cmd = "";

        loadMetadata();

        while(true) {
            System.out.println("SYSTEM:\tReady for Command");
            cmd = userIn.nextLine();
            if(cmd.equalsIgnoreCase("exit")) {
                System.out.println("SYSTEM:\tShutting Down");
                System.exit(0);
                return;
            }
            SQLQuery query = new SQLQuery(cmd, 0);
            //inspect query structure
            query.print(0);
            saveMetadata();
        }
    }

    private static void createDB() throws IOException {
        Files.createDirectories(Paths.get("./DB/"));
        Files.createFile(Paths.get("./DB/db.meta"));
        return;
    }
    
    private static void loadMetadata() throws IOException {
        if((Files.isDirectory(Paths.get("./DB/"))) && (Files.exists(Paths.get("./DB/meta.json")))) {
            return;
        }
        createDB();
        return;
    }

    private static void saveMetadata() throws IOException {

    }
}
