/**
 * Created by Guilherme on 03/07/2014.
 */
/* Necessary to import in order for the sidebar to work (have its window system)*/

// Creates listeners for the events of the sidebar
function initSidebarEventListeners() {

    var $submenu = $('.submenu');
    var $menubox = $('.menu-box');

    $(document).keyup(function(e) {

        // If escape is pressed
        if (e.keyCode == 27) {
            //close sidebar menus
            $submenu.hide();
            $menubox.hide();
        }
    });

    // Creates a way to close the menus (ESC key and clicking out of them)
    $(document).mousedown(function (e) {

        if (!$submenu.is(e.target) // if the target of the click isn't the container...
            && ($submenu.has(e.target).length === 0)) // ... nor a descendant of the container
        {
            $submenu.hide();
        }

        if (!$menubox.is(e.target) // if the target of the click isn't the container...
            && ($menubox.has(e.target).length === 0)) // ... nor a descendant of the container
        {
            $menubox.hide();
        }
    });

    $('.sidebar-item').click(function(){
        $(this).parent().children('.submenu').show();
        $(this).parent().children('.menu-box').show();
    });

    $('#submit-channel').click(function () {
        var json = ({
            "name": $('#c-channel-name').val(),
            "tags": $('#c-channel-tags').val().split( /,\s*/ ) //parseTags($('#c-tag-tags').val()),
        });


        var newChannel = ({
            "name" : json.name,
            "filterType" : 'regular',
            "trending" : false,
            "filter" : []
        });

        var i = 0;
        for(i = 0; i < json.tags.length; i++) {
            newChannel.filter[i] = {
              "tag" : {
                  "name" : json.tags[i]
              },
              "weight" : 100
            };
        }

        console.log(newChannel);


        createChannel(newChannel, function(channel) {
            setSideBarChannel(newChannel);
            console.log(channel);
        }, function(error) {
            console.error(error);
        });
        //console.log("Creating tag. Sending: " + JSON.stringify(json));

    });

    $('#c-tag-submit').click(function () {

        var json = ({
            "imgPath": $('#c-tag-path').val(),
            "brief": $('#c-tag-brief').val(),
            "name": $('#c-tag-name').val(),
            "parents": $('#c-tag-tags').val().split( /,\s*/ ), //parseTags($('#c-tag-tags').val()),
            "authenticated": false
        });

        var newTag = ({
            "name" : json.name,
            "brief" : json.brief,
            "imgPath" : json.imgPath,
            "parents" : [],
            "authenticated": json.authenticated
        });

        var j = 0;
        var i = 0;
        alert(json.parents.length);
        for(i = 0; i < json.parents.length; i++) {
            getTagByName(json.parents[i], function(tag) {
                newTag.parents[i] = tag.id;
                j++;
                if(j == json.parents.length) {
                    console.log(j);
                    newTag.parents = [11];
                    createTag(newTag, function(tag) {
                        console.log(tag);
                    }, function(error) {
                        console.error(error);
                    });
                }
            }, function(error) {

                console.log(error);
            });
        }

        console.log("Creating tag. Sending: " + JSON.stringify(json));

    });


    $('#e-tag-submit').click(function () {

        var json = ({
            "imgPath": $('#e-tag-path').val(),
            "brief": $('#e-tag-brief').val(),
            "name": $('#e-tag-name').val()
        });

        console.log("Editing tag. Sending: " + JSON.stringify(json));

    });
}

function setSideBarChannel(channel) {
    ($("<div/>", {
        "class": 'sidebar-item added-in sidebar-channel',
        "data-id": channel.id,
        html:  channel.name
    })).appendTo('#sidebar-channels');
}

function loadSidebar(member) {
    $.get('templates/sidebar.html', function () {

        $('body').loadTemplate('templates/sidebar.html',{},{
            prepend: true,
            afterInsert: function () {
                $('#sidebar-smart-news').attr('data-id', member.smartTv.id);
                initSidebarEventListeners();
                var i = 0;
                for(i = 0; i < member.channels.length; i++) {
                    setSideBarChannel(member.channels[i]);
                }

                addAutocompleteToHTML();
            }
        });
    });
}