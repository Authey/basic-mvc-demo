function postMsg(url, message) {
    let form = $("<form>")
    form.attr("style", "display: none")
    form.attr("action", url)
    form.attr("method", "post")
    let input = $("<input>")
    input.attr("type", "hidden")
    input.attr("name", "alert")
    input.attr("value", message)
    $("body").append(form)
    form.append(input)
    form.submit()
    form.remove()
}