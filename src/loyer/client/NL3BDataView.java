package loyer.client;

import java.awt.EventQueue;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import loyer.db.DataTools;
import loyer.db.RecordTools.RecordData;
import loyer.db.RecordTools;
import loyer.db.TestDataTools;
import loyer.db.UserTools;
import loyer.db.UserTools.UserData;
import loyer.gui.LoyerFrame;

public class NL3BDataView extends LoyerFrame {

  /** 测试数据表 */
  private JTable table;
  /** 测试数据表渲染类 */
  private MyTableCellRenderrer tableCell;
  /** 管理员用户 */
  private static UserData admin;
  /** 纳印用户 */
  private static UserData nayin;
  /**格式化时间值*/
  private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
  private int productCount = 0;

  static {
    // 加载用户数据
    admin = UserTools.getUserByID(1);
    nayin = UserTools.getUserByID(3);
  }

  public NL3BDataView() {
    initialize();
  }

  private void initialize() {

    PRODUCT_NAME = "NL-3B记忆开关";
    productField.setText(PRODUCT_NAME);
    table = DataTools.completedTable(DataTools.getTestTable("nl3b"));
    dataPanel.setViewportView(table);
    persistScroll.setViewportView(new JLabel(new ImageIcon(JLabel.class.getResource("/pic/frame.jpg"))));
    // setPieChart(10, 2);
  }

  @Override
  public boolean pwdIsPassed(String command) {
    return false;
  }

  @Override
  public void usartMethod() {
    UsartTools.getUsartTools();
  }

  @Override
  public void resultView() {
  }

  @Override
  public void reportView() {
    productCount++;
    scanField.setText(scanTest());
    for(int i = 1; i < 11; i++) {
      record(i, "测试");
    }
    recordNull();
  }

  @Override
  public void nayinMethod() {
  }

  @Override
  public void close() {
    RecordTools.outExcl("nl3b_recordtd");
    TestDataTools.outExcl("nl3b_testdata");
    System.exit(0);
  }
  /**
   * table渲染色，测试结果为"PASS"则设为绿色，"NG"为红色
   */
  public void setTableCellRenderer() {
    if (tableCell == null) {
      tableCell = new MyTableCellRenderrer();
      table.getColumnModel().getColumn(7).setCellRenderer(tableCell);
    } else
      table.getColumnModel().getColumn(7).setCellRenderer(tableCell);
  }
  /**
   * 产品编号字符串
   * @return
   */
  public String scanTest() {
    return LocalDate.now().toString() + "-" + productCount;
  }
  /**
   * 获取测试数据，插入到数据库
   * 
   * @param row
   *          行数
   * @param remark 备注
   */
  public void record(int row, String remark) {
    String[] datas = new String[11];
    datas[0] = scanField.getText(); // 获取产品编号
    for (int i = 1; i <= 7; i++) {
      datas[i] = table.getValueAt(row, i).toString();
    }
    datas[8] = sdf.format(new Date());
    datas[9] = LocalDate.now().toString();
    datas[10] = remark;
    TestDataTools.insert("nl3b_testdata", datas);
  }
  /**
   * 插入空行
   */
  public void recordNull() {
    String[] datas = new String[11];
    for (int i = 0; i <= 10; i++) {
      datas[i] = "--";
    }
    TestDataTools.insert("nl3b_testdata", datas);
  }
  /**
   * 初始化饼图和测试数据
   */
  public void initCountAndPieChart() {
    RecordData rd = RecordTools.getByDate("nl3b_recordtd", LocalDate.now().toString());
    if(rd != null) {
      okCount = Integer.parseInt(rd.getOk());
      ngCount = Integer.parseInt(rd.getNg());
      totalCount = Integer.parseInt(rd.getSum());
      timeCount = 0;
    }
    else {
      okCount = 0;
      ngCount = 0;
      totalCount = 0;
      timeCount = 0;
    }
    okField.setText(okCount + "");
    ngField.setText(ngCount + "");
    totalField.setText(totalCount + "");
    timeField.setText(timeCount + "");
    setPieChart(okCount, ngCount);
  }
  /**
   * 调用测试页面的方法
   */
  public static void getDataView() {
    EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        NL3BDataView win = new NL3BDataView();
        win.frame.setVisible(true);
        win.setTableCellRenderer();
      }
    });
  }

}
