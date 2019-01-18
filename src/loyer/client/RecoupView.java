package loyer.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jfree.ui.RefineryUtilities;

import loyer.db.RecoupTools;
import loyer.db.RecoupTools.RecoupData;
import loyer.properties.Tables;

public class RecoupView {

  private JFrame frame;
  private String productType;
  private JTextField[] pullField = new JTextField[4];
  private JTextField[] strokeField = new JTextField[4];
  private JTextField resField;
  private JButton setButt;
  private JButton updateButt;
  private static final String SEPARATOR = System.getProperty("line.separator");
  /**补偿值是否修改标志位*/
  private static boolean isModify = false;
  
  
  /**
   * 获取补偿值参数页面
   * @param tableName
   */
  public static void getRecoupView(String tableName) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          RecoupView window = new RecoupView(tableName);
          window.frame.setVisible(true);
          window.initLoad();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  /**
   * 判别补偿值是否已被更新
   * @return
   */
  public static boolean isModify() {
    return isModify;
  }
  /**
   * 设置补偿值是否被更新
   * @param bool
   */
  public static void setModify(boolean bool) {
    isModify = bool;
  }
  /**
   * Create the application.
   */
  public RecoupView(String type) {
    this.productType = type;
    initialize();
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    
    try {
      //将界面风格设置成和系统一置
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
      JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
    }//*/
    
    frame = new JFrame("补偿值修改");
    frame.setResizable(false); //窗口大小不可更改
    frame.setIconImage(Toolkit.getDefaultToolkit().getImage(JFrame.class.getResource("/pic/Kyokuto.png")));
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    
    JTextArea area = new JTextArea(4, 50);
    area.setBackground(Color.LIGHT_GRAY);
    area.setEditable(false);
    area.append("*修改参数时，请注意以下几点：" + SEPARATOR);
    area.append("1.先修改文本框内数值" + SEPARATOR);
    area.append("2.然后单击"+"参数设置"+"按键" + SEPARATOR);
    area.append("3.最后单击"+"更新数据"+"按键即完成参数修改");
    JScrollPane pane = new JScrollPane(area);
    pane.setBorder(new TitledBorder(new EtchedBorder(), "Tips", TitledBorder.LEFT, TitledBorder.TOP,
          new Font("等线", Font.ITALIC, 13)));
    
    frame.getContentPane().add(pane, BorderLayout.SOUTH);
    
    for(int i = 0; i < 4; i++) {
      pullField[i] = new JTextField(10);
      strokeField[i] = new JTextField(10);
    }
    resField = new JTextField(10);
    
    JPanel txtPanel = new JPanel(new GridLayout(3, 3, 5, 5));
    txtPanel.setBackground(Color.LIGHT_GRAY);
    txtPanel.setBorder(new TitledBorder(new EtchedBorder(), "补偿值列表", TitledBorder.CENTER, TitledBorder.BOTTOM,
          new Font("等线", Font.ITALIC, 13)));
    for(int i = 0; i < 4; i++) {
      txtPanel.add(new MyPanel("拉力" +(i+1)+ "补偿", pullField[i]));
    }
    txtPanel.add(new MyPanel("电阻补偿", resField));
    for(int i = 0; i < 4; i++) {
      txtPanel.add(new MyPanel("行程" +(i+1)+ "补偿", strokeField[i]));
    }
    
    setButt = new JButton("参数设置");
    setButt.addActionListener(e -> {
      String[] datas = new String[9];
      for(int i = 0; i < 3; i++) {
        datas[i] = pullField[i].getText();
        datas[i + 5] = strokeField[i].getText();
      }
      if(productType.equals(Tables.F517)) {
        datas[3] = pullField[3].getText();
        datas[8] = strokeField[3].getText();
      } else {
        datas[3] = "0";
        datas[8] = "0";
      }
      datas[4] = resField.getText();
      if(RecoupTools.update(productType, datas) != -1); {
        isModify = true;
      }
    });
    updateButt = new JButton("更新数据");
    updateButt.addActionListener(e -> {
      if(isModify) {
        JOptionPane.showMessageDialog(null, "数据已成功更新!");
      } else {
        JOptionPane.showMessageDialog(null, "请您先设置参数!");
      }
    });
    JPanel buttPanel = new JPanel(new GridLayout(1, 2, 20, 5));
    buttPanel.setBorder(new EtchedBorder());
    buttPanel.add(setButt);
    buttPanel.add(updateButt);
    
    JPanel panel = new JPanel(new BorderLayout(5, 5));
    panel.add(txtPanel, BorderLayout.CENTER);
    panel.add(buttPanel, BorderLayout.SOUTH);
    frame.getContentPane().add(panel, BorderLayout.CENTER);
    
    frame.pack();
    RefineryUtilities.centerFrameOnScreen(frame); 
    
    // 窗口"X"关闭事件
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        close();
      }
    });
  }
  /**
   * 载入数据库补偿值
   */
  private void initLoad() {
    RecoupData reData = RecoupTools.getByName(productType);
    pullField[0].setText(reData.getPull_1());
    pullField[1].setText(reData.getPull_2());
    pullField[2].setText(reData.getPull_3());
    pullField[3].setText(reData.getPull_4());
    strokeField[0].setText(reData.getStroke_1());
    strokeField[1].setText(reData.getStroke_2());
    strokeField[2].setText(reData.getStroke_3());
    strokeField[3].setText(reData.getStroke_4());
    resField.setText(reData.getResistance());
  }
  private void close() {
    frame.dispose();
  }
  /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
  public static class MyPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public MyPanel(String title, JTextField field) {
      TitledBorder tb = new TitledBorder(new EtchedBorder(), title, TitledBorder.LEFT, TitledBorder.TOP,
          new Font("等线", Font.ITALIC, 13), Color.BLUE);
      setBorder(tb);
      setLayout(new BorderLayout());
      field.setHorizontalAlignment(SwingConstants.CENTER);
      field.setFont(new Font("宋体", Font.BOLD, 15));
      field.setBackground(new Color(245, 245, 245));
      add(field, BorderLayout.CENTER);
    }
  }
}
