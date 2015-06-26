package test;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Format
{
  char LEFT_BRACE = '{';
  char RIGHT_BRACE = '}';
  char LEFT_BRACKET = '[';
  char RIGHT_BRACKET = ']';
  char QUOTE = '"';
  char COMMA = ',';
  String TAB = "    ";
  String LINEBREAK = "\r\n";
  String result = "";
  JTextArea jt1;
  JTextArea jt2;
  JButton jb1;
  JButton jb2;
  static Format f;
  
  public static void main(String args[])
  {
    f = new Format();
    f.initialize();
  }
  
  private void initialize()
  {
    JFrame jf = new JFrame();
    jf.setSize(800, 600);
    jf.setTitle("JSON Formatter");
    jf.setLayout(null);
    

    this.jt1 = new JTextArea();
    this.jt1.setSize(330, 500);
    this.jt1.setLineWrap(true);
    this.jt1.setWrapStyleWord(true);
    JScrollPane jsp1 = new JScrollPane(this.jt1);
    jsp1.setBounds(35, 30, 290, 500);
    jf.add(jsp1);
    

    this.jt2 = new JTextArea();
    this.jt2.setSize(330, 500);
    this.jt2.setLineWrap(true);
    this.jt2.setWrapStyleWord(true);
    this.jt2.setEditable(false);
    JScrollPane jsp2 = new JScrollPane(this.jt2);
    jsp2.setBounds(455, 30, 290, 500);
    jf.add(jsp2);
    

    this.jb1 = new JButton();
    this.jb1.setText("Format >>");
    this.jb1.setBounds(340, 240, 100, 30);
    this.jb1.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        Format.this.result = "";
        Format.f.formatJson();
      }
    });
    jf.add(this.jb1);
    

    this.jb2 = new JButton();
    this.jb2.setText("Clear");
    this.jb2.setBounds(340, 290, 100, 30);
    this.jb2.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        Format.this.result = "";
        Format.f.clearValue();
      }
    });
    jf.add(this.jb2);
    
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = jf.getSize();
    jf.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    
    jf.setVisible(true);
    jf.setDefaultCloseOperation(2);
  }
  
  private void clearValue()
  {
    this.result = "";
    this.jt1.setText("");
    this.jt2.setText("");
  }
  
  private Integer getNextSpecialChar(String json, Integer startPoint)
  {
    for (int i = startPoint.intValue(); i < json.length(); i++) {
      if ((json.charAt(i) == this.LEFT_BRACE) || (json.charAt(i) == this.RIGHT_BRACE) || 
        (json.charAt(i) == this.LEFT_BRACKET) || (json.charAt(i) == this.RIGHT_BRACKET) || 
        (json.charAt(i) == this.COMMA)) {
        return Integer.valueOf(i);
      }
    }
    return Integer.valueOf(-1);
  }
  
  private void addTab(Integer level)
  {
    int i = 0;
    while (i < level.intValue())
    {
      this.result += this.TAB;
      i++;
    }
  }
  
  private Boolean switchIsString(Boolean isString)
  {
    if (isString.booleanValue()) {
      return Boolean.valueOf(false);
    }
    return Boolean.valueOf(true);
  }
  
  private String processJson(String json)
  {
    String newJson = json.replaceAll("\n", "");
    if (newJson.isEmpty()) {
      return newJson;
    }
    Boolean isString = Boolean.valueOf(false);
    for (int i = 0; i < newJson.length(); i++)
    {
      if ((newJson.charAt(i) == this.QUOTE) && (
        (i == 0) || (!isString.booleanValue()) || (newJson.charAt(i - 1) != '\\'))) {
        isString = switchIsString(isString);
      }
      if ((newJson.charAt(i) == ' ') && 
        (!isString.booleanValue()))
      {
        newJson = newJson.substring(0, i) + newJson.substring(i + 1, newJson.length());
        i--;
      }
    }
    return newJson;
  }
  
  private void formatJson()
  {
    int level = 0;
    

    String json = this.jt1.getText();
    json = processJson(json);
    

    int special_char = 0;
    int next_special_char = 0;
    for (;;)
    {
      next_special_char = getNextSpecialChar(json, Integer.valueOf(special_char)).intValue();
      if (next_special_char == -1)
      {
        addTab(Integer.valueOf(level));
        this.result += json.substring(special_char, json.length());
        break;
      }
      if ((json.charAt(next_special_char) == this.LEFT_BRACE) || (json.charAt(next_special_char) == this.LEFT_BRACKET))
      {
        addTab(Integer.valueOf(level));
        this.result += json.substring(special_char, next_special_char + 1);
        level++;
      }
      if ((json.charAt(next_special_char) == this.RIGHT_BRACE) || (json.charAt(next_special_char) == this.RIGHT_BRACKET))
      {
        if (special_char != next_special_char)
        {
          addTab(Integer.valueOf(level));
          this.result += json.substring(special_char, next_special_char);
          this.result += this.LINEBREAK;
        }
        if (level > 0) {
          level--;
        }
        addTab(Integer.valueOf(level));
        this.result += json.substring(next_special_char, next_special_char + 1);
      }
      if (json.charAt(next_special_char) == this.COMMA)
      {
        if (special_char == next_special_char) {
          this.result = this.result.substring(0, this.result.length() - 2);
        } else {
          addTab(Integer.valueOf(level));
        }
        this.result += json.substring(special_char, next_special_char + 1);
      }
      this.result += this.LINEBREAK;
      special_char = next_special_char + 1;
    }
    this.jt2.setText(this.result);
  }
}
