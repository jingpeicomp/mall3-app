package com.bik.web3.mall3.adapter.github;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Mingo.Liu
 * @date 2022-12-26
 */
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
public class GithubServiceTest {

    @Autowired
    private GithubService githubService;

    @Test
    public void uploadJson() {
        Map<String, Object> body = new HashMap<>(4);
        body.put("image", "11111");
        body.put("name", "test");
        Map<String, Object> attributes = new HashMap<>(4);
        attributes.put("key", "brand");
        attributes.put("value", "blue");
        body.put("attributes", attributes);
        githubService.uploadJson(body);
    }
}