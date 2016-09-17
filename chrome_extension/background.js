function doStuffWithDom(domContent) {
    console.log('I received the following DOM content:\n' + domContent);
}

console.log("HI I AM HERE");
// When the browser-action button is clicked...
chrome.browserAction.onClicked.addListener(function (tab) {
        //send a message specifying a callback
        console.log("hello! i've been clicked");
        chrome.tabs.sendMessage(tab.id, {text: 'report_back'}, doStuffWithDom);
});
