var Manager,
    ASSETS_NS = 'http://www.wso2.org/governance/metadata';

var log = new Log();

var ASSET_LCSTATE_PROP = 'lifecycleState';
var DEFAULT_ASSET_VIEW_STATE = 'published';

(function () {

    var matchAttr = function (searchAttr, artifactAttr) {
        var attribute, attr, val, match;
        for (attribute in searchAttr) {
            if (searchAttr.hasOwnProperty(attribute)) {
                attr = searchAttr[attribute];
                val = artifactAttr[attribute];
                match = (attr instanceof RegExp) ? attr.test(val) : (attr == val);
                if (!match) {
                    return false;
                }
            }
        }
        return true;
    };

    /*
     The function checks whether two artifacts are similar
     @searchArtifact: The artifact containing the search criteria
     @artifact: The artifact to which the searchArtifact must be compared
     @return: If the two artifacts are similar True ,else False.
     */
    var matchArtifact = function (searchArtifact, artifact) {
        var status = true;//We assume that all attributes will match
        var ignoredProperty = 'attributes';
        var term = '';

        log.debug('Invoked matchArtifact');
        log.debug('Ignoring property: ' + ignoredProperty);

        //First go through all of the non attribute properties
        for (var searchKey in searchArtifact) {

            log.debug('Examining property: ' + searchKey);

            if ((searchKey != ignoredProperty) && (artifact.hasOwnProperty(searchKey))) {

                //Match against spaces and lower case
                term = artifact[searchKey] || '';
                term = term.toString().toLowerCase().trim() + '';

                //Determine if the searchKey points to an array
                if (searchArtifact[searchKey] instanceof Array) {

                    log.debug('Checking against array of values: ' + searchArtifact[searchKey]);

                    //Check if the value of the artifact property is defined in the
                    //searchArtifact property array.
                    status = (searchArtifact[searchKey].indexOf(term) != -1) ? true : false;
                }
                else {

                    //Update the status
                    status = (searchArtifact[searchKey] == term);
                }

            }
        }

        log.debug('Properties match: ' + status);

        //Only search attributes if the user has provided any
        if (searchArtifact.attributes) {

            //Check if the attributes match
            status = matchAttr(searchArtifact.attributes, artifact.attributes);

            log.debug('Attribute match : ' + status);

        }

        return status;
    }

    var search = function (that, options) {

        if (options.tag) {
            var registry = that.registry,
                tag = options.tag;
            return that.manager.find(function (artifact) {
                if (registry.tags(artifact.path).indexOf(tag) != -1) {
                    return matchAttr(options.attributes, artifact.attributes);
                }
                return false;
            });
        }
        if (options.query) {
            var query = options.query.toLowerCase();
            return that.manager.find(function (asset) {
                var name, match,
                    attributes = asset.attributes;
                for (name in attributes) {
                    if (attributes.hasOwnProperty(name)) {
                        if (attributes[name].toLowerCase().indexOf(query) !== -1) {
                            match = true;
                            break;
                        }
                    }
                }
                if (!match) {
                    return false;
                }
                return matchAttr(options.attributes, asset.attributes);
            });
        }
        if (options) {

            return that.manager.find(function (artifact) {
                // return matchAttr(options.attributes, artifact.attributes);
                return matchArtifact(options, artifact);
            });
        }
        return [];
    };

    var loadRatings = function (manager, items) {
        var i, asset,
            username = manager.username,
            length = items.length;
        for (i = 0; i < length; i++) {
            asset = items[i];
            asset.rating = manager.registry.rating(asset.path, username);
        }
        return items;
    };

    Manager = function (registry, type) {
        var carbon = require('carbon');
        this.registry = registry;
        this.type = type;
        this.username = registry.username;
        Packages.org.wso2.carbon.governance.api.util.GovernanceUtils.loadGovernanceArtifacts(registry.registry);
        this.manager = new carbon.registry.ArtifactManager(registry, type);
        this.sorter = new Sorter(registry);
    };

    var Sorter = function (registry) {
        this.registry = registry;
    };

    Sorter.prototype.recent = function (items) {
        var registry = this.registry;
        items.sort(function (l, r) {
            return registry.get(l.path).created.time < registry.get(r.path).created.time;
        });
        return items;
    };

    Sorter.prototype.popular = function (items) {
        var registry = this.registry;
        items.sort(function (l, r) {
            return registry.rating(l.path).average < registry.rating(r.path).average;
        });
        return items;
    };

    Sorter.prototype.unpopular = function (items) {
        var registry = this.registry;
        items.sort(function (l, r) {
            return registry.rating(l.path).average > registry.rating(r.path).average;
        });
        return items;
    };

    Sorter.prototype.older = function (items) {
        var registry = this.registry;
        items.sort(function (l, r) {
            return registry.get(l.path).created.time > registry.get(r.path).created.time;
        });
        return items;
    };

    Sorter.prototype.az = function (items) {
        items.sort(function (l, r) {
            return l['overview_name'] > r['overview_name'];
        });
        return items;
    };

    Sorter.prototype.za = function (items) {
        items.sort(function (l, r) {
            return l['overview_name'] < r['overview_name'];
        });
        return items;
    };

    Sorter.prototype.paginate = function (items, paging) {
        switch (paging.sort) {
            case 'recent':
                this.recent(items);
                break;
            case 'older':
                this.older(items);
                break;
            case 'popular':
                this.popular(items);
                break;
            case 'unpopular':
                this.unpopular(items);
                break;
            case 'az':
                this.az(items);
                break;
            case 'za':
                this.za(items);
                break;
            default:
                this.recent(items);
        }
        return items.slice(paging.start, (paging.start + paging.count));
    };

    /*Manager.prototype.search = function (filters, paging) {
     var all = this.manager.find(function (artifact) {
     var expected, field, actual;
     for (field in filters) {
     if (filters.hasOwnProperty(field)) {
     expected = filters[field];
     actual = artifact.attribute(field);
     if (expected instanceof RegExp) {
     if (!expected.test(actual)) {
     return false;
     }
     } else {
     return expected == actual;
     }
     }
     }
     return true;
     });
     return this.sorter.paginate(all, paging);
     };*/

    Manager.prototype.search = function (options, paging) {
        return loadRatings(this, this.sorter.paginate(search(this, options), paging));
    };

    Manager.prototype.checkTagAssets = function (options) {
        return search(this, options);
    };

    /*
     * Assets matching the filter
     */
    Manager.prototype.get = function (id) {
        return this.manager.get(id);
    };

    /*
     * Assets matching the filter
     */
    Manager.prototype.add = function (options) {
        return this.manager.add(options);
    };

    /*
     * Assets matching the filter
     */
    Manager.prototype.update = function (options) {
        return this.manager.update(options);
    };

    /*
     * Assets matching the filter
     */
    Manager.prototype.remove = function (options) {
        var assets;
        if (options.id) {
            this.manager.remove(options.id);
            return;
        }
        assets = this.search(options);
        if (assets.length > 0) {
            this.manager.remove(assets[0].id);
        }
    };


    /*
     * Assets matching the filter
     */
    Manager.prototype.list = function (paging) {

        //Obtain the visible states from the
        var storeConfig = require('/store.json').lifeCycleBehaviour;
        var visibleStates = storeConfig.visibleIn || DEFAULT_ASSET_VIEW_STATE;

        log.info('Searching for assets in the ' + visibleStates + ' states.');

        var all = this.search({
            lifecycleState: visibleStates
        }, paging);

        log.debug('Obtained assets: ' + all.length + ' in the ' + visibleStates + ' states');

        return loadRatings(this, this.sorter.paginate(all, paging));
    };


    Manager.prototype.count = function (options, storeConfig) {

        if (options) {
            return search(this, options).length;
        }

        //Obtain the visible states from the confuration file.
        //var storeConfig = require('/store.json').lifeCycleBehaviour;
        var property;
        var publishedCount = 0;
        var visibleStates = storeConfig.visibleIn || DEFAULT_ASSET_VIEW_STATE;

        //If the visible states is not an array,create one
        if (!(visibleStates instanceof Array)) {
            visibleStates = [visibleStates];
        }

        this.manager.find(function (asset) {
            var name,
                attributes = asset.attributes;

            if (asset.hasOwnProperty(ASSET_LCSTATE_PROP)) {

                property = asset[ASSET_LCSTATE_PROP] || '';

                property = property.toLowerCase().trim() + '';

                //Check if the state of the asset is one of the visibleStates
                if (visibleStates.indexOf(property) != -1) {
                    publishedCount++;
                }

            }
        });

        log.debug('Published count: ' + publishedCount);

        //return this.manager.count();
        return publishedCount;
    };

    /*
     *
     * Add comment
     Manager.prototype.comment = function (path, comment) {
     this.registry.comment(path, comment);
     };

     */
    /**
     * Get comments
     */
    /*

     Manager.prototype.comments = function () {
     return this.registry.comments();
     };

     Manager.prototype.rate = function () {

     };

     Manager.prototype.rating = function () {

     };

     Manager.prototype.invokeAspect = function () {

     };

     Manager.prototype.lifecycles = function () {

     };*/
}());
