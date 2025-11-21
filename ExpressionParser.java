import java.util.*;

public class ExpressionParser {

  public double parse(String expression) {
    expression = expression.replaceAll("\\s+", "");

    expression = processFunctions(expression);

    expression = expression.replaceAll("(?<![0-9)])-", "0-");

    expression = expression.replace("pi", String.valueOf(Math.PI));
    expression = expression.replace("e", String.valueOf(Math.E));

    while (expression.contains("(")) {
      int start = expression.lastIndexOf("(");
      int end = expression.indexOf(")", start);
      String inner = expression.substring(start + 1, end);
      double result = parse(inner);
      expression = expression.substring(0, start) + result + expression.substring(end + 1);
    }

    return parseOperations(expression);
  }


  private String processFunctions(String expr) {
    while (expr.contains("sqrt(")) {
      int start = expr.indexOf("sqrt(");
      int end = findMatchingParenthesis(expr, start + 4);
      String inner = expr.substring(start + 5, end);
      double result = Math.sqrt(parse(inner));
      expr = expr.substring(0, start) + result + expr.substring(end + 1);
    }

    while (expr.contains("sin(")) {
      int start = expr.indexOf("sin(");
      int end = findMatchingParenthesis(expr, start + 3);
      String inner = expr.substring(start + 4, end);
      double result = Math.sin(Math.toRadians(parse(inner)));
      expr = expr.substring(0, start) + result + expr.substring(end + 1);
    }

    while (expr.contains("cos(")) {
      int start = expr.indexOf("cos(");
      int end = findMatchingParenthesis(expr, start + 3);
      String inner = expr.substring(start + 4, end);
      double result = Math.cos(Math.toRadians(parse(inner)));
      expr = expr.substring(0, start) + result + expr.substring(end + 1);
    }

    while (expr.contains("tan(")) {
      int start = expr.indexOf("tan(");
      int end = findMatchingParenthesis(expr, start + 3);
      String inner = expr.substring(start + 4, end);
      double result = Math.tan(Math.toRadians(parse(inner)));
      expr = expr.substring(0, start) + result + expr.substring(end + 1);
    }

    while (expr.contains("log(")) {
      int start = expr.indexOf("log(");
      int end = findMatchingParenthesis(expr, start + 3);
      String inner = expr.substring(start + 4, end);
      double result = Math.log(parse(inner));
      expr = expr.substring(0, start) + result + expr.substring(end + 1);
    }

    return expr;
  }

  private int findMatchingParenthesis(String expr, int start) {
    int count = 1;
    for (int i = start + 1; i < expr.length(); i++) {
      if (expr.charAt(i) == '(') count++;
      else if (expr.charAt(i) == ')') count--;

      if (count == 0) return i;
    }
    throw new RuntimeException("Непарная скобка в выражении: " + expr);
  }

  private double parseOperations(String expr) {
    String[] parts = splitByOperators(expr, "+-");
    if (parts.length > 1) {
      double result = parseOperations(parts[0]);
      for (int i = 1; i < parts.length; i += 2) {
        String op = parts[i];
        double next = parseOperations(parts[i + 1]);
        if (op.equals("+")) result += next;
        else if (op.equals("-")) result -= next;
      }
      return result;
    }

    parts = splitByOperators(expr, "*/");
    if (parts.length > 1) {
      double result = parseOperations(parts[0]);
      for (int i = 1; i < parts.length; i += 2) {
        String op = parts[i];
        double next = parseOperations(parts[i + 1]);
        if (op.equals("*")) result *= next;
        else if (op.equals("/")) {
          if (next == 0) throw new ArithmeticException("Деление на ноль");
          result /= next;
        }
      }
      return result;
    }

    if (expr.contains("^")) {
      String[] powParts = splitByOperators(expr, "^");
      return Math.pow(parseOperations(powParts[0]), parseOperations(powParts[1]));
    }

    try {
      return Double.parseDouble(expr);
    } catch (NumberFormatException e) {
      throw new RuntimeException("Неверное выражение: " + expr);
    }
  }

  private String[] splitByOperators(String expr, String operators) {
    List<String> parts = new ArrayList<>();
    int start = 0;
    int i = 0;

    while (i < expr.length()) {
      if (operators.indexOf(expr.charAt(i)) != -1) {
        if (i == 0 || "+-*/^(".indexOf(expr.charAt(i-1)) != -1) {
          i++;
          continue;
        }

        parts.add(expr.substring(start, i));
        parts.add(expr.substring(i, i + 1));
        start = i + 1;
      }
      i++;
    }
    parts.add(expr.substring(start));

    return parts.toArray(new String[0]);
  }
}