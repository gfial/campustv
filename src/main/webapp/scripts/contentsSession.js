/**
 * Created by guilhermemtr on 7/2/14.
 */

function ContentsSession(getContentsRequester, title, query) {
    this.begin = 0;
    this.offset = 15;
    this.title = title;
    this.getContentsRequester = getContentsRequester;
    this.query = query;
    //news []
}

ContentsSession.prototype.getContents = function(callback, errorCallback) {
    var contentsSession = this;
    console.log(this.begin);
    this.getContentsRequester(this.query, this.begin, this.offset, function(contents) {
        //contentsSession.begin += contents.length;
        //console.log(contents);
        callback(contents);
    }, errorCallback);
    this.begin += this.offset;
};



function getChannelContentSession(channel) {
    return new ContentsSession(fillChannel, channel.name, channel.id);
}

function getSearchContentSession(query) {
    return new ContentsSession(searchNews, 'Search results for ' + query, query);
}

function getTagNewsContentSession(tag) {
    return new ContentsSession(getTagNews, 'News for ' + tag.name, tag.id);
}