package com.example.myapp.signing;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class CertificateInfo {

    public final PrivateKey mKey;
    public final X509Certificate mCertificate;

    public CertificateInfo(PrivateKey key, X509Certificate certificate) {
        this.mKey = key;
        this.mCertificate = certificate;
    }

    public PrivateKey getKey() {
        return mKey;
    }

    public X509Certificate getCertificate() {
        return mCertificate;
    }
}
