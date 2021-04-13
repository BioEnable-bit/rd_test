package in.bioenable.rdservice.fp.model;

import android.content.Context;
import android.os.Build;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import in.bioenable.rdservice.fp.BuildConfig;
import in.bioenable.rdservice.fp.contracts.PersistentStore;

import static java.sql.DriverManager.println;

public class Store implements PersistentStore {

    private Context context;

    public Store(Context ctx){
        this.context = ctx.getApplicationContext();
    }

    private void write(String fileName,String data){
        if(data==null)return;
        try {
            FileOutputStream fos = context.openFileOutput(fileName,Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String read(String fileName){
        try{
            FileInputStream fis = context.openFileInput(fileName);
            StringBuilder sb = new StringBuilder();
            int c;
            while((c=fis.read())!=-1)sb.append((char)c);
            fis.close();
            return sb.toString();
        } catch (IOException e) {
            return null;
        }
    }

    private void delete(String fileName){
        context.deleteFile(fileName);
    }

    @Override
    public void deleteAllEntries(){
        for(String file: context.fileList()){
            context.deleteFile(file);
        }
    }

    @NotNull
    @Override
    public String getInternalVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public int getInternalVersionCode() {
        return BuildConfig.VERSION_CODE;
    }

    @NotNull
    @Override
    public String getOsName() {
        return "Android";
    }

    @NotNull
    @Override
    public String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    @Override
    public String getImei() {
        return read("IMEI");
    }

    @Override
    public void putImei(@NotNull String imei){
        write("IMEI",imei);
    }

    @NotNull
    @Override
    public String getIpAddress(){

        String ip = "";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp()) continue;
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    // *EDIT*
                    if (addr instanceof Inet6Address) continue;
                    ip = addr.getHostAddress();
//                    println(iface.displayName.toString() + " " + ip)
                }
            }
        } catch (Exception ignored) {}
        return ip;
    }

    @Override
    public String getMc(@NotNull String serial, @NotNull String env) {
        String fileName = "MC_"+env+"_"+serial;
        return read(fileName);
    }

    @Override
    public String getDc(@NotNull String serial) {
        String fileName = "DC_"+serial;
        return read(fileName);
    }

    @Override
    public void putMc(@NotNull String serial, @NotNull String env, @NotNull String mc) {
        String fileName = "MC_"+env+"_"+serial;
        write(fileName,mc);
    }

    @Override
    public void putDc(@NotNull String serial,@NotNull String dc) {
        String fileName = "DC_"+serial;
        write(fileName,dc);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public String getUidaiCertificate(@NotNull String env) {
        String fileName = "UC_"+env;
        return read(fileName);
    }

    @Override
    public void putUidaiCertificate(@NotNull String env, @NotNull String cert) {
        String fileName = "UC_"+env;
        write(fileName,cert);
    }

    @Override
    public void putOtpvInfo(@NotNull String serial, @NotNull Object otpvInfo) {
        String fileName = "OTPV_INFO_"+serial;
        serialize(fileName,otpvInfo);
    }

    @Override
    public Object getOtpvInfo(@NotNull String serial) {
        String fileName = "OTPV_INFO_"+serial;
        return deserialize(fileName);
    }

    @Override
    public void putVersionInfo(@NotNull String serial,@NotNull Object versionInfo) {
        String fileName = "VERSION_INFO_"+serial;
        serialize(fileName,versionInfo);
    }

    @Override
    public Object getVersionInfo(@NotNull String serial) {
        String fileName = "VERSION_INFO_"+serial;
        return deserialize(fileName);
    }

    @Override
    public void putActivationInfo(@NotNull String serial, @NotNull Object activationInfo) {
        String fileName = "ACTIVATION_INFO_"+serial;
        serialize(fileName,activationInfo);
    }

    @Override
    public Object getActivationInfo(@NotNull String serial) {
        String fileName = "ACTIVATION_INFO_"+serial;
        return deserialize(fileName);
    }

    private void serialize(String fileName,Object object){
        try {
            FileOutputStream fos = context.openFileOutput(fileName,Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object deserialize(String fileName){
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object object = ois.readObject();
            ois.close();
            fis.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void putSafetyNetAttestation(@NotNull String attestation) {
        write("ATTESTATION",attestation);
    }

    @Override
    public String getSafetyNetAttestation() {
        return read("ATTESTATION");
    }

    @Override
    public void putInitError(@NotNull String serial, @NotNull String errorCode) {
        String fileName = "INIT_ERROR_"+serial;
        write(fileName,errorCode);
    }

    @Nullable
    @Override
    public String getInitError(@NotNull String serial) {
        String fileName = "INIT_ERROR_"+serial;
        return read(fileName);
    }

    @Override
    public void deleteInitError(@NotNull String serial) {
        String fileName = "INIT_ERROR_"+serial;
        delete(fileName);
    }

    @Override
    public void deleteOtpvInfo(@NotNull String serial) {
        String fileName = "OTPV_INFO_"+serial;
        delete(fileName);
    }

    @Override
    public void deleteActivationInfo(@NotNull String serial) {
        String fileName = "ACTIVATION_INFO_"+serial;
        delete(fileName);
    }

    @Override
    public void putActivationLink(String activationLink) {
        write("ACTIVATION_LINK",activationLink);
    }

    @Override
    public String getActivationLink() {
        return read("ACTIVATION_LINK");
    }
}