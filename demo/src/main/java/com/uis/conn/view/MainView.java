package com.uis.conn.view;

public interface MainView {

    void cacheA(TestA a);
    void displayA(TestA a);

    void cacheC(TestA a);
    void displayC(TestA a);

    void cacheB(TestB b);
    void displayB(TestB b);

    void displayMultith(String content);
}
