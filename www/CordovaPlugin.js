cordova.define("com.cordova.plugin.CordovaPlugin", function(require, exports, module) {
/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

var exec = require("cordova/exec");
module.exports = {
    /*
     @info
     getCurrentLocation

     @params

     successCallback: Function
         @param coord_interface:object
             @param accurancy:integer
             @param latitude:integer
             @param longitude:integer
             @param heading:float
             @param altitude:float
             @param speed:float
         @param timestamp:integer

     errorCallback: Function
         options:{
             maximumAge: integer,
             timeout: integer,
             enableHighAccuracy: boolean
         }
     */
    getCurrentLocation : function (successCallback, errorCallback, options) {
        var maximumAge = options && options.maximumAge && options.maximumAge != 0 ? options.maximumAge : -1;
        var timeout = options && options.timeout && options.timeout != Infinity ? options.timeout : -1;
        var enableHighAccuracy = options && options.enableHighAccuracy ? options.enableHighAccuracy  : false;
        exec(successCallback, errorCallback, "CordovaPlugin", "request", [maximumAge,timeout,enableHighAccuracy]);
    }

};

});
