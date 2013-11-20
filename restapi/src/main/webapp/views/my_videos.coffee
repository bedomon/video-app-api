class Video.MyVideos extends Backbone.View
  template: "#videos_tpl"
  className: "my-videos-list"

  initialize: ->
    _.bindAll this, "render"

  render: ->
    container = $(@el)
    self = @
    @collection.each (item) ->
      template = _.template( $(self.template).html(), { video: item } )
      $(container).append(template)
    this
