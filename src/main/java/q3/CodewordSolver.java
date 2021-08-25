package q3;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.io.Resources;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

public class CodewordSolver {

    static final boolean PRINT_WORDS = false;
    static final boolean PRINT_ANSWER_KEY = false;

    List<String> dictionary;

    public static void main(String[] args) throws Exception {
        CodewordSolver solver = new CodewordSolver();
        solver.run(Resources.readLines(Resources.getResource("q3/input.txt"), StandardCharsets.UTF_8));
        solver.run(Resources.readLines(Resources.getResource("q3/input2.txt"), StandardCharsets.UTF_8));
    }

    public CodewordSolver() throws IOException {
        dictionary = Resources.readLines(Resources.getResource("q3/dictionary.txt"), StandardCharsets.UTF_8);
    }

    private void run(List<String> puzzleInput) throws Exception {
        Stopwatch stopwatch = Stopwatch.createStarted();

        Puzzle puzzle = parse(puzzleInput);

        if (PRINT_WORDS) {
            System.out.println("===");
            for (List<Integer> word : puzzle.words) {
                System.out.println(word.stream().map(Object::toString).collect(Collectors.joining(" ")));
            }
            System.out.println("===");
        }

        Map<Integer, Set<Character>> possibleLettersByCode = new HashMap<>();
        Set<Character> allLetters = new HashSet<>();
        for (char c = 'a'; c <= 'z'; c++) {
            allLetters.add(c);
        }
        // only include numbers that are in use
        for (List<Integer> squaresRow : puzzle.squares) {
            for (Integer code : squaresRow) {
                if (code != null) {
                    possibleLettersByCode.computeIfAbsent(code, c -> new HashSet<>(allLetters));
                }
            }
        }

        for (Map.Entry<Integer, Character> clue : puzzle.clues.entrySet()) {
            Character clueLetter = clue.getValue();
            int clueCode = clue.getKey();
            setCodeToLetter(possibleLettersByCode, clueLetter, clueCode);
        }

        System.out.println("Initialised in " + stopwatch.elapsed());

        System.out.println(solve(puzzle, possibleLettersByCode));
    }

    private void setCodeToLetter(Map<Integer, Set<Character>> possibleLettersByCode, char clueLetter, int clueCode) {
        for (Map.Entry<Integer, Set<Character>> entry : possibleLettersByCode.entrySet()) {
            Integer code = entry.getKey();
            Set<Character> possibleLettersForCode = entry.getValue();
            if (code == clueCode) {
                possibleLettersForCode.retainAll(Collections.singleton(clueLetter));
            } else {
                possibleLettersForCode.remove(clueLetter);
            }
        }
    }

    String solve(Puzzle puzzle, Map<Integer, Set<Character>> possibleLettersByCode) throws UnsolvableEx {
        while (true) {
            boolean progressMade = false;

            for (List<Integer> word : puzzle.words) {

                Map<Integer, Set<Character>> possibleLettersByCodeForThisWord = new HashMap<>();
                dictIter:
                for (String dictWord : dictionary) {
                    if (dictWord.length() != word.size()) {
                        continue;
                    }
                    for (int i = 0; i < dictWord.length(); i++) {
                        char dictLetter = dictWord.charAt(i);
                        int codePoint = word.get(i);
                        if (!possibleLettersByCode.get(codePoint).contains(dictLetter)) {
                            continue dictIter;
                        }
                    }
                    // This dictWord is possible:
                    for (int i = 0; i < dictWord.length(); i++) {
                        char dictLetter = dictWord.charAt(i);
                        int codePoint = word.get(i);
                        possibleLettersByCodeForThisWord.computeIfAbsent(codePoint, (x) -> new HashSet<>())
                                .add(dictLetter);
                    }
                }
                // Now constrain the possibleLettersByCode according to seen possible dict words
                for (int i = 0; i < word.size(); i++) {
                    int codePoint = word.get(i);
                    Set<Character> possibleLetters = possibleLettersByCode.get(codePoint);
                    Set<Character> possibleLettersForCode = possibleLettersByCodeForThisWord.get(codePoint);
                    if (possibleLettersForCode == null || possibleLettersByCode.isEmpty()) {
                        // unsolvable
                        throw new UnsolvableEx();
                    }
                    progressMade |= possibleLetters.retainAll(possibleLettersForCode);

                    // Letters are not used more than once
                    // (not needed for input 1)
                    if (possibleLetters.size() == 1) {
                        Character letter = Iterables.getOnlyElement(possibleLetters);
                        for (Set<Character> otherPossibleLetters : possibleLettersByCode.values()) {
                            if (otherPossibleLetters != possibleLetters) {
                                otherPossibleLetters.remove(letter);
                            }
                        }
                    }
                }
            }

            if (possibleLettersByCode.values().stream().allMatch(s -> s.size() == 1)) {
                return printOut(possibleLettersByCode, puzzle);
            }
            if (!progressMade) {
                // Make a guess, recurse:
                System.out.println("Making a guess...");
                for (Map.Entry<Integer, Set<Character>> entry : possibleLettersByCode.entrySet()) {
                    Set<Character> characters = entry.getValue();
                    if (characters.size() > 1) {
                        List<String> solvesFromGuesses = new ArrayList<>();
                        Integer code = entry.getKey();
                        for (Character character : characters) {
                            Map<Integer, Set<Character>> alt = clone(possibleLettersByCode);
                            setCodeToLetter(alt, character, code);
                            try {
                                solvesFromGuesses.add(solve(puzzle, alt));
                            } catch (UnsolvableEx e) {
                                // continue
                            }
                        }
                        if (solvesFromGuesses.size() == 1) {
                            return solvesFromGuesses.get(0);
                        } else if (solvesFromGuesses.isEmpty()) {
                            throw new UnsolvableEx();
                        } else {
                            throw new RuntimeException("Multiple solutions:\n" +
                                    solvesFromGuesses.stream().collect(Collectors.joining("\n\n")));
                        }
                    }
                }
                throw new RuntimeException("No progress made, no guesses possible");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Integer, Set<Character>> clone(Map<Integer, Set<Character>> m) {
        HashMap<Integer, Set<Character>> out = new HashMap<>();
        for (Map.Entry<Integer, Set<Character>> entry : m.entrySet()) {
            out.put(entry.getKey(), (Set<Character>) ((HashSet<Character>) entry.getValue()).clone());
        }
        return out;
    }

    private String printOut(Map<Integer, Set<Character>> possibleLettersByCode, Puzzle puzzle) {
        StringBuilder acc = new StringBuilder();

        if (PRINT_ANSWER_KEY) {
            acc.append(possibleLettersByCode.entrySet().stream()
                    .sorted(Comparator.comparing(e -> e.getKey()))
                    .map(e -> String.format("%s=%s", e.getKey(), printSet(e.getValue())))
                    .collect(Collectors.joining("\n")));
            acc.append("\n\n");
        }

        int maxEntryLen = possibleLettersByCode.values().stream()
                .mapToInt(cs -> printSet(cs).length())
                .max().getAsInt();
        for (int y = 0; y < puzzle.squares.size(); y++) {
            List<Integer> row = puzzle.squares.get(y);
            for (int x = 0; x < row.size(); x++) {
                Integer cell = row.get(x);
                if (cell == null) {
                    acc.append(Strings.repeat(" ", maxEntryLen));
                } else {
                    Set<Character> characters = possibleLettersByCode.get(cell);
                    acc.append(Strings.padEnd(printSet(characters), maxEntryLen, ' '));
                }
                if (maxEntryLen > 1) {
                    acc.append(" ");
                }
            }
            acc.append("\n");
        }

        return acc.toString();
    }

    private String printSet(Set<Character> characters) {
        if (characters.size() == 0) {
            return "*";
        } else if (characters.size() == 1) {
            return Character.toString(Iterables.getOnlyElement(characters));
        }
        StringBuilder acc = new StringBuilder();
        Character runStart = null;
        for (char c = 'a'; c <= ('z' + 1); c++) {
            if (characters.contains(c)) {
                if (runStart == null) {
                    runStart = c;
                } else {
                    // run continues
                }
            } else {
                if (runStart != null) {
                    // run ends
                    acc.append(runStart);
                    if (c - 2 == runStart) {
                        acc.append((char) (c - 1));
                    } else if (c - 1 > runStart) {
                        acc.append("-").append((char) (c - 1));
                    }
                    runStart = null;
                }
            }
        }
        return acc.toString();
    }

    private Puzzle parse(List<String> puzzleInput) {
        List<List<Integer>> squares = new ArrayList<>();
        Map<Integer, Character> clues = new HashMap<>();
        List<List<Integer>> providedWords = new ArrayList<>();

        int section = 0;
        for (String puzzLine : puzzleInput) {
            if (puzzLine.equals("")) {
                section++;
                continue;
            }
            if (section == 0) {
                // grid
                List<Integer> row = Splitter.on(" ").trimResults().omitEmptyStrings()
                        .splitToStream(puzzLine)
                        .map(x -> ".".equals(x) ? null : Integer.parseInt(x))
                        .collect(Collectors.toList());
                squares.add(row);
            } else if (section == 1) {
                // clues
                String[] ab = puzzLine.split("=");
                checkState(ab.length == 2);
                checkState(ab[1].length() == 1);
                clues.put(Integer.parseInt(ab[0]), ab[1].charAt(0));
            } else if (section == 2) {
                // words
                providedWords.add(Splitter.on(" ").trimResults().omitEmptyStrings()
                        .splitToStream(puzzLine)
                        .map(x -> Integer.parseInt(x))
                        .collect(Collectors.toList()));
            } else {
                throw new IllegalArgumentException();
            }
        }

        // compute words:
        List<List<Integer>> words = new ArrayList<>();
        for (int y = 0; y < squares.size(); y++) {
            List<Integer> row = squares.get(y);
            List<Integer> currWord = new ArrayList<>();
            for (int x = 0; x < row.size(); x++) {
                Integer currCell = row.get(x);
                if (null != currCell) {
                    currWord.add(currCell);
                } else {
                    if (currWord.size() > 1) {
                        words.add(currWord);
                    }
                    currWord = new ArrayList<>();
                }
            }
            if (currWord.size() > 1) {
                words.add(currWord);
            }
        }

        int width = squares.get(0).size();
        int height = squares.size();
        for (int x = 0; x < width; x++) {
            List<Integer> currWord = new ArrayList<>();
            for (int y = 0; y < height; y++) {
                Integer currCell = squares.get(y).get(x);
                if (null != currCell) {
                    currWord.add(currCell);
                } else {
                    if (currWord.size() > 1) {
                        words.add(currWord);
                    }
                    currWord = new ArrayList<>();
                }
            }
            if (currWord.size() > 1) {
                words.add(currWord);
            }
        }

        if (!providedWords.isEmpty()) {
            checkArgument(providedWords.equals(words));
        }

        Puzzle out = new Puzzle();
        out.squares = squares;
        out.words = words;
        out.clues = clues;
        return out;
    }

    private static class Puzzle {
        List<List<Integer>> squares;
        List<List<Integer>> words;
        Map<Integer, Character> clues;
    }

    private static class UnsolvableEx extends Exception {
    }
}