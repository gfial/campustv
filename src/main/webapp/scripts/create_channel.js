/**
 * Created by Guilherme on 20-05-2014
 */

$(function () {
    $('.graph-bar').mousedown(function (e) {
        var $bar = $(this);
        var barHeight = $bar.height();
        var y = e.pageY;
        $(document).on('mousemove.barmove', function (e) {
            e.preventDefault();
            var offset = y - e.pageY;
            barHeight += offset;
            $bar.css({ 'height': barHeight + 'px' });
            y = e.pageY;
        });
        $(document).mouseup(function (e) {
            $(document).unbind('mousemove.barmove');
        });
    });
});