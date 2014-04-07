var ProgressView = function(selector) {

    this.initialize = function() {
        // Define a wrapper for the view.
        this.wrapper = $(selector);
    };

    this.render = function(data) {
        this.wrapper.html(ProgressView.template(data));
        return this;
    };
    
    this.update = function(data){
        $('[role="progressbar"]').css('width',data+'%');
    }

    this.initialize();

}
ProgressView.template = Handlebars.compile($("#progress-bar-partial").html());