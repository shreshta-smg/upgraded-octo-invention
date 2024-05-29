package tech.reactivemedia.qrsvc.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Optional;

@Entity
@Table(name = "qr_code_info")
@Cacheable
public class QRCodeInfo extends PanacheEntity {

    @NotNull
    @NotBlank
    @Column(length = 50, name = "user_id")
    public String userId;

    @NotNull
    @NotBlank
    @Column(length = 15, name = "qr_secret")
    public String qrSecret;

    @Column(name = "qr_image_url")
    public String qrImageUrl;

    @Column(columnDefinition = "JSON", name = "qr_info")
    public String qrInfo;

    public static Optional<QRCodeInfo> findByQrSecret(String qrSecret) {
        return find("qrSecret", qrSecret).firstResultOptional();
    }

}
