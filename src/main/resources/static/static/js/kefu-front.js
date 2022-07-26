var KEFU={
    KEFU_URL:"",
    KEFU_KEFU_ID:"",
    KEFU_ENT:"",
    KEFU_LANG:"en",
    KEFU_EXTRA: {},
    KEFU_AUTO_OPEN:true,//是否自动打开
    KEFU_SHOW_TYPES:1,//展示样式，1：普通右下角，2：圆形icon
    KEFU_AUTO_SHOW:false,
    KEFU_WITHOUT_BOX:false,
};
KEFU.launchButtonFlag=false;
KEFU.titleTimer=0;
KEFU.titleNum=0;
KEFU.noticeTimer=null;
KEFU.originTitle=document.title;
KEFU.chatPageTitle="KEFU";
KEFU.kefuName="";
KEFU.kefuAvator="";
KEFU.kefuIntroduce="";
KEFU.kefuDialogDelay="3000";
KEFU.offLine=false;
KEFU.TEXT={
    "cn":{
        "online_notice":"和我们在线交谈",
        "offline_notice":"离线请留言",
    },
    "en":{
        "online_notice":"we are online,chat with us",
        "offline_notice":"we are offline",
    },
};
KEFU.init=function(config){
    var _this=this;
    if(typeof config=="undefined"){
        return;
    }

    if (typeof config.KEFU_URL!="undefined"){
        this.KEFU_URL=config.KEFU_URL.replace(/([\w\W]+)\/$/,"$1");
    }
    this.dynamicLoadCss(this.KEFU_URL+"/static/css/kefu-front.css?v="+Date.now());
    this.dynamicLoadCss(this.KEFU_URL+"/static/css/layui/css/layui.css?v="+Date.now());
    if (typeof config.KEFU_KEFU_ID!="undefined"){
        this.KEFU_KEFU_ID=config.KEFU_KEFU_ID;
    }
    if (typeof config.KEFU_ENT!="undefined"){
        this.KEFU_ENT=config.KEFU_ENT;
    }
    if (typeof config.KEFU_EXTRA!="undefined"){
        this.KEFU_EXTRA=config.KEFU_EXTRA;
    }
    if (typeof config.KEFU_AUTO_OPEN!="undefined"){
        this.KEFU_AUTO_OPEN=config.KEFU_AUTO_OPEN;
    }
    if (typeof config.KEFU_SHOW_TYPES!="undefined"){
        this.KEFU_SHOW_TYPES=config.KEFU_SHOW_TYPES;
    }
    if (typeof config.KEFU_WITHOUT_BOX!="undefined"){
        this.KEFU_WITHOUT_BOX=config.KEFU_WITHOUT_BOX;
    }
    var refer=document.referrer?document.referrer:"无";
    this.KEFU_EXTRA.refer=refer;
    this.KEFU_EXTRA.host=document.location.href;
    this.KEFU_EXTRA=JSON.stringify(_this.KEFU_EXTRA);

    this.dynamicLoadJs(this.KEFU_URL+"/static/js/functions.js?v=1",function(){
        if (typeof config.KEFU_LANG!="undefined"){
            _this.KEFU_LANG=config.KEFU_LANG;
        }else{
            _this.KEFU_LANG=checkLang();
        }
        _this.KEFU_EXTRA=utf8ToB64(_this.KEFU_EXTRA);
        if(window.jQuery||typeof $=="function"){
            _this.dynamicLoadJs("https://cdn.staticfile.org/layer/3.4.0/layer.min.js",function () {
                _this.jsCallBack();
            });
        }else{
            _this.dynamicLoadJs("https://cdn.staticfile.org/jquery/3.6.0/jquery.min.js",function () {
                _this.dynamicLoadJs("https://cdn.staticfile.org/layer/3.4.0/layer.min.js",function () {
                    _this.jsCallBack();
                });
            });
        }
    });


    _this.addEventlisten();
}
KEFU.jsCallBack=function(){
    var _this=this;
    if(_this.isMobile()){
        _this.KEFU_SHOW_TYPES=2;
        //_this.KEFU_AUTO_OPEN=false;
    }
    _this.getNotice(function(welcomeInfo){
        //展示的样式
        switch(_this.KEFU_SHOW_TYPES){
            case 1:
                _this.showPcTips(welcomeInfo);
                break;
            case 2:
                _this.showCircleIcon(welcomeInfo);
                break;
            case 3:
                _this.showLineTips(welcomeInfo);
                break;
            default:

        }
        _this.addClickEvent();
    });
}
//pc端的样式
KEFU.showPcTips=function(welcomeInfo){
    var _this=this;
    _this.kefuAvator=getImageUrl(welcomeInfo.avatar,_this.KEFU_URL);
    _this.kefuName=welcomeInfo.username;
    _this.chatPageTitle=welcomeInfo.username;
    _this.offLine=welcomeInfo.all_offline;
    if(welcomeInfo.all_offline){
        _this.KEFU_AUTO_OPEN=false;
        var userInfo=KEFU.TEXT[_this.KEFU_LANG].offline_notice;
    }else{
        var userInfo=KEFU.TEXT[_this.KEFU_LANG].online_notice;
    }
    //自动展开
    if(_this.KEFU_AUTO_OPEN&&_this.isIE()<=0){
        setTimeout(function () {
            _this.showKefu();
        },_this.kefuDialogDelay);
    }
    // var html="<div class='launchButtonBox'>" +
    //     '<div id="launchButton" class="launchButton">' +
    //     '<div id="launchIcon" class="launchIcon">1</div> ' +
    //     '<div class="launchButtonText">'+userInfo+'<img src="'+_this.KEFU_URL+'/static/images/up_arrow.png"/></div></div>' +
    //     '<div id="launchButtonNotice" class="launchButtonNotice"></div>' +
    //     '</div>';
    var html=`
    <div class='launchButtonBox'>
        <div id="launchButton" class="launchButton">
            <div id="launchIcon" class="launchIcon">1</div>
                <div class="launchButtonText">
                    <img src="`+_this.KEFU_URL+`/static/images/wechatLogo.png"/>
                    <span class='flyUsername'>`+userInfo+`</span>
                </div>
            <div id="launchButtonNotice" class="launchButtonNotice"></div>
        </div>
    </div>
`
    jQuery('body').append(html);
    if(_this.KEFU_AUTO_OPEN){
        return;
    }
    if (!welcomeInfo.welcome) return;
    var msgs = welcomeInfo.welcome;
    var len=msgs.length;
    var i=0;
    if(len<=0) {
        return;
    }
    var delaySecond=0;
    for(let i in msgs){
        var msg=msgs[i];
        if(msg.delay_second){
            delaySecond+=msg.delay_second;
        }else{
            delaySecond+=4;
        }
        var timer =  setTimeout(function (msg) {
            msg.content = replaceSpecialTag(msg.content);



            var welcomeHtml="<div class='flyUser'><img class='flyAvatar' src='"+_this.kefuAvator+"'/> <span class='flyUsername'>"+msg.name+"</span>" +
                "<span id='launchNoticeClose' class='flyClose'>×</span>" +
                "</div>";
            welcomeHtml+="<div id='launchNoticeContent'>"+replaceSpecialTag(msg.content,_this.KEFU_URL)+"</div>";

            var obj=jQuery("#launchButtonNotice");
            if(obj){
                obj.html(welcomeHtml).fadeIn();
                setTimeout(function (obj) {
                    obj.fadeOut();
                },3000,obj);
            }

            i++;
            jQuery("#launchIcon").text(i).fadeIn();
        },1000*delaySecond,msg);
    }
}
//pc端的第二种样式
KEFU.showLineTips=function(welcomeInfo){
    var _this=this;
    _this.kefuAvator=getImageUrl(welcomeInfo.avatar,_this.KEFU_URL);
    _this.kefuName=welcomeInfo.username;
    _this.chatPageTitle=welcomeInfo.username;
    _this.offLine=welcomeInfo.all_offline;
    //自动展开
    if(_this.KEFU_AUTO_OPEN&&_this.isIE()<=0){
        setTimeout(function () {
            _this.showKefu();
        },10000);
    }
   str=`
<div class="lineBox">
    <div class="lineItem" onclick="javascript:KEFU.showPanel();">
        <i class="layui-icon">&#xe606;</i>
    </div>
    <div class="lineItem">
        <i class="layui-icon">&#xe677;</i>
        <div class="lineTip lineWechat">
            <img class="lineWechat" src="/static/images/wechat.jpg"/>
        </div>
    </div>
    <div class="lineItem">
        <i class="layui-icon">&#xe676;</i>
        <div class="lineTip">
            QQ:xxx
        </div>
    </div>
    <div class="lineItem">
        <i class="layui-icon">&#xe626;</i>
        <div class="lineTip">
            QQ:xxx
        </div>
    </div>
    <div class="lineItem lineTop" id="launchTopButton">
        <i class="layui-icon">&#xe604;</i>
    </div>
</div>`
    jQuery('body').append(str);
}
//圆形样式
KEFU.showCircleIcon=function(welcomeInfo){
    var _this=this;
    _this.kefuAvator=getImageUrl(welcomeInfo.avatar,_this.KEFU_URL);
    _this.kefuName=welcomeInfo.username;
    _this.chatPageTitle=welcomeInfo.username;
    this.offLine=welcomeInfo.all_offline;
    if(welcomeInfo.all_offline){
        var imgUrl=KEFU.KEFU_URL+"/static/images/iconchat.png";
        var tipText=KEFU.TEXT[this.KEFU_LANG].offline_notice
        var imgHtml="<img class='flySimpleDefaultImg' src='"+imgUrl+"'/> ";
    }else{
        var imgUrl=_this.kefuAvator;
        var tipText=KEFU.TEXT[this.KEFU_LANG].online_notice
        var imgHtml="<img class='flySimpleUserImg' src='"+imgUrl+"'/> ";
    }

    var html="<div class='flySimpleIconBox'>" +
        "<div class='flySimpleIcon'>" +
        imgHtml+
        '</div>' +
        "<div class='flySimpleIconTip'> " +tipText+
        "<div class='flyClose'>×</div>" +
        "</div>" +
        '</div>';
    jQuery('body').append(html);
    setTimeout(function () {
        jQuery(".flySimpleIconTip").fadeIn();
        setTimeout(function () {
            jQuery(".flySimpleIconTip").fadeOut();
        },5000);
    },_this.kefuDialogDelay);
    window.addEventListener('message',function(e){
        var msg=e.data;
        if(msg.type=="message"){
            jQuery(".flySimpleIconTip").html(replaceSpecialTag(msg.data.content,_this.KEFU_URL)+"<div class='flyClose'>×</div>").show();
            setTimeout(function () {
                jQuery(".flySimpleIconTip").fadeOut();
            },5000);
        }
        if(msg.type=="force_close"){
            layer.close(layer.index);
        }
    });

    //自动展开
    if(_this.KEFU_AUTO_OPEN&&_this.isIE()<=0){
        setTimeout(function () {
            _this.showKefu();
        },5000);
    }
}
KEFU.addClickEvent=function(){
    var _this=this;
    jQuery("#launchButton").on("click",function() {
        if(_this.launchButtonFlag){
            return;
        }
        _this.KEFU_AUTO_SHOW=true;
        _this.showKefu();
        jQuery("#launchIcon").text(0).hide();
    });

    jQuery("body").on("click","#launchNoticeClose",function() {
        jQuery("#launchButtonNotice").fadeOut();
    });
    jQuery("body").on("click",".flySimpleIconTip",function() {
        jQuery(".flySimpleIconTip").fadeOut();
    });
    jQuery("body").on("click","#launchTopButton",function() {
        jQuery('body,html').scrollTop(0);
    });
    jQuery("body").on("mouseover mouseout",".lineItem",function(event) {
        if(event.type == "mouseover"){
            //鼠标悬浮
            jQuery(this).find(".lineTip").show();
        }else if(event.type == "mouseout"){
            //鼠标离开
            jQuery(".lineTip").hide();
        }
    });
    jQuery("body").click(function () {
        clearTimeout(_this.titleTimer);
        document.title = _this.originTitle;
        //剪贴板
        try{
            var selecter = window.getSelection().toString();
            if (selecter != null && selecter.trim() != ""){
                var str=selecter.trim().substr(0,20);
                _this.postMessageToIframe(str);
            }
        } catch (err){
            var selecter = document.selection.createRange();
            var s = selecter.text;
            if (s != null && s.trim() != ""){
                var str=s.trim().substr(0,20);
                _this.postMessageToIframe(str);
            }
        }
    });
    var ms= 1000*2;
    var lastClick = Date.now() - ms;
    jQuery("a,div,p,li").mouseover(function(){
        if (Date.now() - lastClick >= ms) {
            lastClick = Date.now();
            var msg=jQuery(this).text().trim().substr(0,20);
            _this.postMessageToIframe(msg);
        }
    });
    jQuery("body").on("click",".flySimpleIcon",function() {
        _this.showPanel();
    });
}

KEFU.postMessageToIframe=function(str){
    var msg={}
    msg.type='inputing_message';
    msg.content=str;
    this.postToIframe(msg);
}
KEFU.postToIframe=function(messageObj){
    var obj=document.getElementById('layui-layer-iframe19911116');
    if(!obj||!messageObj){
        return;
    }
    document.getElementById('layui-layer-iframe19911116').contentWindow.postMessage(messageObj, "*");
}
KEFU.addEventlisten=function(){
    var _this=this;
    window.addEventListener('message',function(e){
        var msg=e.data;
        if(msg.type=="message"){
            clearInterval(_this.noticeTimer);
            var width=jQuery(window).width();
            if(width>768){
                _this.flashTitle();//标题闪烁
            }
            if (_this.launchButtonFlag){
                return;
            }
            var welcomeHtml="<div class='flyUser'><img class='flyAvatar' src='"+_this.kefuAvator+"'/> <span class='flyUsername'>"+msg.data.name+"</span>" +
                "<span id='launchNoticeClose' class='flyClose'>×</span>" +
                "</div>";
            welcomeHtml+="<div id='launchNoticeContent'>"+replaceSpecialTag(msg.data.content,_this.KEFU_URL)+"</div>";
            var obj=jQuery("#launchButtonNotice");
            if(obj){
                obj.html(welcomeHtml).fadeIn();
                setTimeout(function (obj) {
                    obj.fadeOut();
                },3000,obj);
            }
            var news=jQuery("#launchIcon").text();
            jQuery("#launchIcon").text(++news).show();
        }
        if(msg.type=="focus"){
            clearTimeout(_this.titleTimer);
            _this.titleTimer=0;
            document.title = _this.originTitle;
        }
        if(msg.type=="force_close"){
            layer.close(layer.index);
        }
    });
    window.onfocus = function () {
        clearTimeout(_this.titleTimer);
        _this.titleTimer=0;
        document.title = _this.originTitle;
        _this.postToIframe({type:"focus"});
    };
}
KEFU.dynamicLoadCss=function(url){
    var head = document.getElementsByTagName('head')[0];
    var link = document.createElement('link');
    link.type='text/css';
    link.rel = 'stylesheet';
    link.href = url;
    head.appendChild(link);
}
KEFU.dynamicLoadJs=function(url, callback){
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;
    if(typeof(callback)=='function'){
        script.onload = script.onreadystatechange = function () {
            if (!this.readyState || this.readyState === "loaded" || this.readyState === "complete"){
                callback();
                script.onload = script.onreadystatechange = null;
            }
        };
    }
    head.appendChild(script);
}

//获取基础信息
KEFU.getNotice=function(callback){
    var _this=this;
    jQuery.get(this.KEFU_URL+"/notice?ent_id="+this.KEFU_ENT,function(res) {
        if(!res) return;
        var welcomeInfo=res.result;
        _this.kefuIntroduce=welcomeInfo.ent_introduce;
        if(welcomeInfo.delay_second){
            _this.kefuDialogDelay=welcomeInfo.delay_second;
        }
        callback(welcomeInfo);
    });
}
KEFU.isIE=function(){
    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
    var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; //判断是否IE<11浏览器
    var isEdge = userAgent.indexOf("Edge") > -1 && !isIE; //判断是否IE的Edge浏览器
    var isIE11 = userAgent.indexOf('Trident') > -1 && userAgent.indexOf("rv:11.0") > -1;
    if(isIE) {
        var reIE = new RegExp("MSIE (\\d+\\.\\d+);");
        reIE.test(userAgent);
        var fIEVersion = parseFloat(RegExp["$1"]);
        if(fIEVersion == 7) {
            return 7;
        } else if(fIEVersion == 8) {
            return 8;
        } else if(fIEVersion == 9) {
            return 9;
        } else if(fIEVersion == 10) {
            return 10;
        } else {
            return 6;//IE版本<=7
        }
    } else if(isEdge) {
        return 'edge';//edge
    } else if(isIE11) {
        return 11; //IE11
    }else{
        return -1;//不是ie浏览器
    }
}
KEFU.showPanel=function (){
    var width=jQuery(window).width();
    this.KEFU_AUTO_SHOW=true;
    if(this.isIE()>0){
        this.windowOpen();
        return;
    }
    if(width<768){
        this.layerOpen("100%","72%");
        return;
    }
    this.layerOpen("435px","550px");
    this.launchButtonFlag=true;
}
KEFU.showKefu=function (){
    if (this.launchButtonFlag) return;
    var width=jQuery(window).width();
    if(this.isIE()>0){
        this.windowOpen();
        return;
    }
    if(width<768){
        this.layerOpen("100%","72%");
        return;
    }
    this.layerOpen("450px","580px");
    this.launchButtonFlag=true;
    jQuery(".launchButtonBox").hide();
}
KEFU.layerOpen=function (width,height){
    if (this.launchButtonFlag) return;
    var layBox=jQuery("#layui-layer19911116");
    if(layBox.css("display")=="none"){
        layBox.show();
        return;
    }
    var onlineStatus="<i></i>";
    if(this.offLine){
        onlineStatus="<i class='offline'></i>";
    }
    var title=`
    <div class="kfBar">
        <div class="kfBarAvator">
            <img src="`+this.kefuAvator+`" class="kfBarLogo">
            `+onlineStatus+`
        </div>
        <div class="kfBarText">
            <div class="kfName">`+this.kefuName+`</div>
            <div class="kfDesc">`+this.kefuIntroduce+`</div>
         </div>
    </div>
    `;
    var _this=this;
    layer.index="19911115";
    layer.open({
        type: 2,
        title: title,
        skin:"kfLayer",
        closeBtn: 1, //不显示关闭按钮
        shade: 0,
        area: [width, height],
        offset: 'rb', //右下角弹出
        anim: 2,
        content: [this.KEFU_URL+'/chatIndex?kefu_id='+this.KEFU_KEFU_ID+'&ent_id='+this.KEFU_ENT+'&lang='+this.KEFU_LANG+'&refer='+window.document.title+'&url='+document.location.href+'&extra='+this.KEFU_EXTRA , 'yes'], //iframe的url，no代表不显示滚动条
        success:function(){
            var layBox=jQuery("#layui-layer19911116");
            _this.launchButtonFlag=true;
            if(!_this.KEFU_WITHOUT_BOX&&_this.KEFU_AUTO_SHOW&&layBox.css("display")=="none"){
                layBox.show();
            }
            jQuery("#layui-layer-iframe19911116 .chatEntTitle").hide();
        },
        end: function(){
            _this.launchButtonFlag=false;
            jQuery(".launchButtonBox").show();
        },
        cancel: function(index, layero){
            jQuery("#layui-layer19911116").hide();
            _this.launchButtonFlag=false;
            jQuery(".launchButtonBox").show();
            return false;
        }
    });
}
KEFU.windowOpen=function (){
   window.open(this.KEFU_URL+'/chatIndex?kefu_id='+this.KEFU_KEFU_ID+'&lang='+this.KEFU_LANG+'&refer='+window.document.title+'&ent_id='+this.KEFU_ENT+'&extra='+this.KEFU_EXTRA);
}
KEFU.flashTitle=function () {
    if(this.titleTimer!=0){
        return;
    }
    this.titleTimer = setInterval("KEFU.flashTitleFunc()", 500);
}
KEFU.flashTitleFunc=function(){
    this.titleNum++;
    if (this.titleNum >=3) {
        this.titleNum = 1;
    }
    if (this.titleNum == 1) {
        document.title = '【】' + this.originTitle;
    }
    if (this.titleNum == 2) {
        document.title = '【new message】' + this.originTitle;
    }
}
/**
 * 判断是否是手机访问
 * @returns {boolean}
 */
KEFU.isMobile=function () {
    if( /Mobile|Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) ) {
        return true;
    }
    return false;
}

