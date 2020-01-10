package com.uis.assignor.couple;

import com.uis.decouple.ICouple;

public interface IDecouple extends ICouple {
    void onCallback(Couple.Params param);

    Couple.Result onCall(Couple.Params param);
}
