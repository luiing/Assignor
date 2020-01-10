package com.uis.conn;

import android.os.SystemClock;

import com.uis.assignor.couple.Couple;
import com.uis.assignor.couple.SimpleDecouple;
import com.uis.decouple.BindCouple;

@BindCouple("Test")
public class CoupleTest extends SimpleDecouple {
    @Override
    public void onCallback(Couple.Params param) {
        SystemClock.sleep(5000);
        Couple.newResult(param.id).success().addParam("name",param.toString()).build();
    }

    @Override
    public Couple.Result onCall(Couple.Params param) {

        return Couple.newResult(param.id).success().addParam("name",param.toString()).build();
    }
}
