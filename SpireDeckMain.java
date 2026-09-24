// Imports
import java.io.BufferedReader;
import java.io.File;
import java.util.Scanner;


// Main class
public class Main {

    public static void Main(String[] args) {

        // Create Scanner to take user input
        Scanner scanner = new Scanner(system.in);

        System.out.println("Please input the file path of your Slay the Spire deck: ");
        String filepath = scanner.nextLine();



    }

    // Method to generate a random unique DeckID
    public static String deckIDGen() {

    }

    
    // Method to read the file
    public static void readFile(String filepath) {
        
        // Set up variables
        int errorCount = 0;
        BufferedReader reader = new BufferedReader(new FileReader(filepath));


        // check line count
        int lineCount = fileLineCount(filepath);

        if(lineCount > 1000) {
            
            while (reader.readLine() != null) {
                String curLine = reader.readLine();

                // Split line
                String[] lineVal = curLine.split(":");



                
            }
            
        } else {
            // Void
        }

        


    }


    // Check to make sure its within bounds
    public static int fileLineCount(String filepath) {
        
        int lineCount = 0;
        BufferedReader reader = new BufferedReader(new FileReader(filepath));

        while (reader.readLine() != null) {
            lineCount++;
        }

        reader.close();

        return lineCount;

    }


    // Check for invalid cards
    public static boolean checkLine(String[] lineVal) {
    String card = lineVal[0];
    int cost = Integer.parseInt(lineVal[1]);

    if (cost >= 0 && cost <= 6 && !card.isEmpty() && !card.matches("[ \t]*")) {
        return true;
    }

    return false;
    }




    // Make the Histogram
    public static String makeHistogram(int[] costs) {
    int[] histogram = new int[7];

    for (int cost : costs) {
        if (cost >= 0 && cost <= 6) {
            histogram[cost]++;
        }
    }

    String result = "";

    for (int i = 0; i <= 6; i++) {
        result += "Cost " + i + ": " + histogram[i] + " cards\n";
    }

    return result;
}


}


