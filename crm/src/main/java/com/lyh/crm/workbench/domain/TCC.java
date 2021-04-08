package com.lyh.crm.workbench.domain;

public class TCC {
    private Tran t;
    private String customerName;
    private String contactsName;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactsName() {
        return contactsName;
    }

    public void setContactsName(String contactsName) {
        this.contactsName = contactsName;
    }



    public Tran getT() {
        return t;
    }

    public void setT(Tran t) {
        this.t = t;
    }

    @Override
    public String toString() {
        return "TCC{" +
                "t=" + t +
                ", customerName='" + customerName + '\'' +
                ", contactsName='" + contactsName + '\'' +
                '}';
    }
}
