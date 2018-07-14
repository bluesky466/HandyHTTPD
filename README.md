# HandyHttpd

HandyHttpd is a simple and handy http server in java. You can use it to provide some simple service based on HTTP protocol.


### Hello world Demo


For the Hello World android demo, you can access to "http://ip:8888/hello?name=tom" and receive "Hello tom":

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

"loadService" will scan all the @Path(uri) method in the Object to register the uri processor. Http server will call the method when the uri is visited.


### Param

The engine will coerce the http param to the type need:

```
@Path("foo")
public void foo(int intParam, String strParam, File fileParam, Boolean booleanParam) {
}
```

The url is like "http://ip:port/foo?intParam=123&strParam=abc&booleanParam=true", and file can get the upload file.

Supported parameter types:

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
- Map<String,String>
- Map<String,File>

__Note : You can use Map\<String,String\> to get all parameters, and use Map\<String,File\> to get all upload files.__

### Return Value

The return value will be convert to http response.

Supported return value types:

- File
- InputStream
- String
- HttpResponse
- void

### Annotation

HandyHttpd use java annotation to make make things easy. As we know, @Path is to register the method. And there is many other annotation we can use.

#### Method

The @Path method will be called when the uri is visited,no matter GET or POST. But you can limit the http method by @Get or @Post:

```
@Get
@Path("getFunc")
public void getFunc() {
}

@Post
@Path("postFoo")
public void postFunc() {
}
```

#### Param

The engine will find the http parameter by the method parameter name,but you can use @Param(key) to set the param name:

```
@Path("/hello")
public String sayHello(@Path("name") String arg) {
    return "Hello " + arg;
}
```

For the above examples, the http param name is still "name".So the url is still like 
http://ip:port/hello?name=XXXXX"

#### Header

You can also use @Header to get header param:

```
@Path("/testParmHeader")
public void testParmHeader(@Header() String host,
                           @Header("http-client-ip") String clientIp,
                           @Header Map<String, String> headers) {
    mTarget.testParmHeader(host, clientIp, headers);
}
```


### License

This project is released under the [WTFPL LICENSE](http://www.wtfpl.net/).I love the license,you can do what the fuck you want to do.

