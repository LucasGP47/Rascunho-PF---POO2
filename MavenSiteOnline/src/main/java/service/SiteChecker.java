package service;

import model.Site;
import util.HttpUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SiteChecker {
    private final Map<String, String> previousHashes = new HashMap<>();

    public boolean isSiteOnline(Site site) {
        try {
            URL url = new URL(site.getUrl());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            return responseCode == 200; 
        } catch (IOException e) {
            return false;
        }
    }

    public boolean hasSiteChanged(Site site) {
        try {
            String currentContent = HttpUtil.getHttpResponse(site.getUrl());
            String currentHash = hashContent(currentContent);
            String previousHash = previousHashes.get(site.getUrl());

            if (previousHash != null && !previousHash.equals(currentHash)) {
            	previousHashes.put(site.getUrl(), currentHash);
                return true;
            }

            previousHashes.put(site.getUrl(), currentHash);
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
  
    private String hashContent(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
