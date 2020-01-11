package com.uis.conn;

import android.os.SystemClock;

import com.uis.assignor.call.Call;
import com.uis.assignor.call.SimpleCall;
import com.uis.decouple.BindCouple;

@BindCouple("Test")
public class CoupleTest extends SimpleCall {
    @Override
    public void onCallback(Call.Params param) {
        SystemClock.sleep(2000);
        Call.newResult(param.id).success().addParam("name",param.toString()).build();
    }

    @Override
    public Call.Result onCall(Call.Params param) {

        return Call.newResult(param.id).success().addParam("name",param.toString()).build();
    }
}
