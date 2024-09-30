loadMap("map")

log("Hello from Lua!")

local random = random(2, 200)
log("The random result is: " .. random)

local multi = power(2, 3)
log("The number 2 to the power of 3 result is: " .. multi)

log("The current time in mills is: " .. currentTimeMillis())

runScriptCustomDir("extra.lua", "resources/scripts/")
runScript("extra.lua") -- Never ever ever call init_init.lua here. It will loop then crash