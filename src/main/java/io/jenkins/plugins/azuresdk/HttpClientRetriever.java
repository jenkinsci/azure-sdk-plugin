package io.jenkins.plugins.azuresdk;

import com.azure.core.http.HttpClient;
import com.azure.core.http.ProxyOptions;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import hudson.ProxyConfiguration;
import jenkins.model.Jenkins;
import jenkins.util.JenkinsJVM;

import java.net.InetSocketAddress;

public class HttpClientRetriever {

    public static HttpClient get() {
        // Jenkins class loader prevents the built in auto-detection from working
        // need to pass an explicit http client
        ProxyOptions proxyOptions = null;
        if (JenkinsJVM.isJenkinsJVM()) {
            ProxyConfiguration proxy = Jenkins.get().proxy;
            if (proxy != null) {
                proxyOptions = new ProxyOptions(ProxyOptions.Type.HTTP, new InetSocketAddress(proxy.name, proxy.port));
                if (proxy.getSecretPassword() != null && !proxy.getSecretPassword().getPlainText().equals("")) {
                    proxyOptions.setCredentials(proxy.getUserName(), proxy.getSecretPassword().getPlainText());
                }
            }
        }
        return new NettyAsyncHttpClientBuilder().proxy(proxyOptions).build();
    }
}
