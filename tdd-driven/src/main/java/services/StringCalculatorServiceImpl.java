package src.main.java.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringCalculatorServiceImpl implements StringCalculatorService {

    @Override
    public int add(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        ParsedInput parsed = parseInput(input);
        int[] numbers = splitToInts(parsed.numbersPart, parsed.delimiterRegex);
        checkNegatives(numbers);
        return sum(numbers);
    }

    // ---------- Helpers and small data holder ----------

    // Holder for parsed result
    private static class ParsedInput {
        final String numbersPart;
        final String delimiterRegex;

        ParsedInput(String numbersPart, String delimiterRegex) {
            this.numbersPart = numbersPart;
            this.delimiterRegex = delimiterRegex;
        }
    }

    /**
     * Parse header if present and build a regex to split numbers.
     * Supports:
     *  - default delimiters comma and newline
     *  - custom single delimiter: //;\n1;2
     *  - custom delimiter(s) of any length: //[***]\n1***2***3
     *  - multiple custom delimiters: //[*][%]\n1*2%3
     */
    private ParsedInput parseInput(String input) {
        final List<String> baseDelims = new ArrayList<>(Arrays.asList(",", "\n"));

        if (!input.startsWith("//")) {
            String regex = buildRegex(baseDelims);
            return new ParsedInput(input, regex);
        }

        int newlineIdx = input.indexOf('\n');
        String header = input.substring(2, newlineIdx);
        String numbersPart = input.substring(newlineIdx + 1);

        List<String> custom = parseDelimitersFromHeader(header);
        // Keep existing delimiters as well (requirement: keep existing scenarios supported)
        baseDelims.addAll(custom);

        String regex = buildRegex(baseDelims);
        return new ParsedInput(numbersPart, regex);
    }

    /**
     * Extract delimiters from header.
     * If header contains bracketed delimiters [delim] it extracts all of them.
     * Otherwise header itself is the delimiter.
     */
    private List<String> parseDelimitersFromHeader(String header) {
        List<String> delims = new ArrayList<>();
        if (header.startsWith("[") && header.contains("]")) {
            Matcher m = Pattern.compile("\\[(.*?)]").matcher(header);
            while (m.find()) {
                delims.add(m.group(1));
            }
        } else {
            delims.add(header);
        }
        return delims;
    }

    // Build regex from delimiters, quoting special chars; sort by length desc to favor multi-char delimiters
    private String buildRegex(List<String> delims) {
        delims.sort((a, b) -> Integer.compare(b.length(), a.length()));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < delims.size(); i++) {
            if (i > 0) sb.append("|");
            sb.append(Pattern.quote(delims.get(i)));
        }
        return sb.toString();
    }

    // Split numbers and parse ints (ignore empty tokens)
    private int[] splitToInts(String numbers, String regex) {
        if (numbers.isEmpty()) return new int[0];
        String[] tokens = numbers.split(regex);
        List<Integer> list = new ArrayList<>();
        for (String t : tokens) {
            if (t == null || t.trim().isEmpty()) continue;
            try {
                list.add(Integer.parseInt(t.trim()));
            } catch (NumberFormatException e) {
                // ignore tokens that are not integers (defensive)
            }
        }
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    // Throw if any negative numbers are present (collect all negatives)
    private void checkNegatives(int[] nums) {
        List<Integer> negatives = new ArrayList<>();
        for (int n : nums) {
            if (n < 0) negatives.add(n);
        }
        if (!negatives.isEmpty()) {
            throw new IllegalArgumentException("negatives not allowed: " + joinInts(negatives));
        }
    }

    // Sum numbers ignoring numbers > 1000
    private int sum(int[] nums) {
        int s = 0;
        for (int n : nums) {
            if (n <= 1000) s += n;
        }
        return s;
    }

    private String joinInts(Collection<Integer> ints) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Integer i : ints) {
            if (!first) sb.append(",");
            sb.append(i);
            first = false;
        }
        return sb.toString();
    }
}
