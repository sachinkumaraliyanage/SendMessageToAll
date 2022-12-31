package com.techbirdssolutions.sendmasagetoall.been;

public class Contact {
    private String name;
    private String phoneNumber;
    private String numberCheck;
    private int parts;
    private int part;

    public Contact(String name, String phoneNumber,String numberCheck) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.numberCheck=numberCheck;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNumberCheck() {
        return numberCheck;
    }

    public void setNumberCheck(String numberCheck) {
        this.numberCheck = numberCheck;
    }

    public int getParts() {
        return parts;
    }

    public void setParts(int parts) {
        this.parts = parts;
    }

    public int getPart() {
        return part;
    }

    public void setPart(int part) {
        this.part = part;
    }
}
