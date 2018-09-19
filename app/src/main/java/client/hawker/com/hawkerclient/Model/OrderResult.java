package client.hawker.com.hawkerclient.Model;

public class OrderResult {
    public int OrderId;
    public String OrderDate;
    public int OrderStatus;
    public float Orderprice;
    public String OrderDetail,OrderComment,UserPhone;

    public OrderResult() {
    }

    public OrderResult(int orderId, String orderDate, int orderStatus, float orderprice, String orderDetail, String orderComment, String userPhone) {
        OrderId = orderId;
        OrderDate = orderDate;
        OrderStatus = orderStatus;
        Orderprice = orderprice;
        OrderDetail = orderDetail;
        OrderComment = orderComment;
        UserPhone = userPhone;
    }

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public int getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        OrderStatus = orderStatus;
    }

    public float getOrderprice() {
        return Orderprice;
    }

    public void setOrderprice(float orderprice) {
        Orderprice = orderprice;
    }

    public String getOrderDetail() {
        return OrderDetail;
    }

    public void setOrderDetail(String orderDetail) {
        OrderDetail = orderDetail;
    }

    public String getOrderComment() {
        return OrderComment;
    }

    public void setOrderComment(String orderComment) {
        OrderComment = orderComment;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }
}
