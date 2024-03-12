# Worox
An HTTP reverse proxy.
Based on [Jentiti](https://github.com/vortezwohl/Jentiti), [Netty](https://github.com/netty/netty), [SpringCore/SpringWeb](https://github.com/spring-projects/spring-framework).
## Get Started
#### Put worox.conf beside worox-x.x.x.jar or in classpath to take effect.
![image](https://github.com/vortezwohl/worox/assets/117743023/890565da-a650-4fce-80a1-3b4f05493e77)
#### Do some necessary configurations before worox boots up (values for port and max-content-length are necessary).
![image](https://github.com/vortezwohl/worox/assets/117743023/37f1f2ad-b9aa-4b51-b040-0a60e9807f12)
#### Run worox.
![image](https://github.com/vortezwohl/worox/assets/117743023/e38d0d32-a1cb-48ad-bf5d-48e231fe2cdf)
#### Start a service to test (localhost:8080).
![image](https://github.com/vortezwohl/worox/assets/117743023/b65a368e-a236-4216-b2cf-77945812a3cb)
#### Send a http request to worox and response is successfully received.
![image](https://github.com/vortezwohl/worox/assets/117743023/b7a956ac-c1b6-4611-9e50-dc1e242b03f8)
#### Check logs of worox.
![image](https://github.com/vortezwohl/worox/assets/117743023/16f74afb-021e-4391-8066-6118ee884f9d)
#### Send a bad http request (which doesn't matches the rules of filtering handler).
I sent a PUT request which wasn't allowed according to worox.conf. As you could see, the response received is "Filtered".
![image](https://github.com/vortezwohl/worox/assets/117743023/312a28d7-d710-4bcd-9869-d16cad77d0fc)
A WARN log was appended into stdout.
![image](https://github.com/vortezwohl/worox/assets/117743023/4536b4fe-c85c-47a3-a66e-0d13420f004e)
## To Compose Configurations
There are two sections in config file: \<http-filter\>\</http-filter\> and \<reverse-proxy\>\</reverse-proxy\>. The two sections are configured for the HTTP message filter and reverse proxy handler.
#### \<http-filter\>
```xml
<http-filter>
    methods: ["GET","POST"],
    headers: {
        "Connection": ["keep-alive","something"]
    }
</http-filter>
```
The "methods" is a list of strings, which configures the HTTP request methods allowed by the filter. Currently, only "GET" and "POST" are supported. The "headers" is a Map object containing HTTP header information. For example, "Connection": ["keep-alive","something"] means that the request headers must have at least one "Connection" field, and at least one value of a "Connection" field corresponds to the value of "keep-alive" or "something". If the request method is not configured in worox.conf, or the request header lacks a specific header, or the value of the specific header cannot be found in the configured specified values, then the HTTP request will be marked as a dangerous request and filtered by the packet filter, and will not be forwarded to the proxy processor.
#### \<reverse-proxy\>
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
"port" is an Integer, which indicating the tcp port listened by worox. "max-content-length" is an Integer too, which indicating the maximum HTTP message length supported by worox (unit: Byte). "bind" is a Map which mapping paths to specific HTTP servers. The key is a proxy path corresponding to a service, and the value is the HTTP server providing the corresponding service. For example, if I have enabled the worox service on localhost:80 and send a http request to "http://localhost:80/google-service/api/user", the worox proxy will forward my request to "http://www.google.com/api/user", and after receiving responses, will forward the response to me, thus completing a request proxy.
#### Eventually the whole conf file looks like this: 
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
