
$(function() {

    setupSession(function(sessionJson) {
        session = sessionJson;
        loadSidebar(session.member);
    });

    $(document).on('click', '.sidebar-channel', function() {
        var channelId = $(this).attr('data-id');
        getChannel(channelId, function(channel) {
            window.location.replace('/smarttv.html?channel=' + channel.id);
        }, function(error) {
            console.log(error);
        });
    });

    initSidebarEventListeners();
    console.log(window.location);
    var id = window.location.search.split("id");
    id = id[1].split("=")[1].split("&")[0];
    console.log(id);

    var request = 'api/news/'+id+'/get';
    ajaxRequest(request,null,'get', success,error);

    function success(json){

        var title = json.news.title;
        var image = json.news.imgPath;
        var tags = json.news.tags;
        var content = json.news.content;

        $.get('templates/news_big.html', function () {

            var tagList = "";

            for(var i = 0; i < tags.length; i++) {

                if(i != 0)
                    tagList += ", ";

                tagList += tags[i].name;
            }

            $('#content-area').loadTemplate('templates/news_big.html',
                {
                    newsTitle: title,
                    content: content,
                    tags: tagList
                }, {
                    prepend: false,
                    beforeInsert: function (element) {
                        element.find('.news-photo-wrap').css({'background-image': 'url(' + image + ')'});
                    }
                });

        });
    }

    function error(){
        console.log('Erro no loading da noticia');
    }
});