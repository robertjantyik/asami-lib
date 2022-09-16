let stompClient;
$(document).ready(function (){
    connect();
    setTimeout(function (){
        stompClient.send('/start');
        getNotifList();
    }, 2000);
});

function connect() {
    if(!stompClient) {
        const socket = new SockJS('http://localhost:8080/notifications');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function () {
            stompClient.subscribe('/user/notification', function (response) {
                let notif = JSON.parse(response.body);
                if(Array.isArray(notif)){
                    //TODO add to notif list
                    console.log(notif);
                } else {
                    switch (notif.type) {
                        case "SUCCESS":
                            toastr.success(notif.message);
                            break;
                        case "INFO":
                            toastr.info(notif.message);
                            break;
                        case "WARNING":
                            toastr.warning(notif.message);
                            break;
                        case "ERROR":
                            toastr.error(notif.message);
                            break;
                    }
                }
            });
        });
    }
}

function success(message){
    stompClient.send('/success',{}, message);
}

function info(message){
    stompClient.send('/info',{}, message);
}

function warning(message){
    stompClient.send('/warning',{}, message);
}

function error(message){
    stompClient.send('/error',{}, message);
}

function removeNotif(element){
    //TODO
    //stompClient.send('/remove',{}, id);
}

function getNotifList(){
    stompClient.send('/list');
}