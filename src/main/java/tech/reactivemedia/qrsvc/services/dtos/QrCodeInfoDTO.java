package tech.reactivemedia.qrsvc.services.dtos;

import com.fasterxml.jackson.annotation.JsonCreator;

public class QrCodeInfoDTO {
    private String userId;
    private String qrSecret;
    private String qrImageUrl;
    private String qrInfo;

    public QrCodeInfoDTO() {
    }

    @JsonCreator
    public QrCodeInfoDTO(String userId, String qrSecret, String qrImageUrl, String qrInfo) {
        this.userId = userId;
        this.qrSecret = qrSecret;
        this.qrImageUrl = qrImageUrl;
        this.qrInfo = qrInfo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQrSecret() {
        return qrSecret;
    }

    public void setQrSecret(String qrSecret) {
        this.qrSecret = qrSecret;
    }

    public String getQrImageUrl() {
        return qrImageUrl;
    }

    public void setQrImageUrl(String qrImageUrl) {
        this.qrImageUrl = qrImageUrl;
    }

    public String getQrInfo() {
        return qrInfo;
    }

    public void setQrInfo(String qrInfo) {
        this.qrInfo = qrInfo;
    }
}
