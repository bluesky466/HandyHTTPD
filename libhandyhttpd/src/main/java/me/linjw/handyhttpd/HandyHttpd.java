package me.linjw.handyhttpd;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.linjw.handyhttpd.annotation.Get;
import me.linjw.handyhttpd.annotation.Post;
import me.linjw.handyhttpd.annotation.Path;
import me.linjw.handyhttpd.httpcore.HttpRequest;
import me.linjw.handyhttpd.httpcore.HttpResponse;
import me.linjw.handyhttpd.httpcore.HttpServer;
import me.linjw.handyhttpd.httpcore.MimeType;
import me.linjw.handyhttpd.scheduler.FixSizeScheduler;
import me.linjw.handyhttpd.scheduler.IScheduler;

/**
 * Created by linjw on 18-4-1.
 */

public class HandyHttpd {
    /**
     * new http response.
     *
     * @param message message
     * @return HttpResponse
     */
    public static HttpResponse newResponse(String message) {
        return newResponse(HttpResponse.Status.OK, message);
    }

    /**
     * new http response.
     *
     * @param status  status
     * @param message message
     * @return HttpResponse
     */
    public static HttpResponse newResponse(HttpResponse.Status status, String message) {
        return newResponse(status, message, MimeType.TEXT_PLAIN);
    }

    /**
     * new http response.
     *
     * @param status   status
     * @param message  message
     * @param mimeType mimeType
     * @return HttpResponse
     */
    public static HttpResponse newResponse(
            HttpResponse.Status status,
            String message,
            MimeType mimeType) {
        if (message == null) {
            message = "";
        }
        return new HttpResponse(
                status,
                mimeType,
                new ByteArrayInputStream(message.getBytes()),
                message.getBytes().length);
    }

    /**
     * new http response.
     *
     * @param status status
     * @param file   file
     * @return HttpResponse
     */
    public static HttpResponse newResponse(
            HttpResponse.Status status,
            File file) {
        if (file == null) {
            return newResponse(HttpResponse.Status.NOT_FOUND, "404 Not Found");
        }
        String name = file.getName();
        String ext = name.substring(name.lastIndexOf(".") + 1);
        return newResponse(status, file, MimeType.getMimeTypeByExt(ext));
    }

    /**
     * new http response.
     *
     * @param status   status
     * @param file     file
     * @param mimeType mimeType
     * @return HttpResponse
     */
    public static HttpResponse newResponse(
            HttpResponse.Status status,
            File file,
            MimeType mimeType) {
        if (file == null) {
            return newResponse(HttpResponse.Status.NOT_FOUND, "404 Not Found");
        }

        try {
            FileInputStream fis = new FileInputStream(file);

            return new HttpResponse(
                    status,
                    mimeType,
                    fis,
                    file.length());
        } catch (FileNotFoundException e) {
            return newResponse(HttpResponse.Status.NOT_FOUND, "404 Not Found");
        }
    }

    /**
     * new http response.
     *
     * @param status status
     * @param is     is
     * @return HttpResponse
     */
    public static HttpResponse newResponse(
            HttpResponse.Status status,
            InputStream is) {
        return newResponse(status, is, MimeType.APPLICATION_OCTET_STREAM);
    }

    /**
     * new http response.
     *
     * @param status   status
     * @param is       is
     * @param mimeType mimeType
     * @return HttpResponse
     */
    public static HttpResponse newResponse(
            HttpResponse.Status status,
            InputStream is,
            MimeType mimeType) {
        if (is == null) {
            return newResponse(HttpResponse.Status.NOT_FOUND, "404 Not Found");
        }
        return new HttpResponse(status, mimeType, is, -1);
    }

    /**
     * safe close.
     *
     * @param closeable closeable
     */
    public static void safeClose(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * get service handler class simple name.
     *
     * @param className  class name
     * @param methodName method name
     * @return service handler class name
     */
    public static String getServiceHandlerSimpleName(String className, String methodName) {
        return "HandyHttpd" + "_" + className + "_" + methodName + "_" + "ServiceHandler";
    }

    /**
     * get service handler class name.
     *
     * @param service    service
     * @param methodName method name
     * @return service handler class name
     */
    public static String getServiceHandlerName(Class service, String methodName) {
        return service.getPackage().getName() +
                "." +
                getServiceHandlerSimpleName(service.getSimpleName(), methodName);
    }

    /**
     * Log Helper.
     */
    public static final class Log {
        private static final boolean DEBUG = false;
        private static final String TAG = "HandyHttpd";

        /**
         * log.
         *
         * @param message message
         */
        public static void log(String message) {
            log(null, message);
        }

        /**
         * log.
         *
         * @param tag     tag
         * @param message message
         */
        public static void log(String tag, String message) {
            if (DEBUG) {
                tag = (tag != null ? TAG + "-" + tag : TAG);
                System.out.println("[" + tag + "] " + message);
            }
        }

        /**
         * log.
         *
         * @param e Exception
         */
        public static void log(Exception e) {
            if (DEBUG) {
                e.printStackTrace();
            }
        }

        /**
         * print request just for defbug.
         */
        public static void log(HttpRequest request) {
            if (!DEBUG) {
                return;
            }

            String method = request.getMethod().name();
            String uri = request.getUri();
            String version = request.getVersion();
            Map<String, String> headers = request.getHeaders();
            Map<String, String> params = request.getParams();
            Map<String, File> files = request.getFiles();

            log("########## " + method + " " + uri + " " + version + " ##########");
            log(request.getInetAddress().toString());
            log("Headers:");
            for (Map.Entry<String, String> header : headers.entrySet()) {
                log(header.getKey() + " : " + header.getValue());
            }
            log("Params:");
            for (Map.Entry<String, String> param : params.entrySet()) {
                log(param.getKey() + " = " + param.getValue());
            }

            for (Map.Entry<String, File> param : files.entrySet()) {
                File file = param.getValue();
                log(param.getKey() + " = " + file.getAbsolutePath() + " size = " + file.length());
            }
            log("##########################");
        }
    }

    /**
     * HttpServerBuilder.
     */
    public static final class ServerBuilder {
        private boolean mIsDaemon = false;
        private int mTimeout = 5000;
        private String mTempFileDir = System.getProperty("java.io.tmpdir");
        private IScheduler mScheduler;
        private List<Object> mServices = new ArrayList<>();

        /**
         * set daemon.
         *
         * @param isDaemon isDaemon
         * @return ServerBuilder
         */
        public ServerBuilder setDaemon(boolean isDaemon) {
            mIsDaemon = isDaemon;
            return this;
        }

        /**
         * set timeout.
         *
         * @param timeout timeout
         * @return ServerBuilder
         */
        public ServerBuilder setTimeout(int timeout) {
            mTimeout = timeout;
            return this;
        }

        /**
         * set tempFileDir.
         *
         * @param tempFileDir tempFileDir
         * @return ServerBuilder
         */
        public ServerBuilder setTempFileDir(String tempFileDir) {
            mTempFileDir = tempFileDir;
            return this;
        }

        /**
         * set Scheduler.
         *
         * @param scheduler scheduler
         * @return ServerBuilder
         */
        public ServerBuilder setScheduler(IScheduler scheduler) {
            mScheduler = scheduler;
            return this;
        }

        /**
         * load service.
         *
         * @param service service
         * @param <T>     type
         * @return ServerBuilder
         */
        public <T> ServerBuilder loadService(T service) {
            mServices.add(service);
            return this;
        }

        /**
         * create HttpServer.
         *
         * @return HttpServer
         * @throws ClassNotFoundException
         * @throws NoSuchMethodException
         * @throws InvocationTargetException
         * @throws InstantiationException
         * @throws IllegalAccessException
         */
        public Server create() throws ClassNotFoundException,
                NoSuchMethodException,
                InvocationTargetException,
                InstantiationException,
                IllegalAccessException {
            if (mScheduler == null) {
                mScheduler = new FixSizeScheduler();
            }

            Server server = new Server(
                    mTimeout,
                    mIsDaemon,
                    mTempFileDir,
                    mScheduler);

            for (Object service : mServices) {
                server.loadService(service);
            }

            return server;
        }

        /**
         * create and start HttpServer.
         *
         * @param port port
         * @return server
         * @throws ClassNotFoundException
         * @throws NoSuchMethodException
         * @throws InstantiationException
         * @throws IllegalAccessException
         * @throws InvocationTargetException
         */
        public Server createAndStart(int port) throws
                ClassNotFoundException,
                NoSuchMethodException,
                InstantiationException,
                IllegalAccessException,
                InvocationTargetException {
            Server server = create();
            server.start(port);
            return server;
        }
    }

    public static class Server extends HttpServer {
        private Map<String, IServiceHandler> mPostServices = new HashMap<>();
        private Map<String, IServiceHandler> mGetServices = new HashMap<>();

        public Server(int timeout, boolean isDaemon, String tempFileDir, IScheduler scheduler) {
            super(timeout, isDaemon, tempFileDir, scheduler);
        }

        /**
         * load service.
         *
         * @param service service
         */
        public <T> Server loadService(T service) throws
                ClassNotFoundException,
                IllegalAccessException,
                InstantiationException,
                NoSuchMethodException,
                InvocationTargetException {

            for (Method method : service.getClass().getMethods()) {
                Path annotation = method.getAnnotation(Path.class);
                Get getFlag = method.getAnnotation(Get.class);
                Post postFlag = method.getAnnotation(Post.class);

                if (annotation != null) {

                    String className = getServiceHandlerName(service.getClass(), method.getName());
                    Object handler = Class.forName(className)
                            .getDeclaredConstructor(service.getClass())
                            .newInstance(service);
                    registerServiceHandler(
                            annotation.value(),
                            (IServiceHandler) handler,
                            getFlag,
                            postFlag);
                }
            }
            return this;
        }

        void registerServiceHandler(String uri,
                                    IServiceHandler handler,
                                    Get getFlag,
                                    Post postFlag) {

            if (getFlag == null && postFlag == null) {
                Log.log("registerOperation GET " + uri);
                Log.log("registerOperation POST " + uri);
                mPostServices.put(uri, handler);
                mGetServices.put(uri, handler);
            } else {
                if (getFlag != null) {
                    Log.log("registerOperation GET " + uri);
                    mGetServices.put(uri, handler);
                }

                if (postFlag != null) {
                    Log.log("registerOperation POST " + uri);
                    mPostServices.put(uri, handler);
                }
            }
        }

        @Override
        protected HttpResponse onRequest(HttpRequest request) {
            IServiceHandler handler = null;

            if (request.getMethod() == HttpRequest.Method.GET) {
                handler = mGetServices.get(request.getUri());
            } else if (request.getMethod() == HttpRequest.Method.POST) {
                handler = mPostServices.get(request.getUri());
            }

            if (handler != null) {
                return handler.onRequest(request);
            }

            return super.onRequest(request);
        }
    }
}
