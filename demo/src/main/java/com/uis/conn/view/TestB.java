package com.uis.conn.view;

import java.io.Serializable;

/**
 * @author uis on 2018/4/20.
 */
public class TestB<T>{
    public String id;
    public int age;
    public String hoby;
    public long mills = System.currentTimeMillis();

    public TestB() {
    }

    public TestB(String id, int age, String hoby) {
        this.id = id;
        this.age = age;
        this.hoby = hoby;
    }

    @Override
    public String toString(){
        return "id="+id+"\n"+
                "age="+age+"\n"+
                "addr="+hoby+"\n"+
                "mills="+mills;
    }
}
