# Assignor 数据流架构(委托者)

BodyModel,BodyData提供数据流操作，支持缓存，支持自动释放资源，只有UI可见才更新

BodyData数据调度隶属于BodyModel,每个Activity最多有一个同名BodyModel

IAsyncWork提供异步线程任务，且支持数量设置和结果回调

ISyncWork提供同步线程组任务，且支持数量设置和结果回调

Worker.mainExecute{} 主线程运行（内部异常处理）

Worker.ioExecute{} 子线程运行（内部异常处理）

# Use
    implementation 'com.uis:assignor:0.3.0'
    
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




# Version
Version|Descipt|Fixed|Time
----|----|----|----
0.0.1-CR1|预览版本| |2019/6
0.0.2|增加BodyData.asyncValue()方法|缓存混淆|2019/6
0.1.0|BodyModel支持懒加载||2019/7
0.2.0|增加BodyData.observerOnce,范型新用法||2019/8
0.2.4|增加BodyModel常驻方法,写文件缓存在子线程,兼容java和kotlin写法||2019/8
0.2.5|支持activity存在多个同名BodyModel,需指定key||2019/9
0.3.0|新增observerStartNow只监听新的回调，加入androidx||2019/12



# License
           Copyright [2019] uis
        
           Licensed under the Apache License, Version 2.0 (the "License");
           you may not use this file except in compliance with the License.
           You may obtain a copy of the License at
        
               http://www.apache.org/licenses/LICENSE-2.0
        
           Unless required by applicable law or agreed to in writing, software
           distributed under the License is distributed on an "AS IS" BASIS,
           WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
           See the License for the specific language governing permissions and
           limitations under the License.