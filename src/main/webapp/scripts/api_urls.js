/**
 * Created by guilhermemtr on 6/24/14.
 */


var get = 'Get';
var post = 'Post';

//CREATION URL'S


var createTagUrl = "/api/tag/create";

var createNewsUrl = "/api/news/create";

var createMemberUrl = "/api/member/create";

var createChannelUrl = "/api/channel/create";



//EDITION URL'S

var editMemberUrl = "/api/member/edit";

function editChannelUrl(id) {
    return "/api/channel/" + id + "/edit";
}

function editTagUrl(id) {
    return "/api/tag/" + id + "/edit";
}

function editNewsUrl(id) {
    return "/api/news/" + id + "/edit";
}

//GETS URL'S

var defaultMemberUrl = "/api/member/1/get";

var getMemberUrl = "/api/member/get";

function getMemberByIdUrl (id) {
    return "/api/member/" + id + "/get";
}

function getTagByNameUrl (name) {
    return "/api/tag/get?query=" + name;
}

function getTagUrl(id) {
    return "/api/tag/" + id + "/get";
}

function getChannelUrl(id) {
    return "/api/channel/" + id + "/get";
}

function getNewsUrl(id) {
    return "/api/news/" + id + "/get";
}

function getTagNewsUrl(id, begin, offset) {
    return "/api/tag/" + id + "/search?beg=" + begin + "&offset=" + offset;
}



//SEARCH URL'S
//Todo por offsets

function searchMemberUrl(query, beg, offset) {
    return "/api/member/search?query=" + query + "&beg=" + beg + "&offset=" + offset;
}

function searchTagUrl(query, beg, offset) {
    return "/api/tag/search?query=" + query + "&beg=" + beg + "&offset=" + offset;
}

function searchChannelUrl(query, beg, offset) {
    return "/api/channel/search?query=" + query + "&beg=" + beg + "&offset=" + offset;
}

function searchNewsUrl(query, beg, offset) {
    return "/api/news/search?query=" + query + "&beg=" + beg + "&offset=" + offset;
}


//AUTOCOMPLETE URL'S

function autoCompleteMemberUrl(name) {
    return "/api/news/autocomplete?query=" + name;
}

function autoCompleteTagUrl(name) {
    return "/api/tag/autocomplete?query=" + name;
}

function autoCompleteAuthTagManagersUrl(id, name) {
    return "/api/tag/auth/" + id + "/manager/autocomplete?query=" + name;
}

function autoCompleteAuthTagMembersUrl(id, name) {
    return "/api/tag/auth/" + id + "/member/autocomplete?query=" + name;
}

function autoCompleteAuthTagParentMembersUrl(id, name) {
    return "/api/tag/auth/" + id + "/parentMember/autocomplete?query=" + name;
}




//MEMBER URL'S

var memberLoginUrl = "/api/member/login";

var memberLogoutUrl = "/api/member/logout";

function memberGroupsUrl(id) {
    return "/api/member/" + id + "/memberGroups";
}

function managedGroupsUrl(id) {
    return "/api/member/" + id + "/managedGroups";
}

function memberPostsUrl(id) {
    return "/api/member/" + id + "/posts";
}


//CHANNEL URL'S

function fillChannelUrl(channelId, begin, offset) {
    return "/api/channel/" + channelId + "/fill?begin=" + begin + "&offset=" + offset;
}

//AUTHTAG URL'S

function addManagerUrl(tagId, memberId) {
    return "/api/tag/auth/" + tagId + "/manager/add/" + memberId;
}

function addMemberUrl(tagId, memberId) {
    return "/api/tag/auth/" + tagId + "/member/add/" + memberId;
}

function revokeManagerUrl(tagId, managerId) {
    return "/api/tag/auth/" + tagId + "/manager/revoke/" + managerId;
}

function revokeMemberUrl(tagId, memberId) {
    return "/api/tag/auth/" + tagId + "/member/revoke/" + memberId;
}

function listManagerUrl(tagId) {
    return "/api/tag/auth/" + tagId + "/manager/list/";
}

function listMemberUrl(tagId) {
    return "/api/tag/auth/" + tagId + "/member/list/";
}


//NEWS URL'S

function voteUrl(newsId) {
    return "/api/news/" + newsId + "/vote";
}


//Image upload URL'S

var uploadUrl = "/api/multimedia/upload";

function copyImageUrl(url) {
    return "/api/multimedia/get?query=" + url;
}

