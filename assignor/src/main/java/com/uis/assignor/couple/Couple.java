package com.uis.assignor.couple;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import com.uis.assignor.utils.ALog;
import com.uis.assignor.works.Worker;
import com.uis.decouple.Generate;
import org.json.JSONObject;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Unit;
import kotlin.jvm.Volatile;
import kotlin.jvm.functions.Function0;

public final class Couple {

    private static ConcurrentHashMap<String, WeakReference<IDecouple>> sCouples = new ConcurrentHashMap<>();
    private static ArrayMap<Integer, IResult> sResult = new ArrayMap<>();
    private static @Volatile int sCounter = 1;

    /**
     * create Couple.Params
     * @param coupleName
     * @return
     */
    public static ParamsBuilder newParams(@NonNull String coupleName){
        return new ParamsBuilder(coupleName);
    }

    /**
     * create Couple.Result
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
                Worker.mainExecute(new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        iResult.onResult(result);
                        return null;
                    }
                },0);
            }
            return result;
        }
    }

    public final static class Params {

        public String coupleName;
        public String coupleAction = "";
        public int id = sCounter++;
        private JSONObject data;

        private Params(){

        }

        public JSONObject getData(){
            return data == null ? new JSONObject() : data;
        }

        @Override
        public String toString() {
            return "Params{" +
                    "coupleName='" + coupleName + '\'' +
                    ", coupleAction='" + coupleAction + '\'' +
                    ", id=" + id +
                    ", data=" + getData().toString() +
                    '}';
        }
    }

    public final static class ParamsBuilder{
        private Params param = new Params();

        private ParamsBuilder(@NonNull String name) {
            param.coupleName = name;
        }

        public ParamsBuilder setAction(String action){
            param.coupleAction = action;
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
            IDecouple iDecouple = getDecouple();
            if(iDecouple != null){
                return iDecouple.onCall(param);
            }
            return newResult(param.id).error(404,"Not found "+param.coupleName).build();
        }

        public void call(IResult result){
            final IDecouple iDecouple = getDecouple();
            sResult.put(param.id,result);
            if(iDecouple != null){
                Worker.ioExecute(new Function0<Unit>() {
                    @Override
                    public Unit invoke() {
                        iDecouple.onCallback(param);
                        return null;
                    }
                });
            }else{
                newResult(param.id).error(404,"Not found "+param.coupleName).build();
            }
        }

        private IDecouple getDecouple(){
            IDecouple couple = null;
            if(!TextUtils.isEmpty(param.coupleName)) {
                WeakReference<IDecouple> ref = sCouples.get(param.coupleName);
                if (ref != null) {
                    couple = ref.get();
                }
                if (couple == null) {
                    couple = createDecouple();
                    if (couple != null) {
                        sCouples.put(param.coupleName, new WeakReference<>(couple));
                    }
                }
            }
            return couple;
        }

        private IDecouple createDecouple(){
            String clsName = Generate.generatePkg + "." + param.coupleName+Generate.generate;
            try {
                Class<?> cls = Class.forName(clsName);
                Field field = cls.getField(Generate.generateName);
                field.setAccessible(true);
                Object iDecouple = field.get(cls.newInstance());
                if(iDecouple != null) {
                    ALog.d("Create "+cls.getName()+",coupleName="+param.coupleName);
                    return (IDecouple)iDecouple;
                }
            }catch (Throwable ex){
                ALog.w("------ Not found "+clsName+" ------");
            }
            return null;
        }
    }
}
