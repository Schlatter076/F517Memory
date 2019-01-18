package loyer.client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartPanel;

import loyer.db.RecordTools;

public class ReportView {

  private JFrame frame;
  private JScrollPane botPane;
  private final int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
  private final int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height; 
  private MyLineChart myLine;
  private String tableName;
  
  /**
   * 获取测试报告
   * @param tableName
   */
  public static void getReportView(String tableName) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          ReportView window = new ReportView(tableName);
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }
 
  public ReportView(String tableName) {
    this.tableName = tableName;
    initialize();
  }
  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    
    try {
      // 将界面风格设置成和系统一置
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | UnsupportedLookAndFeelException e) {
      JOptionPane.showMessageDialog(null, e.getLocalizedMessage());
    } // */
    
    frame = new JFrame("查看不良报告");
    frame.setBounds(WIDTH / 4, HEIGHT / 6, WIDTH / 2, HEIGHT * 2 / 3);
    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    frame.setIconImage(Toolkit.getDefaultToolkit().getImage(JFrame.class.getResource("/pic/Kyokuto.png")));
    //窗口添加关闭事件
    frame.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        close();
      }
    });
    
    myLine = new MyLineChart(RecordTools.getAllByDB(tableName));
    ChartPanel chartPanel = myLine.getChartPanel();
    
    botPane = new JScrollPane(chartPanel);
    botPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    botPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    botPane.setBorder(new TitledBorder(new EtchedBorder(), "不良分析", TitledBorder.LEFT, 
        TitledBorder.TOP, new Font("宋体", Font.PLAIN, 15), Color.BLUE));
    
    frame.getContentPane().add(botPane, BorderLayout.CENTER);
  }
  /**
   * 退出事件
   */
  private void close() {
    frame.dispose();
  }

}
