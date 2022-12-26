package com.bik.web3.mall3.adapter.github;

import cn.hutool.core.codec.Base64;
import com.bik.web3.mall3.common.utils.ObjectUtils;
import com.bik.web3.mall3.common.utils.generator.CardIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * github api接口
 *
 * @author Mingo.Liu
 * @date 2022-12-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GithubService {

    @Value("${mall3.github.token}")
    private String token;

    private final OkHttpClient httpClient;

    private final CardIdGenerator cardIdGenerator;

    private static final String PATH = "https://api.github.com/repos/jingpeicomp/mall3-nft-meta/contents/2022-12-26/";

    private static final String CDN_PATH = "https://cdn.jsdelivr.net/gh/jingpeicomp/mall3-nft-meta/2022-12-26/";

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static final String GITHUB_API_VERSION_KEY = "X-GitHub-Api-Version";
    private static final String GITHUB_API_VERSION_VALUE = "2022-11-28";


    /**
     * 上传JSON文件到github
     *
     * @param json json文件内容
     * @return 文件url
     */
    public String uploadJson(String json) {
        return uploadJson(json, cardIdGenerator.generate());
    }

    /**
     * 上传JSON meta文件
     *
     * @param jsonMap json内容
     * @return 文件访问URL
     */
    public String uploadJson(Map<String, Object> jsonMap) {
        String json = ObjectUtils.toJson(jsonMap);
        return uploadJson(json);
    }

    /**
     * 上传JSON meta文件
     *
     * @param json   json文件内容
     * @param fileNo 文件编号
     * @return 文件url
     */
    public String uploadJson(String json, Long fileNo) {
        String fileName = fileNo + ".json";
        String content = Base64.encode(json);
        Map<String, String> body = new HashMap<>(4);
        body.put("message", "upload web3 meta");
        body.put("content", content);
        String bodyJson = ObjectUtils.toJson(body);

        RequestBody requestBody = RequestBody.create(bodyJson, JSON_TYPE);
        Request request = new Request.Builder()
                .url(PATH + fileName)
                .addHeader("Authorization", String.format("Bearer %s", token))
                .addHeader("Accept", "application/vnd.github+json")
                .addHeader(GITHUB_API_VERSION_KEY, GITHUB_API_VERSION_VALUE)
                .put(requestBody)
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                log.info("Upload file by github api successfully {}", response.body() != null ? response.body().string() : "");
                return CDN_PATH + fileName;
            } else {
                log.info("Upload file by github api unsuccessfully {}", response.body() != null ? response.body().string() : "");
            }
        } catch (Exception e) {
            log.error("Cannot upload file by github api ", e);
        }
        return "";
    }
}
