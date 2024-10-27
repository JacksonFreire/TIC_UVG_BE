package com.uvg.digital.service;

import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
public class BlobStorageService {

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.account-key}")
    private String accountKey;

    @Value("${azure.storage.blob-endpoint}")
    private String endpoint;

    public String uploadImage(byte[] imageBytes, String containerName, String fileName) throws IOException {
        var blobClient = new BlobClientBuilder()
            .connectionString(String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net", accountName, accountKey))
            .containerName(containerName)
            .blobName(fileName)
            .buildClient();

        // Subir la imagen al contenedor especificado
        blobClient.upload(new ByteArrayInputStream(imageBytes), imageBytes.length, true);
        blobClient.setHttpHeaders(new BlobHttpHeaders().setContentType("image/jpeg"));

        // Retornar la URL de la imagen
        return blobClient.getBlobUrl();
    }

    public void deleteImage(String containerName, String fileName) {
        var blobClient = new BlobClientBuilder()
            .connectionString(String.format("DefaultEndpointsProtocol=https;AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net", accountName, accountKey))
            .containerName(containerName)
            .blobName(fileName)
            .buildClient();

        blobClient.delete();
    }
}
