package in.bioenable.rdservice.fp.model;

import static in.bioenable.rdservice.fp.model.Config.isFaceSupported;
import static in.bioenable.rdservice.fp.model.Config.isFingerPrintsSupported;
import static in.bioenable.rdservice.fp.model.Config.isIrisSupported;
import static in.bioenable.rdservice.fp.model.Config.PID_VER;

public class ErrorCode {

    public static String[] POSHES = {
            /*0*/"LEFT_IRIS",
            /*1*/"RIGHT_IRIS",
            /*2*/"LEFT_INDEX",
            /*3*/"LEFT_LITTLE",
            /*4*/"LEFT_MIDDLE",
            /*5*/"LEFT_RING",
            /*6*/"LEFT_THUMB",
            /*7*/"RIGHT_INDEX",
            /*8*/"RIGHT_LITTLE",
            /*9*/"RIGHT_MIDDLE",
            /*10*/"RIGHT_RING",
            /*11*/"RIGHT_THUMB",
            /*12*/"UNKNOWN",
            /*13*/"FACE"
    };

    private static int[] fPoshes = null;

    public static int[] getPoshIndices(){
        return fPoshes;
    }

    public static String getErrorInfo(int errorCode) {

        String massage;

        switch (errorCode) {

            case 100:
                massage = "Invalid PidOptions input. XML should strictly adhere to spec.";
                break;
            case 110:
                massage = "Invalid value for fType";
                break;
            case 120:
                massage = "Invalid value for fCount";
                break;
            case 130:
                massage = "Invalid value for iType";
                break;
            case 140:
                massage = "Invalid value for iCount";
                break;
            case 150:
                massage = "Invalid value for pidVer";
                break;
            case 160:
                massage = "Invalid value for timeout";
                break;
            case 170:
                massage = "Invalid value for posh";
                break;
            case 180:
                massage = "Face matching is not supported";
                break;
            case 190:
                massage = "Invalid value for format";
                break;
            case 200:
                massage = "Invalid Demo structure";
                break;
            case 210:
                massage = "Protobuf format not supported";
                break;
            case 700:
                massage = "Capture timed out";
                break;
            case 710:
                massage = "Being used by another application";
                break;
            case 720:
                massage = "Device not ready";
                break;
            case 730:
                massage = "Capture Failed";
                break;
            case 740:
                massage = "Device needs to be re-initialized";
                break;
            case 750:
                massage = "RD Service does not support fingerprints";
                break;
            case 760:
                massage = "RD Service does not support Iris";
                break;
            case 770:
                massage = "Invalid URL";
                break;
            case 999:
                massage = "Internal error";
                break;

            default:
                massage = "Invalid error code";
                break;
        }
        return massage;
    }

    public static int getPidOptionsErrorCode(PidOptions pidOptions){

        String format = pidOptions.getFormat();

        if(!"0".equals(format)&&!"1".equals(format))return 190;

        if(!PID_VER.equals(pidOptions.getPidVer()))return 150;

        int err = 0;

        String fc = pidOptions.getfCount();
        String ft = pidOptions.getfType();
        String ic = pidOptions.getiCount();
        String it = pidOptions.getiType();
        String pc = pidOptions.getpCount();
        String pt = pidOptions.getpType();
        String otp = pidOptions.getOtp();

        err = getCountTypeErr(fc,ft,ic,it,pc,pt,otp);

        if(err!=0)return err;

        if(!(pidOptions.getTimeout()==null||pidOptions.getTimeout().isEmpty())){
            err =
                    isNaturalInt(pidOptions.getTimeout())?
                            Integer.parseInt(pidOptions.getTimeout())==0?
                                    160:
                                    0:
                            160;
        }

        if(err!=0)return err;

        String posh = pidOptions.getPosh();

        err = getPoshErr(
                isNaturalInt(fc)?Integer.parseInt(fc):0,
                isNaturalInt(ic)?Integer.parseInt(ic):0,
                isNaturalInt(pc)?Integer.parseInt(pc):0,
                posh);

        if(err!=0) return err;

        err = getEnvironmentError(pidOptions.getEnv());

        return err;
    }

    private static int getCountTypeErr(String fc, String ft, String ic, String it, String pc, String pt, String otp){

        String natNum = "[0-9]{1,2}";

        boolean nfc = fc!=null&&fc.matches(natNum);
        boolean nft = ft!=null&&ft.matches(natNum);
        boolean nic = ic!=null&&ic.matches(natNum);
        boolean nit = it!=null&&it.matches(natNum);
        boolean npc = pc!=null&&pc.matches(natNum);
        boolean npt = pt!=null&&pt.matches(natNum);

        if(!nft&&ft!=null&&!ft.isEmpty())return 110;
        if(!nfc&&fc!=null&&!fc.isEmpty())return 120;
        if(!nit&&it!=null&&!it.isEmpty())return 130;
        if(!nic&&ic!=null&&!ic.isEmpty())return 140;
        if((!npt&&pt!=null&&!pt.isEmpty())||(!npc&&pc!=null&&!pc.isEmpty()))return 100;

        boolean o = otp!=null&&!otp.isEmpty();
        boolean f = nfc&&nft&&(Integer.parseInt(fc)>0||Integer.parseInt(ft)>0);
        boolean i = nic&&nit&&(Integer.parseInt(ic)>0||Integer.parseInt(it)>0);
        boolean p = npc&&npt&&(Integer.parseInt(pc)>0||Integer.parseInt(pt)>0);

        int switcher = o?(f?(i?(p?0:1):(p?2:3)):(i?(p?4:5):(p?6:7))):(f?(i?(p?8:9):(p?10:11)):(i?(p?12:13):(p?14:15)));

        int err = 0;
        switch(switcher){
            case 0://otp+finger+iris+face
                //more than two modalities are not allowed
                err = 100;
                break;
            case 1://otp+finger+iris
                err = validateFPair(fc,ft);
                if(err==0)err = validateIPair(ic,it);
                break;
            case 2://otp+finger+face
                err = validateFPair(fc,ft);
                if(err==0)err = validatePPair(pc,pt);
                break;
            case 3://otp+finger
                err = validateFPair(fc,ft);
                break;
            case 4://otp+iris+face
                err = validateIPair(ic,it);
                if(err==0)err = validatePPair(pc,pt);
                break;
            case 5://otp+iris
                err = validateIPair(ic,it);
                break;
            case 6://otp+face
                err = validatePPair(pc,pt);
                break;
            case 7://otp
                //otp alone for App is not allowed
                err = 120;
                break;
            case 8://finger+iris+face
                //more than two modalities are not allowed
                err = 100;
                break;
            case 9://finger+iris
                err = validateFPair(fc,ft);
                if(err==0)err = validateIPair(ic,it);
                break;
            case 10://finger+face
                err = validateFPair(fc,ft);
                if(err==0)err = validatePPair(pc,pt);
                break;
            case 11://finger
                err = validateFPair(fc,ft);
                break;
            case 12://iris+face
                err = validateIPair(ic,it);
                if(err==0)err = validatePPair(pc,pt);
                break;
            case 13://iris
                err = validateIPair(ic,it);
                break;
            case 14://face
                //face alone not allowed
                err = 100;
                break;
            case 15://none
                err = 100;
                break;
            default:
                err = 100;
                break;
        }

        System.out.println("switcher: "+switcher);

        return err;
    }

    private static int validateFPair(String fc, String ft){
        int errorCode = isFingerPrintsSupported?0:750;
        if(errorCode!=0)return errorCode;

        int fcn = Integer.parseInt(fc);
        int ftn = Integer.parseInt(ft);

        errorCode = ftn==0||ftn==1?0:110;
//        errorCode = ftn==0?0:110;
        if(errorCode!=0)return errorCode;

        errorCode = fcn>0&&fcn<=10?0:120;
        if(errorCode!=0)return errorCode;

        return errorCode;
    }

    private static int validateIPair(String ic, String it){
        int errorCode = isIrisSupported?0:760;
        if(errorCode!=0)return errorCode;

        int icn = Integer.parseInt(ic);
        int itn = Integer.parseInt(it);

        errorCode = itn==0?0:130;
        if(errorCode!=0)return errorCode;

        errorCode = icn>0&&icn<=2?0:140;
        if(errorCode!=0)return errorCode;

        return errorCode;
    }

    private static int validatePPair(String pc, String pt){
        int errorCode = isFaceSupported?0:180;
        if(errorCode!=0)return errorCode;

        int pcn = Integer.parseInt(pc);
        int ptn = Integer.parseInt(pt);

        errorCode = ptn==0?0:100;
        if(errorCode!=0)return errorCode;

        errorCode = pcn>1?100:0;
        if(errorCode!=0)return errorCode;

        return errorCode;
    }

    private static int getPoshErr(int fc, int ic, int pc, String input){
        if(input==null||input.isEmpty()||"UNKNOWN".equals(input)){
            fPoshes = new int[fc+ic+pc];
            for(int i=0;i<fc+ic+pc;i++)fPoshes[i]= i<fc+ic?12:13;
            return 0;
        }

        if(!input.matches("^[A-Z]*[A-Z,_]*[A-Z]$"))return 170;
        String[] inPoshes = input.split(",");
        int[] indices = poshIndices(inPoshes);
        boolean isStandard = isStandard(indices);
        if(isStandard){
            int fpc=0,ipc=0,ppc=0;
            for(int i:indices){
                if(i==0||i==1)ipc++;
                if(i>1&&i<13)fpc++;
                if(i==13)ppc++;
            }
            if(fc!=fpc||ic!=ipc||pc!=ppc)return 170;
            if((fc+ic+pc)!=indices.length)return 170;
            fPoshes = normalize(indices);
            return 0;
        } else {
            fPoshes = null;
            return 170;
        }
    }

    private static int[] poshIndices(String[] input){
        if(input==null)return null;
        int[] indices = new int[input.length];
        int i=0;
        for(String posh:input){
            int j=0;
            for(String p: POSHES){
                if(p.matches(posh)){
                    indices[i]=j;
                    break;
                } else indices[i]=-1;
                j++;
            }
            i++;
        }
        return indices;
    }

    private static int getEnvironmentError(String env){
        if(env==null||env.isEmpty()||"P".equals(env)||"PP".equals(env)||"S".equals(env)) return 0;
        else return 100;
    }

    private static int[] normalize(int[] input){
        if(input==null)return null;
        int[] normalized = new int[input.length];
        int i=0;
        for(int p:input)if(p>1&&p<13)normalized[i++]=p;
        if(i<input.length)for(int p:input)if(p==0||p==1)normalized[i++]=p;
        if(i<input.length)for(int p:input)if(p==13)normalized[i++]=p;
        return normalized;
    }

    private static boolean isStandard(int[] indices){
        if(indices==null)return false;
        boolean containsUnknown=false;
        boolean containsFingerPosh=false;
        boolean containsIrisPosh=false;
        boolean containsFacePosh=false;
        for(int p:indices){
            if(!containsUnknown)containsUnknown=p==12;
            if(!containsFingerPosh)containsFingerPosh=(p>1&&p<12);
            if(!containsIrisPosh)containsIrisPosh=p==0||p==1;
            if(!containsFacePosh)containsFacePosh=p==13;
            if(p==-1||
                    containsUnknown&&containsFingerPosh||
                    containsUnknown&&containsIrisPosh||
                    containsUnknown&&containsFacePosh)return false;
        }
        int posh,poshAhead;
        for(int i=0;i<indices.length;i++){
            posh = indices[i];
            for(int j=i+1;j<indices.length;j++){
                poshAhead = indices[j];
                if(poshAhead==-1||posh==poshAhead)return false;
            }
        }
        return true;
    }

    private static boolean isNaturalInt(String str){
        return str!=null&&str.matches("[0-9]{1,5}");
    }
}
