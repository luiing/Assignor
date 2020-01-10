/*
 * Copyright (c) 2020 by uis
 * Author: uis
 * Github: https://github.com/luiing
 */

package com.uis.conn

import com.uis.assignor.couple.Couple
import com.uis.assignor.couple.SimpleDecouple
import com.uis.decouple.BindCouple

@BindCouple("Assignor")
class AssignorDecouple : SimpleDecouple() {
    override fun onCallback(param: Couple.Params) {
        Couple.newResult(param.id).success().build()
    }

    override fun onCall(param: Couple.Params): Couple.Result {
        return Couple.newResult(param.id).success().build()
    }
}
