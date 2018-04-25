


//A sessao do membro
var session = null;

//The current contents session
var contentsSession = null;

//Verifica se o member tem login, ou se é um member default.
function hasLogin() {
    return session.valid;
}

function setNewsBox(votedContent, prepend) {
    var news = votedContent.news;
    createNewsBox(news.title, news.content, news.imgPath, news.likes, votedContent.like, votedContent.report, news.id, news.tags, prepend);
}

function setContents() {
    $('#content-list-title').html(contentsSession.title);
    $(window).bind('scroll', bindScroll);
    $("#news-zone").empty();
    contentsSession.getContents(function(contents) {
        var i = 0;
        for(; i < contents.length; i++) {
            console.log(contents[i].news.title);
            setNewsBox(contents[i], false);
        }
    }, function(error) {
        console.error(error);
    });
}

function addContents() {
    contentsSession.getContents(function(contents) {
        //TIRA LOGO
        if(contents.length < contentsSession.offset) {
            //Mostrar que nao ha mais noticias.
            console.log("No more news");
            $(window).unbind('scroll');
        }

        var i = 0;
        for(; i < contents.length; i++) {
            setNewsBox(contents[i], false);
        }
    }, function(error) {
        console.error(error);
    });
}

function setMember(member) {
    if(hasLogin()) {
        $('#login').html('<img id="login-icon" src="' + member.imgPath + '" alt="login icon">' +
            '<div id="username">' + member.name + '</div>' +
            '<div id="logout">Logout</div>');
    } else {
        $('#login').html('<img id="login-icon" src="' + member.imgPath + '" alt="login icon">' +
            '<div id="username">' + member.name + '</div>');
    }

}

$(function () {
    initEventListeners();

    setupSession(function(sessionJson) {
        session = sessionJson;
        var query = window.location.search.split("query");
        var channel = window.location.search.split("channel");
        if (query.length > 1) {
            query = query[1].split("=")[1].split("&")[0];
            console.log(query);
            $('#search-field').val(query);
            history.pushState(null, null, '/smarttv.html?query=' + query);
            contentsSession = getSearchContentSession(query);
            setContents();
        } else if (channel.length > 1) {
            channel = channel[1].split("=")[1].split("&")[0];
            console.log(channel);
            getChannel(channel, function(queryChannel) {
                history.pushState(null, null, '/smarttv.html?channel=' + queryChannel.id);
                contentsSession = getChannelContentSession(queryChannel);
                setContents();
            }, function(error) {
                console.error(error);
            });
        } else {
            history.pushState(null, null, '/smarttv.html?channel=' + session.member.smartTv.id);
            contentsSession = getChannelContentSession(session.member.smartTv);
            setContents();
        }

        setMember(session.member);
        loadSidebar(session.member);

        $("#logout").click(function() {
            memberLogout(function(msg) {
                console.log(msg);
                window.location.replace("/index.html");
            }, function (error) {
                console.log(error);
            });
        });


    });

    var unexpandedHeight = null;
    var newsBodyDefaultHeight = 200;



    // When expanding button is clicked (the plus icon on top of the box)
    $(document).on('click', '.news-expand-button', function () {

        var $newsBodyContainer = $(this).parent().parent();
        var $newsTextWindow = $newsBodyContainer.find('.news-text-window');

        // Storing the value of the default height
        if(unexpandedHeight == null)
            unexpandedHeight =  $newsTextWindow.innerHeight();

        var expandedHeight = $newsTextWindow.find('.news-text-wrapper').innerHeight();

        console.log("Height needed: " + expandedHeight);

        // if text is overflowing and expanding makes sense and its not already expanded
        if(((expandedHeight - unexpandedHeight) > 0) && !$newsTextWindow.data('expanded')) {

            console.log("Texto nao cabe no container, expansao activada.");

            $newsTextWindow.data('expanded', true);
            $newsBodyContainer.find('.gradientDiv').hide();

            console.log("NewsBodyHeight jumping to " + (newsBodyDefaultHeight + extraHeight($newsTextWindow) + "px") + " (" + newsBodyDefaultHeight + " + " + extraHeight($newsTextWindow) + ")");
            console.log((newsBodyDefaultHeight + extraHeight($newsTextWindow)) + "px");

            $newsBodyContainer.animate({
                'height': (newsBodyDefaultHeight + extraHeight($newsTextWindow)) + "px"
            });

            $newsTextWindow.animate({
                'height': expandedHeight + "px"
            });

        }
        // if already expanded
        else if($newsTextWindow.data('expanded')) {

            $newsTextWindow.data('expanded', false);
            $newsBodyContainer.find('.gradientDiv').show();

            $newsBodyContainer.animate({
                'height': newsBodyDefaultHeight
            });

            $newsTextWindow.animate({
                'height': unexpandedHeight + "px"
            });
        }

        // if expanding makes no sense (already showing up all existent text)
        else {
            console.log("Não é necessária expansao, esta lá tudo.");
        }
    });

    $(document).on('click', '.sidebar-channel', function() {
        var channelId = $(this).attr('data-id');
        getChannel(channelId, function(channel) {
            contentsSession = getChannelContentSession(channel);
            history.pushState(null, null, '/smarttv.html?channel=' + channel.id);
            setContents();
        }, function(error) {
            console.log(error);
        });

    });

    // Activates/Deactivates like when clicked on the icon
    $(document).on('click', '.like', function () {
        toggleLike($(this));
    });

    // Activates/Deactivates report when clicked on the icon
    $(document).on('click', '.report', function () {
        toggleReport($(this));
    });

    // Sets the like button On/Off
    function toggleLike($like) {

        if($like.data('clicked')) {
            $like.data('clicked', false);
            $like.attr("src", "img/like.png");
        }

        else {
            $like.data('clicked', true);
            $like.attr("src", "img/liked.png");
        }
    }
    // Sets the like button On/Off
    function toggleReport($report) {

        if($report.data('clicked')) {
            $report.data('clicked', false);
            $report.attr("src", "img/report.png");
        }

        else {
            $report.data('clicked', true);
            $report.attr("src", "img/reported.png");
        }
    }

    // Get the diff between needed height and viewable height in the news box
    function extraHeight($newsTextWindow) {

        if(unexpandedHeight == null)
            return $newsTextWindow.find('.news-text-wrapper').innerHeight() - $newsTextWindow.innerHeight();

        return $newsTextWindow.find('.news-text-wrapper').innerHeight() - unexpandedHeight;
    }

    // Creates the events that perform actions throughout the page
    function initEventListeners() {

        initSidebarEventListeners();

        var $img_url = $('#url-prompt');

        $('#title-create-hidden').focus(function () {
            $('#create-news').slideDown();
        });

        $(document).on('click', '.news-tags', function() {
            var tagId = $(this).attr('tag-id');
            getTag(tagId, function(tag) {
                contentsSession = getTagNewsContentSession(tag);
                setContents();
            }, function(error) {
                console.error(error);
            });
        });


        $('#cancel-create').click(function(){
            cleanNewsForm();
        });

        $img_url.on('input', function () {

            var url = "url(\'/img/no_photo.png\')";

            if($img_url.val() != "")
                url = "url(\'" + $(this).val() + "\')";

            $('#photo-preview').css({"background-image": url});
        });

        /* attach a submit handler to the form */
        $('#submit-create').click(function () {

            var json = ({
                "imgPath": $img_url.val(),
                "brief": $('#brief-field').val(),
                "title": $('#title-create-hidden').val(),
                "tags": parseTags($('#tag-field').val()),
                "show": true,
                "content": $('#news-field').val()
            });
            console.log("Creating news. Sending: " + JSON.stringify(json));

            createNews(json, function(votedContent) {
                console.log(votedContent);
                //createNewsBox(votedContent);
                setNewsBox(votedContent, true);
                //prepend
            }, function(error) {
                console.log(error);
                //show error
            });
            cleanNewsForm();

        });

        $('#search-field').keydown(function(e) {
            if(e.keyCode == 13) {
                var query = $("#search-field").val();
                console.log(query);
                history.pushState(null, null, '/smarttv.html?query=' + query);
                contentsSession = getSearchContentSession(query);
                setContents();
            }
        });

        $(window).scroll(bindScroll);

        $(document).on('click', '.like', function(){
            var $newsID = $(this).closest('.news-container').attr('data-id');
            console.log($newsID);
            getNews($newsID, function(content) {
                content.like = true;
                vote($newsID, content, function(votedContent) {
                    console.log(votedContent);
                }, function(error) {
                    console.error(error);
                });
            }, function (error) {
                console.error(error);
            });
        });
    }
});

function cleanNewsForm() {
    $('#create-news').slideUp();
    $("#brief-field").val("");
    $("#title-create-hidden").val("");
    $("#tag-field").val("");
    $("#news-field").val("");
    $("#url-prompt").val("");
}

function loadMore() {
    addContents();
    $(window).bind('scroll', bindScroll);
}

function bindScroll(){
    if($(window).scrollTop() + $(window).height() > $(document).height() - 100) {
        $(window).unbind('scroll');
        loadMore();
        //METE LOGO DO CAMPUSTV
    }
}

// Create news boxes and fill them with information from the JSON
function createNewsBox(title, brief, img, likes, liked, reported, id, tags, prepend) {

    $.get('templates/newsbox.html', function () {

        var tagList = "";

        for(var i = 0; i < tags.length; i++) {

            if(i != 0)
                tagList += '<div class="news-tags" tag-id="' + tags[i].id + '">, ' + tags[i].name + '</div>';
            else
                tagList += '<div class="news-tags" tag-id="' + tags[i].id + '">  ' + tags[i].name + '</div>';
        }

        $('#news-zone').loadTemplate("templates/newsbox.html",
            {
                //newsTags: "Informática, Matemática",
                newsTitle: title,
                brief: brief,
                nlikes: "> " + likes + " likes",
                tags: tagList
            }, {
                prepend: prepend,
                append: !prepend,
                beforeInsert: function (element) {
                    element.find('.news-photo-wrap').css({'background-image': 'url(' + img + ')'});
                },
                afterInsert: function (element) {
                    var $news = $(element);
                    $news.attr('data-id', id);
                    $news.find('.title').attr('href', 'news.html?id=' + id);
                    $news.find('.news-text-window').data('expanded', false);

                    if(liked)
                        toggleLike($news.find('.like'));

                    if(reported)
                        toggleReport($news.find('.report'));

                    // if is there not any need for extra box height to show the contents
                    if(($news.find('.news-text-wrapper').innerHeight() - $news.find('.news-text-window').innerHeight()) <= 0) {
                        $news.find('.news-expand-button').css({'display': 'none'});
                        $news.find('.gradientDiv').css({'display': 'none'});
                    }
                }
            });
    });
}
