const ARTICLE_APP = (() => {
    'use strict';

    const ArticleController = function () {
        const articleService = new ArticleService();


        const saveArticle = () => {
            const articleSaveButton = document.getElementById('save-button');
            articleSaveButton ? articleSaveButton.addEventListener('click', articleService.save) : undefined;
        };

        const showThumbnail = () => {
            const imageInput = document.getElementById("file");
            imageInput ? imageInput.addEventListener("change", articleService.changeImageJustOnFront) : undefined;
        };

        const init = () => {
            saveArticle();
            showThumbnail();
        };

        return {
            init: init,
        }
    };

    const ArticleService = function () {
        const connector = FETCH_APP.FetchApi();

        const save = () => {
            const file = document.getElementById('file').value;

            if (file === '') {
                alert('파일은 필수입니다');
                return;
            }

            const postForm = document.getElementById('save-form');
            const formData = new FormData(postForm);

            const redirectToArticlePage = response => {
                response.json().then(articleId => window.location.href = `/articles/${articleId}`);
            };
            connector.fetchTemplate('/api/articles', connector.POST, {}, formData, redirectToArticlePage);
        };

        // TODO User꺼랑 합치기!!
        const changeImageJustOnFront = event => {
            const file = event.target.files[0];
            const reader = new FileReader();
            reader.onload = function (readEvent) {
                const articleImage = document.getElementById('article-image');
                articleImage.src = readEvent.target.result;
            };
            reader.readAsDataURL(file);
        };




        return {
            save: save,
            changeImageJustOnFront: changeImageJustOnFront,
        }
    };

    const init = () => {
        const articleController = new ArticleController();
        articleController.init();
    };

    return {
        init: init
    }
})();

ARTICLE_APP.init();