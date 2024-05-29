package tech.reactivemedia.qrsvc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import tech.reactivemedia.qrsvc.constants.QRInfoConstants;
import tech.reactivemedia.qrsvc.entities.QRCodeInfo;
import tech.reactivemedia.qrsvc.exceptions.NoQrFoundForSecretException;
import tech.reactivemedia.qrsvc.services.dtos.QrCodeInfoDTO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;

@Singleton
public class QrService {

    @Inject
    @ConfigProperty(name = "quarkus.http.body.uploads-directory")
    private String qrFolderPath;

    private final Logger logger = Logger.getLogger(QrService.class);

    private ByteArrayOutputStream qrImage(String qrContent) {
        BitMatrix qrMatrix;
        ByteArrayOutputStream baosQrMatrix = null;
        try {
            qrMatrix = new MultiFormatWriter().encode(qrContent, BarcodeFormat.QR_CODE,
                    QRInfoConstants.QR_IMAGE_SIZE, QRInfoConstants.QR_IMAGE_SIZE);
            baosQrMatrix = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(qrMatrix, QRInfoConstants.QR_IMAGE_TYPE, baosQrMatrix);
        } catch (WriterException | IOException e) {
            logger.error("Unable to generate QR_CODE", e);
        }
        return Objects.requireNonNull(baosQrMatrix, "No image found");
    }

    private Function<String, byte[]> createQrCode() {
        return qrSecretFromUser -> {
            ByteArrayOutputStream baosQrMatrix = qrImage(qrSecretFromUser);
            return Objects.requireNonNull(baosQrMatrix).toByteArray();
        };
    }

    public File showQrImage(String qrSecret) throws IOException {
        if (qrSecret.isEmpty()) {
            throw new IllegalArgumentException("QRSecret is mandatory");
        }
        Path qrImageFolder = Files.createDirectories(Paths.get(qrFolderPath));
        return createQrFile(qrImageFolder.resolve(qrSecret + ".png"), createQrCode().apply(qrSecret));
    }

    public String generateQrSecret() {
        return RandomStringUtils.randomAlphanumeric(QRInfoConstants.QR_SECRET_LENGTH);
    }

    public File createQrFile(Path path, byte[] qrImageAsBytes) throws IOException {
        return Files.write(path, qrImageAsBytes).toFile();
    }

    public File findQrFile(String qrSecret) {

        return Paths.get(qrFolderPath).resolve(qrSecret + ".png").toFile();
    }

    @Transactional
    public QrCodeInfoDTO saveQrInfo(String userId, String qrSecret, String qrUrl, String userInfo) {
        QRCodeInfo qrCodeInfo = new QRCodeInfo();
        qrCodeInfo.qrInfo = userInfo;
        qrCodeInfo.qrImageUrl = qrUrl;
        qrCodeInfo.qrSecret = qrSecret;
        qrCodeInfo.userId = userId;
        QRCodeInfo.persist(qrCodeInfo);

        return new QrCodeInfoDTO(userId, qrSecret, qrUrl, userInfo);
    }

    public QrCodeInfoDTO findByQrSecret(String qrSecret) {
        return QRCodeInfo.findByQrSecret(qrSecret).map(qr -> new QrCodeInfoDTO(qr.userId,
                qr.qrSecret, qr.qrImageUrl, qr.qrInfo)).orElseThrow(NoQrFoundForSecretException::new);
    }

    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
