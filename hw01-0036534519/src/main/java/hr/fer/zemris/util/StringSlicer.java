package hr.fer.zemris.util;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A class used to read substrings from a given text using conditions and transformations
 *
 * @author Marko Miljković (miljkovicmarko45@gmail.com)
 */
public class StringSlicer {

    /**
     * Text to be read
     */
    private final char[] text;

    /**
     * Index of the next character to be read
     */
    private int currentIndex;

    /**
     * Character used for escaping special characters
     */
    private char escapeCharacter;

    /**
     * List of all escapable characters
     */
    private final List<Character> escapableCharacters;

    /**
     * Used to initialize the StringSlicer object
     *
     * @param text Text that will be read from
     * @param startIndex The starting index at which reading will begin
     * @param escapeCharacter Character used for escaping characters
     * @param escapableCharacters A list of escapable characters
     */
    public StringSlicer(char[] text, int startIndex, char escapeCharacter, List<Character> escapableCharacters) {
        this.text = text;
        this.currentIndex = startIndex;
        this.escapeCharacter = escapeCharacter;
        this.escapableCharacters = new ArrayList<>(escapableCharacters);
    }

    /**
     * Used to initialize the StringSlicer object
     * <p>
     * List of default values for uninitialized parameters:
     *<ul>
     *  <li>escapableCharacters - Empty list</li>
     *</ul>
     *
     * @param text Text that will be read from
     * @param startIndex The starting index at which reading will begin
     * @param escapeCharacter A list of escapable characters
     */
    public StringSlicer(char[] text, int startIndex, char escapeCharacter) {
        this(text, startIndex, escapeCharacter, Collections.emptyList());
    }

    /**
     * Used to initialize the StringSlicer object
     * <p>
     * List of default values for uninitialized parameters:
     * <ul>
     *     <li>escapeCharacter - '\000'</li>
     *     <li>escapableCharacters - Empty list</li>
     * </ul>
     *
     * @param text Text that will be read from
     * @param startIndex The starting index at which reading will begin
     */
    public StringSlicer(char[] text, int startIndex) {
        this(text, startIndex, '\000');
    }

    /**
     * Used to initialize the StringSlicer object
     * <p>
     * List of default values for uninitialized parameters:
     * <ul>
     *     <li>startIndex - 0</li>
     *     <li>escapeCharacter - '\000'</li>
     *     <li>escapableCharacters - Empty list</li>
     * </ul>
     *
     * @param text Text that will be read from
     */
    public StringSlicer(char[] text) {
        this(text, 0);
    }

    /**
     * Used to initialize the StringSlicer object
     *
     * @param text Text that will be read from
     * @param startIndex The starting index at which reading will begin
     * @param escapeCharacter Character used for escaping characters
     * @param escapableCharacters A list of escapable characters
     */
    public StringSlicer(String text, int startIndex, char escapeCharacter, List<Character> escapableCharacters) {
        this(text.toCharArray(), startIndex, escapeCharacter, escapableCharacters);
    }

    /**
     * Used to initialize the StringSlicer object
     * <p>
     * List of default values for uninitialized parameters:
     *<ul>
     *  <li>escapableCharacters - Empty list</li>
     *</ul>
     *
     * @param text Text that will be read from
     * @param startIndex The starting index at which reading will begin
     * @param escapeCharacter A list of escapable characters
     */
    public StringSlicer(String text, int startIndex, char escapeCharacter) {
        this(text.toCharArray(), startIndex, escapeCharacter);
    }

    /**
     * Used to initialize the StringSlicer object
     * <p>
     * List of default values for uninitialized parameters:
     * <ul>
     *     <li>escapeCharacter - '\000'</li>
     *     <li>escapableCharacters - Empty list</li>
     * </ul>
     *
     * @param text Text that will be read from
     * @param startIndex The starting index at which reading will begin
     */
    public StringSlicer(String text, int startIndex) {
        this(text.toCharArray(), startIndex);
    }

    /**
     * Used to initialize the StringSlicer object
     * <p>
     * List of default values for uninitialized parameters:
     * <ul>
     *     <li>startIndex - 0</li>
     *     <li>escapeCharacter - '\000'</li>
     *     <li>escapableCharacters - Empty list</li>
     * </ul>
     *
     * @param text Text that will be read from
     */
    public StringSlicer(String text) {
        this(text.toCharArray());
    }

    public String read() {
        return read(c -> false);
    }

    /**
     * Reads characters from text, beginning at currentIndex, and reading until stopCond is satisfied
     * or end of text is reached.
     * <p>
     * All the characters that were read are concatenated in the order in which they were
     * read and returned as a string.
     * <p>
     * If this method is called while currentIndex is greater than or equal to the text length
     * NoSuchElementException is thrown
     *
     * @param stopCond Condition used to test if the next character should be read
     * @return Substring of original text
     * @throws NoSuchElementException If read is called after all the characters have been read
     */
    public String read(Predicate<Character> stopCond) {
        return read(stopCond, c -> c);
    }

    /**
     * Reads characters from text, beginning at currentIndex, and reading until stopCond is satisfied
     * or end of text is reached.
     * <p>
     * The transform function is applied to each read character. The transformation doesn't
     * change the characters in the original text
     * <p>
     * All the transformed characters that were read are concatenated in the order in which
     * they were read and returned as a string.
     * <p>
     * If this method is called while currentIndex is greater than or equal to the text length
     * NoSuchElementException is thrown
     *
     * @param stopCond Condition used to test if the next character should be read
     * @param transform Transformation used on every character read
     * @return Substring of original text
     * @throws NoSuchElementException If read is called after all the characters have been read
     */
    public String read(Predicate<Character> stopCond, Function<Character, Character> transform) {
        if (currentIndex >= text.length) {
            throw new NoSuchElementException("All characters from text already read.");
        }

        StringBuilder sb = new StringBuilder();

        while(currentIndex < text.length && !stopCond.test(text(currentIndex))) {
            sb.append(transform.apply(text(currentIndex)));
            currentIndex++;
        }

        return sb.toString();
    }

    public String readAndEscape(Predicate<Character> stopCond) {
        return read(stopCond, this::escape);
    }


    public String readAndEscape(Predicate<Character> stopCond, Function<Character, Character> transform) {
        return read(stopCond, c -> transform.apply(escape(c)));
    }

    public void reset() {
        setCurrentIndex(0);
    }

    public void skip(int n) {
        currentIndex += n;
    }

    public void skip(Predicate<Character> stopCond) {
        if (hasNext()) {
            read(stopCond);
        }
    }

    public boolean hasNext() {
        return currentIndex >= 0 && currentIndex < text.length;
    }

    public void addEscapableCharacter(char c) {
        escapableCharacters.add(c);
    }

    public void addEscapableCharacters(Collection<Character> escapableCharacters) {
        this.escapableCharacters.addAll(escapableCharacters);
    }

    public List<Character> getEscapableCharacters() {
        return escapableCharacters;
    }

    public char getEscapeCharacter() {
        return escapeCharacter;
    }

    public void setEscapeCharacter(char escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }

    private char escape(char c) {
        if (c == escapeCharacter) {
            char nextC = text(++currentIndex);
            if(escapableCharacters.contains(nextC)) {
                return nextC;
            } else {
                currentIndex--;
            }
        }
        return c;
    }

    private char text(int index) {
        if (!hasNext()) {
            throw new NoSuchElementException("All characters from text already read.");
        }
        return text[index];
    }
}
