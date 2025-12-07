import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * The responder class represents a response generator object.
 * It is used to generate an automatic response, based on specified input.
 * Input is presented to the responder as a set of words, and based on those
 * words the responder will generate a String that represents the response.
 *
 * Internally, the reponder uses a HashMap to associate words with response
 * strings and a list of default responses. If any of the input words is found
 * in the HashMap, the corresponding response is returned. If none of the input
 * words is recognized, one of the default responses is randomly chosen.
 * 
 * @author David J. Barnes and Michael Kölling.
 * @version 2016.02.29
 */
public class Responder
{
    // Used to map key words to responses.
    private HashMap<String, String> responseMap;
    // Default responses to use if we don't recognise a word.
    private ArrayList<String> defaultResponses;
    // The name of the file containing the default responses.
    private static final String FILE_OF_DEFAULT_RESPONSES = "default.txt";
    private static final String FILE_OF_RESPONSE_MAP = "responses.txt";

    private Random randomGenerator;

    /**
     * Construct a Responder
     */
    public Responder()
    {
        responseMap = new HashMap<>();
        defaultResponses = new ArrayList<>();
        fillResponseMap();
        randomGenerator = new Random();

        try {
            fillDefaultResponses();
        } catch (MultipleBlankLinesException e) {
            System.out.println("Error loading default responses: " + e.getMessage());

            defaultResponses.add("Could you elaborate on that?");
        }
    }

    /**
     * Generate a response from a given set of input words.
     * 
     * @param words  A set of words entered by the user
     * @return       A string that should be displayed as the response
     */
    public String generateResponse(HashSet<String> words)
    {
        for (String word : words) {
            String response = responseMap.get(word);
            if (response != null) {
                return response;
            }
        }
        return pickDefaultResponse();
    }

    /**
     * Populate the responseMap by reading from responses.txt.
     * Format:
     *   - First line: comma-separated keys
     *   - Following non-blank lines: response text
     *   - Blank line: end of entry
     */
    private void fillResponseMap()
    {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_DEFAULT_RESPONSES);

        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] keys = line.split(",");
                for (int i = 0; i < keys.length; i++) {
                    keys[i] = keys[i].trim();
                }

                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null&& !line.trim().isEmpty()) {
                    if (response.length() > 0) response.append(" ");
                    response.append(line.trim());
                }

                for (String key : keys) {
                    responseMap.put(key, response.toString());
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading " + FILE_OF_RESPONSE_MAP + ": " + e.getMessage());
        }
    }

    /**
     * Build up a list of default responses from which we can pick
     * if we don't know what else to say.
     */
    private void fillDefaultResponses() throws MultipleBlankLinesException
    {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_DEFAULT_RESPONSES);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String line;
            StringBuilder currentResponse = new StringBuilder();
            int blankLineCount = 0;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    blankLineCount++;
                    if (blankLineCount >= 2) {
                        throw new MultipleBlankLinesException("Two or more consecutive blank lines detected");
                    }
                    if (currentResponse.length() > 0) {
                        defaultResponses.add(currentResponse.toString().trim());
                        currentResponse.setLength(0);
                    }
                }   else {
                    blankLineCount = 0;
                    if (currentResponse.length() > 0) {
                        currentResponse.append(" ");
                    }
                    currentResponse.append(line.trim());
                }
            }
            if (currentResponse.length() > 0) {
                defaultResponses.add(currentResponse.toString().trim());
            }   
        } catch (IOException e) {
            System.err.println("Error reading " + FILE_OF_DEFAULT_RESPONSES + ": " + e.getMessage());
        }

        if (defaultResponses.size() == 0) {
            defaultResponses.add("Could you elaborate of that?");
        }
    }

    /**
     * Randomly select and return one of the default responses.
     * @return     A random default response
     */
    private String pickDefaultResponse()
    {
        // Pick a random number for the index in the default response list.
        // The number will be between 0 (inclusive) and the size of the list (exclusive).
        int index = randomGenerator.nextInt(defaultResponses.size());
        return defaultResponses.get(index);
    }
}

class MultipleBlankLinesException extends Exception {
    public MultipleBlankLinesException(String message) {
        super(message);
    }
}