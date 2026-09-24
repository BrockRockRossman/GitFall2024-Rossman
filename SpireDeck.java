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

public class SpireDeck {

    // Main method
    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner(System.in);

        // Get file path from user
        System.out.print("Enter the file path: ");
        String filepath = scanner.nextLine();

        // Read the file
        int[] fileInfo = readFile(filepath);

        // Get total cost
        int totalCost = fileInfo[0];

        // Get histogram
        int[] histogram = new int[7];

        for (int i = 0; i < 7; i++) {
            histogram[i] = fileInfo[i + 1];
        }

        String histogramText = makeHistogram(histogram);

        // Create random ID
        int id = makeID();

        // Create PDF
        createPDF(id, totalCost, histogramText);

        scanner.close();
    }


    // Checks whether a line is valid
    public static boolean checkLine(String[] lineVal) {

        // Make sure there are exactly 2 parts
        if (lineVal.length != 2) {
            return false;
        }

        String card = lineVal[0];

        int cost;

        // Make sure cost is a number
        try {
            cost = Integer.parseInt(lineVal[1].trim());
        } catch (NumberFormatException e) {
            return false;
        }

        return cost >= 0 && cost <= 6
                && !card.isEmpty()
                && !card.matches("[ \t]*");
    }


    // Counts number of lines
    public static int fileLineCount(String filepath) throws IOException {

        BufferedReader reader =
                new BufferedReader(new FileReader(filepath));

        int lineCount = 0;

        while (reader.readLine() != null) {
            lineCount++;
        }

        reader.close();

        return lineCount;
    }


    // Reads file
    public static int[] readFile(String filepath) throws IOException {

        int errorCount = 0;
        int totalCost = 0;

        // Seven positions, one for each cost 0-6
        int[] histogram = new int[7];

        BufferedReader reader =
                new BufferedReader(new FileReader(filepath));

        int lineCount = fileLineCount(filepath);

        if (lineCount > 1000) {

            String curLine;

            while (errorCount < 10
                    && (curLine = reader.readLine()) != null) {

                // Split line
                String[] lineVal = curLine.split(":");

                // Check line
                if (!checkLine(lineVal)) {
                    errorCount++;
                } else {

                    int cost = Integer.parseInt(lineVal[1].trim());

                    // Add cost to total
                    totalCost += cost;

                    // Add card to histogram
                    histogram[cost]++;
                }
            }
        }

        reader.close();

        // Put total cost first,
        // followed by histogram values
        int[] result = new int[8];

        result[0] = totalCost;

        for (int i = 0; i < 7; i++) {
            result[i + 1] = histogram[i];
        }

        return result;
    }


    // Creates histogram text
    public static String makeHistogram(int[] histogram) {

        String result = "";

        for (int i = 0; i <= 6; i++) {
            result += "Cost " + i + ": "
                    + histogram[i] + " cards\n";
        }

        return result;
    }


    // Creates random 9-digit ID
    public static int makeID() {

        Random random = new Random();

        return 100000000 + random.nextInt(900000000);
    }


    // Creates PDF
    public static void createPDF(
            int id,
            int totalCost,
            String histogram) throws IOException {

        String filepath =
                System.getProperty("user.home")
                + File.separator + "Downloads"
                + File.separator + "SpireDeck_"
                + id + ".pdf";

        PDDocument document = new PDDocument();

        PDPage page = new PDPage();

        document.addPage(page);

        PDPageContentStream content =
                new PDPageContentStream(document, page);

        // First line: ID
        content.beginText();

        content.setFont(PDType1Font.HELVETICA, 12);

        content.newLineAtOffset(50, 700);

        content.showText("ID: " + id);

        // Second line: Total Cost
        content.newLineAtOffset(0, -20);

        content.showText("Total Cost: " + totalCost);

        // Histogram
        content.newLineAtOffset(0, -30);

        String[] histogramLines = histogram.split("\n");

        for (String line : histogramLines) {
            content.showText(line);
            content.newLineAtOffset(0, -15);
        }

        content.endText();

        content.close();

        document.save(filepath);

        document.close();
    }
}
