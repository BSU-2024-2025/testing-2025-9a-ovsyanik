import java.util.*;

public class Calculator {

  public double calculate(String expression) {
    String[] tokens = expression.split(" ");
    List<String> postfix = infixToPostfix(tokens);
    return evaluatePostfix(postfix);
  }

  private List<String> infixToPostfix(String[] tokens) {
    List<String> output = new ArrayList<>();
    Stack<String> stack = new Stack<>();

    Map<String, Integer> precedence = new HashMap<>();
    precedence.put("+", 1);
    precedence.put("-", 1);
    precedence.put("*", 2);
    precedence.put("/", 2);

    for (String token : tokens) {
      if (token.matches("-?\\d+(\\.\\d+)?")) {
        output.add(token);
      } else if (precedence.containsKey(token)) {
        while (!stack.isEmpty() && precedence.containsKey(stack.peek()) &&
                precedence.get(stack.peek()) >= precedence.get(token)) {
          output.add(stack.pop());
        }
        stack.push(token);
      } else if (token.equals("(")) {
        stack.push(token);
      } else if (token.equals(")")) {
        while (!stack.isEmpty() && !stack.peek().equals("(")) {
          output.add(stack.pop());
        }
        stack.pop();
      }
    }

    while (!stack.isEmpty()) {
      output.add(stack.pop());
    }

    return output;
  }

  private double evaluatePostfix(List<String> postfix) {
    Stack<Double> stack = new Stack<>();

    for (String token : postfix) {
      if (token.matches("-?\\d+(\\.\\d+)?")) {
        stack.push(Double.parseDouble(token));
      } else {
        double b = stack.pop();
        double a = stack.pop();

        switch (token) {
          case "+": stack.push(a + b); break;
          case "-": stack.push(a - b); break;
          case "*": stack.push(a * b); break;
          case "/":
            if (b == 0) throw new ArithmeticException();
            stack.push(a / b);
            break;
        }
      }
    }

    return stack.pop();
  }
}