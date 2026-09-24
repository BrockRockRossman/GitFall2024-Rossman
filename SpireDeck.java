/* Header
This program reads a file containing card names and costs, validates the information and exports a pdf to the user.
I do want to disclose that I used AI and google on this project particularly when figuring out the pdfbox library.
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;


public class SpireDeck {

    // Main method
    public static void main(String[] args) throws IOException {

        // Scanner to take input from the user
        Scanner scanner = new Scanner(System.in);

        // Gets the file path from user
        System.out.print("Enter the file path: ");
        String filepath = scanner.nextLine();

        // Runs the readFile method to get the information from the file
        int[] fileInfo = readFile(filepath);

        // Creates a random ID for the PDF
        int id = makeID();

        // Checks if the file should be VOID
        if (fileInfo[8] == 1) {

            // Creates a VOID PDF
            createVoidPDF(id);

            System.out.println("File was invalid. VOID PDF created.");

        } else {

            // Gets total cost
            int totalCost = fileInfo[0];

            // Makes the histogram
            int[] histogram = new int[7];

            for (int i = 0; i < 7; i++) {
                histogram[i] = fileInfo[i + 1];
            }

            // Creates the histogram text
            String histogramText = makeHistogram(histogram);

            // Creates the normal PDF
            createPDF(id, totalCost, histogramText);

            System.out.println("PDF created successfully.");
        }

        scanner.close();
    }


    // Checks whether a line is valid
    public static boolean checkLine(String[] lineVal) {

        // Makes sure there are exactly 2 parts
        if (lineVal.length != 2) {
            return false;
        }

        // Stores the card name
        String card = lineVal[0];

        int cost;

        // Makes sure the cost is a number
        try {
            cost = Integer.parseInt(lineVal[1].trim());
        } catch (NumberFormatException e) {
            return false;
        }

        // Makes sure the cost is between 0 and 6
        // and makes sure the card name is not empty
        return cost >= 0 && cost <= 6 && !card.isEmpty() && !card.matches("[ \t]*");
    }





    // Counts number of lines
    public static int fileLineCount(String filepath) throws IOException {

        // Creates a BufferedReader to read the file
        BufferedReader reader = new BufferedReader(new FileReader(filepath));

        // Keeps track of the number of lines
        int lineCount = 0;

        // Reads every line and counts it
        while (reader.readLine() != null) {
            lineCount++;
        }

        // Closes the reader
        reader.close();

        // Returns the number of lines
        return lineCount;
    }





    // Reads and validates the file
    public static int[] readFile(String filepath) throws IOException {

        // Error count tracks how many invalid cards are in the file
        int errorCount = 0;

        // Total cost tracks the total energy of the cards
        int totalCost = 0;

        // Creates a histogram with one position for each energy value 0-6
        int[] histogram = new int[7];

        // Creates a BufferedReader to read the file
        BufferedReader reader = new BufferedReader(new FileReader(filepath));

        // Runs the method to count the number of lines in the file
        int lineCount = fileLineCount(filepath);

        // Keeps track of whether the PDF should be VOID
        int voidFile = 0;

        // Checks to make sure the file is not over 1000 lines
        if (lineCount > 1000) {

            // File is too large, so mark it as VOID
            voidFile = 1;

        } else {

            String curLine;

            // Reads each line until the end of the file
            // or until the error count exceeds 10
            while (errorCount <= 10 && (curLine = reader.readLine()) != null) {

                // Splits the line at the colon
                String[] lineVal = curLine.split(":");

                // Checks whether the line is valid
                if (!checkLine(lineVal)) {

                    // Adds one to the error count
                    errorCount++;

                } else {
                    // Gets the energy cost
                    int cost = Integer.parseInt(lineVal[1].trim());

                    // Adds the cost to the total
                    totalCost += cost;

                    // Adds the card to the correct histogram position
                    histogram[cost]++;
                }
            }

            // Checks if the number of errors exceeded 10
            if (errorCount > 10) {

                // Marks the file as VOID
                voidFile = 1;
            }
        }

        // Closes the reader 
        reader.close();

        // Creates an array to store the results
        // Position 0 = total cost 
        // Positions 1-7 = histogram 
        // Position 8 = VOID status 
        int[] result = new int[9];

        // Stores the total cost
        result[0] = totalCost;

        // Stores the histogram values
        for (int i = 0; i < 7; i++) {
            result[i + 1] = histogram[i];
        }

        // Stores whether the file should be VOID
        result[8] = voidFile;

        // Returns all the information
        return result;
    }



    // Creates histogram text
    public static String makeHistogram(int[] histogram) {

        // Creates an empty string for the histogram
        String result = "";

        // Creates a line for each energy value from 0-6 
        for (int i = 0; i <= 6; i++) {
            result += "Energy " + i + ": "
                    + histogram[i] + " cards\n";
        }

        // Returns the completed histogram
        return result;
    }




    // Creates random 9-digit ID
    public static int makeID() {

        // Creates a random number generator
        Random random = new Random();

        // Returns a random 9-digit number for the ID
        return 100000000 + random.nextInt(900000000);
    }




    // Creates the normal PDF
    // The PDF library was one I was unfamiliar with and so I used ChatGPT to help me figure out the most efficient way to create
    // the PDF and to help format the various elements on the page
    public static void createPDF(int id, int totalCost, String histogram) throws IOException {

        // Creates a file path string
        String filepath =
                System.getProperty("user.home")
                + File.separator + "Downloads"
                + File.separator + "SpireDeck_"
                + id + ".pdf";

        // Creates a new PDF document
        PDDocument document = new PDDocument();

        // Creates a page for the document
        PDPage page = new PDPage();

        // Adds the page to the document
        document.addPage(page);

        // Creates a content stream to write to the page
        PDPageContentStream content = new PDPageContentStream(document, page);

        // Begins writing text
        content.beginText();

        // Sets the font for the text
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);

        // Makes the page look nice: sets the starting position for the next line of text
        content.newLineAtOffset(50, 700);

        // Adds the ID to the page
        content.showText("ID: " + id);

        // Adds the total cost on the second line
        content.newLineAtOffset(0, -20);

        content.showText("Total Cost: " + totalCost);

        // Moves down before adding the histogram
        content.newLineAtOffset(0, -30);

        // Splits the histogram into individual lines
        String[] histogramLines = histogram.split("\n");

        // Adds each histogram line to the PDF
        for (String line : histogramLines) {
            content.showText(line);
            content.newLineAtOffset(0, -15);
        }

        // Finishes writing text
        content.endText();

        // Closes the content stream
        content.close();

        // Saves the PDF
        document.save(filepath);

        // Closes the PDF document
        document.close();
    }




    // Creates a VOID PDF
    public static void createVoidPDF(int id) throws IOException {

        // Creates a file path string for the VOID PDF
        String filepath =
                System.getProperty("user.home")
                + File.separator + "Downloads"
                + File.separator + "SpireDeck_"
                + id + "(VOID).pdf";

        // Creates a new PDF document
        PDDocument document = new PDDocument();

        // Creates a page for the document
        PDPage page = new PDPage();

        // Adds the page to the document
        document.addPage(page);

        // Creates a content stream to write to the page
        PDPageContentStream content = new PDPageContentStream(document, page);

        // Begins writing text
        content.beginText();

        // Sets the font for the text
        content.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 12);

        // Makes the page look nice: sets the starting position for the next line of text
        content.newLineAtOffset(50, 700);

        // Writes VOID on the PDF
        content.showText("VOID");

        // Finishes writing text
        content.endText();

        // Closes the content stream
        content.close();

        // Saves the VOID PDF
        document.save(filepath);

        // Closes the PDF document
        document.close();
    }
}