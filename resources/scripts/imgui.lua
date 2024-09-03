beginWindow("Test Window")
createLabel("Hello, world!")
setDisabled()
buttonPressed = createButton("WOW")
checkboxChecked = createCheckbox("Example Checkbox", "true")
setEnabled()
endWindow()


if buttonPressed then
    print("Button was pressed")
end