/*
 *  Copyright (c) 2005-2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
var api = {};
(function(api) {
    var log = new Log('rating-api');
    api.rate = function(session, options) {
        var success = false; //Assume the rating will fail
        if (!options.type) {
            log.error('Unable to rate assets without a type');
            return success;
        }
        if (!options.id) {
            log.error('Unable to rate assets without an id.');
            return success;
        }
        if (!options.value) {
            log.error('Cannot rate the asset when there is no rating value provided.');
            return success;
        }
        var am = getAssetManager(session, options.type);
        try {
            var asst = am.get(options.id);
            success = am.rate(asst.path, options.value);
            //TODO get a api from registry to get ratings by id
        } catch (e) {
            log.error('Could not rate the asset type: ' + options.type + ' id: ' + options.id + ' with rating: ' + options.value + '.Exception: ' + e);
        }
        return success;
    };
    /**
     * The function adds rating details to the provided assets
     * @param {[type]} assets   [description]
     * @param {[type]} tenantId [description]
     * @param {[type]} username [description]
     */
    api.addRatings = function(assets,am,tenantId, username) {
        var carbon = require('carbon');
        var social = carbon.server.osgiService('org.wso2.carbon.social.core.service.SocialActivityService');
        var utils=require('utils').reflection;
        var tenantId = tenantId;
        var id;
        var rating;
        var average;

        //Determine if a single asset has been provided
        if(!utils.isArray(assets)){
            assets=[assets];
        }
        for (var index in assets) {
            var aid = assets[index].type + ":" + assets[index].id;
            var obj = JSON.parse(social.getRating(aid));
            var average = obj ? obj.rating : 0;
            var count = obj ? obj.count : 0; 
            assets[index].rating = rating ? average : 0;
            assets[index].avgRating = average;
            assets[index].count = count;
            assets[index].ratingPx = calculateRatingPixel(average);
        }
    };
    var calculateRatingPixel = function(avgRating) {
        var STAR_WIDTH = 84;
        var MAX_RATING = 5;
        var ratingPx = (avgRating / MAX_RATING) * STAR_WIDTH;
        return ratingPx;
    };
    var getAssetManager = function(session, type) {
        var asset = require('rxt').asset;
        var am = asset.createUserAssetManager(session, type);
        return am;
    };


    api.getPopularAssets = function(type, tenantId, am, start, pageSize){
        var carbon = require('carbon');
        var social = carbon.server.osgiService('org.wso2.carbon.social.core.service.SocialActivityService');
        var index = 0, maxTry = 0; limit = pageSize;

        var getNextAssetSet = function () {
            var offset = Number(start)+ Number(index);
            var result = JSON.parse(String(social.getPopularAssets(type, tenantId, limit, offset)));
            index += pageSize;
            return result.assets || [];
        };

        assets = [];
        var pos, aid, asset;
        while (assets.length < pageSize && maxTry < 10) {
            maxTry++;
            var result = getNextAssetSet();
            for (var n = 0; n < result.length && assets.length < pageSize; n++) {
                var combinedAid = String(result[n]);
                pos = combinedAid.indexOf(':');
                aid = combinedAid.substring(pos + 1);
                try {
                    /*asset = store.asset(type, aid);
                     asset.indashboard = store.isuserasset(aid, type);*/
                    asset = am.get(aid);
                    if (configs.lifeCycleBehaviour.visibleIn.indexOf(String(asset.lifecycleState), 0) >= 0) {
                        assets.push(asset);
                    }
                } catch (e) {
                    log.warn("Error retrieving asset from store, information might be stale in social cache. id=" +
                        combinedAid + e);
                }
            }
        }

        return assets;
    }





}(api));