/**
 * Created by Guilherme on 21/05/2014.
 */

//Todo: perceber bem o codigo.
//Carregar noticias a partir dos channels do member.
//Procurar videos do youtube no content das news.

var MINUTE = 60 * 1000;

var AVG_CHARS_PER_WORD = 6;
var AVG_WORDS_PER_MINUTE = 130;

//Check this function.
function readTime(news) {
    return (news.brief.length + news.title.length) * MINUTE / ( AVG_CHARS_PER_WORD * AVG_WORDS_PER_MINUTE );
}

//Check this function.
function readTimeWithImage(news) {
   return readTime(news) + 4000;
}

function readTimeWithVideo() {
    return MINUTE;
}

//Main area read time, and fade time
var NEWS_FADE = 1000;

//Hold the current main panel news, and the corresponding counter.
var mainPanelNewsCounter = 0;
var mainPanelNews = [];

var barPanelNewsCounter = 1;
var barPanelNews = [];

//The number of news in the bar.
var numBarNews = 0;


//Style related variables.
var newsOuterHeight;
var newsHeight;
var exitingBarNews = null;
var $mainNewsArea = null;
var $newsHolder = null;

//Holds the current session of the tv.
var tvSession = null;
var currentChannel = null;


$(function() {

    setupSession(function(session) {
        console.log(session);
        setupTvSession(session, function(tvSessionJson) {
            console.log("Tv Session is setup.");
            tvSession = tvSessionJson;
            console.log(tvSessionJson);

            initBarNews();
            initTv();
        });
    });

    $mainNewsArea = $('#main-news-area');
    $newsHolder = $('#news-holder');

    setClock();
});

function setupMainPanelNews() {
    mainPanelNewsCounter = 0;
    currentChannel = tvSession.getNextChannel();
    mainPanelNews = currentChannel.news;
}

function setupBarPanelNews() {

    barPanelNewsCounter = 1;
    console.log(tvSession);
    tvSession.getSmartTvNews();
    barPanelNews = tvSession.smartTvSession.news;
}

// Start displaying time in real-time
function setClock() {
    var date = new Date();
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var day = date.getDate();

    var month = getMonthFromNumber(date.getMonth());

    if (day<=9)
        day="0"+day;
    if (hours<=9)
        hours="0"+hours;
    if (minutes<=9)
        minutes="0"+minutes;
    $("#date").html(day + " " + month + " &nbsp&nbsp ");
    $("#hours").html( + hours + ":" + minutes);

    setTimeout(setClock, MINUTE/2);
}


// Build the slide with the "currentMainPos" of the "mainPaneNews" data
function assembleMainNews(votedContent, newsEntrance) {

    console.log("Phase 1");

    //console.log("Incoming news: " + JSON.stringify(votedContent));

    if(typeof (votedContent) === "undefined" || votedContent =="undefined" || votedContent == null) {
        setTimeout(changeMainNews, 100);
        return;
    }

    console.log("Phase 2");

    $mainNewsArea.empty();
    var news = votedContent.news;
    var tag = "Geral";
    var videoID = checkIfVideo(news.content);
    var $player = $('#player');

    // If there is tags
    if(news.tags.length)
        tag = news.tags[0].name;

    $('#main-news-tag').html(tag);

    if(videoID != -1) {
        console.log("Its a video.");
        setTimeout(changeMainNews, readTimeWithVideo());
        var url = assembleYoutubeLink(videoID);
        $player.attr('src', url);
        $player.show();

        ($("<div/>", {
            "id": "main-news-wrapper",
            html:   "<div class='main-title-video'>" +
                        "<div>" + news.title + "</div>" +
                    "</div>"
        }).appendTo($mainNewsArea));

        $mainNewsArea.fadeIn(NEWS_FADE);
    }

    else {
        console.log("Its news");
        setTimeout(changeMainNews, readTimeWithImage(mainPanelNews[mainPanelNewsCounter].news));
        $player.replaceWith('<iframe id="player" src="" frameBorder="0"></iframe>');

        ($("<div/>", {
            "id": "main-news-wrapper",
            html: "<div class='main-title'>" +
                "<div>" + news.title + "</div>" +
                "</div>" +
                "<div class='main-brief'>" +
                "<div>" + news.brief + "</div>" +
                "</div>"
        }).css({'background-image': 'url(' + news.imgPath + ')'})).appendTo($mainNewsArea);

        $mainNewsArea.fadeIn(NEWS_FADE);
    }

    newsEntrance();
}

// Changes the current slide in the Main news area.
function changeMainNews() {
    if(mainPanelNewsCounter == mainPanelNews.length) {
        setupMainPanelNews();
    }

    if($('#main-news-wrapper').length) {
        $mainNewsArea.fadeOut(NEWS_FADE, function () {
            assembleMainNews(mainPanelNews[mainPanelNewsCounter], newsEntrance);
            mainPanelNewsCounter++;
        });
    } else {
        assembleMainNews(mainPanelNews[mainPanelNewsCounter], newsEntrance);
        mainPanelNewsCounter++;
    }
}

// Performs a specific entrance for the title and briefing on the main news
function newsEntrance() {

}

// Adds more news to the main TV screen area, including views
function initTv() {

    setupMainPanelNews();
    changeMainNews();

    //mainNewsInterval = setInterval(changeMainNews, READTIME);
}

function writeBarNews(votedContent) {
    var news = votedContent.news;
    var tag = "Geral";

    if (news.tags.length > 0) {
            tag = news.tags[0].name;
    }

    $("<li/>", {
        "class": "news-wrapper",
        html: "<h3>" + news.title + "</h3>" +
            "<h6>" + tag + "</h6>" +
            "<h4>" + news.brief + "</h4>"
    }).appendTo($newsHolder);
}

// Loads the right bar with the news present in the json input (stored in barPanelNews).
function reloadBarNews() {
    setupBarPanelNews();

    if(barPanelNews == null)
        return null;

    numBarNews = barPanelNews.length;

    for(var i = 0; i < numBarNews; i++) {
        writeBarNews(barPanelNews[i]);

        // Last cycle, finding out the needed size for the bar news to avoid text-cutting.
        if(i == numBarNews - 1) {
            var minBarNewsHeight = 0;
            var $newsWrapper = $('.news-wrapper');

            $newsWrapper.each(function() {

                if($(this).find('h3').outerHeight(true) > minBarNewsHeight) {
                    minBarNewsHeight = $(this).find('h3').outerHeight(true);
                }
            });

            minBarNewsHeight += $('.news-wrapper > h6').outerHeight(true);
            $newsWrapper.css({'height': minBarNewsHeight + 'px'});
        }
    }

    $newsHolder.css('height',numBarNews * newsOuterHeight);
}

//Initializes the while process of news fetching and scrolling.
function initBarNews() {

    setupBarPanelNews();
    reloadBarNews();
    scrollNews();
}

// Animates an entire step of the bar news.
function scrollNews() {

    var nextDiv;
    if(exitingBarNews != null) {
        exitingBarNews.find('h4').slideUp(function () {
            exitingBarNews.animate({'height': newsHeight + 'px'}, function () {
                // Refresh the entire news column (-1 is a code to indicate bar ended the scroll and is ready to update)
                if(barPanelNewsCounter == -1) {
                    $newsHolder.animate({'opacity': 0}, function() {
                        $newsHolder.empty().css({'margin-top':0});
                        reloadBarNews();

                        $newsHolder.animate({'opacity': 1}, function() {
                            barPanelNewsCounter = 1;
                            exitingBarNews = null;
                            scrollNews();
                        });
                    });
                    return 0;
                } else {
                    nextDiv = $('.news-wrapper:nth-child(' + barPanelNewsCounter + ')');
                }
                
                // Every news fades except last one
                if(!(barPanelNewsCounter == 1))
                    exitingBarNews.animate({'opacity': 0}, 500);

                $newsHolder.animate({'margin-top': newsOuterHeight * ((-barPanelNewsCounter) + 1)}, 1500, function () {
                    nextDiv.animate({'height': expandedSizeNeeded(nextDiv) + 'px'}, function () {
                        nextDiv.find('h4').slideDown(function () {
                            // Div turned invisible last cycle must be put to opacity = 1 again so it reappears.
                            exitingBarNews.css({'opacity': 1});
                            exitingBarNews = nextDiv;
                            setTimeout(scrollNews, readTime(barPanelNews[barPanelNewsCounter - 1].news));
                            // if is at the last news in the bar
                            if(barPanelNewsCounter == numBarNews) {
                                barPanelNewsCounter = -1; // -1 informs the next cycle that it should restart
                            } else {
                                barPanelNewsCounter++;
                            }

                        });
                    });
                });
            });
        });
    } else { //First run - Taking measures of heights to compute future animations. This will make it work with whatever size we define for them in css.

        var $newsWrapper = $('.news-wrapper');

        newsOuterHeight = $newsWrapper.outerHeight(true);
        newsHeight = $newsWrapper.outerHeight(false);

        nextDiv = $('.news-wrapper:nth-child(' + (barPanelNewsCounter) + ')');
        nextDiv.animate({'height': expandedSizeNeeded(nextDiv) + 'px'}, function() {
            nextDiv.find('h4').slideDown();
        });
        exitingBarNews = nextDiv;
        setTimeout(scrollNews, readTime(barPanelNews[barPanelNewsCounter - 1].news));
        barPanelNewsCounter++;
    }
}

// Calculates the size required that the bar news takes in order to show all of the text inside it
function expandedSizeNeeded(barNews) {

    var sizeNeeded = barNews.find('h4').innerHeight() + barNews.find('h3').outerHeight(true) + barNews.find('h6').innerHeight();

    if(sizeNeeded <= barNews.outerHeight(false))
        return barNews.outerHeight(false);

    else
        return sizeNeeded;
}

// If there isn't a video return -1
// Else returns video id (String)
function checkIfVideo(input) {

    var x = linkifyYouTubeURLs(input);

    var y = x.split('VIDEO_ID:--');

    if(y==x) {
        return -1;
    }else{
        var z =  y[1].split('--:');
        return z[0];
    }

    // Linkify youtube URLs which are not already links.
    function linkifyYouTubeURLs(text) {
        var re = /https?:\/\/(?:[0-9A-Z-]+\.)?(?:youtu\.be\/|youtube(?:-nocookie)?\.com\S*[^\w\s-])([\w-]{11})(?=[^\w-]|$)(?![?=&+%\w.-]*(?:['"][^<>]*>|<\/a>))[?=&+%\w.-]*/ig;
        return text.replace(re,
            '<a href="http://www.youtube.com/watch?v=$1">VIDEO_ID:--$1--:</a>');
    }
}

function assembleYoutubeLink(id) {
    return ('http://www.youtube.com/embed/' + id + '?autoplay=1&controls=0&iv_load_policy=3');
}

function getMonthFromNumber(num) {

    if(num == 0)
        return "JANEIRO";
    if(num == 1)
        return "FEVEREIRO";
    if(num == 2)
        return "MARÇO";
    if(num == 3)
        return "ABRIL";
    if(num == 4)
        return "MAIO";
    if(num == 5)
        return "JUNHO";
    if(num == 6)
        return "JULHO";
    if(num == 7)
        return "AGOSTO";
    if(num == 8)
        return "SETEMBRO";
    if(num == 9)
        return "OUTUBRO";
    if(num == 10)
        return "NOVEMBRO";
    if(num == 11)
        return "DEZEMBRO";


}
