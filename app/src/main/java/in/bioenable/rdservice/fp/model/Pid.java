package in.bioenable.rdservice.fp.model;

import android.util.ArrayMap;

import java.util.ArrayList;

/**
 * Created by RND on 3/30/2018.
 */

public class Pid {

    private String ts;
    private String ver;
    private String wadh;
    private String dih;
    private String otp;
    private String pin;

    private ArrayList<Bio> bios = new ArrayList<>();

    private Demo demo;

    public String getTs() {
        return ts;
    }

    public Pid setTs(String ts) {
        this.ts = ts;
        return this;
    }

    public String getVer() {
        return ver;
    }

    public Pid setVer(String ver) {
        this.ver = ver;
        return this;
    }

    public String getWadh() {
        return wadh;
    }

    public Pid setWadh(String wadh) {
        this.wadh = wadh;
        return this;
    }

    public String getDih() {
        return dih;
    }

    public Pid setDih(String dih) {
        this.dih = dih;
        return this;
    }

    public String getOtp() {
        return otp;
    }

    public Pid setOtp(String otp) {
        this.otp = otp;
        return this;
    }

    public String getPin() {
        return pin;
    }

    public Pid setPin(String pin) {
        this.pin = pin;
        return this;
    }

    public ArrayList<Bio> getBios() {
        return bios;
    }

    public Demo getDemo() {
        return demo;
    }

    public Pid setDemo(Demo demo) {
        this.demo = demo;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder biosSB = new StringBuilder();
        for(Bio bio:bios) biosSB.append(bio.toString()).append("\n");
        return "Pid : {\n\tts : "+ts+
                "\nver : "+ver+
                "\nwadh : "+wadh+
                "\ndih : "+dih+
                "\notp : "+otp+
                "\npin : "+pin+
                "\nbios : "+biosSB.toString()+
                "\ndemo : "+demo.toString()+
                "\n}";
    }
}
