/**
 * Created by guilhermemtr on 6/28/14.
 */


//Requests

//CREATION Requests

function createTag(tag, callback, errorCallback) {
    ajaxRequest(createTagUrl, tag, post, callback, errorCallback);
}

function createNews(news, callback, errorCallback) {
    ajaxRequest(createNewsUrl, news, post, callback, errorCallback);
}

function createMember(member, callback, errorCallback) {
    ajaxRequest(createMemberUrl, member, post, callback, errorCallback);
}

function createChannel(channel, callback, errorCallback) {
    ajaxRequest(createChannelUrl, channel, post, callback, errorCallback);
}



//EDITION Requests

function editMember(member, callback, errorCallback) {
    ajaxRequest(editMemberUrl, member, post, callback, errorCallback);
}

function editChannel(id, channel, callback, errorCallback) {
    ajaxRequest(editChannelUrl(id), channel, post, callback, errorCallback);
}

function editTag(id, tag, callback, errorCallback) {
    ajaxRequest(editTagUrl(id), tag, post, callback, errorCallback);
}

function editNews(id, news, callback, errorCallback) {
    ajaxRequest(editNewsUrl(id), news, post, callback, errorCallback);
}



//GET Requests

function getDefaultMember(callback, errorCallback) {
    ajaxGet(defaultMemberUrl, callback, errorCallback);
}

function getMemberRequest(callback, errorCallback) {
    ajaxGet(getMemberUrl, callback, errorCallback);
}

function getMemberById(id, callback, errorCallback) {
    ajaxGet(getMemberByIdUrl(id), callback, errorCallback);
}

function getTagByName(name, callback, errorCallback) {
    ajaxGet(getTagByNameUrl(name), callback, errorCallback);
}

function getTag(id, callback, errorCallback) {
    ajaxGet(getTagUrl(id), callback, errorCallback);
}

function getChannel(id, callback, errorCallback) {
    ajaxGet(getChannelUrl(id), callback, errorCallback);
}

function getNews(id, callback, errorCallback) {
    ajaxGet(getNewsUrl(id), callback, errorCallback);
}

function getTagNews(id, begin, offset, callback, errorCallback) {
    ajaxGet(getTagNewsUrl(id, begin, offset), callback, errorCallback);
}

//SEARCH Requests

function searchMember(query, begin, offset, callback, errorCallback) {
    ajaxGet(searchMemberUrl(query, begin, offset), callback, errorCallback);
}

function searchTag(query, begin, offset, callback, errorCallback) {
    ajaxGet(searchTagUrl(query, begin, offset), callback, errorCallback);
}

function searchChannel(query, begin, offset, callback, errorCallback) {
    ajaxGet(searchChannelUrl(query, begin, offset), callback, errorCallback);
}

function searchNews(query, begin, offset, callback, errorCallback) {
    ajaxGet(searchNewsUrl(query, begin, offset), callback, errorCallback);
}



//AUTOCOMPLETE URL'S

function autoCompleteMember(name, callback, errorCallback) {
    ajaxGet(autoCompleteMemberUrl(name), callback, errorCallback);
}

function autoCompleteTag(name, callback, errorCallback) {
    ajaxGet(autoCompleteTagUrl(name), callback, errorCallback);
}

function autoCompleteAuthTagManager(id, name, callback, errorCallback) {
    ajaxGet(autoCompleteAuthTagManagersUrl(id, name), callback, errorCallback);
}

function autoCompleteAuthTagMember(id, name, callback, errorCallback) {
    ajaxGet(autoCompleteAuthTagMembersUrl(id, name), callback, errorCallback);
}

function autoCompleteAuthTagParentMember(id, name, callback, errorCallback) {
    ajaxGet(autoCompleteAuthTagParentMembersUrl(id, name), callback, errorCallback);
}



//MEMBER URL'S

function memberLogin(credentials, callback, errorCallback) {
    ajaxRequest(memberLoginUrl, credentials, post, callback, errorCallback);
}

function memberLogout(callback, errorCallback) {
    ajaxRequest(memberLogoutUrl, null, post, callback, errorCallback);
}

function memberGroups(id, callback, errorCallback) {
    ajaxGet(memberGroupsUrl(id), callback, errorCallback);
}

function managedGroups(id, callback, errorCallback) {
    ajaxGet(managedGroupsUrl(id), callback, errorCallback);
}

function memberPosts(id, callback, errorCallback) {
    ajaxGet(memberPostsUrl(id), callback, errorCallback);
}



//CHANNEL URL'S

function fillChannel(channelId, begin, offset, callback, errorCallback) {
    ajaxGet(fillChannelUrl(channelId, begin, offset), callback, errorCallback);
}


//AUTHTAG URL'S

function addManager(tagId, memberId, callback, errorCallback) {
    ajaxRequest(addManagerUrl(tagId, memberId), null, post, callback, errorCallback);
}

function addMember(tagId, memberId, callback, errorCallback) {
    ajaxRequest(addMemberUrl(tagId, memberId), null, post, callback, errorCallback);
}

function revokeManager(tagId, managerId, callback, errorCallback) {
    ajaxRequest(revokeManagerUrl(tagId, managerId), null, post, callback, errorCallback);
}

function revokeMember(tagId, memberId, callback, errorCallback) {
    ajaxRequest(revokeMemberUrl(tagId, memberId), null, post, callback, errorCallback);
}

function listManagers(tagId, callback, errorCallback) {
    ajaxGet(listManagerUrl(tagId), callback, errorCallback);
}


function listMembers(tagId, callback, errorCallback) {
    ajaxGet(listMemberUrl(tagId), callback, errorCallback);
}


//NEWS REQUESTS

function vote(newsId, vote, callback, errorCallback) {
    ajaxRequest(voteUrl(newsId), vote, post, callback, errorCallback);
}
