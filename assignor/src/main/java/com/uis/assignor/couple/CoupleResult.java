package com.uis.assignor.couple;

import org.json.JSONObject;

public class CoupleResult {
    private JSONObject data;
    public int id;
    public String errorTxt = "";
    public int errorCode = 0;
    public boolean success;

    private CoupleResult(){

    }

    public JSONObject getData(){
        return data == null ? new JSONObject() : data;
    }

    public static ResultBuilder createResult(int cid){
        return new ResultBuilder( cid);
    }

    public static class ResultBuilder{
        CoupleResult result = new CoupleResult();

        ResultBuilder(int cid) {
            this.result.id = cid;
        }

        public ResultBuilder success(){
            this.result.success = true;
            return this;
        }

        public ResultBuilder error(int code,String txt){
            this.result.errorCode = code;
            this.result.errorTxt = txt;
            return this;
        }

        public ResultBuilder setParam(JSONObject param){
            this.result.data = param;
            return this;
        }

        public ResultBuilder addParam(String key,Object value){
            try {
                if(this.result.data == null) this.result.data = new JSONObject();
                this.result.data.put(key, value);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return this;
        }

        public CoupleResult build(){
            return result;
        }
    }
}
