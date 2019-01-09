
(function(){
    "use strict";

    console.log = function(obj){
        var currentDate = new Date(Date.now());
        var timestamp = currentDate.getHours() + ":" + currentDate.getMinutes() + ":" + currentDate.getSeconds();

        $("#console").append(
            $("<div>").addClass("line").text(timestamp + "  >>  " + JSON.stringify(obj))
        ).scrollTop($("#console").height());
    }

})();