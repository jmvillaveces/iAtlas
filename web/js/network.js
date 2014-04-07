var Network = function(opt){
    
    //private variables
    var width = opt.width || 500, height = opt.height || 500, data = {}, force = d3.layout.force(), 
        nodeColors = d3.scale.category20c(), linkedByIndex = {}, nodesG = null, linksG = null, curLinksData = [],
        curNodesData = [], layout = 'force', node = null, link = null, vis = null, groupCenters = null, widthPx = 500, heightPx = 500;
    
    //Starting point for network visualization
    //Initializes visualization and starts force layout
    var _init = function(){
        //format our data
        data = _setupData(opt.data);
        
        // create our svg and groups
        var svg = d3.select(opt.selector).append("svg")
                .attr("width", width)
                .attr("height", height);
        
        var svg1 = document.getElementsByTagName("svg"), rec = svg1[0].getBoundingClientRect();
        widthPx = rec.width;
        heightPx = rec.height;
        
        svg.attr("viewBox", "0 0 " + widthPx + " " + heightPx )
            .attr("preserveAspectRatio", "xMidYMid meet")
            .attr("pointer-events", "all")
            .call(d3.behavior.zoom().on("zoom", _zoom));
        
        vis = svg.append("g");
        
        linksG = vis.append("g").attr("id", "links");
        nodesG = vis.append("g").attr("id", "nodes");

        // setup the size of the force environment
        force.size([widthPx, heightPx]);
        
        _setLayout('force');
        //setFilter("all")
        
        //perform rendering and start force layout
        _update();
    }
    
    // called once to clean up raw data and switch links to
    // point to node instances
    // Returns modified data
    var _setupData = function(data){
        
        _.each(data.nodes, function(n){
            //set initial x/y to values within the width/height of the visualization
            n.x = Math.floor(Math.random()*widthPx);
            n.y = Math.floor(Math.random()*heightPx);
        });
        
        // id's -> node objects
        var nodesMap  = _mapNodes(data.nodes);
        
        // switch links to point to node objects instead of id's
        _.each(data.links, function(l){
            var source = nodesMap.get(l.source);
            var target = nodesMap.get(l.target);
            
            source.deg = source.deg + 1 || 1;
            target.deg = target.deg + 1 || 1;
            
            l.source = source;
            l.target = target;
            
            //linkedByIndex is used for link sorting
            linkedByIndex[l.source.id+','+l.target.id] = 1;
        });
        return data;
    }
    
    // The update() function performs the bulk of the work to setup our visualization based on the
    // current layout/sort/filter.
    // update() is called everytime a parameter changes and the network needs to be reset.
    var _update = function(){
        
        curNodesData = _filterNodes(data.nodes);
        curLinksData = _filterLinks(data.links, curNodesData);
        
        // sort nodes based on current sort and update centers for
        // radial layout
        if(layout == "radial"){
            //artists = sortedArtists(curNodesData, curLinksData)
            //updateCenters(artists)
            
            var sorted = _.map(curNodesData,function(n){
                return n.taxonomy.join(',');
            });
            _updateCenters(sorted);
        }
        
        //reset nodes in force layout
        force.nodes(curNodesData);

        // enter / exit for nodes
        _updateNodes();
        
        if(layout=='force'){
            force.links(curLinksData);
            _updateLinks();
        }else{
            if(link){
                link.data([]).exit().remove();
                link = null;
            }
        }
        
        // start me up!
        force.start();
    } 
    
    // enter/exit display for nodes
    var _updateNodes = function(){
        curNodesData = _.sortBy(curNodesData, function(n){return n.deg});
        
        
        node = nodesG.selectAll("circle.node")
                    .data(curNodesData, function(d){return d.id});
        
        if(curNodesData.length == 0) return;
        
        var min = curNodesData[0].deg;
        var max =  curNodesData[curNodesData.length-1].deg;
        var radScale = d3.scale.linear().domain([min, max]).range([3,12]);
        
        node.enter().append("circle")
            .attr("class", "node")
            .attr("cx", function(d){return d.x})
            .attr("cy", function(d){return d.y})
            .attr("r", function(d){return radScale(d.deg)})
            .style("fill", function(d){return nodeColors(d.deg)})
            .style("stroke", '#999')
            .style("stroke-width", 1.0)
            .call(force.drag)
            .on("mousedown", function() { d3.event.stopPropagation(); });

        node.on("mouseover", _mouseOver)
            .on("mouseout", _mouseOut);
        
        node.exit().remove();
    }
    
    // enter/exit display for links
    var _updateLinks = function(){
        link = linksG.selectAll("line.link")
          .data(curLinksData, function(d){return d.source.id+'_'+d.target.id});
        
        link.enter().append("line")
          .attr("class", "link")
          .attr("stroke", "#999")
          .attr("stroke-opacity", 0.8)
          .attr("x1", function(d){return d.source.x})
          .attr("y1", function(d){return d.source.y})
          .attr("x2", function(d){return d.target.x})
          .attr("y2", function(d){return d.target.y});

        link.exit().remove();
    }
    
    // Public function to switch between layouts
    var _toggleLayout = function(newLayout){
        force.stop();
        _setLayout(newLayout);
        _update();
    }
    
    // switches force to new layout parameters
    var _setLayout = function(newLayout){
        layout = newLayout;
        if (layout === 'force'){
            force.charge(-30).linkDistance(20).on("tick", _forceTick);
        }else if (layout === 'radial'){
            force.on("tick", _radialTick).charge(-3);
        }
    }
    
    //tick function for force directed layout
    var _forceTick = function(e){
        node
          .attr("cx", function(d){return d.x})
          .attr("cy", function(d){return d.y});

        link
          .attr("x1", function(d){return d.source.x})
          .attr("y1", function(d){return d.source.y})
          .attr("x2", function(d){return d.target.x})
          .attr("y2", function(d){return d.target.y});
    }
    
    // tick function for radial layout
    var _radialTick = function(e){
        node.each(_moveToRadialLayout(e.alpha));

        node
          .attr("cx", function(d){return d.x})
          .attr("cy", function(d){return d.y});
        

        if(link != null){
            link
                .attr("x1", function(d){return d.source.x})
                .attr("y1", function(d){return d.source.y})
                .attr("x2", function(d){return d.target.x})
                .attr("y2", function(d){return d.target.y});
        }
        
        if(e.alpha < 0.03){
            force.stop();
            _updateLinks();
        }
    }
    
    // Adjusts x/y for each node to push them towards appropriate location.
    // Uses alpha to dampen effect over time.
    var _moveToRadialLayout = function(alpha){
        var k = alpha * 0.1;
        return function(d){
            var centerNode = groupCenters(d.taxonomy.join(','));
            d.x += (centerNode.x - d.x) * k;
            d.y += (centerNode.y - d.y) * k;
        };
    }
    
    var _updateCenters = function(criteria){
        if (layout == "radial"){
            groupCenters = RadialPlacement().center({"x":widthPx/2, "y":heightPx / 2 - 100})
            .radius(300).groups(5).keys(criteria);
        }
    }
    
    // Helper function to map node id's to node objects. 
    //Returns d3.map of ids -> nodes
    var _mapNodes = function(nodes){
        var nodesMap = d3.map();
        _.each(nodes, function(n){
            nodesMap.set(n.id, n);
        });            
        return nodesMap;
    }
    
    // Removes nodes from input array based on current filter setting.
    //Returns array of nodes
    var _filterNodes = function(allNodes){
        var filteredNodes = allNodes;
        
        /*filteredNodes = _.filter(filteredNodes, function(n){
            return n.taxonomy[0] != undefined;
        });*/
        
        return filteredNodes;
    }
    
    //Removes links from allLinks whose source or target is not present in curNodes
    //Returns array of links
    var _filterLinks = function(allLinks, curNodes){
        var curNodes = _mapNodes(curNodes);
        allLinks = _.filter(allLinks, function(l){
            return (curNodes.get(l.source.id) != undefined && curNodes.get(l.target.id) != undefined);
        });
        return allLinks;
    }
    
    function _zoom() {
        vis.attr("transform", "translate(" + d3.event.translate + ")"
                 + " scale(" + d3.event.scale + ")");
    }
    
    function _mouseOver(d,i) {
        
        if(link){
            link.attr("stroke", function(l){
                return (l.source == d || l.target == d) ? "#555" : "#999";
            })
            .attr("stroke-opacity", function(l){
                return (l.source == d || l.target == d) ? 1.0 : 0.5;
            });
        }
        node.style("stroke", function(n){
            return (n.searched || _neighboring(d, n)) ? "#555" : "#999";
        })
        .style("stroke-width", function(n){
            return (n.searched || _neighboring(d, n)) ? 2.0 : 1.0;
        });
        
        d3.select(this).style("stroke","#555").style("stroke-width", 2.0);
    }
    
    function _mouseOut(d,i) {
        if(link){
            link.attr("stroke", '#999')
                .attr("stroke-opacity", 0.8);
        }
        
        node.style("stroke", '#999').style('stroke-width',1.0);
                   //-> if !n.searched then strokeFor(n) else "#555")
      //.style("stroke-width", (n) -> if !n.searched then 1.0 else 2.0)
    }
    
    //Given two nodes a and b, returns true if there is a link between them
    // Uses linkedByIndex initialized in setupData
    var _neighboring = function(a, b){
        return linkedByIndex[a.id + "," + b.id] || linkedByIndex[b.id + "," + a.id];
    }
    
    //public API
    var self = {
        toggleLayout: _toggleLayout,
        init: _init
    };
    
    return self;
};