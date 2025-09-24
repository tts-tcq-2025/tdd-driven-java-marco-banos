package src.test.java.tests;


import org.junit.jupiter.api.Test;
import src.main.java.services.StringCalculatorService;
import src.main.java.services.StringCalculatorServiceImpl;
import src.main.java.utils.Printer;

import static org.junit.jupiter.api.Assertions.*;

class TddDrivenTest {

    private final StringCalculatorService calc = new StringCalculatorServiceImpl();

    @Test
    void emptyStringReturnsZero() {
        int result = calc.add("");
        assertEquals(0, result);
        Printer.success("emptyStringReturnsZero passed with result = " + result);
    }

    @Test
    void singleNumberReturnsValue() {
        int result = calc.add("1");
        assertEquals(1, result);
        Printer.success("singleNumberReturnsValue passed with result = " + result);
    }

    @Test
    void twoNumbersCommaSeparatedReturnSum() {
        int result = calc.add("1,2");
        assertEquals(3, result);
        Printer.success("twoNumbersCommaSeparatedReturnSum passed with result = " + result);
    }

    @Test
    void unknownAmountOfNumbersIsAllowed() {
        int result = calc.add("1,2,3,4");
        assertEquals(10, result);
        Printer.success("unknownAmountOfNumbersIsAllowed passed with result = " + result);
    }

    @Test
    void newLinesBetweenNumbersAreAllowed() {
        assertEquals(6, calc.add("1\n2,3"));
    }

    @Test
    void customSingleCharacterDelimiter() {
        assertEquals(3, calc.add("//;\n1;2"));
    }

    @Test
    void negativeNumbersThrowExceptionWithAllNegativesListed() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> calc.add("1,-2,-3,4"));
        String msg = ex.getMessage();
        assertTrue(msg.contains("negatives not allowed"));
        assertTrue(msg.contains("-2"));
        assertTrue(msg.contains("-3"));
    }

    @Test
    void numbersGreaterThan1000AreIgnored() {
        assertEquals(2, calc.add("2,1001"));
    }

    @Test
    void delimitersOfAnyLengthAreSupported() {
        assertEquals(6, calc.add("//[***]\n1***2***3"));
    }

    @Test
    void multipleDelimitersAreSupported() {
        assertEquals(6, calc.add("//[*][%]\n1*2%3"));
    }

    @Test
    void complexCombinationExample() {
        assertEquals(10, calc.add("//[***][%]\n1***2%3,4\n0"));
    }
}
