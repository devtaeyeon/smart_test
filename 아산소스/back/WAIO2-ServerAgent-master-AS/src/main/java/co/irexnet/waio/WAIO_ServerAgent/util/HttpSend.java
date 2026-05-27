package co.irexnet.waio.WAIO_ServerAgent.util;

import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicStatusLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@Getter
@Setter
@ToString
public class HttpSend
{
    @Autowired
    PropertiesDelegate propertiesDelegate;

    private Boolean isVip;

    @PostConstruct
    public void init()
    {
        isVip = false;
    }

    public HttpResponse send(HttpUriRequest request)
    {
        try {
            if (isVip) {
                log.info("This server is VIP!");
                HttpResponseFactory factory = new DefaultHttpResponseFactory();
                HttpResponse response = factory.newHttpResponse(
                    new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_METHOD_NOT_ALLOWED, null),
                    null);
                HttpClient client = HttpClientBuilder.create().build();
                response = client.execute(request);
                return response;
            } else {
                log.info("This server is Not VIP!");
                return null;
            }
        }
        catch(HttpHostConnectException e)
        {
            return null;
        }
        catch(ClientProtocolException e)
        {
            return null;
        }
        catch(IOException e)
        {
            return null;
        }
    }
    
    public HttpResponse sendToLocal(HttpUriRequest request)
    {
        try
        {
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse response = client.execute(request);
            return response;
        }
        catch(HttpHostConnectException e)
        {
        	log.error("Connection error: " + e.getMessage());
            return null;
        }
        catch(ClientProtocolException e)
        {
        	log.error("Protocol error: " + e.getMessage());
            return null;
        }
        catch(IOException e)
        {
        	log.error("IO error: " + e.getMessage());
            return null;
        }
    }

    public void isVip() {
        try {
            String vipHostname = propertiesDelegate.getAddress();
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            Loop1:
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                Loop2:
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    // log.info(address.getHostName());
                    if (address.getHostName().equals(vipHostname)) {
                        isVip = true;
                        break Loop1;
                    }
                }
            }
        }
        catch(HttpHostConnectException e)
        {
            isVip = false;
        }
        catch(IOException e)
        {
            isVip = false;
        }
    }
}