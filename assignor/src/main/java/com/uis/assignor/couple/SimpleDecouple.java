package com.uis.assignor.couple;

public class SimpleDecouple implements IDecouple {
    @Override
    public void onCallback(Couple.Params param) {
        Couple.newResult(param.id).error(500,"IDecouple onCallback() didn't override").build();
    }

    @Override
    public Couple.Result onCall(Couple.Params param) {
        return Couple.newResult(param.id).error(500,"IDecouple onCall() didn't override").build();
    }
}
