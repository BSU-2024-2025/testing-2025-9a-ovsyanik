import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Calculator extends JFrame implements ActionListener {
  private JTextArea codeArea;
  private JTextArea outputArea;
  private JButton runBtn;

  public Calculator() {
    setTitle("Simple Calculator IDE");
    setSize(600, 500);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    codeArea = new JTextArea();
    codeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
    codeArea.setText("// Простой калькулятор\nx = 10\ny = 5\nx + y\n\nif (x > y) {\n    result = x - y\n} else {\n    result = y - x\n}\nresult");

    outputArea = new JTextArea();
    outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
    outputArea.setEditable(false);

    runBtn = new JButton("Выполнить");
    runBtn.addActionListener(this);

    setLayout(new BorderLayout());
    add(new JScrollPane(codeArea), BorderLayout.CENTER);

    JPanel southPanel = new JPanel(new BorderLayout());
    southPanel.add(runBtn, BorderLayout.NORTH);
    southPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);
    add(southPanel, BorderLayout.SOUTH);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == runBtn) {
      executeCode();
    }
  }

  private void executeCode() {
    String code = codeArea.getText();
    Interpreter interpreter = new Interpreter();
    String result = interpreter.execute(code);
    outputArea.setText(result);
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
      new Calculator().setVisible(true);
    });
  }
}