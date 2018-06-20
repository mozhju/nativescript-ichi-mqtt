/**
 * aliyun.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
 */
package com.aliyun.alink.linksdk.channel.core.persistent.mqtt.utils;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.aliyun.alink.linksdk.tools.ALog;

/**
 *
 * */
public class MqttTrustManager implements X509TrustManager{

    private static final String TAG = "MqttTrustManager";
    
    private final X509TrustManager trustManager;
    private final KeyStore keyStore;

    public MqttTrustManager(InputStream certFile) throws Exception {
        keyStore = initKeyStore(certFile); // init keystore from certFile

        TrustManagerFactory trustManagerFactory = TrustManagerFactory
            .getInstance("X509");
        trustManagerFactory.init((KeyStore) null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        trustManager = (X509TrustManager) trustManagers[0];
    }

    private KeyStore initKeyStore(InputStream file) throws Exception {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null);

        CertificateFactory certificateFactory = CertificateFactory
            .getInstance("X.509");
        X509Certificate cert = (X509Certificate) certificateFactory
            .generateCertificate(file);

        trustStore.setCertificateEntry(
            cert.getSubjectX500Principal().getName(), cert);

        return trustStore;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
        // Never called by client, ignore
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
        throws CertificateException {
        try {
            trustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException e) {
            try {
                X509Certificate[] reorderedChain = reorderCertificateChain(chain);
                CertPathValidator validator = CertPathValidator
                    .getInstance("PKIX");
                CertificateFactory factory = CertificateFactory
                    .getInstance("X509");
                CertPath certPath = factory.generateCertPath(Arrays
                    .asList(reorderedChain));
                PKIXParameters params = new PKIXParameters(keyStore);
                params.setRevocationEnabled(false);
                validator.validate(certPath, params);
            } catch (CertificateNotYetValidException ee) {
                ALog.d(TAG,"CertificateNotYetValidException "+ee);
                return;
            } catch (Exception ex) {

                Throwable cases = ex.getCause();
                if (cases instanceof CertificateNotYetValidException) {
                    ALog.d(TAG,"validate cert failed.because system is early than cert valid . wsf will ignore this exception,"+ex);
                    return;
                }
                ALog.d(TAG,"checkServerTrusted faied."+e);
                ALog.d(TAG,"validate cert failed."+ex);
                throw e;
            }
        }
    }

    private X509Certificate[] reorderCertificateChain(X509Certificate[] chain) {

        X509Certificate[] reorderedChain = new X509Certificate[chain.length];
        List<X509Certificate> certificates = Arrays.asList(chain);

        int position = chain.length - 1;
        X509Certificate rootCert = findRootCert(certificates);
        reorderedChain[position] = rootCert;

        X509Certificate cert = rootCert;
        while ((cert = findSignedCert(cert, certificates)) != null
            && position > 0) {
            reorderedChain[--position] = cert;
        }

        return reorderedChain;
    }

    private X509Certificate findRootCert(List<X509Certificate> certificates) {
        X509Certificate rootCert = null;

        for (X509Certificate cert : certificates) {
            X509Certificate signer = findSigner(cert, certificates);
            if (signer == null || signer.equals(cert)) {
                rootCert = cert;
                break;
            }
        }

        return rootCert;
    }

    private X509Certificate findSignedCert(X509Certificate signingCert,
                                           List<X509Certificate> certificates) {
        X509Certificate signed = null;

        for (X509Certificate cert : certificates) {
            Principal signingCertSubjectDN = signingCert.getSubjectDN();
            Principal certIssuerDN = cert.getIssuerDN();
            if (certIssuerDN.equals(signingCertSubjectDN)
                && !cert.equals(signingCert)) {
                signed = cert;
                break;
            }
        }

        return signed;
    }

    private X509Certificate findSigner(X509Certificate signedCert,
                                       List<X509Certificate> certificates) {
        X509Certificate signer = null;

        for (X509Certificate cert : certificates) {
            Principal certSubjectDN = cert.getSubjectDN();
            Principal issuerDN = signedCert.getIssuerDN();
            if (certSubjectDN.equals(issuerDN)) {
                signer = cert;
                break;
            }
        }

        return signer;
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        // Never called by client, ignore
        return new X509Certificate[0];
    }


}
