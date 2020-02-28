package com.example.thunderbolt_108.serverapp.Model;

import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private String comment;
    private String paymentMethod;
    private String paymentState;
    private String latLng;
    private List<Order> foods;//list of foods

    public Request() {

    }
    public Request(String phone,String name,String address,String total,String status,String comment,String paymentMethod,String paymentState,String latLng,List<Order> foods) {
        this.phone=phone;
        this.name=name;
        this.address=address;
        this.total=total;
        this.foods=foods;
        this.status=status;
        this.comment=comment;
        this.paymentMethod=paymentMethod;
        this.paymentState=paymentState;
        this.latLng=latLng;



    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getLatLng() {
        return latLng;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }


    public String getPaymentState() {
        return paymentState;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getTotal() {
        return total;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
