import com.sun.istack.internal.NotNull;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Character.toLowerCase;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

/**
 * This program provides several examples of a Java 8 stream that show
 * how it can be used with "pure" functions, i.e., functions whose
 * return values are only determined by their input values, without
 * observable side effects.  This program also shows various stream
 * terminal operations, including forEach(), collect(), and several
 * variants of reduce().  It also includes a non-Java 8 example as a
 * baseline.
 */
public class ex12 {
    static public void main(String[] argv) {
        // Create an instance of this class.
        ex12 ex = new ex12();

        // Demonstrate each approach.
        ex.runNonJava8();
        ex.runForEach();
        ex.runCollect();
        ex.runCollectReduce();
        ex.runMapReduce();
    }

    /**
     * Capitalize @a s by making the first letter uppercase and the
     * rest lowercase.  Note that this is a "pure" function whose
     * return value is only determined by its input.
     */
    private String capitalize(@NotNull String s) {
        if (s.length() == 0)
            return s;
        return s
            // Uppercase the first character of the string.
            .substring(0, 1)
            .toUpperCase()
            // Lowercase the remainder of the string.
            + s.substring(1)
               .toLowerCase();
    }

    /**
     * Run an example using only Java 7 features.  This serve as a
     * baseline for comparing the Java 8 solutions.
     */
    private void runNonJava8() {
        System.out.println("Results from runNonJava8():");

        List<String> listOfCharacters = new LinkedList
            (Arrays.asList("horatio",
                           "claudius",
                           "Gertrude",
                           "Hamlet",
                           "laertes",
                           "Ophelia"));

        // Loop through all the characters.
        for (int i = 0; i < listOfCharacters.size();) {
            // Remove any strings that don't start with 'h' or 'H'.
            if (toLowerCase(listOfCharacters.get(i).charAt(0)) != 'h') {
                listOfCharacters.remove(i);
            } else {
                // Capitalize the first letter of a character whose
                // names starts with 'H' or 'h'.
                listOfCharacters.set(i, capitalize(listOfCharacters.get(i)));
                i++;
            }
        }

        // Sort the results in ascending order.
        Collections.sort(listOfCharacters);

        // Print the results.
        for (String s : listOfCharacters)
            System.out.println(s);
    }

    /**
     * Run an example using the forEach() terminal operation.
     */
    private void runForEach() {
        System.out.println("\nResults from runForEach():");

        Stream
            // Create a stream of characters from William
            // Shakespeare's Hamlet.
            .of("horatio",
                "claudius",
                "Gertrude",
                "Hamlet",
                "laertes",
                "Ophelia")

            // Remove any strings that don't start with 'h' or 'H'.
            .filter(s -> toLowerCase(s.charAt(0)) == 'h')

            // Capitalize the first letter in the string.
            .map(this::capitalize)

            // Sort the results in ascending order.
            .sorted()

            // Terminal operation that triggers aggregate operation
            // processing and prints the results.
            .forEach(System.out::println);
    }

    /**
     * Run an example using the collect() terminal operation.
     */
    private void runCollect() {
        System.out.println("\nResults from runCollect():");

        // Create a list of key characters in Hamlet.
        List<String> characters = Arrays.asList("horatio",
                                                "claudius",
                                                "Gertrude",
                                                "Hamlet",
                                                "laertes",
                                                "Ophelia");

        // Create sorted list of characters starting with 'h' or 'H'.
        List<String> results = characters
            // Create a stream of characters from William
            // Shakespeare's Hamlet.
            .stream()

            // Remove any strings that don't start with 'h' or 'H'.
            .filter(s -> toLowerCase(s.charAt(0)) == 'h')

            // Capitalize the first letter in the string.
            .map(this::capitalize)

            // Sort the results in ascending order.
            .sorted()

            // Terminal operation that triggers aggregate operation
            // processing and collects the results into a list.
            .collect(toList());
            
        // Print the results.
        System.out.println(results);
    }
 
    /**
     * Run an example using the collect() and the two parameter
     * version of the reduce() terminal operations.
     */
    private void runCollectReduce() {
        System.out.println("\nResults from runCollectReduce():");

        // Create map of Hamlet characters starting with 'h' or 'H'
        // and the length of each characters name.
        Map<String, Long> matchingCharactersMap = Pattern
            // Create a stream of characters from William
            // Shakespeare's Hamlet.
            .compile(",")
            .splitAsStream("horatio,claudius,Gertrude,Hamlet,laertes,Ophelia")

            // Remove any strings that don't start with 'h' or 'H'.
            .filter(s -> toLowerCase(s.charAt(0)) == 'h')

            // Capitalize the first letter in the string.
            .map(this::capitalize)

            // Terminal operation that triggers aggregate operation
            // processing and groups the results into a map whose keys
            // are strings of matching Hamlet characters and whose
            // values are the length of each string.
            .collect(groupingBy(identity(),
                                // Use a TreeMap to sort the results.
                                TreeMap::new,
                                summingLong(String::length)));

        // Count of the length of each Hamlet character names that
        // start with 'h' or 'H'.
        long countOfCharacterNameLengths = matchingCharactersMap
            // Extract values (i.e., Long count of string lengths)
            // from the map.
            .values()

            // Convert these values into a stream.
            .stream()

            // Terminal operation that triggers aggregate operation
            // processing and sums up the lengths of each name.
            .reduce(0L,
                    // Could use Long::sum method reference here.
                    (x, y) -> x + y);
            // Could use .sum() terminal operation here.
            
        // Print the results.
        System.out.println("Count of lengths of Hamlet characters' names "
                           // Get the list of character names.
                           + matchingCharactersMap.keySet()
                           + " starting with 'h' or 'H' = "
                           + countOfCharacterNameLengths);
    }

    /**
     * Run an example show the three parameter reduce() terminal
     * operation, which also plays the role of "map" in map-reduce.
     */
    private void runMapReduce() {
        System.out.println("\nResults from runMapReduce():");

        List<String> characterList = Pattern
            // Create a stream of characters from William
            // Shakespeare's Hamlet.
            .compile(",")
            .splitAsStream("horatio,claudius,Gertrude,Hamlet,laertes,Ophelia")

            // Remove any strings that don't start with 'h' or 'H'.
            .filter(s -> toLowerCase(s.charAt(0)) == 'h')
            
            // Capitalize the first letter in the string.
            .map(this::capitalize)

            // Sort the results in ascending order.
            .sorted()

            // Terminal operation that triggers aggregate operation
            // processing and collects the results into a list.
            .collect(toList());
                 
        // Count of the length of each Hamlet character names that
        // start with 'h' or 'H'.
        long countOfCharacterNameLengths = characterList
            // Convert the list of strings into a stream of strings.
            .stream()

            // Terminal operation that triggers aggregate operation
            // processing and uses the three-parameter version of
            // reduce() to sum the length of each name.  This approach
            // is overkill here, but is useful for parallel streams.
            .reduce(0L,
                    // This is the "map" operation.
                    (sum, s) -> sum + s.length(),
                    // This is the "reduce" operation.
                    Long::sum);
            
        // Print the results.
        System.out.println("Count of lengths of Hamlet characters' names "
                           // Get the list of character names.
                           + characterList
                           + " starting with 'h' or 'H' = "
                           + countOfCharacterNameLengths);
    }
 }

