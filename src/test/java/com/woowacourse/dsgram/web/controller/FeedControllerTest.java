package com.woowacourse.dsgram.web.controller;

import com.woowacourse.dsgram.service.dto.user.SignUpUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FeedControllerTest extends AbstractControllerTest {
    private SignUpUserRequest signUpUserRequest;
    private String cookie;

    @BeforeEach
    void setUp() {
        signUpUserRequest = createSignUpUser();
        cookie = getCookieAfterSignUpAndLogin(signUpUserRequest);
    }

    @Test
    void 마이페이지이동() {
        requestUserFeed(signUpUserRequest.getNickName(), cookie)
                .expectStatus()
                .isOk();
    }

}
