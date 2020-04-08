# Assignor 数据流架构+组件化

### 新增支持注解组件化功能
组件化原理介绍：具体的用法参看demo

1.注解处理器：通过注解BindCall声明组件名称，注解处理器call-processor在编译前匹配到被注解的类，

然后生成一个java源文件：里面有无参被注解的类的对象

2.实现组件：实现接口com.uis.assignor.call.ICall,同步和异步功能分开清晰逻辑

3.调用组件：call-annotation中的方法BindCallUtils.getCallValue(),获取到被注解的类的对象，剩下的就是调用了

### 介绍
BodyModel,BodyData提供数据流操作，支持缓存，支持自动释放资源，只有UI可见才更新

BodyData数据调度隶属于BodyModel,每个Activity最多有一个同名BodyModel

IAsyncWork提供异步线程任务，且支持数量设置和结果回调

ISyncWork提供同步线程组任务，且支持数量设置和结果回调

Worker.mainExecute{} 主线程运行（内部异常处理）

Worker.ioExecute{} 子线程运行（内部异常处理）

# Use
    implementation 'com.uis:assignor:1.0.1'
    
# Personal Maven
    maven { url 'https://dl.bintray.com/sweet/maven'}    

# 3D旋转【切换Activity或者View旋转效果】
    implementation 'com.uis:rotate3d:0.0.1'

### Model Data用法

```
    //在Application先调用，初始化生命周期监听（线程安全）
    Assignor.init(application)
    
    class DemoData :BodyModel() {
        val stringBody = BodyData<String>()
        val intBody = BodyData<Int>()
    }
    
    /** 创建BodyModel,重复调用该方法会使用缓存，每个Activity只有一个同名BodyModel
        Assignor.of()获取到一个BodyStore,接着通过无参构造函数获取DemoData
        DemoData创建后会自动查找BodyData相关变量，把生命周期关联到BodyData（无须手动关联）
        Activity生命周期传递:Assignor->BodyStor->DemoMData->BodyData
        Assignor.of(activity)此处使用的activit.hashCode(),不会内存泄露
    */
    val data = Assignor.of<DemoData>(this){}
    
    /** 监听数据，只有当Activity在OnResumed后OnPaused前会回调
        如果不在该范围，下次重新进入范围继续回调
    */
    data.stringBody.observer{data->
        ALog.e("string data= $data")
    }
    //只监听一次，之后不会在回调
    data.stringBody.observerOnce{data->
        ALog.e("string data= $data")
    }
    
    //子线程设置value和直接设置value（最后都在主线程回调）
    Worker.ioExecute {
        data.stringBody.setValue("action clicked test1")
    }
    data.stringBody.setValue("action clicked test1")
    
    //获取最新缓存（没有值为NULL）
    ALog.e("cache value: "+test.stringBody.getValue())
```

#### 线程组
```
//同步线程组,lambda写法
    fun syncCall(){
        Worker.syncWork().sync {
                    ALog.e(it.toString())
                    "s1"
                }
                .sync {
                    ALog.e(it.toString())
                    "s2"
                }
                .sync {
                    ALog.e(it.toString())
                    "s3"
                }
                .sync {
                    ALog.e(it.toString())
                    "s4"
                }
                .done {
                    ALog.e("done:"+it.toString())
                }
    }
    
//异步线程组，lambda写法
    fun asyncCall(){
        Worker.asyncWork().async {
                    ALog.e("async 1")
                    return@async AsyncResult("001","1")
                }
                .async {
                    ALog.e("async 2")
                    return@async AsyncResult("002","2")
                }
                .async {
                    ALog.e("async 3")
                    return@async AsyncResult("003","3")
                }
                .async {
                    ALog.e("async 4")
                    return@async AsyncResult("004","4")
                }.done {
                    ALog.e("done:"+it.toString())
                }
    }    
```


# 组件化框架使用

    1.加入注解处理器:kapt支持kotlin代码注解处理器扫描，也支持java代码
        apply plugin: 'kotlin-kapt'
        dependencies {
            implementation 'com.uis:assignor:1.0.0'
            kapt 'com.uis:call-processor:0.0.1'
        }
    2.组件实现
        @BindCall("Test")
        public class CoupleTest extends SimpleCall {
            @Override
            public void onCallback(Call.Params param) {//实现异步方法
                SystemClock.sleep(2000);
                Call.newResult(param.id).success().addParam("name",param.toString()).build();
            }

           @Override
            public Call.Result onCall(Call.Params param) {//实现同步方法

                return Call.newResult(param.id).success().addParam("name",param.toString()).build();
            }
        }
    3.调用
        //同步调用
        Call.Result result = Call.newParams("Test")
                    .setAction("a")
                    .addParam("key","111")
                    .call();
        ALog.e(result.toString());
        //异步调用
            Call.newParams("Test")
                    .setAction("b")
                    .addParam("key","222").call(new IResult() {
                @Override
                public void onResult(Call.Result result) {
                    ALog.e(result.toString());
                }
            });


# Version
Version|Descipt|Fixed|Time
----|----|----|----
0.0.1|初始版本| |2019/6
0.0.2|增加BodyData.asyncValue()方法|缓存混淆|2019/6
0.1.0|BodyModel支持懒加载||2019/7
0.2.0|增加BodyData.observerOnce,范型新用法||2019/8
0.2.4|增加BodyModel常驻方法,写文件缓存在子线程,兼容java和kotlin写法||2019/8
0.2.5|支持activity存在多个同名BodyModel,需指定key||2019/9
0.3.0|新增observerStartNow只监听新的回调，加入androidx||2019/12
1.0.0|新增注解组件化功能||2020/1
1.0.1|线程组优化,fixed startOnceStartNow||2020/3

# License
Copyright [2020] uis
        
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
        
     http://www.apache.org/licenses/LICENSE-2.0
        
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.