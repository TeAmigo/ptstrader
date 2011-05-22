/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ptstrader;

import ptsutils.BuySell;
import ptsutils.PtsMySocket;
import com.ib.client.Contract;
import com.ib.client.Order;
import ptsutils.PtsContractFactory;
import ptsutils.PtsIBConnectionManager;

/**
 *
 * @author rickcharon
 */
public class PtsTrader {

  FromIB fromIb = null;
  PtsMySocket socket;
  private static Integer NextOrderID = null;
  private Double stopLoss = null;
  private Double profitLmt = null;
  private Double entryLmt = null;
  private BuySell buysell = null;

  public static Integer getAndIncrementNextOrderID() {
    while (NextOrderID == null) {
      try {
        Thread.sleep(5);
      } catch (InterruptedException ex) {
        System.err.println("InterruptedException in getNextOrderID(): " + ex.getMessage());
      }
    }
    int retVal = NextOrderID;
    NextOrderID++;
    return retVal;
  }

  public static void setNextOrderID(Integer NextOrderID) {
    PtsTrader.NextOrderID = NextOrderID;
  }

  public Double getEntryLmt() {
    return entryLmt;
  }

  public void setEntryLmt(Double entryLmt) {
    this.entryLmt = entryLmt;
  }

  public Double getProfitLmt() {
    return profitLmt;
  }

  public void setProfitLmt(Double profitLmt) {
    this.profitLmt = profitLmt;
  }

  public Double getStopLoss() {
    return stopLoss;
  }

  public void setStopLoss(Double stopLoss) {
    this.stopLoss = stopLoss;
  }

  public PtsTrader(int port) {
    fromIb = new FromIB();
    PtsIBConnectionManager.setPort(port);
    socket = PtsIBConnectionManager.connect(fromIb);
    socket.reqCurrentTime();
  }

  public void reqAllOpenOrders() {
    socket.reqAllOpenOrders();
  }


  public void placeOrder(int id, Contract contract, Order order) {
    System.out.println("Placing order for " + contract.m_symbol);
    socket.placeOrder(order.m_orderId, contract, order);

  }

  public void PlaceBracketOrder(Contract contract, BuySell buyOrSell, int quantity,
          double price, double stopPrice, double profitPrice) {
    Order parent = new Order();
    Order stopLossIn = new Order();
    Order profitStop = new Order();

    parent.m_action = buyOrSell.toString();
    parent.m_outsideRth = true;
    parent.m_totalQuantity = quantity;
    parent.m_orderType = "LMT";
    parent.m_lmtPrice = price;
    parent.m_transmit = false;
    parent.m_orderId = PtsTrader.getAndIncrementNextOrderID();

    placeOrder(parent.m_orderId, contract, parent);

    stopLossIn.m_action = (buyOrSell == BuySell.BUY) ? BuySell.SELL.toString() : BuySell.BUY.toString();
    stopLossIn.m_totalQuantity = quantity;
    stopLossIn.m_orderType = "STP";
    stopLossIn.m_outsideRth = true;
    stopLossIn.m_auxPrice = stopPrice;
    stopLossIn.m_parentId = parent.m_orderId;
    stopLossIn.m_ocaGroup = contract.m_symbol + Integer.toString(parent.m_orderId);
    stopLossIn.m_tif = "GTC";
    stopLossIn.m_transmit = false;
    stopLossIn.m_orderId = PtsTrader.getAndIncrementNextOrderID();

    placeOrder(stopLossIn.m_orderId, contract, stopLossIn);

    profitStop.m_action = stopLossIn.m_action;
    profitStop.m_totalQuantity = 1;
    profitStop.m_orderType = "LMT";
    profitStop.m_outsideRth = true;
    profitStop.m_lmtPrice = profitPrice;
    profitStop.m_parentId = parent.m_orderId;
    profitStop.m_ocaGroup = stopLossIn.m_ocaGroup;
    profitStop.m_tif = "GTC";
    profitStop.m_transmit = false;
    profitStop.m_orderId = PtsTrader.getAndIncrementNextOrderID();

    placeOrder(profitStop.m_orderId, contract, profitStop);
  }

    public void closeConnection() {
    socket.disConnect();
    System.out.println("Connection Closed");
  }


  public static void main(String[] args) {
    PtsTrader trader = new PtsTrader(7510);
    Contract contract = PtsContractFactory.makeContract("AUD", "FUT", "GLOBEX", "201106", "USD");
    trader.PlaceBracketOrder(contract, BuySell.BUY, 1, 1.00, 0.98, 1.23);
    trader.closeConnection();
    int j = 3;
//    Order parent = new Order();
//    Order stopLoss = new Order();
//    Order profitStop = new Order();
//    parent.m_action = "BUY";
//    parent.m_outsideRth = true;
//    parent.m_totalQuantity = 1;
//    parent.m_orderType = "LMT";
//    parent.m_lmtPrice = 1.00;
//    parent.m_transmit = false;
//    parent.m_orderId = PtsTrader.getAndIncrementNextOrderID();
//    trader.placeOrder(parent.m_orderId, contract, parent);
//
//    stopLoss.m_action = "SELL";
//    stopLoss.m_totalQuantity = 1;
//    stopLoss.m_orderType = "STP";
//    stopLoss.m_outsideRth = true;
//    stopLoss.m_auxPrice = 0.98;
//    stopLoss.m_orderId = PtsTrader.getAndIncrementNextOrderID();
//    stopLoss.m_parentId = parent.m_orderId;
//    stopLoss.m_ocaGroup = "test1";
//    stopLoss.m_tif = "GTC";
//    stopLoss.m_transmit = false;
//    trader.placeOrder(stopLoss.m_orderId, contract, stopLoss);
//
//    profitStop.m_action = "SELL";
//    profitStop.m_totalQuantity = 1;
//    profitStop.m_orderType = "LMT";
//    profitStop.m_outsideRth = true;
//    profitStop.m_lmtPrice = 1.23;
//    profitStop.m_orderId = PtsTrader.getAndIncrementNextOrderID();
//    profitStop.m_parentId = parent.m_orderId;
//    profitStop.m_ocaGroup = "test1";
//    profitStop.m_tif = "GTC";
//    profitStop.m_transmit = true;
//
//    trader.placeOrder(profitStop.m_orderId, contract, profitStop);
  }
}
