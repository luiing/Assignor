package com.uis.assignor.call;

public interface ICall{
    void onCallback(Call.Params param);

    Call.Result onCall(Call.Params param);
}
