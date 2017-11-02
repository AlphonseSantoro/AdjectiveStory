import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.Desktop;
import java.net.URI;
import java.io.File;
/** 
 *  Class for creating an adjective story.
 *  
 *  @author (Kjell-Olaf Slagnes)
 *  @version (2.1)
 */
public class StoryCreator {
    private InputReader reader;
    private OutputWriter writer;
    private Random random;
    private Scanner scanner;
    int randomIndex, countAdjectives;
    String randomWord, completeStory = "";
    ArrayList<String> story, adjectiveList, dictionary, htmlList;
    /**
     *  Constructor to initialize three objects:
     *  reader to load a file to an array.
     *  writer to write an array to a file
     *  random to get random values.
     */
    public StoryCreator(){
        reader = new InputReader();
        writer = new OutputWriter();
        random = new Random();
    }

    /**
     *  Create a story from random adjectives selected from a file.
     *  
     *  @param storyFilename name of the file that contains a story
     *  @param adjectivesFilename name of the file that contains adjectives
     *  @param outputFilename name of the output filename
     */
    public void createAdjectiveStory(String storyFilename, String adjectivesFilename, String outputFilename){
        story = reader.getWordsInFile(storyFilename);
        adjectiveList = reader.getWordsInFile(adjectivesFilename);
        validateNumberOfAdjectives();
        notEnoughAdjectives();
        randomAdjectiveLoop(adjectiveList);
        writer.write(story, outputFilename);
        openFile(outputFilename);
    }

    /**
     *  Create a story with input from the user.
     *  
     *  @param storyFilemane name of the file that contains a story
     *  @param outputFilename name of the output filename
     */
    public void createAdjectiveStory(String storyFilename, String outputFilename){
        story = reader.getWordsInFile(storyFilename);
        adjectivesFromUser();
        randomAdjectiveLoop(adjectiveList);
        writer.write(story, outputFilename);
        openFile(outputFilename);
    }
    
    /**
     *  Create a story from a dictionary.
     *  
     *  @param storyFilename name of the file that contains a story
     *  @param dictionaryFilename name of the file that contains a dictionary
     *  @param outputFilename name of the output filename
     */
    public void createAdjectiveStoryFromDictionary(String storyFilename, String dictionaryFilename, String outputFilename){
        story = reader.getWordsInFile(storyFilename);
        adjectiveList = adjectiveArrayFromDictionary(dictionaryFilename);
        validateNumberOfAdjectives();
        notEnoughAdjectives();
        randomAdjectiveLoop(adjectiveList);
        writer.write(story, outputFilename);
        openFile(outputFilename);
    }
    
    /**
     * Loops through an array and replaces "ADJECTIVE" with a random word.
     * 
     * @param Array that contains an adjective story.
     */
    public void randomAdjectiveLoop(ArrayList<String> arrayList){
        for(int i = 0; i < story.size(); i++){
            replaceAdjectives(i, arrayList);
        }
    }

    /**
     * Replace words that match "ADJEKTIV", if a character like comma or period is present include that character.
     * 
     * @param index in array
     */
    public void replaceAdjectives(int i, ArrayList<String> arrayList){
        String word = story.get(i);
        if(word.length() > 8 && word.substring(0, 8).equals("ADJEKTIV")){
            setRandomAdjective(arrayList);
            randomWord = getRandomAdjective() + word.substring(8);
            adjectiveToUpperCase(i);
            story.set(i, randomWord);
            adjectiveList.remove(randomIndex);
        } else if (word.equals("ADJEKTIV")) {
            setRandomAdjective(arrayList);
            randomWord = getRandomAdjective();
            adjectiveToUpperCase(i);
            story.set(i, randomWord);
            adjectiveList.remove(randomIndex);
        }
    }
    
    /**
     * Capitalize first letter in the adjective if the adjective is the first word in a sentence.
     * 
     * @param index in array
     */
    public void adjectiveToUpperCase(int i){
        if(story.get(i-1).endsWith(".") || story.get(i-1).endsWith("!") || story.get(i-1).endsWith("?")){
                randomWord = randomWord.substring(0, 1).toUpperCase() + randomWord.substring(1);
        }
    }

    /**
     *  Set String randomWord to a random word from an array.
     *  
     *  @param adjectiveArray name of array that contains adjectives
     */
    public void setRandomAdjective(ArrayList<String> adjectiveArray){
        randomIndex = random.nextInt(adjectiveArray.size());
        randomWord = adjectiveArray.get(randomIndex);
    }
    
    /**
     *  Return the string randomWord.
     */
    public String getRandomAdjective(){
        return randomWord;
    }

    /**
     *  Insert adjectives from user into an array, the word "exit" will tell the scanner to not ask for another input.
     */
    public void adjectivesFromUser(){
        adjectiveList = new ArrayList<String>();
        scanner = new Scanner(System.in);
        String userInput = "";
        validateNumberOfAdjectives();
        int count = countAdjectives;
        System.out.println("Enter minimum " + countAdjectives + " adjectives.");
        while(!userInput.equals("exit")){
            userInput = scanner.next().toLowerCase();
            if(!userInput.equals("exit")){
                adjectiveList.add(userInput);
                count--;
                if(count > 0){
                System.out.println("Enter " + count + " more adjecives");
                }
                if(count == 0){
                    System.out.println("You have enough adjectives to create the story, enter more adjectives if you want more variety.");
                }
            }
            if(userInput.equals("exit") && adjectiveList.size() < countAdjectives){
                System.out.println("The history cannot be made because there is not enough adjectives!");
                validateNumberOfAdjectives();
                notEnoughAdjectives();
                System.exit(0);
            }
        }
    }
    
    /**
     * Create an array of adjectives from a dictionary.
     * 
     * @param name of the dictionary file
     */
    public ArrayList<String> adjectiveArrayFromDictionary(String filename){
        dictionary = reader.getWordsInFile(filename);
        adjectiveList = new ArrayList<String>();
        for(int i = 0; i < dictionary.size(); i++){
            if(dictionary.get(i).equals("adj")){
                String indexAt = dictionary.get(i - 1).toLowerCase();
                adjectiveList.add(indexAt);
            }
        }
        return adjectiveList;
    }
    
    /**
     * Check if the adjectivelist has enough adjectives to create the story.
     */
    public void validateNumberOfAdjectives(){
        countAdjectives = 0;
        for(int i = 0; i < story.size(); i++){
            if(story.get(i).startsWith("ADJEKTIV")){
                countAdjectives++;
            }
        }
    }
    
    /**
     * Replace the outputfile with an error message if there is not enough adjectives.
     */
    public void notEnoughAdjectives(){
        if(adjectiveList.size() < countAdjectives){
            story = reader.getWordsInFileWithScanner("errorNotEnoughAdjectives.txt");
        }
    }
    
    /**
     * Create html version of story.
     * 
     * @param title of the history
     */
    public void createHtmlFile(String title){
        completeStory = "";
        for(String h : story){
            completeStory += h + " ";
        }
        htmlList = reader.getWordsInFile("story.html");
        for(int i = 0; i < htmlList.size(); i++){
            if(htmlList.get(i).equals("$story")){
                htmlList.set(i, completeStory);
            }
            if(htmlList.get(i).equals("$title")){
                htmlList.set(i, title);
            }
        }
        writer.write(htmlList, "AdjectiveHistory.html");
        openFile("AdjectiveHistory.html");
    }
    
    /**
     * Open outputFilename with default file editor or browser.
     * 
     * @param outputFilename the of the file to open
     */
    public void openFile(String outputFilename){
        File in = new File(outputFilename);
        try{
            if(outputFilename.endsWith(".html")){
                if(Desktop.isDesktopSupported())
                {
                    Desktop.getDesktop().browse(in.toURI());
                }
            }
            if(outputFilename.endsWith(".txt")){
                if(Desktop.isDesktopSupported())
                {
                    Desktop.getDesktop().open(in);
                }
            }
        } catch(Exception e){
            System.out.println("Error reading file: " + e);
        }
    }
}
