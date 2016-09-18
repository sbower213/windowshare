// Listen for messages
chrome.runtime.onMessage.addListener(function (msg, sender, sendResponse) {
    // If the received message has the expected format...
    console.log("hi i am in the listener");
    if (msg.text === 'report_back') {
        // Call the specified callback, passing
        // the web-page's DOM content as argument
        console.log("FEK:OFJLAUKGBUK");
        sendResponse({text:document.getElementsByTagName("html")[0].outerHTML, tab:msg.tab});

        return true;
    } else if (msg.text === 'update_dom') {
      alert("made it fam");
        document.addEventListener("DOMContentLoaded", function(event) {
          alert("?");
          document = msg.dom;
        });
        console.log(msg.dom);
        sendResponse("yes good");
    }
});
/*
var port = chrome.runtime.connect({name: "content_port"});

port.postMessage({myProperty: "value"});
port.onMessage.addListener(function(msg, sender, callback) {
    if(msg.text === 'report_back'){
      callback(document.getElementsByTagName("html")[0].outerHTML);
    } else if (msg.text === 'update_dom') {
      document.getElementsByTagName("html")[0].outerHTML = msg.dom;
    }
    return true;
});
*/

/*port.postMessage({joke: "Knock knock"});
port.onMessage.addListener(function(msg) {
  if (msg.question == "Who's there?")
    port.postMessage({answer: "Madame"});
  else if (msg.question == "Madame who?")
    port.postMessage({answer: "Madame... Bovary"});
});*/
