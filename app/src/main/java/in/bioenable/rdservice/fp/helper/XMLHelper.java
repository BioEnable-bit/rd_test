package in.bioenable.rdservice.fp.helper;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import in.bioenable.rdservice.fp.model.Bio;
import in.bioenable.rdservice.fp.model.BioEnablePacketRequest;
import in.bioenable.rdservice.fp.model.BioEnablePacketResponse;
import in.bioenable.rdservice.fp.model.Demo;
import in.bioenable.rdservice.fp.model.ErrorCode;
import in.bioenable.rdservice.fp.model.Pid;
import in.bioenable.rdservice.fp.model.PidOptions;
import in.bioenable.rdservice.fp.model.RDData;
import in.bioenable.rdservice.fp.model.RDServiceInfo;

import static in.bioenable.rdservice.fp.model.Config.CAPTURE_CALL;
import static in.bioenable.rdservice.fp.model.Config.DP_ID;
import static in.bioenable.rdservice.fp.model.Config.INFO_CALL;
import static in.bioenable.rdservice.fp.model.Config.MODEL_ID;
import static in.bioenable.rdservice.fp.model.Config.RDS_ID;
import static in.bioenable.rdservice.fp.model.Config.RDS_VER;
import static in.bioenable.rdservice.fp.model.Config.isSerialValid;

import android.util.Log;

/**
 * Created by RND on 3/21/2018.
 */

public class XMLHelper {

    private static XMLHelper instance = new XMLHelper();

    private XMLHelper(){}

    public static XMLHelper getInstance(){
        return instance;
    }

    private Document getDeviceInfoDocument(RDData rdData) throws ParserConfigurationException {

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Element rootElement = document.createElement("DeviceInfo");
        document.appendChild(rootElement);
        boolean isv = isSerialValid(rdData.getSerial());
        rootElement.setAttribute("dpId",isv?DP_ID:"");
        rootElement.setAttribute("rdsId",isv?RDS_ID:"");
        rootElement.setAttribute("rdsVer",isv?RDS_VER:"");
        rootElement.setAttribute("mi",isv?MODEL_ID:"");
        rootElement.setAttribute("dc",rdData.getDc()==null?"":rdData.getDc());
        rootElement.setAttribute("mc",rdData.getMc()==null?"":rdData.getMc());

        Element additionalInfo = document.createElement("additional_info");
        rootElement.appendChild(additionalInfo);

        Element param1 = document.createElement("Param");
        additionalInfo.appendChild(param1);
        param1.setAttribute("name","srno");
        param1.setAttribute("value",rdData.getSerial()==null?"":rdData.getSerial());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ",
                new Locale("EN","IN"));
        String time = format.format(new Date(System.currentTimeMillis()));
        time = time.substring(0,time.length()-2)+":"+time.substring(time.length()-2,time.length());

        Element param2 = document.createElement("Param");
        additionalInfo.appendChild(param2);
        param2.setAttribute("name","ts");
        param2.setAttribute("value",time.replace(" ","T"));

        return document;
    }

    private Document getRDServiceDocument(String status) throws ParserConfigurationException {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        //Add root element
        Element rdServiceElement = document.createElement("RDService");
        document.appendChild(rdServiceElement);

        rdServiceElement.setAttribute("status",status);
        rdServiceElement.setAttribute("info","BioEnable Technologies.");

        Element deviceInfoInterfaceElement = document.createElement("Interface");
        rdServiceElement.appendChild(deviceInfoInterfaceElement);
        deviceInfoInterfaceElement.setAttribute("id","INFO");
        deviceInfoInterfaceElement.setAttribute("path",INFO_CALL);

        Element captureInterfaceElement = document.createElement("Interface");
        rdServiceElement.appendChild(captureInterfaceElement);
        captureInterfaceElement.setAttribute("id","PROCESS_CALL");
        captureInterfaceElement.setAttribute("path",CAPTURE_CALL);

        return document;
    }

    private Document getPidDataDocument(RDData rdData) throws ParserConfigurationException {
        Document document = null;
        document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rootElement = document.createElement("PidData");
        document.appendChild(rootElement);

        Element opts = document.createElement("Resp");
        rootElement.appendChild(opts);
        int ec = rdData.getErrCode();

        opts.setAttribute("errCode",String.valueOf(ec));
        opts.setAttribute("errInfo", ErrorCode.getErrorInfo(ec));

        if(rdData.getPidOptions()!=null){

            if((rdData.getPidOptions().getfCount()!=null&&!rdData.getPidOptions().getfCount().isEmpty())||
                    (rdData.getPidOptions().getfType()!=null&&!rdData.getPidOptions().getfType().isEmpty())){
                opts.setAttribute("fCount",rdData.getPidOptions().getfCount());
                opts.setAttribute("fType",rdData.getPidOptions().getfType());
            }

            if((rdData.getPidOptions().getiCount()!=null&&!rdData.getPidOptions().getiCount().isEmpty())||
                    (rdData.getPidOptions().getiType()!=null&&!rdData.getPidOptions().getiType().isEmpty())){
                opts.setAttribute("iCount",rdData.getPidOptions().getiCount());
                opts.setAttribute("iType",rdData.getPidOptions().getiType());
            }

            if((rdData.getPidOptions().getpCount()!=null&&!rdData.getPidOptions().getpCount().isEmpty())||
                    (rdData.getPidOptions().getpType()!=null&&!rdData.getPidOptions().getpType().isEmpty())){
                opts.setAttribute("pCount",rdData.getPidOptions().getpCount());
                opts.setAttribute("pType",rdData.getPidOptions().getpType());
            }

            if((rdData.getPidOptions().getPgCount()!=null&&!rdData.getPidOptions().getPgCount().isEmpty())){
                opts.setAttribute("pgCount",rdData.getPidOptions().getPgCount());
            }

            if((rdData.getPidOptions().getpTimeout()!=null&&!rdData.getPidOptions().getpTimeout().isEmpty())){
                opts.setAttribute("pTimeout",rdData.getPidOptions().getpTimeout());
            }
        }

        if(rdData.getErrCode()==0) {

            StringBuilder nmPoints= new StringBuilder();
            StringBuilder qScore = new StringBuilder();

            boolean trim=false;
            ArrayList<Bio> bios = rdData.getPidData().getPid().getBios();
            for(Bio bio:bios){
                int poshIndex = bio.getPosh();
                if(poshIndex>1&&poshIndex<13){
                    trim=true;
                    nmPoints.append(bio.getNmPoints()).append(",");
                    qScore.append(bio.getqScore()).append(",");
                }
            }

            if(trim){
                nmPoints.deleteCharAt(nmPoints.length()-1);
                qScore.deleteCharAt(qScore.length()-1);
            }

            opts.setAttribute("nmPoints",nmPoints.toString());
            opts.setAttribute("qScore",qScore.toString());

            Element skey = document.createElement("Skey");
            rootElement.appendChild(skey);
            skey.setAttribute("ci",rdData.getPidData().getCi());
            skey.setTextContent(rdData.getPidData().getSkey());

            Element hmac = document.createElement("Hmac");
            rootElement.appendChild(hmac);
            hmac.setTextContent(rdData.getPidData().getHmac());

            Element data = document.createElement("Data");
            rootElement.appendChild(data);
            data.setAttribute("type",rdData.getPidOptions().getFormat().equals("1")?"P":"X");
            data.setTextContent(rdData.getPidData().getEncryptedEncodedPid());
        }

        return document;
    }

    private static String getXml(Document document) throws TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
        transformer.setOutputProperty(OutputKeys.INDENT,"yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.getBuffer().toString().replaceAll("\n|\r", "").trim();
    }

    private Document getPidDocument(Pid pid) throws ParserConfigurationException {
        Demo demo = pid.getDemo();
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element rootElement = document.createElement("Pid");
        document.appendChild(rootElement);

        rootElement.setAttribute("ts",pid.getTs());
        rootElement.setAttribute("ver",pid.getVer());
        rootElement.setAttribute("wadh",pid.getWadh()==null?"":pid.getWadh());

        if(demo!=null){
            Element demoElement = document.createElement("Demo");
            rootElement.appendChild(demoElement);
            if(demo.getLang()!=null)demoElement.setAttribute("lang",demo.getLang());

            if(demo.hasPi()){
                Element piElement = document.createElement("Pi");
                demoElement.appendChild(piElement);
                if(demo.getPims()!=null)piElement.setAttribute("ms",demo.getPims());
                if(demo.getPimv()!=null)piElement.setAttribute("mv",demo.getPimv());
                if(demo.getName()!=null)piElement.setAttribute("name",demo.getName());
                if(demo.getLname()!=null)piElement.setAttribute("lname",demo.getLname());
                if(demo.getPilmv()!=null)piElement.setAttribute("lmv",demo.getPilmv());
                if(demo.getGender()!=null)piElement.setAttribute("gender",demo.getGender());
                if(demo.getDob()!=null)piElement.setAttribute("dob",demo.getDob());
                if(demo.getDobt()!=null)piElement.setAttribute("dobt",demo.getDobt());
                if(demo.getAge()!=null)piElement.setAttribute("age",demo.getAge());
                if(demo.getPhone()!=null)piElement.setAttribute("phone",demo.getPhone());
                if(demo.getEmail()!=null)piElement.setAttribute("email",demo.getEmail());
            }

            if(demo.hasPa()){
                Element paElement = document.createElement("Pa");
                demoElement.appendChild(paElement);
                if(demo.getPams()!=null)paElement.setAttribute("ms",demo.getPams());
                if(demo.getCo()!=null)paElement.setAttribute("co",demo.getCo());
                if(demo.getHouse()!=null)paElement.setAttribute("house",demo.getHouse());
                if(demo.getStreet()!=null)paElement.setAttribute("street",demo.getStreet());
                if(demo.getLm()!=null)paElement.setAttribute("lm",demo.getLm());
                if(demo.getLoc()!=null)paElement.setAttribute("loc",demo.getLoc());
                if(demo.getVtc()!=null)paElement.setAttribute("vtc",demo.getVtc());
                if(demo.getSubdist()!=null)paElement.setAttribute("subdist",demo.getSubdist());
                if(demo.getDist()!=null)paElement.setAttribute("dist",demo.getDist());
                if(demo.getState()!=null)paElement.setAttribute("state",demo.getState());
                if(demo.getCountry()!=null)paElement.setAttribute("country",demo.getCountry());
                if(demo.getPc()!=null)paElement.setAttribute("pc",demo.getPc());
                if(demo.getPo()!=null)paElement.setAttribute("po",demo.getPo());
            }

            if(demo.hasPfa()){
                Element pfaElement = document.createElement("Pfa");
                demoElement.appendChild(pfaElement);
                if(demo.getPfams()!=null)pfaElement.setAttribute("ms",demo.getPfams());
                if(demo.getPfamv()!=null)pfaElement.setAttribute("mv",demo.getPfamv());
                if(demo.getAv()!=null)pfaElement.setAttribute("av",demo.getAv());
                if(demo.getLav()!=null)pfaElement.setAttribute("lav",demo.getLav());
                if(demo.getPfalmv()!=null)pfaElement.setAttribute("lmv",demo.getPfalmv());
            }
        }

        Element biosElement = document.createElement("Bios");
        rootElement.appendChild(biosElement);
        biosElement.setAttribute("dih",pid.getDih());

        Log.e("TAG", "getPidDocument: ");

        for(Bio bio:pid.getBios()){
            Element bioElement = document.createElement("Bio");
            biosElement.appendChild(bioElement);
            bioElement.setAttribute("type",bio.getType());
            bioElement.setAttribute("posh",ErrorCode.POSHES[bio.getPosh()]);
            bioElement.setAttribute("bs",bio.getBs());
            bioElement.setTextContent(bio.getEncodedBiometric());
        }


        if(null!=pid.getOtp()&&!pid.getOtp().isEmpty()
//                &&pid.getOtp().matches("[0-9]{6}")
                ){
            Element pvElement = document.createElement("Pv");
            rootElement.appendChild(pvElement);
            pvElement.setAttribute("otp",pid.getOtp());
//            pvElement.setAttribute("pin",pid.getPin());
        }

        return document;
    }

    public RDServiceInfo parseDeviceInfo(String deviceInfoXML) throws ParserConfigurationException, SAXException, IOException {
        final RDServiceInfo rdServiceInfo = new RDServiceInfo();
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        if(deviceInfoXML!=null){
            InputStream is = new ByteArrayInputStream(deviceInfoXML.getBytes(StandardCharsets.UTF_8));
            parser.parse(is,new DefaultHandler(){
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if(qName.equalsIgnoreCase("DeviceInfo")){
                        if(attributes!=null&&attributes.getLength()>0){
                            String dc = attributes.getValue("dc");
                            rdServiceInfo.setDcPresent(dc!=null&&(!dc.isEmpty())&&dc.length()==36);
                            String mc = attributes.getValue("mc");
                            rdServiceInfo.setMcPresent(mc!=null&&(!mc.isEmpty()));
                        }
                    } else if(qName.equalsIgnoreCase("Param")){
                        if(attributes!=null&&attributes.getLength()>0){
                            if(attributes.getValue("name").equals("srno")){
                                rdServiceInfo.setSerial(attributes.getValue("value"));
                            }
                        }
                    }
                }
            });
        }
        return rdServiceInfo;
    }

    public PidOptions parsePidOptions(String pidOptionsXML) throws ParserConfigurationException, SAXException, IOException {
        final PidOptions pidOptions = new PidOptions();
        final Demo demo = new Demo();
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        InputStream is = new ByteArrayInputStream(pidOptionsXML.getBytes(StandardCharsets.UTF_8));
        parser.parse(is,new DefaultHandler(){
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) {
//                super.startElement(uri,localName,qName,attributes);
                if (qName.equalsIgnoreCase("PidOptions")){
                    if (attributes != null && attributes.getLength() > 0) {
                        pidOptions.setVer(attributes.getValue("ver"));
                    }
                } else if (qName.equalsIgnoreCase("Opts")) {
                    pidOptions.hasOpts(true);
                    if (attributes != null && attributes.getLength() > 0) {
                        pidOptions.setEnv(attributes.getValue("env"));
                        pidOptions.setfCount(attributes.getValue("fCount"));
                        pidOptions.setfType(attributes.getValue("fType"));
                        pidOptions.setiCount(attributes.getValue("iCount"));
                        pidOptions.setiType(attributes.getValue("iType"));
                        pidOptions.setpCount(attributes.getValue("pCount"));
                        pidOptions.setpType(attributes.getValue("pType"));
                        pidOptions.setFormat(attributes.getValue("format"));
                        pidOptions.setPidVer(attributes.getValue("pidVer"));
                        pidOptions.setTimeout(attributes.getValue("timeout"));
                        pidOptions.setpTimeout(attributes.getValue("pTimeout"));
                        pidOptions.setPgCount(attributes.getValue("pgCount"));
                        pidOptions.setOtp(attributes.getValue("otp"));
                        pidOptions.setWadh(attributes.getValue("wadh"));
                        pidOptions.setPosh(attributes.getValue("posh"));
//                        String env = attributes.getValue("env");
//                        pidOptions.setEnv(env == null ? "" : env);
//                        String fCount = attributes.getValue("fCount");
//                        pidOptions.setfCount(fCount == null ? "" : fCount);
//                        String fType = attributes.getValue("fType");
//                        pidOptions.setfType(fType == null ? "" : fType);
//                        String iCount = attributes.getValue("iCount");
//                        pidOptions.setiCount(iCount == null ? "" : iCount);
//                        String iType = attributes.getValue("iType");
//                        pidOptions.setiType(iType == null ? "" : iType);
//                        String pCount = attributes.getValue("pCount");
//                        pidOptions.setpCount(pCount == null ? "" : pCount);
//                        String pType = attributes.getValue("pType");
//                        pidOptions.setpType(pType == null ? "" : pType);
//                        String format = attributes.getValue("format");
//                        pidOptions.setFormat(format == null ? "" : format);
//                        String pidVer = attributes.getValue("pidVer");
//                        pidOptions.setPidVer(pidVer == null ? "" : pidVer);
//                        String timeout = attributes.getValue("timeout");
//                        pidOptions.setTimeout(timeout == null ? "" : timeout);
//                        String pTimeout = attributes.getValue("pTimeout");
//                        pidOptions.setTimeout(timeout == null ? "" : pTimeout);
//                        String pgCount = attributes.getValue("pgCount");
//                        pidOptions.setTimeout(timeout == null ? "" : pgCount);
//                        String otp = attributes.getValue("otp");
//                        pidOptions.setOtp(otp == null ? "" : otp);
//                        String wadh = attributes.getValue("wadh");
//                        pidOptions.setWadh(wadh == null ? "" : wadh);
//                        String posh = attributes.getValue("posh");
//                        pidOptions.setPosh(posh == null ? "" : posh);
                    }
                } else if (qName.equalsIgnoreCase("Demo")) {
                    pidOptions.hasDemo(true);
                    if (attributes != null && attributes.getLength() > 0) {
                        demo.setLang(attributes.getValue("lang"));
                    }
                } else if (qName.equalsIgnoreCase("Pi")){
                    demo.hasPi(true);
                    if (attributes != null && attributes.getLength() > 0) {
                        demo.setPims(attributes.getValue("ms"));
                        demo.setPimv(attributes.getValue("mv"));
                        demo.setName(attributes.getValue("name"));
                        demo.setLname(attributes.getValue("lname"));
                        demo.setPilmv(attributes.getValue("lmv"));
                        demo.setGender(attributes.getValue("gender"));
                        demo.setDob(attributes.getValue("dob"));
                        demo.setDobt(attributes.getValue("dobt"));
                        demo.setAge(attributes.getValue("age"));
                        demo.setPhone(attributes.getValue("phone"));
                        demo.setEmail(attributes.getValue("email"));
                    }
                } else if(qName.equalsIgnoreCase("Pa")){
                    demo.hasPa(true);
                    if (attributes != null && attributes.getLength() > 0) {
                        demo.setPams(attributes.getValue("ms"));
                        demo.setCo(attributes.getValue("co"));
                        demo.setHouse(attributes.getValue("house"));
                        demo.setStreet(attributes.getValue("street"));
                        demo.setLm(attributes.getValue("lm"));
                        demo.setLoc(attributes.getValue("loc"));
                        demo.setVtc(attributes.getValue("vtc"));
                        demo.setSubdist(attributes.getValue("subdist"));
                        demo.setDist(attributes.getValue("dist"));
                        demo.setState(attributes.getValue("state"));
                        demo.setCountry(attributes.getValue("country"));
                        demo.setPc(attributes.getValue("pc"));
                        demo.setPo(attributes.getValue("po"));
                    }
                } else if(qName.equalsIgnoreCase("Pfa")){
                    demo.hasPfa(true);
                    if (attributes != null && attributes.getLength() > 0) {
                        demo.setPfams(attributes.getValue("ms"));
                        demo.setPfamv(attributes.getValue("mv"));
                        demo.setAv(attributes.getValue("av"));
                        demo.setLav(attributes.getValue("lav"));
                        demo.setPfalmv(attributes.getValue("lmv"));
                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                super.endElement(uri, localName, qName);
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                super.characters(ch, start, length);
            }
        });
        pidOptions.setDemo(demo);
        return pidOptions;
    }

    public BioEnablePacketResponse parseBioEnablePacketResponse(String bioEnablePacketResponseXML) throws ParserConfigurationException, SAXException, IOException {
        final BioEnablePacketResponse response = BioEnablePacketResponse.getInstance();
        SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
        InputStream is = new ByteArrayInputStream(bioEnablePacketResponseXML.getBytes(StandardCharsets.UTF_8));
        parser.parse(is,new DefaultHandler(){
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if(qName.equalsIgnoreCase("DeviceInfo")){
                    if(attributes!=null&&attributes.getLength()>0){
                        response.setIs_registered(attributes.getValue("is_registered"));
                    }
                } else if(qName.equalsIgnoreCase("RDServiceInfo")){
                    if(attributes!=null&&attributes.getLength()>0){
                        response.setIs_need_to_update(attributes.getValue("is_need_to_update"));
                        response.setNewVersPath(attributes.getValue("NewVersPath"));
                        response.setMd5(attributes.getValue("md5"));
                    }
                } else if(qName.equalsIgnoreCase("DeviceKeyInfo")){
                    if(attributes!=null&&attributes.getLength()>0){
                        response.setKey_validity(attributes.getValue("key_validity"));
                        response.setSigned_device_public_key(attributes.getValue("signed_device_public_key"));
                    }
                } else if(qName.equalsIgnoreCase("UIDAIPublicKeyInfo")){
                    if(attributes!=null&&attributes.getLength()>0) {
                        response.setIs_need_to_update(attributes.getValue("Isneedtoupdate"));
                        String str = attributes.getValue("NewCerPath");
//                                .replace("-----BEGIN CERTIFICATE-----","")
//                                .replace("-----END CERTIFICATE-----","").trim();
                        response.setNewCerPath(str==null?"":str)
                                .setToken(attributes.getValue("token"));
                    }
                } else if(qName.equalsIgnoreCase("Uuid")){
                    if(attributes!=null&&attributes.getLength()>0){
                        response.setUuid_value(attributes.getValue("value"));//todo might be incorrect attribute name to parse
                    }
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {

            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {

            }
        });

        return response;
    }

    public String createBioEnablePacketRequestXML(BioEnablePacketRequest request) {
        String reqXML = null;
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootElement = document.createElement("BioEnablePacketRequest");
            document.appendChild(rootElement);

            Element deviceInfo = document.createElement("DeviceInfo");
            rootElement.appendChild(deviceInfo);

            deviceInfo.setAttribute("DC", request.getDc());
            deviceInfo.setAttribute("status", request.getStatus());
            deviceInfo.setAttribute("model", request.getModel());
            deviceInfo.setAttribute("clientname", request.getClientname());
            deviceInfo.setAttribute("address", request.getAddress());
            deviceInfo.setAttribute("pincode", request.getPincode());
            deviceInfo.setAttribute("mobile_num", request.getMobileNum());
            deviceInfo.setAttribute("imei", request.getImei());
            deviceInfo.setAttribute("command", request.getCommand());

//            deviceInfo.setAttribute("guid", guid);

            Element rdServiceInfo = document.createElement("RDServiceInfo");
            rootElement.appendChild(rdServiceInfo);

            rdServiceInfo.setAttribute("rdsId", request.getRdsId());
            rdServiceInfo.setAttribute("rdsVer", request.getRdsVer());
//            rdServiceInfo.setAttribute("rdsMD5", request.getRdsMD5());
            rdServiceInfo.setAttribute("osId", request.getOsId());
            rdServiceInfo.setAttribute("osVer", request.getOsVer());
            rdServiceInfo.setAttribute("iv", String.valueOf(request.getIv()));
            rdServiceInfo.setAttribute("rdslevel", request.getRdsLevel());
//            rdServiceInfo.setAttribute("lastupdatedon", request.getLastUpdatedOn());

            Element keyInfo = document.createElement("DeviceKeyInfo");
            rootElement.appendChild(keyInfo);
            keyInfo.setAttribute("is_public_key_included", request.getIsPublicKeyIncluded());
            keyInfo.setAttribute("device_public_key", request.getDevicePublicKey());

            Element uidaiPublicKeyInfo = document.createElement("UIDAIPublicKeyInfo");
            rootElement.appendChild(uidaiPublicKeyInfo);
//            uidaiPublicKeyInfo.setAttribute("md5", request.getMd5());
            uidaiPublicKeyInfo.setAttribute("environment", request.getEnvironment());
            uidaiPublicKeyInfo.setAttribute("AndroidReq", request.getAndroidReq());

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            DOMSource source = new DOMSource(document);
            transformer.transform(source, result);
            reqXML = writer.getBuffer().toString().replaceAll("\n|\r", "").trim();
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
        if(reqXML!=null)reqXML = reqXML.replace("&#10;","");
        return reqXML;
    }

    public String createDeviceInfoXML(RDData data) throws ParserConfigurationException, TransformerException {
        return getXml(getDeviceInfoDocument(data));
    }

    public String createPidDataXML(RDData rdData) throws ParserConfigurationException, TransformerException {
        Document document = getPidDataDocument(rdData);
        Element deviceInfoRootElement = getDeviceInfoDocument(rdData).getDocumentElement();
        Node importedDeviceInfoNode = document.importNode(deviceInfoRootElement,true);
        document.getDocumentElement().appendChild(importedDeviceInfoNode);
        return getXml(document);
    }

    public String createPidXML(Pid pid) throws ParserConfigurationException, TransformerException {
        return getXml(getPidDocument(pid));
    }

    public String createRDServiceXML(String status) throws ParserConfigurationException, TransformerException {
        return getXml(getRDServiceDocument(status));
    }

    public static class DeviceInfoBuilder {

        private Document document;
        private Element rootElement,additionalInfo;

        private boolean isReady = false;

        public DeviceInfoBuilder(boolean isReady,String serial) throws ParserConfigurationException {
            this.isReady = isReady;
            serial = serial==null?"":serial;

            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            rootElement = document.createElement("DeviceInfo");
            document.appendChild(rootElement);

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ",
                    new Locale("EN","IN"));
            String time = format.format(new Date(System.currentTimeMillis()));
            time = time.substring(0,time.length()-2)+":"+time.substring(time.length()-2);
            time = time.replace(" ","T");

            setParam("srno",serial);
            setParam("ts",time);

        }

        public DeviceInfoBuilder setDpId(String dpId){
            dpId = dpId==null?"":dpId;
            rootElement.setAttribute("dpId", isReady ?dpId:"");
            return this;
        }

        public DeviceInfoBuilder setRdsId(String rdsId){
            rdsId = rdsId==null?"":rdsId;
            rootElement.setAttribute("rdsId", isReady ?rdsId:"");
            return this;
        }

        public DeviceInfoBuilder setRdsVer(String rdsVer){
            rdsVer = rdsVer==null?"":rdsVer;
            rootElement.setAttribute("rdsVer", isReady ?rdsVer:"");

            return this;
        }

        public DeviceInfoBuilder setMi(String mi) {
            mi = mi==null?"":mi;
            rootElement.setAttribute("mi", isReady ?mi:"");
            return this;
        }

        public DeviceInfoBuilder setDc(String dc) {
            dc = dc==null?"":dc;
            rootElement.setAttribute("dc", isReady ?dc:"");
            return this;
        }

        public DeviceInfoBuilder setMc(String mc) {
            mc = mc==null?"":mc;
            rootElement.setAttribute("mc", isReady ?mc:"");
            return this;
        }

        public DeviceInfoBuilder setParam(String name,String value){
            name = name==null?"":name;
            value = value==null?"":value;
            if(additionalInfo==null){
                additionalInfo = document.createElement("additional_info");
                rootElement.appendChild(additionalInfo);
            }
            Element param = document.createElement("Param");
            additionalInfo.appendChild(param);
            param.setAttribute("name",name);
            param.setAttribute("value",value);
            return this;
        }

        public String build() throws TransformerException {
            return getXml(document);
        }
    }

    public static class PidDataBuilder {

    }
}