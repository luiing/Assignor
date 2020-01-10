package com.uis.conn;

import android.os.SystemClock;

import com.uis.assignor.couple.Couple;
import com.uis.assignor.couple.CoupleParam;
import com.uis.assignor.couple.CoupleResult;
import com.uis.assignor.couple.SimpleDecouple;
import com.uis.decouple.BindCouple;

@BindCouple("Test")
public class CoupleTest extends SimpleDecouple {
    @Override
    public void onDecouple(CoupleParam param) {
        SystemClock.sleep(5000);
        Couple.result(CoupleResult.createResult(param.id).success().addParam("name","Test onDecouple").build());
    }

    @Override
    public CoupleResult onCall(CoupleParam param) {
        return CoupleResult.createResult(param.id).success().addParam("name","Test onCall").build();
    }
}
