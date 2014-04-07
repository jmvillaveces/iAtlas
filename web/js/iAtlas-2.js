var ATLAS = function() {
    //Private attributes
    
    var use_proxy = true;
    var proxy = 'proxy/proxy.php';
    var psicquicUrl = 'http://dachstein.biochem.mpg.de:8080/psicquic/webservices/current/search/query';
    var esummary = 'http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary.fcgi?';
    
    var colors = ['#1f77b4', '#aec7e8', '#ff7f0e', '#ffbb78', '#2ca02c', '#98df8a', '#d62728', '#ff9896', '#9467bd', '#c5b0d5', '#8c564b', '#c49c94', '#e377c2', '#f7b6d2', '#7f7f7f', '#c7c7c7', '#bcbd22', '#dbdb8d', '#17becf', '#9edae5', '#393b79', '#5254a3', '#6b6ecf', '#9c9ede', '#637939', '#8ca252', '#b5cf6b', '#cedb9c', '#8c6d31', '#bd9e39', '#e7ba52', '#e7cb94', '#843c39', '#ad494a', '#d6616b', '#e7969c', '#7b4173', '#a55194', '#ce6dbd', '#de9ed6'];
    
    var nodes = [], edges = [], organisms = [], interactionCount = 0, pharentesisPatt = /\((.*?)\)/,
            methods = [], types = [], interactors = [], scores = [], invalidChars = /([ ;&,-\.\+\*\~':"\!\^#$%@\[\]\(\)=>\|])/g ;
    
    var _visual_style = {
        nodes: {
            shape: "ELLIPSE",
            opacity: 0.8,
            size: 40,
            borderWidth:0
        },
        edges: {
            width:1,
            opacity:0.6
        }
    };
    
    var _dataSchema = {
        nodes: [{ name: "organism", type: "string" }, { name: "label", type: "string" } , { name:'altIds', type:'string'}],   
        edges: [ { name: "label", type: "string" },{ name: "interscore", type: "number" }]
    };       
            
    // init and draw
    var vis = new org.cytoscapeweb.Visualization('center', {
        // where you have the Cytoscape Web SWF
        swfPath: "cytoscapeweb/swf/CytoscapeWeb",
        // where you have the Flash installer SWF
        flashInstallerPath: "cytoscapeweb/swf/playerProductInstall"
    });
    
    //Private functions
    
    var _getInteractionCount = function(callback){
        
        var done = function(data) {
            interactionCount = Number(data);
            callback(data);
        }
        
        if(use_proxy){
            $.get(proxy, { url: psicquicUrl+'/*?format=count' }).done(done);
        }else{
            $.get(psicquicUrl+'/*?format=count').done(done);
        }
    }
    
    var _on_got_mitab = function(mitab){
        _decodeToJSON(mitab);
                
        var network_json = {
            data: {
                nodes: nodes,
                edges: edges
            }
        };
            
        network_json.dataSchema = _dataSchema;
                
        // Create the mapper:
        var colorMapper = {
                attrName: "organism",
                entries: []
        };
                
        var content = '';
        jQuery.each(organisms, function(i,o) {
            o.color= colors[i];
            content += '<li><input type="checkbox" value="'+o.id+'" checked><div style="background-color:'+colors[i]+';height:15px;width:15px;float:right"></div> '+o.name+'<p class="grey">taxId: '+o.id+' - nodes: '+o.nodes+'</p></li>';
        });
        
        console.log(organisms);
        $('#orgs').append(content);
                
        vis.ready(function(){
                
            $('#orgs input').click(function(){
                var arr = $('#orgs input:checked').map(function(){
                    return $(this).val();
                }).get();
                        
                vis.filter("nodes", function(n) {
                    for(i in arr)
                        if(arr[i] == n.data.organism)
                            return true;
                            
                    return false;
                            
                }, true);
                       
            });
        });
        //image:'proxy/proxy.php?url=http://chart.apis.google.com/chart?chs=300x300&cht=p&chd=t:33.3,33.3,33.3&chco=666666,222222,009900'//'http://chart.googleapis.com/chart?chs=250x100&chd=t:33.3,33.3,33.3&cht=p&chco=666666,222222,009900'    
        _visual_style.nodes.color = {discreteMapper: colorMapper};
                
        vis.draw({ visualStyle:_visual_style, network: network_json });
    }
    
    var _query = function(query){
        
        var url = psicquicUrl + '/' + query;
        
        var ajax_setup ={ 
			url: url,
			dataType: "text",
			success: _on_got_mitab
		};
        
        if(use_proxy){
            ajax_setup.url = proxy;
            ajax_setup.data = [{ name: "url", value: url }];
        }
        jQuery.ajax(ajax_setup);
    };
    
    var _processNode = function(nodeStr, altIds, orgStr){
        var aux = nodeStr.split('|')[0].split(':');
        var id= aux[1];
        if(nodeStr[0].indexOf("chebi") == 0)
            id= aux[1]+':'+aux[2];
        
        var org = [];
        $.each(orgStr.split('|'), function(i, e){
            var match = e.match(/\d+((.|,)\d+)?/);
            var organism = (match == null) ? 'unknown' : match[0];
            _addOrganism(organism);
            org.push(organism);
        });  

        var node = {
            id: id,
            organism: org.join(','),
            label:id,
            altIds: altIds
        };
        _binaryInsert(nodes, node, 'id');
        _binaryInsert(interactors, id);
        
        return node;
    }
    
    var _addOrganism = function(organism){
        
        var orgObj = {id:organism, nodes: 1};
        
        var index = _binarySearch(organisms, orgObj, 'id');
        if (index < 0){
            organisms.splice(-(index + 1), 0, orgObj);
        }else{
            organisms[index].nodes += 1;
        }
    }
    
    var _getOrgNames = function(callback){
         var ids = [];
            
        jQuery.each(organisms, function(i,o) {
            ids.push(o.id);
        });
        
        jQuery.ajax({ 
            url: esummary,
            dataType: "xml",
            async: false,
            data: [{ name: "db", value: 'taxonomy' }, {name: 'id', value:ids}],
            success: function(xml){
                
                jQuery.each(organisms, function(i,o){
                    $(xml).find("Id:contains('"+o.id+"')").each(function(i,e){
                        var el = $(e);
                        if(o.id == el.text()){
                            o.name = el.siblings("Item[Name='ScientificName']").text();
                        }
                    });
                });
                
                _insertionSort(organisms, 'nodes');
                organisms.reverse();
            }
        });
    }
    
    var _insertionSort = function(items, prop) {
        
        var len     = items.length,     // number of items in the array
            value,                      // the value currently being compared
            i,                          // index into unsorted section
            j;                          // index into sorted section
        
        for (i=0; i < len; i++) {
        
            // store the current value because it may shift later
            value = items[i];
            
            /*
             * Whenever the value in the sorted section is greater than the value
             * in the unsorted section, shift all items in the sorted section over
             * by one. This creates space in which to insert the value.
             */
            if(prop !== undefined){
                for (j=i-1; j > -1 && items[j][prop] > value[prop]; j--) {
                    items[j+1] = items[j];
                }
            }else{
                for (j=i-1; j > -1 && items[j] > value; j--) {
                    items[j+1] = items[j];
                }
            }
    
            items[j+1] = value;
        }
        
        return items;
    }
    
    var _binaryInsert = function(array, value, prop) {
        var index = _binarySearch(array, value, prop);
        if (index < 0)
            array.splice(-(index + 1), 0, value);

        return index;
    };
    
    var _binarySearch = function (arr, value, prop) {
        var left = 0;  // inclusive
        var right = arr.length;  // exclusive
        var found;
        while (left < right) {
            var middle = (left + right) >> 1;

            var compareResult;
            if(prop !== undefined){
                compareResult = value[prop] > arr[middle][prop] ? 1 : value[prop] < arr[middle][prop] ? -1 : 0;
            }else{
                compareResult = value > arr[middle] ? 1 : value < arr[middle] ? -1 : 0;
            }
            
            if (compareResult > 0) {
                left = middle + 1;
            } else {
                right = middle;
                // We are looking for the lowest index so we can't return immediately.
                found = !compareResult;
            }
        }
      // left is the index if found, or the insertion point otherwise.
      // ~left is a shorthand for -left - 1.
      return found ? left : ~left;
    };
    
    var _decodeToJSON = function(miTabData){
        
        var lines = miTabData.split('\n');
        for(var i=0; i<lines.length; i++){
            var line = lines[i].split('\t');
            
            if(line.length > 13){
            
                var interactorA = _processNode(line[0], line[2], line[9]);
                var interactorB = _processNode(line[1], line[3], line[10]);
                
                var data = {
                    source: interactorA.id,
                    target: interactorB.id
                };
                
                jQuery.each(line[6].split('|'), function(i, e){
                    var match = e.match(pharentesisPatt);
                    
                    var method = 'unknown';
                    if(match != null)
                        method = match[1].replace(invalidChars, '_');
                    
                    //data[method] = method;
                    
                    //data.method.push(method);
                    
                    _binaryInsert(methods, method);
                });
                
                //scores
                jQuery.each(line[14].split('|'), function(i, e){
                    
                    var arr = e.split(':');
                    var sel = arr[0].replace(invalidChars, '_');
                    
                    
                    if(sel == 'interscore'){
                        
                        data.interscore = Number(arr[1]);
                        if(arr.length >2)
                            data.interscore = Number(arr[1].match(/\d+((.|,)\d+)?/)[0]);
                        
                       
                    }
                    
                    _binaryInsert(scores, sel);
                });
                
                //types
                //data.type = [];
                jQuery.each(line[11].split('|'), function(i, e){
                    var match = e.match(pharentesisPatt);
                    
                    var type = 'unknown';
                    if(match != null)
                        type = match[1].replace(invalidChars, '_');
                    
                    //data[type] =type;
                    
                    //data.type.push(type);
                    
                    _binaryInsert(types, type);
                });
                
                edges.push(data);
            }
        }
        _getOrgNames();
    }

    //Public API
    var atlas = {
        //Attributes
        colors:colors,
              
        //Methods
        query: _query,
        getNodes: function(){
            return nodes;
        },
        getEdges:function(){
            return edges;
        },
        getOrganisms:function(){
            return organisms;
        },
        getInteractionCount: _getInteractionCount,
        binarySearch:_binarySearch,
        binaryInsert:_binaryInsert
    };
    return atlas;
}();