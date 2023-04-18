package in.bioenable.rdservice.fp.model;

public class Bio {

    private String type = "";
    private String type2 = "";
    private String bs = "";
    private String encodedBiometric;
    private String qScore = "";
    private String nmPoints = "";

    public String getType2() {
        return type2;
    }

    public Bio setType2(String type2) {
        this.type2 = type2;
        return this;
    }

    private int posh;

    public String getType() {
        return type;
    }

    public Bio setType(String type) {
        this.type = type;
        return this;
    }

    public String getBs() {
        return bs;
    }

    public Bio setBs(String bs) {
        this.bs = bs;
        return this;
    }

    public String getqScore() {
        return qScore;
    }

    public Bio setqScore(String qScore) {
        this.qScore = qScore;
        return this;
    }

    public String getNmPoints() {
        return nmPoints;
    }

    public Bio setNmPoints(String nmPoints) {
        this.nmPoints = nmPoints;
        return this;
    }


    public String getEncodedBiometric() {
        return encodedBiometric;
    }

    public Bio setEncodedBiometric(String encodedBiometric) {
        this.encodedBiometric = encodedBiometric;
        return this;
    }

    public int getPosh() {
        return posh;
    }

    public Bio setPosh(int posh) {
        this.posh = posh;
        return this;
    }
}

