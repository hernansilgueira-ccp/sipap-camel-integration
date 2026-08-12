package py.ucom.sipap.model;

public class Transferencia {

    private String idTransaccion;
    private String payloadFormatIndicator;
    private String pointOfInitiationMethod;

    private MerchantAccountInformation merchantAccountInformation;

    private String merchantCategoryCode;
    private String transactionCurrency;
    private Long transactionAmount;
    private String countryCode;
    private String merchantName;
    private String merchantCity;
    private String crc;

    public Transferencia() {
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(String idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public String getPayloadFormatIndicator() {
        return payloadFormatIndicator;
    }

    public void setPayloadFormatIndicator(String payloadFormatIndicator) {
        this.payloadFormatIndicator = payloadFormatIndicator;
    }

    public String getPointOfInitiationMethod() {
        return pointOfInitiationMethod;
    }

    public void setPointOfInitiationMethod(String pointOfInitiationMethod) {
        this.pointOfInitiationMethod = pointOfInitiationMethod;
    }

    public MerchantAccountInformation getMerchantAccountInformation() {
        return merchantAccountInformation;
    }

    public void setMerchantAccountInformation(
            MerchantAccountInformation merchantAccountInformation) {
        this.merchantAccountInformation = merchantAccountInformation;
    }

    public String getMerchantCategoryCode() {
        return merchantCategoryCode;
    }

    public void setMerchantCategoryCode(String merchantCategoryCode) {
        this.merchantCategoryCode = merchantCategoryCode;
    }

    public String getTransactionCurrency() {
        return transactionCurrency;
    }

    public void setTransactionCurrency(String transactionCurrency) {
        this.transactionCurrency = transactionCurrency;
    }

    public Long getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Long transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantCity() {
        return merchantCity;
    }

    public void setMerchantCity(String merchantCity) {
        this.merchantCity = merchantCity;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }
}