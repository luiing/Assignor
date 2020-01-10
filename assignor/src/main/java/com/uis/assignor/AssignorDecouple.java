package com.uis.assignor;

import com.uis.assignor.couple.Couple;
import com.uis.assignor.couple.SimpleDecouple;
import com.uis.decouple.BindCouple;

@BindCouple("Assignor")
public class AssignorDecouple extends SimpleDecouple {
    @Override
    public void onDecouple(Couple.Params param) {
        Couple.newResult(param.id).success().addParam("name","assignor onDecouple").notifyResult();
    }

    @Override
    public Couple.Result onCall(Couple.Params param) {
        return Couple.newResult(param.id).success().addParam("name","assignor onCall").build();
    }
}
