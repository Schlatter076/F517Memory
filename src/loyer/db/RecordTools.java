package loyer.db;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

/**
 * 测试记录工具类
 * @author hw076
 *
 */
public class RecordTools {

  private RecordTools() {} //不允许其他类创建本类实例
  /**
   * 获取全部测试记录
   * @param tableName 表名
   * @return
   */
  public static List<RecordData> getAllByDB(String tableName) {
    List<RecordData> list = new ArrayList<>();
    String sql = "select * from " + tableName;
    ResultSet rs = DBHelper.search(sql, null);
    try {
      while(rs.next()) {
        String name = rs.getString(1);
        String sum = rs.getString(2);
        String ok = rs.getString(3);
        String ng = rs.getString(4);
        String times = rs.getString(5);
        String date = rs.getString(6);
        
        list.add(new RecordData(name, sum, ok, ng, times, date));
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "测试记录读取失败:" + e.getLocalizedMessage());
    }
    DBHelper.close();
    return list;
  }
  /**
   * 通过指定日期查询测试记录
   * @param tableName
   * @param date
   * @return
   */
  public static RecordData getByDate(String tableName, String date) {
    RecordData data = null;
    String sql = "select * from " + tableName + " where recordtime='"+date+"'";
    ResultSet rs = DBHelper.search(sql, null);
    try {
      if(rs.next()) {
        String name = rs.getString(1);
        String sum = rs.getString(2);
        String ok = rs.getString(3);
        String ng = rs.getString(4);
        String times = rs.getString(5);
        
        data = new RecordData(name, sum, ok, ng, times, date);
      }
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "测试记录读取失败:" + e.getLocalizedMessage());
    }
    DBHelper.close();
    return data;
  }
  /**
   * 测试记录写进数据库
   * @param tableName
   * @param datas
   * @return
   */
  public static int insertRecord(String tableName, String[] datas) {
    int back = 0;
    String sql = "insert into " + tableName + " values(?, ?, ?, ?, ?, ?)";
    if(datas == null || datas.length != 6) {
      JOptionPane.showMessageDialog(null, "数据格式不对，请检查后重新操作！");
      return -1;
    }
    back = DBHelper.AddU(sql, datas);
    DBHelper.close();
    return back;
  }
  /**
   * 更新测试记录
   * @param datas
   * @return
   */
  public static int updataRecord(String tableName, String[] datas) {
    int back = 0;
    if(datas == null || datas.length != 6) {
      JOptionPane.showMessageDialog(null, "数据格式不对，请检查后重新操作！");
      return -1;
    }
    String sql = "update "+tableName+" set recordname='"+datas[0]+"', recordsum='"+datas[1]+"', recordok='"+datas[2]+"',"
        + "recordng='"+datas[3]+"', recordts='"+datas[4]+"' where recordtime='"+datas[5]+"'";//*/
    back = DBHelper.AddU(sql, null);
    DBHelper.close();
    return back;
  }
  /**
   * 导出到excel
   * @param tableName
   */
  public static void outExcl(String tableName) {
    
    WritableWorkbook wwb = null;
    try {
      String path = "excl/";
      File pathFile = new File(path);
      if(!pathFile.isDirectory()) {
        pathFile.mkdirs();
      }
      //创建可写入的Excel工作簿
      String fileName = tableName + "不良记录.xls";
      File file = new File(pathFile, fileName);
      if(!file.exists()) {
        file.createNewFile();
      }
      //以fileName为文件名来创建一个Workbook
      wwb = Workbook.createWorkbook(file);
      
      //创建工作表
      WritableSheet ws = wwb.createSheet("不良记录表", 0);
      
      //查询数据库中所有的数据
      List<RecordData> list = getAllByDB(tableName);
      //要插入到的excl表格的行号，默认从0开始
      Label labelRecordname = new Label(0, 0, "机种名");
      Label labelRecortimes = new Label(1, 0, "测试总数");
      Label labelTestitem = new Label(2, 0, "良品数");
      Label labelMaxvalue = new Label(3, 0, "不良品数");
      Label labelMinvalue = new Label(4, 0, "测试时长");
      Label labelTestvalue = new Label(5, 0, "日期");
      ws.addCell(labelRecordname);
      ws.addCell(labelRecortimes);
      ws.addCell(labelTestitem);
      ws.addCell(labelMaxvalue);
      ws.addCell(labelMinvalue);
      ws.addCell(labelTestvalue);
      for(int i = 0; i < list.size(); i++) {
        Label labelRecordname_i = new Label(0, i+1, list.get(i).getName());
        Label labelRecordtimes_i = new Label(1, i+1, list.get(i).getSum());
        Label labelTestitem_i = new Label(2, i+1, list.get(i).getOk());
        Label labelMaxvalue_i = new Label(3, i+1, list.get(i).getNg());
        Label labelMinvalue_i = new Label(4, i+1, list.get(i).getTimes());
        Label labelTestvalue_i = new Label(5, i+1, list.get(i).getDate());
        ws.addCell(labelRecordname_i);
        ws.addCell(labelRecordtimes_i);
        ws.addCell(labelTestitem_i);
        ws.addCell(labelMaxvalue_i);
        ws.addCell(labelMinvalue_i);
        ws.addCell(labelTestvalue_i);
      }
      //写进文档
      wwb.write();
      
    } catch(Exception e) {
      JOptionPane.showMessageDialog(null, "excl写入失败:" + e.getLocalizedMessage());
    } finally {
      //关闭Excel工作簿对象
      try {
        wwb.close();
      } catch (WriteException | IOException e) {
        JOptionPane.showMessageDialog(null, "excl导出失败:" + e.getLocalizedMessage());
      }
    }
  }
  
  /**
   * recordtd表的实体
   * @author hw076
   *
   */
  public static class RecordData {
    
    private String name;
    private String sum;
    private String ok;
    private String ng;
    private String times;
    private String date;
    
    public RecordData() {
      super();
    }

    public RecordData(String name, String sum, String ok, String ng, String times, String date) {
      super();
      this.name = name;
      this.sum = sum;
      this.ok = ok;
      this.ng = ng;
      this.times = times;
      this.date = date;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getSum() {
      return sum;
    }

    public void setSum(String sum) {
      this.sum = sum;
    }

    public String getOk() {
      return ok;
    }

    public void setOk(String ok) {
      this.ok = ok;
    }

    public String getNg() {
      return ng;
    }

    public void setNg(String ng) {
      this.ng = ng;
    }

    public String getTimes() {
      return times;
    }

    public void setTimes(String times) {
      this.times = times;
    }

    public String getDate() {
      return date;
    }

    public void setDate(String date) {
      this.date = date;
    }    
  }
}
