package datamodel;

public class ECPayData {
    private int printStatus;
    private int plusCarNumber;
    private String merchantID;
    private String companyID;
    private String hashKey;
    private String hashIV;

    public ECPayData(int printStatus, int plusCarNumber, String merchantID, String companyID, String hashKey, String hashIV) {
        this.setPrintStatus(printStatus);
        this.setPlusCarNumber(plusCarNumber);
        this.setMerchantID(merchantID);
        this.setCompanyID(companyID);
        this.setHashKey(hashKey);
        this.setHashIV(hashIV);
    }

    public int getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(int printStatus) {
        this.printStatus = printStatus;
    }

    public int getPlusCarNumber() {
        return plusCarNumber;
    }

    public void setPlusCarNumber(int plusCarNumber) {
        this.plusCarNumber = plusCarNumber;
    }

    public String getMerchantID() {
        return merchantID;
    }

    public void setMerchantID(String merchantID) {
        this.merchantID = merchantID;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public String getHashIV() {
        return hashIV;
    }

    public void setHashIV(String hashIV) {
        this.hashIV = hashIV;
    }
}
