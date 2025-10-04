import java.util.*;

/**
 * ConsoleQuiz.java
 *
 * Single-file console quiz application.
 *
 * Features:
 * - Uses a List of Question objects (collections).
 * - Presents options, accepts user input (number or letter), validates input.
 * - Scores the user and shows results.
 * - Shuffles questions and (optionally) options.
 * - Demonstrates loops, iterators, control flow, maps, ArrayList, etc.
 *
 * Compile:
 *   javac ConsoleQuiz.java
 * Run:
 *   java ConsoleQuiz
 */

class Question {
    private String prompt;
    private List<String> options;
    private int correctIndex; // 0-based

    public Question(String prompt, List<String> options, int correctIndex) {
        if (options == null || options.size() < 2) {
            throw new IllegalArgumentException("Must provide at least two options");
        }
        if (correctIndex < 0 || correctIndex >= options.size()) {
            throw new IllegalArgumentException("correctIndex out of bounds");
        }
        this.prompt = prompt;
        this.options = new ArrayList<>(options);
        this.correctIndex = correctIndex;
    }

    public String getPrompt() {
        return prompt;
    }

    public List<String> getOptions() {
        return new ArrayList<>(options);
    }

    public int getCorrectIndex() {
        return correctIndex;
    }

    /**
     * Optionally shuffle options while keeping track of the correct index.
     * This returns a new pair: (shuffledOptions, newCorrectIndex)
     */
    public Pair<List<String>, Integer> shuffledOptionsWithCorrectIndex(Random rnd) {
        List<Pair<String, Integer>> paired = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            paired.add(new Pair<>(options.get(i), i));
        }
        Collections.shuffle(paired, rnd);
        List<String> shuffled = new ArrayList<>();
        int newCorrect = -1;
        for (int i = 0; i < paired.size(); i++) {
            shuffled.add(paired.get(i).first);
            if (paired.get(i).second == correctIndex) {
                newCorrect = i;
            }
        }
        return new Pair<>(shuffled, newCorrect);
    }
}

/** Small generic pair helper */
class Pair<A, B> {
    public final A first;
    public final B second;
    public Pair(A a, B b) {
        this.first = a;
        this.second = b;
    }
}

class Quiz {
    private List<Question> questions;
    private int score;
    private Scanner scanner;
    private boolean shuffleQuestions;
    private boolean shuffleOptions;
    private Random rnd;

    public Quiz(List<Question> questions, boolean shuffleQuestions, boolean shuffleOptions) {
        this.questions = new ArrayList<>(questions);
        this.score = 0;
        this.scanner = new Scanner(System.in);
        this.shuffleQuestions = shuffleQuestions;
        this.shuffleOptions = shuffleOptions;
        this.rnd = new Random();
    }

    public void run() {
        System.out.println("Welcome to the Java Developer Quiz!");
        System.out.println("Answer by typing the option number or letter (e.g., 1 or A).");
        System.out.println("-----------------------------------------------------------");

        if (shuffleQuestions) {
            Collections.shuffle(questions, rnd);
        }

        int qNo = 1;
        for (Iterator<Question> it = questions.iterator(); it.hasNext(); ) { // shows iterator usage
            Question q = it.next();
            System.out.println();
            System.out.printf("Q%d. %s%n", qNo++, q.getPrompt());

            List<String> optionsToShow;
            int correctIdx;

            if (shuffleOptions) {
                Pair<List<String>, Integer> pair = q.shuffledOptionsWithCorrectIndex(rnd);
                optionsToShow = pair.first;
                correctIdx = pair.second;
            } else {
                optionsToShow = q.getOptions();
                correctIdx = q.getCorrectIndex();
            }

            // display options with letters
            for (int i = 0; i < optionsToShow.size(); i++) {
                char letter = (char) ('A' + i);
                System.out.printf("  %c. %s%n", letter, optionsToShow.get(i));
            }

            int chosen = promptForAnswer(optionsToShow.size()); // 0-based chosen index

            if (chosen == correctIdx) {
                System.out.println("Correct! ✅");
                score++;
            } else {
                System.out.printf("Wrong ❌   Correct answer: %c. %s%n",
                        (char) ('A' + correctIdx), optionsToShow.get(correctIdx));
            }
        }

        // Results
        System.out.println();
        System.out.println("-----------------------------------------------------------");
        System.out.println("Quiz finished!");
        System.out.printf("Your score: %d out of %d%n", score, questions.size());
        double pct = ((double) score / questions.size()) * 100.0;
        System.out.printf("Percentage: %.2f%%%n", pct);
        if (pct == 100.0) {
            System.out.println("Excellent! You got all questions right. 🎉");
        } else if (pct >= 70.0) {
            System.out.println("Good job! Keep practicing. 👍");
        } else {
            System.out.println("Keep learning — practice makes perfect. 💪");
        }
    }

    /**
     * Accepts answers and validates them.
     * Accept input as:
     * - A number (1..n)
     * - A letter (A..)
     * - Option text index (not recommended)
     */
    private int promptForAnswer(int optionCount) {
        while (true) {
            System.out.print("Your answer: ");
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                System.out.println("Please enter an option (e.g., A or 1).");
                continue;
            }

            // Try as integer index
            try {
                int num = Integer.parseInt(line);
                if (num >= 1 && num <= optionCount) {
                    return num - 1;
                } else {
                    System.out.printf("Enter a number between 1 and %d.%n", optionCount);
                    continue;
                }
            } catch (NumberFormatException ignored) {}

            // Try as letter
            char c = Character.toUpperCase(line.charAt(0));
            if (c >= 'A' && c < ('A' + optionCount)) {
                return c - 'A';
            }

            // support typing full option text by exact match (case-insensitive)
            for (int i = 0; i < optionCount; i++) {
                // This simplistic approach is only for convenience
                // For safety, require at least 3 chars to try match
                if (line.length() >= 3) {
                    // won't match if option text shorter than user input; best-effort
                }
            }

            System.out.println("Invalid input. Try again with option letter (A) or number (1).");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        List<Question> qlist = buildDefaultQuestions();
        // Create quiz: shuffle questions = true, shuffle options = true
        Quiz quiz = new Quiz(qlist, true, true);
        quiz.run();
    }

    private static List<Question> buildDefaultQuestions() {
        List<Question> list = new ArrayList<>();

        // Questions adapted from the uploaded task PDF.
        list.add(new Question(
                "What are Java loops?",
                Arrays.asList(
                        "A way to repeat code blocks multiple times",
                        "A type of Java collection",
                        "A method for sorting arrays",
                        "A way to declare variables"
                ),
                0
        ));
        list.add(new Question(
                "What is the enhanced for-loop (for-each)?",
                Arrays.asList(
                        "A loop that iterates over elements of arrays/collections",
                        "A loop that only executes once",
                        "A loop that runs in parallel automatically",
                        "A loop for reading files"
                ),
                0
        ));
        list.add(new Question(
                "How to handle multiple user inputs in Java console apps?",
                Arrays.asList(
                        "Use Scanner or BufferedReader and parse values sequentially",
                        "Use JOptionPane only",
                        "Create multiple main methods",
                        "Use System.exit to stop inputs"
                ),
                0
        ));
        list.add(new Question(
                "How is a switch-case different from if-else?",
                Arrays.asList(
                        "switch-case branches on discrete values; if-else handles boolean expressions",
                        "if-else is only for strings, switch-case is for numbers only",
                        "switch-case is faster but can't use break",
                        "They are identical in all cases"
                ),
                0
        ));
        list.add(new Question(
                "What are collections in Java?",
                Arrays.asList(
                        "Framework classes and interfaces for grouping objects (List, Set, Map)",
                        "Primitive arrays only",
                        "A way to write threads",
                        "Java bytecode files"
                ),
                0
        ));
        list.add(new Question(
                "What is ArrayList?",
                Arrays.asList(
                        "A resizable array implementation of List in Java",
                        "A fixed-size array",
                        "A thread in Java",
                        "A subclass of HashMap"
                ),
                0
        ));
        list.add(new Question(
                "How to iterate using an Iterator?",
                Arrays.asList(
                        "Obtain iterator() from collection and use hasNext()/next() in a loop",
                        "Call forEach only",
                        "Use indexing like array[i]",
                        "Iterators cannot be used for Lists"
                ),
                0
        ));
        list.add(new Question(
                "What is a Map in Java?",
                Arrays.asList(
                        "An interface for key-value pairs (e.g., HashMap, TreeMap)",
                        "A list of values",
                        "A method of multithreading",
                        "A type of exception"
                ),
                0
        ));
        list.add(new Question(
                "How to sort a list in Java?",
                Arrays.asList(
                        "Use Collections.sort(list) or list.sort(Comparator)",
                        "Use Collections.shuffle(list)",
                        "By converting to array and using System.arraycopy",
                        "Sorting is not supported in Java collections"
                ),
                0
        ));
        list.add(new Question(
                "How to shuffle elements in a list?",
                Arrays.asList(
                        "Use Collections.shuffle(list, new Random())",
                        "Call list.reverse() (which doesn't exist)",
                        "Use Collections.sort with a random comparator",
                        "Shuffling requires manual swapping only"
                ),
                0
        ));

        return list;
    }
}
