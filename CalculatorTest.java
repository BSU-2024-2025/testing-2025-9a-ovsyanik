import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

class CalculatorTest {

  private static final double PRECISION = 0.00001;

  // Positive tests
  @ParameterizedTest(name = "Positive {index}: {0} = {1}")
  @MethodSource("providePositiveTestCases")
  void testPositiveCases(String expression, double expected, double precision) {
    double result = Calculator.calculate(expression);
    assertEquals(expected, result, precision);
  }

  private static Stream<Arguments> providePositiveTestCases() {
    return Stream.of(
            Arguments.of("3+2", 5.0, PRECISION),
            Arguments.of("2-5", -3.0, PRECISION),
            Arguments.of("2/5", 0.4, PRECISION),
            Arguments.of("2*5", 10.0, PRECISION),
            Arguments.of("2+3*4", 14.0, PRECISION),
            Arguments.of("(2+3)*4", 20.0, PRECISION),
            Arguments.of("10/(2+3)", 2.0, PRECISION),
            Arguments.of("7%4", 3.0, PRECISION),
            Arguments.of("7%4+6", 9.0, PRECISION),
            Arguments.of("(8-3)*(2+2)", 20.0, PRECISION),
            Arguments.of("((2+3)*2)", 10.0, PRECISION),
            Arguments.of("3+4*2/(1-5)", 1.0, PRECISION)
    );
  }

  // Negative tests
  @ParameterizedTest(name = "Negative {index}: {0} должно выбросить \"{1}\"")
  @MethodSource("provideNegativeTestCases")
  void testNegativeCases(String expression, String expectedError) {
    Exception exception = assertThrows(CalculatorException.class, () -> {
      Calculator.calculate(expression);
    });
    assertEquals(expectedError, exception.getMessage());
  }

  private static Stream<Arguments> provideNegativeTestCases() {
    return Stream.of(
            Arguments.of("10/0", "Деление на ноль"),
            Arguments.of("1/(1-1)", "Деление на ноль"),
            Arguments.of("5 + 2/(3-3)", "Деление на ноль"),

            Arguments.of("(2+3", "Несбалансированные скобки"),
            Arguments.of("2+3)", "Несбалансированные скобки"),
            Arguments.of("((2+3)", "Несбалансированные скобки"),
            Arguments.of("(2+3))", "Несбалансированные скобки"),
            Arguments.of("sin(cos(0)", "Несбалансированные скобки"),

            Arguments.of("abc+1", "Недопустимый символ"),
            Arguments.of("2 + x", "Недопустимый символ"),
            Arguments.of("sinx(1)", "Недопустимый символ"),
            Arguments.of("2#3", "Недопустимый символ"),

            Arguments.of("++2", "Недопустимый символ"),
            Arguments.of("2..3", "Недопустимый символ"),
            Arguments.of("sin()", "Ошибка в выражении"),
            Arguments.of("+", "Ошибка в выражении"),
            Arguments.of("*5", "Ошибка в выражении"),

            Arguments.of("unknown(1)", "Недопустимый символ"),
            Arguments.of("sine(1)", "Недопустимый символ"),
            Arguments.of("2 + unknown(5)", "Недопустимый символ"),

            Arguments.of("", "Ошибка вычисления"),
            Arguments.of("()", "Ошибка в выражении"),
            Arguments.of("     ", "Ошибка вычисления")
    );
  }

  // Edge cases
  @ParameterizedTest(name = "Edge Case {index}: {0} = {1}")
  @MethodSource("provideEdgeCases")
  void testEdgeCases(String expression, double expected, double precision) {
    double result = Calculator.calculate(expression);
    assertEquals(expected, result, precision);
  }

  private static Stream<Arguments> provideEdgeCases() {
    return Stream.of(
            Arguments.of("1000000 + 2000000", 3000000.0, PRECISION),
            Arguments.of("0.000001 * 1000000", 1.0, PRECISION),

            Arguments.of("sin(-3.14159)", 0.0, 0.0001),
            Arguments.of("cos(-3.14159)", -1.0, 0.0001),
            Arguments.of("exp(-1)", 0.367879, 0.001),

            Arguments.of("-(-5)", 5.0, PRECISION),
            Arguments.of("-(-(-5))", -5.0, PRECISION),

            Arguments.of("sin(cos(exp(0)))", 0.84147, 0.001),
            Arguments.of("1 + sin(2 + cos(1))", 1.425, 0.001),

            Arguments.of("sin(2)*cos(1)", 0.454649, 0.001),
            Arguments.of("sin(2*cos(1))", 0.968912, 0.001),

            Arguments.of("2*sin(3.14159/2)+3*cos(0)", 5.0, 0.0001),
            Arguments.of("(exp(1)-1)/(exp(1)+1)", 0.462117, 0.001)
    );
  }

  // Precision tests
  @Test
  void testVerySmallNumbers() {
    double result = Calculator.calculate("0.0000001 * 10000000");
    assertEquals(1.0, result, PRECISION);
  }

  @Test
  void testTrigonometricPrecision() {
    double result = Calculator.calculate("sin(3.141592653589793/2)");
    assertEquals(1.0, result, 0.0000001);
  }

  @Test
  void testExponentialPrecision() {
    double result = Calculator.calculate("exp(2)");
    assertEquals(7.389056, result, 0.0001);
  }

  // Additional operations tests
  @Test
  void testModulo() {
    double result = Calculator.calculate("10 % 3");
    assertEquals(1.0, result, PRECISION);
  }

  @Test
  void testPower() {
    double result = Calculator.calculate("2 ** 3");
    assertEquals(8.0, result, PRECISION);
  }

  @Test
  void testSquareRoot() {
    double result = Calculator.calculate("√9");
    assertEquals(3.0, result, PRECISION);
  }

  @Test
  void testAbsoluteValue() {
    double result = Calculator.calculate("abs(-5)");
    assertEquals(5.0, result, PRECISION);
  }

  @Test
  void testFactorial() {
    double result = Calculator.calculate("5!");
    assertEquals(120.0, result, PRECISION);
  }

  @Test
  void testLogarithm() {
    double result = Calculator.calculate("log(100)");
    assertEquals(2.0, result, PRECISION);
  }

  @Test
  void testSquareRootOfNegativeNumber() {
    Exception exception = assertThrows(CalculatorException.class, () -> {
      Calculator.calculate("√(-4)");
    });
    assertEquals("Невозможно извлечь корень из отрицательного числа", exception.getMessage());
  }

  @Test
  void testFactorialOfNegativeNumber() {
    Exception exception = assertThrows(CalculatorException.class, () -> {
      Calculator.calculate("(-5)!");
    });
    assertEquals("Факториал определен только для неотрицательных чисел", exception.getMessage());
  }
}

// Вспомогательные классы (должны быть реализованы в вашем проекте)
class CalculatorException extends RuntimeException {
  public CalculatorException(String message) {
    super(message);
  }
}

class Calculator {
  public static double calculate(String expression) {
    // Реализация вашего калькулятора
    // Этот метод должен бросать CalculatorException с соответствующими сообщениями
    return 0.0; // заглушка
  }
}