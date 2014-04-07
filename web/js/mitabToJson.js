var mitabToJson = function(mitab){
    var lines = mitab.split('\n'), nodes = {}, links = [], textInParenthesis = /\((.*?)\)/, textInQuotes = /\"(.*?)\"/, textInTax = /\:(.*?)\(/;
    
    _.each(lines, function(line, i){
        
        var fields = line.split('\t');
        if(line != undefined && fields.length >= 15){
            
            var nodeA = getNode(fields[0], fields[2], fields[9]);
            var nodeB = getNode(fields[1], fields[3], fields[10]);
            
            var interaction = {
                source: nodeA.id,
                target: nodeB.id,
                detMethods: _.map(fields[6].split('|'), mapField),
                firstAuthor: fields[7].split('|'),
                publications: _.map(fields[8].split('|'), mapPub),
                intTypes: _.map(fields[11].split('|'), mapField),
                sourceDbs: _.map(fields[12].split('|'), mapField),
                intIds : _.map(fields[13].split('|'), mapPub),
                scores: _.map(fields[14].split('|'), mapScore)
            };
            
            links.push(interaction);
            nodes[nodeA.id] = nodeA;
            nodes[nodeB.id] = nodeB;
        }
    });
    
    function getNode(idStr, altIdsStr, taxStr){
        var ids = _.map(idStr.split('|'), mapPub);
        var node = {
            id: ids[0].value,
            ids: ids,
            altIds: _.map(altIdsStr.split('|'), mapPub),
            taxonomy: _.map(taxStr.split('|'), mapTaxonomy)
        }
        return node;
    }
    
    function  mapScore(scoreStr){
        var arr = scoreStr.split(':');
        return {name:arr[0], score:+arr[1]};
    };
    
    function mapField(fieldStr){
        if(fieldStr.match(textInQuotes) == null || fieldStr.match(textInParenthesis) == null){
            var arr = fieldStr.split(':');
            return {name:arr[0], score:arr[1]};
        }
        return {name:fieldStr.match(textInQuotes)[1], value:fieldStr.match(textInParenthesis)[1]};
    };
    
    function  mapPub(pubStr){
        var arr = pubStr.split(':');
        return {name:arr[0], value:arr[1]};
    };
    
    function  mapId(idStr){
        var arr = idStr.split(':');
        return  arr[1];
    };
    
    function mapTaxonomy(taxStr){
        if(taxStr != '-'){
            return (taxStr.match(textInTax) == null) ? taxStr.split(':')[1] : taxStr.match(textInTax)[1];
        }
            
    };
    
    return {nodes:_.values(nodes), links:links};
};