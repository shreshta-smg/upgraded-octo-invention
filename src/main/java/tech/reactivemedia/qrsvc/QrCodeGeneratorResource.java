package tech.reactivemedia.qrsvc;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import tech.reactivemedia.qrsvc.services.QrService;
import tech.reactivemedia.qrsvc.services.dtos.QrRequestDTO;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Path("/qr-code")
public class QrCodeGeneratorResource {

    private final QrService qrService;

    public QrCodeGeneratorResource(QrService qrService) {
        this.qrService = qrService;
    }

    @GET
    @Path("/qr-secret")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateQRSecret() {
        return Response.ok(Map.of("qrSecret", qrService.generateQrSecret())).build();
    }

    @POST
    @Path("/user/{userId}/{qrSecret}/qr-img")
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateQRImage(@Context UriInfo uriInfo,
            @PathParam(value = "qrSecret") String qrSecret, @PathParam(value = "userId") String userId,
            QrRequestDTO qrRequestDTO) throws IOException {
        qrService.showQrImage(qrSecret);
        String qrImagePath = uriInfo.getRequestUri().resolve(qrSecret).toString();
        return Response.ok(qrService.saveQrInfo(userId, qrSecret, qrImagePath,
                qrService.objectMapper().writeValueAsString(qrRequestDTO.body()))).build();
    }

    @GET
    @Path("/user/{userId}/{qrSecret}/qr-img")
    @Produces("image/png")
    public Response displayQRCode(@PathParam(value = "qrSecret") String qrSecret,
            @PathParam(value = "userId") String userId) {
        File qrFile = qrService.findQrFile(qrSecret);
        return Response.ok(qrFile).build();
    }
}
