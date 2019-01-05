package loyer.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

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
