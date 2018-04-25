/**
 * Created by PhilipSena on 22-06-2014.
 */

$(document).ready(function () {
    var $news = $('.news-container');
    console.log("aaaaaa");
    $news.each(function () {
        var news = $(this);
        var id = news.attr('id');
        news.find('.title').attr('href', 'news.html?id=' + id);
        console.log('anchor');
    });
});

