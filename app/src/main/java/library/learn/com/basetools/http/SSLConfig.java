package library.learn.com.basetools.http;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.math.BigInteger;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * SSL配置类
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SSLConfig {
    /**
     * X509TrustManager
     */
    private final X509TrustManager x509TrustManager;
    /**
     * SSLSocketFactory
     */
    private final SSLSocketFactory sslSocketFactory;
    /**
     * HostnameVerifier
     */
    private final HostnameVerifier hostnameVerifier;

    /**
     * SSLConfig构造辅助类
     */
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Builder {
        private boolean trustAll;
        private String protocol = "SSLv3";
        private boolean debug;
        private Set<String> publicKeys = Sets.newLinkedHashSet();
        private List<String> hostnameList = Lists.newArrayList();

        /**
         * 设置SSL协议
         */
        public Builder setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        /**
         * 是否启用日志
         */
        public Builder setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        /**
         * 是否信任所有服务器
         */
        public Builder trustAll(boolean trustAll) {
            this.trustAll = trustAll;
            return this;
        }

        /**
         * 添加受信任的服务器公钥
         */
        public Builder addPublicKey(String publicKey) {
            this.publicKeys.add(publicKey);
            return this;
        }

        /**
         * 添加受信任的服务器名称
         */
        public Builder addHostName(String hostname) {
            hostname = hostname.replace(".", "\\.");
            hostname = hostname.replace("*", "[^\\.]*");
            this.hostnameList.add(hostname);
            return this;
        }

        /**
         * 创建SSLConfig实例
         */
        public SSLConfig create() {
            X509TrustManager x509TrustManager = createX509TrustManager();
            SSLSocketFactory sslSocketFactory = createSSLSocketFactory(x509TrustManager);
            HostnameVerifier hostnameVerifier = createHostnameVerifier();
            return new SSLConfig(x509TrustManager, sslSocketFactory, hostnameVerifier);
        }

        private X509TrustManager createX509TrustManager() {
            return new X509TrustManager() {
                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    checkTrusted(chain, authType);
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    checkTrusted(chain, authType);
                }

                @SuppressWarnings("unused")
                private void checkTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    if (trustAll) {
                        return;
                    }
                    if (chain == null) {
                        throw new IllegalArgumentException("checkServerTrusted, X509Certificate array is null");
                    }
                    if (!(chain.length > 0)) {
                        throw new IllegalArgumentException("checkServerTrusted, X509Certificate is empty");
                    }
                    if (authType == null || false == authType.endsWith("RSA")) {
                        throw new CertificateException("checkServerTrusted, AuthType is not support: " + authType);
                    }
                    // Perform customary SSL/TLS checks
                    if (false) {
                        try {
                            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
                            tmf.init((KeyStore) null);
                            for (TrustManager trustManager : tmf.getTrustManagers()) {
                                ((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
                            }
                        } catch (Exception e) {
                            throw new CertificateException(e);
                        }
                    }
                    // Hack ahead: BigInteger and toString(). We know a DER encoded Public Key begins
                    // with 0×30 (ASN.1 SEQUENCE and CONSTRUCTED), so there is no leading 0×00 to drop.
                    RSAPublicKey publicKey = (RSAPublicKey) chain[0].getPublicKey();
                    String encoded = new BigInteger(1 /* positive */, publicKey.getEncoded()).toString(16).toLowerCase();
                    if (!publicKeys.contains(encoded)) {
                        if (debug) {
                            System.out.println("https: " + encoded);
                        }
                        throw new CertificateException("服务器证书错误");
                    }
                }
            };
        }

        private SSLSocketFactory createSSLSocketFactory(X509TrustManager x509TrustManager) {
            try {
                SSLContext sslContext = SSLContext.getInstance(protocol);
                sslContext.init(null, new X509TrustManager[]{x509TrustManager}, new SecureRandom());
                return sslContext.getSocketFactory();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private HostnameVerifier createHostnameVerifier() {
            return (hostname, session) -> {
                if (trustAll) return true;
                if (debug) {
                    System.out.println("https: " + hostname);
                }
                for (String s : hostnameList) {
                    if (hostname.matches(s)) {
                        return true;
                    }
                }
                return false;
            };
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }
}
