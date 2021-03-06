package loyer.serial;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TooManyListenersException;

import javax.swing.JOptionPane;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import loyer.db.DBHelper;
import loyer.exception.NoSuchPort;
import loyer.exception.NotASerialPort;
import loyer.exception.PortInUse;
import loyer.exception.SerialPortParamFail;
import loyer.exception.TooManyListeners;

/**
 * 串口工具类
 * 
 * @author hw076
 *
 */
public class SerialPortTools {

  /**ASCII码校验集合 */
  private static Map<String, String> map = new HashMap<>();
  static {
    for (int i = 0; i < 10; i++) {
      map.put(i + "", (30 + i) + "");
    }
    map.put("a", "41");
    map.put("b", "42");
    map.put("c", "43");
    map.put("d", "44");
    map.put("e", "45");
    map.put("f", "46");
  }

  private SerialPortTools() {
  } // 不允许其他类创建本类实例

  /**
   * 列出可用端口
   * 
   * @return
   */
  public static ArrayList<String> findPort() {
    ArrayList<String> list = new ArrayList<>();
    @SuppressWarnings("unchecked")
    Enumeration<CommPortIdentifier> comm = CommPortIdentifier.getPortIdentifiers();
    while (comm.hasMoreElements()) {
      list.add(comm.nextElement().getName());
    }
    return list;
  }

  private static SerialPortData getPortData(int number) throws SQLException {
    SerialPortData data = null;
    String sql = "select * from serialports where xuhao='" + number + "'";
    ResultSet rs = DBHelper.search(sql, null);
    if (rs.next()) {
      data = new SerialPortData(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4), rs.getInt(5), rs.getInt(6));
    }
    return data;
  }

  /**
   * 根据数据库中串口参数获取串口
   * 
   * @param number
   * @return
   * @throws NoSuchPort
   * @throws PortInUse
   * @throws SerialPortParamFail
   * @throws NotASerialPort
   */
  public static SerialPort getPort(int number) throws NoSuchPort, PortInUse, SerialPortParamFail, NotASerialPort {
    SerialPortData data = null;
    try {
      data = getPortData(number);
    } catch (SQLException e) {
      JOptionPane.showMessageDialog(null, "串口参数获取失败：" + e.getLocalizedMessage());
    }
    try {
      CommPortIdentifier comm = CommPortIdentifier.getPortIdentifier(data.getPortName());
      CommPort commPort = comm.open(data.getPortName(), 2000); // 打开端口，设置延时
      if (commPort instanceof SerialPort) {
        SerialPort port = (SerialPort) commPort;
        try {
          port.setSerialPortParams(data.getBaudRate(), data.getDataBits(), data.getStopBits(), data.getParity());
        } catch (UnsupportedCommOperationException e) {
          throw new SerialPortParamFail();
        }
        return port;
      } else {
        throw new NotASerialPort();
      }
    } catch (NoSuchPortException e) {
      throw new NoSuchPort();
    } catch (PortInUseException e) {
      throw new PortInUse();
    }
  }

  /**
   * 重载串口获取方法
   * 
   * @param portName
   * @param baud
   * @param dataBits
   * @param stopBits
   * @param parity
   * @return
   * @throws SerialPortParamFail
   * @throws NotASerialPort
   * @throws NoSuchPort
   * @throws PortInUse
   */
  public static SerialPort getPort(String portName, int baud, int dataBits, int stopBits, int parity)
      throws SerialPortParamFail, NotASerialPort, NoSuchPort, PortInUse {
    try {
      CommPortIdentifier comm = CommPortIdentifier.getPortIdentifier(portName);
      CommPort commPort = comm.open(portName, 2000); // 打开端口，设置延时
      if (commPort instanceof SerialPort) {
        SerialPort port = (SerialPort) commPort;
        try {
          port.setSerialPortParams(baud, dataBits, stopBits, parity);
        } catch (UnsupportedCommOperationException e) {
          throw new SerialPortParamFail();
        }
        return port;
      } else {
        throw new NotASerialPort();
      }
    } catch (NoSuchPortException e) {
      throw new NoSuchPort();
    } catch (PortInUseException e) {
      throw new PortInUse();
    }
  }

  /**
   * 发送字符串
   * 
   * @param port
   *          串口对象
   * @param charsetName
   *          字符集名称，如UTF-8
   * @param command
   *          待发送的指令
   * @return
   */
  public static boolean writeString(SerialPort port, String charsetName, String command) {
    try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(port.getOutputStream(), Charset.forName(charsetName)),
        true)) {
      pw.println(command);
      return true;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据发送失败::" + e.getLocalizedMessage());
      return false;
    }
  }

  /**
   * 回读字符串
   * 
   * @param port
   *          指定的串口对象
   * @param charsetName
   *          字符集名
   * @return
   */
  public static String readString(SerialPort port, String charsetName) {
    try (BufferedReader br = new BufferedReader(new InputStreamReader(port.getInputStream(), charsetName))) {
      char[] data = new char[1024];
      int len = br.read(data);
      return new String(data, 0, len);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据接收失败::" + e.getLocalizedMessage());
      return "";
    }
  }

  /**
   * 发送字节数组
   * 
   * @param port
   *          串口对象
   * @param datas
   *          待发送的数据
   * @return
   */
  public static boolean writeBytes(SerialPort port, byte[] datas) {
    try (BufferedOutputStream bos = new BufferedOutputStream(port.getOutputStream())) {
      bos.write(datas);
      bos.flush();
      return true;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据发送失败::" + e.getLocalizedMessage());
      return false;
    }
  }

  /**
   * 读取一个字节
   * 
   * @param port
   *          串口对象
   * @return
   */
  public static byte readByte(SerialPort port) {
    try (BufferedInputStream bis = new BufferedInputStream(port.getInputStream())) {
      return (byte) bis.read();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据接收失败::" + e.getLocalizedMessage());
      return -1;
    }
  }

  /**
   * 读取字节数组
   * 
   * @param port
   * @return
   */
  public static byte[] readBytes(SerialPort port) {
    try (BufferedInputStream bis = new BufferedInputStream(port.getInputStream())) {
      int len = bis.available();
      byte[] values = new byte[len];
      bis.read(values);
      return values;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据接收失败::" + e.getLocalizedMessage());
      return new byte[0];
    }
  }

  /**
   * 发送字符数组
   * 
   * @param port
   * @param datas
   * @param charsetName
   * @return
   */
  public static boolean writeChars(SerialPort port, char[] datas, String charsetName) {
    try (BufferedWriter bw = new BufferedWriter(
        new OutputStreamWriter(port.getOutputStream(), Charset.forName(charsetName)))) {
      bw.write(datas);
      bw.flush();
      return true;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据发送失败::" + e.getLocalizedMessage());
      return false;
    }
  }

  /**
   * 接收字符数组
   * 
   * @param port
   * @param charsetName
   * @return
   */
  public static char[] readChars(SerialPort port, String charsetName) {
    try (BufferedReader br = new BufferedReader(
        new InputStreamReader(port.getInputStream(), Charset.forName(charsetName)))) {
      char[] values = new char[1024];
      br.read(values);
      return values;
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, port.getName() + "数据接收失败::" + e.getLocalizedMessage());
      return new char[0];
    }
  }

  /**
   * 获取十六进制整数的ASCII码字符
   * 
   * @param ascii
   * @return
   */
  public static char byteAsciiToChar(int ascii) {
    char ch = (char) ascii;
    return ch;
  }

  /**
   * 将十六进制字符串转成字节数组
   * 
   * @param hexString
   * @return
   */
  public static byte[] toByteArray(String hexString) {
    hexString = hexString.replaceAll(" ", ""); // 去掉空格
    hexString = hexString.toLowerCase(); // 变成小写
    int len = 0;
    if (hexString.length() % 2 == 0) {
      len = hexString.length() / 2;
    } else {
      len = hexString.length() / 2 + 1;
    }
    byte[] array = new byte[len];
    int index = 0;
    for (int i = 0; i < array.length; i++) {
      byte high = (byte) (Character.digit(hexString.charAt(index), 16) & 0xff);
      byte low = (byte) (Character.digit(hexString.charAt(index + 1), 16) & 0xff);
      array[i] = (byte) (high << 4 | low);
      index += 2;
    }
    return array;
  }

  /**
   * 将字节数组转换成16进制字符串
   * 
   * @param datas
   * @return
   */
  public static String bytesToHex(byte[] datas) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < datas.length; i++) {
      String hex = Integer.toHexString(datas[i] & 0xff);
      if (hex.length() < 2) {
        sb.append(0);
      }
      sb.append(hex);
      sb.append(' ');
    }
    return sb.toString();
  }

  /**
   * 回读三菱PLC数据寄存器指令生成
   * 
   * @param num
   *          数据寄存器编号
   * @return
   */
  public static String readPLCDataRegister(int num) {

    StringBuilder sb = new StringBuilder();

    int addr = num * 2 + 4096;
    int chk = 149;
    char[] cc = Integer.toHexString(addr).toLowerCase().toCharArray();
    sb.append("02");
    sb.append(" ");
    sb.append("30");
    sb.append(" ");
    for (int i = 0; i < 4; i++) {
      String val = map.get(String.valueOf(cc[i]));
      chk += Integer.parseInt(val, 16);
      sb.append(val);
      sb.append(" ");
    }
    sb.append("30");
    sb.append(" ");
    sb.append("32");
    sb.append(" ");
    sb.append("03");
    sb.append(" ");

    char[] ccc = Integer.toHexString(chk & 0xff).toCharArray();
    for (int i = 0; i < 2; i++) {
      sb.append(map.get(String.valueOf(ccc[i])));
      sb.append(" ");
    }
    return sb.toString().trim();
  }

  /**
   * 写入三菱PLC数据寄存器指令生成
   * @param num 数据寄存器编号
   * @param val 写入的值
   * @return
   */
  public static String writePLCDataRegister(int num, int val) {
    
    StringBuilder sb = new StringBuilder();
    int addr = num * 2 + 4096;
    int chk = 150;
    char[] cc = Integer.toHexString(addr).toLowerCase().toCharArray();
    sb.append("02");
    sb.append(" ");
    sb.append("31");
    sb.append(" ");
    for(int i = 0; i < 4; i++) {
      String value = map.get(String.valueOf(cc[i]));
      chk += Integer.parseInt(value, 16);
      sb.append(value);
      sb.append(" ");
    }
    sb.append("30");
    sb.append(" ");
    sb.append("32");
    sb.append(" ");
    int H = (val >> 8) & 0xff; //取高位
    int L = val & 0xff; //取低位
    char[] hc = String.format("%02x", H).toCharArray();
    char[] lc = String.format("%02x", L).toCharArray();
    
    for(int i = 0; i < 2; i++) {
      String v = map.get(String.valueOf(lc[i]));
      chk += Integer.parseInt(v, 16);
      sb.append(v);
      sb.append(" ");
    }
    for(int i = 0; i < 2; i++) {
      String v = map.get(String.valueOf(hc[i]));
      chk += Integer.parseInt(v, 16);
      sb.append(v);
      sb.append(" ");
    }
    sb.append("03");
    sb.append(" ");
    
    char[] ccc = Integer.toHexString(chk & 0xff).toCharArray();
    for(int i = 0; i < 2; i++) {
      sb.append(map.get(String.valueOf(ccc[i])));
      sb.append(" ");
    }
    return sb.toString().trim();
  }
  /**
   * 将从串口接收到PLC传回的数据转换成实际值
   * @param readData
   * @return
   */
  public static int checkPLCData(byte[] readData) {
    int val = -1;
    if(readData != null && readData.length > 0) {
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < readData.length; i++) {
        if(String.format("%02x", readData[i]).equals("02")) {
          sb.append(byteAsciiToChar(readData[i + 3]));
          sb.append(byteAsciiToChar(readData[i + 4]));
          sb.append(byteAsciiToChar(readData[i + 1]));
          sb.append(byteAsciiToChar(readData[i + 2]));
          val = Integer.parseInt(sb.toString(), 16);
          break;
        }
      }
    }
    return val;
  }
  /**
   * 是否相等
   * @param hex
   * @param data
   * @return
   */
  public static boolean isEquals(byte hex, String data) {
    String s1 = String.format("%02x", hex);
    if (s1.equals(data))
      return true;
    else
      return false;
  }
  /**
   * 获取拉力表检验和字符串(连续方式)
   * @param data
   * @return
   */
  public static String getCRCcheckSum(byte[] data) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < data.length; i++) {
      if(isEquals(data[i], "02") && isEquals(data[i + 14], "0d") && isEquals(data[i + 15], "0a")) {
        int sum = 0;
        for(int j = 0; j < 12; j++) {
          sum += data[i + j]; //求和
        }
        sum = Integer.parseInt(Integer.toString(sum, 10), 16) & 0xff; //取低8位
        sb.append(Integer.toHexString(sum));
        break;
      }
    }
    char[] cc = sb.toString().trim().toCharArray();
    sb.delete(0, sb.length()); //清空容器
    for(int i = 0; i < 2; i++) {
      sb.append(map.get(String.valueOf(cc[i])));
      sb.append(' ');
    }
    return sb.toString();
  }
  /**
   * 获取拉力值(连续方式)
   * @param data
   * @return
   */
  public static int getRallyMeterVal(byte[] data) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < data.length; i++) {
      if(isEquals(data[i], "02") && isEquals(data[i + 14], "0d") && isEquals(data[i + 15], "0a")) {
        byte[] crc = toByteArray(getCRCcheckSum(data));
        if(crc[0] == data[i + 12] && crc[1] == data[i + 13]) {  //判断校验和
          sb.append(byteAsciiToChar(data[i + 6]));
          sb.append(byteAsciiToChar(data[i + 7]));
          sb.append(byteAsciiToChar(data[i + 8]));
          sb.append(byteAsciiToChar(data[i + 9]));
          sb.append(byteAsciiToChar(data[i + 10]));
          sb.append(byteAsciiToChar(data[i + 11]));
          break;
        }
      }
    }
    if(sb.length() > 0) {
      return Integer.parseInt(sb.toString().trim());
    } else {
      return -1;
    }
  }
  /**
   * 发送读取拉力表数据指令
   * @param port
   */
  public static void sendCommand2Read(SerialPort port) {
    writeBytes(port, toByteArray("02 30 31 31 52 57 54 30 31 0d 0a"));
  }
  /**
   * 获取拉力表检验和字符串(命令方式)
   * @param data
   * @return
   */
  public static String getCRCcheckSum_command(byte[] data) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < data.length; i++) {
      if(isEquals(data[i], "02") && isEquals(data[i + 17], "0d") && isEquals(data[i + 18], "0a")) {
        int sum = 0;
        for(int j = 0; j < 15; j++) {
          sum += data[i + j]; //求和
        }
        sum = Integer.parseInt(Integer.toString(sum, 10), 16) & 0xff; //取低8位
        sb.append(Integer.toHexString(sum));
        break;
      }
    }
    char[] cc = sb.toString().trim().toCharArray();
    sb.delete(0, sb.length()); //清空容器
    for(int i = 0; i < 2; i++) {
      sb.append(map.get(String.valueOf(cc[i])));
      sb.append(' ');
    }
    return sb.toString();
  }
  /**
   * 命令方式下拉力表返回拉力值
   * @param data
   * @return
   */
  public static int readRallyMeter(byte[] data) {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i < data.length; i++) {
      if(isEquals(data[i], "02") && isEquals(data[i + 17], "0d") && isEquals(data[i + 18], "0a")) {
        byte[] crc = toByteArray(getCRCcheckSum_command(data));
        if(crc[0] == data[i + 15] && crc[1] == data[i + 16]) {  //判断校验和
          sb.append(byteAsciiToChar(data[i + 9]));
          sb.append(byteAsciiToChar(data[i + 10]));
          sb.append(byteAsciiToChar(data[i + 11]));
          sb.append(byteAsciiToChar(data[i + 12]));
          sb.append(byteAsciiToChar(data[i + 13]));
          sb.append(byteAsciiToChar(data[i + 14]));
          break;
        }
      }
    }
    if(sb.length() > 0) {
      return Integer.parseInt(sb.toString().trim());
    } else {
      return -1;
    }
  }
  /**
   * 拉力表清零
   * @param port
   */
  public static void clearRallyMeter(SerialPort port) {
    writeBytes(port, toByteArray("02 30 31 31 4e 43 5a 38 34 0d 0a"));
  }
  
  /**
   * 注册监听器
   * 
   * @param port
   * @param listener
   * @throws TooManyListeners
   */
  public static void add(SerialPort port, SerialPortEventListener listener) throws TooManyListeners {

    try {
      // 给串口添加监听器
      port.addEventListener(listener);
      // 设置当有数据到达时唤醒监听接收线程
      port.notifyOnDataAvailable(true);
      // 设置当通信中断时唤醒中断线程
      port.notifyOnBreakInterrupt(true);

    } catch (TooManyListenersException e) {
      throw new TooManyListeners();
    }
  }

  //////////////////////////////////////////////////////////////
  /**
   * serialorts表的实体
   * 
   * @author hw076
   *
   */
  public static class SerialPortData {

    private int number;
    private String PortName;
    private int baudRate;
    private int dataBits;
    private int stopBits;
    private int parity;

    public SerialPortData() {
      super();
    }

    public SerialPortData(int number, String portName, int baudRate, int dataBits, int stopBits, int parity) {
      super();
      this.number = number;
      PortName = portName;
      this.baudRate = baudRate;
      this.dataBits = dataBits;
      this.stopBits = stopBits;
      this.parity = parity;
    }

    public int getNumber() {
      return number;
    }

    public void setNumber(int number) {
      this.number = number;
    }

    public String getPortName() {
      return PortName;
    }

    public void setPortName(String portName) {
      PortName = portName;
    }

    public int getBaudRate() {
      return baudRate;
    }

    public void setBaudRate(int baudRate) {
      this.baudRate = baudRate;
    }

    public int getDataBits() {
      return dataBits;
    }

    public void setDataBits(int dataBits) {
      this.dataBits = dataBits;
    }

    public int getStopBits() {
      return stopBits;
    }

    public void setStopBits(int stopBits) {
      this.stopBits = stopBits;
    }

    public int getParity() {
      return parity;
    }

    public void setParity(int parity) {
      this.parity = parity;
    }

  }
}
