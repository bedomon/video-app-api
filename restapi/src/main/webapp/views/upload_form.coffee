class Video.UploadForm extends Backbone.View
  template: "#upload_form_tpl"
  className: "upload-form"

  initialize: ->
    _.bindAll this, "render"

  events:
    "click .js-upload-cancel" : "cancel"

  render: ->
    container = $(@el)
    template = _.template( $(@template).html() )
    container.html(template)
    console.log "test"
    $(@el).find('.js-upload-form').fileupload
      add: (e, data) ->
        console.log "add"
        data.form.context.action = window.Video.root_path + "videos/create?token=" + localStorage.token
        console.log data
        data.submit()
      progress: (e, data) ->
        console.log('progress')
      fail: (e, data) ->
        console.log('fail')
      done: (e, data) ->
        console.log data.result
    this

  cancel: (e) ->
    $(".popup").html ""
