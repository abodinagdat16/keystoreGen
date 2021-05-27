package com.example.myapp.signing;

import java.io.FileOutputStream;
import java.security.KeyPairGenerator;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.KeyPair;
import sun1.security.x509.X500Name;
import java.security.cert.Certificate;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.security.PrivateKey;
import sun1.security.x509.X509CertInfo;
import java.util.Date;
import sun1.security.x509.CertificateValidity;
import java.math.BigInteger;
import sun1.security.x509.AlgorithmId;
import sun1.security.x509.CertificateSerialNumber;
import sun1.security.x509.CertificateSubjectName;
import sun1.security.x509.CertificateIssuerName;
import sun1.security.x509.CertificateX509Key;
import sun1.security.x509.CertificateVersion;
import sun1.security.x509.CertificateAlgorithmId;
import sun1.security.x509.X509CertImpl;
import java.io.File;
import java.security.Security;
import sun1.security.provider.JavaProvider;
import sun1.security.x509.CertificateExtensions;
import sun1.security.x509.SubjectKeyIdentifierExtension;
import sun1.security.x509.KeyIdentifier;
import sun1.security.x509.PrivateKeyUsageExtension;

public class KeyStoreHelper {

    static {
        Security.addProvider(new JavaProvider());
    }
    
    /*
    String keystoreKeyAlias = null;
    if (keystoreKeyAlias == null) {
        // Private key entry alias not specified. Find the key entry contained in this
        Enumeration<String> aliases = ks.aliases();
        if (aliases != null) {
            while (aliases.hasMoreElements()) {
                String entryAlias = aliases.nextElement();
                if (ks.isKeyEntry(entryAlias)) {
                    keystoreKeyAlias = entryAlias;
                }
            }
        }
     }
     */
    
    public static void generate(Builder cl) throws IOException, GeneralSecurityException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(cl.alg.toString());
        keyPairGenerator.initialize(cl.size.getValue(), SecureRandom.getInstance("SHA1PRNG"));
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        X500Name name = new X500Name(cl.commonName, cl.organizationUnit, cl.organizationName, cl.cityOrLocality, cl.stateName, cl.countryCode);
        X509Certificate certificate = generateCertificate(name, keyPair, cl.validity, cl.sigAlg.toString());
        PrivateKey privateKey = keyPair.getPrivate();
        FileOutputStream fos = new FileOutputStream(cl.output);
        KeyStore ks = KeyStore.getInstance(cl.type.toString());
        ks.load(null, null);
        ks.setKeyEntry(cl.alias, privateKey, cl.keypass.toCharArray(), new Certificate[] {certificate});
        ks.store(fos, cl.storepass.toCharArray());
        fos.close();
    }
    
    private static X509Certificate generateCertificate(X500Name owner, KeyPair keyPair, int validity, String sigAlgName) throws GeneralSecurityException, IOException {
        PrivateKey privateKey = keyPair.getPrivate();
        Date from = new Date(System.currentTimeMillis());
        Date to = new Date(System.currentTimeMillis() + validity * 365L * 24 * 60 * 60 * 1000);
        CertificateValidity interval = new CertificateValidity(from, to);
        BigInteger serialNumber = new BigInteger(64, new SecureRandom());
        X509CertInfo info = new X509CertInfo();
        info.set(X509CertInfo.VALIDITY, interval);
        info.set(X509CertInfo.SERIAL_NUMBER, new CertificateSerialNumber(serialNumber));
        info.set(X509CertInfo.SUBJECT, new CertificateSubjectName(owner));
        info.set(X509CertInfo.ISSUER, new CertificateIssuerName(owner));
        info.set(X509CertInfo.KEY, new CertificateX509Key(keyPair.getPublic()));
        info.set(X509CertInfo.VERSION, new CertificateVersion(CertificateVersion.V3));
        info.set(X509CertInfo.ALGORITHM_ID, new CertificateAlgorithmId(AlgorithmId.get(sigAlgName)));
        CertificateExtensions certificateExtensions = new CertificateExtensions();
        certificateExtensions.set("SubjectKeyIdentifier", new SubjectKeyIdentifierExtension(new KeyIdentifier(keyPair.getPublic()).getIdentifier()));
        certificateExtensions.set("PrivateKeyUsage", new PrivateKeyUsageExtension(from, to));
        if (certificateExtensions != null) {
            info.set(X509CertInfo.EXTENSIONS, certificateExtensions);
        }
        X509CertImpl certificate = new X509CertImpl(info);
        certificate.sign(privateKey, sigAlgName);
        return certificate;
    }
    
    public static class Builder {
        public File output;
        public String alias;
        public String storepass;
        public String keypass;
        public int validity;
        public String commonName;
        public String organizationUnit;
        public String organizationName;
        public String cityOrLocality;
        public String stateName;
        public String countryCode;
        public Type type;
        public Size size;
        public Algorithm alg;
        public SigAlgorithm sigAlg;
        
        public void setOutputFile(File output) {
            this.output = output;
        }
        public void setAlias(String alias) {
            this.alias = alias;
        }
        public void setStorePassword(String storepass) {
            this.storepass = storepass;
        }
        public void setKeyPassword(String keypass) {
            this.keypass = keypass;
        }
        public void setValidityYears(int validity) {
            this.validity = validity;
        }
        public void setCommonName(String commonName) {
            this.commonName = commonName;
        }
        public void setOrganizationUnit(String organizationUnit) {
            this.organizationUnit = organizationUnit;
        }
        public void setOrganizationName(String organizationName) {
            this.organizationName = organizationName;
        }
        public void setCityOrLocalityName(String cityOrLocality) {
            this.cityOrLocality = cityOrLocality;
        }
        public void setStateName(String stateName) {
            this.stateName = stateName;
        }
        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }
        public void setStoreType(Type type) {
            this.type = type;
        }
        public void setKeySize(Size size) {
            this.size = size;
        }
        public void setKeyAlgorithm(Algorithm alg) {
            this.alg = alg;
        }
        public void setSigAlgorithm(SigAlgorithm alg) {
            this.sigAlg = alg;
        }
    }
    
    public static enum Type {
        JKS("JKS"),
        BKS("BKS"),
        PKCS12("PKCS12");
        
        String type;
        
        public Type(String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return type;
        }
    }
    
    public static enum Size {
        S_2048(2048),
        S_1024(1024);

        int size;

        public Size(int size) {
            this.size = size;
        }
        public int getValue() {
            return size;
        }
    }
    
    public static enum Algorithm {
        RSA("RSA"),
        EC("EC");
        
        String alg;

        public Algorithm(String alg) {
            this.alg = alg;
        }
        @Override
        public String toString() {
            return alg;
        }
    }
    
    public static enum SigAlgorithm {
        SHA1WITHRSA("SHA1withRSA"),
        SHA512WITHRSA("SHA512withRSA"),
        SHA256WITHRSA("SHA256withRSA"),
        MD5WITHRSA("MD5withRSA");
        
        String alg;

        public SigAlgorithm(String alg) {
            this.alg = alg;
        }
        @Override
        public String toString() {
            return alg;
        }
    }
}
