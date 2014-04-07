//Help with the placement of nodes
var RadialPlacement = function(){
    
    var values = d3.map(), radius = 200, center = {"x":0, "y":0}, groups = 5;
    
    // Given an center point, angle, and radius length,
    // return a radial position for that angle
    var radialLocation = function(center, angle, radius){
        var x = (center.x + radius * Math.cos(angle * Math.PI / 180));
        var y = (center.y + radius * Math.sin(angle * Math.PI / 180));
        return {"x":x,"y":y};
    }
    
    var setKeys = function(keys){
        // start with an empty values
        values = d3.map();
        
        keys = _.uniq(keys);
        
        var arrays = [];
        while(keys.length > 0)
            arrays.push(keys.splice(0,groups+1));
        
        _.each(arrays, function(arr,i){
            //console.log(arr,i);
            
            var scale = d3.scale.linear().domain([0, arr.length-1]).range([0, 360]),
                r = (i+1)*radius / 1.8;
            
            _.each(arr, function(key, j){
                var angle = (i%2 != 0) ? scale(j)+scale(0.5) : scale(j);
                var value = radialLocation(center, angle, r);
                values.set(key,value);
            });
            
        });
    }
    
    // Main entry point for RadialPlacement
    // Returns location for a particular key,
    // creating a new location if necessary.
    var placement = function(key){
        var value = values.get(key);
        return value;
    }
    
    placement.keys = function(_){
        if (!arguments.length)
            return d3.keys(values);
        setKeys(_);
        return placement;
    };
    
    placement.center = function(_){
        if (!arguments.length)
            return center;
        center = _;
        return placement
    };
    
    placement.radius = function(_){
        if (!arguments.length)
            return radius;
        radius = _;
        return placement;
    };
    
    placement.groups = function(_){
        if (!arguments.length)
            return groups;
        groups = _;
        return placement;
    };
    
    return placement;
}