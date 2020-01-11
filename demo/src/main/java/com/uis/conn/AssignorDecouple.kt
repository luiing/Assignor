/*
 * Copyright (c) 2020 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn

import com.uis.assignor.call.Call
import com.uis.assignor.call.SimpleCall
import com.uis.decouple.BindCouple

@BindCouple("Assignor")
class AssignorDecouple : SimpleCall() {
    override fun onCallback(param: Call.Params) {
        Call.newResult(param.id).success().build()
    }

    override fun onCall(param: Call.Params): Call.Result {
        return Call.newResult(param.id).success().build()
    }
}
