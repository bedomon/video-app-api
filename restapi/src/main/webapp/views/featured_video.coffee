class Video.FeaturedVideo extends Backbone.View
  template: "#featured_video_tpl"
  className: "featured-video"

  initialize: ->
    _.bindAll this, "render"

  render: ->
    container = $(@el)
    template = _.template( $(@template).html(), { test: "test123" } )
    container.html(template)
    this
