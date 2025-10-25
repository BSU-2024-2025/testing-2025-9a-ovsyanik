import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

  private static final double PRECISION = 0.00001;

  @ParameterizedTest(name = "✅ Позитивный тест {index}: {0} = {1}")
  @MethodSource("providePositiveTestCases")
  void testPositiveCases(String expression, double expected) {
    System.out.println("🔍 Тестируем: " + expression + " → Ожидаем: " + expected);

    try {
      double result = Calculator.calculate(expression);
      System.out.println("   ✅ Результат: " + result);
      assertEquals(expected, result, PRECISION,
              "❌ Ошибка в выражении: " + expression + " → ожидалось: " + expected + ", получено: " + result);
    } catch (Exception e) {
      System.out.println("   ❌ Исключение: " + e.getMessage());
      fail("❌ Неожиданное исключение в выражении '" + expression + "': " + e.getMessage());
    }
  }

  private static Stream<Arguments> providePositiveTestCases() {
    return Stream.of(
            Arguments.of("3+2", 5.0),
            Arguments.of("2-5", -3.0),
            Arguments.of("2/5", 0.4),
            Arguments.of("2*5", 10.0),
            Arguments.of("2+3*4", 14.0),
            Arguments.of("(2+3)*4", 20.0),
            Arguments.of("10/(2+3)", 2.0),
            Arguments.of("7%4", 3.0),
            Arguments.of("7%4+6", 9.0),
            Arguments.of("(8-3)*(2+2)", 20.0),
            Arguments.of("((2+3)*2)", 10.0),
            Arguments.of("3+4*2/(1-5)", 1.0),
            Arguments.of("-5+3", -2.0),
            Arguments.of("2^3", 8.0),
            Arguments.of("5!", 120.0),
            Arguments.of("log(100)", 2.0),
            Arguments.of("abs(-5)", 5.0),
            Arguments.of("√9", 3.0)
    );
  }

  @ParameterizedTest(name = "🚫 Негативный тест {index}: {0} → {1}")
  @MethodSource("provideNegativeTestCases")
  void testNegativeCases(String expression, String expectedError) {
    System.out.println("🔍 Тестируем ошибку: " + expression + " → Ожидаем: " + expectedError);

    CalculatorException exception = assertThrows(CalculatorException.class, () -> {
      Calculator.calculate(expression);
    }, "❌ Ожидалось исключение для выражения: " + expression);

    String actualMessage = exception.getMessage();
    System.out.println("   ✅ Получено исключение: " + actualMessage);

    assertTrue(actualMessage.contains(expectedError),
            "❌ Неверное сообщение об ошибке.\n" +
                    "Выражение: " + expression + "\n" +
                    "Ожидалось: '" + expectedError + "'\n" +
                    "Получено: '" + actualMessage + "'");
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
            Arguments.of("     ", "Ошибка вычисления"),

            Arguments.of("√(-4)", "Невозможно извлечь корень из отрицательного числа"),
            Arguments.of("(-5)!", "Факториал определен только для неотрицательных чисел"),
            Arguments.of("log(-1)", "Логарифм определен только для положительных чисел")
    );
  }

  @ParameterizedTest(name = "🎯 Граничный тест {index}: {0} = {1}")
  @MethodSource("provideEdgeCases")
  void testEdgeCases(String expression, double expected) {
    System.out.println("🔍 Тестируем граничный случай: " + expression + " → Ожидаем: " + expected);

    try {
      double result = Calculator.calculate(expression);
      System.out.println("   ✅ Результат: " + result);
      assertEquals(expected, result, PRECISION,
              "❌ Ошибка в граничном случае: " + expression + " → ожидалось: " + expected + ", получено: " + result);
    } catch (Exception e) {
      System.out.println("   ❌ Исключение: " + e.getMessage());
      fail("❌ Неожиданное исключение в граничном случае '" + expression + "': " + e.getMessage());
    }
  }

  private static Stream<Arguments> provideEdgeCases() {
    return Stream.of(
            Arguments.of("1000000 + 2000000", 3000000.0),
            Arguments.of("0.000001 * 1000000", 1.0),

            Arguments.of("sin(-3.14159)", 0.0),
            Arguments.of("cos(-3.14159)", -1.0),
            Arguments.of("exp(-1)", 0.367879),

            Arguments.of("-(-5)", 5.0),
            Arguments.of("-(-(-5))", -5.0),

            Arguments.of("sin(cos(exp(0)))", 0.84147),
            Arguments.of("1 + sin(2 + cos(1))", 1.425),

            Arguments.of("sin(2)*cos(1)", 0.454649),
            Arguments.of("sin(2*cos(1))", 0.968912),

            Arguments.of("2*sin(3.14159/2)+3*cos(0)", 5.0),
            Arguments.of("(exp(1)-1)/(exp(1)+1)", 0.462117)
    );
  }

  @Test
  void testVerySmallNumbers() {
    String expression = "0.0000001 * 10000000";
    double expected = 1.0;

    System.out.println("🔍 Тестируем малые числа: " + expression);

    double result = Calculator.calculate(expression);
    System.out.println("   ✅ Результат: " + result);

    assertEquals(expected, result, PRECISION,
            "❌ Ошибка с малыми числами: " + expression + " → ожидалось: " + expected + ", получено: " + result);
  }

  @Test
  void testTrigonometricPrecision() {
    String expression = "sin(3.141592653589793/2)";
    double expected = 1.0;
    double precision = 0.0000001;

    System.out.println("🔍 Тестируем тригонометрическую точность: " + expression);

    double result = Calculator.calculate(expression);
    System.out.println("   ✅ Результат: " + result);

    assertEquals(expected, result, precision,
            "❌ Ошибка точности в тригонометрии: " + expression + " → ожидалось: " + expected + ", получено: " + result);
  }

  @Test
  void testExponentialPrecision() {
    String expression = "exp(2)";
    double expected = 7.389056;
    double precision = 0.0001;

    System.out.println("🔍 Тестируем экспоненциальную точность: " + expression);

    double result = Calculator.calculate(expression);
    System.out.println("   ✅ Результат: " + result);

    assertEquals(expected, result, precision,
            "❌ Ошибка точности в экспоненте: " + expression + " → ожидалось: " + expected + ", получено: " + result);
  }

  // Дополнительный тест для демонстрации работы
  @Test
  void testAllOperations() {
    System.out.println("🎯 Комплексный тест всех операций");

    String[] expressions = {
            "2+3*4", "(2+3)*4", "10/2", "7%4", "2^3", "5!", "log(100)", "abs(-5)", "√9"
    };

    double[] expected = {
            14.0, 20.0, 5.0, 3.0, 8.0, 120.0, 2.0, 5.0, 3.0
    };

    for (int i = 0; i < expressions.length; i++) {
      String expr = expressions[i];
      double exp = expected[i];

      System.out.println("   🔍 " + expr + " → " + exp);
      double result = Calculator.calculate(expr);
      System.out.println("      ✅ " + result);

      assertEquals(exp, result, PRECISION, "Ошибка в операции: " + expr);
    }
  }
}