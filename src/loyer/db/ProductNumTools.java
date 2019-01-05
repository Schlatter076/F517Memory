package loyer.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JOptionPane;

public class ProductNumTools {

  private ProductNumTools() {}
  /**
   * 获取产品编号计数
   * @param name
   * @param date
   * @return
   */
  public static int getProductNum(String name, String date) {
    int num = -1;
    String sql = "select * from product_num where name='"+name+"' and date='"+date+"'";
    ResultSet rs = DBHelper.search(sql, null);
    try {
      if(rs.next()) {
        num =  rs.getInt(2);
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "产品编号加载失败:" + e.getLocalizedMessage());
    }
    DBHelper.close();
    return num;
  }
  /**
   * 修改产品编号计数
   * @param name
   * @param num
   * @param date
   * @return
   */
  public static int updateProductNum(String name, int num, String date) {
    String sql = "update product_num set num='"+num+"' where name='"+name+"' and date='"+date+"'";
    int back =  DBHelper.AddU(sql, null);
    DBHelper.close();
    return back;
  }
  /**
   * 向表中插入一条数据
   * @param datas
   * @return
   */
  public static int insert(String[] datas) {
    if(datas == null || datas.length != 3) {
      JOptionPane.showMessageDialog(null, "数据格式有误，请重试!");
      return -1;
    } else {
      String sql = "insert into product_num valus(?, ?, ?)";
      int back =  DBHelper.AddU(sql, datas);
      DBHelper.close();
      return back;
    }
  }
}
