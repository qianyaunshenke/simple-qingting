KEFU_ID=KEFU_ID!=""? KEFU_ID:"kefu2";

new Vue({
    el: '#app',
    delimiters:["<{","}>"],
    data: {
        window:window,
        server:getWsBaseUrl()+"/ws_visitor",
        socket:null,
        msgList:[],
        msgListNum:[],
        messageContent:"",
        chatTitle:GOFLY_LANG[LANG]['connecting'],
        visitor:{},
        face:[],
        showKfonline:false,
        socketClosed:false,
        focusSendConn:false,
        wsSocketClosed:true,
        timer:null,
        loadingTimer:null,
        sendDisabled:false,
        entLogo:"",
        entName:"",
        peer:null,
        peerjsId:"",
        kefuPeerId:"",
        loading:null,
        localStream:null,
        flyLang:GOFLY_LANG[LANG],
        lang:LANG,
        textareaFocused:false,
        replys:[],
        noticeName:"",
        noticeAvatar:"",
        allOffline:false,
        visitorContact:{
            email:"",
            weixin:"",
            name:"",
            msg:"",
        },
        haveUnreadMessage:false,
        audioDialog:false,
        recorder:null,
        recorderAudio:null,
        recordeTimer:null,
        recoderSecond:0,
        currentActiveTime:Date.now(),
        timeoutTimer:null,
        timeoutLongTime:20*60*1000,//20分钟没反应
        allTimeouter:[],
        currentPage:1,
        showLoadMore:false,
        loadMoreDisable:false,
        websocketOpenNum:0,//websocket打开次数
        websocketMaxOpenNum:10,//websocket最大打开次数
        talkBtnText:"按住 说话",
        recorderEnd:false,
        isMobile:false,
        isIframe:false,
        onlineType:"success",
    },
    methods: {
        //初始化websocket
        initConn:function() {
            this.socket = new ReconnectingWebSocket(this.server+"?visitor_id="+this.visitor.visitor_id+"&to_id="+this.visitor.to_id);//创建Socket实例
            this.socket.debug = true;
            this.socket.onmessage = this.OnMessage;
            this.socket.onopen = this.OnOpen;
            this.socket.onerror = this.OnError;
            this.socket.onclose = this.OnClose;
        },
        OnOpen:function() {
            console.log("ws:onopen");
            //限制最大打开次数
            if(this.websocketOpenNum>=this.websocketMaxOpenNum){
                this.chatTitle=GOFLY_LANG[LANG]['refresh'];
                this.socket.close();
                return;
            }
            this.websocketOpenNum++;

            this.chatTitle=this.noticeName+" , "+GOFLY_LANG[LANG]['connectok'];
            this.checkTimeout();
            this.socketClosed=false;
            this.focusSendConn=false;
            this.wsSocketClosed=false;
            this.sendVisitorLogin();
            this.getExtendInfo();
        },
        OnMessage:function(e) {
            console.log("ws:onmessage");
            this.socketClosed=false;
            this.focusSendConn=false;
            const redata = JSON.parse(e.data);
            if (redata.type == "kfOnline") {
                let msg = redata.data
                if(this.showKfonline && this.visitor.to_id==msg.id){
                    return;
                }
                this.visitor.to_id=msg.id;
                this.chatTitle=msg.name+","+GOFLY_LANG[LANG]['chating'];
                $(".chatBox").append("<div class=\"chatTime\">"+this.chatTitle+"</div>");
                this.scrollBottom();
                this.showKfonline=true;
            }
            if (redata.type == "transfer") {
                var kefuId = redata.data
                if(!kefuId){
                    return;
                }
                this.visitor.to_id=kefuId;
            }
            if (redata.type == "notice") {
                let msg = redata.data
                if(!msg){
                    return;
                }
                this.chatTitle=msg
                $(".chatBox").append("<div class=\"chatTime\">"+this.chatTitle+"</div>");
                this.scrollBottom();
            }
            if (redata.type == "peerid") {
                let msg = redata.data;
                if(!msg){
                    return;
                }
                this.kefuPeerId=msg;
                this.talkPeer();
            }
            if (redata.type == "delete") {
                var msg = redata.data;
                for(var i=0;i<this.msgList.length;i++){
                    if(this.msgList[i].msg_id==msg.msg_id){
                        this.msgList[i].content=GOFLY_LANG[LANG]['messageDeleted'];
                        this.msgList.splice(i,1);
                        //break;
                    }
                }
            }
            if (redata.type == "read") {
                var msg = redata.data;
                for(var i=0;i<this.msgList.length;i++){
                    this.msgList[i].read_status=GOFLY_LANG[LANG]['read'];
                }
            }
            if (redata.type == "message") {
                let msg = redata.data
                //this.visitor.to_id=msg.id;


                var msgArr=msg.content.split("[b]");
                for(var i in msgArr){
                    let content = {}
                    content.avator = msg.avator;
                    content.name = msg.name;
                    content.content =replaceSpecialTag(msgArr[i]);
                    content.is_kefu = false;
                    content.time = msg.time;
                    content.is_reply=true;
                    content.msg_id = msg.msg_id;
                    this.msgList.push(content);
                    this.scrollBottom();
                }

                // let content = {}
                // content.avator = msg.avator;
                // content.name = msg.name;
                // content.content =replaceSpecialTag(msg.content);
                // content.is_kefu = false;
                // content.time = msg.time;
                // content.msg_id = msg.msg_id;
                // this.msgList.push(content);

                notify(msg.name, {
                    body: msg.content,
                    icon: msg.avator
                },function(notification) {
                    window.focus();
                    notification.close();
                });
                this.scrollBottom();
                flashTitle();//标题闪烁
                //clearInterval(this.timer);
                this.cleanAllTimeout();
                this.alertSound();//提示音
                this.haveUnreadMessage=true;
            }
            if (redata.type == "close") {
                this.chatTitle=GOFLY_LANG[LANG]['closemes'];
                $(".chatBox").append("<div class=\"chatTime\">"+this.chatTitle+"</div>");
                this.scrollBottom();
                this.socket.close();
                //this.socketClosed=true;
                this.focusSendConn=true;
            }
            if (redata.type == "force_close") {
                //this.chatTitle=GOFLY_LANG[LANG]['forceclosemes'];
                //$(".chatBox").append("<div class=\"chatTime\">"+this.chatTitle+"</div>");
                //this.scrollBottom();
                this.socket.close();
                this.socketClosed=true;
            }
            if (redata.type == "auto_close") {
                //this.chatTitle=GOFLY_LANG[LANG]['autoclosemes'];
                //$(".chatBox").append("<div class=\"chatTime\">"+this.chatTitle+"</div>");
                //this.scrollBottom();
                this.socket.close();
                this.socketClosed=true;
            }
            if (redata.type == "change_id") {
                let msg = redata.data;
                var openId=msg.to;
                var visitor=this.getCache("visitor_"+ENT_ID);
                visitor.visitor_id=openId;
                this.setCache("visitor_"+ENT_ID,visitor);
                location.reload();
            }
            window.parent.postMessage(redata,"*");
        },
        //发送给客户
        chatToUser:function() {
            if(this.sendDisabled){
                return;
            }
            var messageContent=this.messageContent.trim("\r\n");
            messageContent=messageContent.replace("\n","");
            messageContent=messageContent.replace("\r\n","");
            if(messageContent==""||messageContent=="\r\n"){
                this.messageContent="";
                return;
            }
            this.messageContent=messageContent;
            this.currentActiveTime=Date.now();
            if(this.socketClosed){
                this.initConn();
                // this.$message({
                //     message: '连接关闭!请重新打开页面',
                //     type: 'warning'
                // });
                //return;
            }
            this.sendDisabled=true;
            let _this=this;

            let content = {}
            content.avator=_this.visitor.avator;
            content.content = replaceContent(_this.messageContent);
            content.name = _this.visitor.name;
            content.is_kefu = true;
            content.read_status = GOFLY_LANG[LANG]['unread'];
            content.time = _this.getNowDate();
            content.show_time=false;
            _this.msgList.push(content);
            _this.scrollBottom();

            let mes = {};
            mes.type = "visitor";
            mes.content = this.messageContent;
            mes.from_id = this.visitor.visitor_id;
            mes.to_id = this.visitor.to_id;
            mes.content = this.messageContent;
            //发送消息
            $.post("/2/message",mes,function(res){
                _this.sendDisabled=false;
                if(res.code!=200){
                    _this.msgList.pop();
                    _this.$message({
                        message: res.msg,
                        type: 'error'
                    });
                    if(res.code==401){
                        setTimeout(function(){
                            window.location.reload();
                        },2000);
                    }
                    return;
                }
                _this.messageContent = "";
                _this.cleanAllTimeout();
                _this.sendSound();
                _this.sendDisabled=false;
            });

        },
        //正在输入
        inputNextText:function(){
            this.sendInputingStrNow(this.messageContent);
        },
        sendInputingStrNow:function(str){
            if(this.socketClosed||!this.socket||this.wsSocketClosed){
                return;
            }
            var message = {}
            message.type = "inputing";
            message.data = {
                from : this.visitor.visitor_id,
                to : this.visitor.to_id,
                content:str
            };
            this.socket.send(JSON.stringify(message));
        },
        sendVisitorLogin:function(){
            var _this=this;
            setTimeout(function(){
                if(_this.socketClosed||!_this.socket||_this.wsSocketClosed){
                    return;
                }
                var message = {}
                message.type = "visitor_login";
                message.data = {
                    from : _this.visitor.visitor_id,
                    to : _this.visitor.to_id,
                };
                _this.socket.send(JSON.stringify(message));
            }, 3000);
        },
        OnClose:function() {
            console.log("ws:onclose");
            this.focusSendConn=true;
            this.wsSocketClosed=true;
            this.closeTimeoutTimer();
        },
        OnError:function() {
            console.log("ws:onerror");
            this.closeTimeoutTimer();
        },
        //获取当前用户信息
        getUserInfo:function(){
            let obj=this.getCache("visitor_"+ENT_ID);
            var visitor_id=""
            var to_id=KEFU_ID;
            if(obj){
                visitor_id=obj.visitor_id;
                //to_id=obj.to_id;
            }
                let _this=this;
                var extra=getQuery("extra");
                var url=getQuery("url");
                var paramVisitorId=getQuery("visitor_id");
                if(paramVisitorId!=""){
                    visitor_id=paramVisitorId;
                }
                var visitorName=getQuery("visitor_name");

                if(extra==""){
                    var ext={};
                    var refer=document.referrer?document.referrer:"-";
                    ext.refer=refer;
                    ext.host=document.location.href;
                    extra=utf8ToB64(JSON.stringify(ext));
                }
                //发送消息
                $.post("/visitor_login",{visitor_id:visitor_id,visitor_name:visitorName,refer:REFER,to_id:to_id,extra:extra,ent_id:ENT_ID,url:url},function(res){
                    if(res.code!=200){
                        _this.$message({
                            message: res.msg,
                            type: 'error'
                        });
                        _this.chatTitle=res.msg;
                        _this.sendDisabled=true;
                        return;
                    }
                    if(result.alloffline){
                        _this.onlineType="danger";
                    }else{
                        _this.onlineType="success";
                    }
                    if(GOFLY_CONFIG.SHOW_OFFLINE_PAGE){
                        _this.allOffline=res.alloffline;
                    }
                    _this.sendDisabled=false;
                    _this.visitor=res.result;
                    _this.noticeName=res.kefu.username;
                    _this.noticeAvatar=res.kefu.avatar;
                    _this.setCache("visitor_"+ENT_ID,res.result);
                    //_this.getMesssagesByVisitorIdUnread();
                    _this.loadMoreMessages();
                    //_this.getMesssagesByVisitorId();
                    _this.initConn();
                    //心跳
                    _this.ping();
                });
            // }else{
            //     this.visitor=obj;
            //     this.initConn();
            // }
        },
        //获取信息列表
        getMesssagesByVisitorId:function(isAll){
            let _this=this;
            $.ajax({
                type:"get",
                url:"/2/messages?visitor_id="+this.visitor.visitor_id,
                success: function(data) {
                    if(data.code==200 && data.result!=null&&data.result.length!=0){
                        let msgList=data.result;
                        _this.msgList=[];
                        for(var i=0;i<msgList.length;i++){
                            let visitorMes=msgList[i];
                            let content = {}
                            if(visitorMes["mes_type"]=="kefu"){
                                content.is_kefu = false;
                            }else{
                                content.is_kefu = true;
                            }
                            content.avator = visitorMes["avator"];
                            content.name = visitorMes["name"];
                            content.content = replaceContent(visitorMes["content"]);
                            content.time = visitorMes["time"];
                            _this.msgList.push(content);
                            _this.scrollBottom();
                        }
                    }
                    if(data.code!=200){
                        _this.$message({
                            message: data.msg,
                            type: 'error'
                        });
                        _this.chatTitle=GOFLY_LANG[LANG]['refresh'];
                    }
                }
            });
        },
        //获取信息列表
        sendEmailMsg:function(){
            let _this=this;
            _this.visitorContact.ent_id=ENT_ID;
            $.ajax({
                type:"post",
                url:"/ent/email_message",
                data:_this.visitorContact,
                success: function(data) {
                    if(data.code!=200){
                        _this.$message({
                            message: data.msg,
                            type: 'error'
                        });
                    }else{
                        _this.allOffline=false;
                    }
                }
            });
        },
        //滚动到底部
        scrollBottom:function(){
            var _this=this;
            this.$nextTick(function(){
                $('.chatVisitorPage').scrollTop(9999999999999999999);
            });
        },
        //软键盘问题
        textareaFocus:function(){
            // if(/Android|webOS|iPhone|iPad|BlackBerry/i.test(navigator.userAgent)) {
            //     //$(".chatContext").css("margin-bottom","0");
            //     //$(".chatBoxSend").css("position","static");
            //     this.textareaFocused=true;
            // }
            this.scrollBottom();
        },
        textareaBlur:function(){
            // if(this.textareaFocused&&/Android|webOS|iPhone|iPad|BlackBerry/i.test(navigator.userAgent)) {
            //     var chatBoxSendObj=$(".chatBoxSend");
            //     var chatContextObj=$(".chatContext");
            //     if(this.textareaFocused&&chatBoxSendObj.css("position")!="fixed"){
            //         //chatContextObj.css("margin-bottom","105px");
            //         //chatBoxSendObj.css("position","fixed");
            //         this.textareaFocused=false;
            //     }
            //
            // }
            this.scrollBottom();
        },
        sendReply:function(title){
            var _this=this;

            let msg = {}
            msg.avator=_this.visitor.avator;
            msg.content = replaceContent(title);
            msg.name = _this.visitor.name;
            msg.is_kefu = true;
            msg.time = _this.getNowDate();
            msg.show_time=false;
            _this.msgList.push(msg);
            _this.scrollBottom();

            var mes = {};
            mes.content = title;
            mes.from_id = this.visitor.visitor_id;
            mes.ent_id = ENT_ID;
            _this.sendAjax("/2/message_ask","post",mes,function(msg){
                var msgArr=msg.content.split("[b]");
                for(var i in msgArr){
                    let content = {}
                    content.avator = msg.avator;
                    content.name = msg.name;
                    content.content =replaceSpecialTag(msgArr[i]);
                    content.is_kefu = false;
                    content.time = msg.time;
                    content.is_reply=true;
                    _this.msgList.push(content);
                    _this.scrollBottom();
                }
                _this.cleanAllTimeout();
                _this.alertSound();//提示音
            });
            //this.chatToUser();
        },
        //获取日期
        getNowDate : function() {// 获取日期
            var d = new Date(new Date());
            return d.getFullYear() + '-' + this.digit(d.getMonth() + 1) + '-' + this.digit(d.getDate())
                + ' ' + this.digit(d.getHours()) + ':' + this.digit(d.getMinutes()) + ':' + this.digit(d.getSeconds());
        },
        //补齐数位
        digit : function (num) {
            return num < 10 ? '0' + (num | 0) : num;
        },
        setCache : function (key,obj){
            if(navigator.cookieEnabled&&typeof window.localStorage !== 'undefined'){
                localStorage.setItem(key, JSON.stringify(obj));
            }
        },getCache : function (key){
            if(navigator.cookieEnabled&&typeof window.localStorage !== 'undefined') {
                return JSON.parse(localStorage.getItem(key));
            }
        },
        //获取自动欢迎语句
        getNotice : function (){
            let _this=this;
            $.get("/notice?ent_id="+ENT_ID+"&kefu_id="+this.visitor.to_id,function(res) {

                //debugger;
                // _this.noticeName=res.result.username;
                // _this.noticeAvatar=res.result.avatar;
                if (res.result.welcome != null) {
                    var msgs = res.result.welcome;
                    var delaySecond=0;
                    for(let i in msgs){
                        var msg=msgs[i];
                        if(msg.delay_second){
                            delaySecond+=msg.delay_second;
                        }else{
                            delaySecond+=4;
                        }
                        var timer =  setTimeout(function (msg) {
                            msg.time=_this.getNowDate();
                            msg.content = replaceSpecialTag(msg.content);
                            _this.msgList.push(msg);
                            _this.scrollBottom();
                            _this.alertSound();
                            var redata={
                                type:"message",
                                data:msg
                            }
                            window.parent.postMessage(redata,"*");
                        },1000*delaySecond,msg);
                        _this.allTimeouter.push(timer);
                    }
                }
            });
        },
        initCss:function(){
            var _this=this;
            $(function () {
                //手机端的样式问题
                if(_this.isMobile){
                    $(".chatVisitorPage").css("height","calc(100% - 155px)");
                }
                //展示表情
                var faces=placeFace();
                $.each(faceTitles, function (index, item) {
                    _this.face.push({"name":item,"path":faces[item]});
                });
                $(".visitorFaceBtn").click(function(e){
                    var status=$('.faceBox').css("display");
                    if(status=="block"){
                        $('.faceBox').hide();
                    }else{
                        $('.faceBox').show();
                    }
                    return false;
                });
                $("body").on("click",".replyContentBtn a",function() {
                    var txt=$(this).text();
                    var href=$(this).attr("href");
                    if(!href){
                        _this.sendReply(txt);
                    }
                });

                var windheight = $(window).height();
                $(window).resize(function(){
                    var docheight = $(window).height();  /*唤起键盘时当前窗口高度*/
                    //_this.scrollBottom();
                    $('body').scrollTop(99999999);
                    // if(docheight < windheight){            /*当唤起键盘高度小于未唤起键盘高度时执行*/
                    //     $(".chatBoxSend").css("position","static");
                    // }else{
                    //     $(".chatBoxSend").css("position","fixed");
                    // }
                });
            });
        },
        //心跳
        ping:function(){
            let _this=this;
            let mes = {}
            mes.type = "ping";
            mes.data = "visitor:"+_this.visitor.visitor_id;
            setInterval(function () {
                if(_this.socket!=null&&!_this.wsSocketClosed){
                    _this.socket.send(JSON.stringify(mes));
                }
            },10000);
        },
        //初始化
        init:function(){
            var _this=this;
            _this.isMobile=isMobile();

            this.initCss();
            //已读消息
            var ms= 1000*2;
            var lastClick = Date.now() - ms;
            $("body").mouseover(function(){
                if(!_this.haveUnreadMessage){
                    return;
                }
                if (Date.now() - lastClick >= ms) {
                    lastClick = Date.now();
                    //如果有未读消息，调用已读接口
                    _this.sendAjax("/2/messages_read","post",{"visitor_id":_this.visitor.visitor_id,"kefu":_this.visitor.to_id},function(data){
                        _this.haveUnreadMessage=false;
                    });
                }
            });
            $('body').click(function(){
                clearFlashTitle();
                window.parent.postMessage({type:"focus"},"*");
                $('.faceBox').hide();
                //剪贴板
                try{
                    var selecter = window.getSelection().toString();
                    if (selecter != null && selecter.trim() != ""){
                        var str=selecter.trim();
                        _this.sendInputingStrNow(str);
                    }
                } catch (err){
                    var selecter = document.selection.createRange();
                    var s = selecter.text;
                    if (s != null && s.trim() != ""){
                        var str=s.trim();
                        _this.sendInputingStrNow(str);
                    }
                }
            });
            window.onfocus = function () {
                //_this.scrollBottom();
                clearFlashTitle();
                window.parent.postMessage({type:"focus"},"*");
                if(_this.socketClosed){
                    return;
                }
                if(!_this.focusSendConn){
                    return;
                }
                _this.initConn();
                _this.scrollBottom();
            }
            var _hmt = _hmt || [];
            (function() {
                var hm = document.createElement("script");
                hm.src = "https://hm.baidu.com/hm.js?82938760e00806c6c57adee91f39aa5e";
                var s = document.getElementsByTagName("script")[0];
                s.parentNode.insertBefore(hm, s);
            })();

            //判断当前是否在iframe中
            if(self!=top){
                _this.isIframe=true;
            }

        },
        //表情点击事件
        faceIconClick:function(index){
            $('.faceBox').hide();
            this.messageContent+="face"+this.face[index].name;
        },
        //上传图片
        uploadImg:function (url){
            let _this=this;
            $('#uploadImg').after('<input type="file" accept="image/gif,image/jpeg,image/jpg,image/png" id="uploadImgFile" name="file" style="display:none" >');
            $("#uploadImgFile").click();
            $("#uploadImgFile").change(function (e) {
                var formData = new FormData();
                var file = $("#uploadImgFile")[0].files[0];
                formData.append("imgfile",file); //传给后台的file的key值是可以自己定义的
                filter(file) && $.ajax({
                    url: url || '',
                    type: "post",
                    data: formData,
                    contentType: false,
                    processData: false,
                    dataType: 'JSON',
                    mimeType: "multipart/form-data",
                    success: function (res) {
                        if(res.code!=200){
                            _this.$message({
                                message: res.msg,
                                type: 'error'
                            });
                        }else{
                            _this.messageContent+='img[/' + res.result.path + ']';
                            _this.chatToUser();
                        }
                    },
                    error: function (data) {
                        console.log(data);
                    }
                });
            });
        },
        //上传文件
        uploadFile:function (url){
            let _this=this;
            $('#uploadFile').after('<input type="file"  id="uploadRealFile" name="file2" style="display:none" >');
            $("#uploadRealFile").click();
            $("#uploadRealFile").change(function (e) {
                var formData = new FormData();
                var file = $("#uploadRealFile")[0].files[0];
                formData.append("realfile",file); //传给后台的file的key值是可以自己定义的
                console.log(formData);
                $.ajax({
                    url: url || '',
                    type: "post",
                    data: formData,
                    contentType: false,
                    processData: false,
                    dataType: 'JSON',
                    mimeType: "multipart/form-data",
                    success: function (res) {

                        if(res.code!=200){
                            _this.$message({
                                message: res.msg,
                                type: 'error'
                            });
                        }else{
                            _this.messageContent+='file[/' + res.result.path + ']';
                            _this.chatToUser();
                        }
                    },
                    error: function (data) {
                        console.log(data);
                    }
                });
            });
        },
        //粘贴上传图片
        onPasteUpload:function(event){
            let items = event.clipboardData && event.clipboardData.items;
            let file = null
            if (items && items.length) {
                // 检索剪切板items
                for (var i = 0; i < items.length; i++) {
                    if (items[i].type.indexOf('image') !== -1) {
                        file = items[i].getAsFile()
                    }
                }
            }
            if (!file) {
                return;
            }
            let _this=this;
            var formData = new FormData();
            formData.append('imgfile', file);
            $.ajax({
                url: '/uploadimg',
                type: "post",
                data: formData,
                contentType: false,
                processData: false,
                dataType: 'JSON',
                mimeType: "multipart/form-data",
                success: function (res) {
                    if(res.code!=200){
                        _this.$message({
                            message: res.msg,
                            type: 'error'
                        });
                    }else{
                        _this.messageContent+='img[/' + res.result.path + ']';
                        _this.chatToUser();
                    }
                },
                error: function (data) {
                    console.log(data);
                }
            });
        },
        //自动
        getAutoReply:function(){
            var _this=this;
            $.get("/autoreply?ent_id="+ENT_ID,function(res) {
                if(res.code==200 && res.result){
                    _this.replys.push(res.result);
                }
            });
        },
        //提示音
        alertSound:function(){
            var b = document.getElementById("chatMessageAudio");
            if (b.canPlayType('audio/ogg; codecs="vorbis"')) {
                b.type= 'audio/mpeg';
                b.src= '/static/images/notification.mp3';
                var p = b.play();
                p && p.then(function () {
                }).catch(function (e) {
                });
            }
        },
        sendSound:function(){
            var b = document.getElementById("chatMessageSendAudio");
            if (b.canPlayType('audio/ogg; codecs="vorbis"')) {
                b.type= 'audio/mpeg';
                b.src= '/static/images/sent.ogg';
                var p = b.play();
                p && p.then(function(){}).catch(function(e){});
            }
        },
        initPeerjs:function(){
            var peer = new Peer();
            this.peer=peer;
            var _this=this;

            peer.on('open', function(id) {
                console.log('My peer ID is: ' + id);
                _this.peerjsId=id;
            });
            peer.on('close', function() {
                console.log('My peer close');
                if(_this.loading!=null){
                    _this.loading.close();
                }
            });
            peer.on('disconnected', function() {
                console.log('My peer disconnected');
                if(_this.loading!=null){
                    _this.loading.close();
                }
            });
            peer.on('error', function() {
                console.log('My peer error');
                if(_this.loading!=null){
                    _this.loading.close();
                }
            });
        },
        callPeer:function(){
            var _this=this;
            this.loading = this.$loading({
                lock: true,
                text: '正在请求通话',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });
            this.loadingTimer=setTimeout(function(){
                _this.loading.close();
            }, 20000);

            var getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
            getUserMedia({video: false, audio: true}, function(stream) {
                _this.localStream=stream;
            }, function(err) {
                console.log('Failed to get local stream' ,err);
            });
            _this.sendAjax("/call_kefu","post",{kefu_id:_this.visitor.to_id,visitor_id:_this.visitor.visitor_id},function(result){
            });
        },
        talkPeer:function(){
            var remoteVideo = document.querySelector('video#chatRtc');
            var _this=this;
            if(this.loading!=null){
                this.loading.close();
            }
            clearTimeout(this.loadingTimer);
            this.$message({
                message: '正在通话...',
                type: 'success'
            });
            if(_this.kefuPeerId==""||_this.localStream==null){
                return;
            }

            var call = _this.peer.call(_this.kefuPeerId, _this.localStream);
            call.on('stream', function(remoteStream) {
                console.log(remoteStream);
                remoteVideo.srcObject = remoteStream;
                remoteVideo.autoplay = true;
            });
            call.on('close', function() {
                console.log("call close");
                _this.loading.close();
            });
            call.on('error', function(err) {
                console.log(err);
                _this.loading.close();
            });

        },
        getExtendInfo:function(){
            var _this=this;
            var extra=getQuery("extra");
            if(extra==""){
                return;
            }
            var extra=JSON.parse(b64ToUtf8(extra));
            if (typeof extra=="string"){
                extra=JSON.parse(extra);
            }
            for(var key in extra){
                if(extra[key]==""){
                    extra[key]="无";
                }
                if(key=="visitorProduct"){
                    _this.messageContent="product["+JSON.stringify(extra[key])+"]";
                    _this.chatToUser();
                };
            }
        },
        sendAjax:function(url,method,params,callback){
            let _this=this;
            $.ajax({
                type: method,
                url: url,
                data:params,
                error:function(res){
                    var data=JSON.parse(res.responseText);
                    console.log(data);
                    if(data.code!=200){
                        _this.$message({
                            message: data.msg,
                            type: 'error'
                        });
                    }
                },
                success: function(data) {
                    if(data.code!=200){
                        _this.$message({
                            message: data.msg,
                            type: 'error'
                        });
                    }else if(data.result!=null){
                        callback(data.result);
                    }else{
                        callback(data);
                    }
                }
            });
        },
        showTitle:function(title){
            $(".chatBox").append("<div class=\"chatTime\"><span>"+title+"</span></div>");
            this.scrollBottom();
        },
        //开始录音
        startRecoder:function(e){
            if(this.recorder){
                this.recorder.destroy();
                this.recorder=null;
            }
            var _this=this;
            Recorder.getPermission().then(function() {
                    _this.recorder = new Recorder();
                    _this.recorderAudio = document.querySelector('#audio');
                    _this.recorder.start();
                    _this.recorder.onprogress = function (params) {
                        _this.recoderSecond = parseInt(params.duration);
                    }
                    this.talkBtnText = "松开 结束";
            }, function(error){
                _this.$message({
                    message: "请允许录音权限,以及浏览器限制非HTTPS下无法使用",
                    type: 'error'
                });
                return;
            });
            e.preventDefault();
        },
        stopRecoder:function(e){
            if(!this.recorder){
                return;
            }
            var blob=this.recorder.getWAVBlob();
            this.recorderAudio.src = URL.createObjectURL(blob);
            this.recorderAudio.controls = true;
            this.talkBtnText="按住 说话";
            this.recorderEnd=true;
            e.preventDefault();
        },
        sendRecoder:function(){
            if(!this.recorder){
                return;
            }

            var blob=this.recorder.getWAVBlob();
            var formdata = new FormData(); // form 表单 {key:value}
            formdata.append("realfile", blob); // form input type="file"
            var _this=this;
            this.loading = this.$loading({
                lock: true,
                text: '正在发送',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });
            $.ajax({
                url: "/uploadaudio",
                type: 'post',
                processData: false,
                contentType: false,
                data: formdata,
                dataType: 'JSON',
                mimeType: "multipart/form-data",
                success: function (res) {
                    _this.loading.close();
                    if(res.code!=200){
                        _this.$message({
                            message: res.msg,
                            type: 'error'
                        });
                    }else{
                        _this.cancelRecoder();
                        _this.messageContent+='audio[/' + res.result.path + ']';
                        _this.chatToUser();
                    }
                }
            })

        },
        cancelRecoder:function(){
            this.audioDialog=false;
            if(!this.recorder){
                return;
            }
            this.recorder.destroy();
            this.recorder=null;
            this.recoderSecond=0;
        },
        recoderFormat:function(percentage){
            return percentage+"s";
        },
        openNewWindow:function(){
            var features = "height=580, width=400, top=10, left=500, toolbar=no, menubar=no,scrollbars=no,resizable=no, location=no, status=no";  //设置新窗口的特性
            var me = window.open(location.href, "newW", features);
        },
        //超时关闭
        checkTimeout:function(){
            var _this=this;
            this.timeoutTimer=setInterval(function(){
                if (Date.now() - _this.currentActiveTime >= _this.timeoutLongTime) {
                    console.log("长时间无操作");
                    if(_this.socket!=null){
                        _this.showTitle(GOFLY_LANG[LANG]['autoclosemes']);
                        _this.socket.close();
                        _this.socket=null;
                    }
                    var msg={type:'force_close'};
                    window.parent.postMessage(msg,"*");
                }
            },55000);
        },
        closeTimeoutTimer:function(){
            clearInterval(this.timeoutTimer);
        },
        cleanAllTimeout:function(){
            for(var i in this.allTimeouter){
                clearTimeout(this.allTimeouter[i]);
            }
        },
        loadMoreMessages:function(){
            var _this=this;
            var pagesize=5;
            if(this.currentPage>1){
                this.replys=[];
            }
            if(_this.loadMoreDisable){
                return;
            }
            var moreMessage=GOFLY_LANG[LANG]['moremessage'];
            this.flyLang.moremessage=this.flyLang.loading;
            this.loadMoreDisable=true;
            var hasUnread=false;
            this.sendAjax("/2/messages_page","get",{pagesize:pagesize,ent_id:ENT_ID,page:this.currentPage,visitor_id:_this.visitor.visitor_id},function(result){
                var len=result.list.length;
                if(result.list.length!=0){
                    if(len<pagesize){
                        _this.showLoadMore=false;
                    }else{
                        _this.showLoadMore=true;
                    }

                    let msgList=result.list;
                    for(var i=0;i<msgList.length;i++) {
                        let visitorMes = msgList[i];
                        let content = {}
                        if (visitorMes["mes_type"] == "kefu") {
                            content.is_kefu = false;
                            content.content = replaceSpecialTag(visitorMes["content"]);
                        } else {
                            content.is_kefu = true;
                            content.content = replaceContent(visitorMes["content"]);
                        }
                        if (visitorMes["read_status"] == "read") {
                            content.read_status = GOFLY_LANG[LANG].read;
                        } else {
                            content.read_status = GOFLY_LANG[LANG].unread;
                            if(i==0){
                                hasUnread=true;
                                _this.haveUnreadMessage=true;
                            }
                        }
                        content.avator = visitorMes["avator"];
                        content.name = visitorMes["name"];
                        content.msg_id = visitorMes["msg_id"];
                        content.time = visitorMes["time"];
                        _this.msgList.unshift(content);
                        _this.scrollBottom();
                    }
                }else{
                    _this.showLoadMore=false;
                }
                if(_this.currentPage==1){
                    _this.showWechatTip();
                }
                if(_this.currentPage==1&&!hasUnread){
                    _this.getNotice();
                }
                _this.currentPage++;
                _this.flyLang.moremessage=moreMessage;
                _this.loadMoreDisable=false;
            });
        },
        showWechatTip:function(){
            if(!GOFLY_CONFIG.SHOW_WECHAT){
                return;
            }
            var child = '<div class="wechatTip">';
            child += '<img style="width:80px; margin:10px;" src="'+GOFLY_CONFIG.WECHAT_QR_URL+'?snow_id=' + this.visitor.visitor_id + '&id_type=visitor">';
            child += '扫描左侧二维码可直接用微信登录。<br>可防止更换浏览器丢失消息、收不到回复。<br>并可接收回复通知</div>';
            $(".chatBox").append(child);
        },
        //格式化时间
        formatTime:function(time) {
            var timeStamp = Math.round(new Date(time).getTime()/1000);
            var nowTime=Math.round(new Date().getTime()/1000);
            if((nowTime-timeStamp)<=3600*24*30*3){
                return beautifyTime(timeStamp,LANG);
            }
            return time;
        },
        getVersion:function(){

        },
    },
    mounted:function() {
        var _this=this;
        document.addEventListener('paste', this.onPasteUpload);
        document.addEventListener('scroll',this.textareaBlur);
        window.addEventListener('message',function(e){
            var msg=e.data;
            if(msg.type=="inputing_message"){
                _this.sendInputingStrNow(msg.content);
            }
            if(msg.type=="send_message"){
                _this.messageContent=msg.content;
                _this.chatToUser();
            }
        });
    },
    created: function () {
        this.init();
        this.getUserInfo();
        //加载历史记录
        //this.msgList=this.getHistory();
        //滚动底部
        //this.scrollBottom();
        //获取欢迎
        //this.initPeerjs();

        this.getAutoReply();
        this.getVersion();
    }
})
