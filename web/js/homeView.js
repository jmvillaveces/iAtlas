var HomeView = function() {

    this.initialize = function() {
        // Define a wrapper for the view.
        this.body = $('body');
    };

    this.render = function(data) {
        this.body.html(HomeView.template({count:data}));
        
        
        var query = $("#query");
        
        //Set focus on query
        query.focus();
        
        //Example Link
        $('#explink').click(function() {
            query.val("P49959 P25454 Q54KD8 O74773 Q8IV36 Q96B01 Q54CS9 P52701 Q9CXE6 Q7T6Y0 Q682D3");
            return false;
        });
        
        //Advanced Link
        /*$('#advbox').hide();
        $('#advlink').click(function() {
            $('#advbox').slideToggle(400);
            return false;
        });*/
        
        $('#search-form').submit(function(e){
            e.preventDefault();
            ATLAS.search(query.val());
        });
        
        return this;
    };

    this.initialize();

}
HomeView.template = Handlebars.compile($("#home-tpl").html());
Handlebars.registerPartial("search", $("#search-partial").html());