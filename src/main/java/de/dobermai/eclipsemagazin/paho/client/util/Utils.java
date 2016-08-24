package de.dobermai.eclipsemagazin.paho.client.util;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Random;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.security.cert.*;
import javax.net.ssl.*;
import java.security.cert.Certificate;

import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.bouncycastle.jce.provider.*;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

/**
 * @author Dominik Obermaier
 * @author Paul Caponetti
 */
public class Utils {

    private static final Random random = new Random();


    public static String getMacAddress() {
        String result = "";

        try {
            for (NetworkInterface ni : Collections.list(
                    NetworkInterface.getNetworkInterfaces())) {
                byte[] hardwareAddress = ni.getHardwareAddress();

                if (hardwareAddress != null) {
                    for (int i = 0; i < hardwareAddress.length; i++)
                        result += String.format((i == 0 ? "" : "-") + "%02X", hardwareAddress[i]);

                    return result;
                }
            }

        } catch (SocketException e) {
            System.out.println("Could not find out MAC Adress. Exiting Application ");
            System.exit(1);
        }
        return result;
    }

    public static int createRandomNumberBetween(int min, int max) {

        return random.nextInt(max - min + 1) + min;
    }
    
	public static SSLSocketFactory getSocketFactory (final String caCrtFile, final String randoPassword) throws Exception
	{
		Security.addProvider(new BouncyCastleProvider());
		
		// load CA certificate
		PemReader reader = new PemReader(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(caCrtFile)))));
		CertificateFactory certFact = CertificateFactory.getInstance("X.509");
		X509Certificate caCert = (X509Certificate)certFact.generateCertificate(new ByteArrayInputStream(reader.readPemObject().getContent()));
		reader.close();
		
//		// load client certificate
//		reader = new PemReader(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(crtFile)))));
//		X509Certificate cert = (X509Certificate)reader.readPemObject();
//		reader.close();
//		
//		// load client private key
//		reader = new PemReader(
//			new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(keyFile)))),
//			new PasswordFinder() {
//				@Override
//				public char[] getPassword() {
//					return password.toCharArray();
//				}
//			}
//		);
//		KeyPair key = (KeyPair)reader.readPemObject();
//		reader.close();
		
		// CA certificate is used to authenticate server
		KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
		caKs.load(null, null);
		caKs.setCertificateEntry("ca-certificate", caCert);
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(caKs);
		
		// client key and certificates are sent to server so it can authenticate us
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, null);
//		ks.setCertificateEntry("certificate", cert);
//		ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(), new java.security.cert.Certificate[]{cert});
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, randoPassword.toCharArray());
		
		// finally, create SSL socket factory
		SSLContext context = SSLContext.getInstance("TLSv1");
		context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
		
		return context.getSocketFactory();
	}
}
