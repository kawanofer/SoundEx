import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * This code is based on the algorithm SoundEx. SoundEx is a phonetic algorithm
 * for indexing names by sound, as pronounced in English.
 * 
 * Some of the American Soundex principles was used to better understand and
 * developer this algorithm. Such as the GenerateGroupKey method, where each
 * group of letters is replaced by a number. This method helps to compare and
 * define each word is phonetic similar.
 * 
 * https://en.wikipedia.org/wiki/Soundex
 * 
 * @author Fernando Kawano
 * @since 09/01/2016
 * 
 */

public class PhoneticSearch {
	public String[] keyboardInput;
	public String fileName = "";

	public List<String> validWords = new ArrayList<String>();
	public List<String> cleanedWords = new ArrayList<String>();
	public List<String> word_dict = new ArrayList<String>();

	public String equivalentGroup1 = "AEIOU";
	public String equivalentGroup2 = "CGJKQSXYZ";
	public String equivalentGroup3 = "BFPVW";
	public String equivalentGroup4 = "DT";
	public String equivalentGroup5 = "MN";

	public String droppedLettersPattern = "[AEIHOUWY]";

	public static void main(String args[]) {
		new PhoneticSearch().ReadKeyboard(args);
	}

	/*
	 * COMPARE EACH WORD'S KEY GENERATED BETWEEN WORD_DIC AND INSERTED BY
	 * KEYBOARD
	 */
	public void ProcessWords() {
		for (int i = 0; i < cleanedWords.size(); i++) {
			String actualWord = cleanedWords.get(i);
			String actualWordKey = GenerateGroupKey(actualWord);

			System.out.print(keyboardInput[i] + ":\t");
			for (String dictWord : word_dict) {
				dictWord = dictWord.toUpperCase();
				String dictWordKey = GenerateGroupKey(dictWord.replaceAll(droppedLettersPattern, ""));
				if (actualWordKey.equals(dictWordKey)) {
					System.out.print(dictWord + ", ");
				}
			}
			System.out.println("");
		}
	}

	/*
	 * GENERATE A KEY TO EACH WORD TO COMPARE AND VERIFY YOUR PHONETIC
	 * SIMILARITY
	 */
	private String GenerateGroupKey(String actualWord) {
		// initiate the key
		String groupKey = "";

		// save the last char added, to avoid repeated digits
		String lastCharAdded = "";

		// save the char i'm trying to add
		String charToAdd = "";

		// get the code of each letter
		for (int i = 0; i < actualWord.length(); i++) {
			String actualChar = "" + actualWord.charAt(i);

			if (equivalentGroup1.indexOf(actualChar) > -1)
				charToAdd = "1";
			else if (equivalentGroup2.indexOf(actualChar) > -1)
				charToAdd = "2";
			else if (equivalentGroup3.indexOf(actualChar) > -1)
				charToAdd = "3";
			else if (equivalentGroup4.indexOf(actualChar) > -1)
				charToAdd = "4";
			else if (equivalentGroup5.indexOf(actualChar) > -1)
				charToAdd = "5";
			else
				charToAdd = "0";

			// verify if the last char is repeated
			if (!lastCharAdded.equals(charToAdd)) {
				groupKey += charToAdd;
				lastCharAdded = charToAdd;
			}
		}

		// SOUNDEX RULE: Append 3 zeros if result contains less than 3 digits.
		if (groupKey.length() < 3) {
			groupKey += "000";
		}

		return groupKey;
	}

	/*
	 * THIS METHOD IS RESPONSABLE FOR CLEAN AND VALIDATE THE WORDS INSERTED BY
	 * KEYBOARD.
	 */
	public void CleanListsOfWords(String[] wordsArray) {
		String newWord = "";

		// #1 - ONLY ALPHABETIC LETTERS IS VALID.
		for (int a = 0; a < wordsArray.length - 1; a++) {
			newWord = wordsArray[a].replaceAll("[^A-Za-z]", "");
			if (!newWord.isEmpty()) {
				// #2 - ALL WORDS TRANSFORMED TO UPPERCASE.
				validWords.add(newWord.toUpperCase());
			}
		}

		// #3 - AFTER THE FIRST LETTER ANY OF THE FOLLOWING LETTERS MUST BE
		// DROPPED.
		for (String word : validWords) {
			cleanedWords.add(word.replaceAll(droppedLettersPattern, ""));
		}
	}

	/*
	 * READ THE TXT FILE WITH THE DICTIONARY OF WORDS
	 */
	public void ReadFile(String fileName) {
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			while (br.ready()) {
				String word = br.readLine();
				word_dict.add(word);
			}
			ProcessWords();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * READ THE KEYBOARD INPUT, WAIT FOR A SEQUENCE OF WORDS AND FOR LAST THE
	 * NAME OF THE TXT FILE.
	 */
	public void ReadKeyboard(String[] args) {
		String inputText = "";

		System.out.print(
				"Enter the words and the txt name for last.\nExample: '1ton# brief soon < word_dict.txt'\n\n->");
		try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
			inputText = br.readLine();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		keyboardInput = inputText.trim().split(" ");
		fileName = keyboardInput[keyboardInput.length - 1];

		CleanListsOfWords(keyboardInput);
		ReadFile(fileName);
	}
}
