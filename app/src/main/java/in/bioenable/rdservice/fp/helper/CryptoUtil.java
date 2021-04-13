package in.bioenable.rdservice.fp.helper;

import android.util.Base64;
import android.util.Log;

import com.google.protobuf.ByteString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import in.bioenable.rdservice.fp.model.Auth;
import in.bioenable.rdservice.fp.model.Bio;
import in.bioenable.rdservice.fp.model.Demo;
import in.bioenable.rdservice.fp.model.ErrorCode;
import in.bioenable.rdservice.fp.model.Pid;
import in.bioenable.rdservice.fp.model.RDData;

import static in.bioenable.rdservice.fp.model.Config.DP_ID;
import static in.bioenable.rdservice.fp.model.Config.MODEL_ID;
import static in.bioenable.rdservice.fp.model.Config.PID_VER;
import static in.bioenable.rdservice.fp.model.Config.RDS_ID;
import static in.bioenable.rdservice.fp.model.Config.RDS_VER;

/**
 * Created by RND on 2/16/2018.
 */

public class CryptoUtil {

    private static final String TAG = "CryptoUtil";

    public static final String SAMPLE_ALIAS = "BioEnable";

    private static final CryptoUtil instance = new CryptoUtil();

    private CryptoUtil(){}

    public static CryptoUtil getInstance() {
        return instance;
    }

    private String hex2dec2hex(String hex) {
        int r16 = Integer.parseInt(hex, 16);
        int sr16 = (r16 & 0x0FFF) | 0x3000;
        return Integer.toHexString(sr16);
    }

    public String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public byte[] getMD5(String data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String timeUpToHour() {
        return new SimpleDateFormat("yyyyMMddHH", new Locale("EN", "IN")).format(new Date());
    }

    public String createGUID() {
        String guid = null;
        try {
            String timestamp = "C" + timeUpToHour();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(timestamp.getBytes(StandardCharsets.UTF_8));
            guid = bytesToHex(hash).replace("-", "");
            guid = guid.substring(0, 8) + "-" +
                    guid.substring(8, 12) + "-" +
                    hex2dec2hex(guid.substring(12, 16)) + "-" +
                    hex2dec2hex(guid.substring(16, 20)) + "-" +
                    guid.substring(20, 32);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return guid;
    }

    public byte[] getEncryptionKey(String guid) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(guid.getBytes());
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String encryptAES256(String data, byte[] key) {
        String encryptedString = null;
        try {
            final String ALGORITHM = "AES/CBC/PKCS7Padding";
            SecretKey spec = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
            cipher.init(Cipher.ENCRYPT_MODE, spec, new IvParameterSpec(iv));
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            encryptedString = Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    public String decryptAES256(String data, byte[] key){
        String decryptedString = null;
        try {
            final String ALGORITHM = "AES/CBC/PKCS7Padding";
            SecretKey spec = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            byte[] iv = new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
            cipher.init(Cipher.DECRYPT_MODE, spec, new IvParameterSpec(iv));
            byte[] decrypted = cipher.doFinal(Base64.decode(data, Base64.DEFAULT));
            decryptedString = new String(decrypted, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return decryptedString;
    }

    public byte[] getRequestNonce() {
        String data = String.valueOf(System.currentTimeMillis());
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[24];
        Random random = new Random();
        random.nextBytes(bytes);

        try {
            byteStream.write(bytes);
            byteStream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return bytes;
        }
        return byteStream.toByteArray();
    }

    private byte[] createSignature(byte[] data, PrivateKey pk){

        byte[] signature = null;

        try {
            Signature dsa = Signature.getInstance("SHA256withRSA");
            dsa.initSign(pk);
            dsa.update(data, 0, data.length);
            signature = dsa.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }

    public static PrivateKey loadPrivateKey(){
        try {
            KeyStore keyStore = KeyStore.getInstance(SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);
            keyStore.load(null);
            KeyStore.Entry entry = keyStore.getEntry(SAMPLE_ALIAS, null);
            if (!(entry instanceof KeyStore.PrivateKeyEntry))return null;
            return ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
        } catch (KeyStoreException | CertificateException | UnrecoverableEntryException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public void logAllEntries(KeyStore keyStore) throws KeyStoreException {
//        Enumeration<String> aliases = keyStore.aliases();
//        while (aliases.hasMoreElements()){
//            String alias = aliases.nextElement();
//            try {
//                keyStore = KeyStore.getInstance("SUN");// SUN provider
//                keyStore.load(null);
//                KeyStore.Entry entry = keyStore.getEntry(alias,null);
//                if (entry instanceof KeyStore.PrivateKeyEntry){
//                    PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();
//                    String pemPubKey = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
//                    System.out.println(pemPubKey);
//                }
//            } catch (KeyStoreException | CertificateException | UnrecoverableEntryException | IOException | NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void processAndSetPidData(RDData rdData) {
        PublicKey publicKey_signSkey;
        Date certExpiryDate;
        String dih, pidXML = "", encryptedEncodedPidXML, encdEncrdHmac, ci;
        byte[] sessionKey = null, hmac,encryptedSessionKey;

        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA256");
            byte[] result = mDigest.digest(rdData.getDc().getBytes());
            StringBuilder sb = new StringBuilder();

            for (byte aResult : result) {
                sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
            }

            String stridhash = sb.toString();

            String dih_generate = DP_ID + RDS_ID + RDS_VER + rdData.getDc() + MODEL_ID + stridhash.toUpperCase();
            byte[] dih_byte = dih_generate.getBytes();
            mDigest.reset();

            byte[] hashValue3 = mDigest.digest(dih_byte);
            String dih_string64 = Base64.encodeToString(hashValue3,Base64.DEFAULT);
            dih = dih_string64.trim();
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256);
            sessionKey = kg.generateKey().getEncoded();

            DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",new Locale("EN","IN"));
            Date date = new Date(System.currentTimeMillis());
            String tsStr = dfm.format(date).replace(" ", "T");
            mDigest.reset();

            byte[] ts = tsStr.getBytes(StandardCharsets.UTF_8);
            byte[] dc = rdData.getDc().getBytes(StandardCharsets.UTF_8);

            for (Bio bio:rdData.getPidData().getPid().getBios()) {
                byte[] bh = mDigest.digest(Base64.decode(bio.getEncodedBiometric(),Base64.DEFAULT));
//                byte[] bh = mDigest.digest(bio.getEncodedBiometric().getBytes(StandardCharsets.UTF_8));
                byte[] tempBe = new byte[bh.length + ts.length + dc.length];
                System.arraycopy(bh, 0, tempBe, 0, bh.length);
                System.arraycopy(ts, 0, tempBe, bh.length, ts.length);
                System.arraycopy(dc, 0, tempBe, bh.length + ts.length, dc.length);
                mDigest.reset();

//                byte[] Be = createSignature(tempBe,RDService.getPrivateKey());
                byte[] Be = createSignature(tempBe,loadPrivateKey());
                String bs = Base64.encodeToString(Be, Base64.NO_WRAP);
                bio.setBs(bs);
            }

            Pid pidBlock = rdData.getPidData().getPid()
                    .setTs(tsStr)
                    .setVer(PID_VER)
                    .setWadh(rdData.getPidOptions().getWadh())
                    .setDemo(rdData.getPidOptions().hasDemo()?rdData.getPidOptions().getDemo():null)
                    .setOtp(rdData.getPidOptions().getOtp())
                    .setDih(dih);

            byte[] pidBytes = null;

            if(rdData.getPidOptions().getFormat().equals("1")) {
                String wadh = rdData.getPidOptions().getWadh();
                Auth.Pid protoPid = getProtoPid(
                        rdData,
                        tsStr,
                        PID_VER,
                        (wadh==null?"":wadh)
                );

                pidBytes = protoPid.toByteArray();

            } else {
                pidXML = XMLHelper.getInstance().createPidXML(pidBlock);
                Log.e(TAG, "processAndSetPidData: "+pidXML);
                pidBytes = pidXML.getBytes();
            }

            String jhkPubKey = rdData.getUidaiCert();

            if(rdData.getErrCode()==0)try {

                byte[] publicKeybyte = Base64.decode(jhkPubKey,Base64.DEFAULT);
                ByteArrayInputStream is = new ByteArrayInputStream(publicKeybyte);

                byte[] tsBytes = tsStr.getBytes("UTF-8");
                byte[] nonce = Arrays.copyOfRange(tsBytes, tsBytes.length - 12, tsBytes.length);
                byte[] aad = Arrays.copyOfRange(tsBytes,tsBytes.length-16,tsBytes.length);

                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                hmac = digest.digest(pidBytes);

                encdEncrdHmac = Base64.encodeToString(encryptAesGcm(sessionKey,hmac,nonce,aad),Base64.DEFAULT);
                byte[] encrptdData = encryptAesGcm(sessionKey,pidBytes,nonce,aad);

                byte[] packedCipherData = new byte[encrptdData.length + tsBytes.length];
                System.arraycopy(tsBytes, 0,packedCipherData, 0,tsBytes.length);
                System.arraycopy(encrptdData, 0, packedCipherData, tsBytes.length,encrptdData.length);

                encryptedEncodedPidXML = Base64.encodeToString(packedCipherData,Base64.DEFAULT);

                CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                X509Certificate cert = (X509Certificate) certFactory.generateCertificate(is);
                is.close();
                publicKey_signSkey = cert.getPublicKey();
                certExpiryDate = cert.getNotAfter();

                if(new Date().after(certExpiryDate)){
                    rdData.setErrCode(740);
                    return;
                }

                SimpleDateFormat ciDateFormat = new SimpleDateFormat("yyyyMMdd",new Locale("EN","IN"));
                ciDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                ci = ciDateFormat.format(certExpiryDate);

                Cipher pkCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
                pkCipher.init(Cipher.ENCRYPT_MODE, publicKey_signSkey);
                encryptedSessionKey = pkCipher.doFinal(sessionKey);

                rdData.setErrCode(0);

                rdData.getPidData()
                        .setCi(ci)
                        .setSkey(Base64.encodeToString(encryptedSessionKey, Base64.DEFAULT))
                        .setHmac(encdEncrdHmac)
                        .setEncryptedEncodedPid(encryptedEncodedPidXML);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException | TransformerException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private Auth.Pid getProtoPid(RDData rdData,String ts,String ver,String wadh){
        Pid pid = rdData.getPidData().getPid();
        Auth.Pid.Builder protoPid = Auth.Pid.newBuilder();
        protoPid.setTs(ts)
                .setVer(ver)
                .setWadh(wadh);
        if(rdData.getPidOptions().hasDemo()){
            Demo demo = rdData.getPidOptions().getDemo();
            Auth.Demo.Builder protoDemo = Auth.Demo.newBuilder();
            if(demo.getLang()!=null)protoDemo.setLang(Auth.LangCode.valueOf(demo.getLang()));
            if(demo.hasPi()){
                Auth.Pi.Builder pi = Auth.Pi.newBuilder();
                if(demo.getPims()!=null)pi.setMs(demo.getPims().equals("P")?Auth.Ms.P:Auth.Ms.E);
                if(demo.getPimv()!=null)pi.setMv(demo.getPimv()
                        .matches("^[0-9]{1,3}$")?
                        Integer.parseInt(demo.getPimv()):100);
                if(demo.getName()!=null)pi.setName(demo.getName());
                if(demo.getLname()!=null)pi.setLname(demo.getLname());
                if(demo.getPilmv()!=null)pi.setLmv(demo.getPilmv().matches("^[0-9]{1,3}$")
                        ?Integer.parseInt(demo.getPilmv()):100);
                if(demo.getGender()!=null)pi.setGender(Auth.Pi.Gender.valueOf(demo.getGender()));
                if(demo.getDob()!=null)pi.setDob(
                        demo.getDob().matches("^[0-9]{4}+-+[0-9]{2}+-+[0-9]{2}$")?
                                Auth.Dob.newBuilder()
                                        .setYear(Integer.parseInt(demo.getDob().substring(0,4)))
                                        .setMonth(Integer.parseInt(demo.getDob().substring(5,7)))
                                        .setDay(Integer.parseInt(demo.getDob().substring(8,10)))
                                        .build():
                                demo.getDob().matches("^[0-9]{4}$")?
                                        Auth.Dob.newBuilder()
                                                .setYear(Integer.parseInt(demo.getDob()))
                                                .build():
                                        Auth.Dob.newBuilder().build()
                );
                if(demo.getDobt()!=null)pi.setDobt(Auth.Pi.Dobt.valueOf(demo.getDobt()));
                if(demo.getAge()!=null)pi.setAge(demo.getAge().matches("^[0-9]{1,3}$")?
                        Integer.parseInt(demo.getAge()):0
                );
                if(demo.getPhone()!=null)pi.setPhone(demo.getPhone());
                if(demo.getEmail()!=null)pi.setEmail(demo.getEmail());
                protoDemo.setPi(pi.build());
            }

            if(demo.hasPa()){
                Auth.Pa.Builder pa = Auth.Pa.newBuilder();
                if(demo.getPams()!=null)pa.setMs(demo.getPams().equals("P")?Auth.Ms.P:Auth.Ms.E);
                if(demo.getCo()!=null)pa.setCo(demo.getCo());
                if(demo.getHouse()!=null)pa.setHouse(demo.getHouse());
                if(demo.getStreet()!=null)pa.setStreet(demo.getStreet());
                if(demo.getLm()!=null)pa.setLm(demo.getLm());
                if(demo.getLoc()!=null)pa.setLoc(demo.getLoc());
                if(demo.getVtc()!=null)pa.setVtc(demo.getVtc());
                if(demo.getSubdist()!=null)pa.setSubdist(demo.getSubdist());
                if(demo.getDist()!=null)pa.setDist(demo.getDist());
                if(demo.getState()!=null)pa.setState(demo.getState());
                if(demo.getCountry()!=null)pa.setCountry(demo.getCountry());
                if(demo.getPc()!=null)pa.setPc(demo.getPc());
                if(demo.getPo()!=null)pa.setPo(demo.getPo());
                protoDemo.setPa(pa.build());
            }

            if(demo.hasPfa()){
                Auth.Pfa.Builder pfa = Auth.Pfa.newBuilder();
                if(demo.getPfams()!=null)pfa.setMs(Auth.Ms.E);
                if(demo.getPfamv()!=null)pfa.setMv(
                        demo.getPfamv().matches("^[0-9]{1,3}$")?
                                Integer.parseInt(demo.getPimv()):
                                100
                );
                if(demo.getAv()!=null)pfa.setAv(demo.getAv());
                if(demo.getLav()!=null)pfa.setLav(demo.getLav());
                if(demo.getPfalmv()!=null)pfa.setLmv(
                        demo.getPfalmv().matches("^[0-9]{1,3}$")?
                                Integer.parseInt(demo.getPfalmv()):
                                100
                );
                protoDemo.setPfa(pfa.build());
            }
            String otp = rdData.getPidOptions().getOtp();
            /*if(otp!=null&&otp.matches("[0-9]{6}"))*/
            protoPid.setPv(
                    Auth.Pv.newBuilder().setOtp(otp==null?"":otp).build()
            );
            protoPid.setDemo(protoDemo.build());
        }

        ArrayList<Bio> bios = rdData.getPidData().getPid().getBios();
        if(bios.size()==ErrorCode.getPoshIndices().length){
            Auth.Bios.Builder protoBios = Auth.Bios.newBuilder();
            int i=0;
            for(Bio bio:bios){
                if(bio==null){
                    rdData.setErrCode(999);
                    break;
                } else {
                    protoBios.addBio(
                            i,
                            Auth.Bio.newBuilder()
                                    .setPosh(Auth.Position.forNumber(bio.getPosh()+1))
                                    .setType(Auth.BioType.valueOf(bio.getType()))
                                    .setContent(ByteString.copyFrom(Base64.decode(bio.getEncodedBiometric(),Base64.DEFAULT)))
                                    .setBs(bio.getBs())
                                    .build()
                    );
                    i++;
                }
            }
            protoPid.setBios(protoBios.setDih(pid.getDih().trim()).build());
        }
        return protoPid.build();
    }

    private byte[] encryptAesGcm(byte[] key, byte[] input, byte[] nonce, byte[] aad) throws
            NoSuchPaddingException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException,
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {

        SecretKeySpec spec = new SecretKeySpec(key,"AES");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128,nonce);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE,spec,gcmParameterSpec);
        cipher.updateAAD(aad);
        return cipher.doFinal(input);
    }

    //35241
    public String getCert(String data,String token){
        System.out.println("getCert: data: "+data);
        String input = data.replace(" ","").replace("\n","");
        String key = getKey(token);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String hash = Base64.encodeToString(md.digest(input.getBytes()),Base64.DEFAULT);
            System.out.println("getCert:\nkey: "+key+"\nhash: "+hash);
            String encrypted = encryptAES256(data,Base64.decode(key,Base64.DEFAULT));
            System.out.println("getCert: encrypted cert: "+encrypted);
            return key.trim().equals(hash.trim())? encrypted:null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String readCert(String input,String key) {

        return decryptAES256(input, Base64.decode(getKey(key), Base64.DEFAULT))
                .replace("-----BEGIN CERTIFICATE-----","")
                .replace("-----END CERTIFICATE-----","");

//        System.out.println("readCert: input: "+input);
//        System.out.println("readCert: key: "+key);
//        System.out.println("readCert: cert: "+cert);
//        return cert;
    }

    private String getKey(String input){
        String imdt = (input.substring(2,42));
        System.out.println("\ngetKey: \nimdt:"+imdt);
        String imdt2 = imdt.substring(32,40)+
                imdt.substring(16,24)+
                imdt.substring(0,8)+
                imdt.substring(24,32)+
                imdt.substring(8,16);
        StringBuilder reverse = new StringBuilder();
        for(char c:imdt2.toCharArray())reverse.insert(0,c);
        return input.substring(0,2)+reverse.toString()+input.substring(42,44);
    }

    public KeyPair createKeys(AlgorithmParameterSpec spec) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(SecurityConstants.TYPE_RSA,
                SecurityConstants.KEYSTORE_PROVIDER_ANDROID_KEYSTORE);

        try {
            keyPairGenerator.initialize(spec);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            return null;
        }
    }

    public static PrivateKey makePrivateKey(byte[] encoded){
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(encoded);
        try {
            return KeyFactory.getInstance(SecurityConstants.TYPE_RSA).generatePrivate(pkcs8EncodedKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getFullUrl(){
//        return "https://certificates2.bioenabletech.com/ws/bioRdServiceApi.php";

       // certificates1
        return  decryptAES256(
                "++PEMb90dCoeWWTdxF7cQ/is2WqkpbNvlIsKP9IROl5cvu4Ns8F+pZSW7trJSNrH1UmtD5JFn3oy5SiHNogXFg==",
                getEncryptionKey("Manish Nandaniya"));

//        return  decryptAES256(
//                "++PEMb90dCoeWWTdxF7cQxb64JN3lDjB6N6cr0988jrJaQ4IELBjLQd20+snJDz2aqMoL2zsI2zAITOYf1IOYA==",
//                getEncryptionKey("Manish Nandaniya"));
    }

    public String getBaseUrl(){
//        return "https://certificates2.bioenabletech.com/";

        //certificates1
        return decryptAES256("++PEMb90dCoeWWTdxF7cQ/is2WqkpbNvlIsKP9IROl7gz2vApcOVcCO207khbLbi",
                getEncryptionKey("Manish Nandaniya"));

//        return decryptAES256("++PEMb90dCoeWWTdxF7cQxb64JN3lDjB6N6cr0988jqcO8bsVVcQ9rbhcS394Pih",
//                getEncryptionKey("Manish Nandaniya"));
    }

    public boolean isMcValid(String mc){
        try {
            return !new Date().after(getMcValidity(mc));
        } catch (Exception e){
            return false;
        }
    }

    public Date getMcValidity(String mc){
        try {
            ByteArrayInputStream is = new ByteArrayInputStream(Base64.decode(mc,Base64.DEFAULT));
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(is);
            is.close();
            return cert.getNotAfter();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}