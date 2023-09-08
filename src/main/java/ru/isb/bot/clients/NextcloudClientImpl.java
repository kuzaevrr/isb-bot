package ru.isb.bot.clients;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.isb.bot.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

@Log4j2
@Component
public class NextcloudClientImpl implements NextcloudClient {

    @Value("${nextcloud.host}")
    private String NEXTCLOUD_HOST;

    @Value("${nextcloud.user.name}")
    private String NEXTCLOUD_USER_NAME;

    @Value("${nextcloud.user.pass}")
    private String NEXTCLOUD_USER_PASS;

    @Value("${nextcloud.path.to.file}")
    private String NEXTCLOUD_PATH_TO_FILE;
    private static final String NEXTCLOUD_URL_PATH = "/remote.php/dav/files/isb/";

    @SneakyThrows
    public void uploadFile(File file, String fileName) {
        fileName = StringUtils.replaceSpace(fileName);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(NEXTCLOUD_HOST + NEXTCLOUD_URL_PATH + NEXTCLOUD_PATH_TO_FILE + "/" + fileName);
            String encodedCredentials = createCredentials(request);
            FileEntity fileEntity = createEntityFile(request, file);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                log.info("File upload status code: " + statusCode);

                if (statusCode == 204) {
                    uploadFile(file, StringUtils.copyFileName(fileName));
                } else if (statusCode == 302) {
                    redirect(response, fileEntity, encodedCredentials, httpClient);
                }
            }
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    @SneakyThrows
    private void redirect(CloseableHttpResponse response, FileEntity fileEntity, String encodedCredentials, CloseableHttpClient httpClient) {
        String redirectUrl = response.getFirstHeader("Location").getValue();
        log.info("Redirect URL: " + redirectUrl);

        HttpPut redirectRequest = new HttpPut(redirectUrl);
        redirectRequest.addHeader("Authorization", "Basic " + encodedCredentials);
        redirectRequest.setEntity(fileEntity);

        try (CloseableHttpResponse redirectResponse = httpClient.execute(redirectRequest)) {
            StatusLine redirectStatusLine = redirectResponse.getStatusLine();
            int redirectStatusCode = redirectStatusLine.getStatusCode();
            log.info("Redirect status code: " + redirectStatusCode);
        }
    }

    private String createCredentials(HttpPut request) {
        String credentials = NEXTCLOUD_USER_NAME + ":" + NEXTCLOUD_USER_PASS;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        request.addHeader("Authorization", "Basic " + encodedCredentials);
        return encodedCredentials;
    }

    private FileEntity createEntityFile(HttpPut request, File file) {
        FileEntity fileEntity = new FileEntity(file, ContentType.DEFAULT_BINARY);
        request.setEntity(fileEntity);
        return fileEntity;
    }
}