/**
 * FromIB.java Created Mar 21, 2011 by rickcharon.
 *
 */
package ptstrader;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;
import org.joda.time.DateTime;
import ptsutils.PtsIBWrapperAdapter;
import ptsutils.TwsMsgGenerator;

public class FromIB extends PtsIBWrapperAdapter {

  @Override
  public void orderStatus(int orderId, String status, int filled, int remaining, double avgFillPrice,
          int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {
    String msg = TwsMsgGenerator.orderStatus(orderId, status, filled, remaining,
            avgFillPrice, permId, parentId, lastFillPrice, clientId, whyHeld);
    System.out.println(msg);
  }

  @Override
  public void openOrder(int orderId, Contract contract, Order order, OrderState orderState) {
    String msg = TwsMsgGenerator.openOrder(orderId, contract, order, orderState);
    System.out.println(msg);
  }

  @Override
  public void openOrderEnd() {
    String msg = TwsMsgGenerator.openOrderEnd();
    System.out.println(msg);
  }

  @Override
  public void nextValidId(int orderId) {
    PtsTrader.setNextOrderID(orderId);
    String msg = TwsMsgGenerator.nextValidId(orderId);
    System.out.println(msg);
  }

  @Override
  public void error(Exception ex) {
    String msg = TwsMsgGenerator.error(ex);
    System.out.println(msg);
  }

  @Override
  public void error(String str) {
    String msg = TwsMsgGenerator.error(str);
    System.out.println(msg);
  }

  @Override
  public void error(int id, int errorCode, String errorMsg) {
    String msg = TwsMsgGenerator.error(id, errorCode, errorMsg);
    System.out.println(msg);
  }

  @Override
  public void connectionClosed() {
    String msg = TwsMsgGenerator.connectionClosed();
    System.out.println(msg);
  }

  @Override
  public void currentTime(long time) {
    //1/18/11 1:53 PM time is in seconds since epoch, so need to * 1000 to get millis
    DateTime dd = new DateTime(time * 1000);
    System.out.println("Current Server Time from TWS: " + dd);
  }
}
