class Video.MainVideo extends Backbone.View
  template: "#video_tpl"
  className: "video"

  initialize: ->
    _.bindAll this, "render"

  render: ->
    container = $(@el)
    self = @
    if @options.videos
      @options.videos.each (video) ->
        template = _.template( $(self.template).html(), { video: video } )
        container.html(template)
    else
      video = new Video.VideoModel()
      video.set({ title: "default title", video_file: "1.mp4", description: "desc" })
      template = _.template( $(@template).html(), { video: video } )
      container.html(template)
    this
