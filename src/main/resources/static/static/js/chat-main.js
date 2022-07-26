var getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
var LANG=checkLang();
var app=new Vue({
    el: '#app',
    delimiters:["<{","}>"],
    data: {
        loading:false,
        flyLang:GOFLY_LANG[LANG],
        visible:false,
        chatTitleType:"info",
        fullscreenLoading:true,
        leftTabActive:"first",
        rightTabActive:"visitorInfo",
        users:[],
        usersMap:[],
        server:getWsBaseUrl()+"/ws_kefu?token="+localStorage.getItem("token"),
        //server:getWsBaseUrl()+"/chat_server",
        socket:null,
        messageContent:"",
        currentGuest:"",
        msgList:[],
        chatTitle:GOFLY_LANG[LANG].chatIntro,
        chatInputing:"",
        kfConfig:{
            id : "kf_1",
            name : "客服丽丽",
            avator : "",
            to_id : "",
        },
        visitor:{
            name:"",
            visitor_id:"",
            refer:"",
            client_ip:"",
            city:"",
            status:"",
            source_ip:"",
            created_at:"",
            osVersion:"",
            browser:"",
        },
        visitorAction:{
            count:0,
            currentPage:1,
            pageSize:22,
            activities:[],
        },
        visitorBlacks:{
            count:0,
            page:1,
            pagesize:16,
            list:[],
        },
        visitorAttrs:{},
        visitorExtra:[],
        visitors:[],
        visitorCount:0,
        visitorCurrentPage:1,
        visitorPageSize:10,
        visitorName:"",
        visitorTag:"",
        face:emojiGifsMap(),
        transKefuDialog:false,
        otherKefus:[],
        replyGroupDialog:false,
        replyContentDialog:false,
        editReplyContentDialog:false,
        replySearch:"",
        replySearchList:[],
        replySearchListActive:[],
        groupName:"",
        groupId:"",
        configs:[],
        replys:[],
        replyId:"",
        replyContent:"",
        replyTitle:"",
        ipBlacks:[],
        sendDisabled:false,
        peerjsId:"",
        mediaConnection:null,
        currentPage:1,
        showLoadMore:false,
        loadMoreDisable:false,
        alertSounding:false,
        alertSoundingTimer:null,
        newMessageComing:false,
        dynamicTags: [],
        inputVisible: false,
        inputValue: '',
        allTags:[],
        editor:null,
        rightMenuVisible:false,
        currentMessage:{},
        recorder:null,
        recorderAudio:null,
        recordeTimer:null,
        audioDialog:false,
        recoderSecond:0,
        recorderEnd:false,
    },
    methods: {
        //跳转
        openUrl(url) {
            window.location.href = url;
        },
        sendKefuOnline(){
            let mes = {}
            mes.type = "kfOnline";
            mes.data = this.kfConfig;
            this.socket.send(JSON.stringify(mes));
        },
        //心跳
        ping(){
            let _this=this;
            let mes = {}
            mes.type = "ping";
            mes.data = "";
            setInterval(function () {
                if(_this.socket!=null){
                    _this.socket.send(JSON.stringify(mes));
                }
            },300000);
            setInterval(function(){
                _this.getOnlineVisitors();
            },300000);
        },
        //初始化websocket
        initConn() {
            let socket = new ReconnectingWebSocket(this.server);//创建Socket实例
            this.socket = socket
            this.socket.onmessage = this.OnMessage;
            this.socket.onopen = this.OnOpen;
        },
        OnOpen() {
            this.sendKefuOnline();
        },
        OnMessage(e) {
            const redata = JSON.parse(e.data);
            switch (redata.type){
                case "read":
                    if (redata.data.visitor_id == this.visitor.visitor_id) {
                        for(var i=0;i<this.msgList.length;i++){
                            this.$set(this.msgList[i],'read_status',GOFLY_LANG[LANG]['read']);
                        }
                    }
                case "inputing":
                    this.handleInputing(redata.data);
                    //this.sendKefuOnline();
                    break;
                case "allUsers":
                    this.handleOnlineUsers(redata.data);
                    //this.sendKefuOnline();
                    break;
                case "userOnline":
                    this.addOnlineUser(redata.data);


                    break;
                case "userOffline":
                    this.removeOfflineUser(redata.data);
                    //this.sendKefuOnline();
                    break;
                case "callpeer":
                    this.handleCall(redata.data);
                    break;
                case "comment":
                    this.$notify({
                        title: '成功',
                        message: redata.data,
                        type: 'success'
                    });
                    break;
                case "notice":
                    //发送通知
                    var _this=this;
                    window.parent.postMessage({
                        name:redata.data.username,
                        body: redata.data.content,
                        icon: redata.data.avator
                    },"*");

                    break;
            }


            if (redata.type == "message") {
                let msg = redata.data
                let content = {}
                let _this=this;
                content.avator = msg.avator;
                content.name = msg.name;
                content.content = replaceSpecialTag(msg.content);
                content.is_kefu = msg.is_kefu=="yes"? true:false;
                content.time = msg.time;
                content.msg_id=msg.msg_id;
                if (msg.id == this.currentGuest) {
                    this.msgList.push(content);
                }

                for(let i=0;i<this.users.length;i++){
                    if(this.users[i].visitor_id==msg.id){
                        this.$set(this.users[i],'last_message',msg.content);
                        if(this.visitor.visitor_id!=msg.id){
                            this.$set(this.users[i],'hidden_new_message',false);
                        }
                    }
                }
                for(let i=0;i<this.visitors.length;i++){
                    if(this.visitors[i].visitor_id==msg.id){
                        this.$set(this.visitors[i],'last_message',msg.content);
                        if(msg.id != this.currentGuest){
                            this.$set(this.visitors[i],'unread_num',++this.visitors[i].unread_num);
                        }
                    }
                }

                this.scrollBottom();
                if(content.is_kefu){
                    return;
                }
                window.parent.postMessage({
                    name:msg.name,
                    body: msg.content,
                    icon: msg.avator

                },"*");
                if(this.visitor.visitor_id!=msg.id && _this.getConfig("VisitorMessageAlert")=="on"){
                    _this.newVisitorForceAlert(msg.name+"新消息提醒");
                }
                _this.chatInputing="";
                _this.newMessageComing=true;
            }
        },
        //接手客户
        talkTo(guestId,name) {
            this.loading=true;
            this.currentGuest = guestId;
            this.visitor.visitor_id=guestId;
            //this.chatTitle=name+"|"+guestId+",正在处理中...";

            //发送给客户
            // let mes = {}
            // mes.type = "kfConnect";
            // this.kfConfig.to_id=guestId;
            // mes.data = this.kfConfig;
            // this.socket.send(JSON.stringify(mes));
            //获取标签
            this.getTags(guestId);
            //获取当前访客信息
            this.getVistorInfo(guestId);
            //获取当前访客动态信息
            this.resetVisitorAction();
            this.getVisitorExt(1);
            this.getVisitorAttr(guestId);
            //获取当前客户消息
            //this.getMesssagesByVisitorId(guestId);
            this.currentPage=1;
            this.loadMoreDisable=false;
            this.loadMoreMessages(guestId);
            var cleanAlertSound=true;
            for(var i=0;i<this.users.length;i++){
                if(this.users[i].visitor_id==guestId){
                    this.$set(this.users[i],'hidden_new_message',true);
                    this.$set(this.users[i],'unread_num',0);
                }
                if(this.users[i].unread_num>0){
                    cleanAlertSound=false;
                }
            }
            for(let i=0;i<this.visitors.length;i++){
                if(this.visitors[i].visitor_id==guestId){
                    this.$set(this.visitors[i],'unread_num',0);
                }
                if(this.visitors[i].unread_num>0){
                    cleanAlertSound=false;
                }
            }

        },
        //发送给客户
        chatToUser() {
            this.messageContent=this.messageContent.trim("\r\n");
            this.messageContent=this.messageContent.replace("\n","");
            this.messageContent=this.messageContent.replace("\r\n","");
            if(this.messageContent==""||this.messageContent=="\r\n"||this.currentGuest==""){
                return;
            }
            if(this.sendDisabled){
                return;
            }
            this.sendDisabled=true;
            let _this=this;
            let mes = {};
            mes.type = "kefu";
            mes.content = this.messageContent;
            mes.from_id = this.kfConfig.id;
            mes.to_id = this.currentGuest;
            mes.content = this.messageContent;
            $.ajax({
                type:"post",
                url:BaseURL + "/kefu/message",
                data:mes,
                headers:{
                    "token":localStorage.getItem("token")
                },
                success: function(data) {
                    _this.sendDisabled=false;
                    if(data.code!=200){
                        _this.$message({
                            message: data.msg,
                            type: 'error'
                        });
                    }
                    _this.messageContent = "";
                    _this.sendSound();
                }
            });
            this.scrollBottom();
        },
        //处理当前在线用户列表
        addOnlineUser:function (retData) {
            var flag=false;
            var newUser={};
            newUser.last_message=retData.last_message;
            newUser.status=1;
            newUser.username=retData.username;
            newUser.hidden_new_message=true;
            newUser.visitor_id=retData.uid;
            newUser.avator=retData.avator;
            newUser.updated_at=getNowDate();
            for(let i=0;i<this.users.length;i++){
                if(this.users[i].visitor_id==newUser.visitor_id){
                    flag=true;
                }
            }
            if(!flag){
                this.users.unshift(newUser);
            }
            var newUserflag=false;
            for(let i=0;i<this.visitors.length;i++){
                if(this.visitors[i].visitor_id==newUser.visitor_id){
                    newUserflag=true;
                    break;
                }
            }
            if(!newUserflag){
                newUser.unread_num=0;
                this.visitors.unshift(newUser);
            }
            if(this.visitor.visitor_id==newUser.visitor_id){
                this.getVistorInfo(newUser.visitor_id)
            }

        },
        //处理当前在线用户列表
        removeOfflineUser:function (retData) {
            for(let i=0;i<this.users.length;i++){
                if(this.users[i].visitor_id==retData.uid){
                    this.users.splice(i,1);
                }
            }
            let vid=retData.uid;
            for(let i=0;i<this.visitors.length;i++){
                if(this.visitors[i].visitor_id==vid){
                    this.visitors[i].status=0;
                    break;
                }
            }
        },
        //处理当前在线用户列表
        handleOnlineUsers:function (retData) {
            if (this.currentGuest == "") {
                this.chatTitle = "连接成功,等待处理中...";
            }
            this.usersMap=[];
            for(let i=0;i<retData.length;i++){
                this.usersMap[retData[i].uid]=retData[i].username;
                retData[i].last_message="新访客";
            }
            if(this.users.length==0){
                this.users = retData;
            }
            for(let i=0;i<this.visitors.length;i++){
                let vid=this.visitors[i].visitor_id;
                if(typeof this.usersMap[vid]=="undefined"){
                    this.visitors[i].status=0;
                }else{
                    this.visitors[i].status=1;
                }
            }

        },
        //处理正在输入
        handleInputing:function (retData) {
            if(retData.from==this.visitor.visitor_id){
                this.chatInputing="当前动态："+retData.content+"...";
                if(retData.content==""){
                    this.chatInputing="";
                }else{
                    this.showVisitorMove(retData.content)
                }
            }
            for(var i=0;i<this.users.length;i++){
                if(this.users[i].visitor_id==retData.from){
                    this.$set(this.users[i],'last_message',retData.content+"...");
                }
            }
        },
        showVisitorMove:function(content){
            // $(".chatBox").append("<div class=\"chatTime timeLine\"><span>访客动态:"+content+"</span></div>");
            // this.scrollBottom();
            //this.activities.push(content);
        },
        handleCall:function (retData) {
            var _this=this;
            this.initPeerjs();
            this.$confirm(retData.name+'请求通话?', '提示', {
                confirmButtonText: '接通',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function(){
                _this.sendAjax("/kefuinfo_peerid","post",{peer_id:_this.peerjsId,visitor_id:retData.id},function(result){
                    _this.$message({
                        type: 'success',
                        message: '接通成功!'
                    });
                    _this.$alert(retData.name+'正在通话..', '提示', {
                        confirmButtonText: '挂断',
                        callback: function(){
                            if(_this.mediaConnection!=null){
                                _this.mediaConnection.close();
                            }
                            console.log(_this.mediaConnection);
                        }
                    });
                });
            }).catch(function(){
                _this.$message({
                    type: 'info',
                    message: '已取消'
                });
            });
        },
        //新访客强制提醒
        newVisitorForceAlert:function (title) {
            var _this=this;
            if(_this.alertSounding){
                return;
            }
            var timer=_this.newVisitorForceAlertSound();
            this.$confirm(title, '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function(){
                clearInterval(timer);
                _this.alertSounding=false;
            }).catch(function(){
                clearInterval(timer);
                _this.alertSounding=false;
            });
            _this.alertSounding=true;
        },
        newVisitorForceAlertSound(){
            var _this=this;
            var timer=setInterval(function(){
                _this.alertSound();
            },3000);
            _this.alertSoundingTimer=timer;
            return timer;
        },
        //获取客服信息
        getKefuInfo(){
            let _this=this;
            $.ajax({
                type:"get",
                url:BaseURL + "/kefuinfo",
                headers:{
                    "token":localStorage.getItem("token")
                },
                success: function(data) {
                    if(data.code==200 && data.result!=null){
                        _this.kfConfig.id=data.result.id;
                        _this.kfConfig.name=data.result.name;
                        _this.kfConfig.avator=data.result.avator;
                        _this.initConn();
                    }
                    if(data.code!=200){
                        _this.$message({
                            message: data.msg,
                            type: 'error'
                        });
                    }
                }
            });
        },
        //获取客服信息
        getOnlineVisitors(){
            let _this=this;
            $.ajax({
                type:"get",
                url:BaseURL + "/visitors_kefu_online",
                headers:{
                    "token":localStorage.getItem("token")
                },
                success: function(data) {
                    if(data.code==200 && data.result!=null){
                        _this.users=data.result;
                        for(var i=0;i<_this.users.length;i++){
                            _this.$set(_this.users[i],'hidden_new_message',true);
                        }
                    }
                    if(data.code!=200){
                        _this.$message({
                            message: data.msg,
                            type: 'error'
                        });
                        window.location.href="/login";
                    }
                    if(data.code==400){
                        window.location.href="/login";
                    }
                }
            });
        },
        //获取信息列表
        getMesssagesByVisitorId(visitorId,isAll){
            let _this=this;
            $.ajax({
                type:"get",
                url:BaseURL + "/kefu/messages?visitor_id="+visitorId,
                headers:{
                    "token":localStorage.getItem("token")
                },
                success: function(data) {
                    if(data.code==200 && data.result!=null){
                        let msgList=data.result;
                        _this.msgList=[];
                        if(!isAll&&msgList.length>10){
                            var i=msgList.length-10
                        }else{
                            var i=0;
                        }
                        for(;i<msgList.length;i++){
                            let visitorMes=msgList[i];
                            let content = {}
                            if(visitorMes["mes_type"]=="kefu"){
                                content.is_kefu = true;
                            }else{
                                content.is_kefu = false;
                            }
                            content.avator = visitorMes["avator"];
                            content.name = visitorMes["name"];
                            content.content = replaceSpecialTag(visitorMes["content"]);
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
                    }
                    if(data.code==400){
                        window.location.href="/login";
                    }
                }
            });
        },
        //获取客服信息
        getVistorInfo(vid){
            let _this=this;
            this.resetVisitorAction();
            //$(".timeLine").remove();
            $.ajax({
                type:"get",
                url:BaseURL + "/kefu/visitor",
                data:{visitorId:vid},
                headers:{
                    "token":localStorage.getItem("token")
                },
                success: function(data) {
                    if(data.result!=null){
                        let r=data.result;
                        _this.visitor=r;
                        _this.visitor.created_at=data.create_time;
                        _this.visitor.updated_at=data.last_time;
                        _this.visitor.osVersion=data.os_version;
                        _this.visitor.browser=data.browser;
                        if(r.refer!=""){
                            _this.visitor.refer=r.refer
                        }else{
                            _this.visitor.refer="-";
                        }

                        //_this.visitor.visitor_id=r.visitor_id;
                        _this.chatTitle=r.name;
                        _this.chatTitleType="success";
                        _this.visitorExtra=[];
                        if(r.extra!=""){
                            var extra=JSON.parse(b64ToUtf8(r.extra));
                            if (typeof extra=="string"){
                                extra=JSON.parse(extra);
                            }
                            for(var key in extra){
                                if(extra[key]==""){
                                    extra[key]="无";
                                }
                                if(key=="visitorAvatar"||key=="visitorName"||key=="visitorProduct") continue;
                                var temp={key:key,val:extra[key]}
                                _this.visitorExtra.push(temp);
                            }
                        }

                    }
                    if(data.code!=200){
                        _this.$message({
                            message: data.msg,
                            type: 'error'
                        });
                    }
                }
            });
        },
        //关闭访客
        closeVisitor(visitorId){
            var _this=this;
            this.$confirm('此操作将会关闭该访客会话, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function(){
                $.ajax({
                    type:"get",
                    url:BaseURL + "/kefu/message_close",
                    data:{visitor_id:visitorId},
                    headers:{
                        "token":localStorage.getItem("token")
                    },
                    success: function(data) {
                        if(data.code!=200){
                            _this.$message({
                                message: data.msg,
                                type: 'error'
                            });
                        }
                    }
                });
            });
        },
        //处理tab切换
        handleTabClick(tab, event){
            let _this=this;
            if(tab.name=="first"){
                this.getOnlineVisitors();
            }
            if(tab.name=="second"){
                this.getVisitorPage(1);
            }
            if(tab.name=="visitorMove"){
                this.resetVisitorAction();
                this.getVisitorExt(1);
            }
            if(tab.name=="blackList"){
                this.getVisitorBlacks(1);
            }
            if(tab.name=="ipBlackList"){
                this.getIpblacks();
            }
        },
        //所有访客分页展示
        visitorPage(page){
            this.getVisitorPage(page);
        },
        //获取访客分页
        getVisitorPage(page){
            let _this=this;
            this.visitorCurrentPage=page;
            var pagesize=18;
            var parames={kefuName:this.kfConfig.name,page:page,pagesize:pagesize,visitorName:this.visitorName,visitorTag:this.visitorTag};
            $.ajax({
                type:"get",
                url:BaseURL + "/kefu/visitorList",
                data:parames,
                headers:{
                    "token":localStorage.getItem("token")
                },
                success: function(data) {
                    if(data.result.list!=null){
                        _this.visitors=data.result.list;
                        _this.visitorCount=data.result.count;
                        _this.visitorPageSize=data.result.pagesize;
                    }
                    if(data.code!=200){
                        _this.$message({
                            message: data.msg,
                            type: 'error'
                        });
                    }
                }
            });
        },
        replaceContent(content){
            return replaceSpecialTag(content)
        },
        //滚到底部
        scrollBottom(){
            this.$nextTick(() => {
                $('.chatBox').scrollTop($(".chatBox")[0].scrollHeight);
            });
        },
        //jquery
        initJquery(){
            this.$nextTick(() => {
                var _this=this;
                $(function () {
                    //展示表情
                    // var faces=placeFace();
                    // $.each(faceTitles, function (index, item) {
                    //     _this.face.push({"name":item,"path":faces[item]});
                    // });
                    $("body").on("click","#faceBtn",function() {
                        $('.faceBox').show();
                    });
                    $("body").on("mouseover",".chatBoxMe",function() {
                        $(".chatDeleteBtn").hide();
                        $(this).find(".chatDeleteBtn").show();
                    });
                    $("body").on("mouseover",".chatBoxMe",function() {
                        $(".chatDeleteBtn").hide();
                        $(this).find(".chatDeleteBtn").show();
                    });
                    $("body").on("mouseleave",".chatBoxMe",function() {
                        $(".chatDeleteBtn").hide();
                    });
                    $("body").on("mouseleave",".faceBox",function() {
                        $(this).hide();
                    });
                    $("body").on("click",".chatDeleteBtn",function() {
                        $(this).parents(".chatBoxMe").hide();
                    });
                    $("body").on("click",".chatImagePic",function() {
                        var url=$(this).attr("data-src");
                        _this.$alert("<img src='"+url+"'/>", "", {
                            dangerouslyUseHTMLString: true
                        });
                        return false;
                    });
                    var ms= 1000*2;
                    var lastClick = Date.now() - ms;
                    $("body").mouseover(function(){
                        if(!_this.newMessageComing||_this.visitor.visitor_id==""){
                            return;
                        }
                        if (Date.now() - lastClick >= ms) {
                            lastClick = Date.now();
                            _this.sendAjax("/kefu/messages_read",'post',{visitor_id:_this.visitor.visitor_id},function(ret){
                                _this.newMessageComing=false;
                            });
                        }
                    });
                });
            });
            var _hmt = _hmt || [];
            (function() {
                var hm = document.createElement("script");
                hm.src = "https://hm.baidu.com/hm.js?82938760e00806c6c57adee91f39aa5e";
                var s = document.getElementsByTagName("script")[0];
                s.parentNode.insertBefore(hm, s);
            })();
        },
        //表情点击事件
        faceIconClick(index){
            $('.faceBox').hide();
            this.messageContent+="face"+this.face[index].name;
        },
        //上传图片
        uploadImg (url){
            let _this=this;
            $('#uploadImg').after('<input type="file" accept="image/gif,image/jpeg,image/jpg,image/png" id="uploadImgFile" name="file" style="display:none" >');
            $("#uploadImgFile").click();
            $("#uploadImgFile").change(function (e) {
                var formData = new FormData();
                var file = $("#uploadImgFile")[0].files[0];
                formData.append("imgfile",file); //传给后台的file的key值是可以自己定义的
                filter(file) && $.ajax({
                    url: BaseURL + url || '',
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
                            _this.messageContent+='img[' + res.result.path + ']';
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
                    url: BaseURL + url || '',
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
                            _this.messageContent+='file[' + res.result.path + ']';
                            _this.chatToUser();
                        }
                    },
                    error: function (data) {
                        console.log(data);
                    }
                });
            });
        },
        addIpblack(ip){
            let _this=this;
            $.ajax({
                type:"post",
                url:BaseURL + "/kefu/ipblack",
                data:{ip:ip,name:this.visitor.name},
                headers:{
                    "token":localStorage.getItem("token")
                },
                success: function(data) {
                    if(data.code!=200){
                        _this.$message({
                            message: data.msg,
                            type: 'error'
                        });
                    }else{
                        _this.$message({
                            message: data.msg,
                            type: 'success'
                        });
                    }
                }
            });
        },
        //粘贴上传图片
        onPasteUpload(event){
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
            console.log(1111)
            $.ajax({
                url: BaseURL + '/uploadimg',
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
                        _this.messageContent+='img[' + res.result.path + ']';
                        _this.chatToUser();
                    }
                },
                error: function (data) {
                    console.log(data);
                }
            });
        },
        openUrl(url){
            window.open(url);
        },
        //提示音
        alertSound(){
            var b = document.getElementById("chatMessageAudio");
            var src=this.getConfig("KefuAlertSound");
            if(src!=""){
                b.src=src;
            }
            var p = b.play();
            p && p.then(function(){}).catch(function(e){});
        },
        sendSound(){
            var b = document.getElementById("chatMessageSendAudio");
            var p = b.play();
            p && p.then(function(){}).catch(function(e){});
        },
        //转移客服
        transKefu(){
            this.transKefuDialog=true;
            var _this=this;
            this.sendAjax("/other_kefulist","get",{},function(result){
                _this.otherKefus=result;
            });
        },
        //转移访客客服
        transKefuVisitor(kefu,visitorId){
            var _this=this;
            this.sendAjax("/trans_kefu","get",{kefu_id:kefu,visitor_id:visitorId},function(result){
                //_this.otherKefus=result;
                _this.transKefuDialog = false;
                _this.visitor.visitor_id="";
            });
        },
        //保存回复分组
        addReplyGroup(){
            var _this=this;
            this.sendAjax("/reply","post",{group_name:_this.groupName},function(result){
                //_this.otherKefus=result;
                _this.replyGroupDialog = false
                _this.groupName="";
                _this.getReplys();
            });
        },
        //添加回复内容
        addReplyContent(){
            var _this=this;
            var content=this.editor.txt.html();
            this.sendAjax("/reply_content","post",{group_id:_this.groupId,item_name:_this.replyTitle,content:content},function(result){
                //_this.otherKefus=result;
                _this.replyContentDialog = false
                _this.replyContent="";
                _this.getReplys();
            });
        },
        //获取快捷回复
        getReplys(){
            var _this=this;
            this.sendAjax("/replys","get",{},function(result){
                _this.replys=result;
            });
        },
        getConfigs(){
            var _this=this;
            this.sendAjax("/ent_configs","get",{},function(result){
                _this.configs=result;
            });
        },
        getConfig(key){
            for(index in this.configs){
                if(key==this.configs[index].conf_key){
                    return this.configs[index].conf_value;
                }
            }
            return "";
        },
        //删除回复
        deleteReplyGroup(id){
            var _this=this;
            this.$confirm('此操作将删除本组所有回复内容, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function(){
                _this.sendAjax("/reply?id="+id,"delete",{},function(result){
                    _this.getReplys();
                    _this.$message({
                        type: 'success',
                        message: '删除成功!'
                    });
                });
            });

        },
        //删除回复
        deleteReplyContent(id){
            var _this=this;
            this.$confirm('此操作将删除这条快捷回复内容, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function(){
                _this.sendAjax("/reply_content?id="+id,"delete",{},function(result){
                    _this.getReplys();
                    _this.$message({
                        type: 'success',
                        message: '删除成功!'
                    });
                });
            });

        },
        //编辑回复
        editReplyContent(save,id,title,content){
            var _this=this;
            if(save=='yes'){
                var data={
                    reply_id:this.replyId,
                    reply_title:this.replyTitle,
                    reply_content:this.replyContent
                }
                this.sendAjax("/reply_content_save","post",data,function(result){
                    _this.editReplyContentDialog=false;
                    _this.getReplys();
                });
            }else{
                this.editReplyContentDialog=true;
                this.replyId=id;
                this.replyTitle=title;
                this.replyContent=content;
            }

        },
        //搜索回复
        searchReply(){
            var _this=this;
            _this.replySearchListActive=[];
            if(this.replySearch==""){
                _this.replySearchList=[];
            }
            this.sendAjax("/reply_search","post",{search:this.replySearch},function(result){
                _this.replySearchList=result;
                for (var i in result) {
                    _this.replySearchListActive.push(result[i].group_id);
                }
            });
        },
        //获取访客动态
        getVisitorExt(currentPage){
            var _this=this;
            var visitorId=this.visitor.visitor_id
            this.sendAjax("/kefu/visitorExt","get",{visitor_id:visitorId,page:currentPage,pagesize:_this.visitorAction.pageSize},function(result){
                if(result.count>=1){
                    firstItem=result.list[0];
                    _this.$nextTick(function(){
                        _this.$set(_this.visitor,"osVersion",firstItem.os_version);
                        _this.$set(_this.visitor,"browser",firstItem.browser);
                    });
                }
                _this.visitorAction.activities=result.list;
                _this.visitorAction.count=result.count;
            });
        },
        resetVisitorAction(){
            this.visitorAction.activities=[];
            this.visitorAction.count=0;
            this.visitorAction.currentPage=1;
        },
        //获取黑名单
        getIpblacks(){
            var _this=this;
            this.sendAjax("/kefu/ipblacks","get",{},function(result){
                _this.ipBlacks=result;
            });
        },
        //删除黑名单
        delIpblack(ip){
            let _this=this;
            this.sendAjax("/kefu/ipblack?ip="+ip,"DELETE",{ip:ip},function(result){
                _this.getIpblacks();
            });
        },
        //获取访客黑名单
        getVisitorBlacks(page){
            var _this=this;
            this.sendAjax("/kefu/visitorBlacks","get",{
                page:page,pagesize:this.visitorBlacks.pagesize
            },function(result){
                _this.visitorBlacks=result;
            });
        },
        //添加进访客黑名单
        addVisitorBlack(){
            var _this=this;
            this.$confirm('此操作将会拉黑该访客, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function(){
                _this.sendAjax("/kefu/visitorBlack","post",{
                    "visitor_id":_this.visitor.visitor_id,
                    "name":_this.visitor.name
                },function(result){
                    _this.$message({
                        message: result.msg,
                        type: 'success'
                    });
                });
            });

        },
        //删除访客黑名单
        delVisitorBlack(id){
            let _this=this;
            this.sendAjax("/kefu/delVisitorBlack","get",{id:id},function(result){
                _this.getVisitorBlacks(1);
            });
        },
        initPeerjs:function(){
            var peer = new Peer();
            this.peer=peer;
            var _this=this;
            peer.on('open', function(id) {
                console.log('My peer ID is: ' + id);
                _this.peerjsId=id;

            });
            peer.on('call', function(call) {
                getUserMedia({video: false, audio: true}, function(stream) {
                    call.answer(stream);
                    call.on('stream', function(remoteStream) {
                        console.log(remoteStream);
                        var remoteVideo = document.querySelector('#chatRtc');
                        remoteVideo.srcObject = remoteStream;
                        remoteVideo.autoplay = true;
                    });
                    _this.mediaConnection=call;
                }, function(err) {
                    console.log('Failed to get local stream' ,err);
                });
            });
        },
        //划词搜索
        selectText(){
            return false;
            var _this=this;
            $('body').click(function(){
                try{
                    var selecter = window.getSelection().toString();
                    if (selecter != null && selecter.trim() != ""){
                        _this.replySearch=selecter.trim();
                        _this.searchReply();
                    }else{
                        _this.replySearch="";
                    }
                } catch (err){
                    var selecter = document.selection.createRange();
                    var s = selecter.text;
                    if (s != null && s.trim() != ""){
                        _this.replySearch=s.trim();
                        _this.searchReply();
                    }else{
                        _this.replySearch="";
                    }
                }
                var status=$('.faceBox').css("display");
                if(status=="block"){
                    $('.faceBox').hide();
                }
            });
        },
        //翻译
        translate(){
            var word=this.messageContent;
            this.sendAjax("/translate","post",{words:word},function(data){
                console.log(data);
            });
         },
        sendAjax(url,method,params,callback,headers){
            let _this=this;
            $.ajax({
                type: method,
                url: BaseURL + url,
                data:params,
                headers: {
                    "token": localStorage.getItem("token")
                },
                error:function(res){
                  console.log(res)
                    var data=JSON.parse(res.responseText);
                    if(data.code==200|| data.code==20000){
                    }else{
                        _this.$message({
                            message: data.msg,
                            type: 'error'
                        });
                    }
                },
                success: function(data) {
                    if(data.code==200 || data.code==20000){
                        if(data.result!=null){
                            callback(data.result);
                        }else{
                            callback(data);
                        }
                    }else{
                        _this.$message({
                            message: data.msg,
                            type: 'error'
                        });
                    }
                },
            });
        },
        resizeChatBox(){
            var height=$(".kefuFuncBox").height()+47;
            $(".chatBox").css("height","calc(100% - "+height+"px)");
        },
        deleteMessage(msgId){
            var _this=this;
            this.sendAjax("/kefu/message_delete","post",{msg_id:msgId,visitor_id:this.visitor.visitor_id},function(result){
                for(var i=0;i<_this.msgList.length;i++){
                    if(_this.msgList[i].msg_id==msgId){
                        _this.msgList.splice(i,1);
                    }
                }
            });
        },
        deleteVisitorMessage(visitorId){
            var _this=this;
            this.$confirm('此操作将清除访客所有记录, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function(){
                _this.sendAjax("/kefu/delVisitorMessage","get",{visitor_id:visitorId},function(result){
                    _this.$message({
                        type: 'success',
                        message: '删除成功!'
                    });
                    _this.msgList=[]
                });
            });

        },
        loadMoreMessages:function(visitor_id){
            var _this=this;
            var pagesize=10;
            if(_this.loadMoreDisable){
                return;
            }
            var moreMessage=GOFLY_LANG[LANG]['moremessage'];
            this.flyLang.moremessage=this.flyLang.loading;
            this.loadMoreDisable=true;
            if(!visitor_id){
                visitor_id=this.visitor.visitor_id;
            }
            if(this.currentPage==1){
                this.msgList=[];
            }
            this.sendAjax("/kefu/messages_page","get",{pagesize:pagesize,page:this.currentPage,visitor_id:visitor_id},function(result){
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
                            content.is_kefu = true;
                        } else {
                            content.is_kefu = false;
                        }
                        if (visitorMes["read_status"] == "read") {
                            content.read_status = GOFLY_LANG[LANG].read;
                        } else {
                            content.read_status = GOFLY_LANG[LANG].unread;
                        }
                        content.avator = visitorMes["avator"];
                        content.name = visitorMes["name"];
                        content.content = replaceSpecialTag(visitorMes["content"]);
                        content.msg_id = visitorMes["msg_id"];
                        content.time = visitorMes["time"];
                        _this.msgList.unshift(content);
                    }
                    if(_this.currentPage==1){
                        _this.scrollBottom();
                    }
                }else{
                    _this.showLoadMore=false;
                }
                _this.currentPage++;
                _this.flyLang.moremessage=moreMessage;
                _this.loadMoreDisable=false;
                _this.loading=false;
            });
        },
        setVisitorListItem:function(visitorId,key,value){
            for(let i=0;i<this.users.length;i++){
                if(this.users[i].visitor_id==visitorId){
                    this.$set(this.users[i],key,value);
                    break;
                }
            }
            for(let i=0;i<this.visitors.length;i++){
                if(this.visitors[i].visitor_id==visitorId){
                    this.$set(this.visitors[i],key,value);
                    break;
                }
            }
        },
        getVisitorAttr:function(visitorId){
            var _this=this;
            this.sendAjax("/kefu/visitor_attr","get",{visitor_id:visitorId},function(result){
                _this.visitorAttrs=result;
            });
        },
        //保存访客属性
        saveVisitorAttr:function(obj){
            var info={
                'visitor_id':this.visitor.visitor_id,
                'visitor_attr':obj
            }
            var _this=this;
            $.ajax({
                type: 'post',
                url: BaseURL + '/kefu/visitor_attrs',
                data:JSON.stringify(info),
                dataType:"json",
                contentType: "application/json",
                headers: {
                    "token": localStorage.getItem("token")
                },
                success: function(data) {
                    if(data.code!=200){
                        _this.$message({
                            message: _this.flyLang.failed,
                            type: 'error'
                        });
                        return;
                    }
                    _this.$message({
                        message: _this.flyLang.success,
                        type: 'success'
                    });
                    if(obj.real_name){
                        _this.setVisitorListItem(_this.visitor.visitor_id,"username",obj.real_name);
                    }
                },
            });
        },
        //格式化时间
        formatTime:function(fmt,time) {
            var timeStamp = Math.round(new Date(time).getTime()/1000);
            var nowTime=Math.round(new Date().getTime()/1000);
            if((nowTime-timeStamp)<=3600*24*30*6){
                return beautifyTime(timeStamp,LANG);
            }
            return dateFormat(fmt,new Date(time));
        },
        //标签相关
        getTags(visitor_id){
            var _this=this;
            sendAjax("/kefu/visitorTag","GET",{
                "visitor_id":visitor_id,
            },function(data){
                _this.dynamicTags=data.result;
            });



        },
        //获取所有标签
        getAllTags(){
            var _this=this;
            sendAjax("/kefu/tags","GET",{
            },function(data){
                _this.allTags=data.result;
            })
        },
        delTag(tagName) {
            var _this=this;
            sendAjax("/kefu/delVisitorTag","GET",{
                "visitor_id":_this.visitor.visitor_id,
                "tag_name":tagName
            },function(data){
                _this.getTags(_this.visitor.visitor_id);
            })
        },

        showInput() {
            this.inputVisible = true;
            this.$nextTick(_ => {
                this.$refs.saveTagInput.$refs.input.focus();
            });
        },
        //添加标签
        addTag() {
            let inputValue = this.inputValue;
            var _this=this;
            sendAjax("/kefu/visitorTag","POST",{
                "visitor_id":this.visitor.visitor_id,
                "tag_name":this.inputValue
            },function(data){
                _this.inputVisible = false;
                _this.inputValue = '';
                _this.getTags(_this.visitor.visitor_id);
            })

        },
        //发送评价
        sendComment(){
            var _this=this;
            this.$confirm('此操作将向访客发送评价请求, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(function(){
                _this.sendAjax("/kefu/sendComment","GET",{
                    "visitor_id":_this.visitor.visitor_id,
                },function(data){
                    _this.$message({
                        message: data.msg,
                        type: 'success'
                    });
                })
            });
        },
        copyText(text){
            copyText(replaceHtml(text));
            this.$message({
                message: "ok",
                type: 'success'
            });
        },
        initEditor(){
            const E = window.wangEditor
            this.editor = new E('#welcomeEditor')
            this.editor.config.height = 240;
            // 配置 server 接口地址
            this.editor.config.uploadImgServer = '/kefu/editorUploadImg?token='+localStorage.getItem("token");
            this.editor.config.height = 240;
            this.editor.config.uploadImgMaxSize = 1 * 1024 * 1024; // 1M
            this.editor.config.uploadFileName = 'imgfile';
            this.editor.config.uploadImgHooks = {
                // 图片上传并返回了结果，图片插入已成功
                success: function(xhr) {
                    console.log('success', xhr)
                },
                // 图片上传并返回了结果，但图片插入时出错了
                fail: function(xhr, editor, resData) {
                    console.log('fail', resData)
                },
                // 上传图片出错，一般为 http 请求的错误
                error: function(xhr, editor, resData) {
                    console.log('error', xhr, resData)
                },
                // 上传图片超时
                timeout: function(xhr) {
                    console.log('timeout')
                },
                // 图片上传并返回了结果，想要自己把图片插入到编辑器中
                // 例如服务器端返回的不是 { errno: 0, data: [...] } 这种格式，可使用 customInsert
                customInsert: function(insertImgFn, result) {
                    // result 即服务端返回的接口
                    console.log('customInsert', result)
                    insertImgFn(result.data.url)
                }
            }
            this.editor.create();
        },
        destoryEditor(){
            this.replyTitle="";
            this.replyContent="";
            if(!this.editor){
                return;
            }
            this.editor.destroy();
            this.editor = null;
        },
        rightClick(row){
            this.rightMenuVisible=false;
            this.rightMenuVisible=true;
            this.currentMessage=row;
            var menu = document.querySelector('.rightMenu')
            this.styleMenu(menu);
        },
        styleMenu(menu) {
            if (event.clientX > 1800) {
                menu.style.left = event.clientX - 100 + 'px'
            } else {
                menu.style.left = event.clientX + 1 + 'px'
            }
            document.addEventListener('click', this.foo) // 给整个document新增监听鼠标事件，点击任何位置执行foo方法
            if (event.clientY > 700) {
                menu.style.top = event.clientY - 30 + 'px'
            } else {
                menu.style.top = event.clientY - 10 + 'px'
            }
        },
        //录音相关
        recoderFormat:function(percentage){
            return percentage+"s";
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
            _this.loading=true;
            $.ajax({
                url: BaseURL + "/2/uploadAudio",
                type: 'post',
                processData: false,
                contentType: false,
                data: formdata,
                dataType: 'JSON',
                mimeType: "multipart/form-data",
                success: function (res) {
                    _this.loading=false;
                    if(res.code!=200){
                        _this.$message({
                            message: res.msg,
                            type: 'error'
                        });
                    }else{
                        _this.cancelRecoder();
                        _this.messageContent+='audio[' + res.result.path + ']';
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
    },
    mounted() {
        document.addEventListener('paste', this.onPasteUpload)
    },
    created: function () {
        //jquery
        this.initJquery();
        this.getKefuInfo();
        this.getOnlineVisitors();
        this.getReplys();
        this.getConfigs();
        this.selectText();
        this.getAllTags();
        //this.initPeerjs();
        //心跳
        this.ping();
    }
})
