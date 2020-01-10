package com.uis.conn;

import com.uis.assignor.couple.CoupleParam;
import com.uis.assignor.couple.SimpleDecouple;
import com.uis.decouple.BindCouple;

@BindCouple("Test")
public class CoupleTest extends SimpleDecouple {
    @Override
    public void onDecouple(CoupleParam param) {

    }
}
