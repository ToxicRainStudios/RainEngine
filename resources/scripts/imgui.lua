beginWindow("Test Window")
createLabel("Hello, world!")
buttonPressed = createButton("WOW")
setDisabled()
checkboxChecked = createCheckbox("Example Checkbox", "true")
setEnabled()
endWindow()


if buttonPressed then
    print("Button was pressed")
end