/**
 * 
 * Graph display of molecular interactions using <a href='http://code.google.com/p/psicquic/'>PSICQUIC</a>.
 * 
 * @class
 * @extends Biojs
 * 
 * @author <a href="mailto:villaveces@biochem.mpg.de">José Villaveces</a>
 * @version 1.0.0_beta
 * @category 2
 * 
 * @requires <a href='http://blog.jquery.com'>jQuery 1.7.2</a>
 * @dependency <script language="JavaScript" type="text/javascript" src="../biojs/dependencies/jquery/jquery-1.7.2.min.js"></script>
 * 
 * @requires <a href='http://cytoscape.github.com/cytoscape.js/download/cytoscape.js-2.0.2.zip'>Cytoscape.js v2.0.2</a>
 * @dependency <script language="JavaScript" type="text/javascript" src="../biojs/dependencies/cytoscape/2.0.2/cytoscape.min.js"></script>
 * 
 * @param {Object} options An object with the options for this component.
 *    
 * @option {string} target 
 *    id of the div where the component should be displayed
 * 
 * @option {string} psicquicUrl
 * 	  url of the PSICQUIC server to query.
 * 
 * @option {string} proxyUrl
 * 	  url of the proxy to use.
 * 
 * @option {string} query
 * 	  MIQL query.
 *
 * @option {Object} cyoptions
 * 	  cytoscapejs inititlization options more info <a href='http://cytoscape.github.io/cytoscape.js/'>here</a>
 *
 * @example
 * var instance = new Biojs.PsicquicGraph({
 *      target: "YourOwnDivId",
 *	    psicquicUrl: 'http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/query',
 *		proxyUrl: '../biojs/dependencies/proxy/proxy.php',
 *      query: 'species:human?firstResult=0&maxResults=100',
 *      cyoptions: {
 *          style: cytoscape.stylesheet().selector('node').css({
 *              'content': 'data(id)',
 *              'font-family': 'helvetica',
 *              'font-size': 14,
 *              'text-outline-width': 3,
 *              'text-outline-color': '#888',
 *              'text-valign': 'center',
 *              'color': '#fff',
 *              'width': 'mapData(weight, 30, 80, 20, 50)',
 *              'height': 'mapData(height, 0, 200, 10, 45)',
 *              'border-color': '#fff'
 *            }).selector(':selected').css({
 *              'background-color': '#000',
 *              'line-color': '#000',
 *              'target-arrow-color': '#000',
 *              'text-outline-color': '#000'
 *           }).selector('edge').css({
 *              'width': 2
 *           }),
 *          layout: {
 *                name: 'circle',
 *                fit: true, // whether to fit the viewport to the graph
 *                ready: undefined, // callback on layoutready
 *                stop: undefined, // callback on layoutstop
 *                rStepSize: 10, // the step size for increasing the radius if the nodes don't fit on screen
 *                padding: 30, // the padding on fit
 *                startAngle: 3/2 * Math.PI, // the position of the first node
 *                counterclockwise: false // whether the layout should go counterclockwise (true) or clockwise (false)
 *            },
 *            ready:function(){
 *                var cy = this;
 *                cy.nodes().click(function(e){
 *                    console.log(e.cyTarget.id());
 *                });
 *            }
 *      }
 * });
 */

Biojs.PsicquicGraph = Biojs.extend(
/** @lends Biojs.PsicquicGraph# */
{
    constructor: function (options) {
        //Biojs.console.enable();
        this._query(this.opt);
	 },
	/**
	 * Default values for the options
	 * @name Biojs.PsicquicGraph-opt
	 */
	opt: {
		target: "YourOwnDivId",
		psicquicUrl: 'http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/query',
		proxyUrl: '../biojs/dependencies/proxy/proxy.php',
        query: 'species:human?firstResult=0&maxResults=100',
        height:'500px',
        width:'100%',
        cyoptions: {
            //see cytoscapejs inititlization options
            style: cytoscape.stylesheet().selector('node').css({
                'content': 'data(id)',
                'font-family': 'helvetica',
                'font-size': 14,
                'text-outline-width': 3,
                'text-outline-color': '#888',
                'text-valign': 'center',
                'color': '#fff',
                'width': 'mapData(weight, 30, 80, 20, 50)',
                'height': 'mapData(height, 0, 200, 10, 45)',
                'border-color': '#fff'
            }).selector(':selected').css({
                'background-color': '#000',
                'line-color': '#000',
                'target-arrow-color': '#000',
                'text-outline-color': '#000'
            }).selector('edge').css({
                'width': 2
            }),
            layout: {
                name: 'circle',
                fit: true, // whether to fit the viewport to the graph
                ready: undefined, // callback on layoutready
                stop: undefined, // callback on layoutstop
                rStepSize: 10, // the step size for increasing the radius if the nodes don't fit on screen
                padding: 30, // the padding on fit
                startAngle: 3/2 * Math.PI, // the position of the first node
                counterclockwise: false // whether the layout should go counterclockwise (true) or clockwise (false)
            },
            ready:function(){
                var cy = this;
                
                cy.nodes().click(function(e){
                    console.log(e.cyTarget.id());
                });
            }
        }
	},
	/**
	 * Array containing the supported event names
	 * @name Biojs.PsicquicGraph-eventTypes
	 */
	eventTypes: [],
    /* 
	 * Function: Biojs.PsicquicGraph._query
	 * Purpose:  Queries PSIQUIC using the provided query in MIQL.
	 * Inputs:   dataSet -> {Object} Settings of the data set.
	 */
    _query: function(dataSet){
        
        dataSet.url = dataSet.psicquicUrl + '/' + dataSet.query;
        
        var instance = this;
        jQuery.ajax({ 
			url: dataSet.proxyUrl,
			dataType: "text",
			data: [{ name: "url", value: dataSet.url }],
			success: function ( data ) {
                instance._decodeToJSON(data, instance);
			}
		});
    },
    /* 
	 * Function: Biojs.PsicquicGraph._decodeToJSON
	 * Purpose:  Transforms MiTab data into JSON and renders the graph.
	 * Inputs:   miTabData -> {Object} interactions in MiTab format.
     *           instance -> {Object} a reference to this widget instance.
	 */
    _decodeToJSON: function(miTabData, instance){
        var nodes = [], edges = [], map = {}, pharentesisPatt = /\((.*?)\)/,
            methods = [], types = [], interactors = [], scores = [];
        
        var lines = miTabData.split('\n');
        for(var i=0; i<lines.length; i++){
            var line = lines[i].split('\t');
            
            if(line.length > 13){
            
                var idSource = line[0].split('|')[0].split(':')[1];
                
                if(map[idSource] === undefined){
                    map[idSource] = {
                        data:{
                            id: idSource,
                            organism: line[9]
                        }
                    }
                    nodes.push(map[idSource]);
                }
                
                var idTarget = line[1].split('|')[0].split(':')[1];
                
                if(map[idTarget] === undefined){
                    map[idTarget] = {
                         data:{
                            id: idTarget,
                            organism: line[10]
                         }
                    }
                    nodes.push(map[idTarget]);
                }
                
                if($.inArray(idSource, interactors) == -1)
                    interactors.push(idSource);
                
                if($.inArray(idTarget, interactors) == -1)
                    interactors.push(idTarget);
                
                var data = {
                    source: idSource,
                    target: idTarget
                };
                
                //methods
                data.method = [];
                $.each(line[6].split('|'), function(i, e){
                    var match = e.match(pharentesisPatt);
                    
                    var method = 'unknown';
                    if(match != null)
                        method = match[1].replace(/([ ;&,-\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '_');
                    
                    data[method] = method;
                    
                    data.method.push(method);
                    
                    if($.inArray(method, methods) == -1)
                        methods.push(method);
                });
                
                //scores
                $.each(line[14].split('|'), function(i, e){
                    var arr = e.split(':');
                    var sel = arr[0].replace(/([ ;&,-\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '_');
                    data[sel] = Number(arr[1]);
                    
                    if($.inArray(sel, scores) == -1)
                        scores.push(sel);
                });
                
                //types
                data.type = [];
                $.each(line[11].split('|'), function(i, e){
                     var match = e.match(pharentesisPatt);
                    
                    var type = 'unknown';
                    if(match != null)
                        type = match[1].replace(/([ ;&,-\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g, '_');
                    
                    data[type] =type;
                    
                    data.type.push(type);
                    
                    if($.inArray(type, types) == -1)
                        types.push(type);
                    
                });
                
                edges.push({
                    data:data
                });
            }
        }
        
        instance.types = types;
        instance.methods = methods;
        instance.interactors = interactors;
        instance.scores = scores;
        
        instance.opt.cyoptions.elements = {
            nodes: nodes,
            edges: edges
        };

        jQuery('<div id="graph" style="height:'+instance.opt.height+';width:'+instance.opt.width+';"></div>').appendTo('#'+instance.opt.target).cytoscape(instance.opt.cyoptions);
        
        //instance._initRuler(instance);
    },
    _initRuler: function(instance){
        //add div for ruler
        jQuery('<div id="ruler"></div>').appendTo('#'+instance.opt.target);
        
        var location = ["all proteins"].concat(instance.interactors);
        
        var scoreObjs = [];
        
        scoreObjs.push({
            "name": "interaction type",
            "type": "selects",
            "amount": 1,
            "values": instance.types
        });
        
        scoreObjs.push({
            "name": "detection method",
            "type": "selects",
            "amount": 1,
            "values": instance.methods
        });
        
        $.each(instance.scores, function(i, e){
            scoreObjs.push({
                "name": e,
                "type": "numeric_comparison"
            });
        });
        
        
        
        var ruler = new Biojs.Ruler({
            target: "ruler",
            allowOrdering:true,
            rules:{
                "location": location,
                "target": [
                {
                    "name": "Interactions",
                    "action": [
                        {name:"Show",type:"single"},
                        {name:"Hide",type:"single"},
                        {name:"Color",type:"color"},
                        {name:"Style",type:"select",options:['solid','dotted','dashed']},
                        {name:"Opacity",type:"select",options:['0','0.1','0.2','0.3','0.4','0.5','0.6','0.7','0.8','0.9','1']},
                        {name:"Show Label",type:"select",options:['interaction type','detection method']}
                    ],
                    "conditions": scoreObjs
                }
                ]
            }
        });
        
        ruler.onRuleCreated(function(e) {                    
            instance._processRules(ruler.getActiveRules());
        });
        
        ruler.onRuleRemoved(function(e) {
            instance._processRules(ruler.getActiveRules());
        });
        
        ruler.onRuleEditing(function(e) {
            instance._processRules(ruler.getActiveRules());
        });
    },
    _processRules:function(rules){
        
        var cy = $('#graph').first().cytoscape('get');
        cy.elements().removeCss();
        
        var instance = this;
        jQuery.each(rules, function(i, rule){
            instance._processRule(rule, cy);
        });
    },
    _processRule:function(rule, cy){
        
                    
        var selector = this._getSelector(rule);
        
        if(rule.action.name == 'Show'){
            cy.elements(selector).show();
        }else if(rule.action.name == 'Hide'){
            cy.elements(selector).hide();
        }else if(rule.action.name == 'Color'){
            if(rule.target == 'Interactions'){
                cy.elements(selector).css({'line-color': rule.actionParameters[0]});
            }else if(rule.target == 'Proteins'){
                cy.elements(selector).css({'background-color': rule.actionParameters[0]});
            }
        }else if(rule.action.name == 'Style'){
            cy.elements(selector).css({'line-style': rule.actionParameters[0]});
        }else if(rule.action.name == 'Opacity'){
            cy.elements(selector).css({'opacity': rule.actionParameters[0]});
        }else if(rule.action.name == 'Show Label'){
            if(rule.actionParameters[0] == 'interaction type'){
                cy.elements(selector).css({'content': 'data(type)'});
            }else if(rule.actionParameters[0] == 'detection method'){
                cy.elements(selector).css({'content': 'data(method)'});
            }else{
                cy.elements(selector).css({'content': ''});
            }
        }
    },
    _getSelector:function(rule){
        var selector = '';
        if(rule.target == 'Proteins'){
                
        }else{
            var i = $.inArray('==', rule.parameters);
            if(i > -1)
                rule.parameters[i] = '=';
            
            var condition = rule.condition+' '+rule.parameters.join(' ');
            if(rule.condition == 'interaction type' || rule.condition == 'detection method')
                condition = rule.parameters[0];
            
            if(rule.location == 'all proteins')
                selector += 'edge['+condition+']';
            else
                selector += 'edge['+condition+'][source = "'+rule.location+'"], edge['+condition+'][target = "'+rule.location+'"]';
        }
        return selector;
    }
});