package com.uis.assignor.couple;

import com.uis.decouple.ICouple;

public interface IDecouple extends ICouple {
    void onDecouple(CoupleParam param);
    CoupleResult onCall(CoupleParam param);
}
