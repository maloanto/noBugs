package models;

public class DepositRequestModel {
    private int id;
    private double balance;

    public DepositRequestModel(int id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    public DepositRequestModel() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
