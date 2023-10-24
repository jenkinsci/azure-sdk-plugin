package io.jenkins.plugins.azuresdk;

import com.azure.core.http.HttpClient;
import com.azure.core.http.ProxyOptions;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import hudson.ProxyConfiguration;
import hudson.Util;
import java.time.Duration;
import jenkins.model.Jenkins;
import jenkins.util.JenkinsJVM;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.stream.Collectors;

import jenkins.util.SystemProperties;

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
        return getBuilder().build();
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
                // com.azure.core.http.ProxyOptions accepts a '|' delimited String
                // https://learn.microsoft.com/en-us/java/api/com.azure.core.http.proxyoptions?view=azure-java-stable#com-azure-core-http-proxyoptions-setnonproxyhosts(java-lang-string)
                proxyOptions.setNonProxyHosts(Arrays.stream(noProxyHost.split("[ \t\n,|]+"))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.joining("|")));
            }
        }

        return getBuilder().proxy(proxyOptions).build();
    }

    private static NettyAsyncHttpClientBuilder getBuilder() {
        NettyAsyncHttpClientBuilder builder = new NettyAsyncHttpClientBuilder();

        // Apply settings from system properties
        Long readTimeoutSeconds = SystemProperties.getLong(HttpClientRetriever.class.getName() + ".readTimeoutSeconds");
        if (readTimeoutSeconds != null) {
            builder.readTimeout(Duration.ofSeconds(readTimeoutSeconds));
        }
        Long responseTimeoutSeconds = SystemProperties.getLong(HttpClientRetriever.class.getName() + ".responseTimeoutSeconds");
        if (responseTimeoutSeconds != null) {
            builder.responseTimeout(Duration.ofSeconds(responseTimeoutSeconds));
        }
        Long writeTimeoutSeconds = SystemProperties.getLong(HttpClientRetriever.class.getName() + ".writeTimeoutSeconds");
        if (writeTimeoutSeconds != null) {
            builder.writeTimeout(Duration.ofSeconds(writeTimeoutSeconds));
        }

        return builder;
    }
}
