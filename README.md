# worox
http reverse proxy
## Configuration File - worox.conf (Put worox.conf beside worox-x.x.x.jar to take effect. While developping, you can also put it in classpath.)
There are two sections in config file: \<http-filter\>\</http-filter\> and \<reverse-proxy\>\</reverse-proxy\>. The two sections are configured for the HTTP message filter and reverse proxy handler.
### \<http-filter\>
```xml
<http-filter>
    methods: ["GET","POST"],
    headers: {
        "Connection": ["keep-alive","something"]
    }
</http-filter>
```
The "methods" is a list of strings, which configures the HTTP request methods allowed by the filter. Currently, only "GET" and "POST" are supported. The "headers" is a Map object containing HTTP header information. For example, "Connection": ["keep-alive","something"] means that the request headers must have at least one "Connection" field, and at least one value of a "Connection" field corresponds to the value of "keep-alive" or "something". If the request method is not configured in worox.conf, or the request header lacks a specific header, or the value of the specific header cannot be found in the configured specified values, then the HTTP request will be marked as a dangerous request and filtered by the packet filter, and will not be forwarded to the proxy processor.
### \<reverse-proxy\>
```xml
<reverse-proxy>
    port: 20386,
    max-content-length: 1048576,
    bind: {
        "xynth-backend": "http://localhost:8080",
        "google-service": "http://www.google.com"
    }
</reverse-proxy>
```
The "port" is an Integer type, indicating the port number listened by the proxy server. The "max-content-length" is an Integer type, indicating the maximum HTTP message length supported by the proxy server (unit: Byte). The "bind" is a Map object that implements proxy by mapping paths to specific HTTP servers. The key is the proxy path corresponding to the service, and the value is the HTTP server providing the corresponding service. For example, if I have enabled the worox service on my local host's port 80 and accessed http://localhost:80/google-service/api/user, then the worox proxy will forward my request to http://www.google.com/api/user, and after receiving the response, it will forward the response to me, thus completing a request proxy.
### Eventually the whole conf file looks like this: 
worox.conf
```xml
<http-filter>
    methods: ["GET","POST"],
    headers: {
        "Connection": ["keep-alive","something"]
    }
</http-filter>

<reverse-proxy>
    port: 20386,
    max-content-length: 1048576,
    bind: {
        "xynth-backend": "http://localhost:8080",
        "google-service": "http://www.google.com"
    }
</reverse-proxy>
```
