var ATLAS = function($) {
    var proxy = 'proxy/proxy.php';
    var psicquicUrl = 'http://dachstein.biochem.mpg.de:8080/psicquic/webservices/current/search/query';
    var esummary = 'http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?';
    var dialog = undefined;
    
    //Get the number of interactions in server
    var _getInteractionCount = function(callback){
        
        var done = function(data) {
            interactionCount = Number(data);
            callback(data);
        }
        
        if(typeof proxy !== 'undefined'){
            $.get(proxy, { url: psicquicUrl+'/*?format=count' }).done(done);
        }else{
            $.get(psicquicUrl+'/*?format=count').done(done);
        }
    }
    
    //create dialog
    var _dialog = function(){
        $('.qbox').css('top', '0');
        dialog = $( "#qbox" ).css( "top", "2%" ).dialog({
            autoOpen: false,
            height: 380,
            width: 730,
            modal: true,
            resizable: false,
            show: {
                effect: "blind",
                duration: 1000
            },
            hide: {
                effect: "explode",
                duration: 1000
            }
        });
        $('#qbox').parent().removeClass('ui-widget').children(":first").removeClass('ui-widget-header');
    }
    
    var _search = function(options){
        
        var d = $('<input style="height: 49px;" type="submit" value="" class="cssButton sbut search">').appendTo('#wrapper').position({
            "of": $('#wrapper'),
            "my": "right top",
            "at": "right top"
        });
    }
    
    //Init Home View
    var _homeView = function(data){
        var parts = data.split(".");
        parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        data = parts.join(".");
        new HomeView().render(data);
        
        //Set focus on query
        $("#q").focus();
        
        //Search Button Click
        $('#sbut').click(function(){
            
            var val = $("#q").val();
            if(val.length > 0){
            
                if(dialog === undefined)
                    _dialog();
                
                var orgs = $("#organism_select option:selected").map(function(){
                    return $(this).val();
                }).get();
                
                var options = {
                    elements: val.split(' '),
                    orgs: orgs,
                    first_result: $('#first_result').val(), 
                    max_result: $('#max_result').val()
                };
                _search(options);
            }
        });
    }
    
    
    var _init = function(){
        _getInteractionCount(_homeView);
    }();
    
}(jQuery);