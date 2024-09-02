beginWindow("Test Window")
createLabel("Hello, world!")
buttonPressed = createButton("WOW")
endWindow()


if buttonPressed then
    print("Button was pressed")
else
    print("Button state:", tostring(buttonPressed))
end