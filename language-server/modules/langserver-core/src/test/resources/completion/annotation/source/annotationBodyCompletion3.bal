import ballerina/http;
import ballerina/config;

@http:ServiceConfig {
    endpoints: [],
    host: "",
    basePath: co,
    compression: {
        enable: "AUTO",
        contentTypes: []
    },
    chunking: "AUTO",
    cors: {},
    versioning: {},
    auth: {}
}
service helloService on new http:Listener(8080) {
    resource function sayHello(http:Caller caller, http:Request request) {
        
    }
}

function helloFunction() returns string {
    return "testValue";
}