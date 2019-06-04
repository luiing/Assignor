# Connector 观察者根据类型进行精准通知
    1.观察者模式根据泛型nofity同一类型observer，支持绑定activity自动回收
    
    2.线程组执行回调（子线程中运行）
    
    3.支持内存，文件缓存，支持缓存时间设置
    
    4.提供了动态代理默认方法
    
# Use
    compile 'com.uis:lib-connector:1.1.0'

#### 精准观察者（支持内存和文件缓存）
    ConnObserverOwner owner = new ConnObserverOwner();
    
    //绑定activity生命周期，自动反注册
    owner.attachActivity(activity);
    
    //注册监听器,当 cacheKey或者范性类型 匹配时回调（cacheKey优先级高于类型匹配）
    //new SimpleObserver<ArrayList<String>>()直接监听对象含有范型不支持，可以把范型放入到新类中
    owner.registerObserver(new SimpleObserver<String>() {
        public String getCacheKey() {
            return "actionb";
        }
        
        @Override
        public void onResponse(Response<String> resp) {
            
        }
        
        @Override
        public void onCacheResponse(Response<TestA> resp) {
                
        }
    });
    
    //匹配缓存，回调接听器中的onCacheResponse方法
    owner.notifyCache("actionb",15000);//结果不为null,回调onCacheResponse生效
    
    //发送结果，回调接听器中的onResponse方法
    Response resp = Response.newBuilder("success")
          .setCacheKey("actionb")//to support cache,setCache()回调onCacheResponse生效
          .build();
    owner.notifyResponse(resp);

#### 线程组调用（回调线程安全）
        MultithOwner multithOwner = new MultithOwner(3,new MultithCallback() {//指定线程数量
            @Override
            public void onMultith(MultithResponse response) {//全部执行完回调
                Clog.print(response.result.toString());
            }

            @Override
            public void onProgress(int position, String key, Object value) {//执行进度
                Clog.print("position="+position+",key="+key+",value="+value);
            }
        });
        ConnPlant.submit(new Runnable() {
            @Override
            public void run() {
                multithOwner.setResult("k100","value100");//发送单个线程执行完结果
            }
        },new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1000);
                multithOwner.setResult("k200","value200");
            }
        },new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
                multithOwner.setResult("k300","value300");
            }
        });

# Version

    v1.0.0 优化资源回收，多线程回调优化
    v0.0.5 解决application反射出null的bug，file缓存采用FileChannel mapBuffer读写加速
    
    v0.1.2 优化缓存方式,增加缓存分类prefix，增加Response建造者模式
                file缓存采用读写锁，废除FileChannel方式
    
    v0.1.3 缓存目录优化，缓存支持内存清除和null设置，activity支持绑定多个ConnObserverOWner
    
    v0.2.0 文件缓存废除序列化，自动将对象转json，取缓存的结果也为json
                registerObserver时根据getCacheKey()读取缓存并回调
                设置cacheKey后：结果回调只匹配key
                不设置cacheKey：只匹配Type
    v0.3.0 fixed Gson格式html字符解析异常，attachFragment在ViewPager异常,Kotlin中范型Type匹配失败
    v0.5.0 fixed String存储Gson读取出现Html格式问题，保存的Entity混淆需要keep fields
    v1.0.0 环境升级，内存回收优化
    v1.1.0 回调方法区分缓存，解决4.4系统Gson().newBuilder()异常问题，结果为null不回调;
        废除Response.Builder setParadigm(Type type)等方法;
        动态代理ProxyInvocationHandler增加beforeInvoke和afterInvoke方法

# License
           Copyright [2018] uis
        
           Licensed under the Apache License, Version 2.0 (the "License");
           you may not use this file except in compliance with the License.
           You may obtain a copy of the License at
        
               http://www.apache.org/licenses/LICENSE-2.0
        
           Unless required by applicable law or agreed to in writing, software
           distributed under the License is distributed on an "AS IS" BASIS,
           WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
           See the License for the specific language governing permissions and
           limitations under the License.