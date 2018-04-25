/**
 * Created by Guilherme on 14/06/2014.
 */

$(function () {

    var createdNews;

    function postSuccess() {
        console.log("Successfully posted content.");
    }

    function postFail() {
        console.log("Post failed.");
    }

    /* attach a submit handler to the form */
    $('#submit-news').click(function () {

        createdNews = makeJson();

        /* Send the data using post */
        ajaxRequest('api/news/create', createdNews, 'post', postSuccess, postFail);

    });

    function makeJson() {

        return ({

            "newsId": -1,
            "imgPath": $('#url-field').val(),
            "brief": $('#resume-field').val(),
            "title": $('#title-field').val(),
            "tags": [],
            "show": false,
            "authorId": -1,
            "likes": 0,
            "reports": 0,
            "likeWeight": -1,
            "reportWeight": -1,
            "content": $('#news-body-field').val()
        });
    }

});

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