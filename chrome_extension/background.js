let dom = "<html><body><p>goodbye</p></body></html>";



var port = chrome.runtime.connectNative('com.google.chrome.example.echo');
port.onMessage.addListener(function(msg) {
  console.log("Received" + msg);
});
port.onDisconnect.addListener(function() {
  console.log("Disconnected");
});
port.postMessage({ text: "Hello, windowshare" });



/*
function doStuffWithDom(domContent) {
    console.log('I received the following DOM content:\n' + domContent.text);
    //dom = domContent;
    dom = "<html><body><p>hello</p></body></html>";
    let newIndex = domContent.tab.index+1;
    chrome.tabs.create({url:domContent.tab.url, index:newIndex});
    console.log(dom);
    chrome.tabs.sendMessage(newIndex, {text: 'update_dom', dom:dom}, confirm);
}

function confirm(status) {
    console.log(status);
}

//var port = chrome.tabs.connect({name:"content_port"});

console.log("HI I AM HERE");
// When the browser-action button is clicked...
chrome.browserAction.onClicked.addListener(function (tab) {
        //send a message specifying a callback
        console.log("hello! i've been clicked");
        chrome.tabs.sendMessage(tab.id, {text: 'report_back', tab:tab}, doStuffWithDom);
        alert("hi");
        //let newIndex = tab.index+1;
        //console.log(dom);
        //chrome.tabs.create({ url: tab.url, index: newIndex });
        //chrome.tabs.sendMessage(newIndex, {text: 'update_dom', dom:dom}, confirm);

});

/*chrome.runtime.onConnect.addListener(function(port) {
    if(port.name == "content_port"){
        port.onMessage.addListener(function(msg) {
            port.postMessage({text:'report_back', });
        });
    }
});
*/
