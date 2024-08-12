package service;

import model.Site;
import util.HttpUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SiteChecker {

    private Map<String, String> previousContentHashes = new HashMap<>();

    public boolean isSiteOnline(Site site) {
        try {
            HttpUtil.getHttpResponse(site.getUrl());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean hasSiteChanged(Site site) {
        try {
            String currentContent = HttpUtil.getHttpResponse(site.getUrl());
            String currentHash = hashContent(currentContent);
            String previousHash = previousContentHashes.get(site.getUrl());

            if (previousHash != null && !previousHash.equals(currentHash)) {
                previousContentHashes.put(site.getUrl(), currentHash);
                return true;
            }

            previousContentHashes.put(site.getUrl(), currentHash);
            return false;
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String hashContent(String content) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(content.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedhash);
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
