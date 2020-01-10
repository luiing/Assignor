package com.uis.assignor.couple;

public class SimpleDecouple implements IDecouple {
    @Override
    public void onDecouple(Couple.Params param) {

    }

    @Override
    public Couple.Result onCall(Couple.Params param) {
        return Couple.newResult(param.id).error(500,"IDecouple onCall() did't override").build();
    }
}
