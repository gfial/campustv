/**
 * Created by guilhermemtr on 6/28/14.
 */

//The channel session.
function ChannelSession(channelJson) {
    this.channel = channelJson;
    this.newsCounter = 0;
    this.offset = DEFAULTOFFSET;
    this.news = [];
}

//Fetches news from to the channel.
ChannelSession.prototype.setNews = function(news) {
    if(news.length > 0)
        this.news = news;
};

//Pushes some news to the channel session.
ChannelSession.prototype.addNews = function (news) {
    var i;
    for(i = 0; i < news.length; i++) {
        this.news.push(news[i]);
    }
    return news.length;
};

//Fetches news from to the channel.
ChannelSession.prototype.fetchNews = function(fetchedNewsCallback, errorCallback) {
    var channelSession = this;
    function loaded(loadedNews) {
        channelSession.newsCounter += channelSession.addNews(loadedNews);
        fetchedNewsCallback(loadedNews);
    }
    fillChannel(this.channel.id,  this.newsCounter, this.offset, loaded, errorCallback);
};

//Fetches news from to the channel.
ChannelSession.prototype.downloadNews = function(callback, errorCallback) {
    var channelSession = this;
    function loaded(loadedNews) {
        channelSession.setNews(loadedNews);
        var i = 0;
        for(i = 0; i < loadedNews.length; i++) {
            console.log(JSON.stringify(loadedNews[i]));
        }
        callback(loadedNews);
    }
    fillChannel(this.channel.id,  this.newsCounter, this.offset, loaded, errorCallback);
};