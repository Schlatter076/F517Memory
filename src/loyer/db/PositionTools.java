package loyer.db;

import java.awt.Color;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 * 点击运行位置工具类
 * @author hw076
 *
 */
public class PositionTools {

  private PositionTools() {} //不允许其他类创建本类实例
  /**
   * 获取全部位置参数
   * @return
   */
  public static List<PositionData> getAllByDB() {
    List<PositionData> list = new ArrayList<>();
    String sql = "select * from position";
    ResultSet rs = DBHelper.search(sql, null);
    try {
      while(rs.next()) {
        int number = rs.getInt(1);
        String name = rs.getString(2);
        int Xposition = rs.getInt(3);
        int Zposition = rs.getInt(4);
        int Xtemp = rs.getInt(5);
        int Ztemp = rs.getInt(6);
        String date = rs.getString(7);
        String tips = rs.getString(8);
        
        list.add(new PositionData(number, name, Xposition, Zposition, Xtemp, Ztemp, date, tips));
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "位置参数加载失败:" + e.getLocalizedMessage());
    }
    DBHelper.close();
    return list;
  }
  /**
   * 获取对应机种名的全部按钮参数
   * @param name
   * @return
   */
  public static List<PositionData> getByName(String name) {
    List<PositionData> list = new ArrayList<>();
    String sql = "select * from position where name='"+name+"'";
    ResultSet rs = DBHelper.search(sql, null);
    try {
      while(rs.next()) {
        int number = rs.getInt(1);
        int Xposition = rs.getInt(3);
        int Zposition = rs.getInt(4);
        int Xtemp = rs.getInt(5);
        int Ztemp = rs.getInt(6);
        String date = rs.getString(7);
        String tips = rs.getString(8);
        
        list.add(new PositionData(number, name, Xposition, Zposition, Xtemp, Ztemp, date, tips));
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "位置参数加载失败:" + e.getLocalizedMessage());
    }
    DBHelper.close();
    return list;
  }
  /**
   * 获取对应机种名的对应按钮的参数
   * @param name
   * @param number 对应按钮序号
   * @return
   */
  public static PositionData getByName(String name, int number) {
    PositionData data = null;
    String sql = "select * from position where name='"+name+"' and number='"+number+"'";
    ResultSet rs = DBHelper.search(sql, null);
    try {
      if(rs.next()) {
        
        int Xposition = rs.getInt(3);
        int Zposition = rs.getInt(4);
        int Xtemp = rs.getInt(5);
        int Ztemp = rs.getInt(6);
        String date = rs.getString(7);
        String tips = rs.getString(8);
        
        data = new PositionData(number, name, Xposition, Zposition, Xtemp, Ztemp, date, tips);
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "位置参数加载失败:" + e.getLocalizedMessage());
    }
    DBHelper.close();
    return data;
  }
  /**
   * 插入一组调试数据
   * @param datas
   * @return
   */
  public static int insert(String[] datas) {
    if(datas == null || datas.length != 8) {
      JOptionPane.showMessageDialog(null, "数据格式不正确！请检查后重试");
      return -1;
    } else {
      String sql = "insert into position values(?, ?, ?, ?, ?, ?, ?, ?)";
      int back =  DBHelper.AddU(sql, datas);
      DBHelper.close();
      return back;
    }    
  }
  /**
   * 更新原始数据
   * @param datas
   * @return
   */
  public static int update(String[] datas) {
    if(datas == null || datas.length != 8) {
      JOptionPane.showMessageDialog(null, "数据格式不正确！请检查后重试");
      return -1;
    } else {
      String sql = "update position set Xposition='"+datas[2]+"', Zposition='"+datas[3]+"', Xtemp='"+datas[4]+"',"
          + "Ztemp='"+datas[5]+"', date='"+datas[6]+"', tips='"+datas[7]+"' where number='"+datas[0]+"' and name='"+datas[0]+"'";
      
      int back =  DBHelper.AddU(sql, null);
      DBHelper.close();
      return back;
    }    
  }
  
  /**
   * 创建JTable方法
   * 
   * @return
   */
  private static JTable getTestTable(String tableName) {
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
    List<PositionData> tableList = getByName(tableName); 
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
  public static JTable completedTable(String tableName) {

    JTable table = getTestTable(tableName);
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

  
  /////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * position表的实体
   * @author hw076
   *
   */
  public static class PositionData {
    
    private int number;
    private String name;
    private int Xposition;
    private int Zposition;
    private int Xtemp;
    private int Ztemp;
    private String date;
    private String tips;
    
    public PositionData() {
      super();
    }
    public PositionData(int number, String name, int xposition, int zposition, int xtemp, int ztemp, String date,
        String tips) {
      super();
      this.number = number;
      this.name = name;
      Xposition = xposition;
      Zposition = zposition;
      Xtemp = xtemp;
      Ztemp = ztemp;
      this.date = date;
      this.tips = tips;
    }

    public int getNumber() {
      return number;
    }

    public void setNumber(int number) {
      this.number = number;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public int getXposition() {
      return Xposition;
    }
    public void setXposition(int xposition) {
      Xposition = xposition;
    }
    public int getZposition() {
      return Zposition;
    }
    public void setZposition(int zposition) {
      Zposition = zposition;
    }
    public int getXtemp() {
      return Xtemp;
    }
    public void setXtemp(int xtemp) {
      Xtemp = xtemp;
    }
    public int getZtemp() {
      return Ztemp;
    }
    public void setZtemp(int ztemp) {
      Ztemp = ztemp;
    }
    public String getDate() {
      return date;
    }
    public void setDate(String date) {
      this.date = date;
    }
    public String getTips() {
      return tips;
    }
    public void setTips(String tips) {
      this.tips = tips;
    }
  }
}
