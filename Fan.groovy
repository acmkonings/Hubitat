/**
 *  ****************  Ceiling Fan Controller  ****************
 *
 *  Usage:
 *  This was designed to control a ceiling fan using a Virtual Fan Controller device synched to a Smart Dimmer device.
 *
**/

definition (
    name: "Ceiling Fan Controller",
    namespace: "Guffman",
    author: "Guffman",
    description: "Controller for a ceiling fan. Uses a Virtual Fan Controller and a Smart Dimmer.",
    category: "My Apps",
    iconUrl: "",
    iconX2Url: ""
)

preferences {
     page name: "mainPage", title: "", install: true, uninstall: true
}

def mainPage() {

    dynamicPage(name: "mainPage") {

        section("<b>Virtual Fan Controller</b>") {

            input (
              name: "fanController1", 
              type: "capability.fanControl", 
              title: "Select Virtual Fan Controller Device", 
              required: true, 
              multiple: false, 
              submitOnChange: true
            )

            if (fanController1) {
                input (
                    name: "trackSwitch", 
                    type: "bool", 
                    title: "Track physical switch changes", 
                    required: true, 
                    defaultValue: "true"
                )
            } 
        }

        section("<b>Fan Switch</b>") {
            input (
                name: "fanSwitch1", 
                type: "capability.switch", 
                title: "Select Switch1 Wired to Ceiling Fan", 
                required: true, 
                multiple: false
            )
        }
        
        section("<b>Fan Switch2</b>") {
            input (
                name: "fanSwitch2", 
                type: "capability.switch", 
                title: "Select Switch2 Wired to Ceiling Fan", 
                required: true, 
                multiple: false
            )
        }

        section("") {
            input (
                name: "debugMode", 
                type: "bool", 
                title: "Enable logging", 
                required: true, 
                defaultValue: false
            )
          }
    }
}

def installed() {
    initialize()
}

def updated() {
    initialize()
}

def initialize() {

    ///subscribe(fanController1, "speed", fanControllerHandler)
    subscribe(fanSwitch1, "switch", fanControllerHandler)
    subscribe(fanSwitch2, "switch", fanControllerHandler)

}

def fanControllerHandler(evt) {
    
    state.fanSpeedRequest = evt.value
    logDebug("Fan Controller Event = $state.fanSpeedRequest")
    fanSetLevel = state.fanSpeedRequest
    switch (state.fanSpeedRequest)
    {
        case "off":
            fanSwitch1.off
            break;
        case "on":
            fanSwitch2.on
            break;
        case "auto":
            break;
        case "low":
            fanSwitch1.off
            break;
        case "medium":
            fanSwitch1.on
            break;
        case "high":
            fanSwitch2.on
            break;
    }

}


def fanDimmerLevelHandler(evt) {

    state.fanDimmerLevel = evt.value
    logDebug("Fan Dimmer Level Event = $state.fanDimmerLevel")
    def lvl = evt.value.toInteger()

    switch (lvl)
    {
        case 1..24:
            fanSetSpeed("low")
            logDebug("Tracking dimmer level - fan speed set to low")
            break;
        case 25..49:
            fanSetSpeed("medium")
            logDebug("Tracking dimmer level - fan speed set to medium")
            break;
        case 50..74:
            fanSetSpeed("medium-high")
            logDebug("Tracking dimmer level - fan speed set to medium-high")
            break;
        case 75..99:
            fanSetSpeed("high")
            logDebug("Tracking dimmer level - fan speed set to high")
            break;
    }
}

def fanDimmerSwitchHandler(evt) {
    state.fanDimmerState = evt.value
    logDebug("Fan Dimmer Switch Event = $state.fanDimmerState")

    switch (state.fanDimmerState)
    {
        case "on":
            fanSetSpeed("on")
            break;
        case "off":
            fanSetSpeed("off")
            break;
    }
}

def fanOn(){
   fanDimmer1.on()
}

def fanOff(){
    fanDimmer1.off()
}

def fanSetLevel(val){
    fanDimmer1.setLevel(val)
}

def fanSetSpeed(val){
    fanController1.setSpeed(val)
}

def logDebug(txt){
    try {
        if (settings.debugMode) { log.debug("${app.label} - ${txt}") }
    } catch(ex) {
        log.error("bad debug message")
    }
}
