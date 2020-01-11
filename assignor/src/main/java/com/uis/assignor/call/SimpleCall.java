package com.uis.assignor.call;

public class SimpleCall implements ICall {
    @Override
    public void onCallback(Call.Params param) {
        Call.newResult(param.id).error(500,"IDecouple onCallback() didn't override").build();
    }

    @Override
    public Call.Result onCall(Call.Params param) {
        return Call.newResult(param.id).error(500,"IDecouple onCall() didn't override").build();
    }
}
