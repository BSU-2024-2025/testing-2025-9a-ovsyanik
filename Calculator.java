import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Calculator {
  private static final Pattern NUMBER_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");
  private static final Pattern FUNCTION_PATTERN = Pattern.compile("(sin|cos|exp|log|abs|sqrt)");
  private static final Pattern TOKEN_PATTERN = Pattern.compile(
          "-?\\d+(\\.\\d+)?|sin|cos|exp|log|abs|sqrt|\\^|!|[+\\-*/%()]"
  );

  private static final Map<String, Integer> OPERATOR_PRECEDENCE = Map.of(
          "+", 1,
          "-", 1,
          "*", 2,
          "/", 2,
          "%", 2,
          "^", 3
  );

  public static double calculate(String expression) {
    if (expression == null || expression.trim().isEmpty()) {
      throw new CalculatorException("Ошибка вычисления");
    }

    String cleanedExpression = expression.replaceAll("\\s+", "");
    List<String> tokens = tokenize(cleanedExpression);
    List<String> rpn = convertToRPN(tokens);
    return evaluateRPN(rpn);
  }

  private static List<String> tokenize(String expression) {
    List<String> tokens = new ArrayList<>();
    Matcher matcher = TOKEN_PATTERN.matcher(expression);

    int lastIndex = 0;
    while (matcher.find()) {
      if (matcher.start() != lastIndex) {
        String invalidChar = expression.substring(lastIndex, matcher.start());
        throw new CalculatorException("Недопустимый символ: " + invalidChar);
      }
      tokens.add(matcher.group());
      lastIndex = matcher.end();
    }

    if (lastIndex != expression.length()) {
      throw new CalculatorException("Недопустимый символ");
    }

    return tokens;
  }

  private static List<String> convertToRPN(List<String> tokens) {
    List<String> output = new ArrayList<>();
    Deque<String> stack = new ArrayDeque<>();

    for (String token : tokens) {
      if (isNumber(token)) {
        output.add(token);
      } else if (isFunction(token)) {
        stack.push(token);
      } else if (token.equals("(")) {
        stack.push(token);
      } else if (token.equals(")")) {
        while (!stack.isEmpty() && !stack.peek().equals("(")) {
          output.add(stack.pop());
        }
        if (stack.isEmpty()) {
          throw new CalculatorException("Несбалансированные скобки");
        }
        stack.pop(); // Remove "("
        if (!stack.isEmpty() && isFunction(stack.peek())) {
          output.add(stack.pop());
        }
      } else if (isOperator(token)) {
        while (!stack.isEmpty() && isOperator(stack.peek()) &&
                getPrecedence(stack.peek()) >= getPrecedence(token)) {
          output.add(stack.pop());
        }
        stack.push(token);
      }
    }

    while (!stack.isEmpty()) {
      String operator = stack.pop();
      if (operator.equals("(")) {
        throw new CalculatorException("Несбалансированные скобки");
      }
      output.add(operator);
    }

    return output;
  }

  private static double evaluateRPN(List<String> rpn) {
    Deque<Double> stack = new ArrayDeque<>();

    for (String token : rpn) {
      if (isNumber(token)) {
        stack.push(Double.parseDouble(token));
      } else if (isFunction(token)) {
        if (stack.isEmpty()) {
          throw new CalculatorException("Ошибка в выражении");
        }
        double operand = stack.pop();
        double result = applyFunction(token, operand);
        stack.push(result);
      } else if (isOperator(token)) {
        if (stack.size() < 2) {
          throw new CalculatorException("Ошибка в выражении");
        }
        double right = stack.pop();
        double left = stack.pop();
        double result = applyOperator(token, left, right);
        stack.push(result);
      }
    }

    if (stack.size() != 1) {
      throw new CalculatorException("Ошибка вычисления");
    }

    return stack.pop();
  }

  private static boolean isNumber(String token) {
    return NUMBER_PATTERN.matcher(token).matches();
  }

  private static boolean isFunction(String token) {
    return FUNCTION_PATTERN.matcher(token).matches();
  }

  private static boolean isOperator(String token) {
    return OPERATOR_PRECEDENCE.containsKey(token) || token.equals("!");
  }

  private static int getPrecedence(String operator) {
    return OPERATOR_PRECEDENCE.getOrDefault(operator, 0);
  }

  private static double applyFunction(String function, double operand) {
    switch (function) {
      case "sin":
        return Math.sin(operand);
      case "cos":
        return Math.cos(operand);
      case "exp":
        return Math.exp(operand);
      case "log":
        if (operand <= 0) {
          throw new CalculatorException("Логарифм определен только для положительных чисел");
        }
        return Math.log(operand);
      case "abs":
        return Math.abs(operand);
      case "sqrt":
        if (operand < 0) {
          throw new CalculatorException("Невозможно извлечь корень из отрицательного числа");
        }
        return Math.sqrt(operand);
      default:
        throw new CalculatorException("Неизвестная функция: " + function);
    }
  }

  private static double applyOperator(String operator, double left, double right) {
    switch (operator) {
      case "+":
        return left + right;
      case "-":
        return left - right;
      case "*":
        return left * right;
      case "/":
        if (right == 0) {
          throw new CalculatorException("Деление на ноль");
        }
        return left / right;
      case "%":
        if (right == 0) {
          throw new CalculatorException("Деление на ноль");
        }
        return left % right;
      case "^":
        return Math.pow(left, right);
      case "!":
        if (left < 0 || left != Math.floor(left)) {
          throw new CalculatorException("Факториал определен только для неотрицательных целых чисел");
        }
        return factorial((int) left);
      default:
        throw new CalculatorException("Неизвестный оператор: " + operator);
    }
  }

  private static double factorial(int n) {
    if (n < 0) {
      throw new CalculatorException("Факториал определен только для неотрицательных чисел");
    }
    double result = 1;
    for (int i = 2; i <= n; i++) {
      result *= i;
    }
    return result;
  }
}

class CalculatorException extends RuntimeException {
  public CalculatorException(String message) {
    super(message);
  }
}