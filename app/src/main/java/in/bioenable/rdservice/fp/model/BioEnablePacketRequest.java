package in.bioenable.rdservice.fp.model;

import static in.bioenable.rdservice.fp.model.Config.MODEL_ID;
import static in.bioenable.rdservice.fp.model.Config.RDS_ID;
import static in.bioenable.rdservice.fp.model.Config.RDS_VER;

/**
 * Created by RND on 3/21/2018.
 */

public class BioEnablePacketRequest {

    private String dc="";
    private String environment="";
    private String devicePublicKey="";
    private String rdsVer= RDS_VER;//todo might be incorrect with internal version for server usage.
    private String clientname="";
    private String address="";
    private String pincode="";
    private String mobileNum="";
    private String imei="";
    private String osVer="";
    private int iv = 0;
    private String osId = "Android";
    private String status = "ok";
    private String model = MODEL_ID;
    private String command = "register";
    private String rdsId = RDS_ID;
    private String rdsLevel = "L0";
    private String isPublicKeyIncluded = "Y";
    private String androidReq = "Y";

    //    private  String md5 = "9BED11C4C563411F0CC000333BB4AE06";
    //    private  String rdsMD5 = "rdsMD5";
    //    private  String lastUpdatedOn;// = "-";

//    private static BioEnablePacketRequest instance = new BioEnablePacketRequest();

    public BioEnablePacketRequest(){
        reset();
    };

//    public static  BioEnablePacketRequest getInstance() {
//        return instance;
//    }

    public  void reset(){
        environment="";
        dc="";
        devicePublicKey="";
        clientname="";
        address="";
        pincode="";
        mobileNum="";
        imei="";
        osVer="";
        iv = 0;
        System.out.println("BioEnablePacketRequest: resetLastBiometricValues!");
    }

    public String getDc() {
        return dc;
    }

    public BioEnablePacketRequest setDcIeSerial(String dc) {
        this.dc = dc==null?"":dc;
        return this;
    }

    public String getStatus() {
        return status;
    }

//    public BioEnablePacketRequest setStatus(String status) {
//        this.status = status;
//        return this;
//    }

    public String getModel() {
        return model;
    }

//    public BioEnablePacketRequest setModel(String model) {
//        this.model = model;
//        return this;
//    }

    public String getClientname() {
        return clientname;
    }

    public BioEnablePacketRequest setClientname(String clientname) {
        this.clientname = clientname==null?"":clientname;
        return this;
    }

    public String getImei() {
        return imei;
    }

    public BioEnablePacketRequest setImei(String imei) {
        this.imei = imei==null?"":imei;
        return this;
    }

//    public String getCompany() {
//        return company;
//    }
//
//    public BioEnablePacketRequest setCompany(String company) {
//        this.company = company;
//        return this;
//    }

    public String getAddress() {
        return address;
    }

    public BioEnablePacketRequest setAddress(String address) {
        this.address = address==null?"":address;
        return this;
    }

    public String getPincode() {
        return pincode;
    }

    public BioEnablePacketRequest setPincode(String pincode) {
        this.pincode = pincode==null?"":pincode;
        return this;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public BioEnablePacketRequest setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum==null?"":mobileNum;
        return this;
    }

    public String getCommand() {
        return command;
    }

//    public BioEnablePacketRequest setCommand(String command) {
//        this.command = command;
//        return this;
//    }

    public String getRdsId() {
        return rdsId;
    }

//    public BioEnablePacketRequest setRdsId(String rdsId) {
//        this.rdsId = rdsId;
//        return this;
//    }

    public String getRdsVer() {
        return rdsVer;
    }

//    public BioEnablePacketRequest setRdsVer(String rdsVer) {
//        this.rdsVer = rdsVer;
//        return this;
//    }

//    public String getRdsMD5() {
//        return rdsMD5;
//    }

//    public BioEnablePacketRequest setRdsMD5(String rdsMD5) {
//        this.rdsMD5 = rdsMD5;
//        return this;
//    }

    public String getOsId() {
        return osId;
    }

//    public BioEnablePacketRequest setOsId(String osId) {
//        this.osId = osId;
//        return this;
//    }

    public String getOsVer() {
        return osVer;
    }

    public BioEnablePacketRequest setOsVer(String osVer) {
        this.osVer = osVer==null?"":osVer;
        return this;
    }

    public String getRdsLevel() {
        return rdsLevel;
    }

//    public BioEnablePacketRequest setRdsLevel(String rdsLevel) {
//        this.rdsLevel = rdsLevel;
//        return this;
//    }

//    public String getLastUpdatedOn() {
//        return lastUpdatedOn;
//    }

//    public BioEnablePacketRequest setLastUpdatedOn(String lastUpdatedOn) {
//        this.lastUpdatedOn = lastUpdatedOn;
//        return this;
//    }

    public String getIsPublicKeyIncluded() {
        return isPublicKeyIncluded;
    }

//    public BioEnablePacketRequest setIsPublicKeyIncluded(String isPublicKeyIncluded) {
//        this.isPublicKeyIncluded = isPublicKeyIncluded;
//        return this;
//    }

    public String getDevicePublicKey() {
        return devicePublicKey;
    }

    public BioEnablePacketRequest setDevicePublicKey(String devicePublicKey) {
        this.devicePublicKey = devicePublicKey==null?"":devicePublicKey;
        return this;
    }

//    public String getMd5() {
//        return md5;
//    }

//    public BioEnablePacketRequest setMd5(String md5) {
//        this.md5 = md5;
//        return this;
//    }

    public String getEnvironment() {
        return environment;
    }

    public BioEnablePacketRequest setEnvironment(String environment) {
        this.environment = environment==null?"":environment;
        return this;
    }

    public String getAndroidReq() {
        return androidReq;
    }

//    public BioEnablePacketRequest setAndroidReq(String androidReq) {
//        this.androidReq = androidReq;
//        return this;
//    }


    public int getIv() {
        return iv;
    }

    public BioEnablePacketRequest setIv(int iv) {
        this.iv = iv;
        return this;
    }

}
