package com.woowacourse.dsgram.web.controller;

import com.woowacourse.dsgram.service.dto.article.ArticleEditRequest;
import com.woowacourse.dsgram.service.dto.user.SignUpUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;


class ArticleApiControllerTest extends AbstractControllerTest {
    private static String COMMON_REQUEST_URL = "/api/articles/{articleId}";

    private String cookie;
    private String anotherCookie;
    private long articleId;

    @BeforeEach
    void setUp() {
        SignUpUserRequest signUpUserRequest = createSignUpUser();

        cookie = getCookieAfterSignUpAndLogin(signUpUserRequest);
        articleId = saveArticle(cookie, "contents");

        signUpUserRequest = createSignUpUser();
        anotherCookie = getCookieAfterSignUpAndLogin(signUpUserRequest);

    }

    @Test
    @DisplayName("게시글 생성 성공")
    void save() {
        requestWithBodyBuilder(createMultipartBodyBuilder("contents"), HttpMethod.POST, "/api/articles", cookie)
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("articles/post/write",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("게시글 1개 조회")
    void findArticle() {
        webTestClient.get().uri(COMMON_REQUEST_URL, articleId)
                .header("Cookie", cookie)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("articles/get/oneArticle",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("페이지별 게시글 조회")
    void findAllArticle() {
        webTestClient.get().uri("/api/articles?page=0")
                .header("Cookie", cookie)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("articles/get/articlesPerPage",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("파일 조회 성공")
    void read() {

        webTestClient.get().uri(COMMON_REQUEST_URL + "/file", articleId)
                .header("Cookie", cookie)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void update() {
        ArticleEditRequest articleEditRequest = new ArticleEditRequest("update contents");

        webTestClient.put().uri(COMMON_REQUEST_URL, articleId)
                .header("Cookie", cookie)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(articleEditRequest), ArticleEditRequest.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("articles/put/updateArticle",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));

        webTestClient.get().uri("/articles/{articleId}", articleId)
                .header("Cookie", cookie)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @DisplayName("다른 사용자에 의한 게시글 수정 실패")
    void update_By_Not_Author() {
        ArticleEditRequest articleEditRequest = new ArticleEditRequest("update contents");

        webTestClient.put().uri(COMMON_REQUEST_URL, articleId)
                .header("Cookie", anotherCookie)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(articleEditRequest), ArticleEditRequest.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void delete() {
        webTestClient.delete().uri(COMMON_REQUEST_URL, articleId)
                .header("Cookie", cookie)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .consumeWith(document("articles/delete/deleteArticle",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));

        webTestClient.get().uri(COMMON_REQUEST_URL, articleId)
                .header("Cookie", cookie)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("다른 사용자에 의한 게시글 삭제 실패")
    void delete_by_Not_Author() {
        webTestClient.delete().uri(COMMON_REQUEST_URL, articleId)
                .header("Cookie", anotherCookie)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @DisplayName("좋아요 추가")
    void like_success() {
        long count = 0;
        moveToArticle(count, cookie, false);
        count = likeOrDisLike(++count, cookie);
        moveToArticle(count, cookie, true);
    }

    @Test
    @DisplayName("좋아요 삭제")
    void dislike_success() {
        long count = 0;
        count = likeOrDisLike(++count, cookie);
        count = likeOrDisLike(--count, cookie);
        moveToArticle(count, cookie, false);
    }

    @Test
    @DisplayName("내좋아요 현황 다른사람이 확인")
    void like_success_by_other() {
        long count = 0;
        count = likeOrDisLike(++count, cookie);

        moveToArticle(count, anotherCookie, false);
    }

    @Test
    void like_by_other_user() {
        long count = 0;
        count = likeOrDisLike(++count, cookie);
        count = likeOrDisLike(++count, anotherCookie);

        moveToArticle(count, anotherCookie, true);
    }

    @Test
    void like() {
        clickLikeButton(cookie)
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(document("like/post/likeOrDislike",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    private void moveToArticle(long count, String cookie, boolean likeState) {
        webTestClient.get().uri(COMMON_REQUEST_URL, articleId)
                .header("Cookie", cookie)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.countOfLikes")
                .isEqualTo(count)
                .jsonPath("$.likeState")
                .isEqualTo(likeState);
    }

    private long likeOrDisLike(long count, String cookie) {
        clickLikeButton(cookie)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$")
                .isEqualTo(count);
        return count;
    }

    private WebTestClient.ResponseSpec clickLikeButton(String cookie) {
        return webTestClient.post().uri(COMMON_REQUEST_URL + "/like", articleId)
                .header("Cookie", cookie)
                .exchange();
    }
}
