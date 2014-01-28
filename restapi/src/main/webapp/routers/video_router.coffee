class Video.VideoRouter extends Backbone.Router

  routes:
    "" : "index"
    "home" : "index"
    "videos" : "my_videos"
    "videos/view/:id" : "view"

  initialize: ->
    _.extend(Video.VideoRouter.prototype, Video.Utils)
    console.log "Video router initialized"

  index: ->
    @render_defaults()

  my_videos: ->
    console.log "my videos"
    @render_main_menu()
    @render_featured_videos()
    @videos = new Video.Videos()
    @videos.url = window.Video.root_path + "videos/index?token=" + localStorage.token
    @videos.fetch
      success: =>
        @my_videos_view = new Video.MyVideos({ collection: @videos })
        $(".video-container").html @my_videos_view.render().el

  view: (id) ->
    @render_main_menu()
    @render_featured_videos()
    videos = new Video.Videos()
    videos.url = window.Video.root_path + "videos/view/" + id
    videos.fetch
      success: ->
        @main_video = new Video.MainVideo({ videos: videos })
        $(".video-container").html @main_video.render().el

