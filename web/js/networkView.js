var NetworkView = function(selector) {

    this.initialize = function() {
        // Define a wrapper for the view.
        this.wrapper = $(selector);
    };

    this.render = function(data) {
        this.wrapper.html(NetworkView.template(data));
        return this;
    };

    this.initialize();

}
NetworkView.template = Handlebars.compile($("#nav-bar-partial").html());