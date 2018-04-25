/**
 * Created by PhilipSena on 19-06-2014.
 */

var GLOBALJSON;
$(document).ready(function () {

    function success(json) {
        console.log("success loading news!");
        GLOBALJSON=json;
        showNews(json);

    }

    function error() {
        console.log("error receiving news");
    }


    function showNews(json) {

        showImage(json.news.imgPath);
        showURL(json.news.imgPath);
        showTitle(json.news.title);
        showBrief(json.news.brief);
        showContent(json.news.content);
    }

    function showImage(img) {
        $("#image").attr('src', img);
    }

    function showURL(img) {
        $("url-field").val(img);
    }

    function showTitle(text) {
        $("title-field").val(text);
    }

    function showBrief(text) {
        $("resume-field").val(text);
    }

    function showContent(text) {
        $("news-body-field").val(text);
    }


    ajaxRequest("scripts/single_news.json", null, "get", success, error);


});

$(window).load(function () {

    $('#submit-news').click(function () {


        var sendNews = makeJson();

        function success1() {
            console.log("Noticia editada com sucesso");
        }

        function error1() {
            console.log("Erro na edição da noticia");
        }

        ajaxRequest("scripts/single_news.json", sendNews, "post", success1, error1);

        function makeJson() {


            return (
            {
                "newsId": GLOBALJSON.news.newsId,
                "imgPath": document.getElementById('url-field').value,
                "brief": document.getElementById('resume-field').value,
                "title": document.getElementById('title-field').value,
                "tags": document.getElementById('tags-field').value,
                "show": GLOBALJSON.news.show,
                "authorId": GLOBALJSON.news.authorId,
                "likes": GLOBALJSON.news.likes,
                "reports": GLOBALJSON.news.reports,
                "likeWeight":GLOBALJSON.news.likeWeight,
                "reportWeight": GLOBALJSON.news.reportWeight,
                "content": document.getElementById('news-body-field').value
            });
        }
    })

})

// REQUEST
function ajaxRequest(url, data, method, success, error) {
    return $.ajax({
        url: url,
        data: JSON.stringify(data),
        type: method,
        contentType: "application/json; charset=utf-8",
        xhrFields: {
            withCredentials: true
        }
    }).done(success).fail(error);
}













