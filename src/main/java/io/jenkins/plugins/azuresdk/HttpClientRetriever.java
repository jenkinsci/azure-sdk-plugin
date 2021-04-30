package io.jenkins.plugins.azuresdk;

import com.azure.core.http.HttpClient;
import com.azure.core.http.ProxyOptions;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import hudson.ProxyConfiguration;
import hudson.Util;
import jenkins.model.Jenkins;
import jenkins.util.JenkinsJVM;

import java.net.InetSocketAddress;

public class HttpClientRetriever {

    /**
     * Jenkins class loader prevents the built in auto-detection from working
     * need to pass an explicit http client.
     *
     * If this is running on an agent then use {@link #get(ProxyConfiguration)}
     */
    public static HttpClient get() {
        if (JenkinsJVM.isJenkinsJVM()) {
            ProxyConfiguration proxy = Jenkins.get().proxy;
            if (proxy != null) {
                return get(proxy);
            }
        }
        return new NettyAsyncHttpClientBuilder().build();
    }
    
    public static HttpClient get(ProxyConfiguration proxy) {
        ProxyOptions proxyOptions = null;

        if (proxy != null) {
            proxyOptions = new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress(proxy.name, proxy.port));
            if (proxy.getSecretPassword() != null && !proxy.getSecretPassword().getPlainText().equals("")) {
                proxyOptions.setCredentials(proxy.getUserName(), proxy.getSecretPassword().getPlainText());
            }
            String noProxyHost = Util.fixEmpty(proxy.getNoProxyHost());
            if (noProxyHost != null) {
                proxyOptions.setNonProxyHosts(noProxyHost);
            }
        }

        return new NettyAsyncHttpClientBuilder().proxy(proxyOptions).build();
    }
}
