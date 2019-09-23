# Content Security Policy

Content Security Policy (CSP) is a computer security standard introduced to prevent cross-site scripting (XSS), 
clickjacking and other code injection attacks resulting from execution of malicious content in the trusted web 
page context. It is widely supported by modern web browsers. CSP provides a standard method for website owners 
to declare approved origins of content that browsers should be allowed to load on that website covered types 
are JavaScript, CSS, HTML frames, web workers, fonts, images, embeddable objects such as Java applets, ActiveX, 
audio and video files, and other HTML5 features.

PrimeFaces provides CSP support by simply enabling a context parameter 

## Configuration

CSP is disabled by default and a global parameter is required to turn it on.

```xml
<context-param>
    <param-name>primefaces.CSP</param-name>
    <param-value>true</param-value>
</context-param>
```

## Troubleshooting

If you have enabled CSP on your PrimeFaces application and you find issues with your application, 
it is recommended to use your browser Console (F12) to diagnose the issues. There is a possibility 
you are using invalid code or Javascript on your site that is now being blocked.  For example, 
the Javascript **eval()** function to execute arbitrary code.

It is also possible that you have detected an issue with one of the Javascript plugins that PrimeFaces
uses.  If this is the case, please report it to the GitHub Issues page. 


