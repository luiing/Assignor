package com.uis.assignor.couple;

import org.json.JSONObject;

import java.util.UUID;

public class CoupleParam {
    public String coupleName;
    public String coupleAction;
    public int id = UUID.randomUUID().version();
    private JSONObject data;

    private CoupleParam(){

    }

    public JSONObject getData(){
        return data == null ? new JSONObject() : data;
    }

    public static ParamBuilder createParam(String coupleName){
        return new ParamBuilder(coupleName);
    }

    public static class ParamBuilder{
        CoupleParam param = new CoupleParam();

        ParamBuilder(String name) {
            param.coupleName = name;
        }

        public ParamBuilder setAction(String action){
            param.coupleAction = action;
            return this;
        }

        public ParamBuilder setParam(JSONObject param){
            this.param.data = param;
            return this;
        }

        public ParamBuilder addParam(String key,Object value){
            try {
                if(param.data == null) param.data = new JSONObject();
                param.data.put(key, value);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return this;
        }

        public CoupleParam build(){
            return param;
        }
    }
}
