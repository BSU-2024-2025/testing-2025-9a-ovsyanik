import java.util.*;

public class Interpreter {
  private Map<String, Double> variables = new HashMap<>();
  private StringBuilder output = new StringBuilder();
  private List<String> lines;
  private int currentLine;

  public String execute(String code) {
    variables.clear();
    output.setLength(0);

    lines = Arrays.asList(code.split("\n"));
    currentLine = 0;

    while (currentLine < lines.size()) {
      String line = lines.get(currentLine).trim();
      currentLine++;

      if (line.isEmpty() || line.startsWith("//")) continue;

      try {
        processLine(line);
      } catch (Exception e) {
        output.append("Ошибка в строке '").append(line).append("': ").append(e.getMessage()).append("\n");
      }
    }

    return output.toString();
  }

  private void processLine(String line) {
    if (line.startsWith("if")) {
      processIfBlock(line);
      return;
    }

    if (line.contains("=") && !line.contains("==") && !line.contains(">=") && !line.contains("<=")) {
      String[] parts = line.split("=");
      if (parts.length == 2) {
        String varName = parts[0].trim();
        double value = evaluateExpression(parts[1].trim());
        variables.put(varName, value);
        output.append(varName).append(" = ").append(value).append("\n");
        return;
      }
    }

    double result = evaluateExpression(line);
    output.append("→ ").append(result).append("\n");
  }

  private void processIfBlock(String firstLine) {
    String conditionStr = firstLine.substring(firstLine.indexOf("(") + 1, firstLine.indexOf(")"));
    boolean conditionResult = evaluateCondition(conditionStr);

    List<String> ifBlock = new ArrayList<>();
    List<String> elseBlock = new ArrayList<>();
    boolean inElse = false;
    int braceCount = 1;

    while (currentLine < lines.size() && braceCount > 0) {
      String line = lines.get(currentLine).trim();
      currentLine++;

      if (line.equals("}")) {
        braceCount--;
      } else if (line.equals("} else {")) {
        inElse = true;
      } else if (line.equals("{")) {
        braceCount++;
      } else if (!line.isEmpty() && !line.startsWith("//")) {
        if (inElse) {
          elseBlock.add(line);
        } else {
          ifBlock.add(line);
        }
      }
    }

    if (conditionResult) {
      executeBlock(ifBlock);
    } else {
      executeBlock(elseBlock);
    }
  }

  private void executeBlock(List<String> block) {
    for (String line : block) {
      processLine(line);
    }
  }

  private double evaluateExpression(String expr) {
    for (String var : variables.keySet()) {
      expr = expr.replace(var, variables.get(var).toString());
    }

    return new ExpressionParser().parse(expr);
  }

  private boolean evaluateCondition(String condition) {
    condition = condition.trim();

    for (String var : variables.keySet()) {
      condition = condition.replace(var, variables.get(var).toString());
    }

    if (condition.contains(">=")) {
      String[] parts = condition.split(">=");
      return evaluateExpression(parts[0]) >= evaluateExpression(parts[1]);
    } else if (condition.contains("<=")) {
      String[] parts = condition.split("<=");
      return evaluateExpression(parts[0]) <= evaluateExpression(parts[1]);
    } else if (condition.contains(">")) {
      String[] parts = condition.split(">");
      return evaluateExpression(parts[0]) > evaluateExpression(parts[1]);
    } else if (condition.contains("<")) {
      String[] parts = condition.split("<");
      return evaluateExpression(parts[0]) < evaluateExpression(parts[1]);
    } else if (condition.contains("==")) {
      String[] parts = condition.split("==");
      return Math.abs(evaluateExpression(parts[0]) - evaluateExpression(parts[1])) < 0.0001;
    } else if (condition.contains("!=")) {
      String[] parts = condition.split("!=");
      return Math.abs(evaluateExpression(parts[0]) - evaluateExpression(parts[1])) >= 0.0001;
    }

    return evaluateExpression(condition) != 0;
  }
}