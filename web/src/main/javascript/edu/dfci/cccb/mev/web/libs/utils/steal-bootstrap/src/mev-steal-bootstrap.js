"use strict";    
(function(global){

  function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? undefined : decodeURIComponent(results[1].replace(/\+/g, " "));
  }

  function writeCookie(name,value,days) {
    if (days) {
      var date = new Date();
      date.setTime(date.getTime()+(days*24*60*60*1000));
      var expires = "; expires="+date.toGMTString();
    }
    else var expires = "";
    document.cookie = name+"="+value+expires+"; path=/";
  }

  function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
      var c = ca[i];
      while (c.charAt(0)==' ') c = c.substring(1,c.length);
      if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
  }

  function eraseCookie(name) {
    writeCookie(name,"",-1);
  }

  function getEnv(){      
    var env = getParameterByName("env") || readCookie("env");
    if(env) return env;
    
    if(window.location.href.indexOf("localhost")>=0 ||
       window.location.href.indexOf("dfci")>=0){
       return "bundle";
    }

    return "prod";
  }

  function getConfig(baseURL, main, livePort, live){
    var env = "prod";
    var system = {
      config: baseURL+"package.json!npm",
      baseURL: baseURL,
      bundlesPath: baseURL+"dist/bundles/min",
      main: main,
      loadBundles: true,
      getStealSrc: function getStealSrc(){      
        var stealsrc = baseURL+"/node_modules/steal/steal.js";          
        if(this && this.loadBundles)
          stealsrc = this.bundlesPath+"/"+this.main+".js"      
        return stealsrc;
      }
    };
    
    var env = getEnv();
    if(env.indexOf("bundle")>=0){
      system.bundlesPath = system.bundlesPath.replace("/min", "/dev");
    }else if(env.indexOf("live")>=0){
      system.loadBundles = false;
      system.main = live || main;
      system.config = baseURL+"package.json!npm";
      delete system.baseURL;        
      system.configDependencies=["live-reload"];        
      system.liveReloadPort=livePort;
    }
    return system;
  }

  function startSteal(steal){  
    console.log("steal", steal);
    var stealscript = document.createElement('script');
    stealscript.setAttribute('src',steal.getStealSrc());
    stealscript.setAttribute('charset',"UTF-8");
    document.head.appendChild(stealscript);
  }

  var getConfig2 = function(pathToPackage, main, livePort){
    var baseURL = pathToPackage.replace("package.json!npm", "");
    return getConfig(baseURL, main, livePort);
  };

  global.startStealLive = function startSteal2(pathToPackage, main, livePort){  

    writeCookie("env", "live");
    window.steal = getConfig2(pathToPackage, main, livePort);

    console.log("steal", steal);
    var stealscript = document.createElement('script');
    stealscript.setAttribute('src',steal.getStealSrc());
    stealscript.setAttribute('charset',"UTF-8");
    document.head.appendChild(stealscript);
  }
  global.live=global.startStealLive;
  global.writeCookie = writeCookie;
  global.readCookie = readCookie;
  global.eraseCookie = eraseCookie;
  global.mevEnv=function(env){
    if(env)
      this.writeCookie("env", env);
    else
      this.readCookie("env");
  };
})(window);
