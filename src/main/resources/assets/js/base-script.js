function alertMsg(component, status, message) {
    $(component).html(message !== "" ? '<div class="alert alert-' + status +'" role="alert"><button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button><b>' + message + '</b></div>' : '')
}

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