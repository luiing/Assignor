package com.uis.assignor.couple;

public class SimpleDecouple implements IDecouple {
    @Override
    public void onDecouple(CoupleParam param) {

    }

    @Override
    public CoupleResult onCall(CoupleParam param) {
        return CoupleResult.createResult(param.id).error(500,"onCall did't override").build();
    }
}
