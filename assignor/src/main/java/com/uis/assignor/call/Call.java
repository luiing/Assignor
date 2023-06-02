package com.uis.assignor.call;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;

import com.uis.assignor.works.Worker;
import com.uis.call.BindCallUtils;

import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Unit;
import kotlin.jvm.Volatile;
import kotlin.jvm.functions.Function0;

public final class Call {

    private static ConcurrentHashMap<String, WeakReference<ICall>> sCall = new ConcurrentHashMap<>();
    private static ArrayMap<Integer, IResult> sResult = new ArrayMap<>();
    private static @Volatile int sCounter = 1;

    /**
     * create Call.Params
     * @param callName
     * @return
     */
    public static ParamsBuilder newParams(@NonNull String callName){
        return new ParamsBuilder(callName);
    }

    /**
     * create Call.Result
     * @param cid
     * @return
     */
    public static ResultBuilder newResult(int cid){
        return new ResultBuilder( cid);
    }


    public final static class Result {

        public int id;
        public boolean success;
        public String errorTxt = "";
        public int errorCode = 0;
        private JSONObject data;

        private Result(){

        }

        public JSONObject getData(){
            return data == null ? new JSONObject() : data;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "id=" + id +
                    ", success=" + success +
                    ", errorTxt='" + errorTxt + '\'' +
                    ", errorCode=" + errorCode +
                    ", data=" + getData().toString() +
                    '}';
        }
    }

    public final static class ResultBuilder{
        private Result result = new Result();

        private ResultBuilder(int cid) {
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

        public Result build(){
            final IResult iResult = sResult.remove(result.id);
            if(iResult != null){
                Worker.mainExecute(() -> {
                    iResult.onResult(result);
                    return null;
                },0);
            }
            return result;
        }
    }

    public final static class Params {

        public String callName;
        public String callAction = "";
        public Context context;
        public int id = sCounter++;
        private JSONObject data;

        private Params(){

        }

        public JSONObject getData(){
            return data == null ? new JSONObject() : data;
        }

        public <T> T getItem(String key){
            return (T)getData().opt(key);
        }

        public String getStringItem(String key){
            return getData().optString(key,"");
        }

        public boolean getBooleanItem(String key){
            return getData().optBoolean(key,false);
        }

        public int getIntItem(String key){
            return getData().optInt(key,0);
        }

        @Override
        public String toString() {
            return "Params{" +
                    "callName='" + callName + '\'' +
                    ", callAction='" + callAction + '\'' +
                    ", id=" + id +
                    ", data=" + getData().toString() +
                    '}';
        }
    }

    public final static class ParamsBuilder{
        private Params param = new Params();

        private ParamsBuilder(@NonNull String name) {
            param.callName = name;
        }

        public ParamsBuilder setAction(String action){
            param.callAction = action;
            return this;
        }

        public ParamsBuilder setContext(Context context){
            param.context = context;
            return this;
        }

        public ParamsBuilder setParam(JSONObject param){
            this.param.data = param;
            return this;
        }

        public ParamsBuilder addParam(String key, Object value){
            try {
                if(param.data == null) param.data = new JSONObject();
                param.data.put(key, value);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return this;
        }

        public Result call(){
            ICall call = getCall();
            if(call != null){
                return call.onCall(param);
            }
            return newResult(param.id).error(404,"Not found "+param.callName).build();
        }

        public void call(IResult result){
            final ICall call = getCall();
            sResult.put(param.id,result);
            if(call != null){
                Worker.ioExecute(() -> {
                    call.onCallback(param);
                    return null;
                });
            }else{
                newResult(param.id).error(404,"Not found "+param.callName).build();
            }
        }

        private ICall getCall(){
            ICall call = null;
            if(!TextUtils.isEmpty(param.callName)) {
                WeakReference<ICall> ref = sCall.get(param.callName);
                if (ref != null) {
                    call = ref.get();
                }
                if (call == null) {
                    Object value = BindCallUtils.getCallValue(param.callName);
                    if (value != null) {
                        call = (ICall) value;
                        sCall.put(param.callName, new WeakReference<>(call));
                    }
                }
            }
            return call;
        }
    }
}
