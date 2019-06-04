package com.uis.conn.observer;

import android.app.Activity;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;


import com.uis.conn.view.MainView;
import com.uis.conn.view.TestA;
import com.uis.conn.view.TestB;
import com.uis.connector.cache.Cache;
import com.uis.connector.cache.CacheImpl;
import com.uis.connector.comm.Clog;
import com.uis.connector.comm.ConnPlant;
import com.uis.connector.multith.MultithCallback;
import com.uis.connector.multith.MultithOwner;
import com.uis.connector.multith.MultithResponse;
import com.uis.connector.workshop.ConnObserver;
import com.uis.connector.workshop.ConnObserverOwner;
import com.uis.connector.workshop.Response;
import com.uis.connector.workshop.SimpleObserver;

import java.io.File;
import java.util.ArrayList;

public class MainConn implements MainAction {
    MainView view;
    final ConnObserverOwner owner;
    final MultithOwner multithOwner;

    File parent = ConnPlant.app().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);

    Cache cacheTest;
    String testValue = String.valueOf(System.currentTimeMillis());
    Cache cache;

    ConnObserver obsStr = new SimpleObserver<String>() {
        @Override
        public void onResponse(Response<String> resp) {
            view.displayMultith(resp.result);
        }
    };

    ConnObserver obsA = new SimpleObserver<TestA>() {
        @Override
        public void onResponse(Response<TestA> resp) {
            view.displayA(resp.result);
        }

        @Override
        public void onCacheResponse(Response<TestA> resp) {
            view.cacheA(resp.result);
        }

        @Override
        public String getCacheKey() {
            return "actiona";
        }
    };

    @Override
    public void attachActivity(Activity act){
        owner.attachActivity(act);
    }

    public MainConn(final MainView view) {
        this.view = view;
        parent.mkdirs();
        owner = new ConnObserverOwner();
        owner.registerObserver(obsA);
        owner.registerObserver(new SimpleObserver<TestA>() {
            @Override
            public void onResponse(Response<TestA> resp) {
                view.displayC(resp.result);
            }

            @Override
            public void onCacheResponse(Response<TestA> resp) {
                view.cacheC(resp.result);
            }
        });
        //actionCacheA();

        cache = ConnPlant.cache();
        cacheTest = new CacheImpl(parent);


        multithOwner = new MultithOwner(3,new MultithCallback() {
            @Override
            public void onMultith(MultithResponse response) {
                //SystemClock.sleep(1000);
                Clog.print((Looper.getMainLooper() == Looper.myLooper())+":OnMultith...="+response.result.toString());
                Response resp = Response.newBuilder(response.result.toString()).build();
                owner.notifyResponse(resp);
            }

            @Override
            public void onProgress(int position, String key, Object value) {
                Clog.print("position="+position+",key="+key+",value="+value);
            }
        });

        owner.registerObserver(obsStr);


        owner.registerObserver(new SimpleObserver<TB>(){
            @Override
            public void onResponse(Response<TB> resp) {
                view.displayB(resp.result.array.get(0));
            }

            @Override
            public void onCacheResponse(Response<TB> resp) {
                view.cacheB(resp.result.array.get(0));
            }

            @Override
            public String getCacheKey() {
                return "actionb";
            }
        });
    }
    static class TB{
        ArrayList<TestB> array;

        public TB(ArrayList<TestB> array) {
            this.array = array;
        }
    }
    static String data = "早期经历" + "秦始皇于秦昭王四十八年正月（公元前259年1月27日）出生， [10-12]  出生地在当时的邯郸廓城（大北城）温明殿遗址和丛台以南，在今城内中街以东，丛台西南的朱家巷一带。是秦庄襄王的中子，商朝重臣恶来的第35世孙，嬴姓赵氏，名政。 [13]  [3]  [14] ";

    @Override
    public void actionA() {
        ConnPlant.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    //SystemClock.sleep(100);
                    owner.notifyResponse(Response.newBuilder(new TestA("lady",20,"ShangHai City"))
                            .setCacheKey("actiona")
                            .build());
                    cache.writeFile("app", "I promise it's true.");
                }catch (Throwable ex){
                    ex.printStackTrace();
                }
            }
        });
        Clog.print("result:"+cache.readFile("app"));
        ConnPlant.submit(new Runnable() {
            @Override
            public void run() {
                //SystemClock.sleep(500);
                Clog.print("result1:"+cache.readFile("app"));
            }
        });
        File file = new File(parent,"qin.txt");
        ConnPlant.cache().saveFile(false,"I can't do it,I promise.\n".getBytes(),file);


        cacheTest.writeFile(testValue,testValue);
    }

    private int sort(String a,String b){
        return a.compareTo(b);
    }

    @Override
    public void actionCacheA() {
        owner.notifyCache("actiona",15000);
    }

    boolean ab;
    @Override
    public void actionB() {
        ArrayList<TestB> r = new ArrayList<TestB>(){};
        r.add(new TestB("10000",22,"Play PingPang"));
        owner.notifyResponse(Response.newBuilder(new TB(r))
                .setCacheKey("actionb")
                .build());

        ConnPlant.submit(new Runnable() {
            @Override
            public void run() {
                //SystemClock.sleep(100);
                ArrayList<TestB> r = new ArrayList<TestB>(){};
                r.add(new TestB("10001",22,"Play PingPang"));
                owner.notifyResponse(Response.newBuilder(new TB(r))
                        .setCacheKey("actionb")
                        .build());
            }
        });
        //test file multiThread

        ab = !ab;
        ConnPlant.submit(new Runnable() {
            @Override
            public void run() {
                cache.writeFile("k1",(ab ? "---data---" : "") + data);
            }
         });
        ConnPlant.submit(new Runnable() {
            @Override
            public void run() {

                Log.e("xx","res1=" + cache.readFile("k1", 0));
            }
        },new Runnable() {
            @Override
            public void run() {

                Log.e("xx","res5=" + cache.readFile("k1", 0));
            }
        });
        Clog.print("testValue:"+cacheTest.readFile(testValue));
    }

    @Override
    public void actionCacheB() {
        owner.notifyCache("actionb",15000);
        //cacheTest.clearAll("");
    }

    @Override
    public void actionC() {
        owner.notifyResponse(Response.newBuilder(new TestA("lady",20,"ShangHai City"))
                .build());
    }

    @Override
    public void actionMultith() {

        ConnPlant.submit(new Runnable() {
            @Override
            public void run() {
                multithOwner.setResult("k100","value100");
            }
        },new Runnable() {
            @Override
            public void run() {
                //SystemClock.sleep(1000);
                multithOwner.setResult("k200","value200");
            }
        },new Runnable() {
            @Override
            public void run() {
                //SystemClock.sleep(2000);
                multithOwner.setResult("k300","value300");
            }
        });

    }
}
