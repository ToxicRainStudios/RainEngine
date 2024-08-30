log("Hello from Lua!")
error("IM ANGRY")

local result = add(10, 20)
log("The result is: " .. result)

local random = random(2, 200)
log("The random result is: " .. random)

runScript("extra.lua")