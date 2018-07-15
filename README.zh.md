[Click me switch to English version](https://github.com/bluesky466/HandyHTTPD/blob/master/README.md)

# HandyHttpd

HandyHttpd是一个用java写的简单易用的微型http服务器,你能用它在局域网中提供一些简单的HTTP服务.

这个README文档列出了一些主要的功能,你也可以在[单元测试](https://github.com/bluesky466/HandyHTTPD/blob/master/app/src/test/java/me/linjw/handyhttpd/samples/SimpleService.java)中查看它的所有功能.

### Hello world Demo

下面是一个Hello World 安卓Demo.你在局域网中访问http://ip:8888/hello?name=tom",然后接收到"Hello tom"的返回:

```
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final int PORT = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            HandyHttpd.Server server = new HandyHttpd.ServerBuilder()
                    .loadService(this)
                    .create();
            server.start(PORT);
        } catch (Exception e) {
            Log.e(TAG, "create HandyHttpdServer err", e);
        }
    }

    @Path("/hello")
    public String sayHello(String name) {
        return "Hello " + name;
    }
}
```

"loadService" 方法会扫描传入对象所有被@Path(uri)注解的方法,将它们注册给处理器. 这些方法会在注册的uri被访问的时候被调用.


### 参数

HandyHttpd 会将通过HTTP协议传来的参数转换成@Path方法的参数类型,然后传给@Path方法:

```
@Path("foo")
public void foo(int intParam, String strParam, File fileParam, Boolean booleanParam) {
}
```

调用上面方法的uri长这个yangz: "http://ip:port/foo?intParam=123&strParam=abc&booleanParam=true", fileParam 参数就是上传的文件.

所有支持的参数类型:

- boolean
- byte
- char
- double
- float
- int
- long
- short
- Boolean
- Byte
- Character
- Double
- Float
- Integer
- Long
- Short
- String
- File
- HttpRequest
- Cookie
- Map<String,Cookie>
- Map<String,String>
- Map<String,File>

__值得一提的是 : 你可以用 Map\<String,String\> 去拿到所有的HTTP参数, 用 Map\<String,File\> 去拿到所有的上传文件, 用Map\<String,Cookie\>去拿到所有的Cookie.__

### 返回值

@Path方法的返回值会被转换成http响应.

所有支持的返回值类型如下:

- File
- InputStream
- String
- HttpResponse
- void

### 注解

HandyHttpd用java注解去将事情变得简单。如上面的例子中,我们用@Path注解去注册方法。其实HandyHttpd还提供了许多其他的注解.

#### HTTP方法

@Path 会在接收到http请求的时候被调用,默认是无论GET请求或POST请求都会调用的.不过你也能用@Get或者@Post注解去限制它:

```
@Get
@Path("getFunc")
public void getFunc() {
}

@Post
@Path("postFunc")
public void postFunc() {
}
```

#### 参数名

HandyHttpd用变量名作为key去找到对应的http请求参数,不用你也可以使用@Param(key)去设置key值:

```
@Path("/hello")
public String sayHello(@Param("name") String arg) {
    return "Hello " + arg;
}
```

如上面的例子,HTTP参数的名字依然是"name",所以url仍然是长这个样子的: "http://ip:port/hello?name=XXXXX"

#### 请求头

你也可以用 @Header 去获取HTTP请求头的数据:

```
@Path("/testParmHeader")
public void testParmHeader(@Header() String host,
                           @Header("http-client-ip") String clientIp,
                           @Header Map<String, String> headers) {
    mTarget.testParmHeader(host, clientIp, headers);
}
```


### 许可

该项目使用 [WTFPL LICENSE](http://www.wtfpl.net/).我喜欢这个许可,你他妈可以想干啥就干啥.
