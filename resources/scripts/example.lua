log("Hello from Lua!")
error("IM ANGRY")

local result = add(10, 20)
log("The result is: " .. result)

local random = random(2, 200)
log("The random result is: " .. random)

local multi = power(2, 3)
log("The number 2 to the power of 3 result is: " .. multi)

log("The current time in mills is: " .. currentTimeMillis())


runScript("extra.lua") -- Never ever ever call example.lua here. It will loop then crash