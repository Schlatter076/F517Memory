package loyer.client;

import java.awt.EventQueue;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.Timer;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import loyer.db.DataTools;
import loyer.db.ProductNumTools;
import loyer.db.RecordTools;
import loyer.db.RecordTools.RecordData;
import loyer.db.TestDataTools;
import loyer.db.UserTools;
import loyer.db.UserTools.UserData;
import loyer.exception.NoSuchPort;
import loyer.exception.NotASerialPort;
import loyer.exception.PortInUse;
import loyer.exception.SerialPortParamFail;
import loyer.exception.TooManyListeners;
import loyer.gui.LoyerFrame;
import loyer.serial.SerialPortTools;

public class F517DataView extends LoyerFrame {

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
  /**产品编号计数器*/
  private int productCount = 0;
  /**串口列表*/
  private ArrayList<String> portList = SerialPortTools.findPort();
  private SerialPort COM1;
  private SerialPort COM2;
  private SerialPort COM3;
  Timer timer;

  static {
    // 加载用户数据
    admin = UserTools.getUserByID(1);
    nayin = UserTools.getUserByID(3);
  }

  public F517DataView() {
    initialize();
  }

  private void initialize() {

    PRODUCT_NAME = "F517记忆开关";
    productField.setText(PRODUCT_NAME);
    table = DataTools.completedTable(DataTools.getTestTable("f517"));
    dataPanel.setViewportView(table);
    persistScroll.setViewportView(new JLabel(new ImageIcon(JLabel.class.getResource("/pic/frame.jpg"))));
    // setPieChart(10, 2);
    com1Butt.addActionListener(e -> {
      if(COM1 == null) {  //如果串口1被关闭了
        initCOM1();
      }
      else
        com1Butt.setSelected(true);
    });
    com2Butt.addActionListener(e -> {
      if(COM2 == null) {
        initCOM2();
      }
      else
        com2Butt.setSelected(true);
    });
    com3Butt.addActionListener(e -> {
      if(COM3 == null) {
        initCOM3();
      }
      else
        com3Butt.setSelected(true);
    });
    
    timer = new Timer(1500, e-> {
      productCount++;
      scanField.setText(scanTest());
      for(int i = 1; i < 14; i++) {
        record(i, "测试");
      }
      recordNull();
    });
    timer.start();//*/
  }

  @Override
  public boolean pwdIsPassed(String command) {
    return false;
  }

  @Override
  public void usartMethod() {
    JPasswordField pf = new JPasswordField();
    pf.setFont(new Font("宋体", Font.PLAIN, 17));
    pf.setEchoChar('*');
    JOptionPane.showMessageDialog(null, pf, "请输入管理员密码：", JOptionPane.PLAIN_MESSAGE);
    char[] pwd = pf.getPassword();
    if(pwd.length == 8) {
      if(String.valueOf(pwd).equals(admin.getPwd())) {
        UsartTools.getUsartTools();
      }
      else 
        JOptionPane.showMessageDialog(null, "密码错误！");
    }
    else
      JOptionPane.showMessageDialog(null, "密码长度为8位！");
  }

  @Override
  public void resultView() {
  }

  @Override
  public void reportView() {
    productCount++;
    scanField.setText(scanTest());
    for(int i = 1; i < 14; i++) {
      record(i, "测试");
    }
    recordNull();
  }

  @Override
  public void nayinMethod() {
    // 由于要触发事件，复选框状态定会改变，故而多加了判断
    JPasswordField pw = new JPasswordField();
    pw.setFont(new Font("宋体", Font.PLAIN, 17));
    pw.setEchoChar('*');
    JOptionPane.showMessageDialog(null, pw, "请输入捺印密码：", JOptionPane.PLAIN_MESSAGE);
    char[] pass = pw.getPassword();
    if (pass.length > 0 && pass.length <= 6) {
      if (String.valueOf(pass).equals(nayin.getPwd())) {
        if (nayinButt.isSelected()) {
          nayinButt.setSelected(true);
        } else
          nayinButt.setSelected(false);
      } else {
        JOptionPane.showMessageDialog(null, "密码错误！");
        if (nayinButt.isSelected()) {
          nayinButt.setSelected(false);
        } else
          nayinButt.setSelected(true);
      }
    } else {
      JOptionPane.showMessageDialog(null, "密码长度为6位！");
      if (nayinButt.isSelected()) {
        nayinButt.setSelected(false);
      } else
        nayinButt.setSelected(true);
    }
  }

  @Override
  public void close() {
    RecordTools.outExcl("f517_recordtd");
    TestDataTools.outExcl("f517_testdata");
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
    TestDataTools.insert("f517_testdata", datas);
  }
  /**
   * 插入空行
   */
  public void recordNull() {
    String[] datas = new String[11];
    for (int i = 0; i <= 10; i++) {
      datas[i] = "--";
    }
    datas[9] = LocalDate.now().toString();
    TestDataTools.insert("f517_testdata", datas);
  }
  /**
   * 初始化饼图和测试数据
   */
  public void initCountAndPieChart() {
    RecordData rd = RecordTools.getByDate("f517_recordtd", LocalDate.now().toString());
    productCount = ProductNumTools.getProductNum("f517", LocalDate.now().toString());
    if(productCount == -1) productCount = 0;
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
   * 初始化串口1
   */
  public void initCOM1() {
    if(portList.contains("COM1") && COM1 == null) {
      try {
        COM1 = SerialPortTools.getPort(1);
      } catch (SerialPortParamFail | NotASerialPort | NoSuchPort | PortInUse e) {
        JOptionPane.showMessageDialog(null, "COM1:" + e.toString());
      }
      com1Butt.setSelected(true);
      try {
        SerialPortTools.add(COM1, event -> {
          switch (event.getEventType()) {
          case SerialPortEvent.BI:  //10 通讯中断
            JOptionPane.showMessageDialog(null, "COM1:" + "通讯中断!");
            break;
          case SerialPortEvent.OE:  // 7 溢位（溢出）错误
            JOptionPane.showMessageDialog(null, "COM1:" + "溢位（溢出）错误!");
            break;
          case SerialPortEvent.FE:  // 9 帧错误
            JOptionPane.showMessageDialog(null, "COM1:" + "帧错误!");
            break;
          case SerialPortEvent.PE:  // 8 奇偶校验错误
            JOptionPane.showMessageDialog(null, "COM1:" + "奇偶校验错误!");
            break;
          case SerialPortEvent.CD:  // 6 载波检测
            JOptionPane.showMessageDialog(null, "COM1:" + "载波检测!");
            break;
          case SerialPortEvent.CTS:  // 3 清除待发送数据
            JOptionPane.showMessageDialog(null, "COM1:" + "清除待发送数据!");
            break;
          case SerialPortEvent.DSR:  // 4 待发送数据准备好了
            JOptionPane.showMessageDialog(null, "COM1:" + "待发送数据准备好了!");
            break;
          case SerialPortEvent.RI:  // 5 振铃指示
            JOptionPane.showMessageDialog(null, "COM1:" + "振铃指示!");
            break;
          case SerialPortEvent.OUTPUT_BUFFER_EMPTY:  // 2 输出缓冲区已清空
            JOptionPane.showMessageDialog(null, "COM1:" + "输出缓冲区已清空");
            break;
          case SerialPortEvent.DATA_AVAILABLE: {
            // 有数据到达-----可以开始处理
            COM1DatasArrived();
          }
            break;
          }
        });
        //SerialPortTools.writeString(COM1, "UTF-8", "*RST");
        //SerialPortTools.writeString(COM1, "UTF-8", ":RATE:RESistance");
      } catch (TooManyListeners e) {
        JOptionPane.showMessageDialog(null, "COM1:" + e.toString());
      }
    }
    else {
      JOptionPane.showMessageDialog(null, "未发现串口1！");
      com1Butt.setSelected(false);
    }
  }
  /**
   * 串口1数据到达
   */
  public void COM1DatasArrived() {
    
  }
  /**
   * 初始化串口2
   */
  public void initCOM2() {
    if(portList.contains("COM2") && COM2 == null) {
      try {
        COM2 = SerialPortTools.getPort(2);
      } catch (SerialPortParamFail | NotASerialPort | NoSuchPort | PortInUse e) {
        JOptionPane.showMessageDialog(null, "COM2:" + e.toString());
      }
      com2Butt.setSelected(true);
      try {
        SerialPortTools.add(COM2, arg0 -> {
          switch (arg0.getEventType()) {
          case SerialPortEvent.BI:  //10 通讯中断
          case SerialPortEvent.OE:  // 7 溢位（溢出）错误
          case SerialPortEvent.FE:  // 9 帧错误
          case SerialPortEvent.PE:  // 8 奇偶校验错误
          case SerialPortEvent.CD:  // 6 载波检测
          case SerialPortEvent.CTS:  // 3 清除待发送数据
          case SerialPortEvent.DSR:  // 4 待发送数据准备好了
          case SerialPortEvent.RI:  // 5 振铃指示
          case SerialPortEvent.OUTPUT_BUFFER_EMPTY:  // 2 输出缓冲区已清空
            JOptionPane.showMessageDialog(null, "COM2错误：" + arg0.toString());
            break;
          case SerialPortEvent.DATA_AVAILABLE: {
            COM2DatasArrived();
          }
            break;
          }
        });
        //SerialPortTools.writeBytes(COM2, new byte[]{(byte) 0xf3, (byte) 0xf4, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0a});
      } catch (TooManyListeners e) {
        JOptionPane.showMessageDialog(null, "COM2:" + e.toString());
      }
    }
    else {
      JOptionPane.showMessageDialog(null, "未发现串口2！");
      com2Butt.setSelected(false);
    }
  }
  /**
   * 串口2数据到达
   */
  public void COM2DatasArrived() {
    
  }
  /**
   * 初始化串口3
   */
  public void initCOM3() {
    if(portList.contains("COM3") && COM3 == null) {
      try {
        COM3 = SerialPortTools.getPort(3);
      } catch (SerialPortParamFail | NotASerialPort | NoSuchPort | PortInUse e) {
        JOptionPane.showMessageDialog(null, "COM3:" + e.toString());
      }
      com3Butt.setSelected(true);
      try {
        SerialPortTools.add(COM3, arg0 -> {
          switch (arg0.getEventType()) {
          case SerialPortEvent.BI:  //10 通讯中断
          case SerialPortEvent.OE:  // 7 溢位（溢出）错误
          case SerialPortEvent.FE:  // 9 帧错误
          case SerialPortEvent.PE:  // 8 奇偶校验错误
          case SerialPortEvent.CD:  // 6 载波检测
          case SerialPortEvent.CTS:  // 3 清除待发送数据
          case SerialPortEvent.DSR:  // 4 待发送数据准备好了
          case SerialPortEvent.RI:  // 5 振铃指示
          case SerialPortEvent.OUTPUT_BUFFER_EMPTY:  // 2 输出缓冲区已清空
            JOptionPane.showMessageDialog(null, "COM3错误：" + arg0.toString());
            break;
          case SerialPortEvent.DATA_AVAILABLE: {
            COM3DatasArrived();
          }
            break;
          }
        });
        //SerialPortTools.writeBytes(COM2, new byte[]{(byte) 0xf3, (byte) 0xf4, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0a});
      } catch (TooManyListeners e) {
        JOptionPane.showMessageDialog(null, "COM3:" + e.toString());
      }
    }
    else {
      JOptionPane.showMessageDialog(null, "未发现串口3！");
      com3Butt.setSelected(false);
    }
  }
  /**
   * 串口3数据到达
   */
  public void COM3DatasArrived() {
    
  }
  /**
   * 初始化表格
   */
  public void initTable() {
    for(int i = 1; i <= 13; i++) {
      table.setValueAt("?", i, 5); // 清空测试值
      table.setValueAt("?", i, 7); // 清空测试结果
    }
  }
  /**
   * 载入
   */
  public void initLoad() {
    initCountAndPieChart();
    initTable();
    initCOM1();
    initCOM2();
    initCOM3();
  }
  /**
   * 调用测试页面的方法
   */
  public static void getDataView() {
    EventQueue.invokeLater(new Runnable() {

      @Override
      public void run() {
        F517DataView win = new F517DataView();
        win.frame.setVisible(true);
        win.setTableCellRenderer();
        win.initLoad();
      }
    });
  }

}
