package workshop05code;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;
//Included for the logging exercise
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author sqlitetutorial.net
 */
public class App {
    // Start code for logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(App.class.getName());
    // End code for logging exercise
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SQLiteConnectionManager wordleDatabaseConnection = new SQLiteConnectionManager("words.db");
        logger.log(Level.INFO, "starting wordle connection");

        wordleDatabaseConnection.createNewDatabase("words.db");
        logger.log(Level.INFO, "attempting to create new database");
        if (wordleDatabaseConnection.checkIfConnectionDefined()) {
            logger.log(Level.INFO, "database created and connected");
        } else {
            logger.log(Level.WARNING, "could not create database");
            return;
        }
        if (wordleDatabaseConnection.createWordleTables()) {
            logger.log(Level.INFO, "wordle structures in place");
        } else {
            logger.log(Level.WARNING,"could not launch");
            return;
        }

        // let's add some words to valid 4 letter words from the data.txt file

        try (BufferedReader br = new BufferedReader(new FileReader("resources/data.txt"))) {
            String line;
            int i = 1;
            while ((line = br.readLine()) != null) {
                logger.log(Level.INFO, "adding word: " + line);
                wordleDatabaseConnection.addValidWord(i, line);
                i++;
            }

        } catch (IOException e) {
            logger.log(Level.WARNING, "could not load" + e.getMessage());
            System.out.println(e.getMessage());
            return;
        }

        // let's get them to enter a word

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter a 4 letter word for a guess or q to quit: ");
            String guess = scanner.nextLine();
            logger.log(Level.INFO, "user input for guess: " + guess);

            while (!guess.equals("q")) {
                System.out.println("You've guessed '" + guess+"'.");

                for (char c : guess.toCharArray()) {
                    if((c >= 'a' && c <= 'z') && guess.length() == 4){ {

                        if (wordleDatabaseConnection.isValidWord(guess)) { 
                            System.out.println("Success! It is in the the list.\n");
                            logger.log(Level.INFO, "guess is correct");
                        }else{
                            System.out.println("Sorry. This word is NOT in the the list.\n");
                            logger.log(Level.INFO, "guess is incorrect");
                        }

                    }
                }
                    else {
                        System.out.println("Please enter a valid 4 letter word.\n");
                        logger.log(Level.INFO, "guess was incorrect format");
                    }
                }


                System.out.print("Enter a 4 letter word for a guess or q to quit: " );
                guess = scanner.nextLine();
            }
            logger.log(Level.INFO, "user quit");
        } catch (NoSuchElementException | IllegalStateException e) {
            e.printStackTrace();
        }

    }
}