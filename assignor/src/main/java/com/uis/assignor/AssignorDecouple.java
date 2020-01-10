package com.uis.assignor;

import com.uis.assignor.couple.Couple;
import com.uis.assignor.couple.CoupleParam;
import com.uis.assignor.couple.CoupleResult;
import com.uis.assignor.couple.SimpleDecouple;
import com.uis.decouple.BindCouple;

@BindCouple("Assignor")
public class AssignorDecouple extends SimpleDecouple {
    @Override
    public void onDecouple(CoupleParam param) {
        Couple.result(CoupleResult.createResult(param.id).success().addParam("name","assignor onDecouple").build());
    }

    @Override
    public CoupleResult onCall(CoupleParam param) {
        return CoupleResult.createResult(param.id).success().addParam("name","assignor onCall").build();
    }
}
