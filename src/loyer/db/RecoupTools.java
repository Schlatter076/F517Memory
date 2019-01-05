package loyer.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class RecoupTools {

  private RecoupTools() {} //不允许其他类创建本类实例
  /**
   * 获取补偿值
   * @param name
   * @return
   */
  public static RecoupData getByName(String name) {
    RecoupData data = null;
    String sql = "select * from recoup where name='"+name+"'";
    ResultSet rs = DBHelper.search(sql, null);
    try {
      if(rs.next()) {
        String pull_1 = rs.getString(1);
        String pull_2 = rs.getString(2);
        String pull_3 = rs.getString(3);
        String pull_4 = rs.getString(4);
        String resistance = rs.getString(5);
        String stroke_1 = rs.getString(6);
        String stroke_2 = rs.getString(7);
        String stroke_3 = rs.getString(8);
        String stroke_4 = rs.getString(9);
        
        data = new RecoupData(pull_1, pull_2, pull_3, pull_4, resistance, stroke_1, stroke_2, stroke_3, stroke_4, name);
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "补偿值加载失败：" + e.getLocalizedMessage());
    }
    DBHelper.close();
    return data;
  }
  /**
   * 修改补偿值
   * @param name
   * @param datas
   * @return
   */
  public static int update(String name, String[] datas) {
    if(datas == null || datas.length != 9) {
      JOptionPane.showMessageDialog(null, "数据格式不对，请重试！");
      return -1;
    } else {
      String sql = "update recoup set pull_1='"+datas[0]+"', pull_2='"+datas[1]+"', pull_3='"+datas[2]+"', pull_4='"+datas[3]+"',"
          + "resistance='"+datas[4]+"', stroke_1='"+datas[5]+"', stroke_2='"+datas[6]+"', "
              + "stroke_3='"+datas[7]+"', stroke_4='"+datas[8]+"' where name='"+name+"'";
      
      int back =  DBHelper.AddU(sql, null);
      DBHelper.close();
      return back;
    }
  }
  /**
   * 补偿值数据类
   * @author hw076
   *
   */
  public static class RecoupData {
    private String pull_1;
    private String pull_2;
    private String pull_3;
    private String pull_4;
    private String resistance;
    private String stroke_1;
    private String stroke_2;
    private String stroke_3;
    private String stroke_4;
    private String name;
    
    public RecoupData() {
      super();
    }
    public RecoupData(String pull_1, String pull_2, String pull_3, String pull_4, String resistance, String stroke_1,
        String stroke_2, String stroke_3, String stroke_4, String name) {
      super();
      this.pull_1 = pull_1;
      this.pull_2 = pull_2;
      this.pull_3 = pull_3;
      this.pull_4 = pull_4;
      this.resistance = resistance;
      this.stroke_1 = stroke_1;
      this.stroke_2 = stroke_2;
      this.stroke_3 = stroke_3;
      this.stroke_4 = stroke_4;
      this.name = name;
    }
    public String getPull_1() {
      return pull_1;
    }
    public void setPull_1(String pull_1) {
      this.pull_1 = pull_1;
    }
    public String getPull_2() {
      return pull_2;
    }
    public void setPull_2(String pull_2) {
      this.pull_2 = pull_2;
    }
    public String getPull_3() {
      return pull_3;
    }
    public void setPull_3(String pull_3) {
      this.pull_3 = pull_3;
    }
    public String getPull_4() {
      return pull_4;
    }
    public void setPull_4(String pull_4) {
      this.pull_4 = pull_4;
    }
    public String getResistance() {
      return resistance;
    }
    public void setResistance(String resistance) {
      this.resistance = resistance;
    }
    public String getStroke_1() {
      return stroke_1;
    }
    public void setStroke_1(String stroke_1) {
      this.stroke_1 = stroke_1;
    }
    public String getStroke_2() {
      return stroke_2;
    }
    public void setStroke_2(String stroke_2) {
      this.stroke_2 = stroke_2;
    }
    public String getStroke_3() {
      return stroke_3;
    }
    public void setStroke_3(String stroke_3) {
      this.stroke_3 = stroke_3;
    }
    public String getStroke_4() {
      return stroke_4;
    }
    public void setStroke_4(String stroke_4) {
      this.stroke_4 = stroke_4;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    
  }
}
