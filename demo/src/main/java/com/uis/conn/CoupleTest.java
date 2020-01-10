package com.uis.conn;

import android.os.SystemClock;

import com.uis.assignor.couple.Couple;
import com.uis.assignor.couple.SimpleDecouple;
import com.uis.decouple.BindCouple;

@BindCouple("Test")
public class CoupleTest extends SimpleDecouple {
    @Override
    public void onDecouple(Couple.Params param) {
        SystemClock.sleep(2000);
        Couple.newResult(param.id).success().addParam("name","Test onDecouple() call").notifyResult();
    }

    @Override
    public Couple.Result onCall(Couple.Params param) {
        return Couple.newResult(param.id).success().addParam("name","Test onCall() call").build();
    }
}
