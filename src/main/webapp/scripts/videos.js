//Init video frame
// 2. This code loads the IFrame Player API code asynchronously.
var tag = document.createElement('script');

tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);


// 3. This function creates an <iframe> (and YouTube player)
//    after the API code downloads.

var player;

function onYouTubeIframeAPIReady(v_id) {
    console.log("Entered onYouTube...");
    player = new YT.Player('player', {
        width: '100%',
        height: '100%',
        videoId: v_id,
        playerVars: {
            showinfo: 0,
            autohide: 0,
            controls : 0,
            iv_load_polic:3,
            rel: 0,
            iv_load_policy: 3
        },
        events: {
            'onReady': onPlayerReady,
            'onStateChange': onPlayerStateChange
        }
    });
}

// 4. The API will call this function when the video player is ready.
function onPlayerReady(v_id) {
    player.cueVideoById({videoId: v_id,
        startSeconds: 2, endSeconds: 10, suggestedQuality: 'highres'});
    player.playVideo();

}

// 5. The API calls this function when the player's state changes.
//    The function indicates that when playing a video (state=1),
//    the player should play for six seconds and then stop.
var done = false;
function onPlayerStateChange(event, v_id) {
    if (event.data == 0 ) {
        player.cueVideoById({videoId: v_id,
            startSeconds: 2, endSeconds: 10, suggestedQuality: 'highres'});
        player.playVideo();
    }
}

function stopVideo() {
    player.stopVideo();
}/**
 * Created by Guilherme on 26/06/2014.
 */
