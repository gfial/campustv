/**
 * Created by guilhermemtr on 6/29/14.
 */

function addAutocompleteToHTML() {

    var $inputField = null;

    // don't navigate away from the field on tab when selecting an item
    $( ".multi-tags-autocomplete").each( function() {
       $(this).bind( "keydown", function( event ) {

           $inputField = $(this);


           if ( event.keyCode === $.ui.keyCode.TAB && $inputField.autocomplete( "instance" ).menu.active ) {
               event.preventDefault();
           }

       }).autocomplete({
           delay: 500,
           minLength: 2,
           source: "/api/tag/autocomplete",
           focus: function() {

               // prevent value inserted on focus
               return false;
           },
           select: function( event, ui ) {
               var terms = split( this.value );

               // remove the current input
               terms.pop();

               // add the selected item
               terms.push( ui.item.name );

               this.value = terms.join( ", " );

               getTagByName(ui.item.name, function(tag) {
                   console.log(tag);
               }, function(error) {
                   console.error(error);
               });

               return false;
           }
       }).autocomplete("instance")._renderItem = function(ul, item) {

           ul.css({"max-width": $inputField.innerWidth()});

            return $( "<li class=\"autocomplete\">" )
               .append( "<h5>" + item.name + "</h5><a>" + item.brief + "</a>" )
               .appendTo( ul );
       };
    });

    $( ".single-tag-autocomplete" )
        // don't navigate away from the field on tab when selecting an item
        .bind( "keydown", function( event ) {
            if ( (event.keyCode === $.ui.key2ode.ENTER || event.keyCode === $.ui.keyCode.TAB) &&
                $( this ).autocomplete( "instance" ).menu.active ) {
                event.preventDefault();
            }
        })
        .autocomplete({
            delay: 500,
            minLength: 0,
            source: "/api/tag/autocomplete",
            focus: function() {

                // prevent value inserted on focus
                return false;
            },
            select: function( event, ui ) {
                this.value = ui.item.name;
                getTagByName(this.value, function(tag) {
                    console.log(tag);
                }, function(error) {
                    console.error(error);
                });
                return false;
            }
        }).autocomplete("instance")._renderItem = function(ul, item) {

        return $( "<li class=\"autocomplete\">" )
            .append( "<h5>" + item.name + "</h5><a>" + item.brief + "</a>" )
            .appendTo( ul );
    };
}