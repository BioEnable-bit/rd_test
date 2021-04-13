package in.bioenable.rdservice.fp.helper;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.security.auth.x500.X500Principal;

import static in.bioenable.rdservice.fp.helper.CryptoUtil.SAMPLE_ALIAS;

/**
 * Created by RND on 9/22/2018.
 */

public class AndroidSecurityUtil {

    private static final String CN = "Pradeep Kumar Bhatia";
    private static final String OU = "Bioenable Technologies Private Limited";
    private static final String O = "Management";
    private static final String L = "Pune";
    private static final String ST = "Maharastra";
    private static final String C = "IN";

    private static final String CN_S = "BioEnable" ;
    private static final String OU_S = "BioEnable" ;
    private static final String O_S = "BIOENABLERDPOC" ;
    private static final String L_S = "Pune" ;
    private static final String ST_S = "Maharastra" ;
    private static final String C_S = "IN";

    public static AlgorithmParameterSpec getSpec(Context context, String env){
        String company, ou, org, locality, state, country;
        if (env.equals("S")) {// TODO: 28-05-2018
            company = CN_S;
            ou = OU_S;
            org = O_S;
            locality = L_S;
            state = ST_S;
            country  = C_S;
        } else {
            company = CN;
            ou = OU;
            org = O;
            locality = L;
            state = ST;
            country = C;
        }

        Calendar start = new GregorianCalendar();
        Calendar end = new GregorianCalendar();

        end.add(Calendar.MONTH, 1);  // MG : it could return 31 instead of 30

        X500Principal principal = new X500Principal(
                "CN=" + company +
                        " OU=" + ou +
                        ", O=" + org +
                        ", L=" + locality +
                        ", ST=" + state +
                        ", C=" + country);

        BigInteger bigInt = BigInteger.valueOf(new Random(System.currentTimeMillis()).nextLong());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Below Android M, use the KeyPairGeneratorSpec.Builder.
            return new KeyPairGeneratorSpec.Builder(context)
                    .setAlias(SAMPLE_ALIAS)
                    .setSubject(principal)
                    .setSerialNumber(bigInt)
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();
        } else {
            // On Android M or above, use the KeyGenparameterSpec.Builder and specify permitted
            // properties  and restrictions of the key.
            return new KeyGenParameterSpec.Builder(SAMPLE_ALIAS, KeyProperties.PURPOSE_SIGN)
                    .setCertificateSubject(principal)
                    .setDigests(KeyProperties.DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .setCertificateSerialNumber(bigInt)
                    .setCertificateNotBefore(start.getTime())
                    .setCertificateNotAfter(end.getTime())
                    .build();
        }
    }
}
