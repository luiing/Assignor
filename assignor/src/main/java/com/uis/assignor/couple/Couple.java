package com.uis.assignor.couple;

import androidx.collection.ArrayMap;

import com.uis.assignor.works.Worker;
import com.uis.decouple.Generate;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public final class Couple {

    private static ConcurrentHashMap<String, WeakReference<IDecouple>> sCouples = new ConcurrentHashMap<>();
    private static ArrayMap<Integer, IResult> sResult = new ArrayMap<>();

    public static Couple builder(CoupleParam param){
        return new Couple(param);
    }

    public static void result(final CoupleResult result){
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
    }

    private CoupleParam param;
    private Couple(CoupleParam param){
        this.param = param;
    }

    public CoupleResult exec(){
        IDecouple iDecouple = getDecouple(param.coupleName);
        if(iDecouple != null){
            return iDecouple.onCall(param);
        }
        return CoupleResult.createResult(param.id).error(404,"Not found "+param.coupleAction).build();
    }

    public void exec(IResult result){
        final IDecouple iDecouple = getDecouple(param.coupleName);
        if(iDecouple != null){
            sResult.put(param.id,result);
            Worker.ioExecute(new Function0<Unit>() {
                @Override
                public Unit invoke() {
                    iDecouple.onDecouple(param);
                    return null;
                }
            });
        }
    }

    public IDecouple getDecouple(String name){
        WeakReference<IDecouple> ref = sCouples.get("name");
        IDecouple couple = null;
        if(ref != null){
            couple = ref.get();
        }
        if(couple == null){
            couple = createDecouple(name);
            if(couple != null) {
                sCouples.put(name, new WeakReference<>(couple));
            }else{
                System.out.println("Not found OnConnect of "+name);
            }
        }
        return couple;
    }

    private IDecouple createDecouple(String name){
        try {
            Class<?> cls = Class.forName(Generate.generatePkg + "." + name+Generate.generate);
            Field field = cls.getField(Generate.generateName);
            field.setAccessible(true);
            cls = (Class<?>)field.get(cls);
            System.out.println("Create OnConnect of "+name);
            if(cls != null) {
                return (IDecouple) cls.newInstance();
            }
        }catch (Throwable ex){
            ex.printStackTrace();
        }
        return null;
    }
}
