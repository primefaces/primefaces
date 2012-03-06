package org.primefaces.tests;

import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebResponse;
import org.jboss.jsfunit.framework.RequestListener;

public class BaseRequestListener implements RequestListener{

    //fields
    
    public void beforeRequest(WebRequest wr) {
        System.out.println(wr.getRequestBody());
        System.out.println("before");
    }

    public void afterRequest(WebResponse wr) {
        System.out.println(wr.getContentAsString());
        System.out.println("after");
        
    }
    
    // write useful request/response informative functions.
}
