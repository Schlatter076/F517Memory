package loyer.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import loyer.db.PositionTools;
import loyer.db.PositionTools.PositionData;

public class UsartTools {

  private JFrame frame;
  private JTabbedPane tabbedPane;
  private JScrollPane leftPane;
  private JPanel rightPane;
  private JScrollPane rightScrollPane;
  private final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
  private final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;  
  private JMenuBar menuBar;
  private JMenu helpMenu;
  private JMenuItem tipItem;
  private JMenuItem aboutItem;
  private JProgressBar progressBar;
  
  private JTextField[] XpositionField = new JTextField[4];
  private JTextField[] ZpositionField = new JTextField[4];
  private JTextField[] XtempField = new JTextField[4];
  private JTextField[] ZtempField = new JTextField[4];
  private JButton[] XposButt = new JButton[4];
  private JButton[] XtempButt = new JButton[4];
  private JButton[] ZposButt = new JButton[4];
  private JButton[] ZtempButt = new JButton[4];
  private JToggleButton[] forward = new JToggleButton[16];
  private JToggleButton[] backward = new JToggleButton[16];
  private MyPanel[] XpositionPanel = new MyPanel[4];
  private MyPanel[] XtempPanel = new MyPanel[4];
  private MyPanel[] ZpositionPanel = new MyPanel[4];
  private MyPanel[] ZtempPanel = new MyPanel[4];
  private JPanel[] panel = new JPanel[4];
  private Timer timer1;
  private int progressValue = 0;
  
  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          UsartTools window = new UsartTools();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  /**
   * 获取调试助手
   */
  public static void getUsartTools() {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          UsartTools window = new UsartTools();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
  /**
   * Create the application.
   */
  public UsartTools() {
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
    
    frame = new JFrame("调试助手");
    //frame.setResizable(false);  //窗口大小不可更改
    frame.setIconImage(Toolkit.getDefaultToolkit().getImage(JFrame.class.getResource("/pic/Kyokuto.png")));
    frame.setBounds(WIDTH / 4, HEIGHT / 6, WIDTH / 2, HEIGHT * 2 / 3);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    // 窗口"X"关闭事件
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        close();
      }
    });
    
    progressBar = new JProgressBar(JProgressBar.HORIZONTAL); //水平进度条
    //progressBar.setIndeterminate(true);  //不确定进度条
    //progressBar.setBorderPainted(false);  //除去边框
    //progressBar.setForeground(new Color(255, 0, 0));
    timer1 = new Timer(20, e -> {
      if(progressBar.getValue() >= 100) {
        progressValue = 0;
      }
      progressBar.setValue(progressValue);
      progressValue++;
    });
    timer1.start();    
    
    menuBar = new JMenuBar();
    helpMenu = new JMenu("帮助(H)");
    tipItem = new JMenuItem("提示与技巧(T)...");
    aboutItem = new JMenuItem("关于(A)");
    helpMenu.add(tipItem);
    helpMenu.addSeparator();
    helpMenu.add(aboutItem);
    menuBar.add(helpMenu);
    frame.setJMenuBar(menuBar);
    tipItem.addActionListener(e -> tips());
    aboutItem.addActionListener(e -> about());
    
    JPanel fieldPanel = new JPanel(new GridLayout(2, 2));
    for(int i = 0; i < 4; i++) {
      
      PositionData data = PositionTools.getByName("F517记忆开关", i + 1);
      XpositionField[i] = new JTextField(10);
      XpositionField[i].setText(data.getXposition() + "");
      XpositionField[i].setHorizontalAlignment(SwingConstants.CENTER);
      XpositionField[i].setBackground(Color.ORANGE);
      XpositionField[i].setForeground(Color.RED);
      XpositionField[i].setFont(new Font("宋体", Font.PLAIN, 18));
      XtempField[i] = new JTextField(10);
      XtempField[i].setText(data.getXtemp() + "");
      XtempField[i].setHorizontalAlignment(SwingConstants.CENTER);
      XtempField[i].setBackground(Color.ORANGE);
      XtempField[i].setForeground(Color.RED);
      XtempField[i].setFont(new Font("宋体", Font.PLAIN, 18));
      ZpositionField[i] = new JTextField(10);
      ZpositionField[i].setText(data.getZposition() + "");
      ZpositionField[i].setHorizontalAlignment(SwingConstants.CENTER);
      ZpositionField[i].setBackground(Color.ORANGE);
      ZpositionField[i].setForeground(Color.RED);
      ZpositionField[i].setFont(new Font("宋体", Font.PLAIN, 18));
      ZtempField[i] = new JTextField(10);
      ZtempField[i].setText(data.getZtemp() + "");
      ZtempField[i].setHorizontalAlignment(SwingConstants.CENTER);
      ZtempField[i].setBackground(Color.ORANGE);
      ZtempField[i].setForeground(Color.RED);
      ZtempField[i].setFont(new Font("宋体", Font.PLAIN, 18));
      XposButt[i] = new JButton("修改数据");
      XtempButt[i] = new JButton("修改数据");
      ZposButt[i] = new JButton("修改数据");
      ZtempButt[i] = new JButton("修改数据");
      XpositionPanel[i] = new MyPanel("X设定位置", XpositionField[i], XposButt[i]);
      XtempPanel[i] = new MyPanel("X当前位置", XtempField[i], XtempButt[i]);
      ZpositionPanel[i] = new MyPanel("Z设定位置", ZpositionField[i], ZposButt[i]);
      ZtempPanel[i] = new MyPanel("Z当前位置", ZtempField[i], ZtempButt[i]);
      panel[i] = new JPanel(new GridLayout(4, 1));
      panel[i].setBorder(new TitledBorder(new LineBorder(Color.GRAY, 2), (i + 1) + "号按钮", TitledBorder.LEFT, 
          TitledBorder.TOP, new Font("等线", Font.PLAIN, 13), Color.BLACK));
      panel[i].add(XpositionPanel[i]);
      panel[i].add(XtempPanel[i]);
      panel[i].add(ZpositionPanel[i]);
      panel[i].add(ZtempPanel[i]);
      fieldPanel.add(panel[i]);
    }
    tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
    leftPane = new JScrollPane();
    leftPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    leftPane.setBorder(new TitledBorder(new EtchedBorder(), "串口调试助手", TitledBorder.CENTER, 
        TitledBorder.TOP, new Font("等线", Font.PLAIN, 13), Color.BLACK));
    rightPane = new JPanel(new BorderLayout(5, 10));
    rightScrollPane = new JScrollPane();
    rightScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
    rightScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    rightScrollPane.setBorder(new TitledBorder(new LineBorder(Color.GRAY, 2), "参数列表", TitledBorder.LEFT, 
        TitledBorder.TOP, new Font("等线", Font.PLAIN, 13), Color.BLACK));
    rightPane.setBorder(new TitledBorder(new EtchedBorder(), "电机参数设置", TitledBorder.CENTER, 
        TitledBorder.TOP, new Font("等线", Font.PLAIN, 13), Color.BLACK));
    tabbedPane.addTab("串口工具", leftPane);
    tabbedPane.addTab("位置参数", rightPane);
    
    /*
    JPanel buttPanel = new JPanel(new GridLayout(1, 4));
    JButton butt1 = new JButton("1");
    JButton butt2 = new JButton("2");
    JButton butt3 = new JButton("3");
    JButton butt4 = new JButton("4");
    buttPanel.add(butt1);
    buttPanel.add(butt2);
    buttPanel.add(butt3);
    buttPanel.add(butt4);
    JPanel p = new JPanel(new GridLayout(2, 1));
    p.add(fieldPanel);
    p.add(new JLabel(new ImageIcon(JLabel.class.getResource("/pic/frame.jpg"))));
    //*/
    rightPane.add(rightScrollPane, BorderLayout.CENTER);
    rightPane.add(fieldPanel, BorderLayout.WEST);
    rightPane.add(progressBar, BorderLayout.SOUTH);
    rightScrollPane.setViewportView(completedTable(getTestTable()));
    
    //frame.getContentPane().add(progressBar, BorderLayout.NORTH);
    frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
  }
  
  /**
   * 创建JTable方法
   * 
   * @return
   */
  public JTable getTestTable() {
    Vector<Object> rowNum = null, colNum = null;
    // 创建列对象
    colNum = new Vector<>();
    colNum.add("");
    colNum.add("按钮");
    colNum.add("机种名");
    colNum.add("X设定位置");
    colNum.add("X当前位置");
    colNum.add("Z设定位置");
    colNum.add("Z当前位置");
    colNum.add("日期");
    colNum.add("说明");

    // 创建行对象
    rowNum = new Vector<>();
    List<PositionData> tableList = PositionTools.getByName("F517记忆开关"); 
    for (PositionData rd : tableList) {
      Vector<String> vt = new Vector<>();
      vt.add("");
      vt.add(rd.getNumber()+ "");
      vt.add(rd.getName());
      vt.add(rd.getXposition() + "");
      vt.add(rd.getXtemp() + "");
      vt.add(rd.getZposition() + "");
      vt.add(rd.getZtemp() + "");
      vt.add(rd.getDate());
      vt.add(rd.getTips());

      rowNum.add(vt);
    }

    DefaultTableModel model = new DefaultTableModel(rowNum, colNum) {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean isCellEditable(int row, int column) {
        return false;
      }
    };
    model.addRow(new Object[] { "*", "", "", "", "", "", "", "", "", "", "", "", "" });
    JTable table = new JTable(model);
    return table;
  }

  /**
   * 提供设置JTable方法
   * 
   * @param table
   * @return
   */
  public JTable completedTable(JTable table) {

    DefaultTableCellRenderer r = new DefaultTableCellRenderer(); // 设置
    r.setHorizontalAlignment(JLabel.CENTER); // 单元格内容居中
    // table.setOpaque(false); //设置表透明
    JTableHeader jTableHeader = table.getTableHeader(); // 获取表头
    // 设置表头名称字体样式
    jTableHeader.setFont(new Font("宋体", Font.PLAIN, 14));
    // 设置表头名称字体颜色
    jTableHeader.setForeground(Color.BLACK);
    jTableHeader.setDefaultRenderer(r);

    // 表头不可拖动
    jTableHeader.setReorderingAllowed(false);
    // 列大小不可改变
    jTableHeader.setResizingAllowed(false);
    // 设置列宽
    TableColumn col_0 = table.getColumnModel().getColumn(0);
    TableColumn col_2 = table.getColumnModel().getColumn(2);
    TableColumn col_3 = table.getColumnModel().getColumn(3);
    TableColumn col_4 = table.getColumnModel().getColumn(4);
    TableColumn col_5 = table.getColumnModel().getColumn(5);
    TableColumn col_7 = table.getColumnModel().getColumn(7);
    TableColumn col_8 = table.getColumnModel().getColumn(8);
    col_0.setPreferredWidth(20);
    col_2.setPreferredWidth(150);
    col_3.setPreferredWidth(120);
    col_4.setPreferredWidth(120);
    col_5.setPreferredWidth(120);
    col_7.setPreferredWidth(120);
    col_8.setPreferredWidth(200);

    // table.setEnabled(false); // 内容不可编辑
    table.setDefaultRenderer(Object.class, r); // 居中显示
    table.setRowHeight(30); // 设置行高
    // 增加一行空白行
    table.setGridColor(new Color(245, 245, 245)); // 设置网格颜色
    table.setForeground(Color.BLACK); // 设置文字颜色
    table.setBackground(new Color(245, 245, 245));
    table.setFont(new Font("宋体", Font.PLAIN, 13));
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);// 关闭表格列自动调整

    return table;
  }

  /**
   * 窗口退出时调用
   */
  private void close() {
    timer1.stop();
    frame.dispose();
  }
  /**
   * 提示与技巧菜单事件
   */
  private void tips() {
    int tem = JOptionPane.showConfirmDialog(null, "你确定要查看技巧？", "询问", JOptionPane.YES_NO_OPTION);
    if(tem == JOptionPane.YES_OPTION) {
      JOptionPane.showMessageDialog(null, "并没有什么技巧！哈哈^_^");
    }
  }
  /**
   * 关于菜单事件
   */
  private void about() {
    JOptionPane.showMessageDialog(null, "软件版本：V1.0-2018\r\n技术支持：Loyer");
  }
  ///////////////////////////////////////////////////////////////////////////////
  /**
   * 测试数据及测试时间显示面板
   * 
   * @author hw076
   *
   */
  class MyPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    public MyPanel(String title, JTextField field) {
      TitledBorder tb = new TitledBorder(new EtchedBorder(), title, TitledBorder.LEFT, TitledBorder.TOP,
          new Font("等线", Font.ITALIC, 11), Color.BLUE);
      setBorder(tb);
      setLayout(new BorderLayout());
      add(field, BorderLayout.CENTER);
    }
    public MyPanel(String title, JTextField field, JButton button) {
      TitledBorder tb = new TitledBorder(new EtchedBorder(), title, TitledBorder.LEFT, TitledBorder.TOP,
          new Font("等线", Font.ITALIC, 11), Color.BLUE);
      setBorder(tb);
      setLayout(new GridLayout(1, 2, 5, 5));
      add(field);
      add(button);
    }
  }
}
