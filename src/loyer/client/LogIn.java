package loyer.client;

import java.awt.EventQueue;
import java.util.List;

import loyer.db.ProductTypeTools;
import loyer.db.ProductTypeTools.ProductType;
import loyer.gui.LogInFrame;

public class LogIn extends LogInFrame {

  private static List<ProductType> list = null;
  
  static {
    list = ProductTypeTools.getAllByDB();
  }//*/
  
  public LogIn() {
    textField.setText("F517记忆开关");  //设置默认机种
  }
  @Override
  public void logInEvent() {
    if(!isDataView) {
      if(textField.getText().equals(list.get(0).getName())) {
        isDataView = true;
        frame.dispose();
        F517DataView.getDataView();
      } else if(textField.getText().equals(list.get(1).getName())) {
        isDataView = true;
        frame.dispose();
        NL3BDataView.getDataView();
      } else {
        isDataView = true;
        frame.dispose();
        C211DataView.getDataView();
      }
    }
  }
  @Override
  public void chooseEvent() {
    textField.setText(list.get(typeCount % list.size()).getName());
    typeCount++;
  }
  
  public static void logIn() {
    EventQueue.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        LogIn win = new LogIn();
        win.frame.setVisible(true);
      }
    });
  }//*/
  /*
  public static void main(String[] args) {

    EventQueue.invokeLater(new Runnable() {
      
      @Override
      public void run() {
        LogIn win = new LogIn();
        win.frame.setVisible(true);
      }
    });
  }//*/

}
