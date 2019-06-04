package com.uis.conn.view;

import java.io.Serializable;

/**
 * @author uis on 2018/4/20.
 */
public class TestA implements Serializable{
    public String name;
    public int age;
    public String addr;
    public long mills = System.currentTimeMillis();

    public TestA(){}

    public TestA(String name, int age, String addr) {
        this.name = name;
        this.age = age;
        this.addr = addr;
    }

    @Override
    public String toString(){
        return "name="+name+"\n"+
                "age="+age+"\n"+
                "addr="+addr+"\n"+
                "mills="+mills;
    }
}
