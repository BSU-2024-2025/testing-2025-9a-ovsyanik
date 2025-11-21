import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CalculatorIdeTest {

  private final Interpreter interpreter = new Interpreter();

  private double getLastResult(String output) {
    String[] lines = output.trim().split("\n");
    String lastLine = lines[lines.length - 1];

    String valueStr;
    if (lastLine.contains("→ ")) {
      valueStr = lastLine.substring(lastLine.indexOf("→ ") + 2).trim();
    } else if (lastLine.contains("=")) {
      valueStr = lastLine.substring(lastLine.indexOf("=") + 1).trim();
    } else {
      throw new IllegalArgumentException("Не удалось распарсить результат: " + lastLine);
    }
    return Double.parseDouble(valueStr);
  }

  @ParameterizedTest
  @CsvSource({
          // Простые операции
          "2 + 3, 5.0",
          "10 - 5, 5.0",
          "4 * 3, 12.0",
          "15 / 3, 5.0",
          "2 + 3 * 4, 14.0",
          "(2 + 3) * 4, 20.0",

          // Отрицательные числа
          "-5, -5.0",
          "-1-1, -2.0",
          "0 - 5, -5.0",
          "2 * -3, -6.0",
          "(2 - 4), -2.0",

          // Десятичные числа
          "2.5 + 3.5, 6.0",
          "10.5 - 5.2, 5.3",
          "2.5 * 4, 10.0",
          "15.0 / 2.0, 7.5",

          // Многоуровневые скобки
          "((2 + 3) * (4 - 1)), 15.0",
          "4 + (1 + 2) * 3, 13.0",

          // Функции
          "sin(0), 0.0",
          "cos(0), 1.0",
          "sin(90), 1.0",
          "cos(180), -1.0",
          "log(1), 0.0",
          "sqrt(16), 4.0",

          // Константы и степени
          "e^0, 1.0",
          "e^1, 2.718281828459045",
          "pi, 3.141592653589793",
  })
  void testEvaluateMathExpressions(String expression, double expectedResult) {
    String resultOutput = interpreter.execute(expression);
    double actual = getLastResult(resultOutput);
    assertEquals(expectedResult, actual, 0.001, "Ошибка в выражении: " + expression);
  }

  @ParameterizedTest
  @CsvSource({
          // Переменные
          "'x=5\nx', 5.0",
          "'x=2\ny=3\nx+y', 5.0",
          "'x=5\ny=2+x\ny*5', 35.0",
          "'a=2\nb=3\nc=a*b\nc+1', 7.0",
          "'x=10\ny=x/2\nz=x+y\nz', 15.0",
          // Переопределение
          "'x=5\nx=10\nx', 10.0",
          "'x=2\nx=x+3\nx', 5.0",
  })
  void testVariables(String script, double expectedResult) {
    String resultOutput = interpreter.execute(script);
    double actual = getLastResult(resultOutput);
    assertEquals(expectedResult, actual, 0.001, "Ошибка в скрипте:\n" + script);
  }

  @Test
  void testIfElseTrue() {
    String code = "x = 5\n" +
            "y = 3\n" +
            "if (x > y) {\n" +
            "    res = 10\n" +
            "} else {\n" +
            "    res = 20\n" +
            "}\n" +
            "res";
    String output = interpreter.execute(code);
    assertEquals(10.0, getLastResult(output), 0.001);
  }

  @Test
  void testIfElseFalse() {
    String code = "x = 1\n" +
            "if (x > 5) {\n" +
            "    res = 100\n" +
            "} else {\n" +
            "    res = 200\n" +
            "}\n" +
            "res";
    String output = interpreter.execute(code);
    assertEquals(200.0, getLastResult(output), 0.001);
  }

  @Test
  void testIfWithEquality() {
    String code = "x = 5\n" +
            "if (x == 5) {\n" +
            "   y = 1\n" +
            "} else {\n" +
            "   y = 0\n" +
            "}\n" +
            "y";
    assertEquals(1.0, getLastResult(interpreter.execute(code)), 0.001);
  }

  @Test
  void testComments() {
    String code = "// Это комментарий\n" +
            "x = 10\n" +
            "// Еще один комментарий\n" +
            "x + 5";
    assertEquals(15.0, getLastResult(interpreter.execute(code)), 0.001);
  }

  @Test
  void testDivisionByZero() {
    String code = "5 / 0";
    String output = interpreter.execute(code);
    assertTrue(output.contains("Деление на ноль"), "Должно быть сообщение об ошибке деления на ноль");
  }

  @Test
  void testUnknownFunction() {
    String code = "unknown(5)";
    String output = interpreter.execute(code);
    assertTrue(output.contains("Ошибка в строке") || output.contains("NumberFormatException"),
            "Должна быть ошибка на неизвестной функции");
  }

  @Test
  void testMismatchedParenthesis() {
    String code = "(2 + 3";
    String output = interpreter.execute(code);
    assertTrue(output.contains("Ошибка") || output.contains("StringIndexOutOfBounds"),
            "Должна быть ошибка скобок");
  }
}